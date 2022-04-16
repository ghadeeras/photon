package io.github.ghadeeras.photon.geometries;

import io.github.ghadeeras.photon.materials.Material;
import io.github.ghadeeras.photon.materials.Vacuum;
import io.github.ghadeeras.photon.sampling.SampleSpace;
import io.github.ghadeeras.photon.sampling.Sampler;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.SurfacePoint;
import io.github.ghadeeras.photon.structs.Vector;
import io.github.ghadeeras.photon.things.AtomicThing;
import io.github.ghadeeras.photon.transformations.Transformable;
import io.github.ghadeeras.photon.transformations.Transformation;

import java.util.List;

import static io.github.ghadeeras.photon.misc.Constants.epsilon;

public interface GeometricSurface extends Transformable<GeometricSurface> {

    Incident incident(Ray ray, Material material, double min, double max);

    Sampler<Vector> visibleSurface(Vector viewPosition, double time);

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

    default boolean contains(Incident.Hit hit) {
        return incident(
            hit.ray(),
            Vacuum.instance,
            hit.distance() * (1 - epsilon),
            hit.distance() * (1 + epsilon)
        ) instanceof Incident.Hit;
    }

    default SampleSpace<Vector> directionsFrom(Vector origin, double time) {
        Sampler<Vector> surface = visibleSurface(origin, time);
        return SampleSpace.of(
            surface.map(v -> v.minus(origin)).map(Vector::unit),
            direction -> {
                var ray = Ray.of(time, origin, direction);
                var incident = incident(ray, Vacuum.instance, epsilon, Double.MAX_VALUE);
                return incident instanceof Incident.Hit hit
                    ? hit.point().position().minus(origin).lengthSquared() / -hit.point().sampleArea().dot(direction)
                    : 0;
            }
        );
    }

}
