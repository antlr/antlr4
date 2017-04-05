/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#if !PORTABLE

using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;
using System.IO;

namespace Antlr4.Runtime
{
    /// <author>Sam Harwell</author>
    public class ConsoleErrorListener<Symbol> : IAntlrErrorListener<Symbol>
    {
        /// <summary>
        /// Provides a default instance of
        /// <see cref="ConsoleErrorListener{Symbol}"/>
        /// .
        /// </summary>
        public static readonly ConsoleErrorListener<Symbol> Instance = new ConsoleErrorListener<Symbol>();

        /// <summary>
        /// <inheritDoc/>
        /// <p>
        /// This implementation prints messages to
        /// <see cref="System.Console.Error"/>
        /// containing the
        /// values of
        /// <paramref name="line"/>
        /// ,
        /// <paramref name="charPositionInLine"/>
        /// , and
        /// <paramref name="msg"/>
        /// using
        /// the following format.</p>
        /// <pre>
        /// line <em>line</em>:<em>charPositionInLine</em> <em>msg</em>
        /// </pre>
        /// </summary>
        public virtual void SyntaxError(TextWriter output, IRecognizer recognizer, Symbol offendingSymbol, int line, int charPositionInLine, string msg, RecognitionException e)
        {
            output.WriteLine("line " + line + ":" + charPositionInLine + " " + msg);
        }
    }
}

#endif
