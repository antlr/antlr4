/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// A token has properties: text, type, line, character position in the line
    /// (so we can ignore tabs), token channel, index, and source from which
    /// we obtained this token.
    /// </summary>
    /// <remarks>
    /// A token has properties: text, type, line, character position in the line
    /// (so we can ignore tabs), token channel, index, and source from which
    /// we obtained this token.
    /// </remarks>
    public interface IToken
    {
        /// <summary>Get the text of the token.</summary>
        /// <remarks>Get the text of the token.</remarks>
        string Text
        {
            get;
        }

        /// <summary>Get the token type of the token.</summary>
        /// <remarks>Get the token type of the token.</remarks>
        int Type
        {
            get;
        }

        /// <summary>
        /// The line number on which the 1st character of this token was matched,
        /// line=1..n
        /// </summary>
        int Line
        {
            get;
        }

        /// <summary>
        /// The index of the first character of this token relative to the
        /// beginning of the line at which it occurs, 0..n-1
        /// </summary>
        int Column
        {
            get;
        }

        /// <summary>Return the channel this token.</summary>
        /// <remarks>
        /// Return the channel this token. Each token can arrive at the parser
        /// on a different channel, but the parser only "tunes" to a single channel.
        /// The parser ignores everything not on DEFAULT_CHANNEL.
        /// </remarks>
        int Channel
        {
            get;
        }

        /// <summary>An index from 0..n-1 of the token object in the input stream.</summary>
        /// <remarks>
        /// An index from 0..n-1 of the token object in the input stream.
        /// This must be valid in order to print token streams and
        /// use TokenRewriteStream.
        /// Return -1 to indicate that this token was conjured up since
        /// it doesn't have a valid index.
        /// </remarks>
        int TokenIndex
        {
            get;
        }

        /// <summary>
        /// The starting character index of the token
        /// This method is optional; return -1 if not implemented.
        /// </summary>
        /// <remarks>
        /// The starting character index of the token
        /// This method is optional; return -1 if not implemented.
        /// </remarks>
        int StartIndex
        {
            get;
        }

        /// <summary>The last character index of the token.</summary>
        /// <remarks>
        /// The last character index of the token.
        /// This method is optional; return -1 if not implemented.
        /// </remarks>
        int StopIndex
        {
            get;
        }

        /// <summary>
        /// Gets the
        /// <see cref="ITokenSource"/>
        /// which created this token.
        /// </summary>
        ITokenSource TokenSource
        {
            get;
        }

        /// <summary>
        /// Gets the
        /// <see cref="ICharStream"/>
        /// from which this token was derived.
        /// </summary>
        ICharStream InputStream
        {
            get;
        }
    }

    public static class TokenConstants
    {
        public const int InvalidType = 0;

        /// <summary>
        /// During lookahead operations, this "token" signifies we hit rule end ATN state
        /// and did not follow it despite needing to.
        /// </summary>
        /// <remarks>
        /// During lookahead operations, this "token" signifies we hit rule end ATN state
        /// and did not follow it despite needing to.
        /// </remarks>
        public const int EPSILON = -2;

        public const int MinUserTokenType = 1;

        public const int EOF = IntStreamConstants.EOF;

        /// <summary>
        /// All tokens go to the parser (unless skip() is called in that rule)
        /// on a particular "channel".
        /// </summary>
        /// <remarks>
        /// All tokens go to the parser (unless skip() is called in that rule)
        /// on a particular "channel".  The parser tunes to a particular channel
        /// so that whitespace etc... can go to the parser on a "hidden" channel.
        /// </remarks>
        public const int DefaultChannel = 0;

        /// <summary>
        /// Anything on different channel than DEFAULT_CHANNEL is not parsed
        /// by parser.
        /// </summary>
        /// <remarks>
        /// Anything on different channel than DEFAULT_CHANNEL is not parsed
        /// by parser.
        /// </remarks>
        public const int HiddenChannel = 1;

        /// <summary>
        /// This is the minimum constant value which can be assigned to a
        /// user-defined token channel.
        /// </summary>
        /// <remarks>
        /// This is the minimum constant value which can be assigned to a
        /// user-defined token channel.
        /// <p>
        /// The non-negative numbers less than
        /// <see cref="MinUserChannelValue"/>
        /// are
        /// assigned to the predefined channels
        /// <see cref="DefaultChannel"/>
        /// and
        /// <see cref="HiddenChannel"/>
        /// .</p>
        /// </remarks>
        /// <seealso cref="IToken.Channel"/>
        public const int MinUserChannelValue = 2;
    }
}
