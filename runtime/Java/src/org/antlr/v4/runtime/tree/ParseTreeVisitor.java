package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

/** T is return type of visit methods. Use T=Void for no return type. */
public class ParseTreeVisitor<T> {
	public T visit(ParserRuleContext<?> ctx) {
		return ctx.dispatch(this);
	}

	/** Visit all rule, nonleaf children */
	public <Symbol> void visitChildren(ParserRuleContext<Symbol> ctx) {
		for (ParseTree c : ctx.children) {
			if ( c instanceof ParseTree.RuleNode) {
				ParseTree.RuleNode r = (ParseTree.RuleNode)c;
				ParserRuleContext<Symbol> rctx = (ParserRuleContext<Symbol>)r.getRuleContext();
				rctx.dispatch(this);
			}
		}
	}

//  Don't need to visit tokens. each visit() method deals with it's leaf children
//	public T visit(Token t) {
//		return null;
//	}
}
