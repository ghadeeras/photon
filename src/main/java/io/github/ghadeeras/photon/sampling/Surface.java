package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.structs.Matrix;
import io.github.ghadeeras.photon.structs.SurfacePoint;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.ToDoubleFunction;

import static io.github.ghadeeras.photon.sampling.WeightedSampling.WeightedSample;

public interface Surface extends SampleSpace<SurfacePoint> {

    static Surface of(Sampler<SurfacePoint> sampler, ToDoubleFunction<SurfacePoint> pdf) {
        return new Surface.Generic(sampler, pdf);
    }

    default Surface transform(Vector translation) {
        var negTranslation = translation.neg();
        return Surface.of(
            sampler().map(p -> p.transform(translation)),
            point -> pdf().applyAsDouble(point.transform(negTranslation))
        );
    }

    default Surface transform(Matrix matrix) {
        var antiMatrix = matrix.antiMatrix();
        var inverse = matrix.inverse();
        var antiInverse = inverse.antiMatrix();
        return transform(matrix, antiMatrix, inverse, antiInverse);
    }

    default Surface transform(Matrix matrix, Matrix antiMatrix, Matrix inverse, Matrix antiInverse) {
        return Surface.of(
            sampler().map(p -> p.transform(matrix, antiMatrix)),
            point -> transformPDF(point, inverse, antiInverse)
        );
    }

    private double transformPDF(SurfacePoint globalPoint, Matrix inverse, Matrix antiInverse) {
        var localPoint = globalPoint.transform(inverse, antiInverse);
        var localPDF = pdf().applyAsDouble(localPoint);
        var redistribution = Math.sqrt(localPoint.normal().lengthSquared() / globalPoint.normal().lengthSquared());
        return localPDF * redistribution;
    }

    default Sampler<WeightedSample<Vector>> asSeenFrom(Vector origin) {
        return () -> {
            var point = next();
            var pdf = pdf(point);
            var relativePosition = point.position().minus(origin);
            var normal = point.normal().unit();
            var distanceSquared = relativePosition.lengthSquared();
            var distance = Math.sqrt(distanceSquared);
            var direction = relativePosition.scale(1 / distance);
            var cosTheta = direction.dot(normal);
            var perceivedPDF = pdf * distanceSquared / cosTheta;
            return WeightedSample.of(direction, perceivedPDF);
        };
    }

    record Generic(Sampler<SurfacePoint> sampler, ToDoubleFunction<SurfacePoint> pdf) implements Surface {}

}
