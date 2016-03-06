/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.tool;


import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.TerminalAST;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.ArrayList;
import java.util.List;

/** An outermost alternative for a rule.  We don't track inner alternatives. */
public class Alternative implements AttributeResolver {
    public Rule rule;

	public AltAST ast;

	/** What alternative number is this outermost alt? 1..n */
	public int altNum;

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
    //public List<GrammarAST> rewriteElements = new ArrayList<GrammarAST>();

    /** Track all executable actions other than named actions like @init
     *  and catch/finally (not in an alt). Also tracks predicates, rewrite actions.
     *  We need to examine these actions before code generation so
     *  that we can detect refs to $rule.attr etc...
	 *
	 *  This tracks per alt
     */
    public List<ActionAST> actions = new ArrayList<ActionAST>();

    public Alternative(Rule r, int altNum) { this.rule = r; this.altNum = altNum; }

	@Override
	public boolean resolvesToToken(String x, ActionAST node) {
		if ( tokenRefs.get(x)!=null ) return true;
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		if ( anyLabelDef!=null && anyLabelDef.type==LabelType.TOKEN_LABEL ) return true;
		return false;
	}

	@Override
	public boolean resolvesToAttributeDict(String x, ActionAST node) {
		if ( resolvesToToken(x, node) ) return true;
        if ( ruleRefs.get(x)!=null ) return true; // rule ref in this alt?
        LabelElementPair anyLabelDef = getAnyLabelDef(x);
        if ( anyLabelDef!=null && anyLabelDef.type==LabelType.RULE_LABEL ) return true;
		return false;
	}

	/**  $x		Attribute: rule arguments, return values, predefined rule prop.
	 */
	@Override
	public Attribute resolveToAttribute(String x, ActionAST node) {
		return rule.resolveToAttribute(x, node); // reuse that code
	}

	/** $x.y, x can be surrounding rule, token/rule/label ref. y is visible
	 *  attr in that dictionary.  Can't see args on rule refs.
	 */
	@Override
	public Attribute resolveToAttribute(String x, String y, ActionAST node) {
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
			AttributeDict scope = rule.getPredefinedScope(anyLabelDef.type);
			if (scope == null) {
				return null;
			}

			return scope.get(y);
		}
		return null;
	}

	@Override
	public boolean resolvesToLabel(String x, ActionAST node) {
		LabelElementPair anyLabelDef = getAnyLabelDef(x);
		return anyLabelDef!=null &&
			   (anyLabelDef.type==LabelType.TOKEN_LABEL ||
				anyLabelDef.type==LabelType.RULE_LABEL);
	}

	@Override
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
        return null;
    }
}
