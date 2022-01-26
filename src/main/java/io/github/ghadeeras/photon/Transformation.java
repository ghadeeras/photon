package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;

public interface Transformation<I> {

    default Thing transform(Thing thing) {
        return thing.transformed(this);
    }

    default Incident incident(Thing thing, Ray ray, double min, double max) {
        var instance = instance(ray);
        var localRay = toLocal(instance, ray);
        var localIncident = thing.incident(localRay, min, max);
        return toGlobal(instance, localIncident, ray);
    }

    I instance(Ray ray);

    Ray toLocal(I instance, Ray globalRay);

    default Incident toGlobal(I instance, Incident localIncident, Ray globalRay) {
        return localIncident instanceof Incident.Hit hit ? toGlobal(instance, hit, globalRay) : localIncident;
    }

    Incident.Hit toGlobal(I instance, Incident.Hit localHit, Ray globalRay);

    Box boundingVolume(Box box, double time1, double time2);

}
