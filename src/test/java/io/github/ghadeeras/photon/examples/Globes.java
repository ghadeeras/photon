package io.github.ghadeeras.photon.examples;

import io.github.ghadeeras.photon.Camera;
import io.github.ghadeeras.photon.Lens;
import io.github.ghadeeras.photon.Sensor;
import io.github.ghadeeras.photon.World;
import io.github.ghadeeras.photon.geometries.Sphere;
import io.github.ghadeeras.photon.imaging.PNG;
import io.github.ghadeeras.photon.materials.*;
import io.github.ghadeeras.photon.noise.Perlin;
import io.github.ghadeeras.photon.noise.Sharpener;
import io.github.ghadeeras.photon.sampling.RegularSquareSampler;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Vector;
import io.github.ghadeeras.photon.things.CompositeThing;

import java.io.IOException;
import java.util.function.Function;

public class Globes {

    public static void main(String[] args) throws IOException {
        var perlin = new Perlin();
        var noise = new Sharpener(p -> Math.abs(perlin.noise(p)), 7);

        // The Materials
        var reddishMatte = Diffusive.of(Color.of(0.8, 0.4, 0.2));
        var bluishMatte = Diffusive.of(Color.of(0.2, 0.4, 0.8));
        var goldMirror = Reflective.of(Color.of(1, 1, 0));
        var checkerTextured = Textured.with(checkerBoard(reddishMatte, goldMirror, 5, 5, 0.5, 0.0));
        var shinyWhite = Composite.of(
            Reflective.of(Color.colorWhite).withWeight(0.7),
            Diffusive.of(Color.colorWhite).withWeight(0.3)
        );
        var swirlyBluishMatte = Textured.with(h -> bluishMatte.modulated(Color.whiteShade((noise.noise(h.localHit().point().position().scale(3)) + 1) / 2)));
        var marbledShinyWhite = Textured.with(h -> {
            var position = h.localHit().point().position();
            var color = Color.whiteShade(Math.abs(Math.sin(2 * position.y() + noise.noise(position.scale(2)))));
            return shinyWhite.modulated(color);
        });
        var glass = Refractive.of(1.5, Color.colorWhite);
        var light = Emissive.of(Color.whiteShade(16));

        // The Scene
        var distance = 100;
        var scene = CompositeThing.of(
            Sphere.ofRadius(1).translated(1, 1, 1).of(glass),
            Sphere.ofRadius(2).translated(-1, -1, -1).of(swirlyBluishMatte),
            Sphere.ofRadius(2)
                .scaled(Vector.of(0, 1, 0), 0.5, 1)
                .rotated(Vector.of(1, 0, -1), -Math.PI / 6)
                .translated(-2, 2, -1)
                .of(checkerTextured),
            Sphere.ofRadius(3)
                .scaled(Vector.of(0, 1, 0), 1, 2D / 3)
                .rotated(Vector.of(2, 0, 1), -Math.PI / 6)
                .translated(3, 0, -8)
                .of(marbledShinyWhite),
            Sphere.ofRadius(10).translated(20, 20, 20).of(light)
        ).translated(-0.35, -0.2, -distance);

        // The World
        int depth = 16;
        var world = new World(
            scene,
            Globes::galaxyFuzz,
            depth,
            (thing, things) -> thing.material() instanceof Emissive ? 2D : 1D
        );

        // The Camera
        var focalLength = 30;
        var aperture = 0;
        var exposure = 0;
        var focalDistance = distance - 1;
        var gain = 1;
        var frameWidth = 960;
        var aspect = 4.0 / 3.0;
        var quality = 0.1;
        var camera = new Camera(
            newSensor(quality, gain, aspect, frameWidth),
            new Lens(focalLength),
            focalDistance,
            aperture,
            exposure
        );

        var image = camera.render(world, 0, false);
        PNG.saveTo("_Globes.png", image);
    }

    private static Sensor newSensor(double quality, double gain, double aspect, int frameWidth) {
        var frameHeight = (int) Math.round(frameWidth / aspect);
        var samplesPerPixel = (int) Math.ceil(quality * Math.pow(0x10000 / (double) Math.max(frameWidth, frameHeight), 2));
        return new Sensor(RegularSquareSampler.of(samplesPerPixel), gain, frameWidth, frameHeight);
    }

    private static Color galaxyFuzz(Vector direction) {
        var galaxyAxis = Vector.of(1, 1, -1).unit();
        int galaxyThinness = 8;
        var alignmentWithGalaxy = Math.pow(1 - Math.pow(direction.dot(galaxyAxis), 2), galaxyThinness);
        var galaxyBrightness = 1D / 16;
        return Color.whiteShade(alignmentWithGalaxy * galaxyBrightness);
    }

    private static Function<Incident.Hit, Material> checkerBoard(Material odd, Material even, int xDivs, int yDivs, double xPhase, double yPhase) {
        return h -> {
            var p = h.surfacePosition();
            var x = (int) Math.floor(xDivs * p.x() + xPhase);
            var y = (int) Math.floor(yDivs * p.y() + yPhase);
            return (x + y) % 2 == 0 ? even : odd;
        };
    }

}
