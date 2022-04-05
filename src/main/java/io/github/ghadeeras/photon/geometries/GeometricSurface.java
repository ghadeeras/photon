package io.github.ghadeeras.photon.geometries;

import io.github.ghadeeras.photon.materials.Material;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.SurfacePoint;
import io.github.ghadeeras.photon.structs.Vector;
import io.github.ghadeeras.photon.things.AtomicThing;
import io.github.ghadeeras.photon.transformations.Transformable;
import io.github.ghadeeras.photon.transformations.Transformation;

import java.util.List;

public interface GeometricSurface extends Transformable<GeometricSurface> {

    Incident incident(Ray ray, Material material, double min, double max);

    BoundingBox boundingVolume(double time1, double time2);

    List<GeometricSurface> flatten();

    default Vector surfacePosition(SurfacePoint point) {
        return point.position();
    }

    default <I extends Transformation.Instance> GeometricSurface transformed(Transformation<I> transformation) {
        return new TransformedSurface<>(this, transformation);
    }

    default AtomicThing of(Material material) {
        return new AtomicThing(this, material);
    }

}
