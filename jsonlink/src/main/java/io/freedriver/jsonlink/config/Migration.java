package io.freedriver.jsonlink.config;

public interface Migration<VN> {
    VN migrate();
}
