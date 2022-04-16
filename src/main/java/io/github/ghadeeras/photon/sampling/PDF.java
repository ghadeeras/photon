package io.github.ghadeeras.photon.sampling;

import java.util.function.ToDoubleFunction;

@FunctionalInterface
public interface PDF<T> extends ToDoubleFunction<T> {

    double ofSample(T sample);

    @Override
    default double applyAsDouble(T value) {
        return ofSample(value);
    }

}
