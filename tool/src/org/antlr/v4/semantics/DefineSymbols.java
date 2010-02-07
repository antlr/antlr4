package org.antlr.v4.semantics;

import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.Rule;

/** */
public class DefineSymbols {
    Grammar g;
    CollectSymbols collector;

    public DefineSymbols(Grammar g, CollectSymbols collector) {
        this.g = g;
        this.collector = collector;
    }

    public void define() {
        for (Rule r : collector.rules) {
            if ( g.getRule(r.name)==null ) {
                g.defineRule(r);
            }
            else {
                //error
            }
        }

        for (GrammarAST t : collector.actions) {
            
        }
    }
}
