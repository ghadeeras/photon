package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;
import io.github.ghadeeras.photon.things.Translated;

public interface Thing {

    Incident incident(Ray ray, double min, double max);

    default Thing translated(double x, double y, double z) {
        var position = Vector.of(x, y, z);
        return new Translated<>(this, t -> position);
    }

    default Thing translated(double x, double y, double z, Vector velocity) {
        var position = Vector.of(x, y, z);
        return new Translated<>(this, t -> position.plus(velocity.scale(t)));
    }

}
