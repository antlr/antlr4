package org.antlr.v4.parse;

import org.antlr.runtime.Token;

/** */
public interface ActionSplitterListener {
    void setQualifiedAttr(Token x, Token y, Token expr);

    void qualifiedAttr(Token x, Token y);

    void setDynamicScopeAttr(Token x, Token y, Token expr);

    void dynamicScopeAttr(Token x, Token y);

    void setDynamicNegativeIndexedScopeAttr(Token x, Token y, Token index, Token expr);

    void dynamicNegativeIndexedScopeAttr(Token x, Token y, Token index);

    void setDynamicAbsoluteIndexedScopeAttr(Token x, Token y, Token index, Token expr);

    void dynamicAbsoluteIndexedScopeAttr(Token x, Token y, Token index);

    void setAttr(Token x, Token expr);

    void attr(Token x);

    void templateInstance();

    void indirectTemplateInstance();

    void setExprAttribute();

    void setAttribute();

    void templateExpr();

    void unknownSyntax(String text);

    void text(String text);
}
