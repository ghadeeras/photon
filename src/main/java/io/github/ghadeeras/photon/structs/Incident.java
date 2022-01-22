package io.github.ghadeeras.photon.structs;

import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.Thing;

public sealed interface Incident {

    Miss miss = new Miss();

    static Hit hit(Ray ray, Thing thing, Material material, Vector point, Vector normal, double distance) {
        return new Hit(ray, thing, material, point, normal, distance);
    }

    record Miss() implements Incident {}

    record Hit(Ray ray, Thing thing, Material material, Vector position, Vector normal, double distance) implements Incident {}

}