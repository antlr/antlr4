package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.TerminalAST;

/** */
public class MatchToken extends SrcOp {
	public String name;
	public String bitSetName;

	public MatchToken(CodeGenerator gen, TerminalAST ast) {
		this.gen = gen;
		name = ast.getText();

		LinearApproximator approx = new LinearApproximator(gen.g, -1);
		IntervalSet follow = approx.LOOK(ast.nfaState.transition(0).target);
		System.out.println("follow="+follow);
		//bitSetName = gen.defineBitSet(follow);
	}
}
