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

	public LL1Loop(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
		this.sync = new Sync(factory, blkAST, expecting);		
	}

	public void addIterationOp(SrcOp op) {
		if ( iteration==null ) iteration = new ArrayList<SrcOp>();
		iteration.add(op);
	}

	public void addCodeForLookaheadTempVar(IntervalSet look) {
		expr = factory.getLL1Test(look, ast);
		if ( expr instanceof TestSetInline ) {
			TestSetInline e = (TestSetInline) expr;
			Decl d = new TokenDecl(factory, e.varName);
			factory.currentRule.peek().addDecl(d);
			CaptureNextToken nextToken = new CaptureNextToken(e.varName);
			addPreambleOp(nextToken);
			addIterationOp(nextToken);
		}
	}

}
