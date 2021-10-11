/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>The default mechanism for creating tokens.</summary>
    /// <remarks>
    /// The default mechanism for creating tokens. It's used by default in Lexer and
    /// the error handling strategy (to create missing tokens).  Notifying the parser
    /// of a new factory means that it notifies it's token source and error strategy.
    /// </remarks>
    public interface ITokenFactory
    {
        /// <summary>
        /// This is the method used to create tokens in the lexer and in the
        /// error handling strategy.
        /// </summary>
        /// <remarks>
        /// This is the method used to create tokens in the lexer and in the
        /// error handling strategy. If text!=null, than the start and stop positions
        /// are wiped to -1 in the text override is set in the CommonToken.
        /// </remarks>
        [return: NotNull]
        IToken Create(Tuple<ITokenSource, ICharStream> source, int type, string text, int channel, int start, int stop, int line, int charPositionInLine);

        /// <summary>Generically useful</summary>
        [return: NotNull]
        IToken Create(int type, string text);
    }
}
