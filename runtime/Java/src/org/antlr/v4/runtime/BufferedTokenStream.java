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

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/** Buffer all input tokens but do on-demand fetching of new tokens from
 *  lexer. Useful when the parser or lexer has to set context/mode info before
 *  proper lexing of future tokens. The ST template parser needs this,
 *  for example, because it has to constantly flip back and forth between
 *  inside/output templates. E.g., {@code <names:{hi, <it>}>} has to parse names
 *  as part of an expression but {@code "hi, <it>"} as a nested template.
 *
 *  You can't use this stream if you pass whitespace or other off-channel
 *  tokens to the parser. The stream can't ignore off-channel tokens.
 *  ({@link UnbufferedTokenStream} is the same way.)  Use {@link CommonTokenStream}.
 *
 *  This is not a subclass of {@code UnbufferedTokenStream} because I don't want
 *  to confuse small moving window of tokens it uses for the full buffer.
 */
public class BufferedTokenStream<T extends Token> implements TokenStream<T> {
	@NotNull
    protected TokenSource<? extends T> tokenSource;

    /** Record every single token pulled from the source so we can reproduce
     *  chunks of it later. This list captures everything so we can access
     *  complete input text.
     */
    protected List<T> tokens = new ArrayList<T>(100);

    /** The index into the tokens list of the current token (next token
     *  to consume).  {@code tokens[p]} should be {@code LT(1)}.  {@code p==-1} indicates need
     *  to initialize with first token.  The ctor doesn't get a token.
     *  First call to {@code LT(1)} or whatever gets the first token and sets {@code p=0;}.
     */
    protected int p = -1;

	/**
	 * Set to {@code true} when the EOF token is fetched. Do not continue fetching
	 * tokens after that point, or multiple EOF tokens could end up in the
	 * {@link #tokens} array.
	 *
	 * @see #fetch
	 */
	protected boolean fetchedEOF;

    public BufferedTokenStream(TokenSource<? extends T> tokenSource) {
		if (tokenSource == null) {
			throw new NullPointerException("tokenSource cannot be null");
		}
        this.tokenSource = tokenSource;
    }

    @Override
    public TokenSource<? extends T> getTokenSource() { return tokenSource; }

	@Override
	public int index() { return p; }

//	public int range() { return range; }

    @Override
    public int mark() {
		return 0;
	}

	@Override
	public void release(int marker) {
		// no resources to release
	}

    public void reset() {
        seek(0);
    }

    @Override
    public void seek(int index) {
        lazyInit();
        p = adjustSeekIndex(index);
    }

    @Override
    public int size() { return tokens.size(); }

    /** Move the input pointer to the next incoming token.  The stream
     *  must become active with {@code LT(1)} available.  {@code consume()} simply
     *  moves the input pointer so that {@code LT(1)} points at the next
     *  input symbol. Consume at least one token, unless EOF has been reached.
     */
    @Override
    public void consume() {
        lazyInit();
		if (sync(p + 1)) {
			p = adjustSeekIndex(p + 1);
		}
    }

    /** Make sure index {@code i} in tokens has a token.
	 *
	 * @return {@code true} if a token is located at index {@code i}, otherwise
	 *    {@code false}.
	 * @see #get(int i)
	 */
    protected boolean sync(int i) {
		assert i >= 0;
        int n = i - tokens.size() + 1; // how many more elements we need?
        //System.out.println("sync("+i+") needs "+n);
        if ( n > 0 ) {
			int fetched = fetch(n);
			return fetched >= n;
		}

		return true;
    }

    /** Add {@code n} elements to buffer.
	 *
	 * @return The actual number of elements added to the buffer.
	 */
    protected int fetch(int n) {
		if (fetchedEOF) {
			return 0;
		}

        for (int i = 0; i < n; i++) {
            T t = (T)tokenSource.nextToken();
            if ( t instanceof WritableToken ) {
                ((WritableToken)t).setTokenIndex(tokens.size());
            }
            tokens.add(t);
            if ( t.getType()==Token.EOF ) {
				fetchedEOF = true;
				return i + 1;
			}
        }

		return n;
    }

    @Override
    public T get(int i) {
        if ( i < 0 || i >= tokens.size() ) {
            throw new IndexOutOfBoundsException("token index "+i+" out of range 0.."+(tokens.size()-1));
        }
        return tokens.get(i);
    }

	/** Get all tokens from start..stop inclusively. */
	public List<T> get(int start, int stop) {
		if ( start<0 || stop<0 ) return null;
		lazyInit();
		List<T> subset = new ArrayList<T>();
		if ( stop>=tokens.size() ) stop = tokens.size()-1;
		for (int i = start; i <= stop; i++) {
			T t = tokens.get(i);
			if ( t.getType()==Token.EOF ) break;
			subset.add(t);
		}
		return subset;
	}

	@Override
	public int LA(int i) { return LT(i).getType(); }

    protected T LB(int k) {
        if ( (p-k)<0 ) return null;
        return tokens.get(p-k);
    }

    @Override
    public T LT(int k) {
        lazyInit();
        if ( k==0 ) return null;
        if ( k < 0 ) return LB(-k);

		int i = p + k - 1;
		sync(i);
        if ( i >= tokens.size() ) { // return EOF token
            // EOF must be last token
            return tokens.get(tokens.size()-1);
        }
//		if ( i>range ) range = i;
        return tokens.get(i);
    }

	/**
	 * Allowed derived classes to modify the behavior of operations which change
	 * the current stream position by adjusting the target token index of a seek
	 * operation. The default implementation simply returns {@code i}. If an
	 * exception is thrown in this method, the current stream index should not be
	 * changed.
	 * <p>
	 * For example, {@link CommonTokenStream} overrides this method to ensure that
	 * the seek target is always an on-channel token.
	 *
	 * @param i The target token index.
	 * @return The adjusted target token index.
	 */
	protected int adjustSeekIndex(int i) {
		return i;
	}

	protected final void lazyInit() {
		if (p == -1) {
			setup();
		}
	}

    protected void setup() {
		sync(0);
		p = adjustSeekIndex(0);
	}

    /** Reset this token stream by setting its token source. */
    public void setTokenSource(TokenSource<? extends T> tokenSource) {
        this.tokenSource = tokenSource;
        tokens.clear();
        p = -1;
    }

    public List<T> getTokens() { return tokens; }

    public List<T> getTokens(int start, int stop) {
        return getTokens(start, stop, null);
    }

    /** Given a start and stop index, return a {@code List} of all tokens in
     *  the token type {@code BitSet}.  Return {@code null} if no tokens were found.  This
     *  method looks at both on and off channel tokens.
     */
    public List<T> getTokens(int start, int stop, BitSet types) {
        lazyInit();
		if ( start<0 || stop>=tokens.size() ||
			 stop<0  || start>=tokens.size() )
		{
			throw new IndexOutOfBoundsException("start "+start+" or stop "+stop+
												" not in 0.."+(tokens.size()-1));
		}

        if ( start>stop ) return null;

        // list = tokens[start:stop]:{T t, t.getType() in types}
        List<T> filteredTokens = new ArrayList<T>();
        for (int i=start; i<=stop; i++) {
            T t = tokens.get(i);
            if ( types==null || types.get(t.getType()) ) {
                filteredTokens.add(t);
            }
        }
        if ( filteredTokens.isEmpty() ) {
            filteredTokens = null;
        }
        return filteredTokens;
    }

    public List<T> getTokens(int start, int stop, int ttype) {
		BitSet s = new BitSet(ttype);
		s.set(ttype);
		return getTokens(start,stop, s);
    }

	/** Given a starting index, return the index of the next token on channel.
	 *  Return {@code i} if {@code tokens[i]} is on channel.  Return {@code -1} if there are no tokens
	 *  on channel between {@code i} and EOF.
	 */
	protected int nextTokenOnChannel(int i, int channel) {
		sync(i);
		Token token = tokens.get(i);
		if ( i>=size() ) return -1;
		while ( token.getChannel()!=channel ) {
			if ( token.getType()==Token.EOF ) return -1;
			i++;
			sync(i);
			token = tokens.get(i);
		}
		return i;
	}

	/** Given a starting index, return the index of the previous token on channel.
	 *  Return {@code i} if {@code tokens[i]} is on channel. Return {@code -1} if there are no tokens
	 *  on channel between {@code i} and {@code 0}.
	 */
	protected int previousTokenOnChannel(int i, int channel) {
		while ( i>=0 && tokens.get(i).getChannel()!=channel ) {
			i--;
		}
		return i;
	}

	/** Collect all tokens on specified channel to the right of
	 *  the current token up until we see a token on {@link Lexer#DEFAULT_TOKEN_CHANNEL} or
	 *  EOF. If {@code channel} is {@code -1}, find any non default channel token.
	 */
	public List<T> getHiddenTokensToRight(int tokenIndex, int channel) {
		lazyInit();
		if ( tokenIndex<0 || tokenIndex>=tokens.size() ) {
			throw new IndexOutOfBoundsException(tokenIndex+" not in 0.."+(tokens.size()-1));
		}

		int nextOnChannel =
			nextTokenOnChannel(tokenIndex + 1, Lexer.DEFAULT_TOKEN_CHANNEL);
		int to;
		int from = tokenIndex+1;
		// if none onchannel to right, nextOnChannel=-1 so set to = last token
		if ( nextOnChannel == -1 ) to = size()-1;
		else to = nextOnChannel;

		return filterForChannel(from, to, channel);
	}

	/** Collect all hidden tokens (any off-default channel) to the right of
	 *  the current token up until we see a token on {@link Lexer#DEFAULT_TOKEN_CHANNEL}
	 *  or EOF.
	 */
	public List<T> getHiddenTokensToRight(int tokenIndex) {
		return getHiddenTokensToRight(tokenIndex, -1);
	}

	/** Collect all tokens on specified channel to the left of
	 *  the current token up until we see a token on {@link ANTLRErrorListener#DEFAULT_TOKEN_CHANNEL}.
	 *  If {@code channel} is {@code -1}, find any non default channel token.
	 */
	public List<T> getHiddenTokensToLeft(int tokenIndex, int channel) {
		lazyInit();
		if ( tokenIndex<0 || tokenIndex>=tokens.size() ) {
			throw new IndexOutOfBoundsException(tokenIndex+" not in 0.."+(tokens.size()-1));
		}

		int prevOnChannel =
			previousTokenOnChannel(tokenIndex - 1, Lexer.DEFAULT_TOKEN_CHANNEL);
		if ( prevOnChannel == tokenIndex - 1 ) return null;
		// if none onchannel to left, prevOnChannel=-1 then from=0
		int from = prevOnChannel+1;
		int to = tokenIndex-1;

		return filterForChannel(from, to, channel);
	}

	/** Collect all hidden tokens (any off-default channel) to the left of
	 *  the current token up until we see a token on {@link Lexer#DEFAULT_TOKEN_CHANNEL}.
	 */
	public List<T> getHiddenTokensToLeft(int tokenIndex) {
		return getHiddenTokensToLeft(tokenIndex, -1);
	}

	protected List<T> filterForChannel(int from, int to, int channel) {
		List<T> hidden = new ArrayList<T>();
		for (int i=from; i<=to; i++) {
			T t = tokens.get(i);
			if ( channel==-1 ) {
				if ( t.getChannel()!= Lexer.DEFAULT_TOKEN_CHANNEL ) hidden.add(t);
			}
			else {
				if ( t.getChannel()==channel ) hidden.add(t);
			}
		}
		if ( hidden.isEmpty() ) return null;
		return hidden;
	}

	@Override
    public String getSourceName() {	return tokenSource.getSourceName();	}

	/** Get the text of all tokens in this buffer. */
	@NotNull
	@Override
	public String getText() {
		fill();
		return getText(Interval.of(0,size()-1));
	}

	@NotNull
    @Override
    public String getText(Interval interval) {
		int start = interval.a;
		int stop = interval.b;
        if ( start<0 || stop<0 ) return "";
        lazyInit();
        if ( stop>=tokens.size() ) stop = tokens.size()-1;

		StringBuilder buf = new StringBuilder();
		for (int i = start; i <= stop; i++) {
			T t = tokens.get(i);
			if ( t.getType()==Token.EOF ) break;
			buf.append(t.getText());
		}
		return buf.toString();
    }

	@NotNull
	@Override
	public String getText(RuleContext<?> ctx) {
		return getText(ctx.getSourceInterval());
	}

	@NotNull
    @Override
    public String getText(Object start, Object stop) {
        if ( start instanceof Token && stop instanceof Token ) {
            return getText(Interval.of(((Token)start).getTokenIndex(), ((Token)stop).getTokenIndex()));
        }

		return "";
    }

    /** Get all tokens from lexer until EOF. */
    public void fill() {
        lazyInit();
		final int blockSize = 1000;
		while (true) {
			int fetched = fetch(blockSize);
			if (fetched < blockSize) {
				return;
			}
		}
    }
}
