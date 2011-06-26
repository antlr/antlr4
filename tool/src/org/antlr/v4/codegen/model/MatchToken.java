package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.decl.*;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.*;

import java.util.*;

/** */
public class MatchToken extends RuleElement implements LabeledOp {
	public String name;
	public List<Decl> labels = new ArrayList<Decl>();

	public MatchToken(OutputModelFactory factory, TerminalAST ast, GrammarAST labelAST) {
		super(factory, ast);
		int ttype = factory.g.getTokenType(ast.getText());
		name = factory.gen.target.getTokenTypeAsTargetLabel(factory.g, ttype);
		if ( labelAST!=null ) {
			String label = labelAST.getText();
			TokenDecl d = new TokenDecl(factory, label);
			labels.add(d);
			factory.currentRule.peek().addContextDecl(d);
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN ) {
				TokenListDecl l = new TokenListDecl(factory, factory.gen.target.getListLabel(label));
				factory.currentRule.peek().addContextDecl(l);
			}
		}

		// If action refs as token not label, we need to define implicit label
		boolean needsImplicitLabel =
			labels.size()==0 &&
			(factory.currentAlt.tokenRefsInActions.containsKey(ast.getText()) ||
			 factory.g.hasASTOption());
		if ( needsImplicitLabel ) {
			String label = factory.gen.target.getImplicitTokenLabel(ast.getText());
			TokenDecl d = new TokenDecl(factory, label);
			labels.add(d);
			factory.currentRule.peek().addLocalDecl(d);
		}
	}

	public List<Decl> getLabels() { return labels; }
}
