package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.geometries.CompositeSurface;
import io.github.ghadeeras.photon.geometries.GeometricSurface;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public record CompositeThing(Thing... things) implements Thing {

    public static CompositeThing of(Thing... things) {
        return new CompositeThing(things);
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
    public GeometricSurface surface() {
        return CompositeSurface.of(Stream.of(things)
            .map(Thing::surface)
            .toArray(GeometricSurface[]::new)
        );
    }

    @Override
    public List<AtomicThing> flatten() {
        return Stream.of(things)
            .flatMap(thing -> thing.flatten().stream())
            .collect(toList());
    }

}
