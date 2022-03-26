package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.Sampler;

import java.util.function.ToDoubleFunction;

public interface SampleSpace<T> {

    Sampler<T> sampler();

    ToDoubleFunction<T> pdf();

}
