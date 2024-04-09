/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.Pair;

/**
 * This implementation of {@link TokenFactory} creates
 * {@link CommonTokenWithStates} objects.
 */
public class CommonTokenWithStatesFactory implements TokenFactory<CommonTokenWithStates> {

	/**
	 * The default {@link CommonTokenWithStatesFactory} instance.
	 *
	 * <p>
	 * This token factory does not explicitly copy token text when constructing
	 * tokens.</p>
	 */
	public static final TokenFactory<CommonTokenWithStates> DEFAULT = new CommonTokenWithStatesFactory();

	/**
	 * Indicates whether {@link CommonToken#setText} should be called after
	 * constructing tokens to explicitly set the text. This is useful for cases
	 * where the input stream might not be able to provide arbitrary substrings
	 * of text from the input after the lexer creates a token (e.g. the
	 * implementation of {@link CharStream#getText} in
	 * {@link UnbufferedCharStream} throws an
	 * {@link UnsupportedOperationException}). Explicitly setting the token text
	 * allows {@link Token#getText} to be called at any time regardless of the
	 * input stream implementation.
	 *
	 * <p>
	 * The default value is {@code false} to avoid the performance and memory
	 * overhead of copying text for every token unless explicitly requested.</p>
	 */
	protected final boolean copyText;

	/**
	 * Constructs a {@link CommonTokenWithStatesFactory} with the specified value for
	 * {@link #copyText}.
	 *
	 * <p>
	 * When {@code copyText} is {@code false}, the {@link #DEFAULT} instance
	 * should be used instead of constructing a new instance.</p>
	 *
	 * @param copyText The value for {@link #copyText}.
	 */
	public CommonTokenWithStatesFactory(boolean copyText) { this.copyText = copyText; }

	/**
	 * Constructs a {@link CommonTokenWithStatesFactory} with {@link #copyText} set to
	 * {@code false}.
	 *
	 * <p>
	 * The {@link #DEFAULT} instance should be used instead of calling this
	 * directly.</p>
	 */
	public CommonTokenWithStatesFactory() { this(false); }

	@Override
	public CommonTokenWithStates create(Pair<TokenSource, CharStream> source, int type, String text,
										int channel, int start, int stop,
										int line, int charPositionInLine)
	{
		CommonTokenWithStates t = new CommonTokenWithStates(source, type, channel, start, stop);
		t.setLine(line);
		t.setCharPositionInLine(charPositionInLine);
		if ( text!=null ) {
			t.setText(text);
		}
		else if ( copyText && source.b != null ) {
			t.setText(source.b.getText(Interval.of(start,stop)));
		}

		return t;
	}

	@Override
	public CommonTokenWithStates create(int type, String text) {
		return new CommonTokenWithStates(type, text);
	}
}
