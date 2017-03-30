/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.tree;

/**
 * This interface defines the basic notion of a parse tree visitor. Generated
 * visitors implement this interface and the {@code XVisitor} interface for
 * grammar {@code X}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface ParseTreeVisitor<T> {

	/**
	 * Visit a parse tree, and return a user-defined result of the operation.
	 *
	 * @param tree The {@link ParseTree} to visit.
	 * @return The result of visiting the parse tree.
	 */
	T visit(ParseTree tree);

	/**
	 * Visit the children of a node, and return a user-defined result of the
	 * operation.
	 *
	 * @param node The {@link RuleNode} whose children should be visited.
	 * @return The result of visiting the children of the node.
	 */
	T visitChildren(RuleNode node);

	/**
	 * Visit a terminal node, and return a user-defined result of the operation.
	 *
	 * @param node The {@link TerminalNode} to visit.
	 * @return The result of visiting the node.
	 */
	T visitTerminal(TerminalNode node);

	/**
	 * Visit an error node, and return a user-defined result of the operation.
	 *
	 * @param node The {@link ErrorNode} to visit.
	 * @return The result of visiting the node.
	 */
	T visitErrorNode(ErrorNode node);

}
