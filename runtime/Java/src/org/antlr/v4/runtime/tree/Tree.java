/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;

/** The basic notion of a tree has a parent, a payload, and a list of children.
 *  It is the most abstract interface for all the trees used by ANTLR.
 */
public interface Tree {
	/** The parent of this node. If the return value is null, then this
	 *  node is the root of the tree.
	 */
	Tree getParent();

	/**
	 * This method returns whatever object represents the data at this note. For
	 * example, for parse trees, the payload can be a {@link Token} representing
	 * a leaf node or a {@link RuleContext} object representing a rule
	 * invocation. For abstract syntax trees (ASTs), this is a {@link Token}
	 * object.
	 */
	Object getPayload();

	/** If there are children, get the {@code i}th value indexed from 0. */
	Tree getChild(int i);

	/** How many children are there? If there is none, then this
	 *  node represents a leaf node.
	 */
	int getChildCount();

	/** Print out a whole tree, not just a node, in LISP format
	 *  {@code (root child1 .. childN)}. Print just a node if this is a leaf.
	 */
	String toStringTree();
}
