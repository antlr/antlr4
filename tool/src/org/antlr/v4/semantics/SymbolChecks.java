package org.antlr.v4.semantics;

import org.antlr.v4.tool.*;

import java.util.*;

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
    }

    public void checkRuleRedefinitions(List<Rule> rules) {
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

    public void checkActionRedefinitions(List<GrammarAST> actions) {
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
