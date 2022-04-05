package io.github.ghadeeras.photon.geometries;

import io.github.ghadeeras.photon.structs.Range;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.ToDoubleFunction;

public record BoundingBox(Vector min, Vector max, double time1, double time2) {

    public BoundingBox(Vector min, Vector max, double time1, double time2) {
        var xRange = new Range.Bounded(min.x(), max.x());
        var yRange = new Range.Bounded(min.y(), max.y());
        var zRange = new Range.Bounded(min.z(), max.z());
        this.min = Vector.of(xRange.min(), yRange.min(), zRange.min());
        this.max = Vector.of(xRange.max(), yRange.max(), zRange.max());
        this.time1 = Math.min(time1, time2);
        this.time2 = Math.max(time1, time2);
    }

    public Vector center() {
        return min.plus(max).scale(0.5);
    }

    public BoundingBox enclose(BoundingBox that) {
        var time1 = Math.max(this.time1, that.time1);
        var time2 = Math.min(this.time2, that.time2);
        var box1 = new BoundingBox(this.min, that.min, time1, time2);
        var box2 = new BoundingBox(this.max, that.max, time1, time2);
        return new BoundingBox(box1.min, box2.max, time1, time2);
    }

    public Range potentialHitRange(Ray ray, double min, double max) {
        Range r0 = new Range.Bounded(min, max);
        return potentialHitRange(ray, Vector::x).overlap(r0) instanceof Range.Bounded r1 ?
            potentialHitRange(ray, Vector::y).overlap(r1) instanceof Range.Bounded r2 ?
                potentialHitRange(ray, Vector::z).overlap(r2) :
                Range.empty :
            Range.empty;
    }

    private Range potentialHitRange(Ray ray, ToDoubleFunction<Vector> component) {
        var d1 = distanceTo(this.min, ray, component);
        if (Double.isNaN(d1)) {
            return Range.empty;
        }
        var d2 = distanceTo(this.max, ray, component);
        if (Double.isNaN(d2)) {
            return Range.empty;
        }
        return new Range.Bounded(d1, d2);
    }

    private double distanceTo(Vector bound, Ray ray, ToDoubleFunction<Vector> component) {
        return
            (component.applyAsDouble(bound) - component.applyAsDouble(ray.origin())) /
            component.applyAsDouble(ray.direction());
    }

}
