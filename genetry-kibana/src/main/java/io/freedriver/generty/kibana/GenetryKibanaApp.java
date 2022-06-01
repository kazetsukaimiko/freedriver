package io.freedriver.generty.kibana;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.freedriver.discovery.DiscoveredService;
import io.freedriver.generty.InverterFinder;
import io.freedriver.generty.model.Statsjson;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.client.RestHighLevelClient;

import org.apache.http.HttpHost;


public class GenetryKibanaApp {

    private static final Logger LOGGER = Logger.getLogger(GenetryKibanaApp.class.getName());
    private static final ExecutorService POOL = Executors.newCachedThreadPool();
    private static final Map<DiscoveredService, Future<Boolean>> WORKERS = new HashMap<>();
    private static final Duration POLL_INTERVAL = Duration.ofSeconds(2);

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);


    private static RestHighLevelClient client;


    public static void main(String[] args) throws IOException {

        // TODO From args

        HttpHost host = new HttpHost("localhost", 9200);
        client = new RestHighLevelClient(RestClient.builder(new Node(host)));

        Set<DiscoveredService> found = InverterFinder.findInverters(POLL_INTERVAL);
        /*
        found.forEach(discoveredService -> {
            Statsjson statsjson = getStats(discoveredService);
            try {
                System.out.println(MAPPER.writeValueAsString(new KibanaStats(discoveredService.getDns(), statsjson)));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });*/
        AtomicInteger foundNothing = new AtomicInteger();
        while (foundNothing.getAndIncrement() < 10) {
            found.forEach(GenetryKibanaApp::createWorker);
            found = InverterFinder.findInverters(POLL_INTERVAL);
            if (!found.isEmpty()) {
                foundNothing.set(0);
            }
        }
        LOGGER.info("No inverters found for a while, terminating.");
        POOL.shutdown();
        POOL.shutdownNow();
        client.close();
    }


    public static Statsjson getStats(DiscoveredService service) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String address = "http://" + service.getDns() + "/stats.json";
        LOGGER.info("Asking " + address + " for stats.json");
        HttpRequest request = HttpRequest.newBuilder(URI.create(address)).GET().build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        return MAPPER.readValue(response.body(), Statsjson.class);
    }
    private synchronized static void createWorker(DiscoveredService discoveredService) {
        if (WORKERS.containsKey(discoveredService) && !(WORKERS.get(discoveredService).isDone() || WORKERS.get(discoveredService).isCancelled())) {
            LOGGER.finest("Worker for service already exists: " + discoveredService);
        } else {
            LOGGER.info("Adding Worker for " + discoveredService);
            WORKERS.put(discoveredService, spawn(discoveredService));
        }
    }

    private static Future<Boolean> spawn(DiscoveredService discoveredService) {
        return POOL.submit(() -> {
            Throwable lastException = null;
            AtomicInteger consecutiveFailCount = new AtomicInteger(0);
            while (consecutiveFailCount.get() < 10) {
                try {
                    Statsjson statsjson = getStats(discoveredService);
                    KibanaStats kibanaStats = new KibanaStats(discoveredService.getDns(), statsjson);
                    sendToKibana(kibanaStats);
                    consecutiveFailCount.set(0);
                } catch (Exception e) {
                    lastException = e;
                    consecutiveFailCount.getAndIncrement();
                }
            }
            if (lastException != null) {
                LOGGER.log(Level.WARNING, "Exception in worker", lastException);
            }
            LOGGER.info("Worker finished: " + discoveredService);
            return true;
        });
    }

    private static void sendToKibana(KibanaStats kibanaStats) throws IOException {
        LOGGER.info("Sending to Elastic: " + MAPPER.writeValueAsString(kibanaStats));
        client.index(new IndexRequest(kibanaStats.getInverterId()).source(MAPPER.writeValueAsString(kibanaStats), XContentType.JSON), RequestOptions.DEFAULT);
    }
}
