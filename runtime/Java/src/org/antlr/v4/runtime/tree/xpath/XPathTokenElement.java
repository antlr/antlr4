package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

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
	public Collection<ParseTree> evaluate(ParseTree t) {
		// return all children of t that match nodeName
		List<ParseTree> nodes = new ArrayList<ParseTree>();
		for (ParseTree c : t.getChildren()) {
			if ( c instanceof TerminalNode ) {
				TerminalNode tnode = (TerminalNode)c;
				if ( tnode.getSymbol().getType() == tokenType ) {
					nodes.add(c);
				}
			}
		}
		return nodes;
	}
}
