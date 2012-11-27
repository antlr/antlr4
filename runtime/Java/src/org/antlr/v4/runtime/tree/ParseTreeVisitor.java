package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.misc.NotNull;

/**
 * This class defines the basic notion of a parse tree visitor object. Generated
 * visitors extend this class and implement the {@code XVisitor} interface for
 * grammar {@code X}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public class ParseTreeVisitor<T> {

	/**
	 * Visit a parse tree, and return a user-defined result of the operation.
	 * <p/>
	 * The default implementation calls {@link ParseTree#accept} on the
	 * specified tree.
	 *
	 * @param tree The {@link ParseTree} to visit.
	 * @return The result of visiting the parse tree.
	 */
	public T visit(@NotNull ParseTree tree) {
		return tree.accept(this);
	}

	/**
	 * Visit the children of a node, and return a user-defined result of the
	 * operation.
	 * <p/>
	 * The default implementation initializes the aggregate result to
	 * {@link #defaultResult defaultResult}. Before visiting each child, it
	 * calls {@link #shouldVisitNextChild shouldVisitNextChild}; if the result
	 * is {@code false} no more children are visited and the current aggregate
	 * result is returned. After visiting a child, the aggregate result is
	 * updated by calling {@link #aggregateResult aggregateResult} with the
	 * previous aggregate result and the result of visiting the child.
	 *
	 * @param node The {@link RuleNode} whose children should be visited.
	 * @return The result of visiting the children of the node.
	 */
	public T visitChildren(@NotNull RuleNode node) {
		T result = defaultResult();
		int n = node.getChildCount();
		for (int i = 0; i < n; i++) {
			if (!shouldVisitNextChild(node, result)) {
				break;
			}

			ParseTree c = node.getChild(i);
			T childResult = c.accept(this);
			result = aggregateResult(result, childResult);
		}

		return result;
	}

	/**
	 * Visit a terminal node, and return a user-defined result of the operation.
	 * <p/>
	 * The default implementation returns the result of
	 * {@link #defaultResult defaultResult}.
	 *
	 * @param node The {@link TerminalNode} to visit.
	 * @return The result of visiting the node.
	 */
	public T visitTerminal(@NotNull TerminalNode node) {
		return defaultResult();
	}

	/**
	 * Visit an error node, and return a user-defined result of the operation.
	 * <p/>
	 * The default implementation returns the result of
	 * {@link #defaultResult defaultResult}.
	 *
	 * @param node The {@link ErrorNode} to visit.
	 * @return The result of visiting the node.
	 */
	public T visitErrorNode(@NotNull ErrorNode node) {
		return defaultResult();
	}

	/**
	 * Gets the default value returned by visitor methods. This value is
	 * returned by the default implementations of
	 * {@link #visitTerminal visitTerminal} and
	 * {@link #visitErrorNode visitErrorNode}. The default implementation of
	 * {@link #visitChildren visitChildren} initializes its aggregate result to
	 * this value.
	 * <p/>
	 * The default implementation returns {@code null}.
	 *
	 * @return The default value returned by visitor methods.
	 */
	protected T defaultResult() {
		return null;
	}

	/**
	 * Aggregates the results of visiting multiple children of a node. After
	 * either all children are visited or {@link #shouldVisitNextChild} returns
	 * {@code false}, the aggregate value is returned as the result of
	 * {@link #visitChildren}.
	 * <p/>
	 * The default implementation returns {@code nextResult}, meaning
	 * {@link #visitChildren} will return the result of the last child visited
	 * (or return the initial value if the node has no children).
	 *
	 * @param aggregate The previous aggregate value. In the default
	 * implementation, the aggregate value is initialized to
	 * {@link #defaultResult}, which is passed as the {@code aggregate} argument
	 * to this method after the first child node is visited.
	 * @param nextResult The result of the immediately preceeding call to visit
	 * a child node.
	 *
	 * @return The updated aggregate result.
	 */
	protected T aggregateResult(T aggregate, T nextResult) {
		return nextResult;
	}

	/**
	 * This method is called before visiting each child in
	 * {@link #visitChildren}. This method is first called before the first
	 * child is visited; at that point {@code currentResult} will be the initial
	 * value (in the default implementation, the initial value is returned by a
	 * call to {@link #defaultResult}.
	 * <p/>
	 * The default implementation always returns {@code true}, indicating that
	 * {@code visitChildren} should only return after all children are visited.
	 * <p/>
	 * One reason to override this method is to provide a "short circuit"
	 * evaluation option for situations where the result of visiting a single
	 * child has the potential to determine the result of the visit operation as
	 * a whole.
	 *
	 * @param node The {@link RuleNode} whose children are currently being
	 * visited.
	 * @param currentResult The current aggregate result of the children visited
	 * to the current point.
	 *
	 * @return {@code true} to continue visiting children. Otherwise return
	 * {@code false} to stop visiting children and immediately return the
	 * current aggregate result from {@link #visitChildren}.
	 */
	protected boolean shouldVisitNextChild(RuleNode node, T currentResult) {
		return true;
	}
}
