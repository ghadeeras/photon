package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.sampling.WeightedSampling.WeightedSample;
import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

public interface Material {

    Effect effectOf(Incident.Hit hit);

    default WeightedSample<Material> withWeight(double weight) {
        return WeightedSample.of(this, weight);
    }

    default Material modulated(Color color) {
        return hit -> {
            var effect = effectOf(hit);
            return effect instanceof Effect.Redirection r ?
                Effect.redirectionOf(r.color().mul(color), r.vector()) :
                Effect.emissionOf(effect.color().mul(color));
        };
    }

}
