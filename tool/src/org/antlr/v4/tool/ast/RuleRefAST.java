/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool.ast;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;

public class RuleRefAST extends GrammarASTWithOptions implements RuleElementAST {
	public RuleRefAST(RuleRefAST node) {
		super(node);
	}

	public RuleRefAST(Token t) { super(t); }
    public RuleRefAST(int type) { super(type); }
    public RuleRefAST(int type, Token t) { super(type, t); }

	/** Dup token too since we overwrite during LR rule transform */
	@Override
	public RuleRefAST dupNode() {
		RuleRefAST r = new RuleRefAST(this);
		// In LR transform, we alter original token stream to make e -> e[n]
		// Since we will be altering the dup, we need dup to have the
		// original token.  We can set this tree (the original) to have
		// a new token.
		r.token = this.token;
		this.token = new CommonToken(r.token);
		return r;
	}

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
