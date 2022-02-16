/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.TerminalAST;

import java.util.ArrayList;
import java.util.List;

/** */
public class MatchToken extends RuleElement implements LabeledOp {
	public final String name;
	public final String escapedName;
	public final int ttype;
	public final List<Decl> labels = new ArrayList<Decl>();

	public MatchToken(OutputModelFactory factory, TerminalAST ast) {
		super(factory, ast);
		Grammar g = factory.getGrammar();
		CodeGenerator gen = factory.getGenerator();
		ttype = g.getTokenType(ast.getText());
		Target target = gen.getTarget();
		name = target.getTokenTypeAsTargetLabel(g, ttype);
		escapedName = target.escapeIfNeeded(name);
	}

	public MatchToken(OutputModelFactory factory, GrammarAST ast) {
		super(factory, ast);
		ttype = 0;
		name = null;
		escapedName = null;
	}

	@Override
	public List<Decl> getLabels() { return labels; }
}
