package io.freedriver.discovery;

import java.util.Objects;

public class DiscoveredService {
    private String name;
    private String dns;
    private String status;
    private String address;

    public DiscoveredService() {
    }

    public DiscoveredService(String name, String dns, String status, String address) {
        this.name = name;
        this.dns = dns;
        this.status = status;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscoveredService that = (DiscoveredService) o;
        return Objects.equals(name, that.name) && Objects.equals(dns, that.dns) && Objects.equals(status, that.status) && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dns, status, address);
    }

    @Override
    public String toString() {
        return "DiscoveredService{" +
                "name='" + name + '\'' +
                ", dns='" + dns + '\'' +
                ", status='" + status + '\'' +
                ", address=" + address +
                '}';
    }
}
