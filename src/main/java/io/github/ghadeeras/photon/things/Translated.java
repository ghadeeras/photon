package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.Thing;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

import java.util.function.DoubleFunction;

public record Translated<T extends Thing>(T thing, DoubleFunction<Vector> position) implements Transformed<T, Vector> {

    @Override
    public Vector instance(Ray ray) {
        return position.apply(ray.time());
    }

    @Override
    public Incident.Hit toGlobal(Incident.Hit localHit, Ray globalRay, Vector instancePosition) {
        return Incident.hit(
            globalRay,
            localHit.thing(),
            localHit.material(),
            localHit.position().plus(instancePosition),
            localHit.normal(),
            localHit.distance()
        );
    }

    @Override
    public Ray toLocal(Ray globalRay, Vector instancePosition) {
        return Ray.of(
            globalRay.time(),
            globalRay.origin().minus(instancePosition),
            globalRay.direction()
        );
    }

}
