/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;

public class StarBlockAST extends GrammarAST implements RuleElementAST, QuantifierAST {
	private final boolean _greedy;

	public StarBlockAST(StarBlockAST node) {
		super(node);
		_greedy = node._greedy;
	}

	public StarBlockAST(int type, Token t, Token nongreedy) {
		super(type, t);
		_greedy = nongreedy == null;
	}

	@Override
	public boolean isGreedy() {
		return _greedy;
	}

	@Override
	public StarBlockAST dupNode() { return new StarBlockAST(this); }

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
