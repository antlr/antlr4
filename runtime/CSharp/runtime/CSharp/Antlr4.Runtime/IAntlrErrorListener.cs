/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>How to emit recognition errors.</summary>
    /// <remarks>How to emit recognition errors.</remarks>
#if COMPACT
    public interface IAntlrErrorListener<TSymbol>
#else
    public interface IAntlrErrorListener<in TSymbol>
#endif
    {
        /// <summary>Upon syntax error, notify any interested parties.</summary>
        /// <remarks>
        /// Upon syntax error, notify any interested parties. This is not how to
        /// recover from errors or compute error messages.
        /// <see cref="IAntlrErrorStrategy"/>
        /// specifies how to recover from syntax errors and how to compute error
        /// messages. This listener's job is simply to emit a computed message,
        /// though it has enough information to create its own message in many cases.
        /// <p>The
        /// <see cref="RecognitionException"/>
        /// is non-null for all syntax errors except
        /// when we discover mismatched token errors that we can recover from
        /// in-line, without returning from the surrounding rule (via the single
        /// token insertion and deletion mechanism).</p>
        /// </remarks>
        /// <param name="recognizer">
        /// What parser got the error. From this
        /// object, you can access the context as well
        /// as the input stream.
        /// </param>
        /// <param name="offendingSymbol">
        /// The offending token in the input token
        /// stream, unless recognizer is a lexer (then it's null). If
        /// no viable alternative error,
        /// <paramref name="e"/>
        /// has token at which we
        /// started production for the decision.
        /// </param>
        /// <param name="line">The line number in the input where the error occurred.</param>
        /// <param name="charPositionInLine">The character position within that line where the error occurred.</param>
        /// <param name="msg">The message to emit.</param>
        /// <param name="e">
        /// The exception generated by the parser that led to
        /// the reporting of an error. It is null in the case where
        /// the parser was able to recover in line without exiting the
        /// surrounding rule.
        /// </param>
        void SyntaxError(IRecognizer recognizer, TSymbol offendingSymbol, int line, int charPositionInLine, string msg, RecognitionException e);
    }
}
