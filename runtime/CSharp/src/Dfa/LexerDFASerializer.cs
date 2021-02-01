/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime.Dfa
{
    public class LexerDFASerializer : DFASerializer
    {
        public LexerDFASerializer(DFA dfa)
            : base(dfa, Vocabulary.EmptyVocabulary)
        {
        }

        [return: NotNull]
        protected internal override string GetEdgeLabel(int i)
        {
            return "'" + (char)i + "'";
        }
    }
}
