package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.Thing;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;

public record ThingsSet(Thing... things) implements Thing {

    public static ThingsSet of(Thing... things) {
        return new ThingsSet(things);
    }

    @Override
    public Incident incident(Ray ray, double min, double max) {
        var currentMax = max;
        Incident result = Incident.miss;
        for (var thing : things) {
            if (thing.incident(ray, min, currentMax) instanceof Incident.Hit hit) {
                result = hit;
                currentMax = hit.distance();
            }
        }
        return result;
    }

}
