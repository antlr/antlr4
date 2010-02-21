package org.antlr.v4.tool;

import org.antlr.runtime.Token;

/** A problem with the symbols and/or meaning of a grammar such as rule
 *  redefinition.
 */
public class GrammarSemanticsMessage extends Message {
	public Grammar g;
	/** Most of the time, we'll have a token such as an undefined rule ref
     *  and so this will be set.
     */
    public Token offendingToken;

	/*
    public GrammarSemanticsMessage(ErrorType etype,
                                   Grammar g,
                                   Token offendingToken,
                                   Object... args)
    {
        super(etype,args);
        this.g = g;
        if ( g!=null ) fileName = g.fileName;
        this.offendingToken = offendingToken;
        if ( offendingToken!=null ) {
            line = offendingToken.getLine();
            charPosition = offendingToken.getCharPositionInLine();
        }
    }
*/
    public GrammarSemanticsMessage(ErrorType etype,
                                   String fileName,
                                   Token offendingToken,
                                   Object... args)
    {
        super(etype,args);
        this.fileName = fileName;
        this.offendingToken = offendingToken;
        if ( offendingToken!=null ) {
            line = offendingToken.getLine();
            charPosition = offendingToken.getCharPositionInLine();
        }
    }
}

