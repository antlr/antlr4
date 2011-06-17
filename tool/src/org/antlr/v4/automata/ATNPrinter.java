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

	public String toString() {
		if ( start==null ) return null;
		marked = new HashSet<ATNState>();

		work = new ArrayList<ATNState>();
		work.add(start);

		StringBuilder buf = new StringBuilder();
		ATNState s = null;

		while ( work.size()>0 ) {
			s = work.remove(0);
			if ( marked.contains(s) ) continue;
			int n = s.getNumberOfTransitions();
			//System.out.println("visit "+getATNStateString(s)+"; edges="+n);
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
					buf.append("->"+ getStateString(t.target)+'\n');
				}
				else if ( t instanceof ActionTransition ) {
					ActionTransition a = (ActionTransition)t;
					buf.append("-"+a.actionAST.getText()+"->"+ getStateString(t.target)+'\n');
				}
				else if ( t instanceof AtomTransition ) {
					AtomTransition a = (AtomTransition)t;
					buf.append("-"+a.toString(g)+"->"+ getStateString(t.target)+'\n');
				}
				else {
					buf.append("-"+t.toString(g)+"->"+ getStateString(t.target)+'\n');
				}
			}
		}
		return buf.toString();
	}

	String getStateString(ATNState s) {
		if ( s==null ) {
			System.out.println("s==null");
		}
		int n = s.stateNumber;
		String stateStr = "s"+n;
		if ( s instanceof StarBlockStartState ) stateStr = "StarBlockStart_"+n;
		else if ( s instanceof PlusBlockStartState ) stateStr = "PlusBlockStart_"+n;
		else if ( s instanceof StarBlockStartState ) stateStr = "StarBlockStart_"+n;
		else if ( s instanceof BlockStartState) stateStr = "BlockStart_"+n;
		else if ( s instanceof BlockEndState ) stateStr = "BlockEnd_"+n;
		else if ( s instanceof RuleStartState) stateStr = "RuleStart_"+s.rule.name+"_"+n;
		else if ( s instanceof RuleStopState ) stateStr = "RuleStop_"+s.rule.name+"_"+n;
		else if ( s instanceof PlusLoopbackState) stateStr = "PlusLoopBack_"+n;
		else if ( s instanceof StarLoopbackState) stateStr = "StarLoopBack_"+n;
		return stateStr;
	}
}
