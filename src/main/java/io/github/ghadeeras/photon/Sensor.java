package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.imaging.Image;
import io.github.ghadeeras.photon.misc.Utils;
import io.github.ghadeeras.photon.sampling.SampleSetsSampler;
import io.github.ghadeeras.photon.sampling.Sampler;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Vector;

import java.time.LocalTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

public class Sensor {

    private static boolean debugging = false;
    private static int testX = 270;
    private static int testY = 130;

    private final Sampler<Vector> pixelSampler;

    public final int samplesPerPixel;
    public final double gain;
    public final int width;
    public final int height;

    private final double halfHeight;
    private final double aspect;

    public Sensor(SampleSetsSampler<Vector> pixelSampler, double gain, int width, int height) {
        this.pixelSampler = pixelSampler.shuffled().caching(pixelSampler.samplesPerSet() * Utils.primeJustUnder(width));
        this.samplesPerPixel = pixelSampler.samplesPerSet();
        this.gain = gain;
        this.width = width;
        this.height = height;

        var halfWidth = width / 2.0;
        this.halfHeight = height / 2.0;
        this.aspect = halfWidth / halfHeight;

        System.out.println("Samples per pixel = " + this.samplesPerPixel);
    }

    public Image render(Function<Vector, Color> projection) {
        var image = new Image(width, height);
        if (debugging) {
            renderPixel(projection, testX, testY);
        } else {
            renderTo(image, projection);
        }
        return image;
    }

    private void renderTo(Image image, Function<Vector, Color> projection) {
        var threads = Runtime.getRuntime().availableProcessors();
        var service = Executors.newFixedThreadPool(threads);
        System.out.printf("[%s] Spawning %s threads ...%n", LocalTime.now(), threads);
        var futureRows = renderConcurrentlyTo(image, projection, service);
        waitFor(futureRows);
        System.out.printf("[%s] Done!%n", LocalTime.now());
        service.shutdown();
    }

    private Future<?>[] renderConcurrentlyTo(Image image, Function<Vector, Color> projection, ExecutorService service) {
        var futureRows = new Future<?>[height];
        for (int y = 0; y < height; y++) {
            final var row = y;
            futureRows[row] = service.submit(() -> renderRow(image, projection, row));
        }
        return futureRows;
    }

    private void waitFor(Future<?>[] futureRows) {
        for (int i = 0; i < futureRows.length; i++) {
            try {
                futureRows[i].get();
                System.out.printf("[%s] Rendered %s out of %s rows.%n", LocalTime.now(), i + 1, futureRows.length);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void renderRow(Image image, Function<Vector, Color> projection, int y) {
        for (int x = 0; x < width; x++) {
            var color = renderPixel(projection, x, y);
            image.setPixel(x, y, color.gammaCorrected().dithered());
        }
    }

    private Color renderPixel(Function<Vector, Color> projection, int x, int y) {
        var color = Color.of(0, 0, 0);
        for (var dP : pixelSampler.next(samplesPerPixel)) {
            if (debugging) {
                dP = Vector.zero;
            }
            var normP = normalized(x + dP.x(), y + dP.y());
            color = color.plus(projection.apply(normP));
        }
        return color.scale(gain / samplesPerPixel);
    }

    private Vector normalized(double x, double y) {
        return Vector.of(
            x / halfHeight - aspect,
            1 - y / halfHeight,
            0
        );
    }

}
