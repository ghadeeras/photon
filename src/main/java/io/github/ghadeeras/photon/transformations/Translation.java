package io.github.ghadeeras.photon.transformations;

import io.github.ghadeeras.photon.geometries.BoundingBox;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.DoubleFunction;

public record Translation(DoubleFunction<Vector> position) implements Transformation<Translation.Shift> {

    @Override
    public Shift instance(double time) {
        return new Shift(position.apply(time));
    }

    @Override
    public BoundingBox boundingVolume(BoundingBox boundingBox, double time1, double time2) {
        BoundingBox movement = new BoundingBox(instance(time1).position, instance(time2).position, time1, time2);
        return new BoundingBox(
            boundingBox.min().plus(movement.min()),
            boundingBox.max().plus(movement.max()),
            time1,
            time2
        );
    }

    public record Shift(Vector position) implements Transformation.Instance {

        @Override
        public Vector toLocalPosition(Vector position) {
            return position.minus(this.position);
        }

        @Override
        public Vector toLocalDirection(Vector direction) {
            return direction;
        }

        @Override
        public Vector toGlobalPosition(Vector position) {
            return position.plus(this.position);
        }

        @Override
        public Vector toGlobalArea(Vector area) {
            return area;
        }

    }

}
