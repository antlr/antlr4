/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.EmptyPredictionContext;

import antlr.v4.runtime.atn;

/**
 * Empty PredictionContex
 */
class EmptyPredictionContext : SingletonPredictionContext
{

    public this()
    {
        super(null, EMPTY_RETURN_STATE);
    }

    /**
     * @uml
     * @override
     */
    public override bool isEmpty()
    {
        return true;
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
        return null;
    }

    /**
     * @uml
     * @override
     */
    public override int getReturnState(int index)
    {
        return returnState;
    }

    /**
     * @uml
     * @override
     */
    public override bool opEquals(Object o)
    {
        if (cast(EmptyPredictionContext)o)
            return true;
        return this is o;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return "$";
    }

}

version(unittest) {
    import dshould : be, equal, not, should;
    import std.typecons : tuple;
    import unit_threaded;

    @Tags("EmptyPredictionContext")
    @("Construction")
    unittest {
        auto spc = new EmptyPredictionContext;
        spc.should.not.be(null);
        spc.isEmpty.should.equal(true);
        spc.getReturnState(0).should.equal(int.max);
        auto spc1 = new EmptyPredictionContext;
        spc1.isEmpty.should.equal(true);
        if (spc == spc1)
            assert(true);
        auto spc2 = SingletonPredictionContext.create(null,
                                                      PredictionContext.EMPTY_RETURN_STATE);
        spc2.isEmpty.should.equal(true);
        spc2.toString.should.equal("$");
        spc2.getReturnState(0).should.equal(int.max);
        spc2.classinfo.should.equal(EmptyPredictionContext.classinfo);
        spc.opEquals(spc1).should.equal(true);
        import antlr.v4.runtime.ParserRuleContext;
        auto prc1 = new ParserRuleContext;
        spc2.opEquals(prc1).should.equal(false);
        spc.toDOTString(spc2).should.equal("digraph G {\nrankdir=LR;\n}\n");
    }
}
