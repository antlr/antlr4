package org.antlr.v4.automata;

import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.List;

/** Lexer DFA states track just NFAStates not config with stack/alt etc... like
 *  DFA used for prediction.
 */
public class LexerState extends DFAState {
	/** For ambiguous lexer rules, the accept state matches a set of rules,
	 *  not just one. Means we can't use predictsAlt (an int).  The
	 *  order of rules is order given in grammar.  So, gives precedence to
	 *  keywords vs IDs if keywords are first.
	 */
	public List<Rule> matchesRules = new ArrayList<Rule>();

	public LexerState(DFA dfa) {
		super(dfa);
	}

//	public Set<NFAState> getUniqueNFAStates() { return nfaStates; }
//
//	public Set<Integer> getAltSet() { return null; }
//
//	/** Two LexerStates are equal if their NFA state lists are the
//	 *  same. Don't test the DFA state numbers here because
//	 *  we use to know if any other state exists that has this exact set
//	 *  of states. The DFAState state number is irrelevant.
//	 */
//	public boolean equals(Object o) {
//		// compare set of NFA configurations in this set with other
//		if ( this==o ) return true;
//		LexerState other = (LexerState)o;
//		return this.nfaStates.equals(other.nfaStates);
//	}
//
//	public int hashCode() {
//		int h = 0;
//		for (NFAState s : nfaStates) h += s.stateNumber;
//		return h;
//	}
//
//	/** Print all NFA states plus what alts they predict */
//	public String toString() {
//		StringBuffer buf = new StringBuffer();
//		buf.append(stateNumber+":{");
//		for (int i = 0; i < nfaStates.size(); i++) {
//			NFAState s = nfaStates.get(i);
//			if ( i>0 ) {
//				buf.append(", ");
//			}
//			buf.append(s);
//		}
//		buf.append("}");
//		return buf.toString();
//	}
}
