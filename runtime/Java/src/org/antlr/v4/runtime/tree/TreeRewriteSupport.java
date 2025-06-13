/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.tree;


import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

/** This class encapsulates code necessary to support tree rewriting
 *  using visitors. To rewrite trees using ANTLR-generated visitors,
 *  override two key methods:
 * <pre>
	 public class MyTreeRewriter extends MyParserNameBaseVisitor<ParseTree> {
	    @Override
	    public ParseTree visitChildren(RuleNode node) {
	        return TreeRewriteSupport.rewriteChildren(this, node);
	    }

	    @Override
	    public ParseTree visitTerminal(TerminalNode node) {
	        return node;
	    }

		@Override
		public ParseTree visitErrorNode(ErrorNode node) {
		    return node;
		}
		... any/all of your visitX() methods for rules in MyParserName ...
	 }
 </pre>
 *
 *  See also TestTestRewriteVisitors.
 *
 *  The visitor event methods return either the same node, if no change,
 *  or a node/subtree to replace the event method parameter. Null means
 *  delete that subtree/leaf.
 *
 *  The tree returned from the overall visit() method will be a
 *  maximally-sharing copy of the original. I.e., it treats the original
 *  tree as immutable.  Replacing a child node of t will force a copy of t
 *  so its children list can be modified w/o altering original tree. That
 *  implies a copy is made of all nodes on the path from t to the root
 *  of the tree (the spine).
 *
 *  The start and stop token parse tree fields are not altered in any way.
 *  They are meaningless after altering the tree.  Use node's getText()
 *  to get text of rewrite trees, not getText(RuleContext) from token stream,
 *  which relies on node start/stop fields.
 *
 *  @since 4.6.1
 */
public class TreeRewriteSupport {
	public static ParseTree rewriteChildren(ParseTreeVisitor<ParseTree> visitor, RuleNode tree) {
		ParserRuleContext resultTree = (ParserRuleContext)tree;
		int n = tree.getChildCount();
		int nextChildToAlter = 0; // because of deletions, next child might not be i
		for (int i=0; i<n; i++) {
			ParseTree c = tree.getChild(i);
			// visit child so we can make substitutions in child before replacing it as child of this node
			ParseTree replacement = visitor.visit(c);
			if ( replacement != c ) {
				// To alter child list, we make copy of this node and set child in the copy
				if ( resultTree==tree ) { // Only make one copy
					resultTree = copy((ParserRuleContext)tree);
				}
				if ( replacement!=null ) {
					_setChild(resultTree, nextChildToAlter, replacement);
				}
				else {
					resultTree.children.remove(nextChildToAlter);
					nextChildToAlter--; // to delete, we adjust so nextChildToAlter doesn't advance
				}
			}
			nextChildToAlter++; // track i unless deletion
		}

		return resultTree; // return original or possibly copy of original
	}

	/** Hook replacement in as tree's ith child, set parent of new child properly.
	 *  If replacement is a token node, make copy of it before setting parent
	 *  if not a clone already (parent is null).
	 *
	 *  This is not meant as general API for users. It is destructive to tree
	 *  argument, which we assume is a copy made from original incoming tree.
	 */
	protected static void _setChild(RuleNode tree, int i, ParseTree replacement) {
		if ( replacement instanceof TerminalNodeImpl ) { // must assume an implementation to alter
			TerminalNodeImpl trepl = (TerminalNodeImpl)replacement;
			if ( trepl.getParent()!=null ) {
				// uh oh, it's not a copy. cannot alter so dup first
				// the parent will likely be null, for example, when
				// visitTerminal() returns a copy of a node rather
				// than the actual node.
				trepl = Trees.shallowCopy(trepl);
			}
			trepl.parent = tree;
		}
		else {
			((ParserRuleContext)replacement).parent = (ParserRuleContext)tree;
		}
		((ParserRuleContext)tree).children.set(i, replacement);
	}

	/** Set child of tree, making copy of tree; return the new tree. The
	 *  parent of replacement subtree is set to the copy of tree.  No effort
	 *  is made to prevent tree cycles under replacement subtree.
	 */
	public static ParserRuleContext setChild(RuleNode tree, int i, ParseTree replacement) {
		ParserRuleContext resultTree = copy((ParserRuleContext)tree);
		_setChild(resultTree, i, replacement);
		return resultTree;
	}

	public static ParserRuleContext setChildren(RuleNode tree, List<ParseTree> children) {
		if ( children!=null && children.size()==0 ) children = null;
		ParserRuleContext resultTree = Trees.shallowCopy((ParserRuleContext)tree);
		if ( resultTree.children==null ) { // cpy's children could be non-null already if copyFrom copies error children; don't overwrite
			if ( children!=null ) {
				resultTree.children = new ArrayList<>();
				resultTree.children.addAll(children);
			}
		}
		else {
			resultTree.children = children;
		}
		return resultTree;
	}

	/** Remove the ith child of tree, making copy of tree; return the new tree */
	public static ParserRuleContext deleteChild(RuleNode tree, int i) {
		ParserRuleContext resultTree = copy((ParserRuleContext)tree);
		resultTree.children.remove(i);
		return resultTree;
	}

	/** Copy a rule node and its children list (shallow). The parent of
	 *  the children is not updated.
	 */
	public static ParserRuleContext copy(ParserRuleContext t) {
		ParserRuleContext cpy = Trees.shallowCopy(t);
		if ( cpy.children==null ) { // cpy's children could be non-null already if copyFrom copies error children; don't overwrite
			cpy.children = new ArrayList<>();
		}
		cpy.children.addAll(t.children); // copy all children
		return cpy;
	}

	/** Walk tree and update each subtree of t so t's children point at t.
	 *  Because there is only a single parent pointer, tree rewriting leaves
	 *  the parent pointing at the original parse tree. After a number of
	 *  transformations, one might want the parent pointers to be
	 *  consistent with the final rewritten tree.
	 */
	public static void updateParentPointers(ParseTree tree) {
		// TODO
	}

	public static TerminalNode copy(TerminalNode t) { return Trees.shallowCopy(t); }

	private TreeRewriteSupport() { }
}
