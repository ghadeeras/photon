package io.github.ghadeeras.photon.geometries;

import io.github.ghadeeras.photon.materials.Material;
import io.github.ghadeeras.photon.sampling.Surface;
import io.github.ghadeeras.photon.sampling.Surfaces;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.SurfacePoint;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.List;

import static java.util.Collections.singletonList;

public record Sphere(double radius) implements GeometricSurface {

    public static Sphere ofRadius(double radius) {
        return new Sphere(radius);
    }

    @Override
    public Incident incident(Ray ray, Material material, double min, double max) {
        var directionLengthSquared = ray.direction().lengthSquared();
        var halfB = ray.direction().dot(ray.origin()) / directionLengthSquared;
        var c = (ray.origin().lengthSquared() - radius * radius) / directionLengthSquared;
        if (c != 0) {
            var d = halfB * halfB - c;
            if (d <= 0) {
                return Incident.miss;
            }
            var sqrtD = Math.sqrt(d);
            var incident = incident(ray, material, -halfB - sqrtD, min, max);
            return incident instanceof Incident.Miss ?
                incident(ray, material, -halfB + sqrtD, min, max) :
                incident;
        } else {
            return incident(ray, material, -2 * halfB, min, max);
        }
    }

    @Override
    public Surface visibleSurface(Vector viewPosition, double time) {
        return Surfaces.sphereSurfacePortion(viewPosition.neg(), 1, 1 / viewPosition.length());
    }

    private Incident incident(Ray ray, Material material, double length, double min, double max) {
        return min < length && length < max ?
            hit(ray, material, length) :
            Incident.miss;
    }

    private Incident.Hit hit(Ray ray, Material material, double length) {
        var point = ray.at(length);
        return Incident.of(ray, this.of(material), SurfacePoint.of(point, point.unit()), length);
    }

    @Override
    public BoundingBox boundingVolume(double time1, double time2) {
        return new BoundingBox(
            Vector.of(-radius, -radius, -radius),
            Vector.of(+radius, +radius, +radius),
            time1,
            time2
        );
    }

    @Override
    public Vector surfacePosition(SurfacePoint point) {
        var n = point.normal();
        var a = Math.atan2(n.x(), n.z()) / Math.PI;
        var b = Math.acos(n.y()) / Math.PI + 0.5;
        return Vector.of(a, b, 0);
    }

    @Override
    public List<GeometricSurface> flatten() {
        return singletonList(this);
    }

}
