package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.TerminalAST;

/** */
public class MatchToken extends SrcOp {
	public String name;
	public BitSetDef follow;
	public String label;

	public MatchToken(OutputModelFactory factory, TerminalAST ast, GrammarAST labelAST) {
		this.factory = factory;
		name = ast.getText();
		if ( labelAST!=null ) this.label = labelAST.getText();

		LinearApproximator approx = new LinearApproximator(factory.g, -1);
		IntervalSet fset = approx.LOOK(ast.nfaState.transition(0).target);
		System.out.println("follow="+follow);
		follow = factory.createFollowBitSet(ast, fset);
		factory.defineBitSet(follow);
	}
}
