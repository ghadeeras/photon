package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.Box;
import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.Thing;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.List;

import static java.util.Collections.singletonList;

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

    @Override
    public Box boundingVolume(double time1, double time2) {
        return new Box(
            Vector.of(-radius, -radius, -radius),
            Vector.of(+radius, +radius, +radius)
        );
    }

    @Override
    public List<Thing> flatten() {
        return singletonList(this);
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
