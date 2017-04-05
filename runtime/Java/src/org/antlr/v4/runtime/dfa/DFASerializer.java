/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.dfa;

import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;

import java.util.Arrays;
import java.util.List;

/** A DFA walker that knows how to dump them to serialized strings. */
public class DFASerializer {

	private final DFA dfa;

	private final Vocabulary vocabulary;

	/**
	 * @deprecated Use {@link #DFASerializer(DFA, Vocabulary)} instead.
	 */
	@Deprecated
	public DFASerializer(DFA dfa, String[] tokenNames) {
		this(dfa, VocabularyImpl.fromTokenNames(tokenNames));
	}

	public DFASerializer(DFA dfa, Vocabulary vocabulary) {
		this.dfa = dfa;
		this.vocabulary = vocabulary;
	}

	@Override
	public String toString() {
		if ( dfa.s0==null ) return null;
		StringBuilder buf = new StringBuilder();
		List<DFAState> states = dfa.getStates();
		for (DFAState s : states) {
			int n = 0;
			if ( s.edges!=null ) n = s.edges.length;
			for (int i=0; i<n; i++) {
				DFAState t = s.edges[i];
				if ( t!=null && t.stateNumber != Integer.MAX_VALUE ) {
					buf.append(getStateString(s));
					String label = getEdgeLabel(i);
					buf.append("-").append(label).append("->").append(getStateString(t)).append('\n');
				}
			}
		}

		String output = buf.toString();
		if ( output.length()==0 ) return null;
		//return Utils.sortLinesInString(output);
		return output;
	}

	protected String getEdgeLabel(int i) {
		return vocabulary.getDisplayName(i - 1);
	}


	protected String getStateString(DFAState s) {
		int n = s.stateNumber;
		final String baseStateStr = (s.isAcceptState ? ":" : "") + "s" + n + (s.requiresFullContext ? "^" : "");
		if ( s.isAcceptState ) {
            if ( s.predicates!=null ) {
                return baseStateStr + "=>" + Arrays.toString(s.predicates);
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
