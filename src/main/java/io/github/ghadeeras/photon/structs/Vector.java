package io.github.ghadeeras.photon.structs;

public record Vector(double x, double y, double z) {

    public static Vector zero = of(0, 0, 0);

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
        return scale(1 / length());
    }

    public Ray towards(Vector that) {
        return Ray.of(this, that.minus(this));
    }

    public Ray inDirectionOf(Vector direction) {
        return Ray.of(this, direction);
    }

    public Ray asRay() {
        return zero.towards(this);
    }

}
