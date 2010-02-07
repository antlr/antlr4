package org.antlr.v4.semantics;

import org.antlr.runtime.Token;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.*;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.*;

/** Check for symbol problems; no side-effects.  Inefficient to walk rules
 *  and such multiple times, but I like isolating all error checking outside
 *  of code that actually defines symbols etc...
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
        checkForRuleConflicts(collector.rules);         // sets nameToRuleMap
        checkActionRedefinitions(collector.actions);    // sets actionScopeToActionNames
        checkTokenAliasRedefinitions(collector.tokensDefs);
        checkRuleArgs(collector.rulerefs);
        checkForTokenConflicts(collector.tokenIDRefs);  // sets tokenIDs
        checkForLabelConflicts(collector.ruleToLabelSpace, collector.rules);
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

    public void checkScopeRedefinitions(List<GrammarAST> scopes) {
        if ( scopes==null ) return;
        for (int i=0; i< scopes.size(); i++) {
            GrammarAST s = scopes.get(i);
            GrammarAST idNode = (GrammarAST)s.getChild(0);
            if ( !globalScopeNames.contains(idNode.getText()) ) {
                globalScopeNames.add(idNode.getText());
            }
            else {
                ErrorManager.grammarError(ErrorType.SCOPE_REDEFINITION,
                                          g.fileName, idNode.token, idNode.getText());
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

    public void checkRuleArgs(List<GrammarAST> rulerefs) {
        if ( rulerefs==null ) return;
        for (GrammarAST ref : rulerefs) {
            String ruleName = ref.getText();
            Rule r = nameToRuleMap.get(ruleName);
            if ( r==null ) {
                ErrorManager.grammarError(ErrorType.UNDEFINED_RULE_REF,
                                          g.fileName, ref.token, ruleName);
            }
            GrammarAST arg = (GrammarAST)ref.getChild(0);
            if ( arg!=null && r.arg==null ) {
                ErrorManager.grammarError(ErrorType.RULE_HAS_NO_ARGS,
                                          g.fileName, ref.token, ruleName);

            }
            else if ( arg==null && (r!=null&&r.arg!=null) ) {
                ErrorManager.grammarError(ErrorType.MISSING_RULE_ARGS,
                                          g.fileName, ref.token, ruleName);
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
     *  defined in surrounding rule.
     */
    public void checkForLabelConflicts(MultiMap<String, LabelElementPair> ruleToLabelSpace,
                                       List<Rule> rules) {
        for (Rule r : rules) {
            Map<String, LabelElementPair> labelNameSpace =
                new HashMap<String, LabelElementPair>();

            List<LabelElementPair> pairs = ruleToLabelSpace.get(r.name);
            if ( pairs==null ) continue;
            
            for (LabelElementPair labelPair : pairs) {
                checkForLabelConflict(r, labelPair.label);

                String name = labelPair.label.getText();
                LabelElementPair prevLabelPair = labelNameSpace.get(name);
                if ( prevLabelPair==null ) {
                    labelNameSpace.put(name, labelPair);
                }
                else {
                    // label already defined; if same type, no problem
                    if ( prevLabelPair.type != labelPair.type ) {
                        String typeMismatchExpr = labelPair.type+"!="+prevLabelPair.type;
                        ErrorManager.grammarError(
                            ErrorType.LABEL_TYPE_CONFLICT,
                            g.fileName,
                            labelPair.label.token,
                            name,
                            typeMismatchExpr);
                    }
                }
            }
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

//        else if ( r.ruleScope!=null && r.ruleScope.getAttribute(label.getText())!=null ) {
//            etype = ErrorType.LABEL_CONFLICTS_WITH_RULE_SCOPE_ATTRIBUTE;
//            arg2 = r.name;
//        }
//        else if ( (r.returnScope!=null&&r.returnScope.getAttribute(label.getText())!=null) ||
//                  (r.parameterScope!=null&&r.parameterScope.getAttribute(label.getText())!=null) )
//        {
//            etype = ErrorType.LABEL_CONFLICTS_WITH_RULE_ARG_RETVAL;
//            arg2 = r.name;
//        }
        if ( etype!=ErrorType.INVALID ) {
            ErrorManager.grammarError(etype,g,labelID.token,name,arg2);
        }
    }
}
