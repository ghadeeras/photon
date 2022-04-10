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
import io.github.ghadeeras.photon.sampling.SampleSpace;
import io.github.ghadeeras.photon.sampling.SampleSpaces;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Vector;
import io.github.ghadeeras.photon.things.CompositeThing;

import java.io.IOException;

import static io.github.ghadeeras.photon.sampling.WeightedSampling.WeightedSample;
import static io.github.ghadeeras.photon.sampling.WeightedSampling.mixedSpace;

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
        var perlin = new Perlin();
        var noise = new Sharpener(p -> Math.abs(perlin.noise(p)), 7);
        var matte = Diffusive.of(bluish);
        var swirly = Textured.with(h -> matte.modulated(Color.whiteShade((noise.noise(((Incident.Hit) h.localHit()).point().position().scale(3)) + 1) / 2)));
        var shiny = Composite.of(
            Reflective.of(Color.colorWhite).withWeight(0.7),
            Diffusive.of(Color.colorWhite).withWeight(0.3)
        );
        var marbled = Textured.with(h -> {
            var position = ((Incident.Hit) h.localHit()).point().position();
            var color = Color.whiteShade(Math.abs(Math.sin(2 * position.y() + noise.noise(position.scale(2)))));
            return shiny.modulated(color);
        });
        var glass = Refractive.of(1.5, Color.colorWhite);
        var light = Emissive.of(Color.whiteShade(16));

        var distance = 100;

        var subject = CompositeThing.of(
            Sphere.ofRadius(1).translated(1, 1, 1).of(glass),
            Sphere.ofRadius(2).translated(-1, -1, -1).of(swirly),
            Sphere.ofRadius(2)
                .scaled(Vector.of(0, 1, 0), 0.5, 1)
//                .rotated(Vector.of(0, 1, 0), t -> t * Math.PI / 45)
                .rotated(Vector.of(1, 0, -1), -Math.PI / 6)
                .translated(-2, 2, -1)
                .of(textured),
            Sphere.ofRadius(3)
                .scaled(Vector.of(0, 1, 0), 1, 2D / 3)
                .rotated(Vector.of(2, 0, 1), -Math.PI / 6)
                .translated(3, 0, -8)
                .of(marbled),
            Sphere.ofRadius(10).translated(20, 20, 20).of(light)
        ).translated(0, 0, -distance);

        var focalLength = 30;
        var aperture = 0;
        var exposure = 0;
        var focalDistance = distance - 1;
        var gain = 1;

        var frameWidth = 960;
        var aspect = 4.0 / 3.0;

        var quality = 0.1;
        int depth = 16;

        World.ImportantDirections lightSampler = hit -> mixedSpace(
            WeightedSample.of(lightSampleSpace(Vector.of(20, 20, 20 - distance), 10D, hit), 2),
            WeightedSample.of(lightSampleSpace(Vector.of(1, 1, 1 - distance), 1D, hit), 1),
            WeightedSample.of(lightSampleSpace(Vector.of(-1, -1, -1 - distance), 2D, hit), 1),
            WeightedSample.of(lightSampleSpace(Vector.of(-2, 2, -1 - distance), 2D, hit), 1),
            WeightedSample.of(lightSampleSpace(Vector.of(3, 0, -8 - distance), 3D, hit), 1)
        );

        var world = new World(
            subject,
            Globes::galaxyFuzz,
            depth,
//            Diffusive::scatteringSpace
            lightSampler
        );

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

    private static SampleSpace<Vector> lightSampleSpace(Vector lightPos, double lightRadius, Incident.Hit hit) {
        var relativeLightPos = lightPos.minus(hit.point().position());
        var relativeLightDistanceSquared = relativeLightPos.lengthSquared();
        double cosSquared = 1 - lightRadius * lightRadius / relativeLightDistanceSquared;
        return cosSquared > 0 ?
            SampleSpaces.sphereSurfacePortion(relativeLightPos, 1, Math.sqrt(cosSquared)) :
            Diffusive.scatteringSpace(hit);
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

    private static Color sun(Vector direction) {
        var galaxyAxis = Vector.of(1, 1, 1).unit();
        return Color.whiteShade(Math.pow((1 + direction.dot(galaxyAxis)) / 2, 16) * 16);
    }

}
