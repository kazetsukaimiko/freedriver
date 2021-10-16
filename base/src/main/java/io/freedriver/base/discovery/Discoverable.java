package io.freedriver.base.discovery;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

public class Discoverable {
    private String type;
    private String name;
    private String                  domain;
    private String                  protocol;
    private String                  application;
    private String                  subtype;
    private String                  server;
    private int                     port;
    private int                     weight;
    private int                     priority;
    private byte[]                  text;
    private Map<String, byte[]> props;
    private Set<Inet4Address> ipv4Addresses;
    private Set<Inet6Address> ipv6Addresses;

    public Discoverable() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public byte[] getText() {
        return text;
    }

    public void setText(byte[] text) {
        this.text = text;
    }

    public Map<String, byte[]> getProps() {
        return props;
    }

    public void setProps(Map<String, byte[]> props) {
        this.props = props;
    }

    public Set<Inet4Address> getIpv4Addresses() {
        return ipv4Addresses;
    }

    public void setIpv4Addresses(Set<Inet4Address> ipv4Addresses) {
        this.ipv4Addresses = ipv4Addresses;
    }

    public Set<Inet6Address> getIpv6Addresses() {
        return ipv6Addresses;
    }

    public void setIpv6Addresses(Set<Inet6Address> ipv6Addresses) {
        this.ipv6Addresses = ipv6Addresses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discoverable that = (Discoverable) o;
        return port == that.port && Objects.equals(type, that.type) && Objects.equals(name, that.name) && Objects.equals(domain, that.domain) && Objects.equals(protocol, that.protocol) && Objects.equals(application, that.application) && Objects.equals(subtype, that.subtype) && Objects.equals(server, that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, domain, protocol, application, subtype, server, port);
    }

    public static int sort(Discoverable a, Discoverable b) {
        return IntStream.of(
                String.valueOf(a.getName()).compareTo(b.getName()),
                Integer.compare(a.port, b.port)
        )
                .filter(i -> i != 0)
                .findFirst()
                .orElse(0);
    }
}
