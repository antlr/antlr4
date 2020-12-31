/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>A simple stream of symbols whose values are represented as integers.</summary>
    /// <remarks>
    /// A simple stream of symbols whose values are represented as integers. This
    /// interface provides <em>marked ranges</em> with support for a minimum level
    /// of buffering necessary to implement arbitrary lookahead during prediction.
    /// For more information on marked ranges, see
    /// <see cref="Mark()"/>
    /// .
    /// <p><strong>Initializing Methods:</strong> Some methods in this interface have
    /// unspecified behavior if no call to an initializing method has occurred after
    /// the stream was constructed. The following is a list of initializing methods:</p>
    /// <ul>
    /// <li>
    /// <see cref="LA(int)"/>
    /// </li>
    /// <li>
    /// <see cref="Consume()"/>
    /// </li>
    /// <li>
    /// <see cref="Size()"/>
    /// </li>
    /// </ul>
    /// </remarks>
    public interface IIntStream
    {
        /// <summary>Consumes the current symbol in the stream.</summary>
        /// <remarks>
        /// Consumes the current symbol in the stream. This method has the following
        /// effects:
        /// <ul>
        /// <li><strong>Forward movement:</strong> The value of
        /// <see cref="Index()">index()</see>
        /// before calling this method is less than the value of
        /// <c>index()</c>
        /// after calling this method.</li>
        /// <li><strong>Ordered lookahead:</strong> The value of
        /// <c>LA(1)</c>
        /// before
        /// calling this method becomes the value of
        /// <c>LA(-1)</c>
        /// after calling
        /// this method.</li>
        /// </ul>
        /// Note that calling this method does not guarantee that
        /// <c>index()</c>
        /// is
        /// incremented by exactly 1, as that would preclude the ability to implement
        /// filtering streams (e.g.
        /// <see cref="CommonTokenStream"/>
        /// which distinguishes
        /// between "on-channel" and "off-channel" tokens).
        /// </remarks>
        /// <exception cref="System.InvalidOperationException">
        /// if an attempt is made to consume the the
        /// end of the stream (i.e. if
        /// <c>LA(1)==</c>
        /// <see cref="IntStreamConstants.EOF">EOF</see>
        /// before calling
        /// <c>consume</c>
        /// ).
        /// </exception>
        void Consume();

        /// <summary>
        /// Gets the value of the symbol at offset
        /// <paramref name="i"/>
        /// from the current
        /// position. When
        /// <c>i==1</c>
        /// , this method returns the value of the current
        /// symbol in the stream (which is the next symbol to be consumed). When
        /// <c>i==-1</c>
        /// , this method returns the value of the previously read
        /// symbol in the stream. It is not valid to call this method with
        /// <c>i==0</c>
        /// , but the specific behavior is unspecified because this
        /// method is frequently called from performance-critical code.
        /// <p>This method is guaranteed to succeed if any of the following are true:</p>
        /// <ul>
        /// <li>
        /// <c>i&gt;0</c>
        /// </li>
        /// <li>
        /// <c>i==-1</c>
        /// and
        /// <see cref="Index()">index()</see>
        /// returns a value greater
        /// than the value of
        /// <c>index()</c>
        /// after the stream was constructed
        /// and
        /// <c>LA(1)</c>
        /// was called in that order. Specifying the current
        /// <c>index()</c>
        /// relative to the index after the stream was created
        /// allows for filtering implementations that do not return every symbol
        /// from the underlying source. Specifying the call to
        /// <c>LA(1)</c>
        /// allows for lazily initialized streams.</li>
        /// <li>
        /// <c>LA(i)</c>
        /// refers to a symbol consumed within a marked region
        /// that has not yet been released.</li>
        /// </ul>
        /// <p>If
        /// <paramref name="i"/>
        /// represents a position at or beyond the end of the stream,
        /// this method returns
        /// <see cref="IntStreamConstants.EOF"/>
        /// .</p>
        /// <p>The return value is unspecified if
        /// <c>i&lt;0</c>
        /// and fewer than
        /// <c>-i</c>
        /// calls to
        /// <see cref="Consume()">consume()</see>
        /// have occurred from the beginning of
        /// the stream before calling this method.</p>
        /// </summary>
        /// <exception cref="System.NotSupportedException">
        /// if the stream does not support
        /// retrieving the value of the specified symbol
        /// </exception>
        int LA(int i);

        /// <summary>
        /// A mark provides a guarantee that
        /// <see cref="Seek(int)">seek()</see>
        /// operations will be
        /// valid over a "marked range" extending from the index where
        /// <c>mark()</c>
        /// was called to the current
        /// <see cref="Index()">index()</see>
        /// . This allows the use of
        /// streaming input sources by specifying the minimum buffering requirements
        /// to support arbitrary lookahead during prediction.
        /// <p>The returned mark is an opaque handle (type
        /// <c>int</c>
        /// ) which is passed
        /// to
        /// <see cref="Release(int)">release()</see>
        /// when the guarantees provided by the marked
        /// range are no longer necessary. When calls to
        /// <c>mark()</c>
        /// /
        /// <c>release()</c>
        /// are nested, the marks must be released
        /// in reverse order of which they were obtained. Since marked regions are
        /// used during performance-critical sections of prediction, the specific
        /// behavior of invalid usage is unspecified (i.e. a mark is not released, or
        /// a mark is released twice, or marks are not released in reverse order from
        /// which they were created).</p>
        /// <p>The behavior of this method is unspecified if no call to an
        /// <see cref="IIntStream">initializing method</see>
        /// has occurred after this stream was
        /// constructed.</p>
        /// <p>This method does not change the current position in the input stream.</p>
        /// <p>The following example shows the use of
        /// <see cref="Mark()">mark()</see>
        /// ,
        /// <see cref="Release(int)">release(mark)</see>
        /// ,
        /// <see cref="Index()">index()</see>
        /// , and
        /// <see cref="Seek(int)">seek(index)</see>
        /// as part of an operation to safely work within a
        /// marked region, then restore the stream position to its original value and
        /// release the mark.</p>
        /// <pre>
        /// IntStream stream = ...;
        /// int index = -1;
        /// int mark = stream.mark();
        /// try {
        /// index = stream.index();
        /// // perform work here...
        /// } finally {
        /// if (index != -1) {
        /// stream.seek(index);
        /// }
        /// stream.release(mark);
        /// }
        /// </pre>
        /// </summary>
        /// <returns>
        /// An opaque marker which should be passed to
        /// <see cref="Release(int)">release()</see>
        /// when the marked range is no longer required.
        /// </returns>
        int Mark();

        /// <summary>
        /// This method releases a marked range created by a call to
        /// <see cref="Mark()">mark()</see>
        /// . Calls to
        /// <c>release()</c>
        /// must appear in the
        /// reverse order of the corresponding calls to
        /// <c>mark()</c>
        /// . If a mark is
        /// released twice, or if marks are not released in reverse order of the
        /// corresponding calls to
        /// <c>mark()</c>
        /// , the behavior is unspecified.
        /// <p>For more information and an example, see
        /// <see cref="Mark()"/>
        /// .</p>
        /// </summary>
        /// <param name="marker">
        /// A marker returned by a call to
        /// <c>mark()</c>
        /// .
        /// </param>
        /// <seealso cref="Mark()"/>
        void Release(int marker);

        /// <summary>
        /// Return the index into the stream of the input symbol referred to by
        /// <c>LA(1)</c>
        /// .
        /// <p>The behavior of this method is unspecified if no call to an
        /// <see cref="IIntStream">initializing method</see>
        /// has occurred after this stream was
        /// constructed.</p>
        /// </summary>
        int Index
        {
            get;
        }

        /// <summary>
        /// Set the input cursor to the position indicated by
        /// <paramref name="index"/>
        /// . If the
        /// specified index lies past the end of the stream, the operation behaves as
        /// though
        /// <paramref name="index"/>
        /// was the index of the EOF symbol. After this method
        /// returns without throwing an exception, the at least one of the following
        /// will be true.
        /// <ul>
        /// <li>
        /// <see cref="Index()">index()</see>
        /// will return the index of the first symbol
        /// appearing at or after the specified
        /// <paramref name="index"/>
        /// . Specifically,
        /// implementations which filter their sources should automatically
        /// adjust
        /// <paramref name="index"/>
        /// forward the minimum amount required for the
        /// operation to target a non-ignored symbol.</li>
        /// <li>
        /// <c>LA(1)</c>
        /// returns
        /// <see cref="IntStreamConstants.EOF"/>
        /// </li>
        /// </ul>
        /// This operation is guaranteed to not throw an exception if
        /// <paramref name="index"/>
        /// lies within a marked region. For more information on marked regions, see
        /// <see cref="Mark()"/>
        /// . The behavior of this method is unspecified if no call to
        /// an
        /// <see cref="IIntStream">initializing method</see>
        /// has occurred after this stream
        /// was constructed.
        /// </summary>
        /// <param name="index">The absolute index to seek to.</param>
        /// <exception cref="System.ArgumentException">
        /// if
        /// <paramref name="index"/>
        /// is less than 0
        /// </exception>
        /// <exception cref="System.NotSupportedException">
        /// if the stream does not support
        /// seeking to the specified index
        /// </exception>
        void Seek(int index);

        /// <summary>
        /// Returns the total number of symbols in the stream, including a single EOF
        /// symbol.
        /// </summary>
        /// <remarks>
        /// Returns the total number of symbols in the stream, including a single EOF
        /// symbol.
        /// </remarks>
        /// <exception cref="System.NotSupportedException">
        /// if the size of the stream is
        /// unknown.
        /// </exception>
        int Size
        {
            get;
        }

        /// <summary>Gets the name of the underlying symbol source.</summary>
        /// <remarks>
        /// Gets the name of the underlying symbol source. This method returns a
        /// non-null, non-empty string. If such a name is not known, this method
        /// returns
        /// <see cref="IntStreamConstants.UnknownSourceName"/>
        /// .
        /// </remarks>
        string SourceName
        {
            get;
        }
    }

    public static class IntStreamConstants
    {
        /// <summary>
        /// The value returned by
        /// <see cref="IIntStream.LA(int)">LA()</see>
        /// when the end of the stream is
        /// reached.
        /// </summary>
        public const int EOF = -1;

        /// <summary>
        /// The value returned by
        /// <see cref="IIntStream.SourceName"/>
        /// when the actual name of the
        /// underlying source is not known.
        /// </summary>
        public const string UnknownSourceName = "<unknown>";
    }
}
