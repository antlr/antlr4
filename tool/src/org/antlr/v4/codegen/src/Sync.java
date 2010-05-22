package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class Sync extends SrcOp {
	public int decision;
	public BitSetDecl expecting;
	public Sync(OutputModelFactory factory, GrammarAST blkOrEbnfRootAST,
							IntervalSet expecting)
	{
		super(factory, blkOrEbnfRootAST);
//		this.decision = ((BlockStartState)blkOrEbnfRootAST.nfaState).decision;
		this.expecting = factory.createExpectingBitSet(ast, decision, expecting);
		factory.defineBitSet(this.expecting);
	}
}
