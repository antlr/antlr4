package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Collection;

public class XPathWildcardElement extends XPathElement {
	public XPathWildcardElement() {
		super(XPath.WILDCARD);
	}

	@Override
	public Collection<ParseTree> evaluate(final ParseTree t) {
		return new ArrayList<ParseTree>() {{addAll(t.getChildren());}};
	}
}
