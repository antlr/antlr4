package org.antlr.v4.semantics;

import org.antlr.runtime.Token;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.*;

import java.util.*;

/** Check for symbol problems; no side-effects.  Inefficient to walk rules
 *  and such multiple times, but I like isolating all error checking outside
 *  of code that actually defines symbols etc...
 *
 *  Side-effect: strip away redef'd rules.
 */
public class SymbolChecks {
    protected Grammar g;
    protected CollectSymbols collector;
    protected Map<String, Rule> nameToRuleMap = new HashMap<String, Rule>();
    protected Set<String> tokenIDs = new HashSet<String>();
    protected Set<String> globalScopeNames = new HashSet<String>();
    protected Map<String, Set<String>> actionScopeToActionNames = new HashMap<String, Set<String>>();

    public SymbolChecks(Grammar g, CollectSymbols collector) {
        this.g = g;
        this.collector = collector;
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

    public void examine() {
        // methods affect fields, but no side-effects outside this object
        // So, call order sensitive
        checkScopeRedefinitions(collector.scopes);      // sets globalScopeNames
		//checkForImportedRuleIssues(collector.qualifiedRulerefs);
        checkForRuleConflicts(collector.rules);         // sets nameToRuleMap
        checkActionRedefinitions(collector.actions);    // sets actionScopeToActionNames
        checkTokenAliasRedefinitions(collector.tokensDefs);
        //checkRuleArgs(collector.rulerefs);
        checkForTokenConflicts(collector.tokenIDRefs);  // sets tokenIDs
        checkForLabelConflicts(collector.rules);
        checkRewriteElementsPresentOnLeftSide(collector.rules);
    }

    public void checkForRuleConflicts(List<Rule> rules) {
        if ( rules==null ) return;
        for (Rule r : collector.rules) {
            if ( nameToRuleMap.get(r.name)==null ) {
                nameToRuleMap.put(r.name, r);
            }
            else {
                GrammarAST idNode = (GrammarAST)r.ast.getChild(0);
                ErrorManager.grammarError(ErrorType.RULE_REDEFINITION,
                                          g.fileName, idNode.token, r.name);
            }
            if ( globalScopeNames.contains(r.name) ) {
                GrammarAST idNode = (GrammarAST)r.ast.getChild(0);
                ErrorManager.grammarError(ErrorType.SYMBOL_CONFLICTS_WITH_GLOBAL_SCOPE,
                                          g.fileName, idNode.token, r.name);                
            }
        }
    }

    public void checkScopeRedefinitions(List<AttributeDict> dicts) {
        if ( dicts ==null ) return;
        for (int i=0; i< dicts.size(); i++) {
            AttributeDict s = dicts.get(i);
            //GrammarAST idNode = (GrammarAST)s.getChild(0);
            if ( !globalScopeNames.contains(s.getName()) ) {
                globalScopeNames.add(s.getName());
            }
            else {
                Token idNode = ((GrammarAST) s.ast.getChild(0)).token;
                ErrorManager.grammarError(ErrorType.SCOPE_REDEFINITION,
                                          g.fileName, idNode, s.getName());
            }
        }
    }

    public void checkTokenAliasRedefinitions(List<GrammarAST> aliases) {
        if ( aliases==null ) return;
        Map<String, GrammarAST> aliasTokenNames = new HashMap<String, GrammarAST>();
        for (int i=0; i< aliases.size(); i++) {
            GrammarAST a = aliases.get(i);
            GrammarAST idNode = a;
            if ( a.getType()== ANTLRParser.ASSIGN ) {
                idNode = (GrammarAST)a.getChild(0);
            }
            GrammarAST prev = aliasTokenNames.get(idNode.getText());
            if ( prev==null ) {
                aliasTokenNames.put(idNode.getText(), a);
            }
            else {
                GrammarAST value = (GrammarAST)prev.getChild(1);
                String valueText = null;
                if ( value!=null ) valueText = value.getText();
                ErrorManager.grammarError(ErrorType.TOKEN_ALIAS_REASSIGNMENT,
                                          g.fileName, idNode.token, idNode.getText(), valueText);
            }
        }
    }

    public void checkForTokenConflicts(List<GrammarAST> tokenIDRefs) {
        for (GrammarAST a : tokenIDRefs) {
            Token t = a.token;
            String ID = t.getText();
            tokenIDs.add(ID);
            if ( globalScopeNames.contains(t.getText()) ) {
                ErrorManager.grammarError(ErrorType.SYMBOL_CONFLICTS_WITH_GLOBAL_SCOPE,
                                          g.fileName, t, ID);
            }
        }
    }

    public void checkActionRedefinitions(List<GrammarAST> actions) {
        if ( actions==null ) return;
        String scope = g.getDefaultActionScope();
        String name = null;
        GrammarAST nameNode = null;
        for (GrammarAST ampersandAST : actions) {
            nameNode = (GrammarAST)ampersandAST.getChild(0);
            if ( ampersandAST.getChildCount()==2 ) {
                name = nameNode.getText();
            }
            else {
                scope = nameNode.getText();
                name = ampersandAST.getChild(1).getText();
            }
            Set<String> scopeActions = actionScopeToActionNames.get(scope);
            if ( scopeActions==null ) { // init scope
                scopeActions = new HashSet<String>();
                actionScopeToActionNames.put(scope, scopeActions);
            }
            if ( !scopeActions.contains(name) ) {
                scopeActions.add(name);
            }
            else {
                ErrorManager.grammarError(ErrorType.ACTION_REDEFINITION,
                                          g.fileName, nameNode.token, name);
            }
        }
    }

    /** Make sure a label doesn't conflict with another symbol.
     *  Labels must not conflict with: rules, tokens, scope names,
     *  return values, parameters, and rule-scope dynamic attributes
     *  defined in surrounding rule.  Also they must have same type
     *  for repeated defs.
     */
    public void checkForLabelConflicts(List<Rule> rules) {
        for (Rule r : rules) {
            checkForRuleArgumentAndReturnValueConflicts(r);
            checkForRuleScopeAttributeConflict(r);
            Map<String, LabelElementPair> labelNameSpace =
                new HashMap<String, LabelElementPair>();
            for (int i=1; i<=r.numberOfAlts; i++) {
                Alternative a = r.alt[i];
                for (List<LabelElementPair> pairs : a.labelDefs.values() ) {
                    for (LabelElementPair p : pairs) {
                        checkForLabelConflict(r, p.label);
                        String name = p.label.getText();
                        LabelElementPair prev = labelNameSpace.get(name);
                        if ( prev==null ) labelNameSpace.put(name, p);
                        else checkForTypeMismatch(prev, p);
                    }
                }
            }
        }
    }

    protected void checkForTypeMismatch(LabelElementPair prevLabelPair,
                                        LabelElementPair labelPair)
    {
        // label already defined; if same type, no problem
        if ( prevLabelPair.type != labelPair.type ) {
            String typeMismatchExpr = labelPair.type+"!="+prevLabelPair.type;
            ErrorManager.grammarError(
                ErrorType.LABEL_TYPE_CONFLICT,
                g.fileName,
                labelPair.label.token,
                labelPair.label.getText(),
                typeMismatchExpr);
        }
    }

    public void checkForLabelConflict(Rule r, GrammarAST labelID) {
        ErrorType etype = ErrorType.INVALID;
        Object arg2 = null;
        String name = labelID.getText();
        if ( globalScopeNames.contains(name) ) {
            etype = ErrorType.SYMBOL_CONFLICTS_WITH_GLOBAL_SCOPE;
        }
        else if ( nameToRuleMap.containsKey(name) ) {
            etype = ErrorType.LABEL_CONFLICTS_WITH_RULE;
        }
        else if ( tokenIDs.contains(name) ) {
            etype = ErrorType.LABEL_CONFLICTS_WITH_TOKEN;
        }
        else if ( r.scope !=null && r.scope.get(name)!=null ) {
            etype = ErrorType.LABEL_CONFLICTS_WITH_RULE_SCOPE_ATTRIBUTE;
            arg2 = r.name;
        }
        else if ( (r.retvals!=null&&r.retvals.get(name)!=null) ||
                  (r.args!=null&&r.args.get(name)!=null) )
        {
            etype = ErrorType.LABEL_CONFLICTS_WITH_RULE_ARG_RETVAL;
            arg2 = r.name;
        }
        if ( etype!=ErrorType.INVALID ) {
            ErrorManager.grammarError(etype,g.fileName,labelID.token,name,arg2);
        }
    }

    public void checkForRuleArgumentAndReturnValueConflicts(Rule r) {
        if ( r.retvals!=null ) {
            Set conflictingKeys = r.retvals.intersection(r.args);
            if (conflictingKeys!=null) {
                for (Iterator it = conflictingKeys.iterator(); it.hasNext();) {
                    String key = (String) it.next();
                    ErrorManager.grammarError(
                        ErrorType.ARG_RETVAL_CONFLICT,
                        g.fileName,
                        ((GrammarAST)r.ast.getChild(0)).token,
                        key,
                        r.name);
                }
            }
        }
    }

    /** Check for collision of a rule-scope dynamic attribute with:
     *  arg, return value, rule name itself.  Labels are checked elsewhere.
     */
    public void checkForRuleScopeAttributeConflict(Rule r) {
        if ( r.scope ==null ) return;
        for (Attribute a : r.scope.attributes.values()) {
            ErrorType msgID = ErrorType.INVALID;
            Object arg2 = null;
            String attrName = a.name;
            if ( r.name.equals(attrName) ) {
                msgID = ErrorType.ATTRIBUTE_CONFLICTS_WITH_RULE;
                arg2 = r.name;
            }
            else if ( (r.retvals!=null&&r.retvals.get(attrName)!=null) ||
                      (r.args!=null&&r.args.get(attrName)!=null) )
            {
                msgID = ErrorType.ATTRIBUTE_CONFLICTS_WITH_RULE_ARG_RETVAL;
                arg2 = r.name;
            }
            if ( msgID!=ErrorType.INVALID ) {
                ErrorManager.grammarError(msgID,g.fileName,
                                          r.scope.ast.token,
                                          attrName,arg2);
            }
        }
    }

    public void checkRewriteElementsPresentOnLeftSide(List<Rule> rules) {
        for (Rule r : rules) {
            for (int a=1; a<=r.numberOfAlts; a++) {
                Alternative alt = r.alt[a];
                for (GrammarAST e : alt.rewriteElements) {
                    if ( !(alt.ruleRefs.containsKey(e.getText()) ||
                           alt.tokenRefs.containsKey(e.getText()) ||
                           alt.labelDefs.containsKey(e.getText())) ) {
                        ErrorManager.grammarError(ErrorType.REWRITE_ELEMENT_NOT_PRESENT_ON_LHS,
                                                  g.fileName, e.token, e.getText());
                    }
                }
            }
        }
    }
}
