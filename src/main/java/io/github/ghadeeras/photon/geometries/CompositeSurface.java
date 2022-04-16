package io.github.ghadeeras.photon.geometries;

import io.github.ghadeeras.photon.materials.Material;
import io.github.ghadeeras.photon.sampling.Sampler;
import io.github.ghadeeras.photon.sampling.WeightedSampling;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public record CompositeSurface(GeometricSurface... surfaces) implements GeometricSurface {

    public static CompositeSurface of(GeometricSurface... things) {
        return new CompositeSurface(things);
    }

    @Override
    public Incident incident(Ray ray, Material material, double min, double max) {
        var currentMax = max;
        Incident result = Incident.miss;
        for (var surface : surfaces) {
            if (surface.incident(ray, material, min, currentMax) instanceof Incident.Hit hit) {
                result = hit;
                currentMax = hit.distance();
            }
        }
        return result;
    }

    @Override
    public Sampler<Vector> visibleSurface(Vector viewPosition, double time) {
        return WeightedSampling.equallyMixedSampler(Stream.of(surfaces)
            .map(s -> s.visibleSurface(viewPosition, time))
            .toList()
        );
    }

    @Override
    public BoundingBox boundingVolume(double time1, double time2) {
        return Stream.of(surfaces)
            .map(thing -> thing.boundingVolume(time1, time2))
            .reduce(BoundingBox::enclose)
            .orElseThrow(() -> new RuntimeException("Empty thing sets are not supported!"));
    }

    @Override
    public List<GeometricSurface> flatten() {
        return Stream.of(surfaces)
            .flatMap(surface -> surface.flatten().stream())
            .collect(toList());
    }

}
