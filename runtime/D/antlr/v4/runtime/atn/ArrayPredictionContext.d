/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.ArrayPredictionContext;

import antlr.v4.runtime.atn.PredictionContext;
import antlr.v4.runtime.atn.SingletonPredictionContext;
import std.array;
import std.container.array;
import std.conv;
import std.stdio;

/**
 * Array of prediction contexts
 */
class ArrayPredictionContext : PredictionContext
{

    /**
     * Parent can be null only if full ctx mode and we make an array
     * from {@link #EMPTY} and non-empty. We merge {@link #EMPTY} by using null parent and
     * returnState == {@link #EMPTY_RETURN_STATE}.
     */
    public PredictionContext[] parents;

    /**
     * Sorted for merge, no duplicates; if present,
     * {@link #EMPTY_RETURN_STATE} is always last.
     * @uml
     * @final
     */
    public int[] returnStates;

    public this(SingletonPredictionContext a)
    {
        PredictionContext[] parents;
        parents ~= a.parent;
        int[] returnStates;
        returnStates ~= a.returnState;
        this(parents, returnStates);
    }

    public this(PredictionContext[] parents, int[] returnStates)
    {
        super(calculateHashCode(parents, returnStates)); // caching the hash code
        assert(parents && parents.length > 0);
        assert(returnStates && returnStates.length > 0);
        debug {
            import std.stdio;
            writefln("CREATE ARRAY: %s, %s", parents, returnStates);
        }
        this.parents = parents;
        this.returnStates = returnStates;
    }

    /**
     * @uml
     * @override
     */
    public override bool isEmpty()
    {
        // since EMPTY_RETURN_STATE can only appear in the last position, we
        // don't need to verify that size==1
        return returnStates[0] == EMPTY_RETURN_STATE;
    }

    /**
     * @uml
     * @override
     */
    public override size_t size()
    {
        return returnStates.length;
    }

    /**
     * @uml
     * @override
     */
    public override PredictionContext getParent(int index)
    {
        return parents[index];
    }

    /**
     * @uml
     * @override
     */
    public override int getReturnState(int index)
    {
        return returnStates[index];
    }

    /**
     * @uml
     * @override
     */
    public override bool opEquals(Object o)
    {
        if (!cast(ArrayPredictionContext)o) {
            return false;
        }

        if (this.toHash != o.toHash) {
            return false; // can't be same if hash is different
        }
        auto aObject = cast(ArrayPredictionContext)o;
        import std.algorithm.comparison : equal;
        return equal(parents, aObject.parents) &&
            equal(returnStates, aObject.returnStates);
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        if (isEmpty)
            return "[]";
        string[] buf;
        foreach (i, el; returnStates) {
            if (el == EMPTY_RETURN_STATE) {
                buf ~= "$";
                continue;
            }
            buf ~= to!string(el);
            if (parents[i]) {
                buf ~= " " ~ parents[i].toString;
            }
            else {
                buf ~= "null";
            }
        }
        return "[" ~ join(buf, ", ") ~ "]";
    }

}

version(unittest) {
    import dshould : be, equal, not, should;
    import std.typecons : tuple;
    import unit_threaded;

    class Test {

        @Tags("ArrayPredictionContext")
        @("Empty")
        unittest {
            import antlr.v4.runtime.atn.EmptyPredictionContext;
            auto spc = new EmptyPredictionContext;
            auto apc = new ArrayPredictionContext(spc);
            ArrayPredictionContext apcp;
            apc.toString.should.equal("[]");
            apc.size.should.equal(1);
            apc.isEmpty.should.equal(true);
            static if (size_t.sizeof == 4)
                apc.toHash.should.equal(786443632U);
            else
                apc.toHash.should.equal(6723470047294944096UL);
            apc.getParent(0).should.be(null);
            apc.getReturnState(0).should.equal(PredictionContext.EMPTY_RETURN_STATE);
            auto apc1 = new ArrayPredictionContext(spc);
            apcp = apc;
            apc.should.equal(apcp);
            class A {}
            auto apc2 = new A;
            apc.should.not.equal(apc2);
            apc.should.equal(apc1);
        }

        @Tags("ArrayPredictionContext")
        @("Flat")
        unittest {
            import antlr.v4.runtime.atn.EmptyPredictionContext;
            auto spc = new EmptyPredictionContext;
            auto apc = new ArrayPredictionContext(spc);
            apc.returnStates = 12 ~ apc.returnStates;
            apc.toString.should.equal("[12, null, $]");
            apc.size.should.equal(2);
            apc.isEmpty.should.equal(false);
            apc.getParent(0).should.be(null);
            static if (size_t.sizeof == 4)
                apc.toHash.should.equal(786443632U);
            else
                apc.toHash.should.equal(6723470047294944096UL);
            static if (size_t.sizeof == 4)
                apc.calculateHashCode(apc.getParent(0), apc.getReturnState(0))
                    .should.equal(182986417U);
            else
                apc.calculateHashCode(apc.getParent(0), apc.getReturnState(0))
                    .should.equal(4292457832056041856UL);
            apc.getReturnState(0).should.equal(12);
            auto apc1 = new ArrayPredictionContext(spc);
            apc.should.not.equal(apc1);
        }

        @Tags("ArrayPredictionContext")
        @("Deep")
        unittest {
            import antlr.v4.runtime.atn.EmptyPredictionContext;
            auto spc = new EmptyPredictionContext;
            auto apc = new ArrayPredictionContext(spc);
            auto apc1 = new ArrayPredictionContext(spc);
            apc.returnStates = 12 ~ apc.returnStates;
            apc.toString.should.equal("[12, null, $]");
            apc.size.should.equal(2);
            apc.isEmpty.should.equal(false);
            apc.getParent(0).should.be(null);
            apc.getReturnState(0).should.equal(12);
            apc.parents[0] = apc1;
            auto apc2 = new ArrayPredictionContext(apc.parents, apc.returnStates);
            apc2.toString.should.equal("[12,  [], $]");
            apc.toString.should.equal("[12,  [], $]");
            apc.should.not.equal(apc2);
            apc2.parents = apc1 ~ apc2.parents;
            apc2.returnStates = 13 ~ apc2.returnStates;
            apc2.toString.should.equal("[13,  [], 12,  [], $]");
        }
    }
}
