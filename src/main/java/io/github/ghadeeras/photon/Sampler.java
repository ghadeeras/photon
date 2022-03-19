package io.github.ghadeeras.photon;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface Sampler<T> {

    T next();

    default List<T> next(int count) {
        return Stream.generate(this::next).limit(count).toList();
    }

    default Sampler<T> filter(Predicate<T> predicate) {
        return () -> {
            T result;
            do {
               result = next();
            } while (!predicate.test(result));
            return result;
        };
    }

    default <R> Sampler<R> map(Function<T, R> mapper) {
        return () -> mapper.apply(next());
    }

    default Sampler<List<T>> tuplesOf(int tupleSize) {
        return () -> next(tupleSize);
    }

    default <R> Sampler<R> mapTuplesOf(int tupleSize, Function<List<T>, R> mapper) {
        return tuplesOf(tupleSize).map(mapper);
    }

    default Sampler<T> caching(int count) {
        var cache = next(count);
        var index = ThreadLocal.withInitial(() -> 0);
        return () -> {
            var i = index.get();
            var next = cache.get(i);
            index.set((i + 1) % count);
            return next;
        };
    }

}
