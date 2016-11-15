package org.antlr.v4.misc;

/**
 * An immutable range of possible frequencies.
 */
public class FrequencyRange {
    public final Frequency min;
    public final Frequency max;

    // Short names for single-value ranges.
    public static final FrequencyRange NONE = of(Frequency.NONE, Frequency.NONE);
    public static final FrequencyRange ONE = of(Frequency.ONE, Frequency.ONE);
    public static final FrequencyRange MANY = of(Frequency.MANY, Frequency.MANY);

    private FrequencyRange(Frequency min, Frequency max) {
        assert min.compareTo(max) <= 0;
        this.min = min;
        this.max = max;
    }

    /**
     * Returns a range object with the given minimum and maximum values.
     */
    public static FrequencyRange of(Frequency min, Frequency max) {
        return new FrequencyRange(min, max);
    }

    /**
     * Adds the values two ranges, producing a new range.
     */
    public FrequencyRange plus(FrequencyRange that) {
        return FrequencyRange.of(min.plus(that.min), max.plus(that.max));
    }

    /**
     * Produces a range that is the union of two existing ranges.
     */
    public FrequencyRange union(FrequencyRange that) {
        return FrequencyRange.of(min.min(that.min), max.max(that.max));
    }

    @Override
    public String toString() {
        if (min == max) {
            return min.toString();
        } else {
            return min + ".." + max;
        }
    }
}
