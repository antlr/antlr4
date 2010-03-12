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
public class StackLimitedNFAToDFAConverter {
	public static final NFAContext NFA_EMPTY_STACK_CONTEXT = new NFAContext(null, null);

	Grammar g;

	DecisionState nfaStartState;

	/** DFA we are creating */
	DFA dfa;

	/** Stack depth max; same as Bermudez's m */
	int m = 1;

	/** A list of DFA states we still need to process during NFA conversion */
	List<DFAState> work = new LinkedList<DFAState>();

	/** Each alt in an NFA derived from a grammar must have a DFA state that
     *  predicts it lest the parser not know what to do.  Nondeterminisms can
     *  lead to this situation (assuming no semantic predicates can resolve
     *  the problem) and when for some reason, I cannot compute the lookahead
     *  (which might arise from an error in the algorithm or from
     *  left-recursion etc...).  This list starts out with all alts contained
     *  and then in method doesStateReachAcceptState() I remove the alts I
     *  know to be uniquely predicted.
     */
    public List<Integer> unreachableAlts;

	/** Track all DFA states with nondeterministic alternatives.
	 *  By reaching the same DFA state, a path through the NFA for some input
	 *  is able to reach the same NFA state by starting at more than one
	 *  alternative's left edge.  Though, later, we may find that predicates
	 *  resolve the issue, but track info anyway.
	 *  Note that from the DFA state, you can ask for
	 *  which alts are nondeterministic.
	 */
	public Set<DFAState> nondeterministicStates = new HashSet<DFAState>();

	/** The set of states w/o emanating edges (and w/o resolving sem preds). */
	public Set<DFAState> danglingStates = new HashSet<DFAState>();

	/** Was a syntactic ambiguity resolved with predicates?  Any DFA
	 *  state that predicts more than one alternative, must be resolved
	 *  with predicates or it should be reported to the user.
	 */
	Set<DFAState> resolvedWithSemanticPredicates = new HashSet<DFAState>();

	/** Tracks alts insufficiently covered.
	 *  For example, p1||true gets reduced to true and so leaves
	 *  whole alt uncovered.  This maps DFA state to the set of alts
	 */
	Set<DFAState> incompletelyCoveredStates = new HashSet<DFAState>();

	Set<DFAState> recursionOverflowStates = new HashSet<DFAState>();
	
	/** Used to prevent the closure operation from looping to itself and
     *  hence looping forever.  Sensitive to the NFA state, the alt, and
     *  the stack context.
     */
	Set<NFAConfig> closureBusy;

	Resolver resolver;

	public static boolean debug = false;

	public StackLimitedNFAToDFAConverter(Grammar g, DecisionState nfaStartState) {
		this.g = g;
		this.nfaStartState = nfaStartState;
		dfa = new DFA(g, nfaStartState);
		dfa.converter = this;
		resolver = new Resolver(this);
		unreachableAlts = new ArrayList<Integer>();
		for (int i = 1; i <= dfa.nAlts; i++) {
			unreachableAlts.add(i);
		}		
	}

	public DFA createDFA() {
		computeStartState();
		dfa.addState(dfa.startState); // make sure dfa knows about this state
		work.add(dfa.startState);

		// while more DFA states to check, process them
		while ( work.size()>0 ) {
			DFAState d = work.get(0);
			reach(d);
			resolver.resolveDanglingState(d);
			work.remove(0); // we're done with this DFA state
		}

		return dfa;
	}

	/** From this node, add a d--a-->t transition for all
	 *  labels 'a' where t is a DFA node created
	 *  from the set of NFA states reachable from any NFA
	 *  configuration in DFA state d.
	 */
	void reach(DFAState d) {
		OrderedHashSet<IntervalSet> labels = getReachableLabels(d);

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

			closure(t);  // add any NFA states reachable via epsilon

			addTransition(d, label, t); // make d-label->t transition
		}

		// Add semantic predicate transitions if we resolved when added to work list
		if ( d.resolvedWithPredicates ) addPredicateTransitions(d);
	}

	/** Add t if not in DFA yet, resolving nondet's and then make d-label->t */
	void addTransition(DFAState d, IntervalSet label, DFAState t) {
		DFAState existing = dfa.uniqueStates.get(t);
		if ( existing != null ) { // seen before; point at old one
			d.addTransition(new Edge(existing, label));
			return;
		}

		dfa.addState(t);  // add state we've never seen before

		// resolve any syntactic conflicts by choosing a single alt or
		// by using semantic predicates if present.
		resolver.resolveNonDeterminisms(t);

		// If deterministic, don't add this state; it's an accept state
		// Just return as a valid DFA state
		int alt = t.getUniquelyPredictedAlt();
		if ( alt > 0 ) { // uniquely predicts an alt?
			System.out.println(t+" predicts "+alt);
			t.isAcceptState = true;
		}
		else {
			System.out.println("ADD "+t);
			work.add(t); // unresolved, add to work list to continue NFA conversion
		}

		d.addTransition(new Edge(t, label));
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
		//System.out.println("reach "+label.toString(g)+" from "+d.stateNumber);
		DFAState labelTarget = dfa.newState();

		for (NFAConfig c : d.nfaConfigs) {
			int n = c.state.getNumberOfTransitions();
			for (int i=0; i<n; i++) {               // for each transition
				Transition t = c.state.transition(i);
				// when we added this state as target of some other state,
				// we tried to resolve any conflicts.  Ignore anything we
				// were able to fix previously
				if ( c.resolved || c.resolvedWithPredicate) continue;
				// found a transition with label; does it collide with label?
				if ( !t.isEpsilon() && !t.label().and(label).isNil() ) {
					// add NFA target to (potentially) new DFA state
					labelTarget.addNFAConfig(t.target, c.alt, c.context, c.semanticContext);
				}
			}
		}

		// if we couldn't find any non-resolved edges to add, return nothing
		if ( labelTarget.nfaConfigs.size()==0 ) return null;
		
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
			d.addNFAConfig(altStart, altNum,
						   NFA_EMPTY_STACK_CONTEXT,
						   SemanticContext.EMPTY_SEMANTIC_CONTEXT);
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
		
		closureBusy = new HashSet<NFAConfig>();
		
		List<NFAConfig> configs = new ArrayList<NFAConfig>();
		for (NFAConfig c : d.nfaConfigs) {
			closure(c.state, c.alt, c.context, c.semanticContext, collectPredicates, configs);
		}
		d.nfaConfigs.addAll(configs); // Add new NFA configs to DFA state d

		closureBusy = null; // wack all that memory used during closure

		if ( debug ) {
			System.out.println("after closure("+d+")");
		}
		// System.out.println("after closure d="+d);
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
	 *  We could walk configurations linearly doing the comparison instead
	 *  of a set for exact matches but it's much slower because you can't
	 *  do a Set lookup.  I use exact match as ANTLR
	 *  always detect the conflict later when checking for context suffixes...
	 *  I check for left-recursive stuff and terminate before analysis to
	 *  avoid need to do this more expensive computation.
	 *
	 *  TODO: remove altNum if we don't reorder for loopback nodes
	 */
	public void closure(NFAState s, int altNum, NFAContext context,
						SemanticContext semanticContext,
						boolean collectPredicates,
						List<NFAConfig> configs)
	{
		NFAConfig proposedNFAConfig = new NFAConfig(s, altNum, context, semanticContext);

		if ( closureBusy.contains(proposedNFAConfig) ) return;
		closureBusy.add(proposedNFAConfig);

		// p itself is always in closure
		configs.add(proposedNFAConfig);

		if ( s instanceof RuleStopState ) {
			ruleStopStateClosure(s, altNum, context, semanticContext, collectPredicates, configs);
		}
		else {
			commonClosure(s, altNum, context, semanticContext, collectPredicates, configs);
		}
	}

	// if we have context info and we're at rule stop state, do
	// local follow for invokingRule and global follow for other links	
	void ruleStopStateClosure(NFAState s, int altNum, NFAContext context,
							  SemanticContext semanticContext,
							  boolean collectPredicates,
							  List<NFAConfig> configs)
	{
		Rule invokingRule = null;

		if ( context!=NFA_EMPTY_STACK_CONTEXT ) {
			// if stack not empty, get invoking rule from top of stack
			invokingRule = context.returnState.rule;
		}

		//System.out.println("FOLLOW of "+s+" context="+context);
		// follow all static FOLLOW links
		int n = s.getNumberOfTransitions();
		for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( !(t instanceof EpsilonTransition) ) continue; // ignore EOF transitions
			// Chase global FOLLOW links if they don't point at invoking rule
			// else follow link to context state only
			if ( t.target.rule != invokingRule ) {
				//System.out.println("OFF TO "+t.target);
				closure(t.target, altNum, context, semanticContext, collectPredicates, configs);
			}
			else { // t.target is in invoking rule; only follow context's link
				if ( t.target == context.returnState ) {
					//System.out.println("OFF TO CALL SITE "+t.target);
					// go only to specific call site; pop context
					NFAContext newContext = context.parent; // "pop" invoking state
					closure(t.target, altNum, newContext, semanticContext, collectPredicates, configs);
				}
			}
		}
		return;
	}

	void commonClosure(NFAState s, int altNum, NFAContext context,
						SemanticContext semanticContext, boolean collectPredicates,
						List<NFAConfig> configs)
	{
		int n = s.getNumberOfTransitions();
		for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t instanceof RuleTransition) {
				NFAContext newContext = context;	// assume old context
				NFAState retState = ((RuleTransition)t).followState;
				if ( context.depth() < m ) { // track first call return state only
					newContext = new NFAContext(context, retState);
				}
				closure(t.target, altNum, newContext, semanticContext, collectPredicates, configs);
			}
			else if ( t instanceof ActionTransition ) {
				continue;
			}
			else if ( t instanceof PredicateTransition ) {
                SemanticContext labelContext = ((PredicateTransition)t).semanticContext;
                SemanticContext newSemanticContext = semanticContext;
                if ( collectPredicates ) {
                    // AND the previous semantic context with new pred
//                    int walkAlt =
//						dfa.decisionNFAStartState.translateDisplayAltToWalkAlt(alt);
					NFAState altLeftEdge = dfa.decisionNFAStartState.transition(altNum).target;
					/*
					System.out.println("state "+p.stateNumber+" alt "+alt+" walkAlt "+walkAlt+" trans to "+transition0.target);
					System.out.println("DFA start state "+dfa.decisionNFAStartState.stateNumber);
					System.out.println("alt left edge "+altLeftEdge.stateNumber+
						", epsilon target "+
						altLeftEdge.transition(0).target.stateNumber);
					*/
					// do not hoist syn preds from other rules; only get if in
					// starting state's rule (i.e., context is empty)
					if ( !labelContext.isSyntacticPredicate() || s==altLeftEdge ) {
						System.out.println("&"+labelContext+" enclosingRule="+s.rule);
						newSemanticContext =
							SemanticContext.and(semanticContext, labelContext);
					}
				}
				closure(t.target, altNum, context, newSemanticContext, collectPredicates, configs);
			}

			else if ( t.isEpsilon() ) {
				closure(t.target, altNum, context, semanticContext, collectPredicates, configs);
			}
		}
	}
	
	public OrderedHashSet<IntervalSet> getReachableLabels(DFAState d) {
		OrderedHashSet<IntervalSet> reachableLabels = new OrderedHashSet<IntervalSet>();
		for (NFAState s : d.getUniqueNFAStates()) { // for each state
			int n = s.getNumberOfTransitions();
			for (int i=0; i<n; i++) {               // for each transition
				Transition t = s.transition(i);
				IntervalSet label = null;
				if ( t instanceof AtomTransition ) {
					label = IntervalSet.of(((AtomTransition)t).label);
				}
				else if ( t instanceof SetTransition ) {
					label = ((SetTransition)t).label;
				}
				if ( label!=null ) {
					addReachableLabel(reachableLabels, label);
				}
			}
		}
		//System.out.println("reachable labels for "+d+"="+reachableLabels);
		return reachableLabels;
	}

	/** Add label uniquely and disjointly; intersection with
     *  another set or int/char forces breaking up the set(s).
     *
     *  Example, if reachable list of labels is [a..z, {k,9}, 0..9],
     *  the disjoint list will be [{a..j,l..z}, k, 9, 0..8].
     *
     *  As we add NFA configurations to a DFA state, we might as well track
     *  the set of all possible transition labels to make the DFA conversion
     *  more efficient.  W/o the reachable labels, we'd need to check the
     *  whole vocabulary space (could be 0..\uFFFE)!  The problem is that
     *  labels can be sets, which may overlap with int labels or other sets.
     *  As we need a deterministic set of transitions from any
     *  state in the DFA, we must make the reachable labels set disjoint.
     *  This operation amounts to finding the character classes for this
     *  DFA state whereas with tools like flex, that need to generate a
     *  homogeneous DFA, must compute char classes across all states.
     *  We are going to generate DFAs with heterogeneous states so we
     *  only care that the set of transitions out of a single state is
     *  unique. :)
     *
     *  The idea for adding a new set, t, is to look for overlap with the
     *  elements of existing list s.  Upon overlap, replace
     *  existing set s[i] with two new disjoint sets, s[i]-t and s[i]&t.
     *  (if s[i]-t is nil, don't add).  The remainder is t-s[i], which is
     *  what you want to add to the set minus what was already there.  The
     *  remainder must then be compared against the i+1..n elements in s
     *  looking for another collision.  Each collision results in a smaller
     *  and smaller remainder.  Stop when you run out of s elements or
     *  remainder goes to nil.  If remainder is non nil when you run out of
     *  s elements, then add remainder to the end.
     */
    protected void addReachableLabel(OrderedHashSet<IntervalSet> reachableLabels,
									 IntervalSet label)
	{
		/*
		System.out.println("addReachableLabel to state "+dfa.decisionNumber+"."+stateNumber+": "+label.getSet().toString(dfa.nfa.grammar));
		System.out.println("start of add to state "+dfa.decisionNumber+"."+stateNumber+": " +
				"reachableLabels="+reachableLabels.toString());
				*/
		if ( reachableLabels.contains(label) ) { // exact label present
            return;
        }
        IntervalSet remainder = label; // remainder starts out as whole set to add
        int n = reachableLabels.size(); // only look at initial elements
        // walk the existing list looking for the collision
        for (int i=0; i<n; i++) {
			IntervalSet rl = reachableLabels.get(i);
            /*
			System.out.println("comparing ["+i+"]: "+label.toString(dfa.nfa.grammar)+" & "+
                    rl.toString(dfa.nfa.grammar)+"="+
                    intersection.toString(dfa.nfa.grammar));
            */
			IntervalSet intersection = (IntervalSet)label.and(rl);
			if ( intersection.isNil() ) {
                continue;
            }
			//System.out.println(label+" collides with "+rl);

			// For any (s_i, t) with s_i&t!=nil replace with (s_i-t, s_i&t)
            // (ignoring s_i-t if nil; don't put in list)

            // Replace existing s_i with intersection since we
            // know that will always be a non nil character class
			IntervalSet s_i = rl;
            reachableLabels.set(i, intersection);

            // Compute s_i-t to see what is in current set and not in incoming
            IntervalSet existingMinusNewElements = (IntervalSet)s_i.subtract(label);
			//System.out.println(s_i+"-"+t+"="+existingMinusNewElements);
            if ( !existingMinusNewElements.isNil() ) {
                // found a new character class, add to the end (doesn't affect
                // outer loop duration due to n computation a priori.
                reachableLabels.add(existingMinusNewElements);
            }

			/*
            System.out.println("after collision, " +
                    "reachableLabels="+reachableLabels.toString());
					*/

            // anything left to add to the reachableLabels?
            remainder = (IntervalSet)label.subtract(s_i);
            if ( remainder.isNil() ) {
                break; // nothing left to add to set.  done!
            }

            label = remainder;
        }
        if ( !remainder.isNil() ) {
			/*
			System.out.println("before add remainder to state "+dfa.decisionNumber+"."+stateNumber+": " +
					"reachableLabels="+reachableLabels.toString());
			System.out.println("remainder state "+dfa.decisionNumber+"."+stateNumber+": "+remainder.toString(dfa.nfa.grammar));
            */
            reachableLabels.add(remainder);
        }
		/*
		System.out.println("#END of add to state "+dfa.decisionNumber+"."+stateNumber+": " +
				"reachableLabels="+reachableLabels.toString());
				*/
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
			DFAState predDFATarget = dfa.altToAcceptState[c.alt];
			if ( predDFATarget==null ) {
				predDFATarget = dfa.newState(); // create if not there.
				// new DFA state is a target of the predicate from d
				predDFATarget.addNFAConfig(c.state,
										   c.alt,
										   c.context,
										   c.semanticContext);
				predDFATarget.isAcceptState = true;
				dfa.defineAcceptState(c.alt, predDFATarget);
				// v3 checked if already there, but new state is an accept
				// state and therefore can't be there yet; we just checked above
//				DFAState existingState = dfa.addState(predDFATarget);
//				if ( predDFATarget != existingState ) {
//					// already there...use/return the existing DFA state that
//					// is a target of this predicate.  Make this state number
//					// point at the existing state
//					dfa.setState(predDFATarget.stateNumber, existingState);
//					predDFATarget = existingState;
//				}
			}
			// add a transition to pred target from d
			d.addTransition(new PredicateEdge(c.semanticContext, predDFATarget));
		}
	}
	
}
