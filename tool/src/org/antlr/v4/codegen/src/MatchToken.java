package org.antlr.v4.codegen.src;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.TerminalAST;

/** */
public class MatchToken extends SrcOp {
	public int ttype;
	public IntervalSet[] follow;

	public MatchToken(CodeGenerator gen, TerminalAST ast) {
		this.gen = gen;
	}
}
