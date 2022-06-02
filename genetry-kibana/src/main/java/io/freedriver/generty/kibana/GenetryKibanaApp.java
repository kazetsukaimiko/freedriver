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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GenetryKibanaApp {

    private static final Logger LOGGER = Logger.getLogger(GenetryKibanaApp.class.getName());
    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    private static final Map<DiscoveredService, Future<Boolean>> WORKERS = new HashMap<>();

    private static final Duration INVERTER_SCAN_INTERVAL = Duration.ofSeconds(6);
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(1);

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);


    private static ElasticsearchClient client;


    /**
     * Where the application starts.
     */
    public static void main(String[] args) throws IOException {
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


        // Look for inverters.
        Set<DiscoveredService> found = InverterFinder.findInverters(INVERTER_SCAN_INTERVAL);

        // Continuously scan for new inverters. If nothing found for one minute, exit.
        AtomicInteger foundNothing = new AtomicInteger();
        while (foundNothing.getAndIncrement() < 2) {
            // Any inverters found, create a worker for.
            found.forEach(GenetryKibanaApp::createWorker);
            found = InverterFinder.findInverters(INVERTER_SCAN_INTERVAL);
            if (WORKERS.values().stream().anyMatch(GenetryKibanaApp::inProgress)) {
                foundNothing.set(0);
            }
        }
        LOGGER.info("No inverters found for a while, terminating.");
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
            WORKERS.put(discoveredService, spawn(discoveredService));
        }
    }

    /**
     * Spawns a thread to poll an inverter.
     */
    private static Future<Boolean> spawn(DiscoveredService discoveredService) {
        return POOL.submit(() -> {
            while (true) {
                try {
                    Statsjson statsjson = tryToGet(() -> getStats(discoveredService));
                    KibanaStats kibanaStats = new KibanaStats(discoveredService.getDns(), statsjson);

                    LOGGER.info("Sending to Elastic: " + MAPPER.writeValueAsString(kibanaStats));
                    tryToDo(() -> sendToKibana(kibanaStats));
                } catch (Throwable e) {
                    LOGGER.log(Level.SEVERE, "Inverter telemetry indexing failed for " + discoveredService, e);
                    return false;
                }
                waitFor(POLL_INTERVAL);
            }
        });
    }

    @FunctionalInterface
    private interface ThrowingBlackHole {
        void apply() throws Exception;
    }
    private static void tryToDo(ThrowingBlackHole throwingBlackHole) throws Throwable {
        tryToGet(() -> {
            throwingBlackHole.apply();
            return true;
        });
    }
    private static <T> T tryToGet(Callable<T> callable) throws Throwable {
        Throwable lastException = null;
        AtomicInteger consecutiveFailCount = new AtomicInteger(0);
        while (consecutiveFailCount.getAndIncrement() < 10) {
            try {
                return callable.call();
            } catch (Exception e) {
                lastException = e;
                waitFor(Duration.ofMillis(50));
            }
        }
        throw lastException;
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


    /**
     * Send Kibana the good stuff.
     */
    private static void sendToKibana(KibanaStats kibanaStats) throws IOException {
        String index = kibanaStats.getInverterId().toLowerCase();
        IndexRequest<KibanaStats> indexRequest = IndexRequest.of(i -> i.index(index).document(kibanaStats));
        client.index(indexRequest);
    }


    /**
     * Waits for a given duration.
     */
    private static void waitFor(Duration pollInterval) throws InterruptedException {
        Thread.sleep(pollInterval.toMillis());
    }
}
