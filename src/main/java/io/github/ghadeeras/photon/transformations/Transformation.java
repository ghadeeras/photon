package io.github.ghadeeras.photon.transformations;

import io.github.ghadeeras.photon.geometries.BoundingBox;
import io.github.ghadeeras.photon.geometries.GeometricSurface;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.things.Thing;

public interface Transformation<I extends Transformation.Instance> {

    I instance(double time);

    BoundingBox boundingVolume(BoundingBox box, double time1, double time2);

    default GeometricSurface transform(GeometricSurface surface) {
        return surface.transformed(this);
    }

    default Thing transform(Thing thing) {
        return thing.transformed(this);
    }

    interface Instance {

        Ray toLocal(Ray globalRay);

        Incident.Hit.Global toGlobal(Incident.Hit localHit, Ray globalRay);

    }

}
