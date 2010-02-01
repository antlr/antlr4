package org.antlr.v4.tool;

import org.antlr.runtime.Token;

import java.util.HashMap;
import java.util.Map;

public class BlockAST extends GrammarAST {
    // TODO: maybe I need a Subrule object like Rule so these options mov to that?
    /** What are the default options for a subrule? */
    public static final Map defaultBlockOptions =
            new HashMap() {{put("greedy","true");}};

    public static final Map defaultLexerBlockOptions =
            new HashMap() {{put("greedy","true");}};

    protected Map<String, String> options;

    public BlockAST(Token t) { super(t); }

}
