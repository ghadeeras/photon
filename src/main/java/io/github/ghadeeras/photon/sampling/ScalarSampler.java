package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.Utils;
import io.github.ghadeeras.photon.structs.Range;

import java.util.Random;

public final class ScalarSampler implements Sampler<Double> {

    private final Range.Bounded range;
    private final ThreadLocal<Random> random;

    private ScalarSampler(Range.Bounded range) {
        this.range = range;
        this.random = ThreadLocal.withInitial(() -> new Random(
            (long) Utils.staticHashCode() * (long) range.hashCode()
        ));
    }

    public static ScalarSampler of(Range.Bounded range) {
        return new ScalarSampler(range);
    }

    @Override
    public Double next() {
        return random.get().nextDouble(range.min(), range.max());
    }

}
