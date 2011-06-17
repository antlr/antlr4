package org.antlr.v4.tool;


import org.stringtemplate.v4.misc.MultiMap;

import java.util.ArrayList;
import java.util.List;

/** Record use/def information about an outermost alternative in a subrule
 *  or rule of a grammar.
 */
public class Alternative implements AttributeResolver {
    Rule rule;

	public AltAST ast;

    // token IDs, string literals in this alt
    public MultiMap<String, TerminalAST> tokenRefs = new MultiMap<String, TerminalAST>();

	// does not include labels
	public MultiMap<String, GrammarAST> tokenRefsInActions = new MultiMap<String, GrammarAST>();

    // all rule refs in this alt
    public MultiMap<String, GrammarAST> ruleRefs = new MultiMap<String, GrammarAST>();

	// does not include labels
	public MultiMap<String, GrammarAST> ruleRefsInActions = new MultiMap<String, GrammarAST>();

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

	public boolean resolvesToToken(String x, ActionAST node) {
		if ( tokenRefs.get(x)!=null ) return true;
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		if ( anyLabelDef!=null && anyLabelDef.type==LabelType.TOKEN_LABEL ) return true;
		return false;
	}

//	public String getTokenLabel(String x, ActionAST node) {
//		LabelElementPair anyLabelDef = getAnyLabelDef(x);
//		if ( anyLabelDef!=null ) return anyLabelDef.label.getText();
//		if ( tokenRefs.get(x)!=null ) {
//
//		}
//		LabelElementPair anyLabelDef = getAnyLabelDef(x);
//		if ( anyLabelDef!=null && anyLabelDef.type==LabelType.TOKEN_LABEL ) return true;
//		return false;
//	}

	public boolean resolvesToAttributeDict(String x, ActionAST node) {
		if ( resolvesToToken(x, node) ) return true;
		if ( x.equals(rule.name) ) return true; // $r for action in rule r, $r is a dict
		if ( rule!=null && rule.scope!=null ) return true;
		if ( rule.g.scopes.get(x)!=null ) return true;
		return false;
	}

	/**  $x		Attribute: rule arguments, return values, predefined rule prop.
	 */
	public Attribute resolveToAttribute(String x, ActionAST node) {
		return rule.resolveToAttribute(x, node); // reuse that code
	}

	/** $x.y, x can be surrounding rule, token/rule/label ref. y is visible
	 *  attr in that dictionary.  Can't see args on rule refs.
	 */
	public Attribute resolveToAttribute(String x, String y, ActionAST node) {
		if ( rule.name.equals(x) ) { // x is this rule?
			return rule.resolveToAttribute(x, y, node);
		}
        if ( tokenRefs.get(x)!=null ) { // token ref in this alt?
            return rule.getPredefinedScope(LabelType.TOKEN_LABEL).get(y);
        }
        if ( ruleRefs.get(x)!=null ) {  // rule ref in this alt?
            // look up rule, ask it to resolve y (must be retval or predefined)
			return rule.g.getRule(x).resolveRetvalOrProperty(y);
		}
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		if ( anyLabelDef!=null && anyLabelDef.type==LabelType.RULE_LABEL ) {
			return rule.g.getRule(anyLabelDef.element.getText()).resolveRetvalOrProperty(y);
		}
		else if ( anyLabelDef!=null ) {
			return rule.getPredefinedScope(anyLabelDef.type).get(y);
		}
		return null;
	}

	public AttributeDict resolveToDynamicScope(String x, ActionAST node) {
		Rule r = resolveToRule(x);
		if ( r!=null && r.scope !=null ) return r.scope;
		return rule.resolveToDynamicScope(x, node);
	}

	public boolean resolvesToLabel(String x, ActionAST node) {
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		return anyLabelDef!=null &&
			   (anyLabelDef.type==LabelType.TOKEN_LABEL ||
				anyLabelDef.type==LabelType.RULE_LABEL);
	}

	public boolean resolvesToListLabel(String x, ActionAST node) {
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		return anyLabelDef!=null &&
			   (anyLabelDef.type==LabelType.RULE_LIST_LABEL ||
				anyLabelDef.type==LabelType.TOKEN_LIST_LABEL);
	}

	public LabelElementPair getAnyLabelDef(String x) {
		List<LabelElementPair> labels = labelDefs.get(x);
		if ( labels!=null ) return labels.get(0);
		return null;
	}

	/** x can be ruleref or rule label. */
	public Rule resolveToRule(String x) {
        if ( ruleRefs.get(x)!=null ) return rule.g.getRule(x);
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		if ( anyLabelDef!=null && anyLabelDef.type==LabelType.RULE_LABEL ) {
            return rule.g.getRule(anyLabelDef.element.getText());
        }
		if ( x.equals(rule.name) ) return rule;
        return null;
    }
}
