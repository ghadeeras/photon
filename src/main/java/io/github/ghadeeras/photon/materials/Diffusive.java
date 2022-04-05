package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.sampling.Sampler;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;
import io.github.ghadeeras.photon.structs.Vector;

import static io.github.ghadeeras.photon.sampling.Samplers.sphereSurface;

public record Diffusive(Color color) implements Material {

    private static final Sampler<Vector> unitVectorsSampler = sphereSurface().caching(0x10000);

    public static Diffusive of(Color color) {
        return new Diffusive(color);
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        var scatterDirection = hit.point().normal().scale(1.0001).plus(unitVectorsSampler.next());
        return Effect.redirectionOf(color, scatterDirection);
    }

}
