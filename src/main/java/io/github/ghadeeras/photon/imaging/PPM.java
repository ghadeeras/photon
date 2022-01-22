package io.github.ghadeeras.photon.imaging;

import io.github.ghadeeras.photon.structs.Color;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import static java.lang.Math.round;

public class PPM {

    public static void saveTo(String fileName, Image image) throws FileNotFoundException {
        try (var writer = new PrintWriter(fileName)) {
            writeTo(writer, image);
        }
    }

    public static void writeTo(PrintWriter writer, Image image) {
        writeHeader(writer, image.width, image.height);
        for (int y = 0; y < image.height; y++) {
            for (int x = 0; x < image.width; x++) {
                var color = image.getPixel(x, y);
                writeColor(writer, color.clamped());
            }
        }
        writer.flush();
    }

    private static void writeColor(PrintWriter writer, Color color) {
        writer.print(unsignedByte(color.red()));
        writer.print(" ");
        writer.print(unsignedByte(color.green()));
        writer.print(" ");
        writer.print(unsignedByte(color.blue()));
        writer.print("\n");
    }

    private static void writeHeader(PrintWriter writer, int width, int height) {
        writer.print("P3\n");
        writer.print(width);
        writer.print(" ");
        writer.print(height);
        writer.print("\n255\n");
    }

    private static long unsignedByte(double c) {
        return round(255 * c);
    }

}
