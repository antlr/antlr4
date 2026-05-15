/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime
{
    /// <summary>
    /// Token factory that creates <see cref="OptimizedToken"/> instances with
    /// deferred text materialization and ValueTuple-based source storage.
    /// <para>
    /// Drop-in replacement for <see cref="CommonTokenFactory"/>. To use:
    /// <code>lexer.TokenFactory = OptimizedTokenFactory.Default;</code>
    /// </para>
    /// </summary>
    public class OptimizedTokenFactory : ITokenFactory
    {
        /// <summary>
        /// Default instance — does NOT eagerly copy text (deferred materialization).
        /// </summary>
        public static readonly ITokenFactory Default = new OptimizedTokenFactory();

        /// <summary>
        /// When true, text is eagerly copied at token creation time.
        /// Needed for <see cref="UnbufferedCharStream"/> where the input
        /// window may slide away before text is accessed.
        /// </summary>
        private readonly bool _copyText;

        public OptimizedTokenFactory() : this(false) { }

        public OptimizedTokenFactory(bool copyText)
        {
            _copyText = copyText;
        }

        public virtual IToken Create(
            Tuple<ITokenSource, ICharStream> source,
            int type, string text, int channel,
            int start, int stop, int line, int charPositionInLine)
        {
            var t = new OptimizedToken(
                (source.Item1, source.Item2),
                type, channel, start, stop);
            t.Line = line;
            t.Column = charPositionInLine;
            if (text != null)
            {
                t.Text = text;
            }
            else if (_copyText && source.Item2 != null)
            {
                t.Text = source.Item2.GetText(Interval.Of(start, stop));
            }
            // else: text stays null — will be lazily materialized on first .Text access
            return t;
        }

        public virtual IToken Create(int type, string text)
        {
            return new OptimizedToken(type, text);
        }
    }
}
