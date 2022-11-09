package io.freedriver.generty.kibana;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GenetryTrayIcon implements GenetryUI {

    private final TrayIcon trayIcon;
    private final PopupMenu popupMenu;
    private final Runnable shutdownFunction;
    private final Map<String, MenuItem> menuItemsMap = new ConcurrentHashMap<>();
    private State state;

    public GenetryTrayIcon(Runnable shutdownFunction)  {
        this.shutdownFunction = shutdownFunction;
        Image image = getImage(INIT_ICON);
        this.popupMenu = new PopupMenu();
        MenuItem exit = new MenuItem("Exit");
        exit.addActionListener(actionEvent -> {
            shutdownFunction.run();
        });
        popupMenu.add(exit);
        this.trayIcon = new TrayIcon(image, TITLE, popupMenu);
    }

    public GenetryTrayIcon show() throws AWTException {
        if (SystemTray.isSupported()) {
            System.setProperty("awt.useSystemAAFontSettings","on");
            System.setProperty("swing.aatext", "true");
            SystemTray.getSystemTray()
                    .add(trayIcon);
        }
        return this;
    }

    public synchronized void add(KibanaStats kibanaStats) {
        MenuItem item;
        if (!menuItemsMap.containsKey(kibanaStats.getInverterId())) {
            item = new MenuItem(kibanaStats.getInverterId());
            item.setName(kibanaStats.getInverterId());
            popupMenu.add(item);
            menuItemsMap.put(kibanaStats.getInverterId(), item);
        } else {
            item = menuItemsMap.get(kibanaStats.getInverterId());
        }
        item.setLabel(kibanaStats.getMenuText());
    }

    public void running(String info) {
        trayIcon.setToolTip(String.format("%s is running.\n%s", TITLE, info));
        setState(State.RUNNING_STATE);
    }

    public void error(String error) {
        trayIcon.setToolTip(String.format("%s has encountered errors:\n%s", TITLE, error));
        setState(State.ERROR_STATE);
    }


    private synchronized void setState(State state) {
        if (this.state != state) {
            this.state = state;
            trayIcon.setImage(getImage(state.imageURL));
        }
    }

    private static Image getImage(URL imageUrl) {
        Image image = Toolkit.getDefaultToolkit().getImage(imageUrl);
        Dimension dimension = SystemTray.getSystemTray().getTrayIconSize();
        return image.getScaledInstance(dimension.width, dimension.height, java.awt.Image.SCALE_SMOOTH);
    }

    private enum State {
        INIT_STATE(INIT_ICON),
        RUNNING_STATE(RUNNING_ICON),
        ERROR_STATE(ERROR_ICON)
        ;
        private final URL imageURL;

        State(URL imageURL) {
            if (imageURL == null) {
                throw new IllegalStateException("Cannot create application, missing icon.");
            }
            this.imageURL = imageURL;
        }
    }
}
