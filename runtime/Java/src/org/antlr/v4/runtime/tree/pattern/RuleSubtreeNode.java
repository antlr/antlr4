package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.ParserRuleContext;

public class RuleSubtreeNode extends ParserRuleContext {
	RuleTagToken ruleTagToken;

	public RuleSubtreeNode(ParserRuleContext ctx) {
		copyFrom(ctx);
		this.ruleTagToken = (RuleTagToken)ctx.start; // first (and only) token is the <ruletag>
	}

	@Override
	public String getText() {
		return ruleTagToken.getText();
	}

	@Override
	public int getRuleIndex() { return ruleTagToken.ruleIndex; }
}
