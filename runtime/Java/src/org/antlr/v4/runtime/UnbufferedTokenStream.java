package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

import java.util.Arrays;

public class UnbufferedTokenStream<T extends Token> implements TokenStream {
	protected TokenSource tokenSource;

	/** A moving window buffer of the data being scanned. While there's a
	 *  marker, we keep adding to buffer.  Otherwise, consume() resets
	 *  so we start filling at index 0 again.
	 */
	protected Token[] tokens;

	/** How many tokens are actually in the buffer; this is not
	 *  the buffer size, that's tokens.length.
	 */
	protected int n;

	/** 0..n-1 index into tokens of next token; tokens[p] is LT(1).
	 *  If p == n, we are out of buffered tokens.
	 */
	protected int p=0;

	/** Count up with mark() and down with release(). When we release()
	 *  and hit zero, reset buffer to beginning. Copy data[p]..data[n-1]
	 *  to data[0]..data[(n-1)-p].
	 */
	protected int numMarkers = 0;

	protected Token lastToken;

	/** Absolute token index. It's the index of the token about to be
	 *  read via LA(1). Goes from 0 to numtokens-1 in entire stream.
	 */
	protected int currentTokenIndex = 0; // simple counter to set token index in tokens

    /** Skip tokens on any channel but this one; this is how we skip whitespace... */
	//  TODO: skip off-channel tokens!!!
    protected int channel = Token.DEFAULT_CHANNEL;

	public UnbufferedTokenStream(TokenSource tokenSource) {
		this(tokenSource, 256);
	}

	public UnbufferedTokenStream(TokenSource tokenSource, int bufferSize) {
		this.tokenSource = tokenSource;
		tokens = new Token[bufferSize];
		n = 0;
		fill(1); // prime the pump
	}

	@Override
	public Token get(int i) { // get absolute index
		int bufferStartIndex = getBufferStartIndex();
		if (i < bufferStartIndex || i >= bufferStartIndex + n) {
			throw new IndexOutOfBoundsException("get("+i+") outside buffer: "+
			                    bufferStartIndex+".."+(bufferStartIndex+n));
		}
		return tokens[i - bufferStartIndex];
	}

	@Override
	public Token LT(int i) {
		if ( i==-1 ) return lastToken; // special case
        sync(i);
        int index = p + i - 1;
        if ( index < 0 ) throw new IndexOutOfBoundsException("LT("+i+") gives negative index");
		if ( index > n ) {
			TokenFactory<?> factory = tokenSource.getTokenFactory();
			int cpos = tokenSource.getCharPositionInLine();
			// The character position for EOF is one beyond the position of
			// the previous token's last character
			Token eof = factory.create(tokenSource, Token.EOF, null, Token.DEFAULT_CHANNEL,
									   index(), index()-1,
									   tokenSource.getLine(), cpos);
			return eof;
		}
        return tokens[index];
	}

	@Override
	public int LA(int i) { return LT(i).getType(); }

	@Override
	public TokenSource getTokenSource() {
		return null;
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public String getText(RuleContext ctx) {
		return getText(ctx.getSourceInterval());
	}

	@Override
	public String getText(Token start, Token stop) {
		return getText(Interval.of(start.getTokenIndex(), stop.getTokenIndex()));
	}

	@Override
	public void consume() {
		// buf always has at least data[p==0] in this method due to ctor
		if ( p==0 ) lastToken = null; // we're at first token; no LA(-1)
		else lastToken = tokens[p];   // track last char for LT(-1)

		// if we're at last token and no markers, opportunity to flush buffer
		if ( p == n-1 && numMarkers==0 ) { // can we release buffer?
//			System.out.println("consume: reset");
			n = 0;
			p = -1; // p++ will leave this at 0
		}

		p++;
		currentTokenIndex++;
//		System.out.println("consume p="+p+", numMarkers="+numMarkers+
//						   ", currentCharIndex="+currentCharIndex+", n="+n);
		sync(1);
	}

	/** Make sure we have 'need' elements from current position p. Last valid
	 *  p index is tokens.size()-1.  p+need-1 is the tokens index 'need' elements
	 *  ahead.  If we need 1 element, (p+1-1)==p must be < tokens.size().
	 */
	protected void sync(int want) {
		int need = (p+want-1) - n + 1; // how many more elements we need?
		if ( need > 0 ) fill(need);    // out of elements?
	}

	/** add n elements to buffer */
	public void fill(int n) {
		for (int i=1; i<=n; i++) {
			Token t = tokenSource.nextToken();
			if ( t instanceof WritableToken ) {
				((WritableToken)t).setTokenIndex(currentTokenIndex);
			}
			add(t);
		}
	}

	protected void add(Token t) {
		if ( n>=tokens.length ) {
			Token[] newtokens = new Token[tokens.length*2]; // resize
			System.arraycopy(tokens, 0, newtokens, 0, tokens.length);
			tokens = newtokens;
		}
		tokens[n++] = t;
	}


	/** Return a marker that we can release later.  Marker happens to be
	 *  index into buffer (not index()).
	 */
	@Override
	public int mark() {
		int m = p;
		numMarkers++;
		return m;
	}

	@Override
	public void release(int marker) {
		if ( numMarkers==0 ) {
			throw new IllegalStateException("release() called w/o prior matching mark()");
		}
//		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
//		System.out.println(stackTrace[2].getMethodName()+": release " + marker);
		numMarkers--;
		if ( numMarkers==0 ) { // can we release buffer?
			System.out.println("release: shift "+p+".."+(n-1)+" to 0: '"+
								   Arrays.toString(Arrays.copyOfRange(tokens,p,n))+"'");
			// Copy data[p]..data[n-1] to data[0]..data[(n-1)-p], reset ptrs
			// p is last valid token; move nothing if p==n as we have no valid char
			System.arraycopy(tokens, p, tokens, 0, n - p); // shift n-p char from p to 0
			n = n - p;
			p = 0;
		}
	}

	@Override
	public int index() {
		return currentTokenIndex;
	}

	@Override
	public void seek(int index) { // seek to absolute index
		int bufferStartIndex = getBufferStartIndex();
		int i = index - bufferStartIndex;
		if ( i < 0 || i >= n ) {
			throw new UnsupportedOperationException("seek to index outside buffer: "+
													index+" not in "+ bufferStartIndex +".."+(bufferStartIndex +n));
		}
		p = i;
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException("Unbuffered stream cannot know its size");
	}

	@Override
	public String getSourceName() {
		return tokenSource.getSourceName();
	}

	@Override
	public String getText(Interval interval) {
		int bufferStartIndex = getBufferStartIndex();
		int bufferStopIndex = bufferStartIndex + tokens.length - 1;

		int start = interval.a;
		int stop = interval.b;
		if (start < bufferStartIndex || stop > bufferStopIndex) {
			throw new UnsupportedOperationException("interval "+interval+" not in token buffer window: "+
													bufferStartIndex+".."+bufferStopIndex);
		}

		int a = start - bufferStartIndex;
		int b = stop - bufferStartIndex;

		StringBuilder buf = new StringBuilder();
		for (int i = a; i <= b; i++) {
			Token t = tokens[i];
			buf.append(t.getText());
		}

		return buf.toString();
	}

	public int getBufferStartIndex() {
		return currentTokenIndex - p;
	}
}
