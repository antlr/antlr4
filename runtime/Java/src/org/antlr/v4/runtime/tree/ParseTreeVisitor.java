package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;

/** T is return type of visit methods. Use T=Void for no return type. */
public class ParseTreeVisitor<T> {
	public T visit(ParserRuleContext<?> ctx) {
		return ctx.accept(this);
	}

	/** Visit all rule, nonleaf children. Not useful if you are using T as
	 *  non-Void.  This returns nothing, losing all computations from below.
	 *  But handy if you are just walking the tree with a visitor and only
	 *  care about some nodes.  The ParserRuleContext.accept() method
	 *  walks all children by default; i.e., calls this method.
	 */
	public <Symbol> void visitChildren(ParserRuleContext<Symbol> ctx) {
		for (ParseTree c : ctx.children) {
			if ( c instanceof ParseTree.RuleNode) {
				ParseTree.RuleNode r = (ParseTree.RuleNode)c;
				ParserRuleContext<Symbol> rctx = (ParserRuleContext<Symbol>)r.getRuleContext();
				visit(rctx);
			}
		}
	}
}
