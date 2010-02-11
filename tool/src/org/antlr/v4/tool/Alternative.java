package org.antlr.v4.tool;


import org.stringtemplate.v4.misc.MultiMap;

import java.util.ArrayList;
import java.util.List;

/** Record use/def information about an outermost alternative in a subrule
 *  or rule of a grammar.
 */
public class Alternative {
    // token IDs, string literals in this alt
    public MultiMap<String, GrammarAST> tokenRefs = new MultiMap<String, GrammarAST>();

    // all rule refs in this alt
    public MultiMap<String, GrammarAST> ruleRefs = new MultiMap<String, GrammarAST>();

    /** A list of all LabelElementPair attached to tokens like id=ID, ids+=ID */
    public MultiMap<String, GrammarAST> labelDefs = new MultiMap<String, GrammarAST>();

    // track all token, rule, label refs in rewrite (right of ->)
    public List<GrammarAST> rewriteElements = new ArrayList<GrammarAST>();

    /** Track all executable actions other than named actions like @init
     *  and catch/finally (not in an alt). Also tracks predicates, rewrite actions.
     *  We need to examine these actions before code generation so
     *  that we can detect refs to $rule.attr etc...
     */
    public List<GrammarAST> actions = new ArrayList<GrammarAST>();

    public Alternative() {
    }
}
