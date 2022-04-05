package io.github.ghadeeras.photon.structs;

import io.github.ghadeeras.photon.things.AtomicThing;

public sealed interface Incident {

    Miss miss = new Miss();

    static Hit.Local of(Ray ray, AtomicThing thing, SurfacePoint point, double distance) {
        return new Hit.Local(ray, thing, point, distance);
    }

    record Miss() implements Incident {}

    sealed interface Hit extends Incident {

        Local localHit();

        Ray ray();

        AtomicThing thing();

        SurfacePoint point();

        double distance();

        default Global globalHit(Ray ray, double distance, SurfacePoint point) {
            return new Global(localHit(), ray, point, distance);
        }

        default Vector surfacePosition() {
            return localHit().thing().surface().surfacePosition(localHit().point());
        }

        record Local(Ray ray, AtomicThing thing, SurfacePoint point, double distance) implements Hit {

            @Override
            public Local localHit() {
                return this;
            }

        }

        record Global(Local localHit, Ray ray, SurfacePoint point, double distance) implements Hit {

            @Override
            public AtomicThing thing() {
                return localHit.thing;
            }

        }

    }

}