package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.List;

public class Rule {
    public String name;
    public GrammarASTWithOptions ast;
    public AttributeScope args;
    public AttributeScope retvals;
    public AttributeScope scope;
    /** A list of scope names used by this rule */
    public List<Token> useScopes;

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
