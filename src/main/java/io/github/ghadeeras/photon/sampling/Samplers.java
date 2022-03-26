package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.Sampler;
import io.github.ghadeeras.photon.structs.Matrix;
import io.github.ghadeeras.photon.structs.Range;
import io.github.ghadeeras.photon.structs.Vector;

public class Samplers {

    private static final double TwoPI = 2 * Math.PI;

    public static Sampler<Double> unsigned() {
        return Range.of(0, 1).sampler();
    }

    public static Sampler<Double> signed() {
        return Range.of(-1, 1).sampler();
    }

    public static Sampler<int[]> shuffledIndexes(int count) {
        return ShuffledIndexesSampler.of(count);
    }

    public static Sampler<Vector> square() {
        return unsigned().mapTuplesOf(2, tuple -> Vector.of(tuple.get(0), tuple.get(1), 0));
    }

    public static Sampler<Vector> cube() {
        return unsigned().mapTuplesOf(3, tuple -> Vector.of(tuple.get(0), tuple.get(1), tuple.get(3)));
    }

    public static Sampler<Vector> disk() {
        return square().map(vector -> {
            var radius = Math.sqrt(vector.x());
            var angle = TwoPI * vector.y();
            return Vector.of(radius * Math.cos(angle), radius * Math.sin(angle), 0);
        });
    }

    public static Sampler<Vector> sphere() {
        return cube().map(vector -> {
            var radius = Math.pow(vector.z(), 1D / 3D);
            return squareToSphere(vector).scale(radius);
        });
    }

    public static Sampler<Vector> sphereSurface() {
        return square().map(Samplers::squareToSphere);
    }

    private static Vector squareToSphere(Vector vector) {
        var y = 1 - 2 * vector.y();
        var xz = Math.sqrt(1 - y * y);
        var angle = TwoPI * vector.x();
        return Vector.of(xz * Math.sin(angle), y, xz * Math.cos(angle));
    }

    public static Sampler<Vector> hemisphereSurface() {
        return hemisphereSurface(0);
    }

    public static Sampler<Vector> hemisphereSurface(int power) {
        return square().map(vector -> squareToHemisphere(vector, power));
    }

    public static Sampler<Vector> hemisphereSurface(Vector orientation) {
        return hemisphereSurface(orientation, 0);
    }

    public static Sampler<Vector> hemisphereSurface(Vector orientation, int power) {
        return reoriented(square().map(vector -> squareToHemisphere(vector, power)), Matrix.yAlignedWith(orientation));
    }

    public static Vector squareToHemisphere(Vector vector, int power) {
        var c = 1 - Math.abs(vector.y() % 1);
        var cosTheta = switch (power) {
            case 0 -> c;
            case 1 -> Math.sqrt(c);
            case 2 -> Math.cbrt(c);
            default -> Math.pow(c, 1D / (1 + power));
        };
        var sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
        var phi = TwoPI * vector.x();
        return Vector.of(sinTheta * Math.sin(phi), cosTheta, sinTheta * Math.cos(phi));
    }

    private static Sampler<Vector> reoriented(Sampler<Vector> sampler, Matrix orientation) {
        return sampler.map(orientation::mul);
    }

}
