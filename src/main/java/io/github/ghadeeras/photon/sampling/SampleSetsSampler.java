package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.Sampler;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public interface SampleSetsSampler<T> extends Sampler<List<T>> {

    int samplesPerSet();

    default Sampler<T> shuffled() {
        var indexesSampler = Samplers.shuffledIndexes(samplesPerSet());
        var setIterator = ThreadLocal.withInitial(() ->
            new SetIterator<>(this, indexesSampler)
        );
        return () -> {
            var currentSetIterator = setIterator.get();
            if (!currentSetIterator.hasNext()) {
                setIterator.remove();
                currentSetIterator = setIterator.get();
            }
            return currentSetIterator.next();
        };
    }

    class SetIterator<T> implements Iterator<T> {

        private final List<T> samples;
        private final Iterator<Integer> indexesIterator;

        private SetIterator(SampleSetsSampler<T> sampler, Sampler<int[]> indexesSampler) {
            this.samples = sampler.next();
            this.indexesIterator = IntStream.of(indexesSampler.next()).iterator();
        }

        @Override
        public boolean hasNext() {
            return indexesIterator.hasNext();
        }

        @Override
        public T next() {
            return samples.get(indexesIterator.next());
        }

    }

}
