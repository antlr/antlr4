/*
 * Copyright (c) 2012-2018 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.misc.Interval;

import std.algorithm;
import std.conv;

/**
 * @uml
 * An immutable inclusive interval a..b
 */
class Interval
{

    enum int INTERVAL_POOL_MAX_VALUE = 1000;

    public static const Interval INVALID = new Interval(-1, -2);

    private static Interval[] cache = new Interval[INTERVAL_POOL_MAX_VALUE + 1];

    public int a;

    public int b;

    public static int creates = 0;

    public static int misses = 0;

    public static int hits = 0;

    public static int outOfRange = 0;

    /**
     * @uml
     * @pure
     * @safe
     */
    public this(int a, int b) @safe pure
    {
        this.a = a;
        this.b = b;
    }

    /**
     * @uml
     * Interval objects are used readonly so share all with the
     * same single value a==b up to some max size.  Use an array as a perfect hash.
     * Return shared object for 0..INTERVAL_POOL_MAX_VALUE or a new
     * Interval object with a..a in it.  On Java.g4, 218623 IntervalSets
     * have a..a (set with 1 element).
     * @safe
     */
    public static Interval of(int a, int b) @safe
    {
        // cache just a..a
        if (a != b || a < 0 || a > INTERVAL_POOL_MAX_VALUE)
        {
            return new Interval(a, b);
        }
        if (cache[a] is null)
        {
            cache[a] = new Interval(a, a);
        }
        return cache[a];
    }

    /**
     * @uml
     * return number of elements between a and b inclusively. x..x is length 1.
     * if b &lt; a, then length is 0.  9..10 has length 2.
     * @pure
     * @safe
     */
    public int length() @safe pure
    {
        if (b < a)
            return 0;
        return b - a + 1;
    }

    /**
     * @uml
     * @pure
     * @safe
     */
    public bool equals(Object o) @safe pure
    {
        Interval other = cast(Interval) o;
        return this.a == other.a && this.b == other.b;
    }

    unittest
    {
        auto a = new Interval(1, 2);
        auto b = new Interval(1, 2);
        assert(a.equals(b), a.toString);
    }

    /**
     * @uml
     * @pure
     * @safe
     */
    public int hashCode() @safe pure
    {
        int hash = 23;
        hash = hash * 31 + a;
        hash = hash * 31 + b;
        return hash;
    }

    /**
     * @uml
     * Does this start completely before other? Disjoint
     * @pure
     * @safe
     */
    public bool startsBeforeDisjoint(Interval other) @safe pure
    {
        return this.a < other.a && this.b < other.a;
    }

    /**
     * @uml
     * Does this start at or before other? Nondisjoint
     * @pure
     * @safe
     */
    public bool startsBeforeNonDisjoint(Interval other) @safe pure
    {
        return this.a <= other.a && this.b >= other.a;
    }

    /**
     * @uml
     * Does this.a start after other.b? May or may not be disjoint
     * @pure
     * @safe
     */
    public bool startsAfter(Interval other) @safe pure
    {
        return this.a > other.a;
    }

    /**
     * @uml
     * Does this start completely after other? Disjoint
     * @pure
     * @safe
     */
    public bool startsAfterDisjoint(Interval other) @safe pure
    {
        return this.a > other.b;
    }

    /**
     * @uml
     * Does this start after other? NonDisjoint
     * @pure
     * @safe
     */
    public bool startsAfterNonDisjoint(Interval other) @safe pure
    {
        return this.a > other.a && this.a <= other.b; // this.b>=other.b implied
    }

    /**
     * @uml
     * Are both ranges disjoint? I.e., no overlap?
     * @pure
     * @safe
     */
    public bool disjoint(Interval other) @safe pure
    {
        return startsBeforeDisjoint(other) || startsAfterDisjoint(other);
    }

    /**
     * @uml
     * Are two intervals adjacent such as 0..41 and 42..42?
     * @pure
     * @safe
     */
    public bool adjacent(Interval other) @safe pure
    {
        return this.a == other.b + 1 || this.b == other.a - 1;
    }

    unittest
    {
        auto a = new Interval(1, 2);
        auto b = new Interval(3, 10);
        assert(b.adjacent(a));
        assert(!b.adjacent(new Interval(1, 6)));
        assert(!b.adjacent(new Interval(10, 16)));
    }

    /**
     * @uml
     * @pure
     * @safe
     */
    public bool properlyContains(Interval other) @safe pure
    {
        return other.a >= this.a && other.b <= this.b;
    }

    /**
     * @uml
     * Return the interval computed from combining this and other
     * @safe
     */
    public Interval unionInterval(Interval other) @safe
    {
        return Interval.of(min(a, other.a), max(b, other.b));
    }

    unittest
    {
        auto a = new Interval(1, 2);
        auto b = new Interval(3, 10);
        auto c = new Interval(1, 10);
        auto d = new Interval(7, 10);
        assert(b.unionInterval(a).equals(c));
        assert(c.unionInterval(a).equals(c));
        assert(a.unionInterval(c).equals(c));
        assert(c.unionInterval(d).equals(c));
        assert(d.unionInterval(c).equals(c));
        assert(!d.unionInterval(c).equals(d));
    }

    /**
     * @uml
     * Return the interval in common between this and o
     * @safe
     */
    public Interval intersection(Interval other) @safe
    {
        return Interval.of(max(a, other.a), min(b, other.b));
    }

    /**
     * @uml
     * Return the interval with elements from this not in other;
     * other must not be totally enclosed (properly contained)
     * within this, which would result in two disjoint intervals
     * instead of the single one returned by this method.
     * @safe
     */
    public Interval differenceNotProperlyContained(Interval other) @safe
    {
        Interval diff = null;
        // other.a to left of this.a (or same)
        if (other.startsBeforeNonDisjoint(this))
        {
            diff = Interval.of(max(this.a, other.b + 1), this.b);
        }

        // other.a to right of this.a
        else if (other.startsAfterNonDisjoint(this))
        {
            diff = Interval.of(this.a, other.a - 1);
        }
        return diff;
    }

    /**
     * @uml
     * @override
     * @pure
     * @safe
     */
    public override string toString() @safe pure
    {
        return to!string(a) ~ ".." ~ to!string(b);
    }

}
