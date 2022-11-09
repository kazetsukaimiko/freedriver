package io.freedriver.generty.kibana;

import java.net.URL;

public interface GenetryUI {
    URL INIT_ICON = GenetryUI.class.getClassLoader().getResource("init_icon.png");
    URL RUNNING_ICON = GenetryUI.class.getClassLoader().getResource("running_icon.png");
    URL ERROR_ICON = GenetryUI.class.getClassLoader().getResource("error_icon.png");
    String TITLE = "Genetry Solar ElasticSearch Bridge";

    void add(KibanaStats kibanaStats);
    void running(String info);

    void error(String error);

}
