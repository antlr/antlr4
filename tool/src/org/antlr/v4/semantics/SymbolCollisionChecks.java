package org.antlr.v4.semantics;

import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.Rule;

import java.util.List;

/**  check for the following errors:
 *
 * RULE_REDEFINITION
RULE_HAS_NO_ARGS
UNDEFINED_RULE_REF
MISSING_RULE_ARGS
SYMBOL_CONFLICTS_WITH_GLOBAL_SCOPE
LABEL_CONFLICTS_WITH_RULE
LABEL_CONFLICTS_WITH_TOKEN
LABEL_TYPE_CONFLICT
ACTION_REDEFINITION
NO_SUCH_RULE_IN_SCOPE
TOKEN_ALIAS_CONFLICT
TOKEN_ALIAS_REASSIGNMENT

 The
*/
public class SymbolCollisionChecks {
    public List<Rule> rules;
    public List<GrammarAST> terminals;
    public List<GrammarAST> aliases;
    public List<GrammarAST> scopes;
    public List<GrammarAST> actions;

    public void check(List<Rule> rules,
                      List<GrammarAST> terminals,
                      List<GrammarAST> aliases,
                      List<GrammarAST> scopes,
                      List<GrammarAST> actions)
    {
        checkRuleRedefinition(rules);
    }
    
    public void checkRuleRedefinition(List<Rule> rules) {

    }
}
