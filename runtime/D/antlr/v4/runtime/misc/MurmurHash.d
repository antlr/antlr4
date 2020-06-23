/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.misc.MurmurHash;

import std.conv;
import std.stdio;

/**
 * @author Sam Harwell
 * @author Egbert Voigt (D)
 */
class MurmurHash
{

    enum size_t DEFAULT_SEED = 0;

    /**
     * Initialize the hash using the default seed value.
     *
     *  @return the intermediate hash value
     * @uml
     * @safe
     * @nothrow
     */
    public static size_t initialize() @safe nothrow
    {
        auto mh = new MurmurHash;
        return initialize(DEFAULT_SEED);
    }

    /**
     * Initialize the hash using the specified {@code seed}.
     *
     *  @param seed the seed
     *  @return the intermediate hash value
     * @uml
     * @safe
     * @nothrow
     */
    public static size_t initialize(size_t seed) @safe nothrow
    {
        return seed;
    }

    /**
     * Update the intermediate hash value for the next input {@code value}.
     *
     * @param hash the intermediate hash value
     * @param value the value to add to the current hash
     * @return the updated intermediate hash value
     *
     * @uml
     * @safe
     * @nothrow
     */
    public static size_t update(size_t hash, size_t value) @safe nothrow
    {
        immutable size_t c1 = 0xCC9E2D51;
        immutable size_t c2 = 0x1B873593;
        immutable size_t r1 = 15;
        immutable size_t r2 = 13;
        immutable size_t m = 5;
        immutable size_t n = 0xE6546B64;

        size_t k = value;
        k = k * c1;
        k = (k << r1) | (k >>> (32 - r1));
        k = k * c2;

        hash = hash ^ k;
        hash = (hash << r2) | (hash >>> (32 - r2));
        hash = hash * m + n;

        return hash;

    }

    /**
     * Update the intermediate hash value for the next input {@code value}.
     *
     * @param hash the intermediate hash value
     * @param value the value to add to the current hash
     * @return the updated intermediate hash value
     *
     * @uml
     * @nothrow
     */
    public static size_t update(U)(size_t hash, U value) nothrow
    {
        return update(hash, value !is null ? value.toHash : 0);
    }

    /**
     * Apply the final computation steps to the intermediate value {@code hash}
     * to form the final result of the MurmurHash 3 hash function.
     *
     *  @param hash the intermediate hash value
     *  @param numberOfWords the number of integer values added to the hash
     *  @return the final hash result
     * @uml
     * @safe
     * @nothrow
     */
    public static size_t finish(size_t hash, size_t numberOfWords) @safe nothrow
    {
        hash = hash ^ (cast(size_t) numberOfWords * 4);
        hash = hash ^ (hash >>> 16);
        hash = hash * 0x85EBCA6B;
        hash = hash ^ (hash >>> 13);
        hash = hash * 0xC2B2AE35;
        hash = hash ^ (hash >>> 16);
        return hash;
    }

    /**
     * Utility function to compute the hash code of an array using the
     * MurmurHash algorithm.
     *
     * @param T the array element type
     * @param data the array data
     * @param seed the seed for the MurmurHash algorithm
     * @return the hash code of the data
     */
    public static size_t hashCode(T)(T[] data, size_t seed)
    {
        size_t hash = initialize(seed);
        foreach (T value; data)
        {
            hash = update(hash, value);
        }
        hash = finish(hash, data.length);
        return hash;
    }

}

version (unittest)
{
    import dshould : equal, should;
    import unit_threaded;

    @Tags("MurmurHash")
    @("Calculation")
    unittest
    {
        auto hash = MurmurHash.initialize;
        hash.should.equal(0);
        hash = MurmurHash.update(hash, 33);
        static if (size_t.sizeof == 4)
        {
            hash.should.equal(3641358107U);
        }
        else
        {
            hash.should.equal(6137767987951124103UL);
        }
        hash = MurmurHash.finish(hash, 1);
        static if (size_t.sizeof == 4)
        {
            hash.should.equal(2689861987U);
        }
        else
        {
            hash.should.equal(4470425249505779227UL);
        }
    }
}
