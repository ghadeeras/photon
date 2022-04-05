package io.github.ghadeeras.photon.transformations;

import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.DoubleUnaryOperator;

public interface Transformable<This extends Transformable<?>> {

    <I extends Transformation.Instance> This transformed(Transformation<I> transformation);

    default This translated(double x, double y, double z) {
        return translated(Vector.of(x, y, z));
    }

    default This translated(double x, double y, double z, Vector velocity) {
        return translated(Vector.of(x, y, z), velocity);
    }

    default This rotated(Vector axis, double angle) {
        return transformed(Linear.rotation(axis, angle));
    }

    default This rotated(Vector axis, DoubleUnaryOperator angle) {
        return transformed(Linear.rotation(axis, angle));
    }

    default This scaled(Vector axis, double along, double away) {
        return transformed(Linear.scaling(axis, along, away));
    }

    private This translated(Vector position) {
        return transformed(new Translation(t -> position));
    }

    private This translated(Vector position, Vector velocity) {
        return transformed(new Translation(t -> position.plus(velocity.scale(t))));
    }

}
