package org.antlr.v4.tool;

import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.LexerInterpreter;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.Collection;
import java.util.Map;

/** To execute actions in a lexer like skip(), we need to know
 *  what actionIndex values mean. They are accessible only through
 *  a grammar object. LexerInterpreter is in the runtime to avoid
 *  tying it to the tool lib so we make an adaptor here that
 *  ties a LexerInterpreter to a grammar.
 */
public class LexerGrammarInterpreter extends LexerInterpreter {
	protected LexerGrammar g;
	protected Map<String,Integer> modeNameToIndex;

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
		modeNameToIndex = Utils.toMap(modeNames.toArray(new String[modeNames.size()]));
	}

	/** Execute lexer commands like ->skip and ->channel(HIDDEN) if we are
	 *  able to compute the command's argument value.
	 */
	@Override
	public void action(@Nullable RuleContext _localctx, int ruleIndex, int actionIndex) {
		Rule r = g.getRule(ruleIndex);
//		System.out.println(r.lexerCommandTree.toStringTree());
		for (int i = 0; i < r.lexerCommandTrees.size(); i++) {
			GrammarAST cmdTree = r.lexerCommandTrees.get(i);
			switch ( cmdTree.getType() ) {
				case ANTLRParser.ID : // -> skip etc...
					callAction(cmdTree);
					break;
				case ANTLRParser.LEXER_ACTION_CALL : // -> channel(HIDDEN) etc...
					callActionWithArg(cmdTree);
					break;
			}
		}
	}

	protected void callAction(GrammarAST cmdTree) {
		String cmd = cmdTree.getText();
		if ( cmd.equals("skip") ) {
			skip();
		}
		else if ( cmd.equals("more") ) {
			more();
		}
		else if ( cmd.equals("popMode") ) {
			popMode();
		}
	}

	/** exec
	Tree: (LEXER_ACTION_CALL command x)
	mode( x )
	pushMode( x )
	type( x )
	channel( x )
	 */
	protected void callActionWithArg(GrammarAST cmdTree) {
		String call = cmdTree.getChild(0).getText();
		String arg = cmdTree.getChild(1).getText();
		if ( call.equals("pushMode") ) {
			Integer m = modeNameToIndex.get(arg);
//			System.out.println(call+" "+arg+"="+ mode);
			pushMode(m);
		}
		else if ( call.equals("mode") ) {
			Integer m = modeNameToIndex.get(arg);
//			System.out.println(call+" "+arg+"="+ mode);
			mode(m);
		}
		else if ( call.equals("type") ) {
//			System.out.println(call+" "+arg);
			// don't know how to set type unless it's an int
			try {
				int v = Integer.valueOf(arg);
				setType(v);
			}
			catch (NumberFormatException nfe) {
				; // oh well, we tried
			}
		}
		else if ( call.equals("channel") ) {
//			System.out.println(call+" "+arg);
			// don't know how to set type unless it's an int
			// or HIDDEN or DEFAULT_TOKEN_CHANNEL
			if ( arg.equals("HIDDEN") ) {
				setChannel(HIDDEN);
			}
			else if ( arg.equals("DEFAULT_TOKEN_CHANNEL") ) {
				setChannel(DEFAULT_TOKEN_CHANNEL);
			}
			else {
				try {
					int v = Integer.valueOf(arg);
					setChannel(v);
				}
				catch (NumberFormatException nfe) {
					; // oh well, we tried
				}
			}
		}
	}

	public LexerGrammar getGrammar() {
		return g;
	}
}
