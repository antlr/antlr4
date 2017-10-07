package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

/**
 * This class wraps an existing {@link CharStream} and converts all returned
 * symbols to upper or lower case. This is useful for case-insensitive grammars
 * where all input is assumed to be either upper or lower case.
 */
public class CaseChangingCharStream implements CharStream {

	final CharStream stream;
	final boolean upper;

	/**
	 * Constructs a new CaseChangingCharStream wrapping the given {@link CharStream} forcing
	 * all characters to upper case or lower case.
	 * @param stream The stream to wrap.
	 * @param upper If true force each symbol to upper case, otherwise force to lower.
	 */
	public CaseChangingCharStream(CharStream stream, boolean upper) {
		this.stream = stream;
		this.upper = upper;
	}

	@Override
	public String getText(Interval interval) {
		return stream.getText(interval);
	}

	@Override
	public void consume() {
		stream.consume();
	}

	@Override
	public int LA(int i) {
		int c = stream.LA(i);
		if (c <= 0) {
			return c;
		}
		if (upper) {
			return Character.toUpperCase(c);
		}
		return Character.toLowerCase(c);
	}

	@Override
	public int mark() {
		return stream.mark();
	}

	@Override
	public void release(int marker) {
		stream.release(marker);
	}

	@Override
	public int index() {
		return stream.index();
	}

	@Override
	public void seek(int index) {
		stream.seek(index);
	}

	@Override
	public int size() {
		return stream.size();
	}

	@Override
	public String getSourceName() {
		return stream.getSourceName();
	}
}
