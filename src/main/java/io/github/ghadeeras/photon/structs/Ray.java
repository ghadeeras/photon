package io.github.ghadeeras.photon.structs;

import io.github.ghadeeras.photon.Thing;

public record Ray(Vector origin, Vector direction, double time) {

    public Ray(Vector origin, Vector direction, double time) {
        this.time = time;
        this.origin = origin;
        this.direction = direction.unit();
    }

    public static Ray of(double time, Vector origin, Vector direction) {
        return new Ray(origin, direction, time);
    }

    public Vector at(double length) {
        return origin.plus(direction.scale(length));
    }

    public Incident incidentOn(Thing thing, double min, double max) {
        return thing.incident(this, min, max);
    }

}
