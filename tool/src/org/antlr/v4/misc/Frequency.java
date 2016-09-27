package org.antlr.v4.misc;

public enum Frequency {
    NONE, ONE, MANY;

    public Frequency plus(Frequency that) {
        return values()[Math.min(ordinal() + that.ordinal(), 2)];
    }

    public Frequency min(Frequency that) {
        return values()[Math.min(ordinal(), that.ordinal())];
    }

    public Frequency max(Frequency that) {
        return values()[Math.max(ordinal(), that.ordinal())];
    }
}
