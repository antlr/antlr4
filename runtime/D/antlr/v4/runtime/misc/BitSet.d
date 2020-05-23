/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.misc.BitSet;

import std.algorithm;
import std.bitmanip;
import std.conv;
import std.format : format;

/**
 * This struct implements a vector of bits that grows as needed. Each component
 * of the bit set has a bool value. The bits of a BitSet are indexed by nonnegative integers.
 * Individual indexed bits can be examined, set, or cleared. One BitSet may be used to modify
 * the contents of another BitSet through logical AND, logical inclusive OR,
 * and logical exclusive OR operations.
 *
 * By default, all bits in the set initially have the value false.
 */
struct BitSet
{

    private BitArray bitSet;

    public this(BitArray bitArray)
    {
        this.bitSet = bitArray;
    }

    public this(size_t initialSize)
    {
        this.bitSet.length = initialSize;
    }

    public size_t length()
    {
        return bitSet.length;
    }

    public auto cardinality()
    {
        int res;
        bitSet.each!(v => res += to!int(v));
        return res;
    }

    public int nextSetBit(int fromIndex)
    {
        foreach (i, el; bitSet)
        {
            if (i > fromIndex && el == true)
                return to!int(i);
        }
        return -1;
    }

    public void set(int bitIndex, bool value)
    {
        if (bitSet.length <= bitIndex)
            bitSet.length = bitIndex + 16; // dynamic array need more space
        bitSet[bitIndex] = value;
    }

    /**
     * @uml
     * @nothrow
     */
    public bool get(int bitIndex) nothrow
    in
    {
        assert(bitSet.length > bitIndex);
    }
    do
    {
        return bitSet[bitIndex];
    }

    public bool isEmpty()
    {
        return cardinality == 0;
    }

    public string toString()
    {
        if (bitSet.length)
        {
            string res;
            foreach (j, b; bitSet)
            {
                if (b == true)
                {
                    res ~= to!string(j) ~ ", ";
                }
            }
            return format!"{%s}"(res[0 .. $ - 2]);
        }
        else
            return "{}";
    }

    /**
     * @uml
     * @trusted
     */
    public size_t toHash() @trusted
    {
        size_t hash;
        foreach (el; bitSet)
            hash = (hash * 9) + el;
        return hash;
    }

    /**
     * @uml
     * @const
     * @pure
     * @nothrow
     */
    public bool opEquals(ref const BitSet bitSet) const pure nothrow
    {
        return this.bitSet == bitSet.bitSet;
    }

    public void clear()
    {
        bitSet.length(0);
    }

    public BitSet or(BitSet bits)
    {
        BitSet result;
        auto maxLenght = max(this.bitSet.length, bits.bitSet.length);
        if (this.bitSet.length < maxLenght)
            this.bitSet.length = maxLenght;
        else if (bits.values.length < maxLenght)
            bits.values.length = maxLenght;
        result.bitSet = this.bitSet | bits.bitSet;
        return result;
    }

    public string toIndexString()
    {
        import std.conv;
        import std.array : join;

        string[] res;
        int index;
        foreach (i, el; bitSet)
        {
            if (el)
                res ~= to!string(i);
        }
        return "{" ~ join(res, ",") ~ "}";
    }

    public BitArray values()
    {
        return bitSet;
    }

    public void dup(BitSet old)
    {
        this.bitSet = old.bitSet.dup;
    }

}
