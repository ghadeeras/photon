package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.Sampler;
import io.github.ghadeeras.photon.structs.Matrix;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.ToDoubleFunction;

public record Surface(Sampler<Point> sampler, ToDoubleFunction<Point> pdf) implements SampleSpace<Surface.Point> {

    static Surface of(Sampler<Point> sampler, ToDoubleFunction<Point> pdf) {
        return new Surface(sampler, pdf);
    }

    record Point(Vector position, Vector normal) {

        public static Point of(Vector position, Vector normal) {
            return new Point(position, normal);
        }

        public Point transform(Vector translation) {
            return new Point(position.plus(translation), normal);
        }

        public Point transform(Matrix matrix) {
            return transform(matrix, matrix.antiMatrix());
        }

        public Point transform(Matrix matrix, Vector translation) {
            return transform(matrix).transform(translation);
        }

        private Point transform(Matrix matrix, Matrix antiMatrix) {
            return new Point(matrix.mul(position), antiMatrix.mul(normal));
        }

    }

    public Surface transform(Vector translation) {
        return Surface.of(
            sampler.map(p -> p.transform(translation)),
            pdf
        );
    }

    public Surface transform(Matrix matrix) {
        var antiMatrix = matrix.antiMatrix();
        var inverse = matrix.inverse();
        var antiInverse = inverse.antiMatrix();
        return Surface.of(
            sampler.map(p -> p.transform(matrix, antiMatrix)),
            point -> transformPDF(point, inverse, antiInverse)
        );
    }

    public Surface transform(Matrix matrix, Vector translation) {
        return transform(matrix).transform(translation);
    }

    private double transformPDF(Point pointAfter, Matrix inverse, Matrix antiInverse) {
        var pointBefore = pointAfter.transform(inverse, antiInverse);
        var pdfBefore = pdf.applyAsDouble(pointBefore);
        var redistribution = Math.sqrt(pointBefore.normal.lengthSquared() / pointAfter.normal.lengthSquared());
        return pdfBefore * redistribution;
    }

}
