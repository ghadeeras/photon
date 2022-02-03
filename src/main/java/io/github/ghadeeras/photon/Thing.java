package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;
import io.github.ghadeeras.photon.things.ThingsSet;
import io.github.ghadeeras.photon.things.ThingsTree;
import io.github.ghadeeras.photon.things.Transformed;
import io.github.ghadeeras.photon.transformations.Linear;
import io.github.ghadeeras.photon.transformations.Translation;

import java.util.List;
import java.util.function.DoubleUnaryOperator;

public interface Thing {

    Incident incident(Ray ray, double min, double max);

    BoundingBox boundingVolume(double time1, double time2);

    default Vector surfacePosition(Incident.Hit.Local localHit) {
        return localHit.position();
    }

    List<Thing> flatten();

    default Thing optimized(double time1, double time2) {
        BoundingBox boundingVolume = boundingVolume(time1, time2);
        List<Thing> things = flatten();
        things.sort(boundingVolume.thingsOrder());
        return things.size() > 1 ?
            ThingsTree.of(
                ThingsSet.of(things.subList(0, things.size() / 2).toArray(Thing[]::new)).optimized(time1, time2),
                ThingsSet.of(things.subList(things.size() / 2, things.size()).toArray(Thing[]::new)).optimized(time1, time2),
                time1,
                time2
            ) : ThingsTree.of(
                things.get(0),
                things.get(0),
                time1,
                time2
            );
    }

    default <I> Thing transformed(Transformation<I> transformation) {
        return new Transformed<>(this, transformation);
    }

    default Thing translated(double x, double y, double z) {
        return translated(Vector.of(x, y, z));
    }

    default Thing translated(double x, double y, double z, Vector velocity) {
        return translated(Vector.of(x, y, z), velocity);
    }

    default Thing rotated(Vector axis, double angle) {
        return new Transformed<>(this, Linear.rotation(axis, angle));
    }

    default Thing rotated(Vector axis, DoubleUnaryOperator angle) {
        return new Transformed<>(this, Linear.rotation(axis, angle));
    }

    default Thing scaled(Vector axis, double along, double away) {
        return new Transformed<>(this, Linear.scaling(axis, along, away));
    }

    private Thing translated(Vector position) {
        return transformed(new Translation(t -> position));
    }

    private Thing translated(Vector position, Vector velocity) {
        return transformed(new Translation(t -> position.plus(velocity.scale(t))));
    }

}
