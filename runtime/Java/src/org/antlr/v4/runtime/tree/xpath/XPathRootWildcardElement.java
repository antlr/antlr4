package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Collection;

public class XPathRootWildcardElement extends XPathElement {
	public XPathRootWildcardElement() {
		super(XPath.WILDCARD);
	}

	@Override
	public Collection<? extends ParseTree> evaluate(final ParseTree t) {
		return new ArrayList<ParseTree>() {{add(t);}};
	}
}
