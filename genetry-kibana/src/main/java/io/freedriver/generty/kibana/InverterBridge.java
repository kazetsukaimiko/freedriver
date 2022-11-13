package io.freedriver.generty.kibana;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.freedriver.discovery.DiscoveredService;
import io.freedriver.generty.InverterFinder;
import io.freedriver.generty.model.Statsjson;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;


public class InverterBridge {

    private static final Logger LOGGER = Logger.getLogger(InverterBridge.class.getName());
    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    private static final Map<DiscoveredService, Future<Boolean>> WORKERS = new HashMap<>();

    // How long to look on mdns for inverters.
    private static final Duration INVERTER_SCAN_TIME = Duration.ofSeconds(1);

    // How often to scan for inverters.
    private static final Duration DISCOVERY_INTERVAL = Duration.ofSeconds(10);
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(1);

    private static final Duration INDEX_INTERVAL = Duration.ofMillis(50);

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);

    private static final List<KibanaStats> deque = new ArrayList<>();

    private static ElasticsearchClient client;
    private static GenetryUI app;
    private static boolean running = true;

    public static void main(String[] args) throws AWTException {
        block(args, new GenetryTrayIcon(InverterBridge::shutdown).show());
    }

    public static void block(String[] args, GenetryUI app) {
        InverterBridge.app = app;

        HttpHost host;
        if (args.length == 2) {
            host = new HttpHost(args[0], Integer.parseInt(args[1]));
        } else {
            host = new HttpHost("localhost", 9200);
        }

        // Create the low-level client
        RestClient restClient = RestClient.builder(host).build();

        // Create the transport with a Jackson mapper

        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(MAPPER));

        // And create the API client
        client = new ElasticsearchClient(transport);

        spawnIndexer();
        spawnDiscoveryWorker();
    }

    public static void discoverInverters() {
        // Look for inverters.
        InverterFinder.findInverters(INVERTER_SCAN_TIME)
                .forEach(InverterBridge::createWorker);
    }

    public static void shutdown() {
        running = false;
        LOGGER.info("Shutting down.");
        POOL.shutdown();
        POOL.shutdownNow();
        System.exit(0);
    }

    private static boolean inProgress(Future<Boolean> booleanFuture) {
        return !booleanFuture.isDone() && !booleanFuture.isCancelled();
    }

    /**
     * Looks to see if we currently have a worker for a given inverter. If so, ignores.
     * If no worker for the given address present, we spawn a thread to poll the inverter.
     */
    private synchronized static void createWorker(DiscoveredService discoveredService) {
        if (WORKERS.containsKey(discoveredService)) { // && !(WORKERS.get(discoveredService).isDone() || WORKERS.get(discoveredService).isCancelled())) {
            LOGGER.finest("Worker for service already exists: " + discoveredService);
        } else {
            LOGGER.info("Adding Worker for " + discoveredService);
            WORKERS.put(discoveredService, spawnWorker(discoveredService));
        }
    }

    /**
     * Spawns a thread to poll an inverter.
     */
    private static Future<Boolean> spawnWorker(DiscoveredService discoveredService) {
        return POOL.submit(() -> {
            while (running) {
                try {
                    Statsjson statsjson = getStats(discoveredService);
                    KibanaStats kibanaStats = new KibanaStats(discoveredService.getDns(), statsjson);
                    app.add(kibanaStats);
                    deque.add(kibanaStats);
                } catch (Throwable e) {
                    LOGGER.log(Level.SEVERE, "Inverter telemetry indexing failed for " + discoveredService, e);
                    return false;
                }
                waitFor(POLL_INTERVAL);
            }
            return true;
        });
    }

    private static void spawnIndexer() {
        POOL.submit(() -> {
            while (running) {
                try {
                    tryToSendToKibana();
                    waitFor(INDEX_INTERVAL);
                } catch (Throwable e) {
                    LOGGER.log(Level.SEVERE, "Failed to send data to ElasticSearch. ", e);
                    app.error("Unable to reach ElasticSearch.");
                    waitFor(Duration.ofSeconds(2));
                }

            }
        });
    }

    private static void spawnDiscoveryWorker() {
        POOL.submit(() -> {
            while (running) {
                try {
                    discoverInverters();
                    if (WORKERS.values().stream().noneMatch(InverterBridge::inProgress)) {
                       LOGGER.warning("No inverters found. Waiting.");
                    }
                    waitFor(DISCOVERY_INTERVAL);
                } catch (Throwable e) {
                    LOGGER.log(Level.SEVERE, "Failed to discover inverters. ", e);
                    app.error("Unable to discover inverters.");
                    waitFor(Duration.ofSeconds(2));
                }

            }
        });
    }

    /**
     * Get Stats.json as a Java Object.
     */
    public static Statsjson getStats(DiscoveredService service) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI address = URI.create(service.getAddress()).resolve("/stats.json");
        LOGGER.info("GET " + address);
        HttpRequest request = HttpRequest.newBuilder(address).GET().build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        return MAPPER.readValue(response.body(), Statsjson.class);
    }

    private static void tryToSendToKibana() {
        if (!deque.isEmpty()) {
            deque.removeIf(kibanaStats -> {
                try {
                    LOGGER.info("Sending to Elastic: " + MAPPER.writeValueAsString(kibanaStats));
                    String index = kibanaStats.getInverterId().toLowerCase();
                    IndexRequest<KibanaStats> indexRequest = IndexRequest.of(i -> i.index(index).document(kibanaStats));
                    client.index(indexRequest);
                    app.running(GenetryUI.TITLE);
                    return true;
                } catch (IOException ioe) {
                    LOGGER.log(Level.WARNING, "Couldn't send to elastic:", ioe);
                    return false;
                }
            });
        }
    }


    /**
     * Waits for a given duration.
     */
    private static void waitFor(Duration pollInterval) {
        try {
            Thread.sleep(pollInterval.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
