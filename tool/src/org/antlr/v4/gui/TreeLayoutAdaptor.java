/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
