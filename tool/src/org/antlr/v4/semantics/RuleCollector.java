/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.semantics;

import org.antlr.v4.analysis.LeftRecursiveRuleAnalyzer;
import org.antlr.v4.misc.OrderedHashMap;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.GrammarTreeVisitor;
import org.antlr.v4.parse.ScopeParser;
import org.antlr.v4.tool.*;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RuleCollector extends GrammarTreeVisitor {
	private boolean grammarCaseInsensitive = false;

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
	public void grammarOption(GrammarAST ID, GrammarAST valueAST) {
		Boolean caseInsensitive = getCaseInsensitiveValue(ID, valueAST);
		if (caseInsensitive != null) {
			grammarCaseInsensitive = caseInsensitive;
		}
	}

	@Override
	public void discoverLexerRule(RuleAST rule, GrammarAST ID, List<GrammarAST> modifiers,
								  GrammarAST options, GrammarAST block)
	{
		boolean currentCaseInsensitive = grammarCaseInsensitive;
		if (options != null) {
			for (Object child : options.getChildren()) {
				GrammarAST childAST = (GrammarAST) child;
				Boolean caseInsensitive = getCaseInsensitiveValue((GrammarAST)childAST.getChild(0), (GrammarAST)childAST.getChild(1));
				if (caseInsensitive != null) {
					currentCaseInsensitive = caseInsensitive;
				}
			}
		}

		int numAlts = block.getChildCount();
		Rule r = new Rule(g, ID.getText(), rule, numAlts, currentModeName, currentCaseInsensitive);
		if ( !modifiers.isEmpty() ) r.modifiers = modifiers;
		rules.put(r.name, r);
	}

	private Boolean getCaseInsensitiveValue(GrammarAST optionID, GrammarAST valueAST) {
		String optionName = optionID.getText();
		if (optionName.equals(Grammar.caseInsensitiveOptionName)) {
			String valueText = valueAST.getText();
			if (valueText.equals("true") || valueText.equals("false")) {
				return Boolean.parseBoolean(valueText);
			}
		}
		return null;
	}
}
