package org.antlr.v4.semantics;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.parse.ActionSplitterListener;
import org.antlr.v4.tool.*;

import java.util.List;

/** Trigger checks for various kinds of attribute expressions. no side-effects */
public class AttributeChecks implements ActionSplitterListener {
    public Grammar g;
    public Rule r;          // null if action outside of rule
    public Alternative alt; // null if action outside of rule
    public GrammarAST node;
    public String action;
    
    public AttributeChecks(Grammar g, Rule r, Alternative alt, GrammarAST node, String action) {
        this.g = g;
        this.r = r;
        this.alt = alt;
        this.node = node;
        this.action = action;
    }

    public void examine() {
        ANTLRStringStream in = new ANTLRStringStream(action);
        in.setLine(node.getLine());
        in.setCharPositionInLine(node.getCharPositionInLine());
        ActionSplitter splitter = new ActionSplitter(in, this);
        splitter.getActionChunks(); // forces eval, fills extractor
    }



    // LISTENER METHODS
    
    public void setQualifiedAttr(Token x, Token y, Token expr) {
        System.out.println(x+"."+y+"="+expr);
        new AttributeChecks(g, r, alt, node, expr.getText()).examine();
    }

    public void qualifiedAttr(Token x, Token y) {
        System.out.println(x+"."+y);
        AttributeScope s = r.resolveScope(x.getText(), alt);
        if ( s==null ) {
            System.err.println("not found: "+x);            
        }
    }

    public void setAttr(Token x, Token expr) {
        System.out.println(x+"="+expr);
        if ( !r.resolves(x.getText(), alt) ) System.err.println("not found: "+x);
        new AttributeChecks(g, r, alt, node, expr.getText()).examine();
    }

    public void attr(Token x) { // arg, retval, predefined, token ref, rule ref, current rule
        System.out.println(x);
        if ( !r.resolves(x.getText(), alt) ) System.err.println("not found: "+x);
    }

    public void setDynamicScopeAttr(Token x, Token y, Token expr) { }

    public void dynamicScopeAttr(Token x, Token y) { }

    public void setDynamicNegativeIndexedScopeAttr(Token x, Token y, Token index, Token expr) { }

    public void dynamicNegativeIndexedScopeAttr(Token x, Token y, Token index) { }

    public void setDynamicAbsoluteIndexedScopeAttr(Token x, Token y, Token index, Token expr) { }

    public void dynamicAbsoluteIndexedScopeAttr(Token x, Token y, Token index) { }

    public void unknownSyntax(String text) { }

    public void text(String text) { }

    // don't care
    public void templateInstance() {   }
    public void indirectTemplateInstance() {   }
    public void setExprAttribute() {   }
    public void setAttribute() {   }
    public void templateExpr() {   }
}
