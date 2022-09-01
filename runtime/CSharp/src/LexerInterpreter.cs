/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Linq;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime
{
    public class LexerInterpreter: Lexer
    {
        private readonly string grammarFileName;

        private readonly ATN atn;

        private readonly string[] ruleNames;

        private readonly string[] channelNames;

        private readonly string[] modeNames;

        [NotNull]
        private readonly IVocabulary vocabulary;

        protected DFA[] decisionToDFA;
        protected PredictionContextCache sharedContextCache = new PredictionContextCache();

        [Obsolete("Use constructor with channelNames argument")]
        public LexerInterpreter(string grammarFileName, IVocabulary vocabulary, IEnumerable<string> ruleNames, IEnumerable<string> modeNames, ATN atn, ICharStream input)
            : this(grammarFileName, vocabulary, ruleNames, EmptyArray<String>.Value, modeNames, atn, input)
        {
        }

        public LexerInterpreter(string grammarFileName, IVocabulary vocabulary, IEnumerable<string> ruleNames, IEnumerable<string> channelNames, IEnumerable<string> modeNames, ATN atn, ICharStream input)
            : base(input)
        {
            if (atn.grammarType != ATNType.Lexer)
            {
                throw new ArgumentException("The ATN must be a lexer ATN.");
            }
            this.grammarFileName = grammarFileName;
            this.atn = atn;
            this.ruleNames = ruleNames.ToArray();
            this.channelNames = channelNames.ToArray();
            this.modeNames = modeNames.ToArray();
            this.vocabulary = vocabulary;
            decisionToDFA = new DFA[atn.NumberOfDecisions];
            for (int i = 0; i < decisionToDFA.Length; i++)
            {
                decisionToDFA[i] = new DFA(atn.GetDecisionState(i), i);
            }
            Interpreter = new LexerATNSimulator(this, atn, decisionToDFA, sharedContextCache);
        }

        public override ATN Atn => atn;

        public override string GrammarFileName => grammarFileName;

        public override string[] RuleNames => ruleNames;

        public override string[] ChannelNames => channelNames;

        public override string[] ModeNames => modeNames;

        public override IVocabulary Vocabulary => vocabulary;
    }
}
