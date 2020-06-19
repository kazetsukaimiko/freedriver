package io.freedriver.base.util.file;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Supplier;

public interface ImageStream extends Supplier<InputStream> {
    static BufferedImage toBufferedImage(Image img) {
        return Optional.of(img)
                .filter(BufferedImage.class::isInstance)
                .map(BufferedImage.class::cast)
                .orElseGet(() -> {
                    BufferedImage bufferedImage = new BufferedImage(
                            img.getWidth(null),
                            img.getHeight(null),
                            BufferedImage.TYPE_INT_ARGB);
                    // Draw the image on to the buffered image
                    Graphics2D bGr = bufferedImage.createGraphics();
                    bGr.drawImage(img, 0, 0, null);
                    bGr.dispose();
                    // Return the buffered image
                    return bufferedImage;
                });
    }

    static InputStream imageInputStream(Image image) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image instanceof BufferedImage
                            ? (BufferedImage) image
                            : toBufferedImage(image)
                    ,"png", os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ByteArrayInputStream(os.toByteArray());
    }
}
