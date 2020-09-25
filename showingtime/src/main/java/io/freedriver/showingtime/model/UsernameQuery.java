package io.freedriver.showingtime.model;

public class UsernameQuery {
    private boolean usernameExists;
    private String version;

    public UsernameQuery() {
    }

    public boolean isUsernameExists() {
        return usernameExists;
    }

    public void setUsernameExists(boolean usernameExists) {
        this.usernameExists = usernameExists;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
