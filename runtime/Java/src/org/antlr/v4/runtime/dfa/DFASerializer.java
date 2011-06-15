package org.antlr.v4.runtime.dfa;

/** A DFA walker that knows how to dump them to serialized strings. */
public class DFASerializer {
	String[] tokenNames;
	DFA dfa;

	public DFASerializer(DFA dfa, String[] tokenNames) {
		this.dfa = dfa;
		this.tokenNames = tokenNames;
	}

	public String toString() {
		if ( dfa.s0==null ) return null;
		StringBuilder buf = new StringBuilder();
		for (DFAState s : dfa.states.values()) {
			int n = 0;
			if ( s.edges!=null ) n = s.edges.length;
			for (int i=0; i<n; i++) {
				DFAState t = s.edges[i];
				if ( t!=null && t.stateNumber != Integer.MAX_VALUE ) {
					buf.append(getStateString(s));
					String label = getEdgeLabel(i);
					buf.append("-"+label+"->"+ getStateString(t)+'\n');
				}
			}
		}
		String output = buf.toString();
		//return Utils.sortLinesInString(output);
		return output;
	}

	protected String getEdgeLabel(int i) {
		String label;
		if ( i==0 ) return "EOF";
		if ( tokenNames!=null ) label = tokenNames[i-1];
		else label = String.valueOf(i-1);
		return label;
	}

	String getStateString(DFAState s) {
		int n = s.stateNumber;
		String stateStr = "s"+n;
		if ( s.isAcceptState ) {
			stateStr = ":s"+n+"=>"+s.prediction;
		}
		if ( s.isCtxSensitive ) {
			stateStr = ":s"+n+"@"+s.ctxToPrediction;
		}
		return stateStr;
	}
}
