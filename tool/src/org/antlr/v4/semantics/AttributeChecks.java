package org.antlr.v4.semantics;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.parse.ActionSplitterListener;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.Rule;

/** Trigger checks for various kinds of attribute expressions. no side-effects */
public class AttributeChecks implements ActionSplitterListener {
    public Grammar g;
    public Rule r;
    public GrammarAST node;
    String action;
    
    public AttributeChecks(Grammar g, Rule r, GrammarAST node, String action) {
        this.g = g;
        this.r = r;
        this.node = node;
        this.action = action;
    }

    public void examine() {
        ActionSplitter splitter =
            new ActionSplitter(new ANTLRStringStream(action), this);
        splitter.getActionChunks(); // forces eval, fills extractor
    }

    public void setQualifiedAttr(Token x, Token y, Token expr) {
        System.out.println(x+"."+y+"="+expr);
        new AttributeChecks(g, r, node, expr.getText()).examine();
    }
    public void qualifiedAttr(Token x, Token y) {
        System.out.println(x+"."+y);
    }
    public void setDynamicScopeAttr(Token x, Token y, Token expr) { }
    public void dynamicScopeAttr(Token x, Token y) { }
    public void setDynamicNegativeIndexedScopeAttr(Token x, Token y, Token index, Token expr) { }
    public void dynamicNegativeIndexedScopeAttr(Token x, Token y, Token index) { }
    public void setDynamicAbsoluteIndexedScopeAttr(Token x, Token y, Token index, Token expr) { }
    public void dynamicAbsoluteIndexedScopeAttr(Token x, Token y, Token index) { }
    public void setAttr(Token x, Token expr) {
        System.out.println(x+"="+expr);                
        new AttributeChecks(g, r, node, expr.getText()).examine();
    }
    public void attr(Token x) {
        System.out.println(x);
    }
    public void unknownSyntax(String text) { }
    public void text(String text) { }

    // don't care
    public void templateInstance() {   }
    public void indirectTemplateInstance() {   }
    public void setExprAttribute() {   }
    public void setAttribute() {   }
    public void templateExpr() {   }
}
