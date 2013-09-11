package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.tree.ParseTree;

import java.util.Collection;

public class XPathRuleElement extends XPathElement {
	protected int ruleIndex;
	public XPathRuleElement(String ruleName, int ruleIndex) {
		super(ruleName);
		this.ruleIndex = ruleIndex;
	}

	@Override
	public Collection<? extends ParseTree> evaluate(ParseTree t) {
		return null;
	}
}
