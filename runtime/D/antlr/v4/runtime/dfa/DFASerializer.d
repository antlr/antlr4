/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.dfa.DFASerializer;

import std.array;
import std.conv;
import antlr.v4.runtime.dfa.DFA;
import antlr.v4.runtime.dfa.DFAState;
import antlr.v4.runtime.Vocabulary;
import antlr.v4.runtime.VocabularyImpl;

/**
 * @uml
 * A DFA walker that knows how to dump them to serialized strings.
 */
class DFASerializer
{

    public DFA dfa;

    public Vocabulary vocabulary;

    public this(DFA dfa, string[] tokenNames)
    {
        this(dfa, VocabularyImpl.fromTokenNames(tokenNames));
    }

    public this(DFA dfa, Vocabulary vocabulary)
    {
        this.dfa = dfa;
        this.vocabulary = vocabulary;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        if (dfa.s0 is null)
            return null;
        auto buf = appender!string;
        DFAState[] states = dfa.getStates;
        foreach (DFAState s; states) {
            uint n = 0;
            if (s.edges !is null)
                n = to!uint(s.edges.length);
            for (uint i = 0; i < n; i++) {
                DFAState t = s.edges[i];
                if (t && t.stateNumber != int.max) {
                    buf.put(getStateString(s));
                    string label = getEdgeLabel(i);
                    buf.put("-");
                    buf.put(label);
                    buf.put("->");
                    buf.put(getStateString(t));
                    buf.put('\n');
                }
            }
        }

        string output = buf.data;
        if (output.length == 0) return null;
        //return Utils.sortLinesInString(output);
        return output;
    }

    public string getEdgeLabel(int i)
    {
        return vocabulary.getDisplayName(i - 1);
    }

    public string getStateString(DFAState s)
    {
        int n = s.stateNumber;
        string baseStateStr = (s.isAcceptState ? ":" : "") ~ "s" ~ to!string(n) ~
            (s.requiresFullContext ? "^" : "");
        if (s.isAcceptState) {
            if (s.predicates !is null) {
                return baseStateStr ~ "=>" ~ to!string(s.predicates);
            }
            else {
                return baseStateStr ~ "=>" ~ to!string(s.prediction);
            }
        }
        else {
            return baseStateStr;
        }

    }

}
