package org.antlr.v4.tool;

import org.antlr.runtime.BitSet;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;
import org.antlr.v4.automata.NFAState;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.tree.CommonTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GrammarAST extends CommonTree {
	/** If we build an NFA, we make AST node point at left edge of NFA construct */
	public NFAState nfaState;

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
		return getNodesWithType(BitSet.of(ttype));
	}

	public List<GrammarAST> getNodesWithType(BitSet types) {
		List<GrammarAST> nodes = new ArrayList<GrammarAST>();
		List<GrammarAST> work = new LinkedList<GrammarAST>();
		work.add(this);
		GrammarAST t = null;
		while ( work.size()>0 ) {
			t = work.remove(0);
			if ( types.member(t.getType()) ) nodes.add(this);
			work.addAll(children);
		}
		return nodes;
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
