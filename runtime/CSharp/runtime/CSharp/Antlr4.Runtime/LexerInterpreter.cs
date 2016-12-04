/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Linq;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    public class LexerInterpreter : Lexer
    {
		private readonly string grammarFileName;

		private readonly ATN atn;

		private readonly string[] ruleNames;

		private readonly string[] modeNames;

        [NotNull]
        private readonly IVocabulary vocabulary;

        public LexerInterpreter(string grammarFileName, IVocabulary vocabulary, IEnumerable<string> ruleNames, IEnumerable<string> modeNames, ATN atn, ICharStream input)
            : base(input)
        {
            if (atn.grammarType != ATNType.Lexer)
            {
                throw new ArgumentException("The ATN must be a lexer ATN.");
            }
            this.grammarFileName = grammarFileName;
            this.atn = atn;
            this.ruleNames = ruleNames.ToArray();
            this.modeNames = modeNames.ToArray();
            this.vocabulary = vocabulary;
            this.Interpreter = new LexerATNSimulator(this, atn);
        }

        public override ATN Atn
        {
            get
            {
                return atn;
            }
        }

        public override string GrammarFileName
        {
            get
            {
                return grammarFileName;
            }
        }

        public override string[] RuleNames
        {
            get
            {
                return ruleNames;
            }
        }

        public override string[] ModeNames
        {
            get
            {
                return modeNames;
            }
        }

        public override IVocabulary Vocabulary
        {
            get
            {
                return vocabulary;
            }
        }
    }
}
