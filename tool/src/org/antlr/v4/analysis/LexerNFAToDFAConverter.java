package org.antlr.v4.analysis;

import org.antlr.v4.automata.*;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.misc.OrderedHashSet;
import org.antlr.v4.tool.Grammar;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LexerNFAToDFAConverter {
	Grammar g;

	/** DFA we are creating */
	DFA dfa;

	/** A list of DFA states we still need to process during NFA conversion */
	List<LexerState> work = new LinkedList<LexerState>();
	List<LexerState> accepts = new LinkedList<LexerState>();

	public static boolean debug = false;	

	public LexerNFAToDFAConverter(Grammar g) {
		this.g = g;
		TokensStartState startState = (TokensStartState)g.nfa.states.get(0);
		dfa = new DFA(g, startState);
	}

	public DFA createDFA() {
		LexerState start = computeStartState();
		dfa.startState = start;
		dfa.addState(start); // make sure dfa knows about this state
		work.add((LexerState)dfa.startState);

		// while more DFA states to check, process them
		while ( work.size()>0 ) {
			LexerState d = work.get(0);
			reach(d);
			work.remove(0); // we're done with this DFA state
		}

		// walk accept states, informing DFA
		for (LexerState d : accepts) {
			for (NFAState s : d.nfaStates) {
				if ( s instanceof RuleStopState && !s.rule.isFragment() ) {
					dfa.defineAcceptState(s.rule.index, d);
					d.matchesRules.add(s.rule);
				}
			}
		}

		return dfa;
	}

	/** */
	public LexerState computeStartState() {
		LexerState d = dfa.newLexerState();
		d.nfaStates.add(dfa.decisionNFAStartState);		
		closure(d);
		return d;
	}
	
	/** From this node, add a d--a-->t transition for all
	 *  labels 'a' where t is a DFA node created
	 *  from the set of NFA states reachable from any NFA
	 *  configuration in DFA state d.
	 */
	void reach(LexerState d) {
		OrderedHashSet<IntervalSet> labels = DFA.getReachableLabels(d);

		for (IntervalSet label : labels) {
			LexerState t = reach(d, label);
			if ( debug ) {
				System.out.println("DFA state after reach -" +
								   label.toString(g)+"->"+t);
			}
			closure(t);  // add any NFA states reachable via epsilon
			addTransition(d, label, t); // make d-label->t transition
		}
	}

	/** Add t if not in DFA yet and then make d-label->t */
	void addTransition(LexerState d, IntervalSet label, LexerState t) {
		LexerState existing = (LexerState)dfa.stateSet.get(t);
		if ( existing != null ) { // seen before; point at old one
			d.addEdge(new Edge(existing, label));
			return;
		}

		System.out.println("ADD "+t);
		work.add(t); 		// add to work list to continue NFA conversion
		dfa.addState(t); 	// add state we've never seen before
		if ( t.isAcceptState ) accepts.add(t);

		d.addEdge(new Edge(t, label));
	}

	/** Given the set of NFA states in DFA state d, find all NFA states
	 *  reachable traversing label arcs.  By definition, there can be
	 *  only one DFA state reachable by a single label from DFA state d so we must
	 *  find and merge all NFA states reachable via label.  Return a new
	 *  LexerState that has all of those NFA states.
	 */
	public LexerState reach(LexerState d, IntervalSet label) {
		//System.out.println("reach "+label.toString(g)+" from "+d.stateNumber);
		LexerState labelTarget = dfa.newLexerState();

		for (NFAState s : d.nfaStates) {
			int n = s.getNumberOfTransitions();
			for (int i=0; i<n; i++) {               // for each transition
				Transition t = s.transition(i);
				// found a transition with label; does it collide with label?
				if ( !t.isEpsilon() && !t.label().and(label).isNil() ) {
					// add NFA target to (potentially) new DFA state
					labelTarget.nfaStates.add(t.target);
				}
			}
		}

		return labelTarget;
	}
	
	/** For all NFA states in d, compute the epsilon closure; that is, find
	 *  all NFA states reachable from the NFA states in d purely via epsilon
	 *  transitions.
	 */
	public void closure(LexerState d) {
		if ( debug ) {
			System.out.println("closure("+d+")");
		}

		List<NFAState> states = new ArrayList<NFAState>();
		states.addAll(d.nfaStates.elements()); // dup initial list; avoid walk/update issue
		for (NFAState s : states) closure(d, s, NFAContext.EMPTY); // update d.nfaStates

		if ( debug ) {
			System.out.println("after closure("+d+")");
		}
		//System.out.println("after closure d="+d);
	}

	public void closure(LexerState d, NFAState s, NFAContext context) {
		// s itself is always in closure
		d.nfaStates.add(s);

		if ( s instanceof RuleStopState ) {
			// TODO: chase FOLLOW links if recursive
			if ( context!=NFAContext.EMPTY ) {
				if ( !d.nfaStates.contains(context.returnState) ) {
					closure(d, context.returnState, context.parent);
				}
				// do nothing if context not empty and already added to nfaStates
			}
			else {
				d.isAcceptState = true;
			}
		}
		else {
			int n = s.getNumberOfTransitions();
			for (int i=0; i<n; i++) {
				Transition t = s.transition(i);
				if ( t instanceof RuleTransition ) {
					NFAContext newContext =
						new NFAContext(context, ((RuleTransition)t).followState);
					if ( !d.nfaStates.contains(t.target) ) closure(d, t.target, newContext);
				}
				else if ( t.isEpsilon() && !d.nfaStates.contains(t.target) ) {
					closure(d, t.target, context);
				}
			}
		}
	}

//	void ruleStopStateClosure(LexerState d, NFAState s) {
//		//System.out.println("FOLLOW of "+s+" context="+context);
//		// follow all static FOLLOW links
//		int n = s.getNumberOfTransitions();
//		for (int i=0; i<n; i++) {
//			Transition t = s.transition(i);
//			if ( !(t instanceof EpsilonTransition) ) continue; // ignore EOF transitions
//			if ( !d.nfaStates.contains(t.target) ) closure(d, t.target);
//		}
//		return;
//	}
}
