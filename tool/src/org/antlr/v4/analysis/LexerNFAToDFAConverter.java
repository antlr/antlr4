package org.antlr.v4.analysis;

import org.antlr.v4.automata.*;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.misc.OrderedHashSet;
import org.antlr.v4.tool.Grammar;

import java.util.*;

public class LexerNFAToDFAConverter {
	Grammar g;

	/** DFA we are creating */
	DFA dfa;

	/** A list of DFA states we still need to process during NFA conversion */
	List<LexerState> work = new LinkedList<LexerState>();
	List<LexerState> accepts = new LinkedList<LexerState>();

	/** Used to prevent the closure operation from looping to itself and
     *  hence looping forever.  Sensitive to the NFA state, the alt, and
     *  the stack context.
     */
	Set<NFAConfig> closureBusy;	

	public static boolean debug = false;	

	public LexerNFAToDFAConverter(Grammar g) {
		this.g = g;
		TokensStartState startState = (TokensStartState)g.nfa.states.get(0);
		dfa = new DFA(g, startState);
	}

	public DFA createDFA() {
		closureBusy = new HashSet<NFAConfig>();
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
			for (NFAConfig c : d.nfaConfigs) {
				NFAState s = c.state;
				if ( s instanceof RuleStopState && !s.rule.isFragment() ) {
					dfa.defineAcceptState(c.alt, d);
					d.matchesRules.add(s.rule);
				}
			}
		}

		closureBusy = null; // wack all that memory used during closure			

		return dfa;
	}

	/** */
	public LexerState computeStartState() {
		LexerState d = dfa.newLexerState();
		// add config for each alt start, then add closure for those states
		for (int ruleIndex=1; ruleIndex<=dfa.nAlts; ruleIndex++) {
			Transition t = dfa.decisionNFAStartState.transition(ruleIndex-1);
			NFAState altStart = t.target;
			d.addNFAConfig(
				new NFAConfig(altStart, ruleIndex,
							  NFAContext.EMPTY(),
							  SemanticContext.EMPTY_SEMANTIC_CONTEXT));
		}

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

		for (NFAConfig c : d.nfaConfigs) {
			NFAState s = c.state;
			int n = s.getNumberOfTransitions();
			for (int i=0; i<n; i++) {               // for each transition
				Transition t = s.transition(i);
				// found a transition with label; does it collide with label?
				if ( !t.isEpsilon() && !t.label().and(label).isNil() ) {
					// add NFA target to (potentially) new DFA state
					labelTarget.addNFAConfig(
						new NFAConfig(c, t.target, SemanticContext.EMPTY_SEMANTIC_CONTEXT));
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

		List<NFAConfig> configs = new ArrayList<NFAConfig>();
		configs.addAll(d.nfaConfigs.elements()); // dup initial list; avoid walk/update issue
		for (NFAConfig c : configs) {
			closure(d, c.state, c.alt, c.context); // update d.nfaStates
		}

		closureBusy.clear();

		if ( debug ) {
			System.out.println("after closure("+d+")");
		}
		//System.out.println("after closure d="+d);
	}

	// TODO: make pass NFAConfig like other DFA
	public void closure(LexerState d, NFAState s, int ruleIndex, NFAContext context) {
		NFAConfig proposedNFAConfig =
			new NFAConfig(s, ruleIndex, context, SemanticContext.EMPTY_SEMANTIC_CONTEXT);

		if ( closureBusy.contains(proposedNFAConfig) ) return;
		closureBusy.add(proposedNFAConfig);

		// s itself is always in closure
		d.nfaConfigs.add(proposedNFAConfig);

		if ( s instanceof RuleStopState ) {
			// TODO: chase FOLLOW links if recursive
			if ( !context.isEmpty() ) {
				closure(d, context.returnState, ruleIndex, context.parent);
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
					// simulate an r=0 recursion limited conversion by avoiding
					// any recursive call. It approximates recursive lexer
					// rules with loops.  Later we can try rule for real.
					if ( !context.contains(((RuleTransition)t).followState) ) {
						NFAContext newContext =
							new NFAContext(context, ((RuleTransition)t).followState);
						closure(d, t.target, ruleIndex, newContext);
					}
				}
				else if ( t.isEpsilon() ) {
					closure(d, t.target, ruleIndex, context);
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
