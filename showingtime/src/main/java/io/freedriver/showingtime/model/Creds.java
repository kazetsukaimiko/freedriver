package io.freedriver.showingtime.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class Creds {

    private static final Path CONFIG_PATH = Paths.get(System.getProperty("user.home"), ".config/showingtime");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static final Optional<Creds> load() {
        Path json = get("creds.json");
        try {
            return Optional.of(MAPPER.readValue(json.toFile(), Creds.class));
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();

        }
    }


    private static Path get(String... parts) {
        return Paths.get(CONFIG_PATH.toAbsolutePath().toString(), parts);
    }
}
