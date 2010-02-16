package org.antlr.v4.tool;


import org.stringtemplate.v4.misc.MultiMap;

import java.util.ArrayList;
import java.util.List;

/** Record use/def information about an outermost alternative in a subrule
 *  or rule of a grammar.
 */
public class Alternative implements AttributeResolver {
    Rule rule;

    // token IDs, string literals in this alt
    public MultiMap<String, GrammarAST> tokenRefs = new MultiMap<String, GrammarAST>();

    // all rule refs in this alt
    public MultiMap<String, GrammarAST> ruleRefs = new MultiMap<String, GrammarAST>();

    /** A list of all LabelElementPair attached to tokens like id=ID, ids+=ID */
    public MultiMap<String, LabelElementPair> labelDefs = new MultiMap<String, LabelElementPair>();

    // track all token, rule, label refs in rewrite (right of ->)
    public List<GrammarAST> rewriteElements = new ArrayList<GrammarAST>();

    /** Track all executable actions other than named actions like @init
     *  and catch/finally (not in an alt). Also tracks predicates, rewrite actions.
     *  We need to examine these actions before code generation so
     *  that we can detect refs to $rule.attr etc...
     */
    public List<ActionAST> actions = new ArrayList<ActionAST>();

    public Alternative(Rule r) { this.rule = r; }

    private AttributeResolver getParent() { return rule; }

	/**  $x		Attribute: rule arguments, return values, predefined rule prop.
	 */
	public Attribute resolveToAttribute(String x, ActionAST node) {
		Attribute a = rule.args.get(x);   if ( a!=null ) return a;
		a = rule.retvals.get(x);          if ( a!=null ) return a;
		AttributeDict properties = rule.getPredefinedScope(LabelType.RULE_LABEL);
		return properties.get(x);
	}

	/** $x.y, x can be surrounding rule, token/rule/label ref. y is visible
	 *  attr in that dictionary.  Can't see args on rule refs.
	 */
	public Attribute resolveToAttribute(String x, String y, ActionAST node) {
		if ( rule.name.equals(x) ) { // x is this rule?
			AttributeDict d = rule.getPredefinedScope(LabelType.RULE_LABEL);
			return d.get(y);
		}
        if ( tokenRefs.get(x)!=null ) { // token ref in this alt?
            return rule.getPredefinedScope(LabelType.TOKEN_LABEL).get(y);
        }
        if ( ruleRefs.get(x)!=null ) {  // rule ref in this alt?
            // look up rule, ask it to resolve y (must be retval or predefined)
            return rule.g.getRule(x).resolveRetvalOrProperty(y);
        }
		List<LabelElementPair> labels = labelDefs.get(x);
		if ( labels!=null ) { // it's a label ref. is it a rule label?
			LabelElementPair anyLabelDef = labels.get(0);
			if ( anyLabelDef.type==LabelType.RULE_LABEL ) {
				return rule.g.getRule(anyLabelDef.element.getText()).resolveRetvalOrProperty(y);
			}
			return rule.getPredefinedScope(anyLabelDef.type).get(y);
		}
		return null;
	}

	/** $x		AttributeDict: references to tokens and token labels in the
	 * 			current alt (including any elements within subrules contained
	 * 			in that outermost alt). x can be rule with scope or a global scope.
	 *
	 * 			x can also be surrounding rule since we use for error checking. 
	 */
	public boolean resolvesToAttributeDict(String x, ActionAST node) {
		if ( tokenRefs.get(x)!=null ) return true;
		List<LabelElementPair> labels = labelDefs.get(x);
		if ( labels!=null ) { // it's a label ref. is it a token label?
			LabelElementPair anyLabelDef = labels.get(0);
			if ( anyLabelDef.type==LabelType.TOKEN_LABEL ) return true;
		}
		if ( x.equals(rule.name) ) return true; // $r for action in rule r, $r is a dict
		Rule r = rule.g.getRule(x);
		if ( r!=null && r.scope!=null ) return true;
		if ( rule.g.scopes.get(x)!=null ) return true;
		return false;
	}

	//    public boolean resolves(String x, ActionAST node) {
//        boolean inAlt =
//            tokenRefs.get(x)!=null||
//            ruleRefs.get(x)!=null ||
//            labelDefs.get(x)!=null;
//        if ( inAlt ) return inAlt;
//        return getParent().resolves(x, node);
//    }
//
//    /** Find x as token/rule/label ref then find y in properties list. */
//    public boolean resolves(String x, String y, ActionAST node) {
//        if ( tokenRefs.get(x)!=null ) { // token ref in this alt?
//            return rule.getPredefinedScope(LabelType.TOKEN_LABEL).get(y)!=null;
//        }
//        if ( ruleRefs.get(x)!=null ) {  // rule ref in this alt?
//            // look up rule, ask it to resolve y (must be retval or predefined)
//            return rule.g.getRule(x).resolvesAsRetvalOrProperty(y);
//        }
//        Rule r = resolveRule(x, node);
//        if ( r!=null ) return r.resolvesAsRetvalOrProperty(y);
//        return getParent().resolves(x, y, node);
//    }


	public AttributeDict resolveToDynamicScope(String x, ActionAST node) {
		Rule r = resolveToRule(x, node);
		if ( r!=null && r.scope !=null ) return r.scope;
		return getParent().resolveToDynamicScope(x, node);
	}

	public Rule resolveToRule(String x, ActionAST node) {
        if ( ruleRefs.get(x)!=null ) return rule.g.getRule(x);
        List<LabelElementPair> labels = labelDefs.get(x);
        if ( labels!=null ) { // it's a label ref. is it a rule label?
            LabelElementPair anyLabelDef = labels.get(0);
            if ( anyLabelDef.type==LabelType.RULE_LABEL ) {
                return rule.g.getRule(anyLabelDef.element.getText());
            }
        }
        return getParent().resolveToRule(x, node);
    }
}
