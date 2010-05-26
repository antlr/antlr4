package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** */
public abstract class LL1Loop extends Choice {
	public OutputModelObject expr;
	public List<SrcOp> iteration;
	public Sync sync;	

	public LL1Loop(OutputModelFactory factory,
				   GrammarAST blkAST,
				   List<CodeBlock> alts)
	{
		super(factory, blkAST, alts);
	}

	public void addIterationOp(SrcOp op) {
		if ( iteration==null ) iteration = new ArrayList<SrcOp>();
		iteration.add(op);
	}

	public OutputModelObject addCodeForLoopLookaheadTempVar(IntervalSet look) {
		OutputModelObject expr = addCodeForLookaheadTempVar(look);
		if ( expr instanceof TestSetInline ) {
			TestSetInline e = (TestSetInline)expr;
			CaptureNextTokenType nextType = new CaptureNextTokenType(e.varName);
			addIterationOp(nextType);
		}
		return expr;
	}
}
