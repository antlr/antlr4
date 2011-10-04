/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.tool;

import org.stringtemplate.v4.misc.MultiMap;

import java.util.*;

public class Rule implements AttributeResolver {
	    /** Rule refs have a predefined set of attributes as well as
     *  the return values and args.
     */
    public static AttributeDict predefinedRulePropertiesDict =
        new AttributeDict(AttributeDict.DictType.PREDEFINED_RULE) {{
            add(new Attribute("text"));
            add(new Attribute("start"));
            add(new Attribute("stop"));
            add(new Attribute("tree"));
            add(new Attribute("st"));
        }};

    public static AttributeDict predefinedTreeRulePropertiesDict =
        new AttributeDict(AttributeDict.DictType.PREDEFINED_TREE_RULE) {{
            add(new Attribute("text"));
            add(new Attribute("start")); // note: no stop; not meaningful
            add(new Attribute("tree"));
            add(new Attribute("st"));
        }};

    public static AttributeDict predefinedLexerRulePropertiesDict =
        new AttributeDict(AttributeDict.DictType.PREDEFINED_LEXER_RULE) {{
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
	public List<GrammarAST> modifiers;

    public RuleAST ast;
    public AttributeDict args;
    public AttributeDict retvals;
	public AttributeDict locals;
    public AttributeDict scope; // scope { int i; } // TODO: remove

	/** In which grammar does this rule live? */
	public Grammar g;

	/** If we're in a lexer grammar, we might be in a mode */
	public String mode;

    /** Map a name to an action for this rule like @init {...}.
     *  The code generator will use this to fill holes in the rule template.
     *  I track the AST node for the action in case I need the line number
     *  for errors.
     */
    public Map<String, ActionAST> namedActions =
        new HashMap<String, ActionAST>();

    /** Track exception handler actions (exception type is prev child);
	 *  don't track finally action
	 */
    public List<ActionAST> exceptionActions = new ArrayList<ActionAST>();

	/** Track all executable actions other than named actions like @init
	 *  and catch/finally (not in an alt). Also tracks predicates, rewrite actions.
	 *  We need to examine these actions before code generation so
	 *  that we can detect refs to $rule.attr etc...
	 *
	 *  This tracks per rule; Alternative objs also track per alt.
	 */
	public List<ActionAST> actions = new ArrayList<ActionAST>();

	public ActionAST finallyAction;

	public int numberOfAlts;

	public boolean isStartRule = true; // nobody calls us

	/** 1..n alts */
	public Alternative[] alt;

	/** All rules have unique index 0..n-1 */
	public int index;

	public int actionIndex = -1; // if lexer; 0..n-1

	public Rule(Grammar g, String name, RuleAST ast, int numberOfAlts) {
		this.g = g;
		this.name = name;
		this.ast = ast;
		this.numberOfAlts = numberOfAlts;
		alt = new Alternative[numberOfAlts+1]; // 1..n
		for (int i=1; i<=numberOfAlts; i++) alt[i] = new Alternative(this, i);
	}

	public void defineActionInAlt(int currentAlt, ActionAST actionAST) {
		actions.add(actionAST);
		alt[currentAlt].actions.add(actionAST);
		if ( g.isLexer() ) {
			actionIndex = g.lexerActions.size();
			if ( g.lexerActions.get(actionAST)==null ) {
				g.lexerActions.put(actionAST, actionIndex);
			}
		}
	}

	public void definePredicateInAlt(int currentAlt, PredAST predAST) {
		actions.add(predAST);
		alt[currentAlt].actions.add(predAST);
		if ( g.sempreds.get(predAST)==null ) {
			g.sempreds.put(predAST, g.sempreds.size());
		}
	}

	public Attribute resolveRetvalOrProperty(String y) {
		if ( retvals!=null ) {
			Attribute a = retvals.get(y);
			if ( a!=null ) return a;
		}
		AttributeDict d = getPredefinedScope(LabelType.RULE_LABEL);
		return d.get(y);
	}

	public Set<String> getTokenRefs() {
        Set<String> refs = new HashSet<String>();
		for (int i=1; i<=numberOfAlts; i++) {
			refs.addAll(alt[i].tokenRefs.keySet());
		}
		return refs;
    }

    public Set<String> getElementLabelNames() {
        Set<String> refs = new HashSet<String>();
        for (int i=1; i<=numberOfAlts; i++) {
            refs.addAll(alt[i].labelDefs.keySet());
        }
		if ( refs.size()==0 ) return null;
        return refs;
    }

    public MultiMap<String, LabelElementPair> getElementLabelDefs() {
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

	public List<String> getAltLabels() {
		List<String> labels = new ArrayList<String>();
		for (int i=1; i<=numberOfAlts; i++) {
			GrammarAST altLabel = alt[i].ast.altLabel;
			if ( altLabel==null ) break; // all or none
			labels.add(altLabel.getText());
		}
		if ( labels.size()==0 ) return null;
		return labels;
	}

	/**  $x		Attribute: rule arguments, return values, predefined rule prop.
	 */
	public Attribute resolveToAttribute(String x, ActionAST node) {
		if ( args!=null ) {
			Attribute a = args.get(x);   	if ( a!=null ) return a;
		}
		if ( retvals!=null ) {
			Attribute a = retvals.get(x);	if ( a!=null ) return a;
		}
		if ( locals!=null ) {
			Attribute a = locals.get(x);	if ( a!=null ) return a;
		}
		AttributeDict properties = getPredefinedScope(LabelType.RULE_LABEL);
		return properties.get(x);
	}

	/** $x.y	Attribute: x is surrounding rule, label ref (in any alts) */
	public Attribute resolveToAttribute(String x, String y, ActionAST node) {
		if ( this.name.equals(x) ) { // x is this rule?
			return resolveToAttribute(y, node);
		}
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		if ( anyLabelDef!=null ) {
			if ( anyLabelDef.type==LabelType.RULE_LABEL ) {
				return g.getRule(anyLabelDef.element.getText()).resolveRetvalOrProperty(y);
			}
			else {
				return getPredefinedScope(anyLabelDef.type).get(y);
			}
		}
		return null;

	}

	public boolean resolvesToLabel(String x, ActionAST node) {
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		return anyLabelDef!=null &&
			   (anyLabelDef.type==LabelType.RULE_LABEL ||
				anyLabelDef.type==LabelType.TOKEN_LABEL);
	}

	public boolean resolvesToListLabel(String x, ActionAST node) {
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		return anyLabelDef!=null &&
			   (anyLabelDef.type==LabelType.RULE_LIST_LABEL ||
				anyLabelDef.type==LabelType.TOKEN_LIST_LABEL);
	}

	public boolean resolvesToToken(String x, ActionAST node) {
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		if ( anyLabelDef!=null && anyLabelDef.type==LabelType.TOKEN_LABEL ) return true;
		return false;
	}

	public boolean resolvesToAttributeDict(String x, ActionAST node) {
		if ( resolvesToToken(x, node) ) return true;
		if ( x.equals(name) ) return true; // $r for action in rule r, $r is a dict
		if ( scope!=null ) return true;
		return false;
	}

	public Rule resolveToRule(String x) {
		if ( x.equals(this.name) ) return this;
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		if ( anyLabelDef!=null && anyLabelDef.type==LabelType.RULE_LABEL ) {
			return g.getRule(anyLabelDef.element.getText());
		}
		return g.getRule(x);
	}

	public LabelElementPair getAnyLabelDef(String x) {
		List<LabelElementPair> labels = getElementLabelDefs().get(x);
		if ( labels!=null ) return labels.get(0);
		return null;
	}

    public AttributeDict getPredefinedScope(LabelType ltype) {
        String grammarLabelKey = g.getTypeString() + ":" + ltype;
        return Grammar.grammarAndLabelRefTypeToScope.get(grammarLabelKey);
    }

	public boolean isFragment() {
		if ( modifiers==null ) return false;
		for (GrammarAST a : modifiers) {
			if ( a.getText().equals("fragment") ) return true;
		}
		return false;
	}

	@Override
	public int hashCode() { return name.hashCode(); }

	@Override
	public boolean equals(Object obj) {
		return this==obj || name.equals(((Rule)obj).name);
	}

	@Override
    public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("Rule{name="+name);
		if ( args!=null ) buf.append(", args=" + args);
		if ( retvals!=null ) buf.append(", retvals=" + retvals);
		if ( scope!=null ) buf.append(", scope=" + scope);
		buf.append("}");
		return buf.toString();
    }
}
