package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.Material;
import io.github.ghadeeras.photon.RND;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

public record Diffusive(Color color) implements Material {

    public static Diffusive of(Color color) {
        return new Diffusive(color);
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        var scatterDirection = hit.normal().scale(1.0001).plus(RND.randomUnitVector());
        return Effect.redirectionOf(color, scatterDirection);
    }

}
