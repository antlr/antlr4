package org.antlr.v4.tool;

import org.antlr.runtime.Token;

/** An ALT or ALT_REWRITE node (left of ->) */
public class AltAST extends GrammarAST {
	public Alternative alt;

	public AltAST(Token t) { super(t); }
	public AltAST(int type) { super(type); }
	public AltAST(int type, Token t) { super(type, t); }	
}
