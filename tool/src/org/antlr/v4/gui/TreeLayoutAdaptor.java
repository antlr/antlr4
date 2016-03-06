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

package org.antlr.v4.gui;

import org.abego.treelayout.TreeForTreeLayout;
import org.antlr.v4.runtime.tree.Tree;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** Adaptor ANTLR trees to {@link TreeForTreeLayout}. */
public class TreeLayoutAdaptor implements TreeForTreeLayout<Tree> {
	private static class AntlrTreeChildrenIterable implements Iterable<Tree> {
		private final Tree tree;

		public AntlrTreeChildrenIterable(Tree tree) {
			this.tree = tree;
		}

		@Override
		public Iterator<Tree> iterator() {
			return new Iterator<Tree>() {
				private int i = 0;

				@Override
				public boolean hasNext() {
					return tree.getChildCount() > i;
				}

				@Override
				public Tree next() {
					if (!hasNext())
						throw new NoSuchElementException();

					return tree.getChild(i++);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	private static class AntlrTreeChildrenReverseIterable implements
		Iterable<Tree>
	{
		private final Tree tree;

		public AntlrTreeChildrenReverseIterable(Tree tree) {
			this.tree = tree;
		}

		@Override
		public Iterator<Tree> iterator() {
			return new Iterator<Tree>() {
				private int i = tree.getChildCount();

				@Override
				public boolean hasNext() {
					return i > 0;
				}

				@Override
				public Tree next() {
					if (!hasNext())
						throw new NoSuchElementException();

					return tree.getChild(--i);
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	private Tree root;

	public TreeLayoutAdaptor(Tree root) {
		this.root = root;
	}

	@Override
	public boolean isLeaf(Tree node) {
		return node.getChildCount() == 0;
	}

	@Override
	public boolean isChildOfParent(Tree node, Tree parentNode) {
		return node.getParent() == parentNode;
	}

	@Override
	public Tree getRoot() {
		return root;
	}

	@Override
	public Tree getLastChild(Tree parentNode) {
		return parentNode.getChild(parentNode.getChildCount() - 1);
	}

	@Override
	public Tree getFirstChild(Tree parentNode) {
		return parentNode.getChild(0);
	}

	@Override
	public Iterable<Tree> getChildrenReverse(Tree node) {
		return new AntlrTreeChildrenReverseIterable(node);
	}

	@Override
	public Iterable<Tree> getChildren(Tree node) {
		return new AntlrTreeChildrenIterable(node);
	}
}
