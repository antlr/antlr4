package org.antlr.v4.semantics;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.v4.parse.ASTVerifier;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.tool.*;

/** */
public class SemanticPipeline {
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

        // don't continue if we get errors in this basic check
        if ( false ) return;

        // TODO: can i move to Tool.process? why recurse here?
        // NOW DO BASIC / EASY SEMANTIC CHECKS FOR DELEGATES (IF ANY)
        if ( g.getImportedGrammars()!=null ) {
            for (Grammar d : g.getImportedGrammars()) {
                process(d);
            }
        }

        // COLLECT SYMBOLS: RULES, ACTIONS, TERMINALS, ...
        nodes.reset();
        CollectSymbols collector = new CollectSymbols(nodes,g);
        collector.downup(g.ast); // no side-effects; compute lists

        // CHECK FOR SYMBOL COLLISIONS
        SymbolChecks symcheck = new SymbolChecks(g, collector);
        symcheck.examine(); // side-effect: strip away redef'd rules.

        // don't continue if we get symbol errors
        if ( false ) return;

        // STORE RULES/ACTIONS/SCOPES IN GRAMMAR
        for (Rule r : collector.rules) g.defineRule(r);
        for (AttributeScope s : collector.scopes) g.defineScope(s);
        for (GrammarAST a : collector.actions) g.defineAction(a);
        // TODO: named actions

        // CHECK ATTRIBUTE EXPRESSIONS FOR SEMANTIC VALIDITY
        AttributeChecks.checkAllAttributeExpressions(g);

        // ASSIGN TOKEN TYPES
    }
}
