package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.sampling.WeightedSampling.WeightedSample;
import io.github.ghadeeras.photon.structs.Matrix;
import io.github.ghadeeras.photon.structs.Range;
import io.github.ghadeeras.photon.structs.SurfacePoint;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import static io.github.ghadeeras.photon.Utils.approximatelyEqual;

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
            p -> 1D,
            Surfaces::isInUnitSquare
        );
    }

    private static boolean isInUnitSquare(SurfacePoint p) {
        var px = p.position().x();
        var py = p.position().y();
        var pz = p.position().z();
        return
            0 <= px && px <= 1 &&
            0 <= py && py <= 1 &&
            approximatelyEqual(pz, 0) &&
            p.normal().approximatelyEqualTo(Vector.unitZ);
    }

    public static Surface disk() {
        return Surface.of(
            Samplers.disk().map(v -> SurfacePoint.of(v, Vector.unitZ)),
            p -> OneByPI,
            Surfaces::isInUnitDisk
        );
    }

    private static boolean isInUnitDisk(SurfacePoint p) {
        return
            p.position().lengthSquared() <= 1 &&
            p.normal().approximatelyEqualTo(Vector.unitZ);
    }

    public static Surface sphereSurface() {
        return Surface.of(
            Samplers.sphereSurface().map(v -> SurfacePoint.of(v, v)),
            p -> OneByFourPI,
            Surfaces::isOnUnitSphereSurface
        );
    }

    private static boolean isOnUnitSphereSurface(SurfacePoint p) {
        return
            approximatelyEqual(p.position().lengthSquared(), 1) &&
            p.normal().approximatelyEqualTo(p.position());
    }

    public static Surface sphereSurfacePortion(double cos1, double cos2) {
        return Surface.of(
            Samplers.sphereSurfacePortion(cos1, cos2).map(v -> SurfacePoint.of(v, v)),
            sphereSurfacePortionPDF(cos1, cos2),
            isOnUnitSphereSurfacePortionPDF(cos1, cos2)
        );
    }

    public static Surface sphereSurfacePortion(Vector orientation, double cos1, double cos2) {
        return sphereSurfacePortion(cos1, cos2).transform(Matrix.yAlignedWith(orientation));
    }

    private static Predicate<SurfacePoint> isOnUnitSphereSurfacePortionPDF(double cos1, double cos2) {
        var cosRange = Range.of(cos1, cos2);
        return p -> isOnUnitSphereSurface(p) && cosRange.test(p.position().y());
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
            hemispherePDF(power),
            Surfaces::isHemispherePoint
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

    private static boolean isHemispherePoint(SurfacePoint point) {
        return
            point.position().y() >= 0 &&
            approximatelyEqual(point.position().lengthSquared(), 1) &&
            point.normal().approximatelyEqualTo(point.position());
    }

    @SafeVarargs
    public static Surface composite(WeightedSample<Surface>... surfaces) {
        var surfacesWithWeight = Stream.of(surfaces)
            .filter(surface -> surface.weight() != 0)
            .toList();
        return Surface.of(
            WeightedSampling.space(surfaces).map(SampleSpace::next),
            point -> compositePDF(point, surfacesWithWeight),
            point -> surfacesWithWeight.stream().anyMatch(s -> s.sample().contains(point))
        );
    }

    private static double compositePDF(SurfacePoint point, List<WeightedSample<Surface>> surfacesWithWeight) {
        double pdf = 0;
        for (var surface : surfacesWithWeight) {
            pdf += surface.weight() * surface.sample().pdf(point);
        }
        return pdf;
    }

}
