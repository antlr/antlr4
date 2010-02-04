package org.antlr.v4.semantics;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.v4.parse.ASTVerifier;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.Grammar;

/** */
public class SemanticsPipeline {
    public void process(Grammar g) {
        // VALIDATE AST STRUCTURE
        // use buffered node stream as we will look around in stream
        // to give good error messages.
        // TODO: send parse errors to buffer not stderr
        GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
        BufferedTreeNodeStream nodes =
            new BufferedTreeNodeStream(adaptor,g.ast);
        ASTVerifier walker = new ASTVerifier(nodes);
        try {walker.grammarSpec();}
        catch (RecognitionException re) {
            ErrorManager.internalError("bad grammar AST structure", re);
        }

        // DO BASIC / EASY SEMANTIC CHECKS
        nodes.reset();
        BasicSemanticTriggers basics = new BasicSemanticTriggers(nodes,g);
        basics.downup(g.ast);

        // NOW DO BASIC / EASY SEMANTIC CHECKS FOR DELEGATES (IF ANY)
        if ( g.getImportedGrammars()!=null ) {
            for (Grammar d : g.getImportedGrammars()) {
                process(d);
            }
        }

        // DEFINE SYMBOLS

        // ASSIGN TOKEN TYPES

        /* dump options
        TreeVisitor v = new TreeVisitor(adaptor);
        v.visit(g.ast,
                new TreeVisitorAction() {
                    public Object pre(Object t) {
                        if ( t instanceof GrammarASTWithOptions ) {
                            GrammarASTWithOptions gt = (GrammarASTWithOptions)t;
                            if ( gt.getOptions()!=null ) {
                                System.out.println("options @ "+gt.toStringTree()+"="+gt.getOptions());
                            }
                        }
                        return t;
                    }
                    public Object post(Object t) { return t; }
                });
                */
    }
}
