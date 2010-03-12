package org.antlr.v4.analysis;

import org.antlr.v4.automata.DFA;

import java.util.HashSet;
import java.util.Set;

/** Detect imperfect DFA:
 *
 *  1. nonreduced DFA (dangling states)
 *  2. unreachable stop states
 *  3. nondeterministic states
 */
public class DFAVerifier {
	DFA dfa;
	StackLimitedNFAToDFAConverter converter;

	public DFAVerifier(DFA dfa, StackLimitedNFAToDFAConverter converter) {
		this.dfa = dfa;
		this.converter = converter;
	}

	public void analyze() {

	}

	public Set<Integer> getUnreachableAlts() {
		return new HashSet<Integer>();
	}
}
