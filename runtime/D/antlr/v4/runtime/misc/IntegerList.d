/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.misc.IntegerList;

import antlr.v4.runtime.IllegalArgumentException;
import std.conv;
import std.format;
import std.range;
import std.typecons;

/**
 * A buffert list of interger values
 */
class IntegerList
{

    /**
     * @uml
     * @read
     */
    public int[] data_;

    public this()
    {
    }

    public this(int capacity)
    {
        if (capacity < 0)
        {
            throw new IllegalArgumentException("Capacity can't be a negativ value!");
        }
        if (capacity)
        {
            data_.reserve(capacity);
        }
    }

    public this(IntegerList list)
    {
        data_ = list.data;
        //size_ = list.size;
    }

    public this(int[] list)
    {
        foreach (value; list)
        {
            add(value);
        }
    }

    /**
     * @uml
     * @final
     */
    public final void add(int value)
    {
        data_ ~= value;
    }

    /**
     * @uml
     * @final
     */
    public final void addAll(int[] array)
    {
        foreach (value; array)
        {
            add(value);
        }
    }

    public void addAll(IntegerList list)
    {
        foreach (value; list.data)
        {
            add(value);
        }
    }

    /**
     * @uml
     * @final
     */
    public final int get(int index)
    in
    {
        assert(index >= 0 || index < data_.length);
    }
    do
    {
        return data_[index];
    }

    /**
     * @uml
     * @final
     */
    public final bool contains(int value)
    {
        import std.algorithm.searching : canFind;

        return data_.canFind(value);
    }

    /**
     * @uml
     * @final
     */
    public final int set(int index, int value)
    in
    {
        assert(index >= 0 || index < data_.length);
    }
    do
    {
        int previous = data_[index];
        data_[index] = value;
        return previous;
    }

    /**
     * @uml
     * @final
     */
    public final int removeAt(int index)
    {
        int value = get(index);
        import std.algorithm.mutation : remove;

        data_ = remove(data_, index);
        return value;
    }

    /**
     * @uml
     * @final
     */
    public final void removeRange(int fromIndex, int toIndex)
    in
    {
        assert(fromIndex >= 0 || toIndex >= 0 || fromIndex <= data_.length || toIndex <= data_.length, "IndexOutOfBoundsException");
        assert(fromIndex <= toIndex, "IllegalArgumentException");
    }
    do
    {
        import std.algorithm.mutation : remove;

        data_ = remove(data_, tuple(fromIndex, toIndex + 1));
    }

    /**
     * @uml
     * @final
     */
    public final bool isEmpty()
    {
        return data_.length == 0;
    }

    /**
     * @uml
     * @final
     */
    public final int size()
    {
        return to!int(data_.length);
    }

    /**
     * @uml
     * @final
     * @property
     */
    public final @property void clear()
    {
        data_.length = 0;
    }

    /**
     * @uml
     * @final
     * @property
     */
    public final @property int[] toArray()
    {
        return data_;
    }

    public void sort()
    {
        import std.algorithm.sorting : sort;

        data_.sort;
    }

    /**
     * Compares the specified object with this list for equality.  Returns
     * {@code true} if and only if the specified object is also an {@link IntegerList},
     * both lists have the same size, and all corresponding pairs of elements in
     * the two lists are equal.  In other words, two lists are defined to be
     * equal if they contain the same elements in the same order.
     * <p>
     * This implementation first checks if the specified object is this
     * list. If so, it returns {@code true}; if not, it checks if the
     * specified object is an {@link IntegerList}. If not, it returns {@code false};
     * if so, it checks the size of both lists. If the lists are not the same size,
     * it returns {@code false}; otherwise it iterates over both lists, comparing
     * corresponding pairs of elements.  If any comparison returns {@code false},
     * this method returns {@code false}.
     *
     *  @param o the object to be compared for equality with this list
     *  @return {@code true} if the specified object is equal to this list
     * @uml
     * @override
     */
    public override bool opEquals(Object o)
    {
        if (o is this)
        {
            return true;
        }

        if (!cast(IntegerList) o)
        {
            return false;
        }

        IntegerList other = cast(IntegerList) o;
        if (data_.length != other.size)
        {
            return false;
        }
        import std.algorithm.comparison : equal;

        return data_.equal(other.data);
    }

    /**
     * Returns the hash code value for this list.
     *
     * <p>This implementation uses exactly the code that is used to define the
     * list hash function in the documentation for the {@link List#hashCode}
     * method.</p>
     *
     *  @return the hash code value for this list
     * @uml
     * @override
     * @safe
     * @nothrow
     */
    public override size_t toHash() @safe nothrow
    {
        int hashCode = 1;
        for (int i = 0; i < data_.length; i++)
        {
            hashCode = 31 * hashCode + data_[i];
        }
        return hashCode;
    }

    /**
     * Returns a string representation of this list.
     * @uml
     * @override
     */
    public override string toString()
    {
        return format("%s", data_);
    }

    public final int[] data()
    {
        return this.data_.dup;
    }

}

version (unittest)
{
    import dshould : be, equal, not, should;
    import dshould.thrown;
    import unit_threaded;

    class Test
    {

        @Tags("IntegerList")
        @("TestEmpty")
        unittest
        {
            auto il = new IntegerList;
            il.should.not.be(null);
            il.isEmpty.should.equal(true);
            il.toArray.should.equal([]);
        }

        @Tags("IntegerList")
        @("Capacity")
        unittest
        {
            auto il = new IntegerList(20);
            il.should.not.be(null);
            il.addAll([3, 17, 55, 12, 1, 7]);
            il.toArray.should.equal([3, 17, 55, 12, 1, 7]);
            il.toString.should.equal("[3, 17, 55, 12, 1, 7]");
            (new IntegerList(-3)).should.throwAn!IllegalArgumentException.where.msg.should.equal(
                    "Capacity can't be a negativ value!");
        }

        @Tags("IntegerList")
        @("SetGet")
        unittest
        {
            auto il = new IntegerList([7, 2, 5, 15, 40]);
            il.contains(3).should.equal(false);
            il.contains(5).should.equal(true);
            il.get(2).should.equal(5);
            il.sort;
            il.toString.should.equal("[2, 5, 7, 15, 40]");
            il.set(3, 2);
            il.toString.should.equal("[2, 5, 7, 2, 40]");
        }

        @Tags("IntegerList")
        @("Hash")
        unittest
        {
            auto il = new IntegerList([7, 5, 15, 40]);
            auto il1 = new IntegerList;
            il1.addAll(il);
            il.toHash.should.equal(1137368);
            il1.toHash.should.equal(1137368);
        }

        @Tags("IntegerList")
        @("Remove")
        unittest
        {
            auto il = new IntegerList([7, 5, 15, 40]);
            il.toString.should.equal("[7, 5, 15, 40]");
            il.removeAt(2);
            il.toString.should.equal("[7, 5, 40]");
            il.removeAt(2);
            il.toString.should.equal("[7, 5]");
            il.removeRange(0, 1);
            il.isEmpty.should.equal(true);
            il.addAll([4, 2, 1, 33, 22, 11, 13]);
            il.removeRange(2, 4);
            il.toString.should.equal("[4, 2, 11, 13]");
        }

        @Tags("IntegerList")
        @("Compare")
        unittest
        {
            auto il = new IntegerList([7, 2, 5, 15, 40]);
            il.contains(3).should.equal(false);
            il.contains(5).should.equal(true);
            il.opEquals(il).should.equal(true);
            il.get(2).should.equal(5);
            auto il1 = new IntegerList(il);
            il1.should.not.be(null);
            il.toString.should.equal("[7, 2, 5, 15, 40]");
            il1.toString.should.equal("[7, 2, 5, 15, 40]");
            il.should.equal(il1);
            il.sort;
            il.toString.should.equal("[2, 5, 7, 15, 40]");
            il.should.not.equal(il1);
            il.clear;
            il.toString.should.equal("[]");
            il.should.not.equal(il1);
            class A
            {
            }

            auto a = new A;
            il.should.not.equal(a);
        }
    }
}
