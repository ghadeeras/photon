package io.github.ghadeeras.photon.sampling;

import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import static io.github.ghadeeras.photon.sampling.Samplers.discrete;
import static io.github.ghadeeras.photon.sampling.Samplers.unsigned;
import static java.util.stream.Collectors.toMap;

public class WeightedSampling {

    public record WeightedSample<T>(T sample, double weight) {

        public static <T> WeightedSample<T> of(T sample, double weight) {
            return new WeightedSample<>(sample, weight);
        }

    }

    @SafeVarargs
    public static <T> SampleSpace<T> space(T... samples) {
        return SampleSpace.of(sampler(samples), pdf(samples));
    }

    @SafeVarargs
    public static <T> SampleSpace<T> space(WeightedSample<T>... samples) {
        return SampleSpace.of(sampler(samples), pdf(samples));
    }

    @SafeVarargs
    public static <T> Sampler<T> sampler(T... samples) {
        return discrete(0, samples.length).map(choice -> samples[choice]);
    }

    @SafeVarargs
    public static <T> Sampler<T> sampler(WeightedSample<T>... samples) {
        var sum = weightSum(samples);
        return unsigned().map(choice -> chooseSample(choice * sum, samples));
    }

    @SafeVarargs
    public static <T> ToDoubleFunction<T> pdf(T... samples) {
        var pdf = 1D / samples.length;
        return s -> pdf;
    }

    @SafeVarargs
    public static <T> ToDoubleFunction<T> pdf(WeightedSample<T>... samples) {
        Map<T, Double> weightsMap = Stream.of(samples).collect(toMap(WeightedSample::sample, WeightedSample::weight, Double::sum));
        return weightsMap::get;
    }

    @SafeVarargs
    public static <T> SampleSpace<T> mixedSpace(SampleSpace<T>... spaces) {
        return SampleSpace.of(mixedSampler(spaces), mixedPDF(spaces));
    }

    @SafeVarargs
    public static <T> SampleSpace<T> mixedSpace(WeightedSample<SampleSpace<T>>... spaces) {
        return SampleSpace.of(mixedSampler(spaces), mixedPDF(spaces));
    }

    @SafeVarargs
    public static <T, S extends Sampler<T>> Sampler<T> mixedSampler(S... samplers) {
        return sampler(samplers).map(Sampler::next);
    }

    @SafeVarargs
    public static <T, S extends Sampler<T>> Sampler<T> mixedSampler(WeightedSample<S>... samplers) {
        return sampler(samplers).map(Sampler::next);
    }

    @SafeVarargs
    public static <T, P extends ToDoubleFunction<T>> ToDoubleFunction<T> mixedPDF(P... pdfs) {
        return s -> {
            var result = 0D;
            for (var pdf : pdfs) {
                result += pdf.applyAsDouble(s);
            }
            return result / pdfs.length;
        };
    }

    @SafeVarargs
    public static <T, P extends ToDoubleFunction<T>> ToDoubleFunction<T> mixedPDF(WeightedSample<P>... pdfs) {
        return s -> {
            var result = 0D;
            for (var pdf : pdfs) {
                result += pdf.weight * pdf.sample.applyAsDouble(s);
            }
            return result / weightSum(pdfs);
        };
    }

    private static <T> double weightSum(WeightedSample<T>[] samples) {
        var sum = 0D ;
        for (var sample : samples) {
            sum += sample.weight;
        }
        return sum;
    }

    private static <T> T chooseSample(double choice, WeightedSample<T>[] samples) {
        WeightedSample<T> result = samples[0];
        for (var sample : samples) {
            choice -= sample.weight;
            if (choice < 0) {
                result = sample;
                break;
            }
        }
        return result.sample;
    }

}
