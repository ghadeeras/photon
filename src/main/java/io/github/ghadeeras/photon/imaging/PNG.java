package io.github.ghadeeras.photon.imaging;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PNG {

    public static void saveTo(String fileName, Image image) throws IOException {
        var bufferedImage = new BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB);
        var raster = bufferedImage.getRaster();
        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                var color = image.getPixel(x, y);
                raster.setPixel(x, y, color.scale(255).components());
            }
        }
        ImageIO.write(bufferedImage,"PNG", new File(fileName));
    }

}
