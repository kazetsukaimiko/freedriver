package io.freedriver.base.util.notify.notifysend;

import io.freedriver.base.util.file.PathImageStream;
import io.freedriver.base.util.file.TempFile;
import io.freedriver.base.util.notify.notifysend.hint.Hint;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class NotifySend {
    private UrgencyLevel urgencyLevel;
    private Duration expiry;
    private String appName;
    private PathImageStream icon;
    private NotificationCategory category;
    private Hint hint;
    private final String summary;
    private final String body;

    public NotifySend(String summary, String body) {
        this.summary = summary;
        this.body = body;
    }

    public UrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(UrgencyLevel urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public Duration getExpiry() {
        return expiry;
    }

    public void setExpiry(Duration expiry) {
        this.expiry = expiry;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public PathImageStream getIcon() {
        return icon;
    }

    public void setIcon(PathImageStream icon) {
        this.icon = icon;
    }

    public void setIcon(String iconPath) throws IOException {
        this.icon = new PathImageStream(Paths.get(iconPath));
    }

    public NotificationCategory getCategory() {
        return category;
    }

    public void setCategory(NotificationCategory category) {
        this.category = category;
    }

    public Hint getHint() {
        return hint;
    }

    public void setHint(Hint hint) {
        this.hint = hint;
    }

    public String getSummary() {
        return summary;
    }

    public String getBody() {
        return body;
    }


    public Stream<String> getUrgencyFlag() {
        if (urgencyLevel != null) {
            return Stream.of("-u", urgencyLevel.toString());
        }
        return Stream.empty();
    }

    public Stream<String> getExpiryFlag() {
        if (expiry != null) {
            return Stream.of("-t", String.valueOf(expiry.toMillis()));
        }
        return Stream.empty();
    }

    public Stream<String> getAppNameFlag() {
        if (appName != null) {
            return Stream.of("-a", appName);
        }
        return Stream.empty();
    }

    public Stream<String> getIconFlag(Path path) {
        if (path != null) {
            return Stream.of("-i", path.toAbsolutePath().toString());
        }
        return Stream.empty();
    }

    private Stream<String> getCategoryFlag() {
        return Optional.ofNullable(getCategory())
                .map(NotificationCategory::toString)
                .map(category -> Stream.of("-c", category))
                .orElseGet(Stream::empty);
    }

    private Stream<String> getHintFlag() {
        return Optional.ofNullable(getHint())
                .map(Hint::toString)
                .map(hint -> Stream.of("-h", hint))
                .orElseGet(Stream::empty);
    }

    public void display() {
        try {
            spawn();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private int spawn() throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("notify-send");

        Stream.of(getUrgencyFlag(), getExpiryFlag(), getAppNameFlag(), getCategoryFlag(), getHintFlag())
                .flatMap(Function.identity())
                .forEach(command::add);

        if (icon != null) {
            try (TempFile tempFile = icon.tempFile()) {
                getIconFlag(tempFile.getPath()).forEach(command::add);
                command.add(getSummary());
                command.add(getBody());
                return new ProcessBuilder(command).start().waitFor();
            }
        } else {
            command.add(getSummary());
            command.add(getBody());
            ProcessBuilder pb = new ProcessBuilder(command);
            return pb.start().waitFor();
        }
    }

    /*
    -u, --urgency=LEVEL               Specifies the urgency level (low, normal, critical).
    -t, --expire-time=TIME            Specifies the timeout in milliseconds at which to expire the notification.
    -a, --app-name=APP_NAME           Specifies the app name for the icon
    -i, --icon=ICON[,ICON...]         Specifies an icon filename or stock icon to display.
    -c, --category=TYPE[,TYPE...]     Specifies the notification category.
    -h, --hint=TYPE:NAME:VALUE        Specifies basic extra data to pass. Valid types are int, double, string and byte.
    -v, --version                     Version of the package.
     */
}
