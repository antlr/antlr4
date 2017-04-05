/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.Interval;

/**
 * An {@link IntStream} whose symbols are {@link Token} instances.
 */
public interface TokenStream extends IntStream {
	/**
	 * Get the {@link Token} instance associated with the value returned by
	 * {@link #LA LA(k)}. This method has the same pre- and post-conditions as
	 * {@link IntStream#LA}. In addition, when the preconditions of this method
	 * are met, the return value is non-null and the value of
	 * {@code LT(k).getType()==LA(k)}.
	 *
	 * @see IntStream#LA
	 */
	public Token LT(int k);

	/**
	 * Gets the {@link Token} at the specified {@code index} in the stream. When
	 * the preconditions of this method are met, the return value is non-null.
	 *
	 * <p>The preconditions for this method are the same as the preconditions of
	 * {@link IntStream#seek}. If the behavior of {@code seek(index)} is
	 * unspecified for the current state and given {@code index}, then the
	 * behavior of this method is also unspecified.</p>
	 *
	 * <p>The symbol referred to by {@code index} differs from {@code seek()} only
	 * in the case of filtering streams where {@code index} lies before the end
	 * of the stream. Unlike {@code seek()}, this method does not adjust
	 * {@code index} to point to a non-ignored symbol.</p>
	 *
	 * @throws IllegalArgumentException if {code index} is less than 0
	 * @throws UnsupportedOperationException if the stream does not support
	 * retrieving the token at the specified index
	 */
	public Token get(int index);

	/**
	 * Gets the underlying {@link TokenSource} which provides tokens for this
	 * stream.
	 */
	public TokenSource getTokenSource();

	/**
	 * Return the text of all tokens within the specified {@code interval}. This
	 * method behaves like the following code (including potential exceptions
	 * for violating preconditions of {@link #get}, but may be optimized by the
	 * specific implementation.
	 *
	 * <pre>
	 * TokenStream stream = ...;
	 * String text = "";
	 * for (int i = interval.a; i &lt;= interval.b; i++) {
	 *   text += stream.get(i).getText();
	 * }
	 * </pre>
	 *
	 * @param interval The interval of tokens within this stream to get text
	 * for.
	 * @return The text of all tokens within the specified interval in this
	 * stream.
	 *
	 * @throws NullPointerException if {@code interval} is {@code null}
	 */
	public String getText(Interval interval);

	/**
	 * Return the text of all tokens in the stream. This method behaves like the
	 * following code, including potential exceptions from the calls to
	 * {@link IntStream#size} and {@link #getText(Interval)}, but may be
	 * optimized by the specific implementation.
	 *
	 * <pre>
	 * TokenStream stream = ...;
	 * String text = stream.getText(new Interval(0, stream.size()));
	 * </pre>
	 *
	 * @return The text of all tokens in the stream.
	 */
	public String getText();

	/**
	 * Return the text of all tokens in the source interval of the specified
	 * context. This method behaves like the following code, including potential
	 * exceptions from the call to {@link #getText(Interval)}, but may be
	 * optimized by the specific implementation.
	 *
	 * <p>If {@code ctx.getSourceInterval()} does not return a valid interval of
	 * tokens provided by this stream, the behavior is unspecified.</p>
	 *
	 * <pre>
	 * TokenStream stream = ...;
	 * String text = stream.getText(ctx.getSourceInterval());
	 * </pre>
	 *
	 * @param ctx The context providing the source interval of tokens to get
	 * text for.
	 * @return The text of all tokens within the source interval of {@code ctx}.
	 */
	public String getText(RuleContext ctx);

	/**
	 * Return the text of all tokens in this stream between {@code start} and
	 * {@code stop} (inclusive).
	 *
	 * <p>If the specified {@code start} or {@code stop} token was not provided by
	 * this stream, or if the {@code stop} occurred before the {@code start}
	 * token, the behavior is unspecified.</p>
	 *
	 * <p>For streams which ensure that the {@link Token#getTokenIndex} method is
	 * accurate for all of its provided tokens, this method behaves like the
	 * following code. Other streams may implement this method in other ways
	 * provided the behavior is consistent with this at a high level.</p>
	 *
	 * <pre>
	 * TokenStream stream = ...;
	 * String text = "";
	 * for (int i = start.getTokenIndex(); i &lt;= stop.getTokenIndex(); i++) {
	 *   text += stream.get(i).getText();
	 * }
	 * </pre>
	 *
	 * @param start The first token in the interval to get text for.
	 * @param stop The last token in the interval to get text for (inclusive).
	 * @return The text of all tokens lying between the specified {@code start}
	 * and {@code stop} tokens.
	 *
	 * @throws UnsupportedOperationException if this stream does not support
	 * this method for the specified tokens
	 */
	public String getText(Token start, Token stop);
}
