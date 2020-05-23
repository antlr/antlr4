/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.misc.DoubleKeyMap;

import std.typecons;

/**
 * Sometimes we need to map a key to a value but key is two pieces of data.
 * This nested hash table saves creating a single key each time we access
 * map; avoids mem creation.
 */
class DoubleKeyMap(K1, K2, V)
{

    public V[K1][K2] data;

    public V put(K1 k1, K2 k2, V v)
    {
        data[k1][k2] = v;
        return v;
    }

    public Nullable!V get(K1 k1, K2 k2)
    {
        Nullable!V v;
        if (k1 !in data || k2 !in data[k1])
        {
            return v; // null
        }
        v = data[k1][k2];
        return v;
    }

    public V[K2] get(K1 k1)
    {
        if (k1 !in data)
        {
            V[K2] v;
            return v;
        }
        return data[k1];
    }

    /**
     * Get all values associated with a primary key.
     */
    public V[] values(K1 k1)
    {
        V[] va;
        if (k1 !in data)
        {
            return va; // []
        }
        V[K2] data2 = data[k1];
        return data2.values;
    }

    /**
     * get all primary keys
     */
    public K1[] keySet()
    {
        return data.keys;
    }

    /**
     * Get all secondary keys associated with a primary key.
     */
    public K2[] keySet(K1 k1)
    {
        if (k1 !in data)
        {
            K2[] v;
            return v;
        }
        V[K2] data2 = data[k1];
        return data2.keys;
    }

}

version (unittest)
{
    import dshould : be, equal, not, should;
    import unit_threaded;

    class Test
    {

        @Tags("DoubleKeyMap")
        @("construction DoubleKeyMap")
        unittest
        {
            auto t1 = new DoubleKeyMap!(int, int, int);
            t1.put(7, 1, 12);
            t1.put(7, 1, 13);
            auto x = t1.get(7, 1);
            x.get.should.equal(13);
            x = t1.get(7, 2);
            x.isNull.should.equal(true);
        }

        @Tags("DoubleKeyMap")
        @("comparing DoubleKeyMaps")
        unittest
        {
            auto t1 = new DoubleKeyMap!(int, int, int);
            t1.put(7, 1, 13);
            auto y = t1.get(7);
            int[int] c;
            c[1] = 13;
            c.should.equal(y);
            y = t1.get(6);
            y.length.should.equal(0);
            t1.put(7, 4, 71);
            c[4] = 71;
            y = t1.get(7);
            c.should.equal(y);

            auto v1 = t1.values(7);
            v1.should.equal([71, 13]);
            v1 = t1.values(0);
            v1.should.equal([]);

            auto kx = t1.keySet;
            auto kk = [7];
            kk.should.equal(kx);

            auto tx = t1.keySet(8);
            tx.should.equal([]);
            tx = t1.keySet(7);
            tx.should.equal([4, 1]);
        }

    }

}
