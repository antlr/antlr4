/*
 * Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.semantics;

import org.antlr.v4.Tool;
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
	protected void exitGrammarSpec(GrammarAST tree) { 
//		System.out.println("exitGrammarSpec---------------" );
//
//		if( g.tool.importParams != null ) {
//			for (Rule r : g.rules.values()) {
//				String importedG = g.tool.importRules_Alts.get(r.name);
//				if ( importedG != null ) {
//					String prefix = g.tool.importParamsMap.get(importedG).prefix;
//					r.prefix = prefix;
//					r.imported = true;
//				}
//			}		
//		}
	}
	
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
//			System.out.println("RuleCollector " + g.name.equals(rule.g.name) + " \t" + rule.getRuleName() + " " + g.name + " " + rule.g.name);
			if ( g.tool.importParams != null && !g.name.equals(rule.g.name) ) {
				String prefix = g.tool.importParamsMap.get(rule.g.name).prefix;
				g.tool.importRules_Alts.put(ID.getText(),rule.g.name);
				r = new LeftRecursiveRule(g, ID.getText(), rule, prefix, true);
			} else {
				r = new LeftRecursiveRule(g, ID.getText(), rule, "", false);
			}
		}
		else {
//			System.out.println("RuleCollector " + g.name.equals(rule.g.name) + " \t" + rule.getRuleName() + " " + g.name + " " + rule.g.name);
			if ( g.tool.importParams != null && !g.name.equals(rule.g.name) ) {
				String prefix = g.tool.importParamsMap.get(rule.g.name).prefix;
				g.tool.importRules_Alts.put(ID.getText(),rule.g.name);
				r = new Rule(g, ID.getText(), rule, numAlts, prefix, true, rule.isExtention);
			} else {
				r = new Rule(g, ID.getText(), rule, numAlts, "", false, rule.isExtention);
			}
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
			if ( !g.name.equals(alt.g.name) ) {
				g.tool.importRules_Alts.put(altLabel,alt.g.name);
			}

		}
	}

	@Override
	public void discoverLexerRule(RuleAST rule, GrammarAST ID, List<GrammarAST> modifiers,
								  GrammarAST block)
	{
		int numAlts = block.getChildCount();
		Rule r ;
		if ( g.tool.importParams != null && !g.name.equals(rule.g.name) ) {
			String prefix = g.tool.importParamsMap.get(rule.g.name).prefix;
			g.tool.importRules_Alts.put(ID.getText(),rule.g.name);
			r = new Rule(g, ID.getText(), rule, numAlts, prefix, true, rule.isExtention);
		} else {
			r = new Rule(g, ID.getText(), rule, numAlts, "", false, rule.isExtention);
		}

		r.mode = currentModeName;
		if ( !modifiers.isEmpty() ) r.modifiers = modifiers;
		rules.put(r.name, r);
	}
}
