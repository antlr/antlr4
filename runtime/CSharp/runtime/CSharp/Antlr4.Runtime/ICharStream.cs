/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
