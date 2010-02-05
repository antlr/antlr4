package org.antlr.v4.tool;

public class Rule {
    public String name;
    public GrammarASTWithOptions ast;
    public GrammarAST arg;
    public GrammarAST ret;
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
               '}';
    }
}
