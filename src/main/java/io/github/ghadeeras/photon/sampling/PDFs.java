package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.misc.Utils;
import io.github.ghadeeras.photon.structs.Range;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import static io.github.ghadeeras.photon.misc.Constants.*;

public class PDFs {

    public static <T> PDF<T> cachingLast(PDF<T> pdf) {
        SamplePDF<T> last = new SamplePDF<>();
        return s -> {
            if (Objects.equals(s, last.sample)) {
                return last.pdf;
            } else {
                last.sample = s;
                last.pdf = pdf.applyAsDouble(s);
                return last.pdf;
            }
        };
    }

    public static PDF<Vector> square() {
        return provided(inSquare(), p -> 1D);
    }

    private static Predicate<Vector> inSquare() {
        var range = Range.of(0, 1);
        return p -> Utils.approximatelyEqual(p.z(), 0) && range.test(p.x()) && range.test(p.y());
    }

    public static PDF<Vector> disk() {
        return provided(PDFs::inDisk, p -> OneByPI);
    }

    private static boolean inDisk(Vector p) {
        return p.lengthSquared() < 1;
    }

    public static PDF<Vector> sphere() {
        return provided(Vector::hasUnitLength, p -> OneByFourPI);
    }

    public static PDF<Vector> sphereSurfacePortion(double cos1, double cos2) {
        return sphereSurfacePortion(Vector::y, cos1, cos2);
    }

    public static PDF<Vector> sphereSurfacePortion(Vector orientation, double cos1, double cos2) {
        var y = orientation.unit();
        return sphereSurfacePortion(y::dot, cos1, cos2);
    }

    private static PDF<Vector> sphereSurfacePortion(ToDoubleFunction<Vector> y, double cos1, double cos2) {
        var oneByArea = OneByTwoPI / Math.abs(cos1 - cos2);
        return provided(inSphereSurfacePortion(y, cos1, cos2), p -> oneByArea);
    }

    private static Predicate<Vector> inSphereSurfacePortion(ToDoubleFunction<Vector> y, double cos1, double cos2) {
        var range = Range.of(cos1, cos2);
        return p -> p.hasUnitLength() && range.test(y.applyAsDouble(p));
    }

    public static PDF<Vector> hemisphere() {
        return hemisphere(0);
    }

    public static PDF<Vector> hemisphere(int power) {
        return provided(Vector::hasUnitLength, hemisphere(Vector::y, power));
    }

    public static PDF<Vector> hemisphere(Vector orientation) {
        return hemisphere(orientation, 0);
    }

    public static PDF<Vector> hemisphere(Vector orientation, int power) {
        var y = orientation.unit();
        return provided(Vector::hasUnitLength, hemisphere(y::dot, power));
    }

    private static PDF<Vector> hemisphere(PDF<Vector> y, int power) {
        return switch (power) {
            case 0 -> p -> OneByTwoPI;
            case 1 -> p -> OneByPI * Math.max(y.applyAsDouble(p), 0);
            default -> p -> (power + 1) * OneByTwoPI * Math.pow(Math.max(y.applyAsDouble(p), 0), power);
        };
    }

    private static PDF<Vector> provided(Predicate<Vector> predicate, PDF<Vector> pdf) {
        return p -> predicate.test(p) ? pdf.applyAsDouble(p) : 0;
    }

    private static class SamplePDF<T> {
        T sample;
        double pdf;
    }

}
