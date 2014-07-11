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

package org.antlr.v4.semantics;

import org.antlr.v4.analysis.LeftRecursiveRuleAnalyzer;
import org.antlr.v4.misc.OrderedHashMap;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.GrammarTreeVisitor;
import org.antlr.v4.parse.ScopeParser;
import org.antlr.v4.tool.AttributeDict;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LeftRecursiveRule;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleCollector extends GrammarTreeVisitor {
	/** which grammar are we checking */
	public Grammar g;
	public ErrorManager errMgr;

	// stuff to collect. this is the output
	public OrderedHashMap<String, Rule> rules = new OrderedHashMap<String, Rule>();
	public MultiMap<String,GrammarAST> ruleToAltLabels = new MultiMap<String, GrammarAST>();
	public Map<String,String> altLabelToRuleName = new HashMap<String, String>();

	public RuleCollector(Grammar g) {
		this.g = g;
		this.errMgr = g.tool.errMgr;
	}

	@Override
	public ErrorManager getErrorManager() { return errMgr; }

	public void process(GrammarAST ast) { visitGrammar(ast); }

	@Override
	public void discoverRule(RuleAST rule, GrammarAST ID,
							 List<GrammarAST> modifiers, ActionAST arg,
							 ActionAST returns, GrammarAST thrws,
							 GrammarAST options, ActionAST locals,
							 List<GrammarAST> actions,
							 GrammarAST block)
	{
		int numAlts = block.getChildCount();
		Rule r;
		if ( LeftRecursiveRuleAnalyzer.hasImmediateRecursiveRuleRefs(rule, ID.getText()) ) {
			r = new LeftRecursiveRule(g, ID.getText(), rule);
		}
		else {
			r = new Rule(g, ID.getText(), rule, numAlts);
		}
		rules.put(r.name, r);

		if ( arg!=null ) {
			r.args = ScopeParser.parseTypedArgList(arg, arg.getText(), g);
			r.args.type = AttributeDict.DictType.ARG;
			r.args.ast = arg;
			arg.resolver = r.alt[currentOuterAltNumber];
		}

		if ( returns!=null ) {
			r.retvals = ScopeParser.parseTypedArgList(returns, returns.getText(), g);
			r.retvals.type = AttributeDict.DictType.RET;
			r.retvals.ast = returns;
		}

		if ( locals!=null ) {
			r.locals = ScopeParser.parseTypedArgList(locals, locals.getText(), g);
			r.locals.type = AttributeDict.DictType.LOCAL;
			r.locals.ast = locals;
		}

		for (GrammarAST a : actions) {
			// a = ^(AT ID ACTION)
			ActionAST action = (ActionAST) a.getChild(1);
			r.namedActions.put(a.getChild(0).getText(), action);
			action.resolver = r;
		}
	}

	@Override
	public void discoverOuterAlt(AltAST alt) {
		if ( alt.altLabel!=null ) {
			ruleToAltLabels.map(currentRuleName, alt.altLabel);
			String altLabel = alt.altLabel.getText();
			altLabelToRuleName.put(Utils.capitalize(altLabel), currentRuleName);
			altLabelToRuleName.put(Utils.decapitalize(altLabel), currentRuleName);
		}
	}

	@Override
	public void discoverLexerRule(RuleAST rule, GrammarAST ID, List<GrammarAST> modifiers,
								  GrammarAST block)
	{
		int numAlts = block.getChildCount();
		Rule r = new Rule(g, ID.getText(), rule, numAlts);
		r.mode = currentModeName;
		if ( !modifiers.isEmpty() ) r.modifiers = modifiers;
		rules.put(r.name, r);
	}
}
