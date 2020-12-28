/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// An
    /// <see cref="IIntStream"/>
    /// whose symbols are
    /// <see cref="IToken"/>
    /// instances.
    /// </summary>
    public interface ITokenStream : IIntStream
    {
        /// <summary>
        /// Get the
        /// <see cref="IToken"/>
        /// instance associated with the value returned by
        /// <see cref="IIntStream.LA(int)">LA(k)</see>
        /// . This method has the same pre- and post-conditions as
        /// <see cref="IIntStream.LA(int)"/>
        /// . In addition, when the preconditions of this method
        /// are met, the return value is non-null and the value of
        /// <c>LT(k).getType()==LA(k)</c>
        /// .
        /// </summary>
        /// <seealso cref="IIntStream.LA(int)"/>
        [return: NotNull]
        IToken LT(int k);

        /// <summary>
        /// Gets the
        /// <see cref="IToken"/>
        /// at the specified
        /// <c>index</c>
        /// in the stream. When
        /// the preconditions of this method are met, the return value is non-null.
        /// <p>The preconditions for this method are the same as the preconditions of
        /// <see cref="IIntStream.Seek(int)"/>
        /// . If the behavior of
        /// <c>seek(index)</c>
        /// is
        /// unspecified for the current state and given
        /// <c>index</c>
        /// , then the
        /// behavior of this method is also unspecified.</p>
        /// <p>The symbol referred to by
        /// <c>index</c>
        /// differs from
        /// <c>seek()</c>
        /// only
        /// in the case of filtering streams where
        /// <c>index</c>
        /// lies before the end
        /// of the stream. Unlike
        /// <c>seek()</c>
        /// , this method does not adjust
        /// <c>index</c>
        /// to point to a non-ignored symbol.</p>
        /// </summary>
        /// <exception cref="System.ArgumentException">if {code index} is less than 0</exception>
        /// <exception cref="System.NotSupportedException">
        /// if the stream does not support
        /// retrieving the token at the specified index
        /// </exception>
        [return: NotNull]
        IToken Get(int i);

        /// <summary>
        /// Gets the underlying
        /// <see cref="ITokenSource"/>
        /// which provides tokens for this
        /// stream.
        /// </summary>
        ITokenSource TokenSource
        {
            get;
        }

        /// <summary>
        /// Return the text of all tokens within the specified
        /// <paramref name="interval"/>
        /// . This
        /// method behaves like the following code (including potential exceptions
        /// for violating preconditions of
        /// <see cref="Get(int)"/>
        /// , but may be optimized by the
        /// specific implementation.
        /// <pre>
        /// TokenStream stream = ...;
        /// String text = "";
        /// for (int i = interval.a; i &lt;= interval.b; i++) {
        /// text += stream.get(i).getText();
        /// }
        /// </pre>
        /// </summary>
        /// <param name="interval">
        /// The interval of tokens within this stream to get text
        /// for.
        /// </param>
        /// <returns>
        /// The text of all tokens within the specified interval in this
        /// stream.
        /// </returns>
        /// <exception cref="System.ArgumentNullException">
        /// if
        /// <paramref name="interval"/>
        /// is
        /// <see langword="null"/>
        /// </exception>
        [return: NotNull]
        string GetText(Interval interval);

        /// <summary>Return the text of all tokens in the stream.</summary>
        /// <remarks>
        /// Return the text of all tokens in the stream. This method behaves like the
        /// following code, including potential exceptions from the calls to
        /// <see cref="IIntStream.Size()"/>
        /// and
        /// <see cref="GetText(Antlr4.Runtime.Misc.Interval)"/>
        /// , but may be
        /// optimized by the specific implementation.
        /// <pre>
        /// TokenStream stream = ...;
        /// String text = stream.getText(new Interval(0, stream.size()));
        /// </pre>
        /// </remarks>
        /// <returns>The text of all tokens in the stream.</returns>
        [return: NotNull]
        string GetText();

        /// <summary>
        /// Return the text of all tokens in the source interval of the specified
        /// context.
        /// </summary>
        /// <remarks>
        /// Return the text of all tokens in the source interval of the specified
        /// context. This method behaves like the following code, including potential
        /// exceptions from the call to
        /// <see cref="GetText(Antlr4.Runtime.Misc.Interval)"/>
        /// , but may be
        /// optimized by the specific implementation.
        /// <p>If
        /// <c>ctx.getSourceInterval()</c>
        /// does not return a valid interval of
        /// tokens provided by this stream, the behavior is unspecified.</p>
        /// <pre>
        /// TokenStream stream = ...;
        /// String text = stream.getText(ctx.getSourceInterval());
        /// </pre>
        /// </remarks>
        /// <param name="ctx">
        /// The context providing the source interval of tokens to get
        /// text for.
        /// </param>
        /// <returns>
        /// The text of all tokens within the source interval of
        /// <paramref name="ctx"/>
        /// .
        /// </returns>
        [return: NotNull]
        string GetText(RuleContext ctx);

        /// <summary>
        /// Return the text of all tokens in this stream between
        /// <paramref name="start"/>
        /// and
        /// <paramref name="stop"/>
        /// (inclusive).
        /// <p>If the specified
        /// <paramref name="start"/>
        /// or
        /// <paramref name="stop"/>
        /// token was not provided by
        /// this stream, or if the
        /// <paramref name="stop"/>
        /// occurred before the
        /// <paramref name="start"/>
        /// token, the behavior is unspecified.</p>
        /// <p>For streams which ensure that the
        /// <see cref="IToken.TokenIndex()"/>
        /// method is
        /// accurate for all of its provided tokens, this method behaves like the
        /// following code. Other streams may implement this method in other ways
        /// provided the behavior is consistent with this at a high level.</p>
        /// <pre>
        /// TokenStream stream = ...;
        /// String text = "";
        /// for (int i = start.getTokenIndex(); i &lt;= stop.getTokenIndex(); i++) {
        /// text += stream.get(i).getText();
        /// }
        /// </pre>
        /// </summary>
        /// <param name="start">The first token in the interval to get text for.</param>
        /// <param name="stop">The last token in the interval to get text for (inclusive).</param>
        /// <returns>
        /// The text of all tokens lying between the specified
        /// <paramref name="start"/>
        /// and
        /// <paramref name="stop"/>
        /// tokens.
        /// </returns>
        /// <exception cref="System.NotSupportedException">
        /// if this stream does not support
        /// this method for the specified tokens
        /// </exception>
        [return: NotNull]
        string GetText(IToken start, IToken stop);
    }
}
