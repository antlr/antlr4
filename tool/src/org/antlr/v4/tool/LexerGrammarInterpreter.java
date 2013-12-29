package org.antlr.v4.tool;

import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.Collection;

/** To execute actions in a lexer like skip(), we need to know
 *  what actionIndex values mean. They are accessible only through
 *  a grammar object. LexerInterpreter is in the runtime to avoid
 *  tying it to the tool lib so we make an adaptor here that
 *  ties a LexerInterpreter to a grammar.
 */
public class LexerGrammarInterpreter extends LexerInterpreter {
	protected LexerGrammar g;
	public LexerGrammarInterpreter(LexerGrammar g,
								   String grammarFileName,
								   Collection<String> tokenNames,
								   Collection<String> ruleNames,
								   Collection<String> modeNames,
								   ATN atn,
								   CharStream input)
	{
		super(grammarFileName, tokenNames, ruleNames, modeNames, atn, input);
		this.g = g;
	}

	@Override
	public void action(@Nullable RuleContext _localctx, int ruleIndex, int actionIndex) {
		Rule r = g.getRule(ruleIndex);
//		System.out.println(r.lexerCommandTree.toStringTree());
		switch ( r.lexerCommandTree.getType() ) {
			case ANTLRParser.ID :
				String cmd = r.lexerCommandTree.getText();
				if ( cmd.equals("skip") ) {
					skip();
				}
				else if ( cmd.equals("more") ) {
					more();
				}
				else if ( cmd.equals("popMode") ) {
					popMode();
				}
				break;
			case ANTLRParser.LEXER_ACTION_CALL :
				/*
				mode( x )
				pushMode( x )
				type( x )
				channel( x )
				 */
				break;
		}
	}

	public LexerGrammar getGrammar() {
		return g;
	}
}
