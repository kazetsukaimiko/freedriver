package io.freedriver.base.util.crypt;

public enum HashAlgorithms implements HashAlgorithm {
    MD5("MD5", true),
    SHA_1("SHA-1", true),
    SHA_256("SHA-256", true);

    private final String name;
    private final boolean supported;

    HashAlgorithms(String name, boolean supported) {
        this.name = name;
        this.supported = supported;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isSupported() {
        return supported;
    }
}
