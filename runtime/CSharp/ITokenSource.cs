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
    /// A source of tokens must provide a sequence of tokens via
    /// <see cref="NextToken()"/>
    /// and also must reveal it's source of characters;
    /// <see cref="CommonToken"/>
    /// 's text is
    /// computed from a
    /// <see cref="ICharStream"/>
    /// ; it only store indices into the char
    /// stream.
    /// <p>Errors from the lexer are never passed to the parser. Either you want to keep
    /// going or you do not upon token recognition error. If you do not want to
    /// continue lexing then you do not want to continue parsing. Just throw an
    /// exception not under
    /// <see cref="RecognitionException"/>
    /// and Java will naturally toss
    /// you all the way out of the recognizers. If you want to continue lexing then
    /// you should not throw an exception to the parser--it has already requested a
    /// token. Keep lexing until you get a valid one. Just report errors and keep
    /// going, looking for a valid token.</p>
    /// </summary>
    public interface ITokenSource
    {
        /// <summary>
        /// Return a
        /// <see cref="IToken"/>
        /// object from your input stream (usually a
        /// <see cref="ICharStream"/>
        /// ). Do not fail/return upon lexing error; keep chewing
        /// on the characters until you get a good one; errors are not passed through
        /// to the parser.
        /// </summary>
        [return: NotNull]
        IToken NextToken();

        /// <summary>Get the line number for the current position in the input stream.</summary>
        /// <remarks>
        /// Get the line number for the current position in the input stream. The
        /// first line in the input is line 1.
        /// </remarks>
        /// <returns>
        /// The line number for the current position in the input stream, or
        /// 0 if the current token source does not track line numbers.
        /// </returns>
        int Line
        {
            get;
        }

        /// <summary>
        /// Get the index into the current line for the current position in the input
        /// stream.
        /// </summary>
        /// <remarks>
        /// Get the index into the current line for the current position in the input
        /// stream. The first character on a line has position 0.
        /// </remarks>
        /// <returns>
        /// The line number for the current position in the input stream, or
        /// -1 if the current token source does not track character positions.
        /// </returns>
        int Column
        {
            get;
        }

        /// <summary>
        /// Get the
        /// <see cref="ICharStream"/>
        /// from which this token source is currently
        /// providing tokens.
        /// </summary>
        /// <returns>
        /// The
        /// <see cref="ICharStream"/>
        /// associated with the current position in
        /// the input, or
        /// <see langword="null"/>
        /// if no input stream is available for the token
        /// source.
        /// </returns>
        ICharStream InputStream
        {
            get;
        }

        /// <summary>Gets the name of the underlying input source.</summary>
        /// <remarks>
        /// Gets the name of the underlying input source. This method returns a
        /// non-null, non-empty string. If such a name is not known, this method
        /// returns
        /// <see cref="IntStreamConstants.UnknownSourceName"/>
        /// .
        /// </remarks>
        string SourceName
        {
            get;
        }

        /// <summary>
        /// Set the
        /// <see cref="ITokenFactory"/>
        /// this token source should use for creating
        /// <see cref="IToken"/>
        /// objects from the input.
        /// </summary>
        /// <value>
        /// The
        /// <see cref="ITokenFactory"/>
        /// to use for creating tokens.
        /// </value>
        /// <summary>
        /// Gets the
        /// <see cref="ITokenFactory"/>
        /// this token source is currently using for
        /// creating
        /// <see cref="IToken"/>
        /// objects from the input.
        /// </summary>
        /// <returns>
        /// The
        /// <see cref="ITokenFactory"/>
        /// currently used by this token source.
        /// </returns>
        ITokenFactory TokenFactory
        {
            get;
            set;
        }
    }
}
