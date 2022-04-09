package io.github.ghadeeras.photon.sampling;

import java.util.function.ToDoubleFunction;

public interface SampleSpace<T> extends Sampler<T> {

    static <T> SampleSpace<T> of(Sampler<T> sampler, ToDoubleFunction<T> pdf) {
        return new Generic<>(sampler, pdf);
    }

    Sampler<T> sampler();

    ToDoubleFunction<T> pdf();

    @Override
    default T next() {
        return sampler().next();
    }

    default double pdf(T value) {
        return pdf().applyAsDouble(value);
    }

    record Generic<T>(Sampler<T> sampler, ToDoubleFunction<T> pdf) implements SampleSpace<T> {}

}
