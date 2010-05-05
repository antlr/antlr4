package org.antlr.v4.analysis;

import org.antlr.v4.automata.*;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.misc.OrderedHashSet;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;

import java.util.*;

/** Code that embodies the NFA conversion to DFA. A new object is needed
 *  per DFA (also required for thread safety if multiple conversions
 *  launched).
 */
public class PredictionDFAFactory {
	Grammar g;

	DecisionState nfaStartState;

	/** DFA we are creating */
	DFA dfa;

	/** A list of DFA states we still need to process during NFA conversion */
	List<DFAState> work = new LinkedList<DFAState>();

	/** Each alt in an NFA derived from a grammar must have a DFA state that
     *  predicts it lest the parser not know what to do.  Nondeterminisms can
     *  lead to this situation (assuming no semantic predicates can resolve
     *  the problem) and when for some reason, I cannot compute the lookahead
     *  (which might arise from an error in the algorithm or from
     *  left-recursion etc...).
     */
    public Set<Integer> unreachableAlts;

	/** Track all DFA states with ambiguous configurations.
	 *  By reaching the same DFA state, a path through the NFA for some input
	 *  is able to reach the same NFA state by starting at more than one
	 *  alternative's left edge. If the context is the same or conflicts,
	 *  then we have ambiguity. If the context is different, it's simply
	 *  nondeterministic and we should keep looking for edges that will
	 *  render it deterministic. If we run out of things to add to the DFA,
	 *  we'll get a dangling state; it's non-LL(*). Later we may find that predicates
	 *  resolve the issue, but track ambiguous states anyway.
	 */
	public Set<DFAState> ambiguousStates = new HashSet<DFAState>();

	/** The set of states w/o emanating edges (and w/o resolving sem preds). */
	public Set<DFAState> danglingStates = new HashSet<DFAState>();

	/** Was a syntactic ambiguity resolved with predicates?  Any DFA
	 *  state that predicts more than one alternative, must be resolved
	 *  with predicates or it should be reported to the user.
	 */
	public Set<DFAState> resolvedWithSemanticPredicates = new HashSet<DFAState>();

	/** Tracks alts insufficiently covered.
	 *  For example, p1||true gets reduced to true and so leaves
	 *  whole alt uncovered.  This maps alt num to the set of (Token)
	 *  locations in grammar of uncovered elements.
	 */
	public Map<DFAState, List<Integer>> statesWithIncompletelyCoveredAlts = new HashMap<DFAState, List<Integer>>();

	public boolean hasPredicateBlockedByAction = false;

	/** Recursion is limited to a particular depth. Which state tripped it? */
	public DFAState recursionOverflowState;

	/** Which state found multiple recursive alts? */
	public DFAState abortedDueToMultipleRecursiveAltsAt;

	/** Are there any loops in this DFA? */
//	public boolean cyclic = false;

	/** Used to prevent the closure operation from looping to itself and
     *  hence looping forever.  Sensitive to the NFA state, the alt, and
     *  the stack context.
     */
	Set<NFAConfig> closureBusy;

	Resolver resolver;

	public static boolean debug = false;

	public PredictionDFAFactory(Grammar g, DecisionState nfaStartState) {
		this.g = g;
		this.nfaStartState = nfaStartState;
		dfa = new DFA(g, nfaStartState);
		dfa.converter = this;
		resolver = new Resolver(this);
	}

	public DFA createDFA() {
		closureBusy = new HashSet<NFAConfig>();
		computeStartState();
		dfa.addState(dfa.startState); // make sure dfa knows about this state
		work.add(dfa.startState);

		// while more DFA states to check, process them
		while ( work.size()>0 ) {
			DFAState d = work.get(0);
			reach(d);
			resolver.resolveDeadState(d);
			work.remove(0); // we're done with this DFA state
		}

		unreachableAlts = getUnreachableAlts();

		closureBusy = null; // wack all that memory used during closure		

		return dfa;
	}

	/** From this node, add a d--a-->t transition for all
	 *  labels 'a' where t is a DFA node created
	 *  from the set of NFA states reachable from any NFA
	 *  configuration in DFA state d.
	 */
	void reach(DFAState d) {
		OrderedHashSet<IntervalSet> labels = DFA.getReachableLabels(d);

		for (IntervalSet label : labels) {
			DFAState t = reach(d, label);
			if ( debug ) {
				System.out.println("DFA state after reach -" +
								   label.toString(g)+"->"+t);
			}
			// nothing was reached by label; we must have resolved
			// all NFA configs in d, when added to work, that point at label
			if ( t==null ) continue;
//			if ( t.getUniqueAlt()==NFA.INVALID_ALT_NUMBER ) {
//				// Only compute closure if a unique alt number is not known.
//				// If a unique alternative is mentioned among all NFA
//				// configurations then there is no possibility of needing to look
//				// beyond this state; also no possibility of a nondeterminism.
//				// This optimization May 22, 2006 just dropped -Xint time
//				// for analysis of Java grammar from 11.5s to 2s!  Wow.
//				closure(t);  // add any NFA states reachable via epsilon
//			}

			try {
				closure(t);  // add any NFA states reachable via epsilon
			}
//			catch (RecursionOverflowSignal ros) {
//				recursionOverflowState = d;
//				ErrorManager.recursionOverflow(g.fileName, d, ros.state, ros.altNum, ros.depth);
//			}
//			catch (MultipleRecursiveAltsSignal mras) {
//				abortedDueToMultipleRecursiveAltsAt = d;
//				ErrorManager.multipleRecursiveAlts(g.fileName, d, mras.recursiveAltSet);
//			}
			catch (AnalysisTimeoutSignal at) {// TODO: nobody throws yet
				g.tool.errMgr.analysisTimeout();
			}

			addTransition(d, label, t); // make d-label->t transition
		}

		// Add semantic predicate transitions if we resolved when added to work list
		if ( d.resolvedWithPredicates ) addPredicateTransitions(d);
	}

	/** Add t if not in DFA yet, resolving nondet's and then make d-label->t */
	void addTransition(DFAState d, IntervalSet label, DFAState t) {
		DFAState existing = dfa.stateSet.get(t);
		if ( existing != null ) { // seen before; point at old one
			d.addEdge(new Edge(existing, label));
			return;
		}

		// resolve any syntactic conflicts by choosing a single alt or
		// by using semantic predicates if present.
		resolver.resolveAmbiguities(t);

		// If deterministic, don't add this state to work list; it's an accept state
		// Just return as a valid DFA state
		int alt = t.getUniquelyPredictedAlt();
		if ( alt > 0 ) { // uniquely predicts an alt?
			//System.out.println(t+" predicts "+alt);
			// Define new stop state
			dfa.addAcceptState(alt, t);
		}
		else {
			// System.out.println("ADD "+t);
			work.add(t); // unresolved, add to work list to continue NFA conversion
			dfa.addState(t);  // add state we've never seen before
		}

		d.addEdge(new Edge(t, label));
	}

	/** Given the set of NFA states in DFA state d, find all NFA states
	 *  reachable traversing label arcs.  By definition, there can be
	 *  only one DFA state reachable by a single label from DFA state d so we must
	 *  find and merge all NFA states reachable via label.  Return a new
	 *  DFAState that has all of those NFA states with their context.
	 *
	 *  Because we cannot jump to another rule nor fall off the end of a rule
	 *  via a non-epsilon transition, NFA states reachable from d have the
	 *  same configuration as the NFA state in d.  So if NFA state 7 in d's
	 *  configurations can reach NFA state 13 then 13 will be added to the
	 *  new DFAState (labelDFATarget) with the same configuration as state
	 *  7 had.
	 */
	public DFAState reach(DFAState d, IntervalSet label) {
		System.out.println("reach "+label.toString(g)+" from "+d.stateNumber);
		DFAState labelTarget = null;

		for (NFAConfig c : d.nfaConfigs) {
			int n = c.state.getNumberOfTransitions();
//			int nEp = 0;
			for (int i=0; i<n; i++) {               // for each transition
				Transition t = c.state.transition(i);

//				if ( t.isEpsilon() ) nEp++;

				// when we added this state as target of some other state,
				// we tried to resolve any conflicts.  Ignore anything we
				// were able to fix previously
				if ( c.resolved || c.resolvedWithPredicate ) continue;
				// found a transition with label; does it collide with label?
				// [Note: we still must test for isEpsilon here since
				//  computeStartState has to add these.  Non-start-state
				//  closure ops will not add NFA states with only epsilon
				//  transitions, however.]
				if ( !t.isEpsilon() && !t.label().and(label).isNil() ) {
					// add NFA target to (potentially) new DFA state
					if ( labelTarget==null ) labelTarget = dfa.newState();
					labelTarget.addNFAConfig(new NFAConfig(c, t.target));
				}
			}

//			System.out.println("config "+c+" has "+nEp+'/'+n+" eps edges");
//			if ( nEp>0 && nEp!=n ) {
//				System.out.println("MISMATCH");
//			}
		}

		// [if we couldn't find any non-resolved edges to add, return nothing]
		
		return labelTarget;
	}

	/** From this first NFA state of a decision, create a DFA.
	 *  Walk each alt in decision and compute closure from the start of that
	 *  rule, making sure that the closure does not include other alts within
	 *  that same decision.  The idea is to associate a specific alt number
	 *  with the starting closure so we can trace the alt number for all states
	 *  derived from this.  At a stop state in the DFA, we can return this alt
	 *  number, indicating which alt is predicted.
	 */
	public void computeStartState() {
		DFAState d = dfa.newState();
		dfa.startState = d;

		// add config for each alt start, then add closure for those states
		for (int altNum=1; altNum<=dfa.nAlts; altNum++) {
			Transition t = nfaStartState.transition(altNum-1);
			NFAState altStart = t.target;
			d.addNFAConfig(
				new NFAConfig(altStart, altNum,
							  NFAContext.EMPTY(),
							  SemanticContext.EMPTY_SEMANTIC_CONTEXT));
		}

		closure(d);
	}

	/** For all NFA states (configurations) merged in d,
	 *  compute the epsilon closure; that is, find all NFA states reachable
	 *  from the NFA states in d via purely epsilon transitions.
	 */
	public void closure(DFAState d) {
		if ( debug ) {
			System.out.println("closure("+d+")");
		}

		// Only the start state initiates pred collection; gets turned
		// off maybe by actions later hence we need a parameter to carry
		// it forward
		boolean collectPredicates = (d == dfa.startState);

		// TODO: can we avoid this separate list by directly filling d.nfaConfigs?
		// OH: concurrent modification. dup initialconfigs? works for lexers, try here to save configs param
		List<NFAConfig> configs = new ArrayList<NFAConfig>();
		configs.addAll(d.nfaConfigs);
		for (NFAConfig c : configs) {
			closure(d, c, collectPredicates);
		}

		closureBusy.clear();

		if ( debug ) {
			System.out.println("after closure("+d+")");
		}
		//System.out.println("after closure d="+d);
	}

	/** Where can we get from NFA state s traversing only epsilon transitions?
	 *
	 *  A closure operation should abort if that computation has already
	 *  been done or a computation with a conflicting context has already
	 *  been done.  If proposed NFA config's state and alt are the same
	 *  there is potentially a problem.  If the stack context is identical
	 *  then clearly the exact same computation is proposed.  If a context
	 *  is a suffix of the other, then again the computation is in an
	 *  identical context.  beta $ and beta alpha $ are considered the same stack.
	 *  We could walk configurations linearly doing suuch a comparison instead
	 *  of a set lookup for exact matches but it's much slower because you can't
	 *  do a Set lookup.  I use exact match as ANTLR always detect the conflict
	 *  later when checking for ambiguous configs (it tests context suffixes).
	 *
	 *  TODO: change comment once I figure out if we can ignore suffixes in favor of empty/non test only
	 *  4/11/2010 I removed suffix check from getAmbigAlts and it broke; seems I need it.
	 *
	 *  Side-effect warnings:
	 *
	 *  Rather than pass in a list of configs to update or return and
	 *  collect lots of little config lists, it's more efficient to
	 *  modify d's config list directly.
	 *
	 *  Rather than pass closureBusy everywhere, I use a field of this object.
	 */
	public void closure(DFAState d, NFAConfig c, boolean collectPredicates) {
		if ( closureBusy.contains(c) ) return; // don't re-attempt same closure(c)
		closureBusy.add(c);

		// Theory says p is always in closure; in practice, though, we
		// we want to reduce the number of NFA configurations in the closure.
		// The purpose of the closure operation is to find all NFA states
		// reachable from a particular state traversing only epsilon
 		// transitions. Later, during the reach operation, we're going to
 		// find all NFA states reachable from those states given a particular
 		// label (token).  The fewer the NFA states we have to walk during
		// reach the better.  Since reach only cares about states with non-epsilon
		// transitions, let's only add those states to the closure. Saves memory
		// and time.  When I run TestDFAConstruction, printing out the
		// NFA configs as I test them in reach(), it reduces output from
		// 1436 lines to 74. seriously. like wow.
		//
		// 5/5/2010: This optimization only occurred to me after I implemented
		// the NFA bytecode VM. It had to ignore all SPLIT, JMP states
		// during reach. I realized that we could simply avoid adding these
 		// closures instead of ignoring them later.  I retrofitted to parser
		// DFA construction.
		//
		if ( !c.state.onlyHasEpsilonTransitions() )	{
			d.nfaConfigs.add(c);
		}

		if ( c.state instanceof RuleStopState ) {
			ruleStopStateClosure(d, c, collectPredicates);
		}
		else {
			commonClosure(d, c, collectPredicates);
		}
	}

	// if we have context info and we're at rule stop state, do
	// local follow for invokingRule and global follow for other links
	void ruleStopStateClosure(DFAState d, NFAConfig c, boolean collectPredicates) {
		if ( !c.context.recursed ) {
			//System.out.println("dynamic FOLLOW of "+c.state+" context="+c.context);
			if ( c.context.isEmpty() ) {
				commonClosure(d, c, collectPredicates); // do global FOLLOW
			}
			else {
				NFAContext newContext = c.context.parent; // "pop" invoking state
				closure(d, new NFAConfig(c, c.context.returnState, newContext),
						collectPredicates);
			}
			return;
		}

		Rule invokingRule = null;

		if ( !c.context.isEmpty() ) {
			// if stack not empty, get invoking rule from top of stack
			invokingRule = c.context.returnState.rule;
		}

		//System.out.println("FOLLOW of "+c.state+" context="+c.context);
		// follow all static FOLLOW links
		int n = c.state.getNumberOfTransitions();
		for (int i=0; i<n; i++) {
			Transition t = c.state.transition(i);
			if ( !(t instanceof EpsilonTransition) ) continue; // ignore EOF transitions
			// Chase global FOLLOW links if they don't point at invoking rule
			// else follow link to context state only
			if ( t.target.rule != invokingRule ) {
				//System.out.println("OFF TO "+t.target);
				closure(d, new NFAConfig(c, t.target), collectPredicates);
			}
			else { // t.target is in invoking rule; only follow context's link
				if ( t.target == c.context.returnState ) {
					//System.out.println("OFF TO CALL SITE "+t.target);
					// go only to specific call site; pop context
					NFAContext newContext = c.context.parent; // "pop" invoking state
					closure(d, new NFAConfig(c, t.target, newContext),
							collectPredicates);
				}
			}
		}
		return;
	}


	void commonClosure(DFAState d, NFAConfig c, boolean collectPredicates) {
		int n = c.state.getNumberOfTransitions();
		for (int i=0; i<n; i++) {
			Transition t = c.state.transition(i);
			if ( t instanceof RuleTransition) {
				NFAState retState = ((RuleTransition)t).followState;
				NFAContext newContext = c.context;
				if ( c.state.rule != t.target.rule &&
					 !c.context.contains(((RuleTransition)t).followState) ) { // !recursive?
					// first create a new context and push onto call tree,
					// recording the fact that we are invoking a rule and
					// from which state.
					//System.out.println("nonrecursive invoke of "+t.target+" ret to "+retState+" ctx="+c.context);
					newContext = new NFAContext(c.context, retState);
				}
				else {
					//System.out.println("# recursive invoke of "+t.target+" ret to "+retState+" ctx="+c.context);
					// don't record recursion, but record we did so we know
					// what to do at end of rule.
					c.context.recursed = true;
				}
				// traverse epsilon edge to new rule
				closure(d, new NFAConfig(c, t.target, newContext),
						collectPredicates);
			}
			else if ( t instanceof ActionTransition ) {
				collectPredicates = false; // can't see past actions
				closure(d, new NFAConfig(c, t.target), collectPredicates);
			}
			else if ( t instanceof PredicateTransition ) {
                SemanticContext labelContext = ((PredicateTransition)t).semanticContext;
                SemanticContext newSemanticContext = c.semanticContext;
                if ( collectPredicates ) {
                    // AND the previous semantic context with new pred
//                    int walkAlt =
//						dfa.decisionNFAStartState.translateDisplayAltToWalkAlt(alt);
					NFAState altLeftEdge = dfa.decisionNFAStartState.transition(c.alt-1).target;
					/*
					System.out.println("state "+p.stateNumber+" alt "+alt+" walkAlt "+walkAlt+" trans to "+transition0.target);
					System.out.println("DFA start state "+dfa.decisionNFAStartState.stateNumber);
					System.out.println("alt left edge "+altLeftEdge.stateNumber+
						", epsilon target "+
						altLeftEdge.transition(0).target.stateNumber);
					*/
					// do not hoist syn preds from other rules; only get if in
					// starting state's rule (i.e., context is empty)
					if ( !labelContext.isSyntacticPredicate() || c.state==altLeftEdge ) {
						//System.out.println("&"+labelContext+" enclosingRule="+c.state.rule);
						newSemanticContext =
							SemanticContext.and(c.semanticContext, labelContext);
					}
				}
				else {
					// if we're not collecting, means we saw an action previously. that blocks this pred
					hasPredicateBlockedByAction = true;
				}
				closure(d, new NFAConfig(c, t.target, newSemanticContext),
						collectPredicates);
			}

			else if ( t.isEpsilon() ) {
				closure(d, new NFAConfig(c, t.target), collectPredicates);
			}
		}
	}

	/** for each NFA config in d, look for "predicate required" sign we set
	 *  during nondeterminism resolution.
	 *
	 *  Add the predicate edges sorted by the alternative number; I'm fairly
	 *  sure that I could walk the configs backwards so they are added to
	 *  the predDFATarget in the right order, but it's best to make sure.
	 *  Predicates succeed in the order they are specifed.  Alt i wins
	 *  over alt i+1 if both predicates are true.
	 */
	protected void addPredicateTransitions(DFAState d) {
		List<NFAConfig> configsWithPreds = new ArrayList<NFAConfig>();
		// get a list of all configs with predicates
		for (NFAConfig c : d.nfaConfigs) {
			if ( c.resolvedWithPredicate) {
				configsWithPreds.add(c);
			}
		}
		// Sort ascending according to alt; alt i has higher precedence than i+1
		Collections.sort(configsWithPreds,
			 new Comparator<NFAConfig>() {
				 public int compare(NFAConfig a, NFAConfig b) {
					 if ( a.alt < b.alt ) return -1;
					 else if ( a.alt > b.alt ) return 1;
					 return 0;
				 }
			 });
		List<NFAConfig> predConfigsSortedByAlt = configsWithPreds;
		// Now, we can add edges emanating from d for these preds in right order
		for (NFAConfig c : predConfigsSortedByAlt) {
			DFAState predDFATarget = dfa.newState();
			// new DFA state is a target of the predicate from d
			predDFATarget.addNFAConfig(c);
			dfa.addAcceptState(c.alt, predDFATarget);
			// add a transition to pred target from d
			d.addEdge(new PredicateEdge(c.semanticContext, predDFATarget));
		}
	}

	public Set<Integer> getUnreachableAlts() {
		Set<Integer> unreachable = new HashSet<Integer>();
		for (int alt=1; alt<=dfa.nAlts; alt++) {
			if ( dfa.altToAcceptStates[alt]==null ) unreachable.add(alt);
		}
		return unreachable;
	}

	public void issueAmbiguityWarnings() { resolver.issueAmbiguityWarnings(); }
}
