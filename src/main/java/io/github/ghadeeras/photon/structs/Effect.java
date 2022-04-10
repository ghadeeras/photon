package io.github.ghadeeras.photon.structs;

import io.github.ghadeeras.photon.sampling.SampleSpace;

public sealed interface Effect {

    Color color();

    Effect modulated(Color color);

    static Emission emissionOf(Color color) {
        return new Emission(color);
    }

    static Redirection redirectionOf(Color color, Vector vector) {
        return new Redirection(color, vector);
    }
    
    static Scattering scatteringOf(Color color, SampleSpace<Vector> vector) {
        return new Scattering(color, vector);
    }

    record Emission(Color color) implements Effect {

        @Override
        public Effect modulated(Color color) {
            return emissionOf(this.color.mul(color));
        }

    }

    record Redirection(Color color, Vector direction) implements Effect {

        @Override
        public Effect modulated(Color color) {
            return redirectionOf(this.color.mul(color), direction);
        }

    }

    record Scattering(Color color, SampleSpace<Vector> directionSpace) implements Effect {

        @Override
        public Effect modulated(Color color) {
            return scatteringOf(this.color.mul(color), directionSpace);
        }

    }

}
