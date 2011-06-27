package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CoreOutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class TestSetInline extends SrcOp {
	public String varName;
	public String[] ttypes;
	public TestSetInline(CoreOutputModelFactory factory, GrammarAST ast, IntervalSet set) {
		super(factory, ast);
		this.ttypes = factory.gen.target.getTokenTypesAsTargetLabels(factory.g, set.toArray());
		this.varName = "_la";
	}
}
