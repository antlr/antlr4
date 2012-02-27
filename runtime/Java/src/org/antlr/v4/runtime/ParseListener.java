package org.antlr.v4.runtime;

/** We must distinguish between listeners triggered during the parse
 *  from listeners triggered during a subsequent tree walk.  During
 *  the parse, the ctx object arg for enter methods don't have any labels set.
 *  We can only access the general ParserRuleContext<Symbol> ctx.
 *  Also, we can only call exit methods for left-recursive rules. Let's
 *  make the interface clear these semantics up. If you need the ctx,
 *  use Parser.getRuleContext().
 */
public interface ParseListener<Symbol extends Token> {
	<T extends Symbol> void visitTerminal(ParserRuleContext<T> parent, T token);

	/** Enter all but left-recursive rules */
	void enterNonLRRule(ParserRuleContext<? extends Symbol> ctx);

	void exitEveryRule(ParserRuleContext<? extends Symbol> ctx);
}
