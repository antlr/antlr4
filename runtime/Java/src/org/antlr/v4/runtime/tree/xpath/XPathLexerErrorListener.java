package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class XPathLexerErrorListener implements ANTLRErrorListener<Integer> {
	@Override
	public <T extends Integer> void syntaxError(Recognizer<T, ?> recognizer, T offendingSymbol,
							int line, int charPositionInLine, String msg,
							RecognitionException e)
	{
	}
}
