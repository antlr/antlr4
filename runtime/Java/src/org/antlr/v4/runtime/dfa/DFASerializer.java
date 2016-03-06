/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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
