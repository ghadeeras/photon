package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.BoundingBox;
import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.Thing;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Range;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.List;

import static java.util.Collections.singletonList;

public class Box implements Thing {

    private final Material material;
    private final BoundingBox boundingBox;

    public Box(Material material, Vector dimensions) {
        this.material = material;
        boundingBox = new BoundingBox(
            dimensions.scale(-1D / 2),
            dimensions.scale(1D / 2),
            0, 0
        );
    }

    public static Box of(Material material, Vector dimensions) {
        return new Box(material, dimensions);
    }

    @Override
    public Incident incident(Ray ray, double min, double max) {
        var range = boundingBox.potentialHitRange(ray, min, max);
        return range instanceof Range.Bounded bounded ?
            incident(ray, bounded, min) :
            Incident.miss;
    }

    private Incident incident(Ray ray, Range.Bounded bounded, double min) {
        var distance = bounded.min() > min ? bounded.min() : bounded.max();
        var position = ray.origin().plus(ray.direction().scale(distance));
        if (approximatelyEqual(position.x(), boundingBox.min().x())) {
            return hit(ray, position, Vector.unitX.neg(), distance);
        } else if (approximatelyEqual(position.y(), boundingBox.min().y())) {
            return hit(ray, position, Vector.unitY.neg(), distance);
        } else if (approximatelyEqual(position.z(), boundingBox.min().z())) {
            return hit(ray, position, Vector.unitZ.neg(), distance);
        } else if (approximatelyEqual(position.x(), boundingBox.max().x())) {
            return hit(ray, position, Vector.unitX, distance);
        } else if (approximatelyEqual(position.y(), boundingBox.max().y())) {
            return hit(ray, position, Vector.unitY, distance);
        } else if (approximatelyEqual(position.z(), boundingBox.max().z())) {
            return hit(ray, position, Vector.unitZ, distance);
        }
        return Incident.miss;
    }

    private static boolean approximatelyEqual(double v1, double v2) {
        return Math.abs(v1 - v2) < 0.001;
    }

    private Incident.Hit hit(Ray ray, Vector position, Vector normal, double distance) {
        return Incident.hit(ray, this, material, position, normal, distance);
    }

    @Override
    public BoundingBox boundingVolume(double time1, double time2) {
        return new BoundingBox(boundingBox.min(), boundingBox.max(), time1, time2);
    }

    @Override
    public List<Thing> flatten() {
        return singletonList(this);
    }

}
