/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.TerminalAST;

import java.util.ArrayList;
import java.util.List;

/** */
public class MatchToken extends RuleElement implements LabeledOp {
	public String name;
	public int ttype;
	public List<Decl> labels = new ArrayList<Decl>();

	public MatchToken(OutputModelFactory factory, TerminalAST ast) {
		super(factory, ast);
		Grammar g = factory.getGrammar();
		CodeGenerator gen = factory.getGenerator();
		ttype = g.getTokenType(ast.getText());
		name = gen.getTarget().getTokenTypeAsTargetLabel(g, ttype);
	}

	public MatchToken(OutputModelFactory factory, GrammarAST ast) {
		super(factory, ast);
	}

	@Override
	public List<Decl> getLabels() { return labels; }
}
