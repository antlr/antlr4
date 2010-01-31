package org.antlr.v4.semantics;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.v4.Tool;
import org.antlr.v4.parse.ASTVerifier;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.Grammar;

/** */
public class SemanticsPipeline {
    public void process(Grammar g) {
        // VALIDATE AST STRUCTURE
        // use buffered node stream as we will look around in stream
        // to give good error messages.
        // TODO: send parse errors to buffer not stderr
        BufferedTreeNodeStream nodes = new BufferedTreeNodeStream(Tool.astAdaptor,g.ast);
        ASTVerifier walker = new ASTVerifier(nodes);
        try {walker.grammarSpec();}
        catch (RecognitionException re) {
            ErrorManager.internalError("bad grammar AST structure", re);
        }

        // DO BASIC / EASY SEMANTIC CHECKS
        nodes.reset();
        BasicSemanticsChecker basics = new BasicSemanticsChecker(nodes,g.fileName);
        basics.downup(g.ast);
    }
}
