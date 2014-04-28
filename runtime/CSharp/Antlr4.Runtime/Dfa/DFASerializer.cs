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
        internal readonly DFA dfa;

        [Nullable]
        internal readonly string[] tokenNames;

        [Nullable]
        internal readonly string[] ruleNames;

        [Nullable]
        internal readonly ATN atn;

        public DFASerializer(DFA dfa, string[] tokenNames)
            : this(dfa, tokenNames, null, null)
        {
        }

        public DFASerializer(DFA dfa, IRecognizer parser)
            : this(dfa, parser != null ? parser.TokenNames : null, parser != null ? parser.RuleNames : null, parser != null ? parser.Atn : null)
        {
        }

        public DFASerializer(DFA dfa, string[] tokenNames, string[] ruleNames, ATN atn)
        {
            this.dfa = dfa;
            this.tokenNames = tokenNames;
            this.ruleNames = ruleNames;
            this.atn = atn;
        }

        public override string ToString()
        {
            if (dfa.s0.Get() == null)
            {
                return null;
            }
            StringBuilder buf = new StringBuilder();
            if (dfa.states != null)
            {
                List<DFAState> states = new List<DFAState>(dfa.states.Values);
                states.Sort(new _IComparer_85());
                foreach (DFAState s in states)
                {
                    IEnumerable<KeyValuePair<int, DFAState>> edges = s.EdgeMap;
                    IEnumerable<KeyValuePair<int, DFAState>> contextEdges = s.ContextEdgeMap;
                    foreach (KeyValuePair<int, DFAState> entry in edges)
                    {
                        if ((entry.Value == null || entry.Value == ATNSimulator.Error) && !s.IsContextSymbol(entry.Key))
                        {
                            continue;
                        }
                        bool contextSymbol = false;
                        buf.Append(GetStateString(s)).Append("-").Append(GetEdgeLabel(entry.Key)).Append("->");
                        if (s.IsContextSymbol(entry.Key))
                        {
                            buf.Append("!");
                            contextSymbol = true;
                        }
                        DFAState t = entry.Value;
                        if (t != null && t.stateNumber != int.MaxValue)
                        {
                            buf.Append(GetStateString(t)).Append('\n');
                        }
                        else
                        {
                            if (contextSymbol)
                            {
                                buf.Append("ctx\n");
                            }
                        }
                    }
                    if (s.IsContextSensitive)
                    {
                        foreach (KeyValuePair<int, DFAState> entry_1 in contextEdges)
                        {
                            buf.Append(GetStateString(s)).Append("-").Append(GetContextLabel(entry_1.Key)).Append("->").Append(GetStateString(entry_1.Value)).Append("\n");
                        }
                    }
                }
            }
            string output = buf.ToString();
            if (output.Length == 0)
            {
                return null;
            }
            //return Utils.sortLinesInString(output);
            return output;
        }

        private sealed class _IComparer_85 : IComparer<DFAState>
        {
            public _IComparer_85()
            {
            }

            public int Compare(DFAState o1, DFAState o2)
            {
                return o1.stateNumber - o2.stateNumber;
            }
        }

        protected internal virtual string GetContextLabel(int i)
        {
            if (i == PredictionContext.EmptyFullStateKey)
            {
                return "ctx:EMPTY_FULL";
            }
            else
            {
                if (i == PredictionContext.EmptyLocalStateKey)
                {
                    return "ctx:EMPTY_LOCAL";
                }
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
            string label;
            if (i == -1)
            {
                return "EOF";
            }
            if (tokenNames != null)
            {
                label = tokenNames[i];
            }
            else
            {
                label = i.ToString();
            }
            return label;
        }

        internal virtual string GetStateString(DFAState s)
        {
            if (s == ATNSimulator.Error)
            {
                return "ERROR";
            }
            int n = s.stateNumber;
            string stateStr = "s" + n;
            if (s.isAcceptState)
            {
                if (s.predicates != null)
                {
                    stateStr = ":s" + n + "=>" + Arrays.ToString(s.predicates);
                }
                else
                {
                    stateStr = ":s" + n + "=>" + s.prediction;
                }
            }
            if (s.IsContextSensitive)
            {
                stateStr += "*";
                foreach (ATNConfig config in s.configs)
                {
                    if (config.ReachesIntoOuterContext)
                    {
                        stateStr += "*";
                        break;
                    }
                }
            }
            return stateStr;
        }
    }
}
