package org.antlr.v4.automata;

import org.antlr.v4.tool.Grammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** A DFA walker that knows how to dump them to serialized strings. */
public class DFASerializer {
	List<DFAState> work;
	Set<DFAState> marked;
	Grammar g;
	DFAState start;

	public DFASerializer(Grammar g, DFAState start) {
		this.g = g;
		this.start = start;
	}

	public String toString() {
		if ( start==null ) return null;
		marked = new HashSet<DFAState>();

		work = new ArrayList<DFAState>();
		work.add(start);

		StringBuilder buf = new StringBuilder();
		DFAState s = null;

		while ( work.size()>0 ) {
			s = work.remove(0);
			if ( marked.contains(s) ) continue; 
			int n = s.getNumberOfTransitions();
			//System.out.println("visit "+getDFAStateString(s)+"; edges="+n);
			marked.add(s);
			for (int i=0; i<n; i++) {
				Edge t = s.transition(i);
				buf.append("-"+t.toString()+"->"+ getStateString(t.target)+'\n');
			}
		}
		return buf.toString();
	}

	String getStateString(DFAState s) {
		int n = s.stateNumber;
		String stateStr = "s"+n;
		stateStr = ":s"+n+"=>"+s.getUniquelyPredictedAlt();
		return stateStr;
	}
}
