package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.CommonToken;

public class TokenTagToken extends CommonToken {
	protected String tokenName;
	protected String label;

	public TokenTagToken(String tokenName, int type) {
		super(type);
		this.tokenName = tokenName;
	}

	public TokenTagToken(String tokenName, int type, String label) {
		this(tokenName, type);
		this.label = label;
	}

	@Override
	public String getText() {
		return "<"+tokenName+">";
	}

	@Override
	public String toString() {
		return tokenName+":"+type;
	}
}
