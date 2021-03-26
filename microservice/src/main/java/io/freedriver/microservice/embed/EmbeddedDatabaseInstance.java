package io.freedriver.microservice.embed;

import java.nio.file.Path;

public class EmbeddedDatabaseInstance {
    private String name;
    private Path path;

    public EmbeddedDatabaseInstance() {
    }

    public EmbeddedDatabaseInstance(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "EmbeddedDatabaseInstance{" +
                "name='" + name + '\'' +
                ", path=" + path +
                '}';
    }
}
