package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class TestSetInline extends OutputModelObject {
	public String[] ttypes;
	public TestSetInline(CodeGenerator gen, GrammarAST blkAST, IntervalSet set) {
		this.ttypes = gen.target.getTokenTypeAsTargetLabel(gen.g, set.toArray());
	}
}
