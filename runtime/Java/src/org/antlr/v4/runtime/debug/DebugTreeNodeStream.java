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

import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.*;

/** Debug any tree node stream.  The constructor accepts the stream
 *  and a debug listener.  As node stream calls come in, debug events
 *  are triggered.
 */
public class DebugTreeNodeStream implements ASTNodeStream {
	protected DebugEventListener dbg;
	protected ASTAdaptor adaptor;
	protected ASTNodeStream input;
	protected boolean initialStreamState = true;

	/** Track the last mark() call result value for use in rewind(). */
	protected int lastMarker;

	public DebugTreeNodeStream(ASTNodeStream input,
							   DebugEventListener dbg)
	{
		this.input = input;
		this.adaptor = input.getTreeAdaptor();
		this.input.setUniqueNavigationNodes(true);
		setDebugListener(dbg);
	}

	public void setDebugListener(DebugEventListener dbg) {
		this.dbg = dbg;
	}

	public ASTAdaptor getTreeAdaptor() {
		return adaptor;
	}

	public void consume() {
		Object node = input.LT(1);
		input.consume();
		dbg.consumeNode(node);
	}

	public Object get(int i) {
		return input.get(i);
	}

	public Object LT(int i) {
		Object node = input.LT(i);
		int ID = adaptor.getUniqueID(node);
		String text = adaptor.getText(node);
		int type = adaptor.getType(node);
		dbg.LT(i, node);
		return node;
	}

	public int LA(int i) {
		Object node = input.LT(i);
		int ID = adaptor.getUniqueID(node);
		String text = adaptor.getText(node);
		int type = adaptor.getType(node);
		dbg.LT(i, node);
		return type;
	}

	public int mark() {
		lastMarker = input.mark();
		dbg.mark(lastMarker);
		return lastMarker;
	}

	public void release(int marker) {
		dbg.release(marker);
		input.release(marker);
	}

	public int index() {
		return input.index();
	}

	public void seek(int index) {
		// TODO: implement seek in dbg interface
		// db.seek(index);
		input.seek(index);
	}

	public int size() {
		return input.size();
	}

    public void reset() { ; }

    public Object getTreeSource() {
		return input;
	}

	public String getSourceName() {
		return getTokenStream().getSourceName();
	}

	public TokenStream getTokenStream() {
		return input.getTokenStream();
	}

	/** It is normally this object that instructs the node stream to
	 *  create unique nav nodes, but to satisfy interface, we have to
	 *  define it.  It might be better to ignore the parameter but
	 *  there might be a use for it later, so I'll leave.
	 */
	public void setUniqueNavigationNodes(boolean uniqueNavigationNodes) {
		input.setUniqueNavigationNodes(uniqueNavigationNodes);
	}

	public void replaceChildren(Object parent, int startChildIndex, int stopChildIndex, Object t) {
		input.replaceChildren(parent, startChildIndex, stopChildIndex, t);
	}

	public String toString(Object start, Object stop) {
		return input.toString(start,stop);
	}
}
