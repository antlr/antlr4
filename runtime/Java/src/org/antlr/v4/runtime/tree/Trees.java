/*
 [The "BSD license"]
  Copyright (c) 2011 Terence Parr
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.BaseRecognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.gui.TreePostScriptGenerator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/** A set of utility routines useful for all kinds of ANTLR trees */
public class Trees {

	public static String getPS(Tree t, BaseRecognizer<?> recog,
							   String fontName, int fontSize)
	{
		TreePostScriptGenerator psgen =
			new TreePostScriptGenerator(recog, t, fontName, fontSize);
		return psgen.getPS();
	}

	public static String getPS(Tree t, BaseRecognizer<?> recog) {
		return getPS(t, recog, "Helvetica", 11);
	}

	public static void writePS(Tree t, BaseRecognizer<?> recog,
							   String fileName,
							   String fontName, int fontSize)
		throws IOException
	{
		String ps = getPS(t, recog, fontName, fontSize);
		FileWriter f = new FileWriter(fileName);
		BufferedWriter bw = new BufferedWriter(f);
		bw.write(ps);
		bw.close();
	}

	public static void writePS(Tree t, BaseRecognizer<?> recog, String fileName)
		throws IOException
	{
		writePS(t, recog, fileName, "Helvetica", 11);
	}

	/** Print out a whole tree in LISP form. getNodeText is used on the
	 *  node payloads to get the text for the nodes.  Detect
	 *  parse trees and extract data appropriately.
	 */
	public static String toStringTree(Tree t, BaseRecognizer<?> recog) {
		if ( t.getChildCount()==0 ) return getNodeText(t, recog);
		StringBuilder buf = new StringBuilder();
		boolean nilRoot = t instanceof AST && ((AST)t).isNil();
		if ( !nilRoot ) {
			buf.append("(");
			buf.append(getNodeText(t, recog));
			buf.append(' ');
		}
		for (int i = 0; i<t.getChildCount(); i++) {
			if ( i>0 ) buf.append(' ');
			buf.append(toStringTree(t.getChild(i), recog));
		}
		if ( !nilRoot ) {
			buf.append(")");
		}
		return buf.toString();
	}

	public static <TSymbol> String getNodeText(Tree t, BaseRecognizer<TSymbol> recog) {
		if ( t instanceof AST ) {
			return t.toString();
		}
		if ( recog!=null ) {
			if ( t instanceof ParseTree.RuleNode ) {
				int ruleIndex = ((ParseTree.RuleNode)t).getRuleContext().getRuleIndex();
				String ruleName = recog.getRuleNames()[ruleIndex];
				return ruleName;
			}
			else if ( t instanceof ParseTree.ErrorNodeImpl ) {
				return t.toString();
			}
			else if ( t instanceof ParseTree.TerminalNode) {
				Object symbol = ((ParseTree.TerminalNode<?>)t).getSymbol();
				if (symbol instanceof Token) {
					return ((Token)symbol).getText();
				}
			}
		}
		// not AST and no recog for rule names
		Object payload = t.getPayload();
		if ( payload instanceof Token ) {
			return ((Token)payload).getText();
		}
		return t.getPayload().toString();
	}

	/** Walk upwards and get first ancestor with this token type. */
	public static AST getAncestor(AST t, int ttype) {
		t = t.getParent();
		while ( t!=null ) {
			if ( t.getType()==ttype ) return t;
			t = t.getParent();
		}
		return null;
	}

	/** Return a list of all ancestors of this node.  The first node of
	 *  list is the root and the last is the parent of this node.
	 */
	public static List<? extends Tree> getAncestors(Tree t) {
		if ( t.getParent()==null ) return null;
		List<Tree> ancestors = new ArrayList<Tree>();
		t = t.getParent();
		while ( t!=null ) {
			ancestors.add(0, t); // insert at start
			t = t.getParent();
		}
		return ancestors;
	}

	public static AST getFirstChildWithType(AST t, int type) {
		for (int i = 0; i<t.getChildCount(); i++) {
			AST c = (AST)t.getChild(i);
			if ( c.getType()==type ) {
				return c;
			}
		}
		return null;
	}

	/** Delete children from start to stop and replace with t even if t is
	 *  a list (nil-root tree).  num of children can increase or decrease.
	 *  For huge child lists, inserting children can force walking rest of
	 *  children to set their childindex; could be slow.
	 */
	public static void replaceChildren(BaseAST tree, int startChildIndex, int stopChildIndex, Object t) {
		/*
		System.out.println("replaceChildren "+startChildIndex+", "+stopChildIndex+
						   " with "+((BaseTree)t).toStringTree());
		System.out.println("in="+toStringTree());
		*/
		if ( tree.getChildCount()==0 ) {
			throw new IllegalArgumentException("indexes invalid; no children in list");
		}
		int replacingHowMany = stopChildIndex - startChildIndex + 1;
		int replacingWithHowMany;
		BaseAST newTree = (BaseAST)t;
		List<BaseAST> newChildren;
		// normalize to a list of children to add: newChildren
		if ( newTree.isNil() ) {
			newChildren = newTree.children;
		}
		else {
			newChildren = new ArrayList<BaseAST>(1);
			newChildren.add(newTree);
		}
		replacingWithHowMany = newChildren.size();
		int numNewChildren = newChildren.size();
		int delta = replacingHowMany - replacingWithHowMany;
		// if same number of nodes, do direct replace
		if ( delta == 0 ) {
			int j = 0; // index into new children
			for (int i=startChildIndex; i<=stopChildIndex; i++) {
				BaseAST child = newChildren.get(j);
				tree.setChild(i, child);
				child.setParent(tree);
				child.setChildIndex(i);
                j++;
            }
		}
		else if ( delta > 0 ) { // fewer new nodes than there were
			// set children and then delete extra
			for (int j=0; j<numNewChildren; j++) {
				tree.setChild(startChildIndex + j, newChildren.get(j));
			}
			int indexToDelete = startChildIndex+numNewChildren;
			for (int c=indexToDelete; c<=stopChildIndex; c++) {
				// delete same index, shifting everybody down each time
				tree.children.remove(indexToDelete);
			}
			tree.freshenParentAndChildIndexes(startChildIndex);
		}
		else { // more new nodes than were there before
			// fill in as many children as we can (replacingHowMany) w/o moving data
			for (int j=0; j<replacingHowMany; j++) {
				tree.children.set(startChildIndex+j, newChildren.get(j));
			}
			int numToInsert = replacingWithHowMany-replacingHowMany;
			for (int j=replacingHowMany; j<replacingWithHowMany; j++) {
				tree.children.add(startChildIndex+j, newChildren.get(j));
			}
			tree.freshenParentAndChildIndexes(startChildIndex);
		}
		//System.out.println("out="+toStringTree());
	}

	public static <T> T dupTree(ASTAdaptor<T> adaptor, T t, T parent) {
		if ( t==null ) {
			return null;
		}
		T newTree = adaptor.dupNode(t);
		// ensure new subtree root has parent/child index set
		adaptor.setChildIndex(newTree, adaptor.getChildIndex(t)); // same index in new tree
		adaptor.setParent(newTree, parent);
		int n = adaptor.getChildCount(t);
		for (int i = 0; i < n; i++) {
			T child = adaptor.getChild(t, i);
			T newSubTree = dupTree(adaptor, child, t);
			adaptor.addChild(newTree, newSubTree);
		}
		return newTree;
	}

	/** For every node in this subtree, make sure it's start/stop token's
	 *  are set.  Walk depth first, visit bottom up.  Only updates nodes
	 *  with at least one token index < 0.
	 */
	public static void setUnknownTokenBoundaries(CommonAST t) {
		if ( t.children==null ) {
			if ( t.startIndex<0 || t.stopIndex<0 ) {
				t.startIndex = t.stopIndex = t.token.getTokenIndex();
			}
			return;
		}
		int n = t.getChildCount();
		for (int i = 0; i < n; i++) {
			setUnknownTokenBoundaries(t.getChild(i));
		}
		if ( t.startIndex>=0 && t.stopIndex>=0 ) return; // already set
		if ( t.getChildCount() > 0 ) {
			CommonAST firstChild = t.getChild(0);
			CommonAST lastChild = t.getChild(t.getChildCount()-1);
			t.startIndex = firstChild.getTokenStartIndex();
			t.stopIndex = lastChild.getTokenStopIndex();
		}
	}

	public static AST getFirstDescendantWithType(AST t, final int type) {
		return getFirstDescendantWithType(t, new TreeSet<Integer>() {{add(type);}} );
	}

	// TODO: don't include this node!!
	public static AST getFirstDescendantWithType(AST t, Set<Integer> types) {
		if ( types.contains(t.getType()) ) return t;
		if ( t.getChildCount()==0 ) return null;
		int n = t.getChildCount();
		for (int i = 0; i < n; i++) {
			AST c = (AST)t.getChild(i);
			if ( types.contains(c.getType()) ) return c;
			AST d = getFirstDescendantWithType(c, types);
			if ( d!=null ) return d;
		}
		return null;
	}

	public static void sanityCheckParentAndChildIndexes(BaseAST t) {
		sanityCheckParentAndChildIndexes(t, null, -1);
	}

	public static void sanityCheckParentAndChildIndexes(BaseAST t, AST parent, int i) {
		if ( parent!=t.getParent() ) {
			throw new IllegalStateException("parents don't match; expected "+
											parent+" found "+t.getParent());
		}
		if ( i!=t.getChildIndex() ) {
			throw new IllegalStateException("child index of "+t.toStringTree()+
											" doesn't match in "+
											parent.toStringTree()+
											"; expected "+i+" found "+
											t.getChildIndex());
		}
		int n = t.getChildCount();
		for (int c = 0; c < n; c++) {
			BaseAST child = t.getChild(c);
			sanityCheckParentAndChildIndexes(child, t, c);
		}
	}

}
