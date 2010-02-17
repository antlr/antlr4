package org.antlr.v4.parse;

import org.antlr.runtime.Token;

/** */
public interface ActionSplitterListener {
    void setQualifiedAttr(String expr, Token x, Token y, Token rhs);
    void qualifiedAttr(String expr, Token x, Token y);
    void setAttr(String expr, Token x, Token rhs);
    void attr(String expr, Token x);
    
    void setDynamicScopeAttr(String expr, Token x, Token y, Token rhs);
    void dynamicScopeAttr(String expr, Token x, Token y);
    void setDynamicNegativeIndexedScopeAttr(String expr, Token x, Token y, Token index, Token rhs);
    void dynamicNegativeIndexedScopeAttr(String expr, Token x, Token y, Token index);
    void setDynamicAbsoluteIndexedScopeAttr(String expr, Token x, Token y, Token index, Token rhs);
    void dynamicAbsoluteIndexedScopeAttr(String expr, Token x, Token y, Token index);

    void templateInstance(String expr);
    void indirectTemplateInstance(String expr);
    void setExprAttribute(String expr);
    void setAttribute(String expr);
    void templateExpr(String expr);

    void unknownSyntax(Token t);
    void text(String text);
}
