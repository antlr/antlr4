package org.antlr.v4.codegen.src;

import org.antlr.v4.analysis.LinearApproximator;
import org.antlr.v4.automata.NFA;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.TerminalAST;

import java.util.ArrayList;
import java.util.List;

/** */
public class MatchToken extends SrcOp implements LabeledOp {
	public String name;
	public BitSetDecl follow;
	public List<String> labels = new ArrayList<String>();

	public MatchToken(OutputModelFactory factory, TerminalAST ast, GrammarAST labelAST) {
		super(factory, ast);
		int ttype = factory.g.getTokenType(ast.getText());
		name = factory.gen.target.getTokenTypeAsTargetLabel(factory.g, ttype);
		if ( labelAST!=null ) {
			String label = labelAST.getText();
			labels.add(label);
			TokenDecl d = new TokenDecl(factory, label);
			factory.currentRule.peek().addDecl(d);
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN  ) {
				TokenListDecl l = new TokenListDecl(factory, factory.gen.target.getListLabel(label));
				factory.currentRule.peek().addDecl(l);
			}
		}

		// If action refs as token not label, we need to define implicit label
		if ( factory.currentAlt.tokenRefsInActions.containsKey(ast.getText()) ) {
			String label = factory.gen.target.getImplicitTokenLabel(ast.getText());
			labels.add(label);
			TokenDecl d = new TokenDecl(factory, label);
			factory.currentRule.peek().addDecl(d);
		}

		LinearApproximator approx = new LinearApproximator(factory.g, NFA.INVALID_DECISION_NUMBER);
		IntervalSet fset = approx.LOOK(ast.nfaState.transition(0).target);
		System.out.println("follow="+fset);
		follow = factory.createFollowBitSet(ast, fset);
		factory.defineBitSet(follow);
	}

	public List<String> getLabels() { return labels; }
}
