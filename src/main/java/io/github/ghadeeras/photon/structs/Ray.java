package io.github.ghadeeras.photon.structs;

import io.github.ghadeeras.photon.Thing;

public record Ray(Vector origin, Vector direction) {

    public Ray(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction.unit();
    }

    public static Ray of(Vector origin, Vector direction) {
        return new Ray(origin, direction);
    }

    public Vector at(double length) {
        return origin.plus(direction.scale(length));
    }

    public Incident incidentOn(Thing thing, double min, double max) {
        return thing.incident(this, min, max);
    }

}
