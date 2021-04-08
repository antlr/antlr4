/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>A source of characters for an ANTLR lexer.</summary>
    /// <remarks>A source of characters for an ANTLR lexer.</remarks>
    public interface ICharStream : IIntStream
    {
        /// <summary>
        /// This method returns the text for a range of characters within this input
        /// stream.
        /// </summary>
        /// <remarks>
        /// This method returns the text for a range of characters within this input
        /// stream. This method is guaranteed to not throw an exception if the
        /// specified
        /// <paramref name="interval"/>
        /// lies entirely within a marked range. For more
        /// information about marked ranges, see
        /// <see cref="IIntStream.Mark()"/>
        /// .
        /// </remarks>
        /// <param name="interval">an interval within the stream</param>
        /// <returns>the text of the specified interval</returns>
        /// <exception cref="System.ArgumentNullException">
        /// if
        /// <paramref name="interval"/>
        /// is
        /// <see langword="null"/>
        /// </exception>
        /// <exception cref="System.ArgumentException">
        /// if
        /// <c>interval.a &lt; 0</c>
        /// , or if
        /// <c>interval.b &lt; interval.a - 1</c>
        /// , or if
        /// <c>interval.b</c>
        /// lies at or
        /// past the end of the stream
        /// </exception>
        /// <exception cref="System.NotSupportedException">
        /// if the stream does not support
        /// getting the text of the specified interval
        /// </exception>
        [return: NotNull]
        string GetText(Interval interval);
    }
}
