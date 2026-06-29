package io.freedriver.client;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
public class RESTClient {

    public static <T> T create(Class<T> klazz, UriBuilder uri) {

        //ObjectMapperCustomizer c;

        //ResteasyJackson2Provider r;
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        client.register(ObjectMapperProvider.class);

        ResteasyWebTarget target = client.target(uri);

        return target.proxy(klazz);
    }
}
