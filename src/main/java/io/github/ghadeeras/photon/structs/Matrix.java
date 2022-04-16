package io.github.ghadeeras.photon.structs;

import java.util.function.UnaryOperator;

public record Matrix(Vector x, Vector y, Vector z) implements UnaryOperator<Vector> {

    public static Matrix identity = Matrix.of(Vector.unitX, Vector.unitY, Vector.unitZ);

    public static Matrix of(Vector x, Vector y, Vector z) {
        return new Matrix(x, y, z);
    }

    public static Matrix projectionOn(Vector vector) {
        return vector.outer(vector).scale(1 / vector.lengthSquared());
    }

    public static Matrix scaling(Vector axis, double along, double away) {
        var projection = projectionOn(axis);
        var rejection = identity.minus(projection);
        return projection.scale(along).plus(rejection.scale(away));
    }

    public static Matrix rotation(Vector axis, double angle) {
        var unitAxis = axis.unit();
        var cos = Math.cos(angle);
        var sin = Math.sin(angle);
        return rotMat(cos, sin, unitAxis.x(), unitAxis.y(), unitAxis.z());
    }

    private static Matrix rotMat(double cos, double sin, double x, double y, double z) {
        var oneMinusCos = 1 - cos;
        var x1 = x * oneMinusCos;
        var y1 = y * oneMinusCos;
        var z1 = z * oneMinusCos;
        var xx = x * x1;
        var yy = y * y1;
        var zz = z * z1;
        var xy = x * y1;
        var yz = y * z1;
        var zx = z * x1;
        return Matrix.of(
            Vector.of(xx + cos, xy + z * sin, zx - y * sin),
            Vector.of(xy - z * sin, yy + cos, yz + x * sin),
            Vector.of(zx + y * sin, yz - x * sin, zz + cos)
        );
    }

    public static Matrix xAlignedWith(Vector u) {
        var maxDot = 0.5 * u.lengthSquared();
        var v1 = Vector.of(u.z(), u.x(), u.y());
        var v2 = u.dot(v1) < maxDot ? v1 : Vector.of(u.x(), -2 * u.y(), u.z());
        return alignedWith(u, v2);
    }

    public static Matrix yAlignedWith(Vector u) {
        var m = xAlignedWith(u);
        return Matrix.of(m.z(), m.x(), m.y());
    }

    public static Matrix zAlignedWith(Vector u) {
        var m = xAlignedWith(u);
        return Matrix.of(m.y(), m.z(), m.x());
    }

    public static Matrix alignedWith(Vector u, Vector v) {
        var x = u.unit();
        var y = v.reject(x).unit();
        var z = x.cross(y);
        return Matrix.of(x, y, z);
    }

    public double determinant() {
        return x.cross(y).dot(z);
    }

    public Matrix transposed() {
        return Matrix.of(
            Vector.of(x.x(), y.x(), z.x()),
            Vector.of(x.y(), y.y(), z.y()),
            Vector.of(x.z(), y.z(), z.z())
        );
    }

    public Matrix inverse() {
        var antiMatrix = antiMatrix();
        var det = antiMatrix.z.dot(z);
        return antiMatrix.transposed().scale(1 / det);
    }

    public Matrix antiMatrix() {
        return Matrix.of(
            y.cross(z),
            z.cross(x),
            x.cross(y)
        );
    }

    public Matrix scale(double factor) {
        return Matrix.of(x.scale(factor), y.scale(factor), z.scale(factor));
    }

    public Matrix plus(Matrix that) {
        return Matrix.of(
            this.x.plus(that.x),
            this.y.plus(that.y),
            this.z.plus(that.z)
        );
    }

    public Matrix minus(Matrix that) {
        return Matrix.of(
            this.x.minus(that.x),
            this.y.minus(that.y),
            this.z.minus(that.z)
        );
    }

    public Matrix mul(Matrix that) {
        return Matrix.of(
            this.mul(that.x),
            this.mul(that.y),
            this.mul(that.z)
        );
    }

    public Vector mul(Vector vector) {
        return x.scale(vector.x()).plus(y.scale(vector.y())).plus(z.scale(vector.z()));
    }

    @Override
    public Vector apply(Vector vector) {
        return mul(vector);
    }

}
