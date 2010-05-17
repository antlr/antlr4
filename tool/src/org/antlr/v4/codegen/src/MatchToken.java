package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.TerminalAST;

/** */
public class MatchToken extends SrcOp implements LabeledOp {
	public String name;
	public BitSetDecl follow;
	public String label;

	public MatchToken(OutputModelFactory factory, TerminalAST ast, GrammarAST labelAST) {
		this.factory = factory;
		int ttype = factory.g.getTokenType(ast.getText());
		name = factory.gen.target.getTokenTypeAsTargetLabel(factory.g, ttype);
		if ( labelAST!=null ) {
			label = labelAST.getText();
			TokenDecl d = new TokenDecl(factory, label);
			factory.currentRule.peek().addDecl(d);
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN  ) {
				TokenListDecl l = new TokenListDecl(factory, factory.getListLabel(label));
				factory.currentRule.peek().addDecl(l);
			}
		}

		LinearApproximator approx = new LinearApproximator(factory.g, -1);
		IntervalSet fset = approx.LOOK(ast.nfaState.transition(0).target);
		System.out.println("follow="+fset);
		follow = factory.createFollowBitSet(ast, fset);
		factory.defineBitSet(follow);
	}

	public String getLabel() {
		return label;
	}
}
