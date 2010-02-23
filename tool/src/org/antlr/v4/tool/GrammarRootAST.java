package org.antlr.v4.tool;

import org.antlr.runtime.Token;

import java.util.HashMap;
import java.util.Map;

public class GrammarRootAST extends GrammarASTWithOptions {
    public static final Map defaultOptions =
            new HashMap() {
                {
                    put("language","Java");
                }
            };
    public int grammarType; // LEXER, PARSER, TREE, GRAMMAR (combined)
	public boolean hasErrors;
	
    public GrammarRootAST(int type) { super(type); }
    public GrammarRootAST(Token t) { super(t); }
    public GrammarRootAST(int type, Token t) { super(type, t); }
    public GrammarRootAST(int type, Token t, String text) {
        super(type,t,text);
    }
}
