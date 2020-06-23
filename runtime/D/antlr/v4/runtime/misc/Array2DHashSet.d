/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.misc.Array2DHashSet;

import std.array;
import std.variant;
import std.stdio;
import std.conv;
import std.math : floor;
import std.container.array;
import std.algorithm.mutation : remove;
import std.algorithm.searching : canFind;
import antlr.v4.runtime.misc;

/**
 * Set implementation with closed hashing (open addressing).
 */
class Array2DHashSet(T)
{

    enum int INITAL_CAPACITY = 16;

    enum int INITAL_BUCKET_CAPACITY = 8;

    enum double LOAD_FACTOR = 0.75;

    protected size_t function(Object o) @trusted nothrow hashOfFp;

    protected bool function(Object a, Object b) opEqualsFp;

    protected T[][] buckets;

    /**
     * @uml
     * How many elements in set
     */
    protected int n = 0;

    /**
     * @uml
     * when to expand
     */
    protected int threshold = cast(int)(INITAL_CAPACITY * LOAD_FACTOR);

    /**
     * @uml
     * jump by 4 primes each expand or whatever
     */
    protected int currentPrime = 1;

    protected int initialBucketCapacity = INITAL_BUCKET_CAPACITY;

    public this()
    {
        this(null, null, INITAL_CAPACITY, INITAL_BUCKET_CAPACITY);
    }

    public this(size_t function(Object o) @trusted nothrow hashOfFp,
            bool function(Object a, Object b) opEqualsFp)
    {
        this(hashOfFp, opEqualsFp, INITAL_CAPACITY, INITAL_BUCKET_CAPACITY);
    }

    public this(size_t function(Object o) @trusted nothrow hashOfFp, bool function(Object a,
            Object b) opEqualsFp, int initialCapacity, int initialBucketCapacity)
    {
        if (hashOfFp is null || opEqualsFp is null)
        {
            this.hashOfFp = &ObjectEqualityComparator.hashOf;
            this.opEqualsFp = &ObjectEqualityComparator.opEquals;
        }
        else
        {
            this.hashOfFp = hashOfFp;
            this.opEqualsFp = opEqualsFp;
        }
        this.buckets = createBuckets(initialCapacity);
        this.initialBucketCapacity = initialBucketCapacity;
    }

    /**
     * @uml
     * Add {@code o} to set if not there; return existing value if already
     * there. This method performs the same operation as {@link #add} aside from
     * the return value.
     * @final
     */
    public final T getOrAdd(T o)
    {
        if (n > threshold)
        {
            expand();
        }
        return getOrAddImpl(o);
    }

    protected T getOrAddImpl(T o)
    {
        auto b = getBucket(o);
        T[] bucket = buckets[b];
        // NEW BUCKET
        if (bucket is null)
        {
            bucket = createBucket(initialBucketCapacity);
            bucket[0] = o;
            buckets[b] = bucket;
            n++;
            return o;
        }

        // LOOK FOR IT IN BUCKET
        for (int i = 0; i < bucket.length; i++)
        {
            auto existing = bucket[i];
            if (!existing)
            { // empty slot; not there, add.
                bucket[i] = o;
                n++;
                return o;
            }
            if (opEqualsFp(existing, o))
            {
                return existing; // found existing, quit
            }
        }

        // FULL BUCKET, expand and add to end
        auto oldLength = bucket.length;
        bucket.length = bucket.length * 2;
        buckets[b] = bucket;
        bucket[oldLength] = o; // add to end
        n++;
        return o;
    }

    public T get(T o)
    {
        if (o is null)
            return o;
        T nullElement;
        auto b = getBucket(o);
        T[] bucket = buckets[b];
        if (bucket is null)
            return nullElement; // no bucket
        foreach (e; bucket)
        {
            if (e is null)
                return nullElement; // empty slot; not there
            if (opEqualsFp(e, o))
                return e;
        }
        return nullElement;
    }

    /**
     * @uml
     * @final
     */
    protected final size_t getBucket(T o)
    {
        return hashOfFp(o) & (buckets.length - 1); // assumes length is power of 2
    }

    /**
     * @uml
     * @override
     * @safe
     * @nothrow
     */
    public override size_t toHash() @safe nothrow
    {
        size_t hash = MurmurHash.initialize();
        foreach (bucket; buckets)
        {
            if (bucket is null)
                continue;
            foreach (o; bucket)
            {
                if (o is null)
                    break;
                hash = MurmurHash.update(hash, hashOfFp(o));
            }
        }
        hash = MurmurHash.finish(hash, size());
        return hash;
    }

    /**
     * @uml
     * @override
     */
    public override bool opEquals(Object o)
    {
        if (o is this)
            return true;
        if (!cast(Array2DHashSet) o)
            return false;
        Array2DHashSet!T other = cast(Array2DHashSet!T) o;
        if (other.size() != size())
            return false;
        bool same = this.containsAll(other);
        return same;
    }

    protected void expand()
    {
        auto old = buckets;
        currentPrime += 4;
        int newCapacity = to!int(buckets.length) * 2;
        auto newTable = createBuckets(newCapacity);
        int[] newBucketLengths = new int[newTable.length];
        buckets = newTable;
        threshold = cast(int)(newCapacity * LOAD_FACTOR);
        //      System.out.println("new size="+newCapacity+", thres="+threshold);
        // rehash all existing entries
        int oldSize = size();
        foreach (bucket; old)
        {
            if (bucket is null)
            {
                continue;
            }
            foreach (o; bucket)
            {
                if (o is null)
                {
                    break;
                }
                auto b = getBucket(o);
                int bucketLength = newBucketLengths[b];
                T[] newBucket;
                if (bucketLength == 0)
                {
                    // new bucket
                    newBucket = createBucket(initialBucketCapacity);
                    newTable[b] = newBucket;
                }
                else
                {
                    newBucket = newTable[b];
                    if (bucketLength == newBucket.length)
                    {
                        // expand
                        newBucket.length = newBucket.length * 2;
                        newTable[b] = newBucket;
                    }
                }

                newBucket[bucketLength] = o;
                newBucketLengths[b]++;
            }
        }
        assert(n == oldSize);
    }

    /**
     * @uml
     * @final
     */
    public final bool add(T t)
    {
        T existing = getOrAdd(t);
        return existing == t;
    }

    /**
     * @uml
     * @final
     */
    public final int size()
    {
        return n;
    }

    /**
     * @uml
     * @final
     */
    public final bool isEmpty()
    {
        return n == 0;
    }

    /**
     * @uml
     * @final
     */
    public final bool contains(T o)
    {
        return containsFast(o);
    }

    public bool containsFast(T obj)
    {
        if (obj is null)
        {
            return false;
        }
        return get(obj) !is null;
    }

    public T[] toArray()
    {
        T[] a;
        foreach (bucket; buckets)
        {
            if (bucket is null)
            {
                continue;
            }
            foreach (o; bucket)
            {
                if (o is null)
                {
                    break;
                }
                a ~= o;
            }
        }
        return a;
    }

    public U[] toArray(U)(U[] a)
    {
        if (a.length < size())
        {
            a = Arrays.copyOf(a, size());
        }
        int i = 0;
        foreach (T[] bucket; buckets)
        {
            if (bucket == null)
            {
                continue;
            }
            foreach (T o; bucket)
            {
                if (o is null)
                {
                    break;
                }
                //@SuppressWarnings("unchecked") // array store will check this
                U targetElement = cast(U) o;
                a[i++] = targetElement;
            }
        }
        return a;
    }

    /**
     * @uml
     * @final
     */
    public final bool remove(T o)
    {
        return removeFast(o);
    }

    public bool removeFast(T obj)
    {
        if (obj is null)
        {
            return false;
        }
        size_t b = getBucket(obj);
        auto bucket = buckets[b];
        if (bucket is null)
        {
            // no bucket
            return false;
        }
        for (int i = 0; i < bucket.length; i++)
        {
            auto e = bucket[i];
            if (e is null)
            {
                // empty slot; not there
                return false;
            }
            if (opEqualsFp(e, obj))
            { // found it
                // shift all elements to the right down one
                bucket.remove(i);
                bucket[$ - 1] = null;
                n--;
                return true;
            }
        }
        return false;
    }

    public bool containsAll(Object collection)
    {
        if (cast(Array2DHashSet!T) collection)
        {
            Array2DHashSet!T s = to!(Array2DHashSet!T)(collection);
            foreach (bucket; s.buckets)
            {
                if (bucket is null)
                    continue;
                foreach (o; bucket)
                {
                    if (o is null)
                        break;
                    if (!this.containsFast(o))
                        return false;
                }
            }
        }
        else
        {
            foreach (o; collection.tupleof)
            {
                if (!this.containsFast(o))
                    return false;
            }
            return false;
        }
        return true;
    }

    public bool addAll(T[] c)
    {
        bool changed = false;
        foreach (o; c)
        {
            T existing = getOrAdd(o);
            if (existing != o)
                changed = true;
        }
        return changed;
    }

    public bool retainAll(T[] c)
    {
        int newsize = 0;
        foreach (bucket; buckets)
        {
            if (bucket is null)
            {
                continue;
            }
            int i;
            int j;
            for (i = 0, j = 0; i < bucket.length; i++)
            {
                if (bucket[i] is null)
                {
                    break;
                }
                auto bg = bucket[i];
                if (!c.canFind(bg))
                {
                    // removed
                    continue;
                }
                // keep
                if (i != j)
                {
                    bucket[j] = bucket[i];
                }
                j++;
                newsize++;
            }
            newsize += j;
            while (j < i)
            {
                bucket[j] = null;
                j++;
            }
        }

        bool changed = newsize != n;
        n = newsize;
        return changed;

    }

    public bool removeAll(T[] c)
    {
        bool changed = false;
        foreach (o; c)
        {
            changed |= removeFast(o);
        }
        return changed;
    }

    public void clear()
    {
        buckets = createBuckets(INITAL_CAPACITY);
        n = 0;
        threshold = to!int(floor(INITAL_CAPACITY * LOAD_FACTOR));
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        if (size == 0)
            return "{}";
        auto buf = appender!string;
        buf.put('{');
        bool first = true;
        foreach (bucket; buckets)
        {
            if (bucket is null)
                continue;
            foreach (o; bucket)
            {
                if (o is null)
                    break;
                if (first)
                    first = false;
                else
                    buf.put(", ");
                buf.put(to!string(o));
            }
        }
        buf.put('}');
        return buf.data;
    }

    public string toTableString()
    {
        auto buf = appender!string;
        foreach (bucket; buckets)
        {
            if (bucket is null)
            {
                buf.put("null\n");
                continue;
            }
            buf.put('[');
            bool first = true;
            foreach (o; bucket)
            {
                if (first)
                    first = false;
                else
                    buf.put(" ");
                if (o is null)
                    buf.put("_");
                else
                    buf.put(to!string(o));
            }
            buf.put("]\n");
        }
        return buf.data;
    }

    public T[][] createBuckets(int capacity)
    {
        T[][] obj;
        obj.length = capacity;
        debug
        {
            writefln("T[][] obj -> %1$s, length -> %2$s", obj, obj.length);
        }
        return obj;
    }

    public T[] createBucket(int capacity)
    {
        T[] obj;
        obj.length = capacity;
        return obj;
    }

}
