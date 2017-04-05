/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.semantics;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.tool.Alternative;
import org.antlr.v4.tool.Attribute;
import org.antlr.v4.tool.AttributeDict;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LabelElementPair;
import org.antlr.v4.tool.LabelType;
import org.antlr.v4.tool.LeftRecursiveRule;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Check for symbol problems; no side-effects.  Inefficient to walk rules
 *  and such multiple times, but I like isolating all error checking outside
 *  of code that actually defines symbols etc...
 *
 *  Side-effect: strip away redef'd rules.
 */
public class SymbolChecks {
    Grammar g;
    SymbolCollector collector;
    Map<String, Rule> nameToRuleMap = new HashMap<String, Rule>();
	Set<String> tokenIDs = new HashSet<String>();
    Map<String, Set<String>> actionScopeToActionNames = new HashMap<String, Set<String>>();
//	DoubleKeyMap<String, String, GrammarAST> namedActions =
//		new DoubleKeyMap<String, String, GrammarAST>();

	public ErrorManager errMgr;

	protected final Set<String> reservedNames = new HashSet<String>();
	{
		reservedNames.addAll(LexerATNFactory.getCommonConstants());
	}

    public SymbolChecks(Grammar g, SymbolCollector collector) {
        this.g = g;
        this.collector = collector;
		this.errMgr = g.tool.errMgr;

        for (GrammarAST tokenId : collector.tokenIDRefs) {
            tokenIDs.add(tokenId.getText());
        }
        /*
        System.out.println("rules="+collector.rules);
        System.out.println("rulerefs="+collector.rulerefs);
        System.out.println("tokenIDRefs="+collector.tokenIDRefs);
        System.out.println("terminals="+collector.terminals);
        System.out.println("strings="+collector.strings);
        System.out.println("tokensDef="+collector.tokensDefs);
        System.out.println("actions="+collector.actions);
        System.out.println("scopes="+collector.scopes);
         */
    }

    public void process() {
		// methods affect fields, but no side-effects outside this object
		// So, call order sensitive
		// First collect all rules for later use in checkForLabelConflict()
		if (g.rules != null) {
			for (Rule r : g.rules.values()) nameToRuleMap.put(r.name, r);
		}
		checkReservedNames(g.rules.values());
		checkActionRedefinitions(collector.namedActions);
		checkForTokenConflicts(collector.tokenIDRefs);  // sets tokenIDs
		checkForLabelConflicts(g.rules.values());
	}

	public void checkActionRedefinitions(List<GrammarAST> actions) {
		if (actions == null) return;
		String scope = g.getDefaultActionScope();
		String name;
		GrammarAST nameNode;
		for (GrammarAST ampersandAST : actions) {
			nameNode = (GrammarAST) ampersandAST.getChild(0);
			if (ampersandAST.getChildCount() == 2) {
				name = nameNode.getText();
			}
			else {
				scope = nameNode.getText();
				name = ampersandAST.getChild(1).getText();
			}
			Set<String> scopeActions = actionScopeToActionNames.get(scope);
			if (scopeActions == null) { // init scope
				scopeActions = new HashSet<String>();
				actionScopeToActionNames.put(scope, scopeActions);
			}
			if (!scopeActions.contains(name)) {
				scopeActions.add(name);
			}
			else {
				errMgr.grammarError(ErrorType.ACTION_REDEFINITION,
						g.fileName, nameNode.token, name);
			}
		}
	}

	public void checkForTokenConflicts(List<GrammarAST> tokenIDRefs) {
//        for (GrammarAST a : tokenIDRefs) {
//            Token t = a.token;
//            String ID = t.getText();
//            tokenIDs.add(ID);
//        }
	}

    /** Make sure a label doesn't conflict with another symbol.
     *  Labels must not conflict with: rules, tokens, scope names,
     *  return values, parameters, and rule-scope dynamic attributes
     *  defined in surrounding rule.  Also they must have same type
     *  for repeated defs.
     */
    public void checkForLabelConflicts(Collection<Rule> rules) {
		for (Rule r : rules) {
			checkForAttributeConflicts(r);

			Map<String, LabelElementPair> labelNameSpace = new HashMap<>();
			for (int i = 1; i <= r.numberOfAlts; i++) {
				Alternative a = r.alt[i];
				for (List<LabelElementPair> pairs : a.labelDefs.values()) {
					if (r.hasAltSpecificContexts()) {
						// Collect labelName-labeledRules map for rule with alternative labels.
						Map<String, List<LabelElementPair>> labelPairs = new HashMap<>();
						for (LabelElementPair p : pairs) {
							String labelName = findAltLabelName(p.label);
							if (labelName != null) {
								List<LabelElementPair> list;
								if (labelPairs.containsKey(labelName)) {
									list = labelPairs.get(labelName);
								}
								else {
									list = new ArrayList<>();
									labelPairs.put(labelName, list);
								}
								list.add(p);
							}
						}

						for (List<LabelElementPair> internalPairs : labelPairs.values()) {
							labelNameSpace.clear();
							checkLabelPairs(r, labelNameSpace, internalPairs);
						}
					}
					else {
						checkLabelPairs(r, labelNameSpace, pairs);
					}
				}
			}
		}
	}

	private void checkLabelPairs(Rule r, Map<String, LabelElementPair> labelNameSpace, List<LabelElementPair> pairs) {
		for (LabelElementPair p : pairs) {
			checkForLabelConflict(r, p.label);
			String name = p.label.getText();
			LabelElementPair prev = labelNameSpace.get(name);
			if (prev == null) {
				labelNameSpace.put(name, p);
			}
			else {
				checkForTypeMismatch(r, prev, p);
			}
		}
	}

	private String findAltLabelName(CommonTree label) {
		if (label == null) {
			return null;
		}
		else if (label instanceof AltAST) {
			AltAST altAST = (AltAST) label;
			if (altAST.altLabel != null) {
				return altAST.altLabel.toString();
			}
			else if (altAST.leftRecursiveAltInfo != null) {
				return altAST.leftRecursiveAltInfo.altLabel.toString();
			}
			else {
				return findAltLabelName(label.parent);
			}
		}
		else {
			return findAltLabelName(label.parent);
		}
	}

	private void checkForTypeMismatch(Rule r, LabelElementPair prevLabelPair, LabelElementPair labelPair) {
		// label already defined; if same type, no problem
		if (prevLabelPair.type != labelPair.type) {
			// Current behavior: take a token of rule declaration in case of left-recursive rule
			// Desired behavior: take a token of proper label declaration in case of left-recursive rule
			// See https://github.com/antlr/antlr4/pull/1585
			// Such behavior is referring to the fact that the warning is typically reported on the actual label redefinition,
			//   but for left-recursive rules the warning is reported on the enclosing rule.
			org.antlr.runtime.Token token = r instanceof LeftRecursiveRule
					?  ((GrammarAST) r.ast.getChild(0)).getToken()
					: labelPair.label.token;
			errMgr.grammarError(
					ErrorType.LABEL_TYPE_CONFLICT,
					g.fileName,
					token,
					labelPair.label.getText(),
					labelPair.type + "!=" + prevLabelPair.type);
		}
		if (!prevLabelPair.element.getText().equals(labelPair.element.getText()) &&
			(prevLabelPair.type.equals(LabelType.RULE_LABEL) || prevLabelPair.type.equals(LabelType.RULE_LIST_LABEL)) &&
			(labelPair.type.equals(LabelType.RULE_LABEL) || labelPair.type.equals(LabelType.RULE_LIST_LABEL))) {

			org.antlr.runtime.Token token = r instanceof LeftRecursiveRule
					?  ((GrammarAST) r.ast.getChild(0)).getToken()
					: labelPair.label.token;
			String prevLabelOp = prevLabelPair.type.equals(LabelType.RULE_LIST_LABEL) ? "+=" : "=";
			String labelOp = labelPair.type.equals(LabelType.RULE_LIST_LABEL) ? "+=" : "=";
			errMgr.grammarError(
					ErrorType.LABEL_TYPE_CONFLICT,
					g.fileName,
					token,
					labelPair.label.getText() + labelOp + labelPair.element.getText(),
					prevLabelPair.label.getText() + prevLabelOp + prevLabelPair.element.getText());
		}
	}

	public void checkForLabelConflict(Rule r, GrammarAST labelID) {
		String name = labelID.getText();
		if (nameToRuleMap.containsKey(name)) {
			ErrorType etype = ErrorType.LABEL_CONFLICTS_WITH_RULE;
			errMgr.grammarError(etype, g.fileName, labelID.token, name, r.name);
		}

		if (tokenIDs.contains(name)) {
			ErrorType etype = ErrorType.LABEL_CONFLICTS_WITH_TOKEN;
			errMgr.grammarError(etype, g.fileName, labelID.token, name, r.name);
		}

		if (r.args != null && r.args.get(name) != null) {
			ErrorType etype = ErrorType.LABEL_CONFLICTS_WITH_ARG;
			errMgr.grammarError(etype, g.fileName, labelID.token, name, r.name);
		}

		if (r.retvals != null && r.retvals.get(name) != null) {
			ErrorType etype = ErrorType.LABEL_CONFLICTS_WITH_RETVAL;
			errMgr.grammarError(etype, g.fileName, labelID.token, name, r.name);
		}

		if (r.locals != null && r.locals.get(name) != null) {
			ErrorType etype = ErrorType.LABEL_CONFLICTS_WITH_LOCAL;
			errMgr.grammarError(etype, g.fileName, labelID.token, name, r.name);
		}
	}

	public void checkForAttributeConflicts(Rule r) {
		checkDeclarationRuleConflicts(r, r.args, nameToRuleMap.keySet(), ErrorType.ARG_CONFLICTS_WITH_RULE);
		checkDeclarationRuleConflicts(r, r.args, tokenIDs, ErrorType.ARG_CONFLICTS_WITH_TOKEN);

		checkDeclarationRuleConflicts(r, r.retvals, nameToRuleMap.keySet(), ErrorType.RETVAL_CONFLICTS_WITH_RULE);
		checkDeclarationRuleConflicts(r, r.retvals, tokenIDs, ErrorType.RETVAL_CONFLICTS_WITH_TOKEN);

		checkDeclarationRuleConflicts(r, r.locals, nameToRuleMap.keySet(), ErrorType.LOCAL_CONFLICTS_WITH_RULE);
		checkDeclarationRuleConflicts(r, r.locals, tokenIDs, ErrorType.LOCAL_CONFLICTS_WITH_TOKEN);

		checkLocalConflictingDeclarations(r, r.retvals, r.args, ErrorType.RETVAL_CONFLICTS_WITH_ARG);
		checkLocalConflictingDeclarations(r, r.locals, r.args, ErrorType.LOCAL_CONFLICTS_WITH_ARG);
		checkLocalConflictingDeclarations(r, r.locals, r.retvals, ErrorType.LOCAL_CONFLICTS_WITH_RETVAL);
	}

	protected void checkDeclarationRuleConflicts(Rule r, AttributeDict attributes, Set<String> ruleNames, ErrorType errorType) {
		if (attributes == null) {
			return;
		}

		for (Attribute attribute : attributes.attributes.values()) {
			if (ruleNames.contains(attribute.name)) {
				errMgr.grammarError(
					errorType,
					g.fileName,
					attribute.token != null ? attribute.token : ((GrammarAST)r.ast.getChild(0)).token,
					attribute.name,
					r.name);
			}
		}
	}

	protected void checkLocalConflictingDeclarations(Rule r, AttributeDict attributes, AttributeDict referenceAttributes, ErrorType errorType) {
		if (attributes == null || referenceAttributes == null) {
			return;
		}

		Set<String> conflictingKeys = attributes.intersection(referenceAttributes);
		for (String key : conflictingKeys) {
			errMgr.grammarError(
				errorType,
				g.fileName,
				attributes.get(key).token != null ? attributes.get(key).token : ((GrammarAST) r.ast.getChild(0)).token,
				key,
				r.name);
		}
	}

	protected void checkReservedNames(Collection<Rule> rules) {
		for (Rule rule : rules) {
			if (reservedNames.contains(rule.name)) {
				errMgr.grammarError(ErrorType.RESERVED_RULE_NAME, g.fileName, ((GrammarAST)rule.ast.getChild(0)).getToken(), rule.name);
			}
		}
	}

	public void checkForModeConflicts(Grammar g) {
		if (g.isLexer()) {
			LexerGrammar lexerGrammar = (LexerGrammar)g;
			for (String modeName : lexerGrammar.modes.keySet()) {
				if (!modeName.equals("DEFAULT_MODE") && reservedNames.contains(modeName)) {
					Rule rule = lexerGrammar.modes.get(modeName).iterator().next();
					g.tool.errMgr.grammarError(ErrorType.MODE_CONFLICTS_WITH_COMMON_CONSTANTS, g.fileName, rule.ast.parent.getToken(), modeName);
				}

				if (g.getTokenType(modeName) != Token.INVALID_TYPE) {
					Rule rule = lexerGrammar.modes.get(modeName).iterator().next();
					g.tool.errMgr.grammarError(ErrorType.MODE_CONFLICTS_WITH_TOKEN, g.fileName, rule.ast.parent.getToken(), modeName);
				}
			}
		}
	}

	// CAN ONLY CALL THE TWO NEXT METHODS AFTER GRAMMAR HAS RULE DEFS (see semanticpipeline)

	public void checkRuleArgs(Grammar g, List<GrammarAST> rulerefs) {
		if ( rulerefs==null ) return;
		for (GrammarAST ref : rulerefs) {
			String ruleName = ref.getText();
			Rule r = g.getRule(ruleName);
			GrammarAST arg = (GrammarAST)ref.getFirstChildWithType(ANTLRParser.ARG_ACTION);
			if ( arg!=null && (r==null || r.args==null) ) {
				errMgr.grammarError(ErrorType.RULE_HAS_NO_ARGS,
										  g.fileName, ref.token, ruleName);

			}
			else if ( arg==null && (r!=null&&r.args!=null) ) {
				errMgr.grammarError(ErrorType.MISSING_RULE_ARGS,
										  g.fileName, ref.token, ruleName);
			}
		}
	}

	public void checkForQualifiedRuleIssues(Grammar g, List<GrammarAST> qualifiedRuleRefs) {
		for (GrammarAST dot : qualifiedRuleRefs) {
			GrammarAST grammar = (GrammarAST)dot.getChild(0);
			GrammarAST rule = (GrammarAST)dot.getChild(1);
            g.tool.log("semantics", grammar.getText()+"."+rule.getText());
			Grammar delegate = g.getImportedGrammar(grammar.getText());
			if ( delegate==null ) {
				errMgr.grammarError(ErrorType.NO_SUCH_GRAMMAR_SCOPE,
										  g.fileName, grammar.token, grammar.getText(),
										  rule.getText());
			}
			else {
				if ( g.getRule(grammar.getText(), rule.getText())==null ) {
					errMgr.grammarError(ErrorType.NO_SUCH_RULE_IN_SCOPE,
											  g.fileName, rule.token, grammar.getText(),
											  rule.getText());
				}
			}
		}
	}
}
