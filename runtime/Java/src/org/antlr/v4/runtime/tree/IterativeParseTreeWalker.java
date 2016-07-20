/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.tree;

import java.util.ArrayDeque;
import java.util.Deque;

import org.antlr.v4.runtime.misc.IntegerStack;

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
			} else if (currentNode instanceof TerminalNode) {
				listener.visitTerminal((TerminalNode) currentNode);
			} else {
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
