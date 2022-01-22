package io.github.ghadeeras.photon.things;

import io.github.ghadeeras.photon.Thing;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Ray;
import io.github.ghadeeras.photon.structs.Vector;

public record Translated<T extends Thing>(T thing, Vector position) implements Transformed<T> {

    @Override
    public Incident.Hit toGlobal(Incident.Hit localHit, Ray globalRay) {
        return Incident.hit(
            globalRay,
            localHit.thing(),
            localHit.material(),
            localHit.position().plus(position),
            localHit.normal(),
            localHit.distance()
        );
    }

    @Override
    public Ray toLocal(Ray globalRay) {
        return Ray.of(
            globalRay.origin().minus(position),
            globalRay.direction()
        );
    }

}
