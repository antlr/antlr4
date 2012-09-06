package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

/** This class defines the basic notion of a parse tree visitor
 *  object. Generated visitors extend this class and implement the XVisitor
 *  interface for grammar X.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class ParseTreeVisitor<T> {
	/** Visit a parse tree, and return a user-defined result of the operation.
	 *
	 * @param tree The {@link ParseTree} to visit.
	 * @return The result of visiting the parse tree.
	 */
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
	public T visitChildren(RuleNode node) {
		T result = null;
		int n = node.getChildCount();
		for (int i=0; i<n; i++) {
			ParseTree c = node.getChild(i);
			result = c.accept(this);
		}
		return result;
	}

	/** Visit a terminal node, and return a user-defined result of the operation.
	 *
	 * @param node The {@link TerminalNode} to visit.
	 * @return The result of visiting the node.
	 */
	public T visitTerminal(TerminalNode<? extends Token> node) { return null; }

	/** Visit an error node, and return a user-defined result of the operation.
	 *
	 * @param node The {@link ErrorNode} to visit.
	 * @return The result of visiting the node.
	 */
	public T visitErrorNode(ErrorNode<? extends Token> node) { return null; }
}
