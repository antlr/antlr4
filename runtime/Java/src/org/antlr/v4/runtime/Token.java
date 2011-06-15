package org.antlr.v4.runtime;

/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

public interface Token {
	/** imaginary tree navigation type; traverse "get child" link */
	public static final int DOWN = 1;
	/** imaginary tree navigation type; finish with a child list */
	public static final int UP = 2;

	public static final int MIN_TOKEN_TYPE = UP+1;

    public static final int EOF = CharStream.EOF;

	public static final int INVALID_TYPE = 0;
	public static final Token INVALID_TOKEN = new CommonToken(INVALID_TYPE);

	/** All tokens go to the parser (unless skip() is called in that rule)
	 *  on a particular "channel".  The parser tunes to a particular channel
	 *  so that whitespace etc... can go to the parser on a "hidden" channel.
	 */
	public static final int DEFAULT_CHANNEL = 0;

	/** Anything on different channel than DEFAULT_CHANNEL is not parsed
	 *  by parser.
	 */
	public static final int HIDDEN_CHANNEL = 99;

	/** Get the text of the token */
	public String getText();
	public void setText(String text);

	public int getType();
	public void setType(int ttype);
	/**  The line number on which this token was matched; line=1..n */
	public int getLine();
    public void setLine(int line);

	/** The index of the first character relative to the beginning of the line 0..n-1 */
	public int getCharPositionInLine();
	public void setCharPositionInLine(int pos);

	public int getChannel();
	public void setChannel(int channel);

	/** An index from 0..n-1 of the token object in the input stream.
	 *  This must be valid in order to use the ANTLRWorks debugger.
	 */
	public int getTokenIndex();
	public void setTokenIndex(int index);

	/** From what character stream was this token created?  You don't have to
	 *  implement but it's nice to know where a Token comes from if you have
	 *  include files etc... on the input.
	 */
	public CharStream getInputStream();
	public void setInputStream(CharStream input);
}
