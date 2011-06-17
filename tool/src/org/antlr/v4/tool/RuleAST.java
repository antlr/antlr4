package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;
import org.antlr.v4.parse.ANTLRParser;

public class RuleAST extends GrammarASTWithOptions {
	public RuleAST(GrammarAST node) {
		super(node);
	}

	public RuleAST(Token t) { super(t); }
    public RuleAST(int type) { super(type); }

	@Override
	public Tree dupNode() { return new RuleAST(this); }

	public ActionAST getLexerAction() {
		Tree blk = getFirstChildWithType(ANTLRParser.BLOCK);
		if ( blk.getChildCount()==1 ) {
			Tree onlyAlt = blk.getChild(0);
			Tree lastChild = onlyAlt.getChild(onlyAlt.getChildCount()-1);
			if ( lastChild.getType()==ANTLRParser.ACTION ) {
				return (ActionAST)lastChild;
			}
		}
		return null;
	}
}
