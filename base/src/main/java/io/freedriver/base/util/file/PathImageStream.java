package io.freedriver.base.util.file;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;


public class PathImageStream implements ImageStream {
    private final Image image;
    public PathImageStream(Image image) {
        this.image = image;
    }

    public PathImageStream(Path path) throws IOException {
        this(loadImage(path));
    }

    public TempFile tempFile() throws IOException {
        return new TempFile(this);
    }


    private static Image loadImage(Path path) throws IOException {
        return ImageIO.read(path.toFile());
    }

    @Override
    public InputStream get() {
        return ImageStream.imageInputStream(image);
    }
}
