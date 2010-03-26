package org.antlr.v4.analysis;

import org.antlr.v4.automata.DecisionState;
import org.antlr.v4.automata.NFAState;
import org.antlr.v4.automata.RuleTransition;
import org.antlr.v4.automata.Transition;
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
public class RecursionLimitedNFAToDFAConverter extends StackLimitedNFAToDFAConverter {
	/** This is similar to Bermudez's m constant in his LAR(m) where
	 *  you bound the stack so your states don't explode.  The main difference
	 *  is that I bound only recursion on the stack, not the simple stack size.
	 *  This looser constraint will let the conversion roam further to find
	 *  lookahead to resolve a decision.
	 *
	 *  Bermudez's m operates differently as it is his LR stack depth
	 *  I'm pretty sure it therefore includes all stack symbols.  Here I
	 *  restrict the size of an NFA configuration to be finite because a
	 *  stack component may mention the same NFA invocation state at
	 *  most m times.  Hence, the number of DFA states will not grow forever.
	 *  With recursive rules like
	 *
	 *    e : '(' e ')' | INT ;
	 *
	 *  you could chase your tail forever if somebody said "s : e '.' | e ';' ;"
	 *  This constant prevents new states from being created after a stack gets
	 *  "too big".  Actually (12/14/2007) I realize that this example is
	 *  trapped by the non-LL(*) detector for recursion in > 1 alt.  Here is
	 *  an example that trips stack overflow:
	 *
	 *	  s : a Y | A A A A A X ; // force recursion past m=4
	 *	  a : A a | Q;
	 *
	 *  If that were:
	 *
	 *	  s : a Y | A+ X ;
	 *
	 *  it could loop forever.
	 *
	 *  Imagine doing a depth-first search on the e DFA...as you chase an input
	 *  sequence you can recurse to same rule such as e above.  You'd have a
	 *  chain of ((((.  When you get do some point, you have to give up.  The
	 *  states in the chain will have longer and longer NFA config stacks.
	 *  Must limit size.
	 *
	 *  max=0 implies you cannot ever jump to another rule during closure.
	 *  max=1 implies you can make as many calls as you want--you just
	 *        can't ever visit a state that is on your rule invocation stack.
	 * 		  I.e., you cannot ever recurse.
	 *  max=2 implies you are able to recurse once (i.e., call a rule twice
	 *  	  from the same place).
	 *
	 *  This tracks recursion to a rule specific to an invocation site!
	 *  It does not detect multiple calls to a rule from different rule
	 *  invocation states.  We are guaranteed to terminate because the
	 *  stack can only grow as big as the number of NFA states * max.
	 *
	 *  I noticed that the Java grammar didn't work with max=1, but did with
	 *  max=4.  Let's set to 4. Recursion is sometimes needed to resolve some
	 *  fixed lookahead decisions.
	 */
	public static int DEFAULT_MAX_SAME_RULE_INVOCATIONS_PER_NFA_CONFIG_STACK = 4;

	/** Max recursion depth.
	 *  approx is setting stack size like bermudez: m=1 in this case.
	 *  full alg limits recursion not overall stack size.  that's more
	 *  like LL(k) analysis which can have any stack size, but will recurse
	 *  a max of k times since it can only see k tokens. each recurse pumps
	 *  another token. limiting stack size to m={0,1} lets us convert
	 *  recursion to loops. use r constant not m for recursion depth?
	 */
	public int r = DEFAULT_MAX_SAME_RULE_INVOCATIONS_PER_NFA_CONFIG_STACK;

	/** Track whether an alt discovers recursion for each alt during
	 *  NFA to DFA conversion; >1 alt with recursion implies nonregular.
	 */
	public IntSet recursiveAltSet = new BitSet();
	
	public RecursionLimitedNFAToDFAConverter(Grammar g, DecisionState nfaStartState) {
		super(g, nfaStartState);
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
	void ruleStopStateClosure(NFAState s, int altNum, NFAContext context,
							  SemanticContext semanticContext,
							  boolean collectPredicates,
							  List<NFAConfig> configs)
	{
		if ( context != NFAContext.EMPTY) {
			NFAContext newContext = context.parent; // "pop" invoking state
			closure(context.returnState, altNum, newContext, semanticContext, collectPredicates, configs);
		}
		else {
			commonClosure(s, altNum, context, semanticContext, collectPredicates, configs); // do global FOLLOW
		}
	}

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
	void commonClosure(NFAState s, int altNum, NFAContext context,
					   SemanticContext semanticContext,
					   boolean collectPredicates,
					   List<NFAConfig> configs)
	{
		int n = s.getNumberOfTransitions();
		for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t instanceof RuleTransition) {
				NFAState retState = ((RuleTransition)t).followState;
				int depth = context.recursionDepthEmanatingFromState(retState.stateNumber);
				if ( depth==1 ) { // recursion
					recursiveAltSet.add(altNum); // indicate that this alt is recursive
					if ( recursiveAltSet.size()>1 ) {
						throw new MultipleRecursiveAltsSignal(recursiveAltSet);
					}
				}
				// Detect an attempt to recurse too high
				// if this context has hit the max recursions for p.stateNumber,
				// don't allow it to enter p.stateNumber again
				if ( depth >= r ) {
					throw new RecursionOverflowSignal(altNum, depth, s);
				}
				// first create a new context and push onto call tree,
				// recording the fact that we are invoking a rule and
				// from which state (case 2 below will get the following state
				// via the RuleTransition emanating from the invoking state
				// pushed on the stack).
				NFAContext newContext = new NFAContext(context, retState);
				// traverse epsilon edge to new rule
				closure(t.target, altNum, newContext, semanticContext, collectPredicates, configs);
			}
			else if ( t.isEpsilon() ) {
				closure(t.target, altNum, context, semanticContext, collectPredicates, configs);
			}
		}
	}
}
