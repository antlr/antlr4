/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
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

package org.antlr.v4.runtime;

/** The most common stream of tokens where every token is buffered up
 *  and tokens are filtered for a certain channel (the parser will only
 *  see these tokens).
 *
 *  Even though it buffers all of the tokens, this token stream pulls tokens
 *  from the tokens source on demand. In other words, until you ask for a
 *  token using consume(), LT(), etc. the stream does not pull from the lexer.
 *
 *  The only difference between this stream and BufferedTokenStream superclass
 *  is that this stream knows how to ignore off channel tokens. There may be
 *  a performance advantage to using the superclass if you don't pass
 *  whitespace and comments etc. to the parser on a hidden channel (i.e.,
 *  you set $channel instead of calling skip() in lexer rules.)
 *
 *  @see UnbufferedTokenStream
 *  @see BufferedTokenStream
 */
public class CommonTokenStream extends BufferedTokenStream {
    /** Skip tokens on any channel but this one; this is how we skip whitespace... */
    protected int channel = Token.DEFAULT_CHANNEL;

    public CommonTokenStream() { ; }

    public CommonTokenStream(TokenSource tokenSource) {
        super(tokenSource);
    }

    public CommonTokenStream(TokenSource tokenSource, int channel) {
        this(tokenSource);
        this.channel = channel;
    }

    /** Always leave p on an on-channel token. */
    @Override
    public void consume() {
        if ( p == -1 ) setup();
        p++;
        sync(p);
		Token t = tokens.get(p);
		while ( t.getType()!=Token.EOF && t.getChannel()!=channel ) {
            p++;
            sync(p);
			t = tokens.get(p);
        }
    }

    @Override
    public void seek(int index) {
        super.seek(index);
        while (p < index) {
            consume();
        }
    }

	@Override
	public void reset() {
		super.reset();
		p = nextTokenOnChannel(p, channel);
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
        if ( p == -1 ) setup();
        if ( k == 0 ) return null;
        if ( k < 0 ) return LB(-k);
        int i = p;
        int n = 1; // we know tokens[p] is a good one
        // find k good tokens
        while ( n<k ) {
            // skip off-channel tokens
            i = nextTokenOnChannel(i + 1, channel);
            n++;
        }
//		if ( i>range ) range = i;
        return tokens.get(i);
    }

    @Override
    protected void setup() {
        p = 0;
        sync(0);
        int i = 0;
        Token token = tokens.get(i);
        while ( token.getType()!=Token.EOF && token.getChannel()!=channel ) {
            i++;
            sync(i);
            token = tokens.get(i);
        }
        p = i;
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

    /** Reset this token stream by setting its token source. */
    @Override
    public void setTokenSource(TokenSource tokenSource) {
        super.setTokenSource(tokenSource);
        channel = Token.DEFAULT_CHANNEL;
    }
}
