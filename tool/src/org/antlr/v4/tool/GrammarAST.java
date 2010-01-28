package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.antlr.v4.gui.ASTViewer;

public class GrammarAST extends CommonTree {
    public GrammarAST() {;}
    public GrammarAST(Token t) { super(t); }
    public GrammarAST(GrammarAST node) { super(node); }

    public void inspect() {
        ASTViewer viewer = new ASTViewer(this);
        viewer.open();
    }

    @Override
    public Tree dupNode() {
        return new GrammarAST(this);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
