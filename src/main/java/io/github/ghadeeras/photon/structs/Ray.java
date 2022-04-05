package io.github.ghadeeras.photon.structs;

import io.github.ghadeeras.photon.things.Thing;

public record Ray(Vector origin, Vector direction, double time) {

    public static Ray of(double time, Vector origin, Vector direction) {
        return new Ray(origin, direction, time);
    }

    public Ray unit() {
        return new Ray(origin, direction.unit(), time);
    }

    public Vector at(double length) {
        return origin.plus(direction.scale(length));
    }

    public Incident incidentOn(Thing thing, double min, double max) {
        return thing.incident(this, min, max);
    }

}
