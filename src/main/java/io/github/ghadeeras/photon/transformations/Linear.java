package io.github.ghadeeras.photon.transformations;

import io.github.ghadeeras.photon.BoundingBox;
import io.github.ghadeeras.photon.Transformation;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Matrix;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.UnaryOperator;

public record Linear(DoubleFunction<Matrices> matrices, UnaryOperator<BoundingBox> boundingBoxTransformer) implements Transformation<Linear.Matrices> {

    public static Linear scaling(Vector axis, double along, double away) {
        var matrix = Matrix.scaling(axis, along, away);
        var inverseMatrix = Matrix.scaling(axis, 1 / along, 1 / away);
        var antiMatrix = inverseMatrix.transposed();
        var inverseAntiMatrix = matrix.transposed();
        var matrices = new Matrices(matrix, inverseMatrix, antiMatrix, inverseAntiMatrix);
        return new Linear(time -> matrices, box -> new BoundingBox(matrix.mul(box.min()), matrix.mul(box.max()), box.time1(), box.time2()));
    }

    public static Linear rotation(Vector axis, double angle) {
        var rotation = Matrix.rotation(axis, angle);
        var inverseRotation = rotation.transposed();
        var matrices = new Matrices(rotation, inverseRotation, rotation, inverseRotation);
        return new Linear(time -> matrices, Linear::maxRotatedBox);
    }

    public static Linear rotation(Vector axis, DoubleUnaryOperator angle) {
        return new Linear(time -> {
            var rotation = Matrix.rotation(axis, angle.applyAsDouble(time));
            var inverseRotation = rotation.transposed();
            return new Matrices(rotation, inverseRotation, rotation, inverseRotation);
        }, Linear::maxRotatedBox);
    }

    private static BoundingBox maxRotatedBox(BoundingBox box) {
        var radius = box.max().minus(box.min()).length() / 2;
        var v = Vector.of(radius, radius, radius);
        return new BoundingBox(v.neg(), v, box.time1(), box.time2());
    }

    @Override
    public Matrices instance(double time) {
        return matrices.apply(time);
    }

    @Override
    public Ray toLocal(Matrices instance, Ray globalRay) {
        return Ray.of(globalRay.time(), instance.inverseMatrix.mul(globalRay.origin()), instance.inverseMatrix.mul(globalRay.direction()));
    }

    @Override
    public Incident.Hit.Global toGlobal(Matrices instance, Incident.Hit localHit, Ray globalRay) {
        var normal = instance.antiMatrix.mul(localHit.normal());
        return localHit.globalHit(
            globalRay,
            instance.matrix.mul(localHit.position()),
            instance.matrix == instance.antiMatrix ? normal : normal.unit(),
            localHit.distance()
        );
    }

    @Override
    public BoundingBox boundingVolume(BoundingBox box, double time1, double time2) {
        BoundingBox transformedBox = boundingBoxTransformer.apply(box);
        return new BoundingBox(transformedBox.min(), transformedBox.max(), time1, time2);
    }

    public record Matrices(Matrix matrix, Matrix inverseMatrix, Matrix antiMatrix, Matrix inverseAntiMatrix) {
    }

}
