package org.antlr.v4.analysis;

import org.antlr.v4.automata.*;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.misc.OrderedHashSet;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.RuleAST;

import java.util.*;

// TODO: might not need anymore if NFA simulator is fast enough
public class LexerNFAToDFAConverter {
	Grammar g;

	/** DFA we are creating */
	DFA dfa;

	/** A list of DFA states we still need to process during NFA conversion */
	List<LexerState> work = new LinkedList<LexerState>();
	/** The set of rule stop NFA states we encountered during conversion.
	 *  Walk this list to find ambig stop states (split if we have preds).
	 */
	Set<LexerState> accepts = new HashSet<LexerState>();

	//int[] altToRuleIndex;

	/** Used to prevent the closure operation from looping to itself and
	 *  hence looping forever.  Sensitive to the NFA state, the alt, and
	 *  the stack context.
	 */
	Set<NFAConfig> closureBusy;

	public static boolean debug = false;

	public LexerNFAToDFAConverter(LexerGrammar g) {
		this.g = g;
		//altToRuleIndex = new int[g.getNumRules()+1]; // alts <= num rules
	}

	public DFA createDFA() { return createDFA(LexerGrammar.DEFAULT_MODE_NAME); }

	public DFA createDFA(String modeName) {
		TokensStartState startState = g.nfa.modeToStartState.get(modeName);
		dfa = new DFA(g, startState);
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

		defineLexerAcceptStates();

		closureBusy = null; // wack all that memory used during closure			

		return dfa;
	}

	// walk accept states, informing DFA.
	// get list of NFA states per each DFA accept so we can get list of
	// rules matched (sorted by NFA state num, which gives priority to
	// rules appearing first in grammar).
	// Also, track any extreme right edge actions in
	// DFA accept state (pick action of first of any ambig rules).
	void defineLexerAcceptStates() {
		int aaa = 0;
		System.out.println("accepts ="+accepts);
		for (LexerState d : accepts) {
			if ( d.edges.size()==0 ) aaa++;
			// First get NFA accept states and associated DFA alts for this DFA state
			SortedSet<Integer> nfaAcceptStates = new TreeSet<Integer>();
			SortedSet<Integer> sortedAlts = new TreeSet<Integer>();
			OrderedHashSet<Rule> predictedRules = new OrderedHashSet<Rule>();
			for (NFAConfig c : d.nfaConfigs) {
				NFAState s = c.state;
				if ( s instanceof RuleStopState && !s.rule.isFragment() ) {
					nfaAcceptStates.add(Utils.integer(s.stateNumber));
					sortedAlts.add(c.alt);
					predictedRules.add(s.rule);
				}
			}

			// Look for and count preds
			Map<Integer, SemanticContext> predsPerAlt = d.getPredicatesForAlts();
			int npreds = 0;
			for (SemanticContext ctx : predsPerAlt.values()) if ( ctx!=null ) npreds++;

			// If unambiguous, make it a DFA accept state, else resolve with preds if possible
			if ( predictedRules.size()==1 || npreds==0 ) { // unambig or no preds
				d.predictsRule = predictedRules.get(0);
				d.action = ((RuleAST)d.predictsRule.ast).getLexerAction();
				Integer minAlt = sortedAlts.first();
				dfa.defineAcceptState(minAlt, d);
			}
			if ( predictedRules.size()>1 && npreds>0 ) {
				System.out.println(d.stateNumber+" ambig upon "+ predictedRules+" but we have preds");
				// has preds; add new accept states
				d.isAcceptState = false; // this state isn't a stop state anymore
				d.resolvedWithPredicates = true;
				for (Rule r : predictedRules) {
					SemanticContext preds = predsPerAlt.get(r.index);
					LexerState predDFATarget = dfa.newLexerState();
					predDFATarget.predictsRule = r;
					for (NFAConfig c : d.getNFAConfigsForAlt(r.index)) {
						predDFATarget.addNFAConfig(c);
					}
					// new DFA state is a target of the predicate from d
					//predDFATarget.addNFAConfig(c);
					dfa.addAcceptState(r.index, predDFATarget);
					// add a transition to pred target from d
					if ( preds!=null ) {
						d.addEdge(new PredicateEdge(preds, predDFATarget));
					}
					else {
						d.addEdge(new PredicateEdge(new SemanticContext.TruePredicate(), predDFATarget));
					}
				}
			}
		}
		System.out.println("#accepts ="+accepts.size()+" and "+aaa+" with no edges");
	}

	/** */
	public LexerState computeStartState() {
		LexerState d = dfa.newLexerState();
		// add config for each alt start, then add closure for those states
		for (int alt=1; alt<=dfa.nAlts; alt++) {
			Transition t = dfa.decisionNFAStartState.transition(alt-1);
			NFAState altStart = t.target;
			//altToRuleIndex[alt] = altStart.rule.index;
			d.addNFAConfig(
				new NFAConfig(altStart, alt,
							  NFAContext.EMPTY(),
							  SemanticContext.EMPTY_SEMANTIC_CONTEXT));
		}

		closure(d, true);
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
			closure(t, true);  // add any NFA states reachable via epsilon
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

		//System.out.println("ADD "+t);
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
					//System.out.println("found edge with "+label.toString(g)+" from NFA state "+s);
					// add NFA target to (potentially) new DFA state
					labelTarget.addNFAConfig(
						new NFAConfig(c, t.target, c.semanticContext));
				}
			}
		}

		return labelTarget;
	}

	/** For all NFA states in d, compute the epsilon closure; that is, find
	 *  all NFA states reachable from the NFA states in d purely via epsilon
	 *  transitions.
	 */
	public void closure(LexerState d, boolean collectPredicates) {
		if ( debug ) {
			System.out.println("closure("+d+")");
		}

		List<NFAConfig> configs = new ArrayList<NFAConfig>();
		configs.addAll(d.nfaConfigs.elements()); // dup initial list; avoid walk/update issue
		for (NFAConfig c : configs) {
			closure(d, c.state, c.alt, c.context, c.semanticContext, collectPredicates); // update d.nfaStates
		}

		closureBusy.clear();

		if ( debug ) {
			System.out.println("after closure("+d+")");
		}
		//System.out.println("after closure d="+d);
	}

	// TODO: make pass NFAConfig like other DFA
	public void closure(LexerState d, NFAState s, int ruleIndex, NFAContext context,
						SemanticContext semanticContext, boolean collectPredicates) {
		NFAConfig proposedNFAConfig =
			new NFAConfig(s, ruleIndex, context, semanticContext);

		if ( closureBusy.contains(proposedNFAConfig) ) return;
		closureBusy.add(proposedNFAConfig);

		// s itself is always in closure
		d.nfaConfigs.add(proposedNFAConfig);

		if ( s instanceof RuleStopState ) {
			// TODO: chase FOLLOW links if recursive
			if ( !context.isEmpty() ) {
				closure(d, context.returnState, ruleIndex, context.parent, semanticContext, collectPredicates);
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
						closure(d, t.target, ruleIndex, newContext, semanticContext, collectPredicates);
					}
				}
				else if ( t instanceof ActionTransition ) {
					collectPredicates = false; // can't see past actions
					closure(d, t.target, ruleIndex, context, semanticContext, collectPredicates);
				}
				else if ( t instanceof PredicateTransition ) {
					SemanticContext labelContext = ((PredicateTransition)t).semanticContext;
					SemanticContext newSemanticContext = semanticContext;
					if ( collectPredicates ) {
						// AND the previous semantic context with new pred
						//System.out.println("&"+labelContext+" enclosingRule="+c.state.rule);
						newSemanticContext =
							SemanticContext.and(semanticContext, labelContext);
					}
					closure(d, t.target, ruleIndex, context, newSemanticContext, collectPredicates);
				}
				else if ( t.isEpsilon() ) {
					closure(d, t.target, ruleIndex, context, semanticContext, collectPredicates);
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
