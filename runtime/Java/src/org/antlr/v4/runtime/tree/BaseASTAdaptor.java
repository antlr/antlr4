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

import org.antlr.v4.runtime.*;

import java.util.*;

/** An ASTAdaptor that works with any BaseAST implementation. */
public abstract class BaseASTAdaptor<T extends BaseAST> implements ASTAdaptor<T> {
	/** System.identityHashCode() is not always unique; we have to
	 *  track ourselves.  That's ok, it's only for debugging, though it's
	 *  expensive: we have to create a hashtable with all tree nodes in it.
	 *  TODO: rm?
	 */
	protected Map<BaseAST, Integer> treeToUniqueIDMap;
	protected int uniqueNodeID = 1;

	@Override
	public List<T> createElementList() {
		return new ElementList<T>(this);
	}

	// END v4 stuff

	@Override
	public T nil() {
		return create(null);
	}

	@Override
	public boolean isNil(T tree) {
		return tree.isNil();
	}

	@Override
	public T dupTree(T tree) {
		return dupTree(tree, null);
	}

	/** This is generic in the sense that it will work with any kind of
	 *  tree (not just AST interface).  It invokes the adaptor routines
	 *  not the tree node routines to do the construction.
	 */
	public T dupTree(T t, T parent) {
		if ( t==null ) {
			return null;
		}
		T newTree = dupNode(t);
		// ensure new subtree root has parent/child index set
		setChildIndex(newTree, getChildIndex(t)); // same index in new tree
		setParent(newTree, parent);
		int n = getChildCount(t);
		for (int i = 0; i < n; i++) {
			T child = getChild(t, i);
			T newSubTree = dupTree(child, t);
			addChild(newTree, newSubTree);
		}
		return newTree;
	}

	/** Add a child to the tree t.  If child is a flat tree (a list), make all
	 *  in list children of t.  Warning: if t has no children, but child does
	 *  and child isNil then you can decide it is ok to move children to t via
	 *  t.children = child.children; i.e., without copying the array.  Just
	 *  make sure that this is consistent with have the user will build
	 *  ASTs.
	 */
	@Override
	public void addChild(T t, T child) {
		if ( t!=null && child!=null ) {
			t.addChild(child);
		}
	}

	/** If oldRoot is a nil root, just copy or move the children to newRoot.
	 *  If not a nil root, make oldRoot a child of newRoot.
	 *
	 *    old=^(nil a b c), new=r yields ^(r a b c)
	 *    old=^(a b c), new=r yields ^(r ^(a b c))
	 *
	 *  If newRoot is a nil-rooted single child tree, use the single
	 *  child as the new root node.
	 *
	 *    old=^(nil a b c), new=^(nil r) yields ^(r a b c)
	 *    old=^(a b c), new=^(nil r) yields ^(r ^(a b c))
	 *
	 *  If oldRoot was null, it's ok, just return newRoot (even if isNil).
	 *
	 *    old=null, new=r yields r
	 *    old=null, new=^(nil r) yields ^(nil r)
	 *
	 *  Return newRoot.  Throw an exception if newRoot is not a
	 *  simple node or nil root with a single child node--it must be a root
	 *  node.  If newRoot is ^(nil x) return x as newRoot.
	 *
	 *  Be advised that it's ok for newRoot to point at oldRoot's
	 *  children; i.e., you don't have to copy the list.  We are
	 *  constructing these nodes so we should have this control for
	 *  efficiency.
	 */
	@Override
	public T becomeRoot(T newRoot, T oldRoot) {
        //System.out.println("becomeroot new "+newRoot.toString()+" old "+oldRoot);
		if ( oldRoot==null ) {
			return newRoot;
		}
		// handle ^(nil real-node)
		if ( newRoot.isNil() ) {
            int nc = newRoot.getChildCount();
            if ( nc==1 ) newRoot = (T)newRoot.getChild(0);
            else if ( nc >1 ) {
				// TODO: make tree run time exceptions hierarchy
				throw new RuntimeException("more than one node as root (TODO: make exception hierarchy)");
			}
        }
		// add oldRoot to newRoot; addChild takes care of case where oldRoot
		// is a flat list (i.e., nil-rooted tree).  All children of oldRoot
		// are added to newRoot.
		newRoot.addChild(oldRoot);
		return newRoot;
	}

	/** Transform ^(nil x) to x and nil to null */
	@Override
	public T rulePostProcessing(T root) {
		//System.out.println("rulePostProcessing: "+((AST)root).toStringTree());
		if ( root!=null && root.isNil() ) {
			if ( root.getChildCount()==0 ) {
				root = null;
			}
			else if ( root.getChildCount()==1 ) {
				root = (T)root.getChild(0);
				// whoever invokes rule will set parent and child index
				root.setParent(null);
				root.setChildIndex(-1);
			}
		}
		return root;
	}

	@Override
	public T becomeRoot(Token newRoot, T oldRoot) {
		return becomeRoot(create(newRoot), oldRoot);
	}

	@Override
	public T create(int tokenType, Token fromToken) {
		WritableToken tok = createToken(fromToken);
		//((ClassicToken)fromToken).setType(tokenType);
		tok.setType(tokenType);
		return create(tok);
	}

	@Override
	public T create(int tokenType, Token fromToken, String text) {
        if (fromToken == null) return create(tokenType, text);
		WritableToken tok = createToken(fromToken);
		tok.setType(tokenType);
		tok.setText(text);
		return create(tok);
	}

	@Override
	public T create(int tokenType, String text) {
		Token fromToken = createToken(tokenType, text);
		return create(fromToken);
	}

	@Override
	public int getType(T t) {
		return t.getType();
	}

	@Override
	public void setType(T t, int type) {
		throw new UnsupportedOperationException("don't know enough about AST node");
	}

	@Override
	public String getText(T t) {
		return t.getText();
	}

	@Override
	public void setText(T t, String text) {
		throw new UnsupportedOperationException("don't know enough about AST node");
	}

	@Override
	public T getChild(T t, int i) {
		return (T)t.getChild(i);
	}

	@Override
	public void setChild(T t, int i, T child) {
		t.setChild(i, child);
	}

	@Override
	public T deleteChild(T t, int i) {
		return (T)t.deleteChild(i);
	}

	@Override
	public int getChildCount(T t) {
		return t.getChildCount();
	}

	@Override
	public int getUniqueID(T node) {
		if ( treeToUniqueIDMap==null ) {
			 treeToUniqueIDMap = new HashMap<BaseAST, Integer>();
		}
		Integer prevID = treeToUniqueIDMap.get(node);
		if ( prevID!=null ) {
			return prevID;
		}
		int ID = uniqueNodeID;
		treeToUniqueIDMap.put(node, ID);
		uniqueNodeID++;
		return ID;
		// GC makes these nonunique:
		// return System.identityHashCode(node);
	}

	/** Tell me how to create a token for use with imaginary token nodes.
	 *  For example, there is probably no input symbol associated with imaginary
	 *  token DECL, but you need to create it as a payload or whatever for
	 *  the DECL node as in ^(DECL type ID).
	 *
	 *  If you care what the token payload objects' type is, you should
	 *  override this method and any other createToken variant.
	 */
	public abstract WritableToken createToken(int tokenType, String text);

	/** Tell me how to create a token for use with imaginary token nodes.
	 *  For example, there is probably no input symbol associated with imaginary
	 *  token DECL, but you need to create it as a payload or whatever for
	 *  the DECL node as in ^(DECL type ID).
	 *
	 *  This is a variant of createToken where the new token is derived from
	 *  an actual real input token.  Typically this is for converting '{'
	 *  tokens to BLOCK etc...  You'll see
	 *
	 *    r : lc='{' ID+ '}' -> ^(BLOCK[$lc] ID+) ;
	 *
	 *  If you care what the token payload objects' type is, you should
	 *  override this method and any other createToken variant.
	 */
	public abstract WritableToken createToken(Token fromToken);
}

