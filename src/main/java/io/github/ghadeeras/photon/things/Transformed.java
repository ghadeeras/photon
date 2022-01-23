package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.Thing;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;

public interface Transformed<T extends Thing, I> extends Thing {

    default Incident incident(Ray ray, double min, double max) {
        var instance = instance(ray);
        var localRay = toLocal(ray, instance);
        var localIncident = thing().incident(localRay, min, max);
        return toGlobal(localIncident, ray, instance);
    }

    default Incident toGlobal(Incident localIncident, Ray globalRay, I instance) {
        return localIncident instanceof Incident.Hit hit ? toGlobal(hit, globalRay, instance) : localIncident;
    }

    I instance(Ray ray);

    Incident.Hit toGlobal(Incident.Hit localHit, Ray globalRay, I instance);

    Ray toLocal(Ray globalRay, I instance);

    T thing();

}
