package io.github.ghadeeras.photon.sampling;

import io.github.ghadeeras.photon.Sampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToDoubleFunction;
import java.util.stream.Stream;

import static io.github.ghadeeras.photon.sampling.Samplers.unsigned;
import static java.util.stream.Collectors.toMap;

public class WeightedSampling {

    public record WeightedSample<T>(T sample, double weight) {

        public static <T> WeightedSample<T> of(T sample, double weight) {
            return new WeightedSample<>(sample, weight);
        }

    }

    @SafeVarargs
    public static <T> SampleSpace<T> space(WeightedSample<T>... samples) {
        var samplesSet = Set.of(samples);
        var pdf = pdf(samples);
        return SampleSpace.ofWeighted(weightedSampler(samples), pdf, samplesSet::contains);
    }

    @SafeVarargs
    public static <T> Sampler<WeightedSample<T>> weightedSampler(WeightedSample<T>... samples) {
        var weightSum = weightSum(samples);
        var normalizedSamples = normalizeWeights(samples, weightSum);
        return unsigned().map(choice -> weightedSample(choice, normalizedSamples));
    }

    @SafeVarargs
    public static <T> ToDoubleFunction<T> pdf(WeightedSample<T>... samples) {
        Map<T, Double> weightsMap = Stream.of(samples).collect(toMap(WeightedSample::sample, WeightedSample::weight, Double::sum));
        return weightsMap::get;
    }

    private static <T> double weightSum(WeightedSample<T>[] samples) {
        var sum = 0D ;
        for (var sample : samples) {
            sum += sample.weight;
        }
        return sum;
    }

    private static <T> List<WeightedSample<T>> normalizeWeights(WeightedSample<T>[] samples, double weightSum) {
        var result = new ArrayList<WeightedSample<T>>(samples.length);
        for (WeightedSample<T> sample : samples) {
            if (sample.weight == 0) {
                continue;
            }
            result.add(WeightedSample.of(sample.sample, sample.weight / weightSum));
        }
        return result;
    }

    private static <T> WeightedSample<T> weightedSample(double choice, List<WeightedSample<T>> samples) {
        WeightedSample<T> result = samples.get(0);
        for (var sample : samples) {
            choice -= sample.weight;
            if (choice < 0) {
                result = sample;
                break;
            }
        }
        return result;
    }

}
