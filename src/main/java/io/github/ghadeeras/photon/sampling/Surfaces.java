package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.structs.Matrix;
import io.github.ghadeeras.photon.structs.SurfacePoint;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.ToDoubleFunction;

public class Surfaces {

    private static final double PI = Math.PI;
    private static final double TwoPI = 2 * PI;
    private static final double FourPI = 4 * PI;

    private static final double OneByPI = 1 / PI;
    private static final double OneByTwoPI = 1 / TwoPI;
    private static final double OneByFourPI = 1 / FourPI;

    public static Surface square() {
        return Surface.of(
            Samplers.square().map(v -> SurfacePoint.of(v, Vector.unitZ)),
            p -> 1D
        );
    }

    public static Surface disk() {
        return Surface.of(
            Samplers.disk().map(v -> SurfacePoint.of(v, Vector.unitZ)),
            p -> OneByPI
        );
    }

    public static Surface sphereSurface() {
        return Surface.of(
            Samplers.sphereSurface().map(v -> SurfacePoint.of(v, v)),
            p -> OneByFourPI
        );
    }

    public static Surface sphereSurfacePortion(double cos1, double cos2) {
        return Surface.of(
            Samplers.sphereSurfacePortion(cos1, cos2).map(v -> SurfacePoint.of(v, v)),
            sphereSurfacePortionPDF(cos1, cos2)
        );
    }

    public static Surface sphereSurfacePortion(Vector orientation, double cos1, double cos2) {
        return sphereSurfacePortion(cos1, cos2).transform(Matrix.yAlignedWith(orientation));
    }

    private static ToDoubleFunction<SurfacePoint> sphereSurfacePortionPDF(double cos1, double cos2) {
        var oneByArea = OneByTwoPI / Math.abs(cos1 - cos2);
        return p -> oneByArea;
    }

    public static Surface hemisphereSurface() {
        return hemisphereSurface(0);
    }

    public static Surface hemisphereSurface(int power) {
        return Surface.of(
            Samplers.hemisphereSurface(power).map(v -> SurfacePoint.of(v, v)),
            hemispherePDF(power)
        );
    }

    public static Surface hemisphereSurface(Vector orientation) {
        return hemisphereSurface(orientation, 0);
    }

    public static Surface hemisphereSurface(Vector orientation, int power) {
        return hemisphereSurface(power).transform(Matrix.yAlignedWith(orientation));
    }

    private static ToDoubleFunction<SurfacePoint> hemispherePDF(int power) {
        return switch (power) {
            case 0 -> p -> OneByTwoPI;
            case 1 -> p -> OneByPI * p.normal().y();
            default -> p -> (power + 1) * OneByTwoPI * Math.pow(p.normal().y(), power);
        };
    }

}
