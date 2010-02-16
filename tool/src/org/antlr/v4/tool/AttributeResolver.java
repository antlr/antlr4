package org.antlr.v4.tool;

/** Grammars, rules, and alternatives all have symbols visible to
 *  actions.  To evaluate attr exprs, ask action for its resolver
 *  then ask resolver to look up.  If not found in one space we look
 *  at parent.  Alt's parent is rule; rule's parent is grammar.
 */
public interface AttributeResolver {
    public AttributeResolver getParent();
	public Attribute resolveToAttribute(String x, ActionAST node);
	public Attribute resolveToAttribute(String x, String y, ActionAST node);
	public AttributeScope resolveToScope(String x, ActionAST node);
	public AttributeScope resolveToDynamicScope(String x, ActionAST node);
	//public Attribute resolveToDynamicScopeAttribute(String x, String y, ActionAST node);
	/** Resolve to surrounding rule, rule ref/label if in alt, or other rule */
	public Rule resolveToRule(String x, ActionAST node);
}
