package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.v4.parse.ANTLRParser;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rule {
    /** Rule refs have a predefined set of attributes as well as
     *  the return values and args.
     */
    public static AttributeScope predefinedRulePropertiesScope =
        new AttributeScope() {{
            add(new Attribute("text"));
            add(new Attribute("start"));
            add(new Attribute("stop"));
            add(new Attribute("tree"));
            add(new Attribute("st"));
        }};

    public static AttributeScope predefinedTreeRulePropertiesScope =
        new AttributeScope() {{
            add(new Attribute("text"));
            add(new Attribute("start")); // note: no stop; not meaningful
            add(new Attribute("tree"));
            add(new Attribute("st"));
        }};

    public static AttributeScope predefinedLexerRulePropertiesScope =
        new AttributeScope() {{
            add(new Attribute("text"));
            add(new Attribute("type"));
            add(new Attribute("line"));
            add(new Attribute("index"));
            add(new Attribute("pos"));
            add(new Attribute("channel"));
            add(new Attribute("start"));
            add(new Attribute("stop"));
            add(new Attribute("int"));
        }};

    public String name;
    public GrammarASTWithOptions ast;
    public AttributeScope args;
    public AttributeScope retvals;
    public AttributeScope scope;
    /** A list of scope names used by this rule */
    public List<Token> useScopes;
    public Grammar g;

    /** Map a name to an action for this rule like @init {...}.
     *  The code generator will use this to fill holes in the rule template.
     *  I track the AST node for the action in case I need the line number
     *  for errors.
     */
    public Map<String, GrammarAST> namedActions =
        new HashMap<String, GrammarAST>();

    /** Track exception handlers, finally action */
    public List<GrammarAST> exceptionActions = new ArrayList<GrammarAST>();

    public int numberOfAlts;

    /** Labels are visible to all alts in a rule. Record all defs here.
     *  We need to ensure labels are used to track same kind of symbols.
     *  Tracks all label defs for a label.
     */
    public MultiMap<String, LabelElementPair> labelDefs =
        new MultiMap<String, LabelElementPair>();

    public Alternative[] alt;

    public Rule(Grammar g, String name, GrammarASTWithOptions ast, int numberOfAlts) {
        this.g = g;
        this.name = name;
        this.ast = ast;
        this.numberOfAlts = numberOfAlts;
        alt = new Alternative[numberOfAlts+1]; // 1..n
        for (int i=1; i<=numberOfAlts; i++) alt[i] = new Alternative();
    }

    public boolean resolves(String name, Alternative alt) {
        Attribute a = resolve(name);
        if ( a!=null ) return true; // ok
        if ( alt.tokenRefs.get(name)!=null||
             alt.ruleRefs.get(name)!=null ||
             alt.labelDefs.get(name)!=null )
        {
            return true;
        }
        return false;
    }
    
    /** Look up name from context of this rule and an alternative.
     *  Find an arg, retval, predefined property, or label/rule/token ref.
     */
    public Attribute resolve(String name) {
        Attribute a = args.get(name);   if ( a!=null ) return a;
        a = retvals.get(name);          if ( a!=null ) return a;
        String grammarLabelKey = g.getTypeString() + ":" + LabelType.RULE_LABEL;
        AttributeScope properties =
            Grammar.grammarAndLabelRefTypeToScope.get(grammarLabelKey);
        a = properties.get(name);
        if ( a!=null ) return a;

        //alt[currentAlt].tokenRefs
        // not here? look in grammar for global scope
        return null;
    }

    public AttributeScope resolveScope(String name, Alternative alt) {
        AttributeScope s = args;    if ( s.get(name)!=null ) return s;
        s = retvals;                if ( s.get(name)!=null ) return s;
        s = retvals;                if ( s.get(name)!=null ) return s;
        String grammarLabelKey = g.getTypeString() + ":" + LabelType.RULE_LABEL;
        s = Grammar.grammarAndLabelRefTypeToScope.get(grammarLabelKey);
        if ( s.get(name)!=null ) return s;
        if ( alt.tokenRefs.get(name)!=null ) {
            grammarLabelKey = g.getTypeString() + ":" + LabelType.TOKEN_LABEL;
            return Grammar.grammarAndLabelRefTypeToScope.get(grammarLabelKey);
        }
        if ( alt.ruleRefs.get(name)!=null ) {
            grammarLabelKey = g.getTypeString() + ":" + LabelType.RULE_LABEL;
            return Grammar.grammarAndLabelRefTypeToScope.get(grammarLabelKey);
        }
        List<LabelElementPair> labels = labelDefs.get(name);
        if ( labels!=null ) {
            // it's a label ref, compute scope from label type and grammar type
            LabelElementPair anyLabelDef = labels.get(0);
            grammarLabelKey = g.getTypeString() + ":" + anyLabelDef.type;
            return Grammar.grammarAndLabelRefTypeToScope.get(grammarLabelKey);            
        }
        return null;
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
