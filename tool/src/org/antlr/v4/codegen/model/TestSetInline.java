package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class TestSetInline extends SrcOp {
	public String varName;
	public String[] ttypes;
//	public CaptureNextToken nextToken;
//	public Choice choice;
	public TestSetInline(OutputModelFactory factory, GrammarAST ast, IntervalSet set) {
		super(factory, ast);
		this.ttypes = factory.gen.target.getTokenTypesAsTargetLabels(factory.g, set.toArray());
		this.varName = "_la";
//		this.choice = choice;
//		nextToken = new CaptureNextToken();
//		choice.addPreambleOp(nextToken);
	}
}
