package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.Box;
import io.github.ghadeeras.photon.Thing;
import io.github.ghadeeras.photon.Transformation;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;

import java.util.List;

import static java.util.stream.Collectors.toList;

public record Transformed<T extends Thing, I>(T thing, Transformation<I> transformation) implements Thing {

    @Override
    public Incident incident(Ray ray, double min, double max) {
        return transformation.incident(thing, ray, min, max);
    }

    @Override
    public Box boundingVolume(double time1, double time2) {
        return transformation.boundingVolume(thing.boundingVolume(time1, time2), time1, time2);
    }

    @Override
    public List<Thing> flatten() {
        return thing.flatten().stream().map(transformation::transform).collect(toList());
    }

}
