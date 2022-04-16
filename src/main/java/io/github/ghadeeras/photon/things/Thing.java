package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.geometries.GeometricSurface;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.transformations.Transformable;
import io.github.ghadeeras.photon.transformations.Transformation;

import java.util.List;
import java.util.function.Function;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toMap;

public interface Thing extends Transformable<Thing> {

    Incident incident(Ray ray, double min, double max);

    GeometricSurface surface();

    List<AtomicThing> flatten();

    default <I extends Transformation.Instance> Thing transformed(Transformation<I> transformation) {
        return new TransformedThing<>(this, transformation);
    }

    default Thing optimized(double time1, double time2) {
        var boundingVolume = surface().boundingVolume(time1, time2);
        var orderingDirection = boundingVolume.max().minus(boundingVolume.min());
        var things = flatten();
        var thingsOrder = things.stream().collect(toMap(Function.identity(), thing -> thing
            .surface()
            .boundingVolume(time1, time2)
            .center()
            .minus(boundingVolume.min())
            .dot(orderingDirection)
        ));
        things.sort(comparing(thingsOrder::get));
        return things.size() > 1 ?
            ThingsTree.of(
                CompositeThing.of(things.subList(0, things.size() / 2).toArray(Thing[]::new)).optimized(time1, time2),
                CompositeThing.of(things.subList(things.size() / 2, things.size()).toArray(Thing[]::new)).optimized(time1, time2),
                time1,
                time2
            ) :
            ThingsTree.of(
                things.get(0),
                things.get(0),
                time1,
                time2
            );
    }

}
