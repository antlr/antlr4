package org.antlr.v4.parse;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.Token;
import org.antlr.v4.Tool;
import org.antlr.v4.tool.ErrorType;

public class ToolANTLRLexer extends ANTLRLexer {
	public Tool tool;

	public ToolANTLRLexer(CharStream input, Tool tool) {
		super(input);
		this.tool = tool;
	}

	@Override
	public void grammarError(ErrorType etype, Token token, Object... args) {
		tool.errMgr.grammarError(etype, getSourceName(), token, args);
	}
}
