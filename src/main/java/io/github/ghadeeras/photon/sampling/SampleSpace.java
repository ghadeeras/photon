package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.Sampler;

import java.util.function.ToDoubleFunction;

public interface SampleSpace<T> extends Sampler<T>, ToDoubleFunction<T> {

    Sampler<T> sampler();

    ToDoubleFunction<T> pdf();

    @Override
    default T next() {
        return sampler().next();
    }

    @Override
    default double applyAsDouble(T value) {
        return pdf().applyAsDouble(value);
    }

    static <T> SampleSpace<T> of(Sampler<T> sampler, ToDoubleFunction<T> pdf) {
        return new Generic<>(sampler, pdf);
    }

    record Generic<T>(Sampler<T> sampler, ToDoubleFunction<T> pdf) implements SampleSpace<T> {}

}
