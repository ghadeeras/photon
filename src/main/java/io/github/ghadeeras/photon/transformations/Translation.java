package io.github.ghadeeras.photon.transformations;

import io.github.ghadeeras.photon.Box;
import io.github.ghadeeras.photon.Transformation;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.DoubleFunction;

public record Translation(DoubleFunction<Vector> position) implements Transformation<Vector> {

    @Override
    public Vector instance(Ray ray) {
        return position.apply(ray.time());
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
            localHit.thing(),
            localHit.material(),
            localHit.position().plus(instancePosition),
            localHit.normal(),
            localHit.distance()
        );
    }

    @Override
    public Box boundingVolume(Box box, double time1, double time2) {
        Box movement = new Box(position.apply(time1), position.apply(time2), time1, time2);
        return new Box(
            box.min().plus(movement.min()),
            box.max().plus(movement.max()),
            time1,
            time2
        );
    }
}
