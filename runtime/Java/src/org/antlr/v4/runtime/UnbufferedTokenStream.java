package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

import java.util.Arrays;
import java.util.List;

public class UnbufferedTokenStream<T extends Token> implements TokenStream<T> {
	protected TokenSource<T> tokenSource;

	/** A moving window buffer of the data being scanned. While there's a
	 *  marker, we keep adding to buffer.  Otherwise, consume() resets
	 *  so we start filling at index 0 again.
	 */
	protected T[] tokens;

	/** How many tokens are actually in the buffer; this is not
	 *  the buffer size, that's tokens.length.
	 */
	protected int n;

	/** 0..n-1 index into tokens of next token; tokens[p] is LA(1). */
	protected int p=0;

	protected int earliestMarker = -1;

	/** Absolute token index. It's the index of the token about to be
	 *  read via LA(1). Goes from 0 to numtokens-1 in entire stream.
	 */
	protected int currentTokenIndex = 0; // simple counter to set token index in tokens

	/** Buf is window into stream. This is absolute token index into entire
	 *  stream of tokens[0]
	 */
	protected int bufferStartTokenIndex = 0;

    /** Skip tokens on any channel but this one; this is how we skip whitespace... */
	//  TODO: skip off-channel tokens!!!
    protected int channel = Token.DEFAULT_CHANNEL;

	public UnbufferedTokenStream(TokenSource<T> tokenSource) {
		this(tokenSource, 256);
	}

	public UnbufferedTokenStream(TokenSource<T> tokenSource, int bufferSize) {
		this.tokenSource = tokenSource;
		@SuppressWarnings("unchecked")
		T[] tokens = (T[])new Object[bufferSize];
		this.tokens = tokens;
		fill(1); // prime the pump
	}

	@Override
	public T get(int i) {
		return null;
	}

	@Override
	public T LT(int i) {
		sync(i);
		int index = p + i - 1;
		if ( index < 0 || index > n ) throw new IndexOutOfBoundsException();
		return tokens[index];
	}

	@Override
	public int LA(int i) { return LT(i).getType(); }

	@Override
	public TokenSource<T> getTokenSource() {
		return null;
	}

	@Override
	public String getText() {
		return null;
	}

	@Override
	public String getText(RuleContext<?> ctx) {
		return null;
	}

	@Override
	public String getText(Object start, Object stop) {
		return null;
	}

	@Override
	public void consume() {
		p++;
		currentTokenIndex++;
		// have we hit end of buffer when no markers?
		if ( p==n && earliestMarker < 0 ) {
			// if so, it's an opportunity to start filling at index 0 again
			// System.out.println("p=="+n+", no marker; reset buf start index="+currentCharIndex);
			p = 0;
			n = 0;
			bufferStartTokenIndex = currentTokenIndex;
		}
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
			T t = tokenSource.nextToken();
			if ( t instanceof WritableToken ) {
				((WritableToken)t).setTokenIndex(currentTokenIndex);
			}
			add(t);
		}
	}

	protected void add(T t) {
		if ( n>=tokens.length ) {
			@SuppressWarnings("unchecked")
			T[] newtokens = (T[])new Object[tokens.length*2]; // resize
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
		if ( p < earliestMarker) {
			// they must have done seek to before min marker
			throw new IllegalArgumentException("can't set marker earlier than previous existing marker: "+p+"<"+ earliestMarker);
		}
		if ( earliestMarker < 0 ) earliestMarker = m; // set first marker
		return m;
	}

	@Override
	public void release(int marker) {
		// release is noop unless we remove earliest. then we don't need to
		// keep anything in buffer. We only care about earliest. Releasing
		// marker other than earliest does nothing as we can just keep in
		// buffer.
		if ( marker < earliestMarker || marker >= n ) {
			throw new IllegalArgumentException("invalid marker: "+
											   marker+" not in "+0+".."+n);
		}
		if ( marker == earliestMarker) earliestMarker = -1;
	}

	@Override
	public int index() {
		return p + bufferStartTokenIndex;
	}

	@Override
	public void seek(int index) {
		// index == to bufferStartIndex should set p to 0
		int i = index - bufferStartTokenIndex;
		if ( i < 0 || i >= n ) {
			throw new UnsupportedOperationException("seek to index outside buffer: "+
													index+" not in "+ bufferStartTokenIndex +".."+(bufferStartTokenIndex +n));
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
		int bufferStartIndex = currentTokenIndex - p;
		int bufferStopIndex = bufferStartIndex + tokens.length - 1;

		int start = interval.a;
		int stop = interval.b;
		if (start < bufferStartIndex || stop > bufferStopIndex) {
			throw new UnsupportedOperationException("interval "+interval+" not in token buffer window: "+
													bufferStartIndex+".."+bufferStopIndex);
		}

		StringBuilder buf = new StringBuilder();
		for (int i = start; i <= stop; i++) {
			Token t = tokens[i - bufferStartIndex];
			buf.append(t.getText());
		}

		return buf.toString();
	}

	/** For testing.  What's in moving window into tokens stream? */
	public List<T> getBuffer() {
		if ( n==0 ) return null;
		return Arrays.asList(Arrays.copyOfRange(tokens, 0, n));
	}

}
