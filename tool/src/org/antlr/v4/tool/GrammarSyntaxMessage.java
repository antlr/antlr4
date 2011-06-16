package org.antlr.v4.tool;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

/** A problem with the syntax of your antlr grammar such as
 *  "The '{' came as a complete surprise to me at this point in your program"
 */
public class GrammarSyntaxMessage extends ANTLRMessage {
	public Grammar g;
	/** Most of the time, we'll have a token and so this will be set. */
	public Token offendingToken;
	public RecognitionException antlrException;

	public GrammarSyntaxMessage(ErrorType etype,
								String fileName,
								Token offendingToken,
								RecognitionException antlrException,
								Object... args)
	{
		super(etype,args);
		this.fileName = fileName;
		this.offendingToken = offendingToken;
		this.antlrException = antlrException;
		if ( offendingToken!=null ) {
			line = offendingToken.getLine();
			charPosition = offendingToken.getCharPositionInLine();
		}
	}
}
