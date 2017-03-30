/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;

public class NotAST extends GrammarAST implements RuleElementAST {

	public NotAST(NotAST node) {
		super(node);
	}

	public NotAST(int type, Token t) { super(type, t); }

	@Override
	public NotAST dupNode() {
		return new NotAST(this);
	}

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
