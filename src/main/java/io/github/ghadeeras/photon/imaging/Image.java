package io.github.ghadeeras.photon.imaging;

import io.github.ghadeeras.photon.structs.Color;

public class Image {

    public final int width;
    public final int height;

    private final Color[][] pixelRows;

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixelRows = new Color[height][];
        for (int i = 0; i < pixelRows.length; i++) {
            pixelRows[i] = new Color[width];
        }
    }

    public Color getPixel(int x, int y) {
        Color color = pixelRows[y][x];
        return color != null ? color : Color.colorBlack;
    }

    public void setPixel(int x, int y, Color color) {
        pixelRows[y][x] = color;
    }

}
