package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;

public class RuleTagToken implements Token {
	protected String ruleName;
	protected int ruleImaginaryTokenType;

	public RuleTagToken(String ruleName, int ruleImaginaryTokenType) {
		this.ruleName = ruleName;
		this.ruleImaginaryTokenType = ruleImaginaryTokenType;
	}

	@Override
	public int getChannel() {
		return 0;
	}

	@Override
	public String getText() {
		return "<"+ruleName+">";
	}

	@Override
	public int getType() {
		return ruleImaginaryTokenType;
	}

	@Override
	public int getLine() {
		return 0;
	}

	@Override
	public int getCharPositionInLine() {
		return 0;
	}

	@Override
	public int getTokenIndex() {
		return 0;
	}

	@Override
	public int getStartIndex() {
		return 0;
	}

	@Override
	public int getStopIndex() {
		return 0;
	}

	@Override
	public TokenSource getTokenSource() {
		return null;
	}

	@Override
	public CharStream getInputStream() {
		return null;
	}

	@Override
	public String toString() {
		return ruleName+":"+ ruleImaginaryTokenType;
	}
}
