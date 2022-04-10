package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.Utils;

import java.util.Random;

public final class DiscreteSampler implements Sampler<Integer> {

    private final int origin;
    private final int bound;
    private final ThreadLocal<Random> random;

    private DiscreteSampler(int origin, int bound) {
        this.origin = origin < bound ? origin : (bound + 1);
        this.bound = origin < bound ? bound : (origin + 1);
        this.random = ThreadLocal.withInitial(() -> new Random(
            (long) Utils.staticHashCode() * (((long) origin) * ((long) bound))
        ));
    }

    public static DiscreteSampler of(int origin, int bound) {
        return new DiscreteSampler(origin, bound);
    }

    @Override
    public Integer next() {
        return random.get().nextInt(origin, bound);
    }

}
