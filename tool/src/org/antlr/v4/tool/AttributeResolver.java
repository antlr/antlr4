package org.antlr.v4.tool;

/** Grammars, rules, and alternatives all have symbols visible to
 *  actions.  To evaluate attr exprs, ask action for its space
 *  then ask space to resolve.  If not found in one space we look
 *  at parent.  Alt's parent is rule; rule's parent is grammar.
 */
public interface AttributeResolver {
    public AttributeResolver getParent();
    public boolean resolves(String x, ActionAST node);
    public boolean resolves(String x, String y, ActionAST node);
    public Rule resolveRefToRule(String x, ActionAST node);
}
