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
    public Alternative alt; // null if action outside of alt; could be in rule
    public ActionAST node;
    public String action;
    
    public AttributeChecks(Grammar g, Rule r, Alternative alt, ActionAST node, String action) {
        this.g = g;
        this.r = r;
        this.alt = alt;
        this.node = node;
        this.action = action;
    }

    public static void checkAllAttributeExpressions(Grammar g) {
        for (ActionAST act : g.actions.values()) {
            AttributeChecks checker = new AttributeChecks(g, null, null, act, act.getText());
            checker.examineAction();
        }

        for (Rule r : g.rules.values()) {
            for (ActionAST a : r.namedActions.values()) {
                AttributeChecks checker = new AttributeChecks(g, r, null, a, a.getText());
                checker.examineAction();
            }
            for (int i=1; i<=r.numberOfAlts; i++) {
                Alternative alt = r.alt[i];
                for (ActionAST a : alt.actions) {
                    AttributeChecks checker =
                        new AttributeChecks(g, r, alt, a, a.getText());
                    checker.examineAction();
                }
            }
            for (ActionAST a : r.exceptionActions) {
                AttributeChecks checker = new AttributeChecks(g, r, null, a, a.getText());
                checker.examineAction();                
            }
        }
    }

    public void examineAction() {
        ANTLRStringStream in = new ANTLRStringStream(action);
        in.setLine(node.getLine());
        in.setCharPositionInLine(node.getCharPositionInLine());
        ActionSplitter splitter = new ActionSplitter(in, this);
        List<Token> chunks = splitter.getActionChunks(); // forces eval, fills extractor
        //System.out.println(chunks);
    }

    // LISTENER METHODS
    
    public void setQualifiedAttr(String expr, Token x, Token y, Token rhs) {
        if ( !node.resolver.resolves(x.getText(), y.getText(), node) ) {
            ErrorManager.grammarError(ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE, // TODO; not right error
                                      g.fileName, x, x.getText(), expr);
        }
        new AttributeChecks(g, r, alt, node, rhs.getText()).examineAction();
    }

    public void qualifiedAttr(String expr, Token x, Token y) {
        if ( !node.resolver.resolves(x.getText(), node) ) {
            ErrorManager.grammarError(ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE,
                                      g.fileName, x, x.getText(), expr);
            return;
        }

        // ???if y is not prop of x, we don't care; we'll ignore and leave as simple attr

        if ( !node.resolver.resolves(x.getText(), y.getText(), node) ) {
            if ( node.resolver.resolveRefToRule(x.getText(), node)!=null ) {
                ErrorManager.grammarError(ErrorType.INVALID_RULE_PARAMETER_REF,
                                          g.fileName, y, y.getText(), expr);
            }
            else {
                ErrorManager.grammarError(ErrorType.UNKNOWN_ATTRIBUTE_IN_SCOPE,
                                          g.fileName, y, y.getText(), expr);
            }
        }
    }

    public void setAttr(String expr, Token x, Token rhs) {
        if ( !node.resolver.resolves(x.getText(), node) ) {
            ErrorManager.grammarError(ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE,
                                      g.fileName, x, x.getText(), expr);
        }
        new AttributeChecks(g, r, alt, node, rhs.getText()).examineAction();
    }

    public void attr(String expr, Token x) { // arg, retval, predefined, token ref, rule ref, current rule
        // TODO: check for isolated rule ref "+x+" in "+expr);
        if ( node.resolver.resolveRefToRule(x.getText(), node)!=null ) {
            ErrorManager.grammarError(ErrorType.ISOLATED_RULE_SCOPE,
                                      g.fileName, x, x.getText(), expr);
            return;
        }
        if ( !node.resolver.resolves(x.getText(), node) ) {
            ErrorManager.grammarError(ErrorType.UNKNOWN_SIMPLE_ATTRIBUTE,
                                      g.fileName, x, x.getText(), expr);
        }
    }

    public void setDynamicScopeAttr(String expr, Token x, Token y, Token rhs) { }

    public void dynamicScopeAttr(String expr, Token x, Token y) {
        
    }

    public void setDynamicNegativeIndexedScopeAttr(String expr, Token x, Token y, Token index, Token rhs) { }

    public void dynamicNegativeIndexedScopeAttr(String expr, Token x, Token y, Token index) { }

    public void setDynamicAbsoluteIndexedScopeAttr(String expr, Token x, Token y, Token index, Token rhs) { }

    public void dynamicAbsoluteIndexedScopeAttr(String expr, Token x, Token y, Token index) { }

    public void unknownSyntax(String text) {
        System.err.println("unknown: "+text);
    }

    public void text(String text) { }

    // don't care
    public void templateInstance(String expr) {   }
    public void indirectTemplateInstance(String expr) {   }
    public void setExprAttribute(String expr) {   }
    public void setAttribute(String expr) {  }
    public void templateExpr(String expr) {  }
}
