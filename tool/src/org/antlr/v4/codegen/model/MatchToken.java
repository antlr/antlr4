package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.*;
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
		Grammar g = factory.getGrammar();
		CodeGenerator gen = factory.getGenerator();
		int ttype = g.getTokenType(ast.getText());
		name = gen.target.getTokenTypeAsTargetLabel(g, ttype);
		if ( labelAST!=null ) {
			String label = labelAST.getText();
			TokenDecl d = new TokenDecl(factory, label);
			labels.add(d);
			factory.getCurrentRule().addContextDecl(d);
			if ( labelAST.parent.getType() == ANTLRParser.PLUS_ASSIGN ) {
				TokenListDecl l = new TokenListDecl(factory, gen.target.getListLabel(label));
				factory.getCurrentRule().addContextDecl(l);
			}
		}
	}

	public List<Decl> getLabels() { return labels; }
}
