package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.*;

public class Rule implements AttributeResolver {
    /** Rule refs have a predefined set of attributes as well as
     *  the return values and args.
     */
    public static AttributeDict predefinedRulePropertiesDict =
        new AttributeDict() {{
            add(new Attribute("text"));
            add(new Attribute("start"));
            add(new Attribute("stop"));
            add(new Attribute("tree"));
            add(new Attribute("st"));
        }};

    public static AttributeDict predefinedTreeRulePropertiesDict =
        new AttributeDict() {{
            add(new Attribute("text"));
            add(new Attribute("start")); // note: no stop; not meaningful
            add(new Attribute("tree"));
            add(new Attribute("st"));
        }};

    public static AttributeDict predefinedLexerRulePropertiesDict =
        new AttributeDict() {{
            add(new Attribute("text"));
            add(new Attribute("type"));
            add(new Attribute("line"));
            add(new Attribute("index"));
            add(new Attribute("pos"));
            add(new Attribute("channel"));
            add(new Attribute("start"));
            add(new Attribute("stop"));
            add(new Attribute("int"));
        }};

    public String name;
    public GrammarASTWithOptions ast;
    public AttributeDict args;
    public AttributeDict retvals;
    public AttributeDict scope; // scope { int i; }
    /** A list of scope names used by this rule */
    public List<Token> useScopes;
    public Grammar g;

    /** Map a name to an action for this rule like @init {...}.
     *  The code generator will use this to fill holes in the rule template.
     *  I track the AST node for the action in case I need the line number
     *  for errors.
     */
    public Map<String, ActionAST> namedActions =
        new HashMap<String, ActionAST>();

    /** Track exception handlers, finally action */
    public List<ActionAST> exceptionActions = new ArrayList<ActionAST>();

    public int numberOfAlts;

    /** Labels are visible to all alts in a rule. Record all defs here.
     *  We need to ensure labels are used to track same kind of symbols.
     *  Tracks all label defs for a label.
     public MultiMap<String, LabelElementPair> labelDefs =
     new MultiMap<String, LabelElementPair>();
     */

    public Alternative[] alt;

	public Rule(Grammar g, String name, GrammarASTWithOptions ast, int numberOfAlts) {
        this.g = g;
        this.name = name;
        this.ast = ast;
        this.numberOfAlts = numberOfAlts;
        alt = new Alternative[numberOfAlts+1]; // 1..n
        for (int i=1; i<=numberOfAlts; i++) alt[i] = new Alternative(this);
    }

    /** Is isolated x an arg, retval, predefined prop? */
//    public boolean resolves(String x, ActionAST node) {
//        if ( resolvesAsRetvalOrProperty(x) ) return true;
//        if ( args.get(x)!=null ) return true;
//        // resolve outside of an alt?
//        if ( node.resolver instanceof Alternative ) return getParent().resolves(x, node);
//        if ( getLabelNames().contains(x) ) return true; // can see all labels if not in alt
//        return getParent().resolves(x, node);
//    }
//
//    /** For $x.y, is x an arg, retval, predefined prop, token/rule/label ref?
//     *  If so, make sure y resolves within that perspective.
//	 *  For $x::y, is x this rule or another? If so, is y in that scope?
//     */
//    public boolean resolves(String x, String y, ActionAST node) {
//        Rule r = resolveRule(x, node);
//        if ( r!=null ) return r.resolvesAsRetvalOrProperty(y);
//        return getParent().resolves(x,y,node);
//    }

	public Attribute resolveRetvalOrProperty(String y) {
		if ( retvals!=null ) {
			Attribute a = retvals.get(y);
			if ( a!=null ) return retvals.get(y);
		}
		AttributeDict d = getPredefinedScope(LabelType.RULE_LABEL);
		return d.get(y);
	}

	public Set<String> getRuleRefs() {
        Set<String> refs = new HashSet<String>();
        for (Alternative a : alt) refs.addAll(a.ruleRefs.keySet());
        return refs;
    }

    public Set<String> getLabelNames() {
        Set<String> refs = new HashSet<String>();
        for (int i=1; i<=numberOfAlts; i++) {
            refs.addAll(alt[i].labelDefs.keySet());
        }
        return refs;
    }

    public MultiMap<String, LabelElementPair> getLabelDefs() {
        MultiMap<String, LabelElementPair> defs =
            new MultiMap<String, LabelElementPair>();
        for (int i=1; i<=numberOfAlts; i++) {
            for (List<LabelElementPair> pairs : alt[i].labelDefs.values()) {
                for (LabelElementPair p : pairs) {
                    defs.map(p.label.getText(), p);
                }
            }
        }
        return defs;
    }

	private AttributeResolver getParent() { return g; }

	/**  $x		Attribute: rule arguments, return values, predefined rule prop. */
	public Attribute resolveToAttribute(String x, ActionAST node) {
		Attribute a = args.get(x);   if ( a!=null ) return a;
		a = retvals.get(x);          if ( a!=null ) return a;
		AttributeDict properties = getPredefinedScope(LabelType.RULE_LABEL);
		return properties.get(x);
	}

	/** $x.y	Attribute: x is surrounding rule, label ref (in any alts) */
	public Attribute resolveToAttribute(String x, String y, ActionAST node) {
		if ( this.name.equals(x) ) { // x is this rule?
			AttributeDict d = getPredefinedScope(LabelType.RULE_LABEL);
			return d.get(y);
		}
		List<LabelElementPair> labels = getLabelDefs().get(x);
		if ( labels!=null ) { // it's a label ref. is it a rule label?
			LabelElementPair anyLabelDef = labels.get(0);
			if ( anyLabelDef.type==LabelType.RULE_LABEL ) {
				return g.getRule(anyLabelDef.element.getText()).resolveRetvalOrProperty(y);
			}
			return getPredefinedScope(anyLabelDef.type).get(y);
		}
		return null;

	}

	/** $x		AttributeDict: references to token labels in *any* alt. x can
 	 * 			be any rule with scope or global scope or surrounding rule x.
	 */
	public boolean resolvesToAttributeDict(String x, ActionAST node) {
		List<LabelElementPair> labels = getLabelDefs().get(x);
		if ( labels!=null ) { // it's a label ref. is it a token label?
			LabelElementPair anyLabelDef = labels.get(0);
			if ( anyLabelDef.type==LabelType.TOKEN_LABEL ) return true;
		}
		if ( x.equals(this.name) ) return true; // $r for action in rule r, $r is a dict
		Rule r = g.getRule(x);
		if ( r!=null && r.scope!=null ) return true;
		if ( g.scopes.get(x)!=null ) return true;
		return false;
	}

	public AttributeDict resolveToDynamicScope(String x, ActionAST node) {
		Rule r = resolveToRule(x, node);
		if ( r!=null && r.scope !=null ) return r.scope;
		return getParent().resolveToDynamicScope(x, node);
	}

	public Rule resolveToRule(String x, ActionAST node) {
        if ( x.equals(this.name) ) return this;
        if ( node.resolver == this ) { // action not in alt (attr space is this rule)
            List<LabelElementPair> labels = getLabelDefs().get(x);
            if ( labels!=null ) {  // it's a label ref. is it a rule label?
                LabelElementPair anyLabelDef = labels.get(0);
                if ( anyLabelDef.type==LabelType.RULE_LABEL ) {
                    return g.getRule(anyLabelDef.element.getText());
                }
            }
        }
        return null; // don't look for general rule (not one ref'd in this rule)
    }
	
	/** Look up name from context of this rule and an alternative.
     *  Find an arg, retval, predefined property, or label/rule/token ref.
     */
//    public Attribute resolve(String name) {
//        Attribute a = args.get(name);   if ( a!=null ) return a;
//        a = retvals.get(name);          if ( a!=null ) return a;
//        AttributeScope properties = getPredefinedScope(LabelType.RULE_LABEL);
//        a = properties.get(name);
//        if ( a!=null ) return a;
//
//        //alt[currentAlt].tokenRefs
//        // not here? look in grammar for global scope
//        return null;
//    }

    /** Resolve x in $x.y to
     */
//    public AttributeScope resolveScope(String name, Alternative alt) {
//        AttributeScope s = null;
//        if ( this.name.equals(name) ) { // $r ref in rule r
//            s = resolveLocalAttributeScope(name);
//            if ( s!=null ) return s;
//        }
//
//        if ( alt.tokenRefs.get(name)!=null ) { // token ref in this alt?
//            return getPredefinedScope(LabelType.TOKEN_LABEL);
//        }
//        if ( alt.ruleRefs.get(name)!=null ) {  // rule ref in this alt?
//            s = getLocalAttributeScope(name);
//            if ( s!=null ) return s;
//        }
//        List<GrammarAST> labels = alt.labelDefs.get(name); // label
//        if ( labels!=null ) {
//            // it's a label ref, compute scope from label type and grammar type
//            LabelElementPair anyLabelDef = labels.get(0);
//            return getPredefinedScope(anyLabelDef.type);
//        }
//        return null;
//    }
//
//    /** Look for name in the arg, return value, predefined property,
//     *  or dynamic scope attribute list for this rule.
//     */
//    public AttributeScope resolveLocalAttributeScope(String name) {
//        if ( args.get(name)!=null ) return args;
//        if ( retvals.get(name)!=null ) return retvals;
//        AttributeScope s = getPredefinedScope(LabelType.RULE_LABEL);
//        if ( s.get(name)!=null ) return s;
//        if ( scope!=null && scope.get(name)!=null ) return scope;
//        return null;
//    }

    public AttributeDict getPredefinedScope(LabelType ltype) {
        String grammarLabelKey = g.getTypeString() + ":" + ltype;
        return Grammar.grammarAndLabelRefTypeToScope.get(grammarLabelKey);
    }

    @Override
    public String toString() {
        return "Rule{" +
               "name='" + name + '\'' +
               ", args=" + args +
               ", retvals=" + retvals +
               ", scope=" + scope +
               '}';
    }
}
