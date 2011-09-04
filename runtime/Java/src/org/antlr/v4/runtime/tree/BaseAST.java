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

import java.util.*;

/** A generic tree implementation with no payload.  You must subclass to
 *  actually have any user data.  ANTLR v3 uses a list of children approach
 *  instead of the child-sibling approach in v2.  A flat tree (a list) is
 *  an empty node whose children represent the list.  An empty, but
 *  non-null node is called "nil".
 */
public abstract class BaseAST implements AST {
	/** Who is the parent node of this node; if null, implies node is root */
	public BaseAST parent;

	protected List<BaseAST> children;

	/** What index is this node in the child list? Range: 0..n-1 */
	public int childIndex = -1;

	public BaseAST() {
	}

	/** Create a new node from an existing node does nothing for BaseTree
	 *  as there are no fields other than the children list, which cannot
	 *  be copied as the children are not considered part of this node.
	 */
	public BaseAST(AST node) {
	}

	public BaseAST getChild(int i) {
		if ( children==null || i>=children.size() ) {
			return null;
		}
		return children.get(i);
	}

	/** Get the children internal List; note that if you directly mess with
	 *  the list, do so at your own risk.
	 */
	public List<BaseAST> getChildren() { return children; }

	public AST getFirstChildWithType(int type) {
		return Trees.getFirstChildWithType(this, type);
	}

	public int getChildCount() {
		if ( children==null ) return 0;
		return children.size();
	}

	/** Add t as child of this node.
	 *
	 *  Warning: if t has no children, but child does
	 *  and child isNil then this routine moves children to t via
	 *  t.children = child.children; i.e., without copying the array.
	 */
	public void addChild(BaseAST t) {
		//System.out.println("add child "+t.toStringTree()+" "+this.toStringTree());
		//System.out.println("existing children: "+children);
		if ( t==null ) {
			return; // do nothing upon addChild(null)
		}
		BaseAST childTree = (BaseAST)t;
		if ( childTree.isNil() ) { // t is an empty node possibly with children
			if ( this.children!=null && this.children == childTree.children ) {
				throw new RuntimeException("attempt to add child list to itself");
			}
			// just add all of childTree's children to this
			if ( childTree.children!=null ) {
				if ( this.children!=null ) { // must copy, this has children already
					int n = childTree.children.size();
					for (int i = 0; i < n; i++) {
						BaseAST c = childTree.children.get(i);
						this.children.add(c);
						// handle double-link stuff for each child of nil root
						c.setParent(this);
						c.setChildIndex(children.size()-1);
					}
				}
				else {
					// no children for this but t has children; just set pointer
					// call general freshener routine
					this.children = childTree.children;
					this.freshenParentAndChildIndexes();
				}
			}
		}
		else { // child is not nil (don't care about children)
			if ( children==null ) {
				children = createChildrenList(); // create children list on demand
			}
			children.add(t);
			childTree.setParent(this);
			childTree.setChildIndex(children.size()-1);
		}
		// System.out.println("now children are: "+children);
	}

	/** Add all elements of kids list as children of this node */
	public void addChildren(List<? extends BaseAST> kids) {
		if ( kids==null ) return;
		for (int i = 0; i < kids.size(); i++) {
			BaseAST t = kids.get(i);
			addChild(t);
		}
	}

	public void setChild(int i, BaseAST t) {
		if ( t==null ) {
			return;
		}
		if ( t.isNil() ) {
			throw new IllegalArgumentException("Can't set single child to a list");
		}
		if ( children==null ) {
			children = createChildrenList();
		}
		children.set(i, t);
		t.setParent(this);
		t.setChildIndex(i);
	}

	public int getChildIndex() {
		return childIndex;
	}

	public AST getParent() {
		return parent;
	}

	public void setParent(BaseAST t) {
		this.parent = t;
	}

	public void setChildIndex(int index) {
		this.childIndex = index;
	}

	public Object deleteChild(int i) {
		if ( children==null ) {
			return null;
		}
		AST killed = (AST)children.remove(i);
		// walk rest and decrement their child indexes
		this.freshenParentAndChildIndexes(i);
		return killed;
	}

	public boolean deleteChild(BaseAST t) {
		for (int i=0; i<children.size(); i++) {
			Object c = children.get(i);
			if ( c == t ) {
				deleteChild(t.getChildIndex());
				return true;
			}
		}
		return false;
	}

	/** Insert child t at child position i (0..n-1) by shifting children
		i+1..n-1 to the right one position. Set parent / indexes properly
	 	but does NOT collapse nil-rooted t's that come in here like addChild.
	 */
	public void insertChild(int i, BaseAST t) {
		if (i < 0 || i >= getChildCount()) {
			throw new IndexOutOfBoundsException(i+" out or range");
		}

		children.add(i, t);
		// walk others to increment their child indexes
		// set index, parent of this one too
		this.freshenParentAndChildIndexes(i);
	}

	/** Override in a subclass to change the impl of children list */
	protected List<BaseAST> createChildrenList() {
		return new ArrayList<BaseAST>();
	}

	public boolean isNil() {
		return false;
	}

	/** Set the parent and child index values for all child of t */
	public void freshenParentAndChildIndexes() {
		freshenParentAndChildIndexes(0);
	}

	public void freshenParentAndChildIndexes(int offset) {
		int n = getChildCount();
		for (int c = offset; c < n; c++) {
			BaseAST child = getChild(c);
			child.setChildIndex(c);
			child.setParent(this);
		}
	}

	public void freshenParentAndChildIndexesDeeply() {
		freshenParentAndChildIndexesDeeply(0);
	}

	public void freshenParentAndChildIndexesDeeply(int offset) {
		int n = getChildCount();
		for (int c = offset; c < n; c++) {
			BaseAST child = (BaseAST)getChild(c);
			child.setChildIndex(c);
			child.setParent(this);
			child.freshenParentAndChildIndexesDeeply();
		}
	}

    /** Walk upwards looking for ancestor with this token type. */
    public boolean hasAncestor(int ttype) { return getAncestor(ttype)!=null; }

    /** Walk upwards and get first ancestor with this token type. */
	public Tree getAncestor(int ttype) { return Trees.getAncestor(this, ttype); }

    /** Return a list of all ancestors of this node.  The first node of
     *  list is the root and the last is the parent of this node.
     */
    public List<? extends Tree> getAncestors() { return Trees.getAncestors(this); }

	/** Don't use standard tree printing mechanism since ASTs can have nil
	 *  root nodes.
	 */
    public String toStringTree() {
		if ( children==null || children.size()==0 ) {
			return this.toString();
		}
		StringBuffer buf = new StringBuffer();
		if ( !isNil() ) {
			buf.append("(");
			buf.append(this.toString());
			buf.append(' ');
		}
		for (int i = 0; children!=null && i < children.size(); i++) {
			AST t = children.get(i);
			if ( i>0 ) {
				buf.append(' ');
			}
			buf.append(t.toStringTree());
		}
		if ( !isNil() ) {
			buf.append(")");
		}
		return buf.toString();
	}
}
