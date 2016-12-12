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
using System.Text;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Dfa
{
    /// <summary>A DFA walker that knows how to dump them to serialized strings.</summary>
    /// <remarks>A DFA walker that knows how to dump them to serialized strings.</remarks>
    public class DFASerializer
    {
        [NotNull]
        private readonly DFA dfa;

        [NotNull]
        private readonly IVocabulary vocabulary;

        [Nullable]
        internal readonly string[] ruleNames;

        [Nullable]
        internal readonly ATN atn;

        public DFASerializer(DFA dfa, IVocabulary vocabulary)
            : this(dfa, vocabulary, null, null)
        {
        }

        public DFASerializer(DFA dfa, IRecognizer parser)
            : this(dfa, parser != null ? parser.Vocabulary : Vocabulary.EmptyVocabulary, parser != null ? parser.RuleNames : null, parser != null ? parser.Atn : null)
        {
        }

        public DFASerializer(DFA dfa, IVocabulary vocabulary, string[] ruleNames, ATN atn)
        {
            this.dfa = dfa;
            this.vocabulary = vocabulary;
            this.ruleNames = ruleNames;
            this.atn = atn;
        }

        public override string ToString()
        {
            if (dfa.s0 == null)
            {
                return null;
            }
            StringBuilder buf = new StringBuilder();
            if (dfa.states != null)
            {
                List<DFAState> states = new List<DFAState>(dfa.states.Values);
				states.Sort((x,y)=>x.stateNumber - y.stateNumber);
                foreach (DFAState s in states)
                {
					int n = s.edges != null ? s.edges.Length : 0;
					for (int i = 0; i < n; i++)
					{
						DFAState t = s.edges[i];
						if (t != null && t.stateNumber != int.MaxValue)
						{
							buf.Append(GetStateString(s));
							String label = GetEdgeLabel(i);
							buf.Append("-");
							buf.Append(label);
							buf.Append("->");
							buf.Append(GetStateString(t));
							buf.Append('\n');
						}
					}
	            }
            }
            string output = buf.ToString();
            if (output.Length == 0)
            {
                return null;
            }
            return output;
        }



        protected internal virtual string GetContextLabel(int i)
        {
			if (i == PredictionContext.EMPTY_RETURN_STATE)
            {
                return "ctx:EMPTY";
            }
            if (atn != null && i > 0 && i <= atn.states.Count)
            {
                ATNState state = atn.states[i];
                int ruleIndex = state.ruleIndex;
                if (ruleNames != null && ruleIndex >= 0 && ruleIndex < ruleNames.Length)
                {
                    return "ctx:" + i.ToString() + "(" + ruleNames[ruleIndex] + ")";
                }
            }
            return "ctx:" + i.ToString();
        }

        protected internal virtual string GetEdgeLabel(int i)
        {
            return vocabulary.GetDisplayName(i - 1);
        }

        internal virtual string GetStateString(DFAState s)
        {
			if (s == ATNSimulator.ERROR)
            {
                return "ERROR";
            }

			int n = s.stateNumber;
			string baseStateStr = (s.isAcceptState ? ":" : "") + "s" + n + (s.requiresFullContext ? "^" : "");
			if ( s.isAcceptState ) {
				if ( s.predicates!=null ) {
					return baseStateStr + "=>" + Arrays.ToString(s.predicates);
				}
				else {
					return baseStateStr + "=>" + s.prediction;
				}
			}
			else {
				return baseStateStr;
			}
        }
    }
}
