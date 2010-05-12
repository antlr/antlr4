package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class InvokeRule extends SrcOp {
	public String name;
	public String label;
	public List<String> args;
	public BitSetDef follow;

	public InvokeRule(OutputModelFactory factory, GrammarAST ast, GrammarAST labelAST) {
		this.factory = factory;
		this.ast = ast;
		this.name = ast.getText();
		if ( labelAST!=null ) this.label = labelAST.getText();
		if ( ast.getChildCount()>0 ) {
			String argAction = ast.getChild(0).getText();
			// split and translate argAction
		}
		// compute follow
		LinearApproximator approx = new LinearApproximator(factory.g, -1);
		IntervalSet fset = approx.LOOK(ast.nfaState.transition(0).target);
		System.out.println("follow="+follow);
		follow = factory.createFollowBitSet(ast, fset);
		factory.defineBitSet(follow);
	}
}
