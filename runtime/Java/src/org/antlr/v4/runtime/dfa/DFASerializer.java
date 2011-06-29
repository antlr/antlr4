/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
