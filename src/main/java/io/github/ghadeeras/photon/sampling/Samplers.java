package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.structs.Matrix;
import io.github.ghadeeras.photon.structs.Range;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

import static io.github.ghadeeras.photon.Constants.TwoPI;

public class Samplers {

    public static Sampler<Integer> discrete(int origin, int bound) {
        return DiscreteSampler.of(origin, bound);
    }

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
        var s = squareToSphereSurfacePortion(1, -1);
        return cube().map(vector -> {
            var radius = Math.cbrt(vector.z());
            return s.apply(vector).scale(radius);
        });
    }

    public static Sampler<Vector> sphereSurface() {
        return sphereSurfacePortion(1, -1);
    }

    public static Sampler<Vector> sphereSurfacePortion(double cos1, double cos2) {
        return square().map(squareToSphereSurfacePortion(cos1, cos2));
    }

    public static Sampler<Vector> sphereSurfacePortion(Vector orientation, double cos1, double cos2) {
        return yAlignedWith(orientation, sphereSurfacePortion(cos1, cos2));
    }

    private static UnaryOperator<Vector> squareToSphereSurfacePortion(double cos1, double cos2) {
        var maxCos = Math.max(cos1, cos2);
        var deltaCos = Math.abs(cos1 - cos2);
        return squareToSphereSurface(deltaCos != 1 ?
            y -> maxCos - deltaCos * y :
            y -> maxCos - y
        );
    }

    public static Sampler<Vector> hemisphereSurface() {
        return hemisphereSurface(0);
    }

    public static Sampler<Vector> hemisphereSurface(int power) {
        return square().map(squareToHemisphereSurface(power));
    }

    public static Sampler<Vector> hemisphereSurface(Vector orientation) {
        return hemisphereSurface(orientation, 0);
    }

    public static Sampler<Vector> hemisphereSurface(Vector orientation, int power) {
        return yAlignedWith(orientation, hemisphereSurface(power));
    }

    public static UnaryOperator<Vector> squareToHemisphereSurface(int power) {
        DoubleUnaryOperator cosThetaFunction = switch (power) {
            case 0 -> y -> 1 - y;
            case 1 -> y -> Math.sqrt(1 - y);
            case 2 -> y -> Math.cbrt(1 - y);
            default -> y -> Math.pow(1 - y, 1D / (1 + power));
        };
        return squareToSphereSurface(cosThetaFunction);
    }

    private static UnaryOperator<Vector> squareToSphereSurface(DoubleUnaryOperator cosThetaFunction) {
        return vector -> {
            var cosTheta = cosThetaFunction.applyAsDouble(vector.y());
            var sinTheta = Math.sqrt(1 - cosTheta * cosTheta);
            var phi = TwoPI * vector.x();
            return Vector.of(sinTheta * Math.sin(phi), cosTheta, sinTheta * Math.cos(phi));
        };
    }

    public static Sampler<Vector> yAlignedWith(Vector orientation, Sampler<Vector> sampler) {
        return reoriented(sampler, Matrix.yAlignedWith(orientation));
    }

    private static Sampler<Vector> reoriented(Sampler<Vector> sampler, Matrix orientation) {
        return sampler.map(orientation::mul);
    }

}
