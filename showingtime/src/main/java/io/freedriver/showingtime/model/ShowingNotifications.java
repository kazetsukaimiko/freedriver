package io.freedriver.showingtime.model;

import java.util.ArrayList;
import java.util.List;

public class ShowingNotifications {
    private List<ShowNotification> notifications = new ArrayList<>();
    private int total;
    private String version;

    public List<ShowNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<ShowNotification> notifications) {
        this.notifications = notifications;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
