package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class TestSet extends OutputModelObject {
	public BitSetDef set;
	public TestSet(CodeGenerator gen, GrammarAST blkAST, IntervalSet set) {
		this.set = gen.defineTestBitSet(blkAST, set);
	}
}
