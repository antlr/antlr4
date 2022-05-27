/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This implementation of {@link TokenStream} loads tokens from a
 * {@link TokenSource} on-demand, and places the tokens in a buffer to provide
 * access to any previous token by index.
 *
 * <p>
 * This token stream ignores the value of {@link Token#getChannel}. If your
 * parser requires the token stream filter tokens to only those on a particular
 * channel, such as {@link Token#DEFAULT_CHANNEL} or
 * {@link Token#HIDDEN_CHANNEL}, use a filtering token stream such a
 * {@link CommonTokenStream}.</p>
 */
public class BufferedTokenStream implements TokenStream {
	/**
	 * The {@link TokenSource} from which tokens for this stream are fetched.
	 */
    protected TokenSource tokenSource;

	/**
	 * A collection of all tokens fetched from the token source. The list is
	 * considered a complete view of the input once {@link #fetchedEOF} is set
	 * to {@code true}.
	 */
    protected List<Token> tokens = new ArrayList<Token>(100);

	/**
	 * The index into {@link #tokens} of the current token (next token to
	 * {@link #consume}). {@link #tokens}{@code [}{@link #p}{@code ]} should be
	 * {@link #LT LT(1)}.
	 *
	 * <p>This field is set to -1 when the stream is first constructed or when
	 * {@link #setTokenSource} is called, indicating that the first token has
	 * not yet been fetched from the token source. For additional information,
	 * see the documentation of {@link IntStream} for a description of
	 * Initializing Methods.</p>
	 */
    protected int p = -1;

	/**
	 * Indicates whether the {@link Token#EOF} token has been fetched from
	 * {@link #tokenSource} and added to {@link #tokens}. This field improves
	 * performance for the following cases:
	 *
	 * <ul>
	 * <li>{@link #consume}: The lookahead check in {@link #consume} to prevent
	 * consuming the EOF symbol is optimized by checking the values of
	 * {@link #fetchedEOF} and {@link #p} instead of calling {@link #LA}.</li>
	 * <li>{@link #fetch}: The check to prevent adding multiple EOF symbols into
	 * {@link #tokens} is trivial with this field.</li>
	 * <ul>
	 */
	protected boolean fetchedEOF;

    public BufferedTokenStream(TokenSource tokenSource) {
		if (tokenSource == null) {
			throw new NullPointerException("tokenSource cannot be null");
		}
        this.tokenSource = tokenSource;
    }

    @Override
    public TokenSource getTokenSource() { return tokenSource; }

	@Override
	public int index() { return p; }

    @Override
    public int mark() {
		return 0;
	}

	@Override
	public void release(int marker) {
		// no resources to release
	}

	/**
	 * This method resets the token stream back to the first token in the
	 * buffer. It is equivalent to calling {@link #seek}{@code (0)}.
	 *
	 * @see #setTokenSource(TokenSource)
	 * @deprecated Use {@code seek(0)} instead.
	 */
	@Deprecated
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

    @Override
    public void consume() {
		boolean skipEofCheck;
		if (p >= 0) {
			if (fetchedEOF) {
				// the last token in tokens is EOF. skip check if p indexes any
				// fetched token except the last.
				skipEofCheck = p < tokens.size() - 1;
			}
			else {
				// no EOF token in tokens. skip check if p indexes a fetched token.
				skipEofCheck = p < tokens.size();
			}
		}
		else {
			// not yet initialized
			skipEofCheck = false;
		}

		if (!skipEofCheck && LA(1) == EOF) {
			throw new IllegalStateException("cannot consume EOF");
		}

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
            Token t = tokenSource.nextToken();
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
    public Token get(int i) {
        if ( i < 0 || i >= tokens.size() ) {
            throw new IndexOutOfBoundsException("token index "+i+" out of range 0.."+(tokens.size()-1));
        }
        return tokens.get(i);
    }

	/** Get all tokens from start..stop inclusively */
	public List<Token> get(int start, int stop) {
		if ( start<0 || stop<0 ) return null;
		lazyInit();
		List<Token> subset = new ArrayList<Token>();
		if ( stop>=tokens.size() ) stop = tokens.size()-1;
		for (int i = start; i <= stop; i++) {
			Token t = tokens.get(i);
			if ( t.getType()==Token.EOF ) break;
			subset.add(t);
		}
		return subset;
	}

	@Override
	public int LA(int i) { return LT(i).getType(); }

    protected Token LB(int k) {
        if ( (p-k)<0 ) return null;
        return tokens.get(p-k);
    }


    @Override
    public Token LT(int k) {
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
	 *
	 * <p>For example, {@link CommonTokenStream} overrides this method to ensure that
	 * the seek target is always an on-channel token.</p>
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
    public void setTokenSource(TokenSource tokenSource) {
        this.tokenSource = tokenSource;
        tokens.clear();
        p = -1;
        fetchedEOF = false;
    }

    public List<Token> getTokens() { return tokens; }

    public List<Token> getTokens(int start, int stop) {
        return getTokens(start, stop, null);
    }

    /** Given a start and stop index, return a List of all tokens in
     *  the token type BitSet.  Return null if no tokens were found.  This
     *  method looks at both on and off channel tokens.
     */
    public List<Token> getTokens(int start, int stop, Set<Integer> types) {
        lazyInit();
		if ( start<0 || stop>=tokens.size() ||
			 stop<0  || start>=tokens.size() )
		{
			throw new IndexOutOfBoundsException("start "+start+" or stop "+stop+
												" not in 0.."+(tokens.size()-1));
		}
        if ( start>stop ) return null;

        // list = tokens[start:stop]:{T t, t.getType() in types}
        List<Token> filteredTokens = new ArrayList<Token>();
        for (int i=start; i<=stop; i++) {
            Token t = tokens.get(i);
            if ( types==null || types.contains(t.getType()) ) {
                filteredTokens.add(t);
            }
        }
        if ( filteredTokens.isEmpty() ) {
            filteredTokens = null;
        }
        return filteredTokens;
    }

    public List<Token> getTokens(int start, int stop, int ttype) {
		HashSet<Integer> s = new HashSet<Integer>(ttype);
		s.add(ttype);
		return getTokens(start,stop, s);
    }

	/**
	 * Given a starting index, return the index of the next token on channel.
	 * Return {@code i} if {@code tokens[i]} is on channel. Return the index of
	 * the EOF token if there are no tokens on channel between {@code i} and
	 * EOF.
	 */
	protected int nextTokenOnChannel(int i, int channel) {
		sync(i);
		if (i >= size()) {
			return size() - 1;
		}

		Token token = tokens.get(i);
		while ( token.getChannel()!=channel ) {
			if ( token.getType()==Token.EOF ) {
				return i;
			}

			i++;
			sync(i);
			token = tokens.get(i);
		}

		return i;
	}

	/**
	 * Given a starting index, return the index of the previous token on
	 * channel. Return {@code i} if {@code tokens[i]} is on channel. Return -1
	 * if there are no tokens on channel between {@code i} and 0.
	 *
	 * <p>
	 * If {@code i} specifies an index at or after the EOF token, the EOF token
	 * index is returned. This is due to the fact that the EOF token is treated
	 * as though it were on every channel.</p>
	 */
	protected int previousTokenOnChannel(int i, int channel) {
		sync(i);
		if (i >= size()) {
			// the EOF token is on every channel
			return size() - 1;
		}

		while (i >= 0) {
			Token token = tokens.get(i);
			if (token.getType() == Token.EOF || token.getChannel() == channel) {
				return i;
			}

			i--;
		}

		return i;
	}

	/** Collect all tokens on specified channel to the right of
	 *  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL or
	 *  EOF. If channel is -1, find any non default channel token.
	 */
	public List<Token> getHiddenTokensToRight(int tokenIndex, int channel) {
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
	 *  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL
	 *  or EOF.
	 */
	public List<Token> getHiddenTokensToRight(int tokenIndex) {
		return getHiddenTokensToRight(tokenIndex, -1);
	}

	/** Collect all tokens on specified channel to the left of
	 *  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
	 *  If channel is -1, find any non default channel token.
	 */
	public List<Token> getHiddenTokensToLeft(int tokenIndex, int channel) {
		lazyInit();
		if ( tokenIndex<0 || tokenIndex>=tokens.size() ) {
			throw new IndexOutOfBoundsException(tokenIndex+" not in 0.."+(tokens.size()-1));
		}

		if (tokenIndex == 0) {
			// obviously no tokens can appear before the first token
			return null;
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
	 *  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
	 */
	public List<Token> getHiddenTokensToLeft(int tokenIndex) {
		return getHiddenTokensToLeft(tokenIndex, -1);
	}

	protected List<Token> filterForChannel(int from, int to, int channel) {
		List<Token> hidden = new ArrayList<Token>();
		for (int i=from; i<=to; i++) {
			Token t = tokens.get(i);
			if ( channel==-1 ) {
				if ( t.getChannel()!= Lexer.DEFAULT_TOKEN_CHANNEL ) hidden.add(t);
			}
			else {
				if ( t.getChannel()==channel ) hidden.add(t);
			}
		}
		if ( hidden.size()==0 ) return null;
		return hidden;
	}

	@Override
    public String getSourceName() {	return tokenSource.getSourceName();	}

	/** Get the text of all tokens in this buffer. */

	@Override
	public String getText() {
		return getText(Interval.of(0,size()-1));
	}

	@Override
	public String getText(Interval interval) {
		int start = interval.a;
		int stop = interval.b;
		if ( start<0 || stop<0 ) return "";
		sync(stop);
        if ( stop>=tokens.size() ) stop = tokens.size()-1;

		StringBuilder buf = new StringBuilder();
		for (int i = start; i <= stop; i++) {
			Token t = tokens.get(i);
			if ( t.getType()==Token.EOF ) break;
			buf.append(t.getText());
		}
		return buf.toString();
    }


	@Override
	public String getText(RuleContext ctx) {
		return getText(ctx.getSourceInterval());
	}


    @Override
    public String getText(Token start, Token stop) {
        if ( start!=null && stop!=null ) {
            return getText(Interval.of(start.getTokenIndex(), stop.getTokenIndex()));
        }

		return "";
    }

    /** Get all tokens from lexer until EOF */
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
