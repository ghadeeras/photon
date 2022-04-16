package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.geometries.GeometricSurface;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.transformations.Transformation;

import java.util.List;

import static java.util.stream.Collectors.toList;

public record TransformedThing<T extends Thing, I extends Transformation.Instance>(T thing, Transformation<I> transformation) implements Thing {

    @Override
    public Incident incident(Ray ray, double min, double max) {
        I instance = transformation.instance(ray.time());
        var localRay = instance.toLocal(ray);
        var localIncident = thing.incident(localRay, min, max);
        return localIncident instanceof Incident.Hit hit ? instance.toGlobal(hit, ray) : localIncident;
    }

    @Override
    public GeometricSurface surface() {
        return thing.surface().transformed(transformation);
    }

    @Override
    public List<AtomicThing> flatten() {
        return thing.flatten().stream()
            .map(thing -> new AtomicThing(thing.surface().transformed(transformation), thing.material()))
            .collect(toList());
    }

}
