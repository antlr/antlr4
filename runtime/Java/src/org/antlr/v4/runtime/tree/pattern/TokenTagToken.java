package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.CommonToken;

public class TokenTagToken extends CommonToken {
	protected String tokenName;

	public TokenTagToken(String tokenName, int type) {
		super(type);
		this.tokenName = tokenName;
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
