package org.antlr.v4.semantics;

import org.antlr.v4.parse.ANTLRParser;
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
    Map<String, Set<String>> actionScopeToActionNames = new HashMap<String, Set<String>>();

    public SymbolChecks(Grammar g, CollectSymbols collector) {
        this.g = g;
        this.collector = collector;
        System.out.println("rules="+collector.rules);
        System.out.println("rulerefs="+collector.rulerefs);
        System.out.println("terminals="+collector.terminals);
        System.out.println("strings="+collector.strings);
        System.out.println("tokensDef="+collector.tokensDef);
        System.out.println("actions="+collector.actions);
        System.out.println("scopes="+collector.scopes);
    }

    public void examine() {
        checkRuleRedefinitions(collector.rules);
        checkScopeRedefinitions(collector.scopes);
        checkActionRedefinitions(collector.actions);
        checkTokenAliasRedefinitions(collector.tokensDef);
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

    public void checkScopeRedefinitions(List<GrammarAST> scopes) {
        if ( scopes==null ) return;
        Set<String> scopeNames = new HashSet<String>();
        for (int i=0; i< scopes.size(); i++) {
            GrammarAST s = scopes.get(i);
            GrammarAST idNode = (GrammarAST)s.getChild(0);
            if ( !scopeNames.contains(idNode.getText()) ) {
                scopeNames.add(idNode.getText());
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
}
