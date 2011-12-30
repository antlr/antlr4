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

import org.antlr.v4.runtime.atn.ATNState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TraceTree implements Tree {
	public TraceTree parent;
	public List<TraceTree> children;

	/** If this node is root, it has list of all leaves of tree after ParserATNPathFinder.trace() */
	public List<TraceTree> leaves;

	public ATNState state; // payload

	public TraceTree(ATNState s) { state = s; }

	public void addChild(TraceTree t) {
		if ( children==null ) {
			children = new ArrayList<TraceTree>();
		}
		children.add(t);
		t.parent = this;
	}

	public void addChild(ATNState s) { addChild(new TraceTree(s)); }

	@Override
	public Tree getChild(int i) {
		if ( children==null ) {
			throw new IndexOutOfBoundsException(i+"<0 or >"+getChildCount());
		}
		return children.get(i);
	}

	@Override
	public Tree getParent() {
		return parent;
	}

	@Override
	public Object getPayload() {
		return state;
	}

	@Override
	public int getChildCount() {
		if ( children==null ) return 0;
		return children.size();
	}

	public List<ATNState> getPathToNode(TraceTree s) {
		List<ATNState> states = new LinkedList<ATNState>();
		TraceTree p = s;
		while ( p!=null ) {
			states.add(0, p.state);
			p = p.parent;
		}
		if ( states.size()==0 ) return null;
		return states;
	}

	@Override
	public String toString() {
		if ( state==null ) return "null";
		return state.toString();
	}

	@Override
	public String toStringTree() {
		return Trees.toStringTree(this, null);
	}
}
