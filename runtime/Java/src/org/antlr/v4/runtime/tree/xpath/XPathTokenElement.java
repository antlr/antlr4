package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XPathTokenElement extends XPathElement {
	protected int tokenType;
	public XPathTokenElement(String tokenName, int tokenType) {
		super(tokenName);
		this.tokenType = tokenType;
	}

	@Override
	public Collection<? extends ParseTree> evaluate(ParseTree t) {
		// return all children of t that match nodeName
		List<ParseTree> nodes = new ArrayList<ParseTree>();
		for (ParseTree c : t.getChildren()) {
			if ( c.getText().equals(nodeName) ) {
				nodes.add(c);
			}
		}
		return nodes;
	}
}
