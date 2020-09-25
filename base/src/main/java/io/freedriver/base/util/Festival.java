package io.freedriver.base.util;

import java.io.IOException;

public class Festival extends ProcessSpawner {
    //private static final Festival INSTANCE = new Festival();

    private Festival() {
        super(() -> new ProcessBuilder("festival", "--server").start());
    }

    public static void speak(String phrase) {
        try (AutoClosingProcess client = tts()){
            client.writeToPipe(phrase);
            client.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static AutoClosingProcess tts() throws IOException {
        return new AutoClosingProcess("festival", "--tts");
    }


    private static AutoClosingProcess client() throws IOException {
        return new AutoClosingProcess("festival_client", "--ttw", "--aucommand", "na_play $FILE");
    }


}
