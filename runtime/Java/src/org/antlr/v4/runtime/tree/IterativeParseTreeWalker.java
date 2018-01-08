/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.misc.IntegerStack;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An iterative (read: non-recursive) pre-order and post-order tree walker that
 * doesn't use the thread stack but heap-based stacks. Makes it possible to
 * process deeply nested parse trees.
 */
public class IterativeParseTreeWalker extends ParseTreeWalker {

	@Override
	public void walk(ParseTreeListener listener, ParseTree t) {

		final Deque<ParseTree> nodeStack = new ArrayDeque<ParseTree>();
		final IntegerStack indexStack = new IntegerStack();

		ParseTree currentNode = t;
		int currentIndex = 0;

		while (currentNode != null) {

			// pre-order visit
			if (currentNode instanceof ErrorNode) {
				listener.visitErrorNode((ErrorNode) currentNode);
			}
			else if (currentNode instanceof TerminalNode) {
				listener.visitTerminal((TerminalNode) currentNode);
			}
			else {
				final RuleNode r = (RuleNode) currentNode;
				enterRule(listener, r);
			}

			// Move down to first child, if exists
			if (currentNode.getChildCount() > 0) {
				nodeStack.push(currentNode);
				indexStack.push(currentIndex);
				currentIndex = 0;
				currentNode = currentNode.getChild(0);
				continue;
			}

			// No child nodes, so walk tree
			do {

				// post-order visit
				if (currentNode instanceof RuleNode) {
					exitRule(listener, (RuleNode) currentNode);
				}

				// No parent, so no siblings
				if (nodeStack.isEmpty()) {
					currentNode = null;
					currentIndex = 0;
					break;
				}

				// Move to next sibling if possible
				currentNode = nodeStack.peek().getChild(++currentIndex);
				if (currentNode != null) {
					break;
				}

				// No next, sibling, so move up
				currentNode = nodeStack.pop();
				currentIndex = indexStack.pop();

			} while (currentNode != null);
		}
	}
}
