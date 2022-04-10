package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.Utils;
import io.github.ghadeeras.photon.structs.Range;

import java.util.Random;

public final class ScalarSampler implements Sampler<Double> {

    private final static ThreadLocal<Random> random  = ThreadLocal.withInitial(() -> new Random(Utils.staticHashCode()));

    private final Range.Bounded range;

    private ScalarSampler(Range.Bounded range) {
        this.range = range;
    }

    public static ScalarSampler of(Range.Bounded range) {
        return new ScalarSampler(range);
    }

    @Override
    public Double next() {
        return random.get().nextDouble(range.min(), range.max());
    }

}
