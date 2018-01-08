/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime;

/**
 * A simple stream of symbols whose values are represented as integers. This
 * interface provides <em>marked ranges</em> with support for a minimum level
 * of buffering necessary to implement arbitrary lookahead during prediction.
 * For more information on marked ranges, see {@link #mark}.
 *
 * <p><strong>Initializing Methods:</strong> Some methods in this interface have
 * unspecified behavior if no call to an initializing method has occurred after
 * the stream was constructed. The following is a list of initializing methods:</p>
 *
 * <ul>
 *   <li>{@link #LA}</li>
 *   <li>{@link #consume}</li>
 *   <li>{@link #size}</li>
 * </ul>
 */
public interface IntStream {
	/**
	 * The value returned by {@link #LA LA()} when the end of the stream is
	 * reached.
	 */
	public static final int EOF = -1;

	/**
	 * The value returned by {@link #getSourceName} when the actual name of the
	 * underlying source is not known.
	 */
	public static final String UNKNOWN_SOURCE_NAME = "<unknown>";

	/**
	 * Consumes the current symbol in the stream. This method has the following
	 * effects:
	 *
	 * <ul>
	 *   <li><strong>Forward movement:</strong> The value of {@link #index index()}
	 *		before calling this method is less than the value of {@code index()}
	 *		after calling this method.</li>
	 *   <li><strong>Ordered lookahead:</strong> The value of {@code LA(1)} before
	 *		calling this method becomes the value of {@code LA(-1)} after calling
	 *		this method.</li>
	 * </ul>
	 *
	 * Note that calling this method does not guarantee that {@code index()} is
	 * incremented by exactly 1, as that would preclude the ability to implement
	 * filtering streams (e.g. {@link CommonTokenStream} which distinguishes
	 * between "on-channel" and "off-channel" tokens).
	 *
	 * @throws IllegalStateException if an attempt is made to consume the
	 * end of the stream (i.e. if {@code LA(1)==}{@link #EOF EOF} before calling
	 * {@code consume}).
	 */
	void consume();

	/**
	 * Gets the value of the symbol at offset {@code i} from the current
	 * position. When {@code i==1}, this method returns the value of the current
	 * symbol in the stream (which is the next symbol to be consumed). When
	 * {@code i==-1}, this method returns the value of the previously read
	 * symbol in the stream. It is not valid to call this method with
	 * {@code i==0}, but the specific behavior is unspecified because this
	 * method is frequently called from performance-critical code.
	 *
	 * <p>This method is guaranteed to succeed if any of the following are true:</p>
	 *
	 * <ul>
	 *   <li>{@code i>0}</li>
	 *   <li>{@code i==-1} and {@link #index index()} returns a value greater
	 *     than the value of {@code index()} after the stream was constructed
	 *     and {@code LA(1)} was called in that order. Specifying the current
	 *     {@code index()} relative to the index after the stream was created
	 *     allows for filtering implementations that do not return every symbol
	 *     from the underlying source. Specifying the call to {@code LA(1)}
	 *     allows for lazily initialized streams.</li>
	 *   <li>{@code LA(i)} refers to a symbol consumed within a marked region
	 *     that has not yet been released.</li>
	 * </ul>
	 *
	 * <p>If {@code i} represents a position at or beyond the end of the stream,
	 * this method returns {@link #EOF}.</p>
	 *
	 * <p>The return value is unspecified if {@code i<0} and fewer than {@code -i}
	 * calls to {@link #consume consume()} have occurred from the beginning of
	 * the stream before calling this method.</p>
	 *
	 * @throws UnsupportedOperationException if the stream does not support
	 * retrieving the value of the specified symbol
	 */
	int LA(int i);

	/**
	 * A mark provides a guarantee that {@link #seek seek()} operations will be
	 * valid over a "marked range" extending from the index where {@code mark()}
	 * was called to the current {@link #index index()}. This allows the use of
	 * streaming input sources by specifying the minimum buffering requirements
	 * to support arbitrary lookahead during prediction.
	 *
	 * <p>The returned mark is an opaque handle (type {@code int}) which is passed
	 * to {@link #release release()} when the guarantees provided by the marked
	 * range are no longer necessary. When calls to
	 * {@code mark()}/{@code release()} are nested, the marks must be released
	 * in reverse order of which they were obtained. Since marked regions are
	 * used during performance-critical sections of prediction, the specific
	 * behavior of invalid usage is unspecified (i.e. a mark is not released, or
	 * a mark is released twice, or marks are not released in reverse order from
	 * which they were created).</p>
	 *
	 * <p>The behavior of this method is unspecified if no call to an
	 * {@link IntStream initializing method} has occurred after this stream was
	 * constructed.</p>
	 *
	 * <p>This method does not change the current position in the input stream.</p>
	 *
	 * <p>The following example shows the use of {@link #mark mark()},
	 * {@link #release release(mark)}, {@link #index index()}, and
	 * {@link #seek seek(index)} as part of an operation to safely work within a
	 * marked region, then restore the stream position to its original value and
	 * release the mark.</p>
	 * <pre>
	 * IntStream stream = ...;
	 * int index = -1;
	 * int mark = stream.mark();
	 * try {
	 *   index = stream.index();
	 *   // perform work here...
	 * } finally {
	 *   if (index != -1) {
	 *     stream.seek(index);
	 *   }
	 *   stream.release(mark);
	 * }
	 * </pre>
	 *
	 * @return An opaque marker which should be passed to
	 * {@link #release release()} when the marked range is no longer required.
	 */
	int mark();

	/**
	 * This method releases a marked range created by a call to
	 * {@link #mark mark()}. Calls to {@code release()} must appear in the
	 * reverse order of the corresponding calls to {@code mark()}. If a mark is
	 * released twice, or if marks are not released in reverse order of the
	 * corresponding calls to {@code mark()}, the behavior is unspecified.
	 *
	 * <p>For more information and an example, see {@link #mark}.</p>
	 *
	 * @param marker A marker returned by a call to {@code mark()}.
	 * @see #mark
	 */
	void release(int marker);

	/**
	 * Return the index into the stream of the input symbol referred to by
	 * {@code LA(1)}.
	 *
	 * <p>The behavior of this method is unspecified if no call to an
	 * {@link IntStream initializing method} has occurred after this stream was
	 * constructed.</p>
	 */
	int index();

	/**
	 * Set the input cursor to the position indicated by {@code index}. If the
	 * specified index lies past the end of the stream, the operation behaves as
	 * though {@code index} was the index of the EOF symbol. After this method
	 * returns without throwing an exception, then at least one of the following
	 * will be true.
	 *
	 * <ul>
	 *   <li>{@link #index index()} will return the index of the first symbol
	 *     appearing at or after the specified {@code index}. Specifically,
	 *     implementations which filter their sources should automatically
	 *     adjust {@code index} forward the minimum amount required for the
	 *     operation to target a non-ignored symbol.</li>
	 *   <li>{@code LA(1)} returns {@link #EOF}</li>
	 * </ul>
	 *
	 * This operation is guaranteed to not throw an exception if {@code index}
	 * lies within a marked region. For more information on marked regions, see
	 * {@link #mark}. The behavior of this method is unspecified if no call to
	 * an {@link IntStream initializing method} has occurred after this stream
	 * was constructed.
	 *
	 * @param index The absolute index to seek to.
	 *
	 * @throws IllegalArgumentException if {@code index} is less than 0
	 * @throws UnsupportedOperationException if the stream does not support
	 * seeking to the specified index
	 */
	void seek(int index);

	/**
	 * Returns the total number of symbols in the stream, including a single EOF
	 * symbol.
	 *
	 * @throws UnsupportedOperationException if the size of the stream is
	 * unknown.
	 */
	int size();

	/**
	 * Gets the name of the underlying symbol source. This method returns a
	 * non-null, non-empty string. If such a name is not known, this method
	 * returns {@link #UNKNOWN_SOURCE_NAME}.
	 */

	public String getSourceName();
}
