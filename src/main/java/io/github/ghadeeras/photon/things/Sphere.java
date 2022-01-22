package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.Thing;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;

public record Sphere(Material material, double radius) implements Thing {

    public static Sphere of(Material material, double radius) {
        return new Sphere(material, radius);
    }

    @Override
    public Incident incident(Ray ray, double min, double max) {
        var halfB = ray.direction().dot(ray.origin());
        var c = ray.origin().lengthSquared() - radius * radius;
        if (c != 0) {
            var d = halfB * halfB - c;
            if (d <= 0) {
                return Incident.miss;
            }
            var sqrtD = Math.sqrt(d);
            var incident = incident(ray, -halfB - sqrtD, min, max);
            return incident instanceof Incident.Miss ?
                incident(ray, -halfB + sqrtD, min, max) :
                incident;
        } else {
            return incident(ray, -2 * halfB, min, max);
        }
    }

    private Incident incident(Ray ray, double length, double min, double max) {
        return min < length && length < max ?
            hit(ray, length) :
            Incident.miss;
    }

    private Incident.Hit hit(Ray ray, double length) {
        var point = ray.at(length);
        return Incident.hit(ray, this, material, point, point.unit(), length);
    }

}
