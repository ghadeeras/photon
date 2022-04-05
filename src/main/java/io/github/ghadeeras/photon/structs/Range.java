package io.github.ghadeeras.photon.structs;

import io.github.ghadeeras.photon.sampling.Sampler;
import io.github.ghadeeras.photon.sampling.ScalarSampler;

import java.util.function.DoublePredicate;

public sealed interface Range extends DoublePredicate {

    Range overlap(Range range);

    double length();

    Empty empty = new Empty();

    static Bounded of(double min, double max) {
        return new Bounded(min, max);
    }

    record Empty() implements Range {

        @Override
        public Range overlap(Range range) {
            return this;
        }

        @Override
        public double length() {
            return 0;
        }

        @Override
        public boolean test(double value) {
            return false;
        }

    }

    record Bounded(double min, double max) implements Range {

        public Bounded(double min, double max) {
            if (min < max) {
                this.min = min;
                this.max = max;
            } else {
                this.min = max;
                this.max = min;
            }
        }

        public Sampler<Double> sampler() {
            return ScalarSampler.of(this);
        }

        @Override
        public Range overlap(Range range) {
            return range instanceof Bounded that ? overlap(that) : empty;
        }

        @Override
        public double length() {
            return max - min;
        }

        public Range overlap(Bounded that) {
            if (this.min > that.max || that.min > this.max) {
                return empty;
            }
            return new Bounded(
                Math.max(this.min, that.min),
                Math.min(this.max, that.max)
            );
        }

        @Override
        public boolean test(double value) {
            return this.min <= value && value < this.max;
        }
    }

}
