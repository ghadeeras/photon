package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.Sampler;
import io.github.ghadeeras.photon.structs.Range;
import io.github.ghadeeras.photon.structs.Vector;

public class Samplers {

    private static final double TwoPI = 2 * Math.PI;

    public static final Sampler<Double> unsigned() {
        return Range.of(0, 1).sampler();
    }

    public static final Sampler<Double> signed() {
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

    public static Sampler<Vector> hemisphere() {
        return hemisphere(0);
    }

    public static Sampler<Vector> hemisphere(double power) {
        return square().map(vector -> squareToHemisphere(vector, power));
    }

    public static Vector squareToHemisphere(Vector vector, double power) {
        var y = 1 - vector.y();
        if (power > 0) {
            y = Math.pow(y, 1 / (1 + power));
        }
        var xzRadius = Math.sqrt(1 - y * y);
        var xAngle = TwoPI * vector.x();
        return Vector.of(xzRadius * Math.sin(xAngle), y, xzRadius * Math.cos(xAngle));
    }

}
