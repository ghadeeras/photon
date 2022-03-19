package io.github.ghadeeras.photon;

import io.github.ghadeeras.photon.structs.Color;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

import static io.github.ghadeeras.photon.materials.Composite.WeightedMaterial;

public interface Material {

    Effect effectOf(Incident.Hit hit);

    default WeightedMaterial withWeight(double weight) {
        return new WeightedMaterial(this, weight);
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
