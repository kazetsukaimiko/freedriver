module io.freedriver.jpa.model.wordpress {
    requires java.logging;
    requires jakarta.persistence;
    requires lombok;
    exports io.freedriver.jpa.model.wordpress;
    opens io.freedriver.jpa.model.wordpress;
    requires org.hibernate.orm.core;
}