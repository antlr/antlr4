package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rule {
    public String name;
    public GrammarASTWithOptions ast;
    public AttributeScope args;
    public AttributeScope retvals;
    public AttributeScope scope;
    /** A list of scope names used by this rule */
    public List<Token> useScopes;

    /** Map a name to an action for this rule like @init {...}.
     *  The code generator will use this to fill holes in the rule template.
     *  I track the AST node for the action in case I need the line number
     *  for errors.
     */
    public Map<String, GrammarAST> namedActions =
        new HashMap<String, GrammarAST>();

    /** Track all executable actions other than named actions like @init
     *  including catch/finally so we can sniff attribute expressions.
     *  Also tracks exception handlers, predicates, and rewrite rewrites.
     *  We need to examine these actions before code generation so
     *  that we can detect refs to $rule.attr etc...
     */
    public List<GrammarAST> inlineActions = new ArrayList<GrammarAST>();

    public int numberOfAlts;

    /** Labels are visible to all alts in a rule. Record all defs here.
     *  We need to ensure labels are used to track same kind of symbols.
     *  Tracks all label defs for a label.
     */
    public MultiMap<String, LabelElementPair> labelDefs =
        new MultiMap<String, LabelElementPair>();

    public Alternative[] alt;

    public Rule(String name, GrammarASTWithOptions ast, int numberOfAlts) {
        this.name = name;
        this.ast = ast;
        this.numberOfAlts = numberOfAlts;
        alt = new Alternative[numberOfAlts+1]; // 1..n
        for (int i=1; i<=numberOfAlts; i++) alt[i] = new Alternative();
    }

    @Override
    public String toString() {
        return "Rule{" +
               "name='" + name + '\'' +
               ", args=" + args +
               ", retvals=" + retvals +
               ", labels=" + labelDefs +
               ", scope=" + scope +
               '}';
    }
}
