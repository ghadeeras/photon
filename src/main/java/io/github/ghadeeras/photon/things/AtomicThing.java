package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.geometries.GeometricSurface;
import io.github.ghadeeras.photon.materials.Material;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;

import java.util.List;

import static java.util.stream.Collectors.toList;

public record AtomicThing(GeometricSurface surface, Material material) implements Thing {

    @Override
    public Incident incident(Ray ray, double min, double max) {
        return surface.incident(ray, material, min, max);
    }

    @Override
    public List<Thing> flatten() {
        return surface.flatten().stream()
            .map(surface -> surface.of(material))
            .collect(toList());
    }

}
