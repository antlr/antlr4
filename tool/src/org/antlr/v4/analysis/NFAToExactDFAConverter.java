package org.antlr.v4.analysis;

import org.antlr.v4.automata.*;
import org.antlr.v4.misc.BitSet;
import org.antlr.v4.misc.IntSet;
import org.antlr.v4.tool.Grammar;

import java.util.List;

/**
 * closure: Add new NFA states + context to DFA state d.  Also add semantic
	 *  predicates to semantic context if collectPredicates is set.  We only
	 *  collect predicates at hoisting depth 0, meaning before any token/char
	 *  have been recognized.  This corresponds, during analysis, to the
	 *  initial DFA start state construction closure() invocation.
 * 
 *  When is a closure operation in a cycle condition?  While it is
 *  very possible to have the same NFA state mentioned twice
 *  within the same DFA state, there are two situations that
 *  would lead to nontermination of closure operation:
 *
 *  o   Whenever closure reaches a configuration where the same state
 *      with same or a suffix context already exists.  This catches
 *      the IF-THEN-ELSE tail recursion cycle and things like
 *
 *      a : A a | B ;
 *
 *      the context will be $ (empty stack).
 *
 *      We have to check
 *      larger context stacks because of (...)+ loops.  For
 *      example, the context of a (...)+ can be nonempty if the
 *      surrounding rule is invoked by another rule:
 *
 *      a : b A | X ;
 *      b : (B|)+ ;  // nondeterministic by the way
 *
 *      The context of the (B|)+ loop is "invoked from item
 *      a : . b A ;" and then the empty alt of the loop can reach back
 *      to itself.  The context stack will have one "return
 *      address" element and so we must check for same state, same
 *      context for arbitrary context stacks.
 *
 *      Idea: If we've seen this configuration before during closure, stop.
 *      We also need to avoid reaching same state with conflicting context.
 *      Ultimately analysis would stop and we'd find the conflict, but we
 *      should stop the computation.  Previously I only checked for
 *      exact config.  Need to check for same state, suffix context
 * 		not just exact context.
 *
 *  o   Whenever closure reaches a configuration where state p
 *      is present in its own context stack.  This means that
 *      p is a rule invocation state and the target rule has
 *      been called before.  NFAContext.MAX_RECURSIVE_INVOCATIONS
 *      (See the comment there also) determines how many times
 *      it's possible to recurse; clearly we cannot recurse forever.
 *      Some grammars such as the following actually require at
 *      least one recursive call to correctly compute the lookahead:
 *
 *      a : L ID R
 *        | b
 *        ;
 *      b : ID
 *        | L a R
 *        ;
 *
 *      Input L ID R is ambiguous but to figure this out, ANTLR
 *      needs to go a->b->a->b to find the L ID sequence.
 *
 *      Do not allow closure to add a configuration that would
 *      allow too much recursion.
 *
 *      This case also catches infinite left recursion.
 */
public class NFAToExactDFAConverter extends NFAToApproxDFAConverter {
	/** Track whether an alt discovers recursion for each alt during
	 *  NFA to DFA conversion; >1 alt with recursion implies nonregular.
	 */
	public IntSet recursiveAltSet = new BitSet();
		
	public NFAToExactDFAConverter(Grammar g, DecisionState nfaStartState) {
		super(g, nfaStartState);
	}

	@Override
	void reach(DFAState d) {
		super.reach(d);

//		if ( !d.isResolvedWithPredicates() && d.getNumberOfTransitions()==0 ) {
//			//System.out.println("dangling DFA state "+d+"\nAfter reach / closures:\n"+dfa);
//			// TODO: can fixed lookahead hit a dangling state case?
//			// TODO: yes, with left recursion
//			//System.err.println("dangling state alts: "+d.getAltSet());
//			dfa.probe.reportDanglingState(d);
//			// turn off all configurations except for those associated with
//			// min alt number; somebody has to win else some input will not
//			// predict any alt.
//			int minAlt = Resolver.resolveByPickingMinAlt(d, null);
//			// force it to be an accept state
//			// don't call convertToAcceptState() which merges stop states.
//			// other states point at us; don't want them pointing to dead states
//			d.isAcceptState = true; // might be adding new accept state for alt
//			dfa.defineAcceptState(minAlt, d);
//		}
//
//		// Check to see if we need to add any semantic predicate transitions
//		if ( d.isResolvedWithPredicates() ) {
//			addPredicateTransitions(d);
//		}
	}

	/**
	 *   1. Reach an NFA state associated with the end of a rule, r, in the
	 *      grammar from which it was built.  We must add an implicit (i.e.,
	 *      don't actually add an epsilon transition) epsilon transition
	 *      from r's end state to the NFA state following the NFA state
	 *      that transitioned to rule r's start state.  Because there are
	 *      many states that could reach r, the context for a rule invocation
	 *      is part of a call tree not a simple stack.  When we fall off end
	 *      of rule, "pop" a state off the call tree and add that state's
	 *      "following" node to d's NFA configuration list.  The context
	 *      for this new addition will be the new "stack top" in the call tree.
	 *
	 *   2. Like case 1, we reach an NFA state associated with the end of a
	 *      rule, r, in the grammar from which NFA was built.  In this case,
	 *      however, we realize that during this NFA->DFA conversion, no state
	 *      invoked the current rule's NFA.  There is no choice but to add
	 *      all NFA states that follow references to r's start state.  This is
	 *      analogous to computing the FOLLOW(r) in the LL(k) world.  By
	 *      construction, even rule stop state has a chain of nodes emanating
	 *      from it that points to every possible following node.  This case
	 *      is conveniently handled then by the common closure case.
	 */
	void ruleStopStateClosure(NFAState s, int altNum, NFAContext context, List<NFAConfig> configs) {
		if ( context.parent!=null ) {
			NFAContext newContext = context.parent; // "pop" invoking state
			closure(context.returnState, altNum, newContext, configs);
		}
		else {
			commonClosure(s, altNum, context, configs); // do global FOLLOW
		}
	}

	// TODO: make sure to deal with sempreds in exact DFA
	/**  1. Traverse an edge that takes us to the start state of another
	 *      rule, r.  We must push this state so that if the DFA
	 *      conversion hits the end of rule r, then it knows to continue
	 *      the conversion at state following state that "invoked" r. By
	 *      construction, there is a single transition emanating from a rule
	 *      ref node.
	 *
	 *   2. Normal case.  If s can see another NFA state q via epsilon, then add
	 *      q to d's configuration list, copying p's context for q's context.
	 *      If there is a semantic predicate on the transition, then AND it
	 *      with any existing semantic context.
	 *
	 *   3. Preds?
	 */
	void commonClosure(NFAState s, int altNum, NFAContext context, List<NFAConfig> configs) {
		int n = s.getNumberOfTransitions();
		for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t instanceof RuleTransition) {
				NFAState retState = ((RuleTransition)t).followState;
				int depth = context.recursionDepthEmanatingFromState(retState.stateNumber);
				if ( depth==1 ) { // recursion
					recursiveAltSet.add(altNum); // indicate that this alt is recursive
					if ( recursiveAltSet.size()>1 ) {
						throw new RuntimeException("recursion in >1 alt: "+recursiveAltSet);
					}
				}
				// first create a new context and push onto call tree,
				// recording the fact that we are invoking a rule and
				// from which state (case 2 below will get the following state
				// via the RuleTransition emanating from the invoking state
				// pushed on the stack).
				NFAContext newContext = new NFAContext(context, retState);
				// traverse epsilon edge to new rule
				closure(t.target, altNum, newContext, configs);
			}
			else if ( t.isEpsilon() ) {
				closure(t.target, altNum, context, configs);
			}
		}
	}
}
