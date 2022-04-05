package io.github.ghadeeras.photon.materials;

import io.github.ghadeeras.photon.sampling.Sampler;
import io.github.ghadeeras.photon.sampling.WeightedSampling;
import io.github.ghadeeras.photon.sampling.WeightedSampling.WeightedSample;
import io.github.ghadeeras.photon.structs.Effect;
import io.github.ghadeeras.photon.structs.Incident;

public record Composite(Sampler<Material> materialsSampler) implements Material {

    @SafeVarargs
    public static Composite of(WeightedSample<Material>... materials) {
        return new Composite(WeightedSampling.space(materials).caching(0x100 * materials.length));
    }

    @Override
    public Effect effectOf(Incident.Hit hit) {
        return materialsSampler.next().effectOf(hit);
    }

}
