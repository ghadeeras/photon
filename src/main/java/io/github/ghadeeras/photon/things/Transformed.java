package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.Thing;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;

public interface Transformed<T extends Thing> extends Thing {

    default Incident incident(Ray ray, double min, double max) {
        var localRay = toLocal(ray);
        var localIncident = thing().incident(localRay, min, max);
        return toGlobal(localIncident, ray);
    }

    default Incident toGlobal(Incident localIncident, Ray globalRay) {
        return localIncident instanceof Incident.Hit hit ? toGlobal(hit, globalRay) : localIncident;
    }

    Incident.Hit toGlobal(Incident.Hit localHit, Ray globalRay);

    Ray toLocal(Ray globalRay);

    T thing();

}
