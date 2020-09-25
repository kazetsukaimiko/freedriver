package io.freedriver.showingtime;

import io.freedriver.showingtime.model.Creds;
import io.freedriver.showingtime.model.ShowNotification;
import io.freedriver.showingtime.model.TokenResponse;
import io.freedriver.showingtime.rest.ShowingTimeApi;
import io.freedriver.showingtime.rest.ShowingTimeTokenApi;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.ClientBuilder;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ShowingTimeApiTest {
    private ResteasyClient client;
    private ResteasyWebTarget target;
    private ShowingTimeTokenApi tokenApi;
    private ShowingTimeApi api;

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
        target = client.target("https://"+ShowingTimeApi.HOST);
        tokenApi = target.proxy(ShowingTimeTokenApi.class);
        api = target.proxy(ShowingTimeApi.class);
    }

    @Test
    public void testLogin() {
        Creds.load()
                .ifPresent(creds -> {
                    TokenResponse token = ShowingTimeTokenApi.loginAsUser(tokenApi, creds);
                    //System.out.println(token);
                    List<ShowNotification> notificationList = api
                            .getNotifications(token.request())
                            .getNotifications();

                    //notificationList.forEach(System.out::println);

                    Map<String, Set<ShowNotification>> byStatus = notificationList
                            .stream()
                            .collect(Collectors.toMap(
                                    ShowNotification::getStatus,
                                    ShowingTimeApiTest::makeSet,
                                    ShowingTimeApiTest::mergeSets
                            ));

                    //byStatus.keySet().forEach(System.out::println);

                    System.out.println("Confirmed:");
                    notificationList.stream()
                            .filter(sn -> byStatus.get("Confirmed").contains(sn))
                            .filter(sn -> !byStatus.get("Canceled").contains(sn))
                            .distinct()
                            .sorted(Comparator.comparing(ShowNotification::getId))
                            .map(sn -> sn.getDate() + (byStatus.get("Rescheduled").contains(sn)
                                ? " (Rescheduled, previous date/time unknown) " : ""))
                            .forEach(ShowingTimeApiTest::consume);

                    System.out.println("\nPending Approval: ");
                    notificationList.stream()
                            .filter(sn -> !byStatus.get("Confirmed").contains(sn))
                            .filter(sn -> byStatus.get("Requested").contains(sn))
                            .sorted(Comparator.comparing(ShowNotification::getId))
                            .distinct()
                            .sorted(Comparator.comparing(ShowNotification::getId))
                            .map(sn -> sn.getDate() + (byStatus.get("Rescheduled").contains(sn)
                                    ? " (Rescheduled, previous date/time unknown) " : ""))
                            .forEach(ShowingTimeApiTest::consume);

                });
    }

    private static void consume(String s) {
        System.out.println(s);
        //Festival.speak(s);
    }


    public static <X> Set<X> makeSet(X item) {
        Set<X> set = new HashSet<>();
        set.add(item);
        return set;
    }

    public static <X> Set<X> mergeSets(Set<X> a, Set<X> b) {
        a.addAll(b);
        return a;
    }
}
