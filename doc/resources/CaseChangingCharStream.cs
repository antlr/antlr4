/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime
{
    /// <summary>
    /// This class supports case-insensitive lexing by wrapping an existing
    /// <see cref="ICharStream"/> and forcing the lexer to see either upper or
    /// lowercase characters. Grammar literals should then be either upper or
    /// lower case such as 'BEGIN' or 'begin'. The text of the character
    /// stream is unaffected. Example: input 'BeGiN' would match lexer rule
    /// 'BEGIN' if constructor parameter upper=true but getText() would return
    /// 'BeGiN'.
    /// </summary>
    public class CaseChangingCharStream : ICharStream
    {
        private ICharStream stream;
        private bool upper;

        /// <summary>
        /// Constructs a new CaseChangingCharStream wrapping the given <paramref name="stream"/> forcing
        /// all characters to upper case or lower case.
        /// </summary>
        /// <param name="stream">The stream to wrap.</param>
        /// <param name="upper">If true force each symbol to upper case, otherwise force to lower.</param>
        public CaseChangingCharStream(ICharStream stream, bool upper)
        {
            this.stream = stream;
            this.upper = upper;
        }

        public int Index
        {
            get
            {
                return stream.Index;
            }
        }

        public int Size
        {
            get
            {
                return stream.Size;
            }
        }

        public string SourceName
        {
            get
            {
                return stream.SourceName;
            }
        }

        public void Consume()
        {
            stream.Consume();
        }

        [return: NotNull]
        public string GetText(Interval interval)
        {
            return stream.GetText(interval);
        }

        public int LA(int i)
        {
            int c = stream.LA(i);

            if (c <= 0)
            {
                return c;
            }

            char o = (char)c;

            if (upper)
            {
                return (int)char.ToUpperInvariant(o);
            }

            return (int)char.ToLowerInvariant(o);
        }

        public int Mark()
        {
            return stream.Mark();
        }

        public void Release(int marker)
        {
            stream.Release(marker);
        }

        public void Seek(int index)
        {
            stream.Seek(index);
        }
    }
}
