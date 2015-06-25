/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
