package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class TestSetInline extends OutputModelObject {
	public String[] ttypes;
	public CaptureNextToken nextToken;
	public Choice choice;
	public TestSetInline(CodeGenerator gen, Choice choice, GrammarAST blkAST, IntervalSet set) {
		this.gen = gen;
		this.ast = blkAST;
		this.ttypes = gen.target.getTokenTypesAsTargetLabels(gen.g, set.toArray());
		this.choice = choice;
		nextToken = new CaptureNextToken("la"+blkAST.token.getTokenIndex());
		choice.addPreambleOp(nextToken);
	}
}
