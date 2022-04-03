package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.sampling.Surface.Point;
import io.github.ghadeeras.photon.sampling.WeightedSampling.WeightedSample;
import io.github.ghadeeras.photon.structs.Matrix;
import io.github.ghadeeras.photon.structs.Range;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.List;
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
            Samplers.square().map(v -> Point.of(v, Vector.unitZ)),
            p -> isInUnitSquare(p) ? 1D : 0D
        );
    }

    private static boolean isInUnitSquare(Point p) {
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
            Samplers.disk().map(v -> Point.of(v, Vector.unitZ)),
            p -> isInUnitDisk(p) ? OneByPI : 0
        );
    }

    private static boolean isInUnitDisk(Point p) {
        return
            p.position().lengthSquared() <= 1 &&
            p.normal().approximatelyEqualTo(Vector.unitZ);
    }

    public static Surface sphereSurface() {
        return Surface.of(
            Samplers.sphereSurface().map(v -> Point.of(v, v)),
            p -> isOnUnitSphereSurface(p) ? OneByFourPI : 0
        );
    }

    private static boolean isOnUnitSphereSurface(Point p) {
        return
            approximatelyEqual(p.position().lengthSquared(), 1) &&
            p.normal().approximatelyEqualTo(p.position());
    }

    public static Surface sphereSurfacePortion(double cos1, double cos2) {
        return Surface.of(
            Samplers.sphereSurfacePortion(cos1, cos2).map(v -> Point.of(v, v)),
            sphereSurfacePortionPDF(cos1, cos2)
        );
    }

    public static Surface sphereSurfacePortion(Vector orientation, double cos1, double cos2) {
        return sphereSurfacePortion(cos1, cos2).transform(Matrix.yAlignedWith(orientation));
    }

    private static ToDoubleFunction<Point> sphereSurfacePortionPDF(double cos1, double cos2) {
        var cosRange = Range.of(cos1, cos2);
        var oneByArea = OneByTwoPI / cosRange.length();
        return p ->
            isOnUnitSphereSurface(p) &&
            cosRange.test(p.position().y()) ? oneByArea : 0;
    }

    public static Surface hemisphereSurface() {
        return hemisphereSurface(0);
    }

    public static Surface hemisphereSurface(int power) {
        return Surface.of(
            Samplers.hemisphereSurface(power).map(v -> Point.of(v, v)),
            hemispherePDF(power)
        );
    }

    public static Surface hemisphereSurface(Vector orientation) {
        return hemisphereSurface(orientation, 0);
    }

    public static Surface hemisphereSurface(Vector orientation, int power) {
        return hemisphereSurface(power).transform(Matrix.yAlignedWith(orientation));
    }

    private static ToDoubleFunction<Point> hemispherePDF(int power) {
        return switch (power) {
            case 0 -> p -> isHemispherePoint(p) ? OneByTwoPI : 0;
            case 1 -> p -> isHemispherePoint(p) ? OneByPI * p.normal().y() : 0;
            default -> p -> isHemispherePoint(p) ? (power + 1) * OneByTwoPI * Math.pow(p.normal().y(), power) : 0;
        };
    }

    private static boolean isHemispherePoint(Point point) {
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
            WeightedSampling.sampler(surfaces).map(SampleSpace::next),
            point -> compositePDF(point, surfacesWithWeight)
        );
    }

    private static double compositePDF(Point point, List<WeightedSample<Surface>> surfacesWithWeight) {
        double pdf = 0;
        for (var surface : surfacesWithWeight) {
            pdf += surface.weight() * surface.sample().applyAsDouble(point);
        }
        return pdf;
    }

}
