package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** */
public class LL1StarBlockSingleAlt extends LL1Choice {
	public Object expr;
	public List<SrcOp> loopIteration = new ArrayList<SrcOp>();
	public LL1StarBlockSingleAlt(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
		IntervalSet look = altLookSets[1];
		expr = factory.getLL1Test(look, blkAST);
		if ( expr instanceof TestSetInline ) {
			TestSetInline e = (TestSetInline)expr;
			CaptureNextToken nextToken = new CaptureNextToken(e.varName);
			addPreambleOp(nextToken);
			loopIteration.add(nextToken);
		}
	}

	@Override
	public List<String> getChildren() {
		final List<String> sup = super.getChildren();
		return new ArrayList<String>() {{ if ( sup!=null ) addAll(sup); add("expr"); }};
	}
}
