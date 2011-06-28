package org.antlr.v4.tool;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.*;

public class GrammarAST extends CommonTree {
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
	}
    public GrammarAST(int type, Token t, String text) {
		this(new CommonToken(type, text));
		token.setInputStream(t.getInputStream());
		token.setLine(t.getLine());
		token.setCharPositionInLine(t.getCharPositionInLine());
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
}
