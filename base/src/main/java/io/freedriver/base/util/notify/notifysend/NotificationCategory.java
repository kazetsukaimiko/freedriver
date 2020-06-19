package io.freedriver.base.util.notify.notifysend;

public enum NotificationCategory {
    DEVICE("device", "A generic device-related notification that doesn't fit into any other category."),
    DEVICE_ADDED("device.added", "A device, such as a USB device, was added to the system."),
    DEVICE_ERROR("device.error", "A device had some kind of error."),
    DEVICE_REMOVED("device.removed", "A device, such as a USB device, was removed from the system."),
    EMAIL("email", "A generic e-mail-related notification that doesn't fit into any other category."),
    EMAIL_ARRIVED("email.arrived", "A new e-mail notification."),
    EMAIL_BOUNCED("email.bounced", "A notification stating that an e-mail has bounced."),
    IM("im", "A generic instant message-related notification that doesn't fit into any other category."),
    IM_ERROR("im.error", "An instant message error notification."),
    IM_RECEIVED("im.received", "A received instant message notification."),
    NETWORK("network", "A generic network notification that doesn't fit into any other category."),
    NETWORK_CONNECTED("network.connected", "A network connection notification, such as successful sign-on to a network service. This should not be confused with device.added for new network devices."),
    NETWORK_DISCONNECTED("network.disconnected", "A network disconnected notification. This should not be confused with device.removed for disconnected network devices."),
    NETWORK_ERROR("network.error", "A network-related or connection-related error."),
    PRESENCE("presence", "A generic presence change notification that doesn't fit into any other category, such as going away or idle."),
    PRESENCE_OFFLINE("presence.offline", "An offline presence change notification."),
    PRESENCE_ONLINE("presence.online", "An online presence change notification."),
    TRANSFER("transfer", "A generic file transfer or download notification that doesn't fit into any other category."),
    TRANSFER_COMPLETE("transfer.complete", "A file transfer or download complete notification."),
    TRANSFER_ERROR("transfer.error", "A file transfer or download error.");

    private final String type;
    private final String description;

    NotificationCategory(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return type;
    }
}
