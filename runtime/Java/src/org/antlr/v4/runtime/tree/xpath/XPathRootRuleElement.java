package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.Collection;

public class XPathRootRuleElement extends XPathElement {
	protected int ruleIndex;
	public XPathRootRuleElement(String nodeName, int ruleIndex) {
		super(nodeName);
		this.ruleIndex = ruleIndex;
	}

	@Override
	public Collection<? extends ParseTree> evaluate(final ParseTree t) {
		// /* means whatever the root is
		if ( isWildcard() ) {
			return new ArrayList<ParseTree>() {{add(t);}};
		}
		else { // /x means x must be root
			ParserRuleContext ctx = (ParserRuleContext)t.getPayload();
			if ( ctx.getRuleIndex() == ruleIndex ) {
				return new ArrayList<ParseTree>() {{add(t);}};
			}
		}
		return new ArrayList<ParseTree>();
	}
}
