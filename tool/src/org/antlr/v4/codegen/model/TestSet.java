package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class TestSet extends RuleElement {
	public BitSetDecl set;
	public TestSet(OutputModelFactory factory, GrammarAST blkAST, IntervalSet set) {
		super(factory, blkAST);
		this.set = factory.createTestBitSet(blkAST, set);
		factory.defineBitSet(this.set);
	}
}
