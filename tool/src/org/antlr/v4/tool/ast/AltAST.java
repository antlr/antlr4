/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;
import org.antlr.v4.analysis.LeftRecursiveRuleAltInfo;
import org.antlr.v4.tool.Alternative;

/** Any ALT (which can be child of ALT_REWRITE node) */
public class AltAST extends GrammarASTWithOptions {
	public Alternative alt;

	/** If we transformed this alt from a left-recursive one, need info on it */
	public LeftRecursiveRuleAltInfo leftRecursiveAltInfo;

	/** If someone specified an outermost alternative label with #foo.
	 *  Token type will be ID.
	 */
	public GrammarAST altLabel;

	public AltAST(AltAST node) {
		super(node);
		this.alt = node.alt;
		this.altLabel = node.altLabel;
		this.leftRecursiveAltInfo = node.leftRecursiveAltInfo;
	}

	public AltAST(Token t) { super(t); }
	public AltAST(int type) { super(type); }
	public AltAST(int type, Token t) { super(type, t); }
	public AltAST(int type, Token t, String text) { super(type,t,text); }

	@Override
	public AltAST dupNode() { return new AltAST(this); }

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
