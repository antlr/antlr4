/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.SingletonPredictionContext;

import std.conv;
import antlr.v4.runtime.atn;

/**
 * TODO add class description
 */
class SingletonPredictionContext : PredictionContext
{

    public PredictionContext parent;

    public int returnState;

    public this(PredictionContext parent, int returnState)
    {
        super(parent !is null ? calculateHashCode(parent, returnState) : calculateEmptyHashCode);
        assert(returnState != ATNState.INVALID_STATE_NUMBER);
        this.parent = parent;
        this.returnState = returnState;
        //this.id = new ContextID().instance.getNextId;
    }

    public static SingletonPredictionContext create(PredictionContext parent, int returnState)
    {
        if (returnState == EMPTY_RETURN_STATE && parent is null ) {
            // someone can pass in the bits of an array ctx that mean $
            return cast(SingletonPredictionContext)EMPTY;
        }
        if (parent is null)
            parent = cast(PredictionContext)PredictionContext.EMPTY;
        return new SingletonPredictionContext(parent, returnState);
    }

    /**
     * @uml
     * @override
     */
    public override size_t size()
    {
        return 1;
    }

    /**
     * @uml
     * @override
     */
    public override PredictionContext getParent(int index)
    {
        assert(index == 0);
        return parent;
    }

    /**
     * @uml
     * @override
     */
    public override int getReturnState(int index)
    {
        assert(index == 0);
        return returnState;
    }

    /**
     * @uml
     * @override
     */
    public override bool opEquals(Object o)
    {
        if (!cast(SingletonPredictionContext)o) {
            return false;
        }

        if (this.toHash != (cast(PredictionContext)o).toHash) {
            return false; // can't be same if hash is different
        }

        SingletonPredictionContext s = cast(SingletonPredictionContext)o;
        return returnState == s.returnState &&
            (parent !is null && parent.opEquals(s.parent));
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        string up = parent !is null ? parent.toString : "";
        if (up.length == 0) {
            if (returnState == EMPTY_RETURN_STATE ) {
                return "$";
            }
            return to!string(returnState);
        }
        return to!string(returnState) ~ " " ~ up;
    }

}

version(unittest) {
    import dshould : be, equal, not, should;
    import std.typecons : tuple;
    import unit_threaded;

    class Test {

        @Tags("SingletonPredictionContext")
        @("Construction")
        unittest {
            PredictionContext spc = SingletonPredictionContext.create(null, 12);
            spc.should.not.be(null);
            spc.toString.should.equal("12 $");
            spc.getReturnState(0).should.equal(12);
            (cast(PredictionContext)spc.getParent(0)).should.not.be(null);
            auto emptyPC = SingletonPredictionContext.create(null,
                                                             PredictionContext.EMPTY_RETURN_STATE);
            (cast(SingletonPredictionContext)emptyPC).should.not.be(null);
            spc = SingletonPredictionContext.create(emptyPC, 11);
            spc.toString.should.equal("11 $");
            auto spc1 = SingletonPredictionContext.create(spc, 10);
            spc1.toString.should.equal("10 11 $");
        }

        @Tags("SingletonPredictionContext")
        @("Compare")
        unittest {
            auto spc11 = SingletonPredictionContext.create(null, 11);
            auto spc12 = SingletonPredictionContext.create(null, 12);
            auto spc = SingletonPredictionContext.create(null, 12);
            spc.should.equal(spc12);
            spc11.should.not.equal(spc12);
            spc12.should.equal(spc12);
            class A {}
            auto a = new A;
            spc11.should.not.equal(a);
        }
    }
}
