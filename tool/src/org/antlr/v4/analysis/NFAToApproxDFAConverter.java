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
public class NFAToApproxDFAConverter {
	public static final NFAContext NFA_EMPTY_STACK_CONTEXT = new NFAContext(null, null);

	Grammar g;

	DecisionState nfaStartState;

	/** DFA we are creating */
	DFA dfa;

	/** A list of DFA states we still need to process during NFA conversion */
	List<DFAState> work = new LinkedList<DFAState>();

	/** Used to prevent the closure operation from looping to itself and
     *  hence looping forever.  Sensitive to the NFA state, the alt, and
     *  the stack context.
     */
	Set<NFAConfig> closureBusy;

	public static boolean debug = false;

	public NFAToApproxDFAConverter(Grammar g, DecisionState nfaStartState) {
		this.g = g;
		this.nfaStartState = nfaStartState;
		dfa = new DFA(g, nfaStartState);
	}

	public DFA createDFA() {
		dfa.startState = computeStartState();
		dfa.addState(dfa.startState); // make sure dfa knows about this state
		work.add(dfa.startState);

		// while more DFA states to check, process them
		while ( work.size()>0 ) {
			reach( work.get(0) );
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
			// nothing was reached by label due to conflict resolution
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
		boolean approx = this instanceof NFAToApproxDFAConverter;
		Resolver.resolveNonDeterminisms(t, approx);

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
				// found a transition with label; does it collide with label?
				if ( !t.isEpsilon() && !t.label().and(label).isNil() ) {
					// add NFA target to (potentially) new DFA state
					labelTarget.addNFAConfig(t.target, c.alt, c.context);
				}
			}
		}

		if ( labelTarget.nfaConfigs.size()==0 ) {
			System.err.println("why is this empty?");
		}
		
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
	public DFAState computeStartState() {
		DFAState d = dfa.newState();

		// add config for each alt start, then add closure for those states
		for (int altNum=1; altNum<=dfa.nAlts; altNum++) {
			Transition t = nfaStartState.transition(altNum-1);
			NFAState altStart = t.target;
			NFAContext initialContext = NFA_EMPTY_STACK_CONTEXT;
			d.addNFAConfig(altStart, altNum, initialContext);
		}

		closure(d);

		return d;
	}

	/** For all NFA states (configurations) merged in d,
	 *  compute the epsilon closure; that is, find all NFA states reachable
	 *  from the NFA states in d via purely epsilon transitions.
	 */
	public void closure(DFAState d) {
		if ( debug ) {
			System.out.println("closure("+d+")");
		}

		closureBusy = new HashSet<NFAConfig>();
		
		List<NFAConfig> configs = new ArrayList<NFAConfig>();
		for (NFAConfig c : d.nfaConfigs) {
			closure(c.state, c.alt, c.context, configs);
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
						List<NFAConfig> configs)
	{
		NFAConfig proposedNFAConfig = new NFAConfig(s, altNum, context);

		if ( closureBusy.contains(proposedNFAConfig) ) return;
		closureBusy.add(proposedNFAConfig);

		// p itself is always in closure
		configs.add(proposedNFAConfig);

		// if we have context info and we're at rule stop state, do
		// local follow for invokingRule and global follow for other links
		if ( s instanceof RuleStopState ) {
			ruleStopStateClosure(s, altNum, context, configs);
		}
		else {
			commonClosure(s, altNum, context, configs);
		}
	}

	void ruleStopStateClosure(NFAState s, int altNum, NFAContext context, List<NFAConfig> configs) {
		Rule invokingRule = null;

		if ( context!=NFA_EMPTY_STACK_CONTEXT ) invokingRule = context.returnState.rule;

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
				closure(t.target, altNum, context, configs);
			}
			else {
				if ( t.target == context.returnState) {
					//System.out.println("OFF TO CALL SITE "+t.target);
					// go only to specific call site; pop context
					closure(t.target, altNum, NFA_EMPTY_STACK_CONTEXT, configs);
				}
			}
		}
		return;
	}

	void commonClosure(NFAState s, int altNum, NFAContext context, List<NFAConfig> configs) {
		int n = s.getNumberOfTransitions();
		for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			NFAContext newContext = context;	// assume old context
			if ( t instanceof RuleTransition) {
				NFAState retState = ((RuleTransition)t).followState;
				if ( context==NFA_EMPTY_STACK_CONTEXT ) { // track first call return state only
					newContext = new NFAContext(context, retState);
				}
			}
			if ( t.isEpsilon() ) {
				closure(t.target, altNum, newContext, configs);
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
}
