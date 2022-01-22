package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.Sampling;
import io.github.ghadeeras.photon.structs.Vector;

public final class RegularSampling implements Sampling {

    private final int samplesPerPixel;
    private final double samplesPerPixelLength;

    public RegularSampling(int samplesPerPixel) {
        this.samplesPerPixel = samplesPerPixel;
        this.samplesPerPixelLength = Math.sqrt(samplesPerPixel);
    }

    public static RegularSampling of(int samplePerPixel) {
        return new RegularSampling(samplePerPixel);
    }

    @Override
    public Vector samplePos(int sample) {
        var s = sample / samplesPerPixelLength;
        var x = s % 1;
        var y = (s - x) / samplesPerPixelLength;
        return Vector.of(x, y % 1, 0);
    }

    @Override
    public int samplesPerPixel() {
        return samplesPerPixel;
    }

}
