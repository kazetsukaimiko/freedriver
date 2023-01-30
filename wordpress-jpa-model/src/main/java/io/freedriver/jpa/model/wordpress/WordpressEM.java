package io.freedriver.jpa.model.wordpress;

import jakarta.persistence.*;
public class WordpressEM {
    public static EntityManager getEntityManager() {
        org.hibernate.proxy.ProxyConfiguration op;
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("wordpress");
        return factory.createEntityManager();
    }
}
