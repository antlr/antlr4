package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

/** {@code T} is return type of {@code visit} methods. Use {@link Void} for no return type.
 */
public class ParseTreeVisitor<T> {
	public T visit(ParseTree tree) {
		return tree.accept(this);
	}

	/** Visit all rule, non-leaf children. This returns value returned from last
	 *  child visited, losing all computations from first n-1 children.  Works
	 *  fine for contexts with one child then.
	 *  Handy if you are just walking the tree with a visitor and only
	 *  care about some nodes.  The {@link ParserRuleContext#accept} method
	 *  walks all children by default; i.e., calls this method.
	 */
	public T visitChildren(ParseTree.RuleNode node) {
		T result = null;
		int n = node.getChildCount();
		for (int i=0; i<n; i++) {
			ParseTree c = node.getChild(i);
			result = c.accept(this);
		}
		return result;
	}

	public T visitTerminal(ParseTree.TerminalNode<? extends Token> node) { return null; }
	public T visitErrorNode(ParseTree.ErrorNode<? extends Token> node) { return null; }
}
