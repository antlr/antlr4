package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class TestSetInline extends OutputModelObject {
	public String varName;
	public String[] ttypes;
//	public CaptureNextToken nextToken;
//	public Choice choice;
	public TestSetInline(OutputModelFactory factory, GrammarAST blkAST, IntervalSet set) {
		super(factory, blkAST);
		this.ttypes = factory.gen.target.getTokenTypesAsTargetLabels(factory.g, set.toArray());
		this.varName = "la"+blkAST.token.getTokenIndex();
//		this.choice = choice;
//		nextToken = new CaptureNextToken();
//		choice.addPreambleOp(nextToken);
	}
}
