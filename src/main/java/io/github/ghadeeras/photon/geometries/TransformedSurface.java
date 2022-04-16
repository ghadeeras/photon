package io.github.ghadeeras.photon.geometries;

import io.github.ghadeeras.photon.materials.Material;
import io.github.ghadeeras.photon.sampling.Sampler;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;
import io.github.ghadeeras.photon.transformations.Transformation;

import java.util.List;

import static java.util.stream.Collectors.toList;

public record TransformedSurface<T extends GeometricSurface, I extends Transformation.Instance>(T surface, Transformation<I> transformation) implements GeometricSurface {

    @Override
    public Incident incident(Ray ray, Material material, double min, double max) {
        var instance = transformation.instance(ray.time());
        var localRay = instance.toLocal(ray);
        var localIncident = surface.incident(localRay, material, min, max);
        return localIncident instanceof Incident.Hit hit ? instance.toGlobal(hit, ray) : localIncident;
    }

    @Override
    public Sampler<Vector> visibleSurface(Vector viewPosition, double time) {
        var instance = transformation.instance(time);
        var localPosition = instance.toLocalPosition(viewPosition);
        return surface.visibleSurface(localPosition, time).map(instance::toGlobalPosition);
    }

    @Override
    public BoundingBox boundingVolume(double time1, double time2) {
        return transformation.boundingVolume(surface.boundingVolume(time1, time2), time1, time2);
    }

    @Override
    public List<GeometricSurface> flatten() {
        return surface.flatten().stream().map(transformation::transform).collect(toList());
    }

}
