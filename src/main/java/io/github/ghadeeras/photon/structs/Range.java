package io.github.ghadeeras.photon.structs;

public sealed interface Range {

    Range overlap(Range range);

    Empty empty = new Empty();

    record Empty() implements Range {

        @Override
        public Range overlap(Range range) {
            return this;
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

        @Override
        public Range overlap(Range range) {
            return range instanceof Bounded that ? overlap(that) : empty;
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

    }

}
