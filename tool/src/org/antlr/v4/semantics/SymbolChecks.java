package org.antlr.v4.semantics;

import org.antlr.v4.tool.*;

import java.util.*;

/** Check for symbol problems; no side-effects.  Inefficient to walk rules
 *  and such multiple times, but I like isolating all error checking outside
 *  of code that actually defines symbols etc...
 */
public class SymbolChecks {
    Grammar g;
    CollectSymbols collector;    
    Map<String, Rule> nameToRuleMap = new HashMap<String, Rule>();
    Map<String, Set<String>> scopeToActionNames = new HashMap<String, Set<String>>();

    public SymbolChecks(Grammar g, CollectSymbols collector) {
        this.g = g;
        this.collector = collector;
        System.out.println("rules="+collector.rules);
        System.out.println("rulerefs="+collector.rulerefs);
        System.out.println("terminals="+collector.terminals);
        System.out.println("strings="+collector.strings);
        System.out.println("aliases="+collector.aliases);
        System.out.println("actions="+collector.actions);
    }

    public void examine() {
        checkRuleRedefinitions(collector.rules);
        checkActionRedefinitions(collector.actions);
        checkRuleArgs(collector.rulerefs);
    }

    public void checkRuleRedefinitions(List<Rule> rules) {
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
            Set<String> scopeActions = scopeToActionNames.get(scope);
            if ( scopeActions==null ) { // init scope
                scopeActions = new HashSet<String>();
                scopeToActionNames.put(scope, scopeActions);
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
}
