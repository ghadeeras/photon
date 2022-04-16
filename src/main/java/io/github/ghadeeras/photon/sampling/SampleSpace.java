package io.github.ghadeeras.photon.sampling;

public interface SampleSpace<T> extends Sampler<T>, PDF<T> {

    static <T> SampleSpace<T> of(Sampler<T> sampler, PDF<T> pdf) {
        return new Generic<>(sampler, pdf);
    }

    Sampler<T> sampler();

    PDF<T> pdf();

    @Override
    default T next() {
        return sampler().next();
    }

    @Override
    default double ofSample(T value) {
        return pdf(value);
    }

    default double pdf(T value) {
        return pdf().applyAsDouble(value);
    }

    record Generic<T>(Sampler<T> sampler, PDF<T> pdf) implements SampleSpace<T> {}

}
