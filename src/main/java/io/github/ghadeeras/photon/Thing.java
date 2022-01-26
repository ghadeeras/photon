package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;
import io.github.ghadeeras.photon.things.Transformed;
import io.github.ghadeeras.photon.transformations.Translation;

import java.util.List;

public interface Thing {

    Incident incident(Ray ray, double min, double max);

    Box boundingVolume(double time1, double time2);

    List<Thing> flatten();

    default <I> Thing transformed(Transformation<I> transformation) {
        return new Transformed<>(this, transformation);
    }

    default Thing translated(double x, double y, double z) {
        return translated(Vector.of(x, y, z));
    }

    private Thing translated(Vector position) {
        return transformed(new Translation(t -> position));
    }

    default Thing translated(double x, double y, double z, Vector velocity) {
        return translated(Vector.of(x, y, z), velocity);
    }

    private Thing translated(Vector position, Vector velocity) {
        return transformed(new Translation(t -> position.plus(velocity.scale(t))));
    }

}
