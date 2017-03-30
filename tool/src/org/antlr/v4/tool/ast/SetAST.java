/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;

public class SetAST extends GrammarAST implements RuleElementAST {

	public SetAST(SetAST node) {
		super(node);
	}

	public SetAST(int type, Token t, String text) { super(type,t,text); }

	@Override
	public SetAST dupNode() {
		return new SetAST(this);
	}

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
