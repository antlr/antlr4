package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Collection;

public class XPathRootTokenElement extends XPathElement {
	protected int tokenType;

	public XPathRootTokenElement(String tokenName, int tokenType) {
		super(tokenName);
		this.tokenType = tokenType;
	}

	@Override
	public Collection<? extends ParseTree> evaluate(final ParseTree t) {
		Token tok = (Token)t.getPayload();
		if ( tok.getType() == tokenType ) {
			return new ArrayList<ParseTree>() {{add(t);}};
		}
		return new ArrayList<ParseTree>();
	}
}
