package io.github.ghadeeras.photon.sampling;

import java.util.List;
import java.util.Map;

import static io.github.ghadeeras.photon.sampling.Samplers.discrete;
import static io.github.ghadeeras.photon.sampling.Samplers.unsigned;
import static java.util.stream.Collectors.toMap;

public class WeightedSampling {

    public record WeightedSample<T>(T sample, double weight) {

        public static <T> WeightedSample<T> of(T sample, double weight) {
            return new WeightedSample<>(sample, weight);
        }

    }

    public static <T> SampleSpace<T> uniformSpace(List<T> samples) {
        return SampleSpace.of(uniformSampler(samples), uniformPDF(samples));
    }

    public static <T> SampleSpace<T> space(List<WeightedSample<T>> samples) {
        return SampleSpace.of(sampler(samples), pdf(samples));
    }

    public static <T> Sampler<T> uniformSampler(List<T> samples) {
        return discrete(0, samples.size()).map(samples::get);
    }

    public static <T> Sampler<T> sampler(List<WeightedSample<T>> samples) {
        var sum = weightSum(samples);
        return unsigned().map(choice -> chooseSample(choice * sum, samples));
    }

    public static <T> PDF<T> uniformPDF(List<T> samples) {
        var pdf = 1D / samples.size();
        return s -> pdf;
    }

    public static <T> PDF<T> pdf(List<WeightedSample<T>> samples) {
        Map<T, Double> weightsMap = samples.stream().collect(toMap(WeightedSample::sample, WeightedSample::weight, Double::sum));
        return weightsMap::get;
    }

    public static <T, S extends SampleSpace<T>> SampleSpace<T> equallyMixedSpace(List<S> spaces) {
        return SampleSpace.of(equallyMixedSampler(spaces), equallyMixedPDF(spaces));
    }

    public static <T, S extends SampleSpace<T>> SampleSpace<T> mixedSpace(List<WeightedSample<S>> spaces) {
        return SampleSpace.of(mixedSampler(spaces), mixedPDF(spaces));
    }

    public static <T, S extends Sampler<T>> Sampler<T> equallyMixedSampler(List<S> samplers) {
        return uniformSampler(samplers).map(Sampler::next);
    }

    public static <T, S extends Sampler<T>> Sampler<T> mixedSampler(List<WeightedSample<S>> samplers) {
        return sampler(samplers).map(Sampler::next);
    }

    public static <T, P extends PDF<T>> PDF<T> equallyMixedPDF(List<P> pdfs) {
        return s -> {
            var result = 0D;
            for (var pdf : pdfs) {
                result += pdf.applyAsDouble(s);
            }
            return result / pdfs.size();
        };
    }

    public static <T, P extends PDF<T>> PDF<T> mixedPDF(List<WeightedSample<P>> pdfs) {
        return s -> {
            var result = 0D;
            for (var pdf : pdfs) {
                result += pdf.weight * pdf.sample.applyAsDouble(s);
            }
            return result / weightSum(pdfs);
        };
    }

    private static <T> double weightSum(List<WeightedSample<T>> samples) {
        var sum = 0D ;
        for (var sample : samples) {
            sum += sample.weight;
        }
        return sum;
    }

    private static <T> T chooseSample(double choice, List<WeightedSample<T>> samples) {
        WeightedSample<T> result = samples.get(0);
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
