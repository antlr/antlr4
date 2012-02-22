package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

/** T is return type of visit methods. Use T=Void for no return type. */
public class ParseTreeVisitor<T> {
	public T visit(ParserRuleContext<?> ctx) {
		return ctx.accept(this);
	}

	/** Visit all rule, nonleaf children. Not that useful if you are using T as
	 *  non-Void.  This returns value returned from last child visited,
	 *  losing all computations from first n-1 children.  Works fine for
	 *  ctxs with one child then.
	 *  Handy if you are just walking the tree with a visitor and only
	 *  care about some nodes.  The ParserRuleContext.accept() method
	 *  walks all children by default; i.e., calls this method.
	 */
	public T visitChildren(ParserRuleContext<? extends Token> ctx) {
		T result = null;
		for (ParseTree c : ctx.children) {
			if ( c instanceof ParseTree.RuleNode) {
				ParseTree.RuleNode r = (ParseTree.RuleNode)c;
				ParserRuleContext<?> rctx = (ParserRuleContext<? extends Token>)r.getRuleContext();
				result = visit(rctx);
			}
			else {
				result = visitTerminal(ctx, ((ParseTree.TerminalNode<? extends Token>)c).getSymbol());
			}
		}
		return result;
	}

	public T visitTerminal(ParserRuleContext<? extends Token> ctx, Token symbol) {
		return null;
	}
}
