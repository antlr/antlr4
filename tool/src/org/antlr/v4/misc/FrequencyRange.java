package org.antlr.v4.misc;

/**
 * A range of frequencies.
 */
public class FrequencyRange {
    public Frequency min;
    public Frequency max;

    public FrequencyRange(FrequencyRange that) {
        this(that.min, that.max);
    }

    public FrequencyRange(Frequency min, Frequency max) {
        assert min.compareTo(max) <= 0;
        this.min = min;
        this.max = max;
    }

    public void add(FrequencyRange that) {
        min = min.plus(that.min);
        max = max.plus(that.max);
    }

    public void union(FrequencyRange that) {
        min = min.min(that.min);
        max = max.max(that.max);
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
