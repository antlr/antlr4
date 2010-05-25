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
				   List<CodeBlock> alts,
				   int decision)
	{
		super(factory, blkAST, alts, decision);
		this.sync = new Sync(factory, blkAST, expecting, decision, "enter");		
	}

	public void addIterationOp(SrcOp op) {
		if ( iteration==null ) iteration = new ArrayList<SrcOp>();
		iteration.add(op);
	}

	public void addCodeForLookaheadTempVar(IntervalSet look) {
		expr = factory.getLL1Test(look, ast);
		if ( expr instanceof TestSetInline ) {
			TestSetInline e = (TestSetInline) expr;
			Decl d = new TokenTypeDecl(factory, e.varName);
			factory.currentRule.peek().addDecl(d);
			CaptureNextTokenType nextType = new CaptureNextTokenType(e.varName);
			addPreambleOp(nextType);
			addIterationOp(nextType);
		}
	}

}
