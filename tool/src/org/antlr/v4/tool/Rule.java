package org.antlr.v4.tool;

import java.util.Map;

public class Rule {
    public String name;
    public GrammarASTWithOptions ast;
    public GrammarAST arg;
    public GrammarAST ret;

    /** All labels go in here (TODO: plus being split per the above lists) to
     *  catch dup label and label type mismatches.
     */
    public Map<String, LabelElementPair> labelNameSpace;
    //= new HashMap<String, LabelElementPair>();

    public Rule(String name, GrammarASTWithOptions ast) {
        this.name = name;
        this.ast = ast;
    }

    @Override
    public String toString() {
        return "Rule{" +
               "name='" + name + '\'' +
               ", arg=" + arg +
               ", ret=" + ret +
               ", labels=" + labelNameSpace +
               '}';
    }
}
