package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

import java.util.HashMap;
import java.util.Map;

public class BlockAST extends GrammarASTWithOptions {
    // TODO: maybe I need a Subrule object like Rule so these options mov to that?
    /** What are the default options for a subrule? */
    public static final Map defaultBlockOptions =
            new HashMap() {{put("greedy","true");}};

    public static final Map defaultLexerBlockOptions =
            new HashMap() {{put("greedy","true");}};

	public BlockAST(GrammarAST node) {
		super(node);
	}

	public BlockAST(Token t) { super(t); }
    public BlockAST(int type) { super(type); }
    public BlockAST(int type, Token t) { super(type, t); }
	public BlockAST(int type, Token t, String text) { super(type,t,text); }

	@Override
	public Tree dupNode() { return new BlockAST(this); }	
}
