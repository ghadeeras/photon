package io.github.ghadeeras.photon.structs;

import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.Thing;

public sealed interface Incident {

    Miss miss = new Miss();

    static Hit hit(Ray ray, Thing thing, Material material, Vector point, Vector normal, double distance) {
        return new Hit.Local(ray, thing, material, point, normal, distance);
    }

    record Miss() implements Incident {}

    sealed interface Hit extends Incident {

        Local localHit();

        Ray ray();

        Thing thing();

        Material material();

        Vector position();

        Vector normal();

        double distance();

        default Global globalHit(Ray ray, Vector position, Vector normal, double distance) {
            return new Global(localHit(), ray, position, normal, distance);
        }

        default Vector surfacePosition() {
            return thing().surfacePosition(localHit());
        }

        record Local(Ray ray, Thing thing, Material material, Vector position, Vector normal, double distance) implements Hit {

            @Override
            public Local localHit() {
                return this;
            }

        }

        record Global(Local localHit, Ray ray, Vector position, Vector normal, double distance) implements Hit {

            @Override
            public Thing thing() {
                return localHit.thing;
            }

            @Override
            public Material material() {
                return localHit.material();
            }

        }

    }

}