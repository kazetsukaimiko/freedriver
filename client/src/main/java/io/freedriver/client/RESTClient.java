package io.freedriver.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
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
