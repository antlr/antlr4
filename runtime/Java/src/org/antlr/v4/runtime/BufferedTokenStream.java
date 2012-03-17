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

import org.antlr.v4.runtime.misc.NotNull;

import java.util.*;

/** Buffer all input tokens but do on-demand fetching of new tokens from
 *  lexer. Useful when the parser or lexer has to set context/mode info before
 *  proper lexing of future tokens. The ST template parser needs this,
 *  for example, because it has to constantly flip back and forth between
 *  inside/output templates. E.g., <names:{hi, <it>}> has to parse names
 *  as part of an expression but "hi, <it>" as a nested template.
 *
 *  You can't use this stream if you pass whitespace or other off-channel
 *  tokens to the parser. The stream can't ignore off-channel tokens.
 *  (UnbufferedTokenStream is the same way.)  Use CommonTokenStream.
 *
 *  This is not a subclass of UnbufferedTokenStream because I don't want
 *  to confuse small moving window of tokens it uses for the full buffer.
 */
public class BufferedTokenStream<T extends Token> implements TokenStream {
	@NotNull
    protected TokenSource tokenSource;

    /** Record every single token pulled from the source so we can reproduce
     *  chunks of it later.  The buffer in LookaheadStream overlaps sometimes
     *  as its moving window moves through the input.  This list captures
     *  everything so we can access complete input text.
     */
    protected List<T> tokens = new ArrayList<T>(100);

    /** The index into the tokens list of the current token (next token
     *  to consume).  tokens[p] should be LT(1).  p=-1 indicates need
     *  to initialize with first token.  The ctor doesn't get a token.
     *  First call to LT(1) or whatever gets the first token and sets p=0;
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
        p = 0;
    }

    @Override
    public void seek(int index) {
        lazyInit();
        p = index;
    }

    @Override
    public int size() { return tokens.size(); }

    /** Move the input pointer to the next incoming token.  The stream
     *  must become active with LT(1) available.  consume() simply
     *  moves the input pointer so that LT(1) points at the next
     *  input symbol. Consume at least one token, unless EOF has been reached.
     */
    @Override
    public void consume() {
        lazyInit();
		if (sync(p + 1)) {
			p++;
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
            throw new NoSuchElementException("token index "+i+" out of range 0.."+(tokens.size()-1));
        }
        return tokens.get(i);
    }

	/** Get all tokens from start..stop inclusively */
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

	protected final void lazyInit() {
		if (p == -1) {
			setup();
		}
	}

    protected void setup() { sync(0); p = 0; }

    /** Reset this token stream by setting its token source. */
    public void setTokenSource(TokenSource tokenSource) {
        this.tokenSource = tokenSource;
        tokens.clear();
        p = -1;
    }

    public List<T> getTokens() { return tokens; }

    public List<T> getTokens(int start, int stop) {
        return getTokens(start, stop, null);
    }

    /** Given a start and stop index, return a List of all tokens in
     *  the token type BitSet.  Return null if no tokens were found.  This
     *  method looks at both on and off channel tokens.
     */
    public List<T> getTokens(int start, int stop, Set<Integer> types) {
        lazyInit();
        if ( stop>=tokens.size() ) stop=tokens.size()-1;
        if ( start<0 ) start=0;
        if ( start>stop ) return null;

        // list = tokens[start:stop]:{T t, t.getType() in types}
        List<T> filteredTokens = new ArrayList<T>();
        for (int i=start; i<=stop; i++) {
            T t = tokens.get(i);
            if ( types==null || types.contains(t.getType()) ) {
                filteredTokens.add(t);
            }
        }
        if ( filteredTokens.isEmpty() ) {
            filteredTokens = null;
        }
        return filteredTokens;
    }

    public List<T> getTokens(int start, int stop, int ttype) {
		HashSet<Integer> s = new HashSet<Integer>(ttype);
		s.add(ttype);
		return getTokens(start,stop, s);
    }

    @Override
    public String getSourceName() {	return tokenSource.getSourceName();	}

    /** Grab *all* tokens from stream and return string */
    @Override
    public String toString() {
        lazyInit();
        fill();
        return toString(0, tokens.size()-1);
    }

    @Override
    public String toString(int start, int stop) {
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

    @Override
    public String toString(Token start, Token stop) {
        if ( start!=null && stop!=null ) {
            return toString(start.getTokenIndex(), stop.getTokenIndex());
        }
        return null;
    }

    /** Get all tokens from lexer until EOF */
    public void fill() {
        lazyInit();
        if ( tokens.get(p).getType()==Token.EOF ) return;

        int i = p+1;
        sync(i);
        while ( tokens.get(i).getType()!=Token.EOF ) {
            i++;
            sync(i);
        }
    }
}
