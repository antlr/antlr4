package org.antlr.v4.semantics;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;
import org.antlr.v4.parse.ActionSplitter;
import org.antlr.v4.tool.Grammar;

/** Trigger checks for various kinds of attribute expressions. no side-effects */
public class AttributeChecks extends ActionSplitter {
    public Grammar g;
    
    public AttributeChecks(Grammar g, String action) {
        super(new ANTLRStringStream(action));
        this.g = g;
    }

    public void setQualifiedAttr(Token x, Token y, Token expr) { }
    public void qualifiedAttr(Token x, Token y) {
        System.out.println(x+"."+y);
    }
    public void setDynamicScopeAttr() { }
    public void dynamicScopeAttr() { }
    public void setDynamicNegativeIndexedScopeAttr() { }
    public void dynamicNegativeIndexedScopeAttr() { }
    public void setDynamicAbsoluteIndexedScopeAttr() { }
    public void dynamicAbsoluteIndexedScopeAttr() { }
    public void setAttr() { }
    public void attr() { }
    public void unknownSyntax() { }
    public void text() { }    
}
