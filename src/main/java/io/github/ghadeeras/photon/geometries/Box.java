package io.github.ghadeeras.photon.geometries;

import io.github.ghadeeras.photon.materials.Material;
import io.github.ghadeeras.photon.sampling.Surface;
import io.github.ghadeeras.photon.structs.*;

import java.util.List;

import static java.util.Collections.singletonList;

public class Box implements GeometricSurface {

    private final BoundingBox boundingBox;

    public Box(Vector dimensions) {
        boundingBox = new BoundingBox(
            dimensions.scale(-1D / 2),
            dimensions.scale(1D / 2),
            0, 0
        );
    }

    public static Box ofSize(double width, double height, double depth) {
        return new Box(Vector.of(width, height, depth));
    }

    @Override
    public Incident incident(Ray ray, Material material, double min, double max) {
        var range = boundingBox.potentialHitRange(ray, min, max);
        return range instanceof Range.Bounded bounded ?
            incident(ray, material, bounded, min) :
            Incident.miss;
    }

    @Override
    public Surface visibleSurface(Vector viewPosition, double time) {
        throw new UnsupportedOperationException();
    }

    private Incident incident(Ray ray, Material material, Range.Bounded bounded, double min) {
        var distance = bounded.min() > min ? bounded.min() : bounded.max();
        var position = ray.origin().plus(ray.direction().scale(distance));
        if (approximatelyEqual(position.x(), boundingBox.min().x())) {
            return hit(ray, material, position, Vector.unitX.neg(), distance);
        } else if (approximatelyEqual(position.y(), boundingBox.min().y())) {
            return hit(ray, material, position, Vector.unitY.neg(), distance);
        } else if (approximatelyEqual(position.z(), boundingBox.min().z())) {
            return hit(ray, material, position, Vector.unitZ.neg(), distance);
        } else if (approximatelyEqual(position.x(), boundingBox.max().x())) {
            return hit(ray, material, position, Vector.unitX, distance);
        } else if (approximatelyEqual(position.y(), boundingBox.max().y())) {
            return hit(ray, material, position, Vector.unitY, distance);
        } else if (approximatelyEqual(position.z(), boundingBox.max().z())) {
            return hit(ray, material, position, Vector.unitZ, distance);
        }
        return Incident.miss;
    }

    private static boolean approximatelyEqual(double v1, double v2) {
        return Math.abs(v1 - v2) < 0.001;
    }

    private Incident.Hit hit(Ray ray, Material material, Vector position, Vector normal, double distance) {
        return Incident.of(ray, this.of(material), SurfacePoint.of(position, normal), distance);
    }

    @Override
    public BoundingBox boundingVolume(double time1, double time2) {
        return new BoundingBox(boundingBox.min(), boundingBox.max(), time1, time2);
    }

    @Override
    public List<GeometricSurface> flatten() {
        return singletonList(this);
    }

}
