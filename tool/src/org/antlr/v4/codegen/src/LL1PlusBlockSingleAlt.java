package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.ArrayList;
import java.util.List;

/** */
public class LL1PlusBlockSingleAlt extends LL1Choice {
	public Object expr;
	public List<SrcOp> loopIteration = new ArrayList<SrcOp>();
	public LL1PlusBlockSingleAlt(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlock> alts) {
		super(factory, blkAST, alts);
		IntervalSet loopBackLook = altLookSets[2]; // loop exit is alt 1
		expr = factory.getLL1Test(loopBackLook, ast);
		if ( expr instanceof TestSetInline ) {
			CaptureNextToken nextToken = new CaptureNextToken("la"+ast.token.getTokenIndex());
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
