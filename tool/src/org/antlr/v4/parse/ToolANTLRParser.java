package org.antlr.v4.parse;

import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.TokenStream;
import org.antlr.v4.Tool;
import org.antlr.v4.tool.ErrorType;

/** Override error handling for use with ANTLR tool itself; leaves
 *  nothing in grammar associated with Tool so others can use in IDEs, ...
 */
public class ToolANTLRParser extends ANTLRParser {
	public Tool tool;

	public ToolANTLRParser(TokenStream input, Tool tool) {
		super(input);
		this.tool = tool;
	}

	public void displayRecognitionError(String[] tokenNames,
										RecognitionException e)
	{
		String msg = getParserErrorMessage(this, e);
		if ( paraphrases.size()>0 ) {
			String paraphrase = (String)paraphrases.peek();
			msg = msg+" while "+paraphrase;
		}
	//	List stack = getRuleInvocationStack(e, this.getClass().getName());
	//	msg += ", rule stack = "+stack;
		tool.errMgr.syntaxError(ErrorType.SYNTAX_ERROR, getSourceName(), e.token, e, msg);
	}

	public String getParserErrorMessage(Parser parser, RecognitionException e) {
		String msg = null;
		if ( e instanceof NoViableAltException) {
			String name = parser.getTokenErrorDisplay(e.token);
			msg = name+" came as a complete surprise to me";
		}
		else if ( e instanceof v4ParserException) {
			msg = ((v4ParserException)e).msg;
		}
		else {
			msg = parser.getErrorMessage(e, parser.getTokenNames());
		}
		return msg;
	}


}
