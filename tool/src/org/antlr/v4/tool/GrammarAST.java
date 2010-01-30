package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.v4.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

public class GrammarAST extends CommonTree {
    public GrammarAST() {;}
    public GrammarAST(Token t) { super(t); }
    public GrammarAST(GrammarAST node) { super(node); }

    @Override
    public Tree dupNode() {
        return new GrammarAST(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
