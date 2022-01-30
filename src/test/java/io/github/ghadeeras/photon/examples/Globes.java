package io.github.ghadeeras.photon.examples;

import io.github.ghadeeras.photon.*;
import io.github.ghadeeras.photon.imaging.PNG;
import io.github.ghadeeras.photon.materials.*;
import io.github.ghadeeras.photon.noise.Perlin;
import io.github.ghadeeras.photon.noise.Sharpener;
import io.github.ghadeeras.photon.sampling.RegularSampling;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Vector;
import io.github.ghadeeras.photon.things.Sphere;
import io.github.ghadeeras.photon.things.ThingsSet;

import java.io.IOException;
import java.util.function.Function;

public class Globes {

    public static void main(String[] args) throws IOException {
        // Materials
        var reddishMatte = Diffusive.of(Color.of(0.8, 0.4, 0.2));
        var yellowMatte = Diffusive.of(Color.of(1, 1, 0));
        var shinyGold = Reflective.of(Color.of(1, 1, 0));
        var gold = Composite.of(
            shinyGold.withWeight(0.6),
            yellowMatte.withWeight(0.4)
        );
        var textured = Textured.with(h -> {
            var p = h.surfacePosition();
            var x = (int) Math.floor(5 * p.x() + 0.5);
            var y = (int) Math.floor(5 * p.y());
            return (x + y) % 2 == 0 ? shinyGold : reddishMatte;
        });
        var bluish = Color.of(0.2, 0.4, 0.8);
        Perlin perlin = new Perlin();
        var noise = new Sharpener(p -> Math.abs(perlin.noise(p)), 7);
        var bluishMatte = Textured.with(h -> Diffusive.of(bluish.scale((noise.noise(h.localHit().position().scale(3)) + 1) / 2)));
        var silver = Textured.with(h -> {
            var position = h.localHit().position();
            var color = Color.whiteShade(Math.abs(Math.sin(2 * position.y() + noise.noise(position.scale(2)))));
            return Composite.of(
                Reflective.of(color).withWeight(0.7),
                Diffusive.of(color).withWeight(0.3)
            );
        });
        var glass = Refractive.of(1.5, Color.colorWhite);
        var light = Emissive.of(Color.whiteShade(16));

        var distance = 100;

        var subject = ThingsSet.of(
            Sphere.of(glass, 1).translated(1, 1, 1, Vector.of(-0.1, -0.1, -0.1)),
            Sphere.of(bluishMatte, 2).translated(-1, -1, -1),
            Sphere.of(textured, 2).translated(-2, 3, -1),
            Sphere.of(silver, 3).translated(4, 0, -8),
            Sphere.of(light, 10).translated(20, 20, 20)
        ).translated(0, 0, -distance);


        var focalLength = 30;
        var aperture = 0;
        var exposure = 0; // 7.5;
        var focalDistance = distance - 1;
        var gain = 1;

        var frameWidth = 960;
        var aspect = 4.0 / 3.0;

        var quality = 1;
        int depth = 16;

        var world = new World(subject, Globes::galaxyFuzz, depth);

        var camera = new Camera(
            newSensor(RegularSampling::of, quality, gain, aspect, frameWidth),
            new Lens(focalLength),
            focalDistance,
            aperture,
            exposure
        );

        var image = camera.render(world, 0, false);
        PNG.saveTo("_Globes.png", image);
    }

    private static Sensor newSensor(Function<Integer, Sampling> samplingFunction, double quality, double gain, double aspect, int frameWidth) {
        var frameHeight = (int) Math.round(frameWidth / aspect);
        var samplesPerPixel = (int) Math.ceil(quality * Math.pow(0x10000 / (double) Math.max(frameWidth, frameHeight), 2));
        return new Sensor(samplingFunction.apply(samplesPerPixel), gain, frameWidth, frameHeight);
    }

    private static Color galaxyFuzz(Vector direction) {
        var galaxyAxis = Vector.of(1, 1, -1).unit();
        int galaxyThinness = 8;
        var alignmentWithGalaxy = Math.pow(1 - Math.pow(direction.dot(galaxyAxis), 2), galaxyThinness);
        var galaxyBrightness = 1D / 16;
        return Color.whiteShade(alignmentWithGalaxy * galaxyBrightness);
    }

    private static Color sun(Vector direction) {
        var galaxyAxis = Vector.of(1, 1, 1).unit();
        return Color.whiteShade(Math.pow((1 + direction.dot(galaxyAxis)) / 2, 16) * 16);
    }

}
