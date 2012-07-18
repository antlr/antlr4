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

package org.antlr.v4.automata;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.tool.Grammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** An ATN walker that knows how to dump them to serialized strings. */
public class ATNPrinter {
	List<ATNState> work;
	Set<ATNState> marked;
	Grammar g;
	ATNState start;

	public ATNPrinter(Grammar g, ATNState start) {
		this.g = g;
		this.start = start;
	}

	public String asString() {
		if ( start==null ) return null;
		marked = new HashSet<ATNState>();

		work = new ArrayList<ATNState>();
		work.add(start);

		StringBuilder buf = new StringBuilder();
		ATNState s = null;

		while ( !work.isEmpty() ) {
			s = work.remove(0);
			if ( marked.contains(s) ) continue;
			int n = s.getNumberOfTransitions();
//			System.out.println("visit "+s+"; edges="+n);
			marked.add(s);
			for (int i=0; i<n; i++) {
				Transition t = s.transition(i);
				if ( !(s instanceof RuleStopState) ) { // don't add follow states to work
					if ( t instanceof RuleTransition ) work.add(((RuleTransition)t).followState);
					else work.add( t.target );
				}
				buf.append(getStateString(s));
				if ( t instanceof EpsilonTransition ) {
					buf.append("->"+ getStateString(t.target)+'\n');
				}
				else if ( t instanceof RuleTransition ) {
					buf.append("-"+g.getRule(((RuleTransition)t).ruleIndex).name+"->"+ getStateString(t.target)+'\n');
				}
				else if ( t instanceof ActionTransition ) {
					ActionTransition a = (ActionTransition)t;
					buf.append("-"+a.toString()+"->"+ getStateString(t.target)+'\n');
				}
				else if ( t instanceof SetTransition ) {
					SetTransition st = (SetTransition)t;
					boolean not = st instanceof NotSetTransition;
					if ( g.isLexer() ) {
						buf.append("-"+(not?"~":"")+st.toString()+"->"+ getStateString(t.target)+'\n');
					}
					else {
						buf.append("-"+(not?"~":"")+st.label().toString(g.getTokenNames())+"->"+ getStateString(t.target)+'\n');
					}
				}
				else if ( t instanceof AtomTransition ) {
					AtomTransition a = (AtomTransition)t;
					String label = a.toString();
					if ( g!=null ) label = g.getTokenDisplayName(a.label);
					buf.append("-"+label+"->"+ getStateString(t.target)+'\n');
				}
				else {
					buf.append("-"+t.toString()+"->"+ getStateString(t.target)+'\n');
				}
			}
		}
		return buf.toString();
	}

	String getStateString(ATNState s) {
		int n = s.stateNumber;
		String stateStr = "s"+n;
		if ( s instanceof StarBlockStartState ) stateStr = "StarBlockStart_"+n;
		else if ( s instanceof PlusBlockStartState ) stateStr = "PlusBlockStart_"+n;
		else if ( s instanceof BlockStartState) stateStr = "BlockStart_"+n;
		else if ( s instanceof BlockEndState ) stateStr = "BlockEnd_"+n;
		else if ( s instanceof RuleStartState) stateStr = "RuleStart_"+g.getRule(s.ruleIndex).name+"_"+n;
		else if ( s instanceof RuleStopState ) stateStr = "RuleStop_"+g.getRule(s.ruleIndex).name+"_"+n;
		else if ( s instanceof PlusLoopbackState) stateStr = "PlusLoopBack_"+n;
		else if ( s instanceof StarLoopbackState) stateStr = "StarLoopBack_"+n;
		else if ( s instanceof StarLoopEntryState) stateStr = "StarLoopEntry_"+n;
		return stateStr;
	}
}
