package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.BoundingBox;
import io.github.ghadeeras.photon.Thing;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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

    @Override
    public BoundingBox boundingVolume(double time1, double time2) {
        return Stream.of(things)
            .map(thing -> thing.boundingVolume(time1, time2))
            .reduce(BoundingBox::enclose)
            .orElseThrow(() -> new RuntimeException("Empty thing sets are not supported!"));
    }

    @Override
    public List<Thing> flatten() {
        return Arrays.asList(things);
    }

}
