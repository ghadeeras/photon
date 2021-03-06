package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.misc.Utils;

import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

public class ShuffledIndexesSampler implements Sampler<int[]> {

    private final static ThreadLocal<Random> random  = ThreadLocal.withInitial(() -> new Random(Utils.staticHashCode()));

    private final int indexesCount;

    private ShuffledIndexesSampler(int indexesCount) {
        this.indexesCount = indexesCount;
    }

    public static ShuffledIndexesSampler of(int indexesCount) {
        return new ShuffledIndexesSampler(indexesCount);
    }

    @Override
    public int[] next() {
        return randomIndices(indexesCount, random.get());
    }

    public static int[] randomIndices(int count, RandomGenerator generator) {
        var result = IntStream.range(0, count).toArray();
        for (int i = count - 1; i > 0; i--) {
            int j = generator.nextInt(i);
            int n = result[i];
            result[i] = result[j];
            result[j] = n;
        }
        return result;
    }

}
