package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.materials.Diffusive;
import io.github.ghadeeras.photon.sampling.PDFs;
import io.github.ghadeeras.photon.sampling.SampleSpace;
import io.github.ghadeeras.photon.sampling.WeightedSampling;
import io.github.ghadeeras.photon.structs.*;
import io.github.ghadeeras.photon.things.Thing;

import java.util.function.Function;

public record World(Thing thing, Function<Vector, Color> background, int depth, ImportantDirections importantDirections) {

    public World(Thing thing, Function<Vector, Color> background, int depth) {
        this(thing, background, depth, Diffusive::scatteringSpace);
    }

    public Color trace(Ray ray) {
        return trace(ray.unit(), Color.colorWhite, depth);
    }

    private Color trace(Ray ray, Color color, int depth) {
        if (depth == 0 || (color.red() + color.green() + color.blue()) < 1D / 256D) {
            return Color.colorBlack;
        }
        var incident = ray.incidentOn(thing, 0.001, Double.POSITIVE_INFINITY);
        return incident instanceof Incident.Hit hit ?
            colorOf(hit, color, depth) :
            color.mul(background.apply(ray.direction().unit()));
    }

    private Color colorOf(Incident.Hit hit, Color color, int depth) {
        Effect effect = hit.thing().material().effectOf(hit);
        var newColor = color.mul(effect.color());
        if (effect instanceof Effect.Redirection redirection) {
            return trace(
                Ray.of(hit.ray().time(), hit.point().position(), redirection.direction()),
                newColor,
                depth - 1
            );
        } else if (effect instanceof Effect.Scattering scattering) {
            var scatteringDirectionSpace = withPDFCaching(scattering.directionSpace());
            var directionSpace = WeightedSampling.mixedSpace(
                scatteringDirectionSpace,
                importantDirections.apply(hit)
            );
            var direction = directionSpace.next();
            var directionPDF = directionSpace.pdf(direction);
            var scatteringDirectionPDF = scatteringDirectionSpace.pdf(direction);
            return trace(
                Ray.of(hit.ray().time(), hit.point().position(), direction),
                newColor.scale(scatteringDirectionPDF / directionPDF),
                depth - 1
            );
        } else {
            return newColor;
        }
    }

    private <T> SampleSpace<T> withPDFCaching(SampleSpace<T> space) {
        return SampleSpace.of(
            space.sampler(),
            PDFs.cachingLast(space.pdf())
        );
    }

    public World optimized(double time1, double time2) {
        return new World(thing.optimized(time1, time2), background, depth, importantDirections);
    }

    public interface ImportantDirections extends Function<Incident.Hit, SampleSpace<Vector>> {}

}
