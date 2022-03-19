package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.structs.Vector;

import java.util.List;
import java.util.stream.IntStream;

public final class RegularSquareSampler implements SampleSetsSampler<Vector> {

    private final int samplesPerSet;
    private final double distanceBetweenSamples;

    public RegularSquareSampler(int samplesPerSet) {
        this.samplesPerSet = samplesPerSet;
        this.distanceBetweenSamples = 1 / Math.sqrt(samplesPerSet);
    }

    public static RegularSquareSampler of(int samplePerPixel) {
        return new RegularSquareSampler(samplePerPixel);
    }

    @Override
    public List<Vector> next() {
        return IntStream.range(0, samplesPerSet()).mapToObj(this::samplePos).toList();
    }

    @Override
    public int samplesPerSet() {
        return samplesPerSet;
    }

    private Vector samplePos(int sample) {
        var xx = (sample + 0.5) * distanceBetweenSamples;
        var x = xx % 1;
        var yy = (xx - x + 0.5) * distanceBetweenSamples;
        var y = yy % 1;
        return Vector.of(x, y, 0);
    }

}
