package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.GrammarAST;

import java.util.List;

/** */
public class InvokeRule extends SrcOp {
	public String name;
	public String label;
	public List<String> args;
	public BitSetDef follow;

	public InvokeRule(CodeGenerator gen, GrammarAST ast, GrammarAST labelAST) {
		this.gen = gen;
		this.ast = ast;
		this.name = ast.getText();
		if ( labelAST!=null ) this.label = labelAST.getText();
		if ( ast.getChildCount()>0 ) {
			String argAction = ast.getChild(0).getText();
			// split and translate argAction
		}
		// compute follow
		LinearApproximator approx = new LinearApproximator(gen.g, -1);
		IntervalSet fset = approx.LOOK(ast.nfaState.transition(0).target);
		System.out.println("follow="+follow);
		follow = gen.defineFollowBitSet(ast, fset);
	}
}
