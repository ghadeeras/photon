package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.sampling.PDFs;
import io.github.ghadeeras.photon.sampling.SampleSpace;
import io.github.ghadeeras.photon.sampling.WeightedSampling;
import io.github.ghadeeras.photon.structs.*;
import io.github.ghadeeras.photon.things.AtomicThing;
import io.github.ghadeeras.photon.things.Thing;

import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleBiFunction;

import static io.github.ghadeeras.photon.sampling.WeightedSampling.WeightedSample;
import static io.github.ghadeeras.photon.sampling.WeightedSampling.equallyMixedSpace;

public record World(Thing thing, Function<Vector, Color> background, int depth, ImportantDirections importantDirections) {

    private static final Color BLACK = Color.colorBlack;

    public World(Thing thing, Function<Vector, Color> background, int depth, ThingImportance thingImportance) {
        this(thing, background, depth, importantDirections(thing, thingImportance));
    }

    private static ImportantDirections importantDirections(Thing thing, ThingImportance thingImportance) {
        var things = thing.flatten();
        var weightedThings = things.stream()
            .map(atomicThing -> WeightedSample.of(atomicThing, thingImportance.applyAsDouble(atomicThing, things)))
            .toList();
        return hit -> WeightedSampling.mixedSpace(weightedThings.stream()
            .filter(weightedThing -> !weightedThing.sample().surface().contains(hit))
            .map(weightedThing -> WeightedSample.of(
                weightedThing.sample().surface().directionsFrom(
                    hit.point().position(),
                    hit.ray().time()
                ),
                weightedThing.weight()
            ))
            .toList()
        );
    }

    public Color trace(Ray ray) {
        return trace(ray.unit(), Color.colorWhite, depth);
    }

    private Color trace(Ray ray, Color color, int depth) {
        if (depth == 0 || (color.red() + color.green() + color.blue()) < 1D / 256D) {
            return BLACK;
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
            var directionSpace = equallyMixedSpace(List.of(
                scatteringDirectionSpace,
                importantDirections.apply(hit)
            ));
            var direction = directionSpace.next();
            var scatteringDirectionPDF = scatteringDirectionSpace.pdf(direction);
            if (scatteringDirectionPDF == 0) {
                return BLACK;
            }
            var directionPDF = directionSpace.pdf(direction);
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

    public interface ThingImportance extends ToDoubleBiFunction<AtomicThing, List<AtomicThing>> {}

}
