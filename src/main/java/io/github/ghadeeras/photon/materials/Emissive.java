package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

public record Emissive(Color color) implements Material {

    public static Emissive of(Color color) {
        return new Emissive(color);
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        return Effect.emissionOf(color);
    }

}
