package org.antlr.v4.tool;


import org.stringtemplate.v4.misc.MultiMap;

import java.util.ArrayList;
import java.util.List;

/** Record use/def information about an outermost alternative in a subrule
 *  or rule of a grammar.
 */
public class Alternative implements SymbolSpace {
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

    public SymbolSpace getParent() {
        return null;
    }

    /** Is isolated x a token/rule/label ref? */
    public boolean resolves(String x, ActionAST node) {
        boolean inAlt =
            tokenRefs.get(x)!=null||
            ruleRefs.get(x)!=null ||
            labelDefs.get(x)!=null;
        if ( inAlt ) return inAlt;
        return rule.resolves(x, node);
    }

    /** Find x as token/rule/label ref then find y in properties list. */
    public boolean resolves(String x, String y, ActionAST node) {
        if ( tokenRefs.get(x)!=null ) { // token ref in this alt?
            return rule.getPredefinedScope(LabelType.TOKEN_LABEL).get(y)!=null;
        }
        if ( ruleRefs.get(x)!=null ) {  // rule ref in this alt?
            // look up rule, ask it to resolve y (must be retval or predefined)
            return rule.g.getRule(x).resolvesAsRetvalOrProperty(y);
        }
        List<LabelElementPair> labels = labelDefs.get(x); // label?
        if ( labels!=null ) {
            // it's a label ref, compute scope from label type and grammar type
            LabelElementPair anyLabelDef = labels.get(0);
            if ( rule.getPredefinedScope(anyLabelDef.type).get(y)!=null) return true;
            if ( anyLabelDef.type==LabelType.RULE_LABEL ) {
                Rule ref = rule.g.getRule(anyLabelDef.element.getText());
                return ref.resolvesAsRetvalOrProperty(y);
            }
        }
        return false;
    }

    public boolean resolveToRuleRef(String x, ActionAST node) {
        if ( ruleRefs.get(x)!=null ) return true;
        List<LabelElementPair> labels = labelDefs.get(x);
        if ( labels!=null ) { // it's a label ref. is it a rule label?
            LabelElementPair anyLabelDef = labels.get(0);
            if ( anyLabelDef.type==LabelType.RULE_LABEL ) return true;
        }
        return false;
    }
}
