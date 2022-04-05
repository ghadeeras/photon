package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.structs.Matrix;
import io.github.ghadeeras.photon.structs.SurfacePoint;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import static io.github.ghadeeras.photon.sampling.WeightedSampling.WeightedSample;

public interface Surface extends SampleSpace<SurfacePoint> {

    static Surface of(Sampler<SurfacePoint> sampler, ToDoubleFunction<SurfacePoint> pdf, Predicate<SurfacePoint> containment) {
        return new Surface.Generic(
            sampler,
            sampler.map(sample -> WeightedSample.of(sample, pdf.applyAsDouble(sample))),
            pdf,
            containment
        );
    }

    default Surface transform(Vector translation) {
        var negTranslation = translation.neg();
        return Surface.of(
            sampler().map(p -> p.transform(translation)),
            point -> unsafePDF().applyAsDouble(point.transform(negTranslation)),
            point -> containment().test(point.transform(negTranslation))
        );
    }

    default Surface transform(Matrix matrix) {
        var antiMatrix = matrix.antiMatrix();
        var inverse = matrix.inverse();
        var antiInverse = inverse.antiMatrix();
        return Surface.of(
            sampler().map(p -> p.transform(matrix, antiMatrix)),
            point -> transformPDF(point, inverse, antiInverse),
            point -> containment().test(point.transform(inverse, antiInverse))
        );
    }

    default Surface transform(Matrix matrix, Vector translation) {
        return transform(matrix).transform(translation);
    }

    private double transformPDF(SurfacePoint pointAfter, Matrix inverse, Matrix antiInverse) {
        var pointBefore = pointAfter.transform(inverse, antiInverse);
        var pdfBefore = unsafePDF().applyAsDouble(pointBefore);
        var redistribution = Math.sqrt(pointBefore.normal().lengthSquared() / pointAfter.normal().lengthSquared());
        return pdfBefore * redistribution;
    }

    default Sampler<WeightedSample<Vector>> asDirectionsFrom(Vector origin) {
        return () -> {
            var weightedPoint = nextWeighted();
            var point = weightedPoint.sample();
            var pointPosition = point.position().minus(origin);
            var pointNormal = point.normal().unit();
            var distanceSquared = pointPosition.lengthSquared();
            var direction = pointPosition.scale(1 / Math.sqrt(distanceSquared));
            var cosTheta = direction.dot(pointNormal);
            return WeightedSample.of(direction, weightedPoint.weight() * distanceSquared / cosTheta);
        };
    }

    record Generic(Sampler<SurfacePoint> sampler, Sampler<WeightedSample<SurfacePoint>> weightedSampler, ToDoubleFunction<SurfacePoint> unsafePDF, Predicate<SurfacePoint> containment) implements Surface {}

}
