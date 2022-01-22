package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.RND;
import io.github.ghadeeras.photon.Sampling;
import io.github.ghadeeras.photon.structs.Vector;

public record UniformSampling(int samplesPerPixel) implements Sampling {

    public static UniformSampling of(int samplesPerPixel) {
        return new UniformSampling(samplesPerPixel);
    }

    @Override
    public Vector samplePos(int sample) {
        return Vector.of(RND.anyUnsigned(), RND.anyUnsigned(), 0);
    }

}
