package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XPathWildcardElement extends XPathElement {
	public XPathWildcardElement() {
		super(XPath.WILDCARD);
	}

	@Override
	public Collection<ParseTree> evaluate(final ParseTree t) {
		if ( invert ) return new ArrayList<ParseTree>(); // !* is weird but valid (empty)
		return new ArrayList<ParseTree>() {{addAll((List<ParseTree>)t.getChildren());}};
	}
}
