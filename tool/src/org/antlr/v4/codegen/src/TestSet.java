package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

/** */
public class TestSet extends OutputModelObject {
	public BitSetDef set;
	public TestSet(OutputModelFactory factory, GrammarAST blkAST, IntervalSet set) {
		this.set = factory.createTestBitSet(blkAST, set);
		factory.defineBitSet(this.set);
	}
}
