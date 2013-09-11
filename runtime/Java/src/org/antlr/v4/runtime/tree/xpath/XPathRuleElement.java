package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Collection;

public class XPathRuleElement extends XPathElement {
	public XPathRuleElement(String ruleName) {
		super(ruleName);
	}

	@Override
	public Collection<? extends ParseTree> evaluate(ParseTree t) {
		return null;
	}
}
