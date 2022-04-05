package io.github.ghadeeras.photon.sampling;

import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import static io.github.ghadeeras.photon.sampling.WeightedSampling.WeightedSample;

public interface SampleSpace<T> extends Sampler<T> {

    Sampler<T> sampler();

    Sampler<WeightedSample<T>> weightedSampler();

    ToDoubleFunction<T> unsafePDF();

    Predicate<T> containment();

    @Override
    default T next() {
        return sampler().next();
    }

    default WeightedSample<T> nextWeighted() {
        return weightedSampler().next();
    }

    default double pdf(T value) {
        return contains(value) ? unsafePDF().applyAsDouble(value) : 0;
    }

    default boolean contains(T sample) {
        return containment().test(sample);
    }

    static <T> SampleSpace<T> of(Sampler<T> sampler, ToDoubleFunction<T> pdf, Predicate<T> containment) {
        return new Generic<>(
            sampler,
            sampler.map(sample -> WeightedSample.of(sample, pdf.applyAsDouble(sample))),
            pdf,
            containment
        );
    }

    static <T> SampleSpace<T> ofWeighted(Sampler<WeightedSample<T>> weightedSampler, ToDoubleFunction<T> pdf, Predicate<T> containment) {
        return new Generic<>(
            weightedSampler.map(WeightedSample::sample),
            weightedSampler,
            pdf,
            containment
        );
    }

    record Generic<T>(Sampler<T> sampler, Sampler<WeightedSample<T>> weightedSampler, ToDoubleFunction<T> unsafePDF, Predicate<T> containment) implements SampleSpace<T> {}

}
