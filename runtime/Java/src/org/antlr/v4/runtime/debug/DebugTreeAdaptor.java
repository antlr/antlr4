/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
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
package org.antlr.v4.runtime.debug;

import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TreeAdaptor;

import java.util.List;

/** A TreeAdaptor proxy that fires debugging events to a DebugEventListener
 *  delegate and uses the TreeAdaptor delegate to do the actual work.  All
 *  AST events are triggered by this adaptor; no code gen changes are needed
 *  in generated rules.  Debugging events are triggered *after* invoking
 *  tree adaptor routines.
 *
 *  Trees created with actions in rewrite actions like "-> ^(ADD {foo} {bar})"
 *  cannot be tracked as they might not use the adaptor to create foo, bar.
 *  The debug listener has to deal with tree node IDs for which it did
 *  not see a createNode event.  A single <unknown> node is sufficient even
 *  if it represents a whole tree.
 */
public class DebugTreeAdaptor implements TreeAdaptor {
	protected DebugEventListener dbg;
	protected TreeAdaptor adaptor;

	public DebugTreeAdaptor(DebugEventListener dbg, TreeAdaptor adaptor) {
		this.dbg = dbg;
		this.adaptor = adaptor;
	}

	public Object create(Token payload) {
		if ( payload.getTokenIndex() < 0 ) {
			// could be token conjured up during error recovery
			return create(payload.getType(), payload.getText());
		}
		Object node = adaptor.create(payload);
		dbg.createNode(node, payload);
		return node;
	}

	public List<Object> createElementList() {
		return adaptor.createElementList();
	}

	public Object errorNode(TokenStream input, Token start, Token stop,
							RecognitionException e)
	{
		Object node = adaptor.errorNode(input, start, stop, e);
		if ( node!=null ) {
			dbg.errorNode(node);
		}
		return node;
	}

	public Object dupTree(Object tree) {
		Object t = adaptor.dupTree(tree);
		// walk the tree and emit create and add child events
		// to simulate what dupTree has done. dupTree does not call this debug
		// adapter so I must simulate.
		simulateTreeConstruction(t);
		return t;
	}

	/** ^(A B C): emit create A, create B, add child, ...*/
	protected void simulateTreeConstruction(Object t) {
		dbg.createNode(t);
		int n = adaptor.getChildCount(t);
		for (int i=0; i<n; i++) {
			Object child = adaptor.getChild(t, i);
			simulateTreeConstruction(child);
			dbg.addChild(t, child);
		}
	}

	public Object dupNode(Object treeNode) {
		Object d = adaptor.dupNode(treeNode);
		dbg.createNode(d);
		return d;
	}

	public Object nil() {
		Object node = adaptor.nil();
		dbg.nilNode(node);
		return node;
	}

	public boolean isNil(Object tree) {
		return adaptor.isNil(tree);
	}

	public void addChild(Object t, Object child) {
		if ( t==null || child==null ) {
			return;
		}
		adaptor.addChild(t,child);
		dbg.addChild(t, child);
	}

	public Object becomeRoot(Object newRoot, Object oldRoot) {
		Object n = adaptor.becomeRoot(newRoot, oldRoot);
		dbg.becomeRoot(newRoot, oldRoot);
		return n;
	}

	public Object rulePostProcessing(Object root) {
		return adaptor.rulePostProcessing(root);
	}

	public void addChild(Object t, Token child) {
		Object n = this.create(child);
		this.addChild(t, n);
	}

	public Object becomeRoot(Token newRoot, Object oldRoot) {
		Object n = this.create(newRoot);
		adaptor.becomeRoot(n, oldRoot);
		dbg.becomeRoot(newRoot, oldRoot);
		return n;
	}

	public Object create(int tokenType, Token fromToken) {
		Object node = adaptor.create(tokenType, fromToken);
		dbg.createNode(node);
		return node;
	}

	public Object create(int tokenType, Token fromToken, String text) {
		Object node = adaptor.create(tokenType, fromToken, text);
		dbg.createNode(node);
		return node;
	}

	public Object create(int tokenType, String text) {
		Object node = adaptor.create(tokenType, text);
		dbg.createNode(node);
		return node;
	}

	public int getType(Object t) {
		return adaptor.getType(t);
	}

	public void setType(Object t, int type) {
		adaptor.setType(t, type);
	}

	public String getText(Object t) {
		return adaptor.getText(t);
	}

	public void setText(Object t, String text) {
		adaptor.setText(t, text);
	}

	public Token getToken(Object t) {
		return adaptor.getToken(t);
	}

	public void setTokenBoundaries(Object t, Token startToken, Token stopToken) {
		adaptor.setTokenBoundaries(t, startToken, stopToken);
		if ( t!=null && startToken!=null && stopToken!=null ) {
			dbg.setTokenBoundaries(
				t, startToken.getTokenIndex(),
				stopToken.getTokenIndex());
		}
	}

	public int getTokenStartIndex(Object t) {
		return adaptor.getTokenStartIndex(t);
	}

	public int getTokenStopIndex(Object t) {
		return adaptor.getTokenStopIndex(t);
	}

	public Object getChild(Object t, int i) {
		return adaptor.getChild(t, i);
	}

	public void setChild(Object t, int i, Object child) {
		adaptor.setChild(t, i, child);
	}

	public Object deleteChild(Object t, int i) {
		return deleteChild(t, i);
	}

	public int getChildCount(Object t) {
		return adaptor.getChildCount(t);
	}

	public int getUniqueID(Object node) {
		return adaptor.getUniqueID(node);
	}

	public Object getParent(Object t) {
		return adaptor.getParent(t);
	}

	public int getChildIndex(Object t) {
		return adaptor.getChildIndex(t);
	}

	public void setParent(Object t, Object parent) {
		adaptor.setParent(t, parent);
	}

	public void setChildIndex(Object t, int index) {
		adaptor.setChildIndex(t, index);
	}

	public void replaceChildren(Object parent, int startChildIndex, int stopChildIndex, Object t) {
		adaptor.replaceChildren(parent, startChildIndex, stopChildIndex, t);
	}

	// support

	public DebugEventListener getDebugListener() {
		return dbg;
	}

	public void setDebugListener(DebugEventListener dbg) {
		this.dbg = dbg;
	}

	public TreeAdaptor getTreeAdaptor() {
		return adaptor;
	}
}
