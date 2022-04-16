package io.github.ghadeeras.photon.structs;

import static io.github.ghadeeras.photon.misc.Utils.approximatelyEqual;

public record Vector(double x, double y, double z) {

    public static Vector zero = of(0, 0, 0);
    public static Vector unitX = of(1, 0, 0);
    public static Vector unitY = of(0, 1, 0);
    public static Vector unitZ = of(0, 0, 1);

    public static Vector of(double x, double y, double z) {
        return new Vector(x, y, z);
    }

    public Vector scale(double factor) {
        return of(x * factor, y * factor, z * factor);
    }

    public Vector neg() {
        return of(-x, -y , -z);
    }

    public Vector plus(Vector that) {
        return of(this.x + that.x, this.y + that.y, this.z + that.z);
    }

    public Vector minus(Vector that) {
        return of(this.x - that.x, this.y - that.y, this.z - that.z);
    }

    public Vector cross(Vector that) {
        return Vector.of(
            this.y * that.z - this.z * that.y,
            this.z * that.x - this.x * that.z,
            this.x * that.y - this.y * that.x
        );
    }

    public Vector project(Vector that) {
        var unit = that.unit();
        var dot = dot(unit);
        return unit.scale(dot);
    }

    public Vector reject(Vector that) {
        return this.minus(this.project(that));
    }

    public Matrix outer(Vector that) {
        return Matrix.of(
            this.scale(that.x),
            this.scale(that.y),
            this.scale(that.z)
        );
    }

    public double dot(Vector that) {
        return  this.x * that.x + this.y * that.y + this.z * that.z;
    }

    public double lengthSquared() {
        return dot(this);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public Vector unit() {
        return withLength(1);
    }

    public Vector withLength(double newLength) {
        return scale(newLength / length());
    }

    public Ray towards(Vector that, double time) {
        return Ray.of(time, this, that.minus(this));
    }

    public Ray inDirectionOf(Vector direction, double time) {
        return Ray.of(time, this, direction);
    }

    public Ray asRay(double time) {
        return zero.towards(this, time);
    }

    public boolean hasUnitLength() {
        return approximatelyEqual(lengthSquared(), 1);
    }

}
