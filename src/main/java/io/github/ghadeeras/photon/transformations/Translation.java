package io.github.ghadeeras.photon.transformations;

import io.github.ghadeeras.photon.BoundingBox;
import io.github.ghadeeras.photon.Transformation;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.DoubleFunction;

public record Translation(DoubleFunction<Vector> position) implements Transformation<Vector> {

    @Override
    public Vector instance(double time) {
        return position.apply(time);
    }

    @Override
    public Ray toLocal(Vector instancePosition, Ray globalRay) {
        return Ray.of(
            globalRay.time(),
            globalRay.origin().minus(instancePosition),
            globalRay.direction()
        );
    }

    @Override
    public Incident.Hit.Global toGlobal(Vector instancePosition, Incident.Hit localHit, Ray globalRay) {
        return localHit.globalHit(
            globalRay,
            localHit.position().plus(instancePosition),
            localHit.normal(),
            localHit.distance()
        );
    }

    @Override
    public BoundingBox boundingVolume(BoundingBox boundingBox, double time1, double time2) {
        BoundingBox movement = new BoundingBox(instance(time1), instance(time2), time1, time2);
        return new BoundingBox(
            boundingBox.min().plus(movement.min()),
            boundingBox.max().plus(movement.max()),
            time1,
            time2
        );
    }
}
