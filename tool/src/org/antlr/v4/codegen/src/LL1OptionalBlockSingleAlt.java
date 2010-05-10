package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** */
public class LL1OptionalBlockSingleAlt extends LL1OptionalBlock {
	public Object expr;
	public LL1OptionalBlockSingleAlt(CodeGenerator gen, GrammarAST blkAST, List<CodeBlock> alts) {
		super(gen, blkAST, alts);
		IntervalSet look = altLookSets[1];
		if ( look.size() < gen.target.getInlineTestsVsBitsetThreshold() ) {
			expr = new TestSetInline(gen, this, blkAST, look);
		}
		else {
			expr = new TestSet(gen, blkAST, look);
		}
	}

	@Override
	public List<String> getChildren() {
		final List<String> sup = super.getChildren();
		return new ArrayList<String>() {{ if ( sup!=null ) addAll(sup); add("expr"); }};
	}
}
