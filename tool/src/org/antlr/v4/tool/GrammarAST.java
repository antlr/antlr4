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

package org.antlr.v4.tool;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.v4.parse.*;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.*;

public class GrammarAST extends CommonTree {
	/** For error msgs, nice to know which grammar this AST lives in */
	// TODO: try to remove
	public Grammar g;

	/** If we build an ATN, we make AST node point at left edge of ATN construct */
	public ATNState atnState;

    public GrammarAST() {;}
    public GrammarAST(Token t) { super(t); }
    public GrammarAST(GrammarAST node) { super(node); }
    public GrammarAST(int type) { super(new CommonToken(type, ANTLRParser.tokenNames[type])); }
    public GrammarAST(int type, Token t) {
		this(new CommonToken(type, t.getText()));
		token.setInputStream(t.getInputStream());
		token.setLine(t.getLine());
		token.setCharPositionInLine(t.getCharPositionInLine());
		token.setTokenIndex(t.getTokenIndex());
	}
    public GrammarAST(int type, Token t, String text) {
		this(new CommonToken(type, text));
		token.setInputStream(t.getInputStream());
		token.setLine(t.getLine());
		token.setCharPositionInLine(t.getCharPositionInLine());
		token.setTokenIndex(t.getTokenIndex());
    }

	public List<GrammarAST> getNodesWithType(int ttype) {
		return getNodesWithType(IntervalSet.of(ttype));
	}

	public List<GrammarAST> getNodesWithType(IntervalSet types) {
		List<GrammarAST> nodes = new ArrayList<GrammarAST>();
		List<GrammarAST> work = new LinkedList<GrammarAST>();
		work.add(this);
		GrammarAST t = null;
		while ( work.size()>0 ) {
			t = work.remove(0);
			if ( types.member(t.getType()) ) nodes.add(t);
			if ( t.children!=null ) work.addAll(t.children);
		}
		return nodes;
	}

	public AltAST getOutermostAltNode() {
		if ( this instanceof AltAST && parent.parent instanceof RuleAST ) {
			return (AltAST)this;
		}
		if ( parent!=null ) return ((GrammarAST)parent).getOutermostAltNode();
		return null;
	}

	public boolean deleteChild(org.antlr.runtime.tree.Tree t) {
		for (int i=0; i<children.size(); i++) {
			Object c = children.get(i);
			if ( c == t ) {
				deleteChild(t.getChildIndex());
				return true;
			}
		}
		return false;
	}

    // TODO: move to basetree when i settle on how runtime works
    // TODO: don't include this node!!
	// TODO: reuse other method
    public CommonTree getFirstDescendantWithType(int type) {
        if ( getType()==type ) return this;
        if ( children==null ) return null;
        for (Object c : children) {
            GrammarAST t = (GrammarAST)c;
            if ( t.getType()==type ) return t;
            CommonTree d = t.getFirstDescendantWithType(type);
            if ( d!=null ) return d;
        }
        return null;
    }

	// TODO: don't include this node!!
	public CommonTree getFirstDescendantWithType(org.antlr.runtime.BitSet types) {
		if ( types.member(getType()) ) return this;
		if ( children==null ) return null;
		for (Object c : children) {
			GrammarAST t = (GrammarAST)c;
			if ( types.member(t.getType()) ) return t;
			CommonTree d = t.getFirstDescendantWithType(types);
			if ( d!=null ) return d;
		}
		return null;
	}

//	@Override
//	public boolean equals(Object obj) {
//		return super.equals(obj);
//	}

	@Override
    public Tree dupNode() {
        return new GrammarAST(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }

	public String toTokenString() {
		CharStream input = this.token.getInputStream();
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(input);
		CommonTreeNodeStream nodes =
			new CommonTreeNodeStream(adaptor, this);
		StringBuffer buf = new StringBuffer();
		GrammarAST o = (GrammarAST)nodes.LT(1);
		int type = adaptor.getType(o);
		while ( type!=Token.EOF ) {
			buf.append(" ");
			buf.append(o.token.getText());
			nodes.consume();
			o = (GrammarAST)nodes.LT(1);
			type = adaptor.getType(o);
		}
		return buf.toString();
	}

}
