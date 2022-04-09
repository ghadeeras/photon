package io.github.ghadeeras.photon.transformations;

import io.github.ghadeeras.photon.geometries.BoundingBox;
import io.github.ghadeeras.photon.geometries.GeometricSurface;
import io.github.ghadeeras.photon.sampling.Surface;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.SurfacePoint;
import io.github.ghadeeras.photon.structs.Vector;
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

        Vector toLocalPosition(Vector position);

        Vector toLocalDirection(Vector direction);

        default Ray toLocal(Ray globalRay) {
            return Ray.of(globalRay.time(), toLocalPosition(globalRay.origin()), toLocalDirection(globalRay.direction()));
        }

        SurfacePoint toGlobal(SurfacePoint localPoint);

        default Incident.Hit.Global toGlobal(Incident.Hit localHit, Ray globalRay) {
            return localHit.globalHit(globalRay, localHit.distance(), toGlobal(localHit.point()));
        }

        Surface toGlobal(Surface surface);

    }

}
