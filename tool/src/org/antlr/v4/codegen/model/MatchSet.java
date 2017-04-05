/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.codegen.model;

import org.antlr.v4.codegen.OutputModelFactory;
import org.antlr.v4.codegen.model.decl.Decl;
import org.antlr.v4.codegen.model.decl.TokenTypeDecl;
import org.antlr.v4.runtime.atn.SetTransition;
import org.antlr.v4.tool.ast.GrammarAST;

public class MatchSet extends MatchToken {
	@ModelElement public TestSetInline expr;
	@ModelElement public CaptureNextTokenType capture;

	public MatchSet(OutputModelFactory factory, GrammarAST ast) {
		super(factory, ast);
		SetTransition st = (SetTransition)ast.atnState.transition(0);
		int wordSize = factory.getGenerator().getTarget().getInlineTestSetWordSize();
		expr = new TestSetInline(factory, null, st.set, wordSize);
		Decl d = new TokenTypeDecl(factory, expr.varName);
		factory.getCurrentRuleFunction().addLocalDecl(d);
		capture = new CaptureNextTokenType(factory,expr.varName);
	}
}
