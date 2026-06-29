package io.freedriver.inotify;

public final class InotifyMask {
    public static final int ACCESS = 0x00000001;
    public static final int MODIFY = 0x00000002;
    public static final int MOVED_FROM = 0x00000040;
    public static final int MOVED_TO = 0x00000080;
    public static final int CREATE = 0x00000100;
    public static final int DELETE = 0x00000200;
    public static final int DELETE_SELF = 0x00000400;
    public static final int MOVE_SELF = 0x00000800;

    public static final int SERIAL_HOTPLUG =
            CREATE | DELETE | MOVED_FROM | MOVED_TO | DELETE_SELF | MOVE_SELF;

    private InotifyMask() {
    }
}