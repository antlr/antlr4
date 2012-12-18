/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime;

/** The most common stream of tokens where every token is buffered up
 *  and tokens are filtered for a certain channel (the parser will only
 *  see these tokens).
 *
 *  Even though it buffers all of the tokens, this token stream pulls tokens
 *  from the tokens source on demand. In other words, until you ask for a
 *  token using consume(), LT(), etc. the stream does not pull from the lexer.
 *
 *  The only difference between this stream and {@link BufferedTokenStream} superclass
 *  is that this stream knows how to ignore off channel tokens. There may be
 *  a performance advantage to using the superclass if you don't pass
 *  whitespace and comments etc. to the parser on a hidden channel (i.e.,
 *  you set {@code $channel} instead of calling {@code skip()} in lexer rules.)
 *
 *  @see UnbufferedTokenStream
 *  @see BufferedTokenStream
 */
public class CommonTokenStream extends BufferedTokenStream<Token> {
    /** Skip tokens on any channel but this one; this is how we skip whitespace... */
    protected int channel = Token.DEFAULT_CHANNEL;

    public CommonTokenStream(TokenSource<? extends Token> tokenSource) {
        super(tokenSource);
    }

    public CommonTokenStream(TokenSource<? extends Token> tokenSource, int channel) {
        this(tokenSource);
        this.channel = channel;
    }

	@Override
	protected int adjustSeekIndex(int i) {
		return nextTokenOnChannel(i, channel);
	}

    @Override
    protected Token LB(int k) {
        if ( k==0 || (p-k)<0 ) return null;

        int i = p;
        int n = 1;
        // find k good tokens looking backwards
        while ( n<=k ) {
            // skip off-channel tokens
            i = previousTokenOnChannel(i - 1, channel);
            n++;
        }
        if ( i<0 ) return null;
        return tokens.get(i);
    }

    @Override
    public Token LT(int k) {
        //System.out.println("enter LT("+k+")");
        lazyInit();
        if ( k == 0 ) return null;
        if ( k < 0 ) return LB(-k);
        int i = p;
        int n = 1; // we know tokens[p] is a good one
        // find k good tokens
        while ( n<k ) {
            // skip off-channel tokens, but make sure to not look past EOF
			if (sync(i + 1)) {
				i = nextTokenOnChannel(i + 1, channel);
			}
            n++;
        }
//		if ( i>range ) range = i;
        return tokens.get(i);
    }

	/** Count EOF just once. */
	public int getNumberOfOnChannelTokens() {
		int n = 0;
		fill();
		for (int i = 0; i < tokens.size(); i++) {
			Token t = tokens.get(i);
			if ( t.getChannel()==channel ) n++;
			if ( t.getType()==Token.EOF ) break;
		}
		return n;
	}
}
