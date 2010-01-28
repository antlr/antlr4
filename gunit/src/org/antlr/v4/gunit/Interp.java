package org.antlr.v4.gunit;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.runtime.tree.Tree;

public class Interp {
    public static void main(String[] args) throws Exception {
        String fileName = args[0];
        ANTLRFileStream fs = new ANTLRFileStream(fileName);
        gUnitLexer lexer = new gUnitLexer(fs);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        gUnitParser parser = new gUnitParser(tokens);
        RuleReturnScope r = parser.gUnitDef();
        System.out.println(((Tree)r.getTree()).toStringTree());

        BufferedTreeNodeStream nodes = new BufferedTreeNodeStream(r.getTree());
        ASTVerifier verifier = new ASTVerifier(nodes);
        verifier.gUnitDef();
    }
}
