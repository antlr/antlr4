/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.misc.IntervalSet;

import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.Vocabulary;
import antlr.v4.runtime.misc;
import std.algorithm;
import std.array;
import std.container.rbtree;
import std.conv;
import std.stdio;

/**
 * This class implements the {@link IntSet} backed by a sorted array of
 * non-overlapping intervals. It is particularly efficient for representing
 * large collections of numbers, where the majority of elements appear as part
 * of a sequential range of numbers that are all part of the set. For example,
 * the set { 1, 2, 3, 4, 7, 8 } may be represented as { [1, 4], [7, 8] }.
 *
 * <p>
 * This class is able to represent sets containing any combination of values in
 * the range {@link Integer#MIN_VALUE} to {@link Integer#MAX_VALUE}
 * (inclusive).</p>
 */
class IntervalSet : IntSet
{

    public static IntervalSet COMPLETE_CHAR_SET;

    public static IntervalSet EMPTY_SET;

    private bool readonly;

    /**
     * The list of sorted, disjoint intervals.
     */
    private Interval[] intervals_;

    public this()
    {
        COMPLETE_CHAR_SET = IntervalSet.of(char.min, char.min);
        COMPLETE_CHAR_SET.setReadonly(true);
        EMPTY_SET = new IntervalSet(1);
        EMPTY_SET.clear;
        EMPTY_SET.setReadonly(true);
    }

    public this(Interval[] intervals)
    {
        this.intervals_ = intervals;
    }

    public this(IntervalSet set)
    {
        this();
        addAll(set);
    }

    public this(int[] els...)
    {
        foreach (int e; els)
            add(e);
    }

    /**
     * Create a set with a single element, el.
     */
    public static IntervalSet of(int a)
    {
        IntervalSet s = new IntervalSet();
        s.add(a);
        return s;
    }

    /**
     * Create a set with all ints within range [a..b] (inclusive)
     */
    public static IntervalSet of(int a, int b)
    {
        IntervalSet s = new IntervalSet(1);
        s.clear;
        s.add(a, b);
        return s;
    }

    public void clear()
    {
        assert(!readonly, "can't alter readonly IntervalSet");
        intervals_.length = 0;
    }

    /**
     * Add a single element to the set.  An isolated element is stored
     * as a range el..el.
     * @uml
     * @override
     */
    public override void add(int el)
    {
        assert(!readonly, "can't alter readonly IntervalSet");
        add(el, el);
    }

    /**
     * Add interval; i.e., add all integers from a to b to set.
     * If b&lt;a, do nothing.
     * Keep list in sorted order (by left range value).
     * If overlap, combine ranges.  For example,
     * If this is {1..5, 10..20}, adding 6..7 yields
     * {1..5, 6..7, 10..20}.  Adding 4..8 yields {1..8, 10..20}.
     */
    public void add(int a, int b)
    {
        assert(!readonly, "can't alter readonly IntervalSet");
        add(Interval.of(a, b));
    }

    /**
     * copy on write so we can cache a..a intervals and sets of that
     */
    protected void add(Interval addition)
    {
        assert(!readonly, "can't alter readonly IntervalSet");
        debug (IntervalSet)
            writefln("add %1$s to %2$s", addition, intervals_);
        if (addition.b < addition.a)
        {
            return;
        }
        // find position in list
        // Use iterators as we modify list in place
        foreach (index, ref el; intervals_)
        {
            Interval r = el;
            if (addition.equals(r))
            {
                return;
            }
            if (addition.adjacent(r) || !addition.disjoint(r))
            {
                // next to each other, make a single larger interval
                Interval bigger = addition.unionInterval(r);
                el = bigger;
                // make sure we didn't just create an interval that
                // should be merged with next interval in list
                while (++index < intervals_.length)
                {
                    Interval next = intervals_[index];
                    if (!bigger.adjacent(next) && bigger.disjoint(next))
                    {
                        continue;
                    }
                    // if we bump up against or overlap next, merge
                    intervals_ = intervals_.remove(index);
                    // move backwards to what we just set
                    intervals_[--index] = bigger.unionInterval(next); // set to 3 merged ones
                }
                return;
            }

            if (addition.startsBeforeDisjoint(r))
            {
                // insert before r
                intervals_ = intervals_[0 .. index] ~ addition ~ intervals_[index .. $];
                return;
            }
            // if disjoint and after r, a future iteration will handle it
        }
        // ok, must be after last interval (and disjoint from last interval)
        // just add it
        intervals_ ~= addition;
    }

    public IntervalSet addAll(IntSet set)
    {
        if (set is null)
        {
            return this;
        }
        if (typeid(typeof(set)) == typeid(IntervalSet*))
        {
            IntervalSet other = cast(IntervalSet) set;
            // walk set and add each interval
            auto n = other.intervals_.length;
            for (auto i = 0; i < n; i++)
            {
                Interval I = other.intervals_[i];
                this.add(I.a, I.b);
            }
        }
        else
        {
            foreach (int value; set.toList)
            {
                add(value);
            }
        }
        return this;
    }

    public IntervalSet complement(int minElement, int maxElement)
    {
        return this.complement(IntervalSet.of(minElement, maxElement));
    }

    /**
     * {@inheritDoc}
     */
    public IntervalSet complement(IntSet vocabulary)
    {
        if (vocabulary is null || vocabulary.isNil)
        {
            return null; // nothing in common with null set
        }
        IntervalSet vocabularyIS = new IntervalSet();
        vocabularyIS.addAll(vocabulary);
        return vocabularyIS.subtract(this);
    }

    public IntervalSet complement(IntervalSet vocabulary)
    {
        if (vocabulary is null || vocabulary.isNil)
        {
            return null; // nothing in common with null set
        }
        IntervalSet vocabularyIS = vocabulary;
        return vocabularyIS.subtract(this);
    }

    public IntervalSet subtract(IntSet a)
    {
        if (!a)
        {
            return new IntervalSet(this);
        }
        if (cast(IntervalSet) a)
        {
            return subtract(this, cast(IntervalSet) a);
        }

        IntervalSet other = new IntervalSet;
        other.addAll(a);
        return subtract(this, other);
    }

    public IntervalSet or(IntSet a)
    {
        IntervalSet o = new IntervalSet();
        o.addAll(this);
        o.addAll(a);
        return o;
    }

    public IntervalSet and(IntSet other)
    {
        if (other is null)
        { //|| !(other instanceof IntervalSet) ) {
            return null; // nothing in common with null set
        }

        auto myIntervals = this.intervals_;
        auto theirIntervals = (cast(IntervalSet) other).intervals_;
        IntervalSet intersection;
        auto mySize = myIntervals.length;
        auto theirSize = theirIntervals.length;
        int i = 0;
        int j = 0;
        // iterate down both interval lists looking for nondisjoint intervals
        while (i < mySize && j < theirSize)
        {
            Interval mine = myIntervals[i];
            Interval theirs = theirIntervals[j];
            //System.out.println("mine="+mine+" and theirs="+theirs);
            if (mine.startsBeforeDisjoint(theirs))
            {
                // move this iterator looking for interval that might overlap
                i++;
            }
            else if (theirs.startsBeforeDisjoint(mine))
            {
                // move other iterator looking for interval that might overlap
                j++;
            }
            else if (mine.properlyContains(theirs))
            {
                // overlap, add intersection, get next theirs
                if (intersection is null)
                {
                    intersection = new IntervalSet();
                }
                intersection.add(mine.intersection(theirs));
                j++;
            }
            else if (theirs.properlyContains(mine))
            {
                // overlap, add intersection, get next mine
                if (intersection is null)
                {
                    intersection = new IntervalSet();
                }
                intersection.add(mine.intersection(theirs));
                i++;
            }
            else if (!mine.disjoint(theirs))
            {
                // overlap, add intersection
                if (intersection is null)
                {
                    intersection = new IntervalSet();
                }
                intersection.add(mine.intersection(theirs));
                // Move the iterator of lower range [a..b], but not
                // the upper range as it may contain elements that will collide
                // with the next iterator. So, if mine=[0..115] and
                // theirs=[115..200], then intersection is 115 and move mine
                // but not theirs as theirs may collide with the next range
                // in thisIter.
                // move both iterators to next ranges
                if (mine.startsAfterNonDisjoint(theirs))
                {
                    j++;
                }
                else if (theirs.startsAfterNonDisjoint(mine))
                {
                    i++;
                }
            }
        }
        if (intersection is null)
        {
            return new IntervalSet();
        }
        return intersection;
    }

    public bool contains(int el)
    {
        foreach (I; intervals_)
        {
            int a = I.a;
            int b = I.b;
            if (el < a)
            {
                break; // list is sorted and el is before this interval; not here
            }
            if (el >= a && el <= b)
            {
                return true; // found in this interval
            }
        }
        return false;
    }

    public bool isNil()
    {
        return intervals_ is null || intervals_.length == 0;
    }

    public int getSingleElement()
    {
        if (intervals_ !is null && intervals_.length == 1)
        {
            Interval I = intervals_[0];
            if (I.a == I.b)
            {
                return I.a;
            }
        }
        return TokenConstantDefinition.INVALID_TYPE;
    }

    /**
     * Returns the maximum value contained in the set.
     *
     *  @return the maximum value contained in the set. If the set is empty, this
     *  method returns {@link Token#INVALID_TYPE}.
     */
    public int getMaxElement()
    {
        if (isNil)
        {
            return TokenConstantDefinition.INVALID_TYPE;
        }
        Interval last = intervals_[$ - 1];
        return last.b;
    }

    /**
     * Returns the minimum value contained in the set.
     *
     *  @return the minimum value contained in the set. If the set is empty, this
     *  method returns {@link Token#INVALID_TYPE}.
     */
    public int getMinElement()
    {
        if (isNil)
        {
            return TokenConstantDefinition.INVALID_TYPE;
        }
        return intervals_[0].a;
    }

    /**
     * combine all sets in the array returned the or'd value
     */
    public static IntervalSet or(IntervalSet[] sets)
    {
        IntervalSet r = new IntervalSet(1);
        r.clear;
        foreach (IntervalSet s; sets)
        {
            r.addAll(s);
        }
        return r;
    }

    /**
     * Compute the set difference between two interval sets. The specific
     * operation is {@code left - right}. If either of the input sets is
     * {@code null}, it is treated as though it was an empty set.
     */
    public IntervalSet subtract(IntervalSet left, IntervalSet right)
    {
        if (left is null || left.size == 0)
        {
            return new IntervalSet();
        }
        IntervalSet result = new IntervalSet(left);
        if (right is null || right.isNil)
        {
            // right set has no elements; just return the copy of the current set
            return result;
        }

        int resultI = 0;
        int rightI = 0;
        while (resultI < result.intervals_.length && rightI < right.intervals_.length)
        {
            Interval resultInterval = result.intervals_[resultI];
            Interval rightInterval = right.intervals_[rightI];

            // operation: (resultInterval - rightInterval) and update indexes

            if (rightInterval.b < resultInterval.a)
            {
                rightI++;
                continue;
            }

            if (rightInterval.a > resultInterval.b)
            {
                resultI++;
                continue;
            }

            Interval beforeCurrent = null;
            Interval afterCurrent = null;
            if (rightInterval.a > resultInterval.a)
            {
                beforeCurrent = new Interval(resultInterval.a, rightInterval.a - 1);
            }

            if (rightInterval.b < resultInterval.b)
            {
                afterCurrent = new Interval(rightInterval.b + 1, resultInterval.b);
            }

            if (beforeCurrent !is null)
            {
                if (afterCurrent !is null)
                {
                    // split the current interval into two
                    result.intervals_[resultI] = beforeCurrent;
                    result.intervals_ ~= afterCurrent;
                    resultI++;
                    rightI++;
                    continue;
                }
                else
                {
                    // replace the current interval
                    result.intervals_[resultI] = beforeCurrent;
                    resultI++;
                    continue;
                }
            }
            else
            {
                if (afterCurrent !is null)
                {
                    // replace the current interval
                    result.intervals_[resultI] = afterCurrent;
                    rightI++;
                    continue;
                }
                else
                {
                    // remove the current interval (thus no need to increment resultI)
                    //result.intervals.remove(resultI);
                    result.intervals_ = result.intervals_[0 .. resultI].dup
                        ~ result.intervals_[resultI + 1 .. $].dup;
                    continue;
                }
            }
        }
        // If rightI reached right.intervals.size(), no more intervals to subtract from result.
        // If resultI reached result.intervals.size(), we would be subtracting from an empty set.
        // Either way, we are done.
        return result;
    }

    public string elementName(Vocabulary vocabulary, int a)
    {
        if (a == TokenConstantDefinition.EOF)
        {
            return "<EOF>";
        }
        else if (a == TokenConstantDefinition.EPSILON)
        {
            return "<EPSILON>";
        }
        else
        {
            return vocabulary.getDisplayName(a);
        }
    }

    public int size()
    {
        int n = 0;
        auto numIntervals = intervals_.length;
        if (numIntervals == 1)
        {
            Interval firstInterval = intervals_[0];
            return firstInterval.b - firstInterval.a + 1;
        }
        for (auto i = 0; i < numIntervals; i++)
        {
            Interval I = intervals_[i];
            n += (I.b - I.a + 1);
        }
        return n;
    }

    public IntegerList toIntegerList()
    {
        IntegerList values = new IntegerList();
        auto n = intervals_.length;
        for (auto i = 0; i < n; i++)
        {
            Interval I = intervals_[i];
            int a = I.a;
            int b = I.b;
            for (int v = a; v <= b; v++)
            {
                values.add(v);
            }
        }
        return values;
    }

    public int[] toList()
    {
        int[] values;
        auto n = intervals_.length;
        for (auto i = 0; i < n; i++)
        {
            Interval I = intervals_[i];
            int a = I.a;
            int b = I.b;
            for (int v = a; v <= b; v++)
            {
                values ~= v;
            }
        }
        return values;
    }

    public RedBlackTree!int toSet()
    {
        auto s = redBlackTree!int();
        foreach (Interval I; intervals_)
        {
            int a = I.a;
            int b = I.b;
            for (int v = a; v <= b; v++)
            {
                s.insert(v);
            }
        }
        return s;
    }

    /**
     * Get the ith element of ordered set.  Used only by RandomPhrase so
     * don't bother to implement if you're not doing that for a new
     * ANTLR code gen target.
     * @uml
     * @safe
     * @pure
     */
    public int get(int i) @safe pure
    {
        auto n = intervals_.length;
        ulong index = 0;
        for (auto j = 0; j < n; j++)
        {
            Interval I = intervals_[j];
            int a = I.a;
            int b = I.b;
            for (int v = a; v <= b; v++)
            {
                if (to!int(index) == i)
                {
                    return v;
                }
                index++;
            }
        }
        return -1;
    }

    public int[] toArray()
    {
        return toIntegerList().toArray;
    }

    public void remove(int el)
    {
        assert(!readonly, "can't alter readonly IntervalSet");
        auto n = intervals_.length;
        for (auto i = 0; i < n; i++)
        {
            Interval I = intervals_[i];
            int a = I.a;
            int b = I.b;
            if (el < a)
            {
                break; // list is sorted and el is before this interval; not here
            }
            // if whole interval x..x, remove i
            if (el == a && el == b)
            {
                intervals_ = intervals_[0 .. i] ~ intervals_[i + 1 .. $];
                break;
            }
            // if on left edge x..b, adjust left
            if (el == a)
            {
                I.a++;
                break;
            }
            // if on right edge a..x, adjust right
            if (el == b)
            {
                I.b--;
                break;
            }
            // if in middle a..x..b, split interval
            if (el > a && el < b)
            { // found in this interval
                int oldb = I.b;
                I.b = el - 1; // [a..x-1]
                add(el + 1, oldb); // add [x+1..b]
            }
        }
    }

    /**
     * @uml
     * @safe
     * @pure
     */
    public bool isReadonly() @safe pure
    {
        return readonly;
    }

    /**
     * @uml
     * @safe
     * @pure
     */
    public void setReadonly(bool readonly) @safe pure
    {
        assert(!this.readonly, "can't alter readonly IntervalSet");
        this.readonly = readonly;
    }

    /**
     * @uml
     * @override
     */
    public override bool opEquals(Object obj)
    {
        IntervalSet other = cast(IntervalSet) obj;
        return intervals_ == other.intervals_;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return toString(false);
    }

    public string toString(bool elemAreChar)
    {
        auto buf = appender!string;
        if (intervals_ is null || intervals_.length == 0)
        {
            return "{}";
        }
        if (this.size() > 1)
        {
            buf.put("{");
        }
        foreach (index, I; this.intervals_)
        {
            int a = I.a;
            int b = I.b;
            if (a == b)
            {
                if (a == TokenConstantDefinition.EOF)
                    buf.put("<EOF>");
                else if (elemAreChar)
                    buf.put("'" ~ to!string(a) ~ "'");
                else
                    buf.put(to!string(a));
            }
            else
            {
                if (elemAreChar)
                    buf.put("'" ~ to!string(a) ~ "'..'" ~ to!string(b) ~ "'");
                else
                    buf.put(to!string(a) ~ ".." ~ to!string(b));
            }
            if (index + 1 < intervals_.length)
            {
                buf.put(", "); //  not last element
            }
        }
        if (this.size() > 1)
        {
            buf.put("}");
        }
        return buf.data;

    }

    public string toString(Vocabulary vocabulary)
    {
        auto buf = appender!string;
        if (intervals_ is null || intervals_.length == 0)
        {
            return "{}";
        }
        if (size() > 1)
        {
            buf.put("{");
        }
        foreach (index, I; this.intervals_)
        {
            int a = I.a;
            int b = I.b;
            if (a == b)
            {
                buf.put(elementName(vocabulary, a));
            }
            else
            {
                for (int i = a; i <= b; i++)
                {
                    if (i > a)
                        buf.put(", ");
                    buf.put(elementName(vocabulary, i));
                }
            }
            if (index + 1 < intervals_.length)
            {
                buf.put(", ");
            }
        }
        if (size() > 1)
        {
            buf.put("}");
        }
        return buf.data;

    }

    /**
     * @uml
     * @final
     */
    public final Interval[] intervals()
    {
        return this.intervals_;
    }

}

version (unittest)
{
    import dshould : be, equal, not, should;
    import std.typecons : tuple;
    import unit_threaded;

    class Test
    {

        @Tags("IntervalSet")
        @("Empty")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.should.not.be(null);
            s.intervals.length.should.equal(0);
            s.isNil.should.equal(true);
        }

        @Tags("IntervalSet")
        @("One")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.add(30);
            s.intervals.length.should.equal(1);
            s.isNil.should.equal(false);
            s.contains(30).should.equal(true);
            s.contains(29).should.equal(false);
            s.contains(31).should.equal(false);
        }

        @Tags("IntervalSet")
        @("Two")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.add(30);
            s.add(40);
            s.intervals.length.should.equal(2);
            s.isNil.should.equal(false);
            s.contains(30).should.equal(true);
            s.contains(40).should.equal(true);
            s.contains(35).should.equal(false);
        }

        @Tags("IntervalSet")
        @("Range")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.add(30, 41);
            s.intervals.length.should.equal(1);
            s.isNil.should.equal(false);
            s.contains(30).should.equal(true);
            s.contains(40).should.equal(true);
            s.contains(35).should.equal(true);
        }

        @Tags("IntervalSet")
        @("Distinct1")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.add(30, 32);
            s.add(40, 42);
            s.intervals.length.should.equal(2);
            s.isNil.should.equal(false);
            s.contains(30).should.equal(true);
            s.contains(40).should.equal(true);
            s.contains(35).should.equal(false);
        }

        @Tags("IntervalSet")
        @("Distinct2")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.add(40, 42);
            s.add(30, 32);
            s.intervals.length.should.equal(2);
            s.isNil.should.equal(false);
            s.contains(30).should.equal(true);
            s.contains(40).should.equal(true);
            s.contains(35).should.equal(false);
        }

        @Tags("IntervalSet")
        @("Contiguous1")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.add(30, 36);
            s.add(36, 41);
            s.add(41, 44);
            s.intervals.length.should.equal(1);
            s.isNil.should.equal(false);
            s.contains(30).should.equal(true);
            s.contains(40).should.equal(true);
            s.contains(43).should.equal(true);
            s.contains(44).should.equal(true);
            s.contains(45).should.equal(false);
        }

        @Tags("IntervalSet")
        @("Contiguous2")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.add(41, 44);
            s.add(36, 41);
            s.add(30, 36);
            s.intervals.length.should.equal(1);
            s.isNil.should.equal(false);
            s.contains(30).should.equal(true);
            s.contains(40).should.equal(true);
            s.contains(43).should.equal(true);
            s.contains(44).should.equal(true);
            s.contains(45).should.equal(false);
        }

        @Tags("IntervalSet")
        @("Overlapping1")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.add(30, 40);
            s.add(35, 44);
            s.add(31, 36);
            s.intervals.length.should.equal(1);
            s.isNil.should.equal(false);
            s.contains(30).should.equal(true);
            s.contains(40).should.equal(true);
            s.contains(43).should.equal(true);
            s.contains(44).should.equal(true);
            s.contains(45).should.equal(false);
        }

        @Tags("IntervalSet")
        @("Overlapping2")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.add(35, 44);
            s.add(31, 36);
            s.add(30, 40);
            s.intervals.length.should.equal(1);
            s.isNil.should.equal(false);
            s.contains(30).should.equal(true);
            s.contains(40).should.equal(true);
            s.contains(43).should.equal(true);
            s.contains(44).should.equal(true);
            s.contains(45).should.equal(false);
        }

        @Tags("IntervalSet")
        @("Overlapping3")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.add(30, 32);
            s.add(40, 42);
            s.add(140, 144);
            s.add(50, 52);
            s.add(20, 61);
            s.add(50, 52);
            s.intervals.length.should.equal(2);
            s.isNil.should.equal(false);
            s.contains(20).should.equal(true);
            s.contains(40).should.equal(true);
            s.contains(43).should.equal(true);
            s.contains(61).should.equal(true);
            s.contains(4).should.equal(false);
            s.contains(62).should.equal(false);
            s.contains(139).should.equal(false);
            s.contains(140).should.equal(true);
            s.contains(144).should.equal(true);
            s.contains(145).should.equal(false);
            s.toString.should.equal("{20..61, 140..144}");
        }

        @Tags("IntervalSet")
        @("Complement")
        unittest
        {
            IntervalSet s = new IntervalSet;
            s.add(10, 21);
            auto c = s.complement(1, 100);
            c.intervals.length.should.equal(2);
            c.toString.should.equal("{1..9, 22..100}");
            c.contains(1).should.equal(true);
            c.contains(40).should.equal(true);
            c.contains(22).should.equal(true);
            c.contains(10).should.equal(false);
            c.contains(20).should.equal(false);
        }
    }
}
