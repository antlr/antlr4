package org.antlr.v4.tool;

/** Grammars, rules, and alternatives all have symbols visible to
 *  actions.  To evaluate attr exprs, ask action for its resolver
 *  then ask resolver to look up various symbols. Depending on the context,
 *  some symbols are available at some aren't.
 *
 *  Alternative level:
 *
 *  $x		Attribute: rule arguments, return values, predefined rule prop.
 * 			AttributeDict: references to tokens and token labels in the
 * 			current alt (including any elements within subrules contained
 * 			in that outermost alt). x can be rule with scope or a global scope.
 *  $x.y	Attribute: x is surrounding rule, rule/token/label ref
 *  $s::y	Attribute: s is any rule with scope or global scope; y is prop within
 *
 *  Rule level:
 *
 *  $x		Attribute: rule arguments, return values, predefined rule prop.
 * 			AttributeDict: references to token labels in *any* alt. x can
 * 			be any rule with scope or global scope.
 *  $x.y	Attribute: x is surrounding rule, label ref (in any alts)
 *  $s::y	Attribute: s is any rule with scope or global scope; y is prop within
 *
 *  Grammar level:
 * 
 *  $s		AttributeDict: s is a global scope
 *  $s::y	Attribute: s is a global scope; y is prop within
 */
public interface AttributeResolver {
	public Attribute resolveToAttribute(String x, ActionAST node);
	public Attribute resolveToAttribute(String x, String y, ActionAST node);
	/** Error checking when $x.y is not attribute. We ask if $x is a dict. */
	public boolean resolvesToAttributeDict(String x, ActionAST node);
	public AttributeDict resolveToDynamicScope(String x, ActionAST node);
	//public Attribute resolveToDynamicScopeAttribute(String x, String y, ActionAST node);
	/** Resolve to surrounding rule, rule ref/label if in alt, or other rule */
	public Rule resolveToRule(String x, ActionAST node);
}
