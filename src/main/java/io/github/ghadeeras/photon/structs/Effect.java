package io.github.ghadeeras.photon.structs;

public sealed interface Effect {

    Color color();

    static Emission emissionOf(Color color) {
        return new Emission(color);
    }

    static Redirection redirectionOf(Color color, Vector vector) {
        return new Redirection(color, vector);
    }

    record Emission(Color color) implements Effect {}
    record Redirection(Color color, Vector vector) implements Effect {}

}
