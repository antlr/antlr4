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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.DoubleKeyMap;
import org.antlr.v4.runtime.misc.FlexibleHashMap;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 The embodiment of the adaptive LL(*), ALL(*), parsing strategy.

 The basic complexity of the adaptive strategy makes it harder to
 understand. We begin with ATN simulation to build paths in a
 DFA. Subsequent prediction requests go through the DFA first. If
 they reach a state without an edge for the current symbol, the
 algorithm fails over to the ATN simulation to complete the DFA
 path for the current input (until it finds a conflict state or
 uniquely predicting state).

 All of that is done without using the outer context because we
 want to create a DFA that is not dependent upon the rule
 invocation stack when we do a prediction.  One DFA works in all
 contexts. We avoid using context not necessarily because it's
 slower, although it can be, but because of the DFA caching
 problem.  The closure routine only considers the rule invocation
 stack created during prediction beginning in the decision rule.  For
 example, if prediction occurs without invoking another rule's
 ATN, there are no context stacks in the configurations.
 When lack of context leads to a conflict, we don't know if it's
 an ambiguity or a weakness in the strong LL(*) parsing strategy
 (versus full LL(*)).

 When SLL yields a configuration set with conflict, we rewind the
 input and retry the ATN simulation, this time using
 full outer context without adding to the DFA. Configuration context
 stacks will be the full invocation stacks from the start rule. If
 we get a conflict using full context, then we can definitively
 say we have a true ambiguity for that input sequence. If we don't
 get a conflict, it implies that the decision is sensitive to the
 outer context. (It is not context-sensitive in the sense of
 context-sensitive grammars.)

 The next time we reach this DFA state with an SLL conflict, through
 DFA simulation, we will again retry the ATN simulation using full
 context mode. This is slow because we can't save the results and have
 to "interpret" the ATN each time we get that input.

 CACHING FULL CONTEXT PREDICTIONS

 We could cache results from full context to predicted
 alternative easily and that saves a lot of time but doesn't work
 in presence of predicates. The set of visible predicates from
 the ATN start state changes depending on the context, because
 closure can fall off the end of a rule. I tried to cache
 tuples (stack context, semantic context, predicted alt) but it
 was slower than interpreting and much more complicated. Also
 required a huge amount of memory. The goal is not to create the
 world's fastest parser anyway. I'd like to keep this algorithm
 simple. By launching multiple threads, we can improve the speed
 of parsing across a large number of files.

 There is no strict ordering between the amount of input used by
 SLL vs LL, which makes it really hard to build a cache for full
 context. Let's say that we have input A B C that leads to an SLL
 conflict with full context X.  That implies that using X we
 might only use A B but we could also use A B C D to resolve
 conflict.  Input A B C D could predict alternative 1 in one
 position in the input and A B C E could predict alternative 2 in
 another position in input.  The conflicting SLL configurations
 could still be non-unique in the full context prediction, which
 would lead us to requiring more input than the original A B C.	To
 make a	prediction cache work, we have to track	the exact input	used
 during the previous prediction. That amounts to a cache that maps X
 to a specific DFA for that context.

 Something should be done for left-recursive expression predictions.
 They are likely LL(1) + pred eval. Easier to do the whole SLL unless
 error and retry with full LL thing Sam does.

 AVOIDING FULL CONTEXT PREDICTION

 We avoid doing full context retry when the outer context is empty,
 we did not dip into the outer context by falling off the end of the
 decision state rule, or when we force SLL mode.

 As an example of the not dip into outer context case, consider
 as super constructor calls versus function calls. One grammar
 might look like this:

 ctorBody : '{' superCall? stat* '}' ;

 Or, you might see something like

 stat : superCall ';' | expression ';' | ... ;

 In both cases I believe that no closure operations will dip into the
 outer context. In the first case ctorBody in the worst case will stop
 at the '}'. In the 2nd case it should stop at the ';'. Both cases
 should stay within the entry rule and not dip into the outer context.

 PREDICATES

 Predicates are always evaluated if present in either SLL or LL both.
 SLL and LL simulation deals with predicates differently. SLL collects
 predicates as it performs closure operations like ANTLR v3 did. It
 delays predicate evaluation until it reaches and accept state. This
 allows us to cache the SLL ATN simulation whereas, if we had evaluated
 predicates on-the-fly during closure, the DFA state configuration sets
 would be different and we couldn't build up a suitable DFA.

 When building a DFA accept state during ATN simulation, we evaluate
 any predicates and return the sole semantically valid alternative. If
 there is more than 1 alternative, we report an ambiguity. If there are
 0 alternatives, we throw an exception. Alternatives without predicates
 act like they have true predicates. The simple way to think about it
 is to strip away all alternatives with false predicates and choose the
 minimum alternative that remains.

 When we start in the DFA and reach an accept state that's predicated,
 we test those and return the minimum semantically viable
 alternative. If no alternatives are viable, we throw an exception.  We
 don't report ambiguities in the DFA, but I'm not sure why anymore.

 During full LL ATN simulation, closure always evaluates predicates and
 on-the-fly. This is crucial to reducing the configuration set size
 during closure. It hits a landmine when parsing with the Java grammar,
 for example, without this on-the-fly evaluation.

 SHARING DFA

 All instances of the same parser share the same decision DFAs through
 a static field. Each instance gets its own ATN simulator but they
 share the same decisionToDFA field. They also share a
 PredictionContextCache object that makes sure that all
 PredictionContext objects are shared among the DFA states. This makes
 a big size difference.

 THREAD SAFETY

 The parser ATN simulator locks on the decisionDFA field when it adds a
 new DFA object to that array. addDFAEdge locks on the DFA for the
 current decision when setting the edges[] field.  addDFAState locks on
 the DFA for the current decision when looking up a DFA state to see if
 it already exists.  We must make sure that all requests to add DFA
 states that are equivalent result in the same shared DFA object. This
 is because lots of threads will be trying to update the DFA at
 once. The addDFAState method also locks inside the DFA lock but this
 time on the shared context cache when it rebuilds the configurations'
 PredictionContext objects using cached subgraphs/nodes. No other
 locking occurs, even during DFA simulation. This is safe as long as we
 can guarantee that all threads referencing s.edge[t] get the same
 physical target DFA state, or none.  Once into the DFA, the DFA
 simulation does not reference the dfa.state map. It follows the
 edges[] field to new targets.  The DFA simulator will either find
 dfa.edges to be null, to be non-null and dfa.edges[t] null, or
 dfa.edges[t] to be non-null. The addDFAEdge method could be racing to
 set the field but in either case the DFA simulator works; if null, and
 requests ATN simulation.  It could also race trying to get
 dfa.edges[t], but either way it will work because it's not doing a
 test and set operation.

 Starting with SLL then failing to combined SLL/LL

 Sam pointed out that if SLL does not give a syntax error, then there
 is no point in doing full LL, which is slower. We only have to try LL
 if we get a syntax error.  For maximum speed, Sam starts the parser
 with pure SLL mode:

     parser.getInterpreter().setSLL(true);

 and with the bail error strategy:

     parser.setErrorHandler(new BailErrorStrategy());

 If it does not get a syntax error, then we're done. If it does get a
 syntax error, we need to retry with the combined SLL/LL strategy.

 The reason this works is as follows.  If there are no SLL
 conflicts then the grammar is SLL for sure, at least for that
 input set. If there is an SLL conflict, the full LL analysis
 must yield a set of ambiguous alternatives that is no larger
 than the SLL set. If the LL set is a singleton, then the grammar
 is LL but not SLL. If the LL set is the same size as the SLL
 set, the decision is SLL. If the LL set has size > 1, then that
 decision is truly ambiguous on the current input. If the LL set
 is smaller, then the SLL conflict resolution might choose an
 alternative that the full LL would rule out as a possibility
 based upon better context information. If that's the case, then
 the SLL parse will definitely get an error because the full LL
 analysis says it's not viable. If SLL conflict resolution
 chooses an alternative within the LL set, them both SLL and LL
 would choose the same alternative because they both choose the
 minimum of multiple conflicting alternatives.

 Let's say we have a set of SLL conflicting alternatives {1, 2, 3} and
 a smaller LL set called s. If s is {2, 3}, then SLL parsing will get
 an error because SLL will pursue alternative 1. If s is {1, 2} or {1,
 3} then both SLL and LL will choose the same alternative because
 alternative one is the minimum of either set. If s is {2} or {3} then
 SLL will get a syntax error. If s is {1} then SLL will succeed.

 Of course, if the input is invalid, then we will get an error for sure
 in both SLL and LL parsing. Erroneous input will therefore require 2
 passes over the input.

*/
public class ParserATNSimulator extends ATNSimulator {
	public static boolean debug = false;
	public static boolean debug_list_atn_decisions = false;
	public static boolean dfa_debug = false;
	public static boolean retry_debug = false;

	public static int ATN_failover = 0;
	public static int predict_calls = 0;
	public static int retry_with_context = 0;
	public static int retry_with_context_indicates_no_conflict = 0;
	public static int retry_with_context_predicts_same_as_alt = 0;
	public static int retry_with_context_from_dfa = 0;

	/** A Map that uses just the state and the stack context as the key.
	 *  Used by needMoreLookaheadLL.
	 */
	class AltAndContextMap extends FlexibleHashMap<ATNConfig,BitSet> {
		/** Code is function of (s, _, ctx, _) */
		@Override
		public int hashCode(ATNConfig o) {
			int hashCode = 7;
			hashCode = 31 * hashCode + o.state.stateNumber;
			hashCode = 31 * hashCode + o.context.hashCode();
	        return hashCode;
		}

		@Override
		public boolean equals(ATNConfig a, ATNConfig b) {
			if ( a==b ) return true;
			if ( a==null || b==null ) return false;
			if ( hashCode(a) != hashCode(b) ) return false;
			return a.state.stateNumber==b.state.stateNumber
				&& b.context.equals(b.context);
		}
	}

	@Nullable
	protected final Parser parser;

	@NotNull
	public final DFA[] decisionToDFA;

	/** Do only local context prediction (SLL(k) style). */
	protected boolean SLL = false;

	/** Each prediction operation uses a cache for merge of prediction contexts.
	 *  Don't keep around as it wastes huge amounts of memory. DoubleKeyMap
	 *  isn't synchronized but we're ok since two threads shouldn't reuse same
	 *  parser/atnsim object because it can only handle one input at a time.
	 *  This maps graphs a and b to merged result c. (a,b)->c. We can avoid
	 *  the merge if we ever see a and b again.  Note that (b,a)->c should
	 *  also be examined during cache lookup.
	 */
	protected DoubleKeyMap<PredictionContext,PredictionContext,PredictionContext> mergeCache;

	// LAME globals to avoid parameters!!!!! I need these down deep in predTransition
	protected TokenStream _input;
	protected int _startIndex;
	protected ParserRuleContext<?> _outerContext;

	/** Testing only! */
	public ParserATNSimulator(@NotNull ATN atn, @NotNull DFA[] decisionToDFA,
							  @NotNull PredictionContextCache sharedContextCache)
	{
		this(null, atn, decisionToDFA, sharedContextCache);
	}

	public ParserATNSimulator(@Nullable Parser parser, @NotNull ATN atn,
							  @NotNull DFA[] decisionToDFA,
							  @NotNull PredictionContextCache sharedContextCache)
	{
		super(atn,sharedContextCache);
		this.parser = parser;
		this.decisionToDFA = decisionToDFA;
		//		DOTGenerator dot = new DOTGenerator(null);
		//		System.out.println(dot.getDOT(atn.rules.get(0), parser.getRuleNames()));
		//		System.out.println(dot.getDOT(atn.rules.get(1), parser.getRuleNames()));
	}

	@Override
	public void reset() {
	}

	public int adaptivePredict(@NotNull TokenStream input, int decision,
							   @Nullable ParserRuleContext<?> outerContext)
	{
		if ( debug || debug_list_atn_decisions )  {
			System.out.println("adaptivePredict decision "+decision+
								   " exec LA(1)=="+ getLookaheadName(input)+
								   " line "+input.LT(1).getLine()+":"+input.LT(1).getCharPositionInLine());
		}
		mergeCache = new DoubleKeyMap<PredictionContext,PredictionContext,PredictionContext>();
		_input = input;
		_startIndex = input.index();
		_outerContext = outerContext;
		predict_calls++;
		DFA dfa = decisionToDFA[decision];
		// First, synchronize on the array of DFA for this parser
		// so that we can get the DFA for a decision or create and set one
		if ( dfa==null ) { // only create one if not there
			synchronized (decisionToDFA) {
				dfa = decisionToDFA[decision];
				if ( dfa==null ) { // the usual double-check
					DecisionState startState = atn.decisionToState.get(decision);
					decisionToDFA[decision] = new DFA(startState, decision);
					dfa = decisionToDFA[decision];
				}
			}
		}
		// Now we are certain to have a specific decision's DFA
		// But, do we still need an initial state?
		if ( dfa.s0==null ) { // recheck
			if ( dfa.s0==null ) { // recheck
				try {
					return predictATN(dfa, input, outerContext);
				}
				finally {
					mergeCache = null; // wack cache after each prediction
				}
			}
			// fall through; another thread set dfa.s0 while we waited for lock
		}

		// We can start with an existing DFA
		int m = input.mark();
		int index = input.index();
		try {
			return execDFA(dfa, dfa.s0, input, index, outerContext);
		}
		finally {
			mergeCache = null; // wack cache after each prediction
			input.seek(index);
			input.release(m);
		}
	}

	public int predictATN(@NotNull DFA dfa, @NotNull TokenStream input,
						  @Nullable ParserRuleContext<?> outerContext)
	{
		// caller must have write lock on dfa
		if ( outerContext==null ) outerContext = ParserRuleContext.EMPTY;
		if ( debug || debug_list_atn_decisions )  {
			System.out.println("predictATN decision "+dfa.decision+
							   " exec LA(1)=="+ getLookaheadName(input) +
							   ", outerContext="+outerContext.toString(parser));
		}
		DecisionState decState = atn.getDecisionState(dfa.decision);
		boolean fullCtx = false;
		ATNConfigSet s0_closure =
			computeStartState(dfa.atnStartState,
							  ParserRuleContext.EMPTY,
							  fullCtx);
		dfa.s0 = addDFAState(dfa, new DFAState(s0_closure));

		int alt = 0;
		int m = input.mark();
		int index = input.index();
		try {
			alt = execATN(dfa, dfa.s0, input, index, outerContext);
		}
		catch (NoViableAltException nvae) {
			if ( debug ) dumpDeadEndConfigs(nvae);
			throw nvae;
		}
		finally {
			input.seek(index);
			input.release(m);
		}
		if ( debug ) System.out.println("DFA after predictATN: "+dfa.toString(parser.getTokenNames()));
		return alt;
	}

	public int execDFA(@NotNull DFA dfa, @NotNull DFAState s0,
					   @NotNull TokenStream input, int startIndex,
                       @Nullable ParserRuleContext<?> outerContext)
    {
		// caller must have read lock on dfa
		if ( outerContext==null ) outerContext = ParserRuleContext.EMPTY;
		if ( dfa_debug ) {
			System.out.println("execDFA decision "+dfa.decision+
							   " exec LA(1)=="+ getLookaheadName(input) +
							   ", outerContext="+outerContext.toString(parser));
		}
		if ( dfa_debug ) System.out.print(dfa.toString(parser.getTokenNames()));
		DFAState acceptState = null;
		DFAState s = s0;

		DecisionState decState = atn.getDecisionState(dfa.decision);

		int t = input.LA(1);
	loop:
		while ( true ) {
			if ( dfa_debug ) System.out.println("DFA state "+s.stateNumber+" LA(1)=="+getLookaheadName(input));
			if ( s.requiresFullContext && !SLL ) {
				if ( dfa_debug ) System.out.println("ctx sensitive state "+outerContext+" in "+s);
				boolean fullCtx = true;
				ATNConfigSet s0_closure =
					computeStartState(dfa.atnStartState, outerContext,
									  fullCtx);
				retry_with_context_from_dfa++;
				int alt = execATNWithFullContext(dfa, s, s0_closure,
												 input, startIndex,
												 outerContext,
												 ATN.INVALID_ALT_NUMBER);
				return alt;
			}
			if ( s.isAcceptState ) {
				if ( s.predicates!=null ) {
					if ( dfa_debug ) System.out.println("accept "+s);
				}
				else {
					if ( dfa_debug ) System.out.println("accept; predict "+s.prediction +" in state "+s.stateNumber);
				}
				acceptState = s;
				// keep going unless we're at EOF or state only has one alt number
				// mentioned in configs; check if something else could match
				// TODO: don't we always stop? only lexer would keep going
				// TODO: v3 dfa don't do this.
				break;
			}

			// t is not updated if one of these states is reached
			assert !s.requiresFullContext && !s.isAcceptState;

			// if no edge, pop over to ATN interpreter, update DFA and return
			if ( s.edges == null || t >= s.edges.length || t < -1 || s.edges[t+1] == null ) {
				if ( dfa_debug && t>=0 ) System.out.println("no edge for "+parser.getTokenNames()[t]);
				int alt;
				if ( dfa_debug ) {
					Interval interval = Interval.of(startIndex, parser.getTokenStream().index());
					System.out.println("ATN exec upon "+
										   parser.getTokenStream().getText(interval) +
										   " at DFA state "+s.stateNumber);
				}

				// recheck; another thread might have added edge
				if ( s.edges == null || t >= s.edges.length || t < -1 || s.edges[t+1] == null ) {
					alt = execATN(dfa, s, input, startIndex, outerContext);
					// this adds edge even if next state is accept for
					// same alt; e.g., s0-A->:s1=>2-B->:s2=>2
					// TODO: This next stuff kills edge, but extra states remain. :(
					if ( s.isAcceptState && alt!=-1 ) {
						DFAState d = s.edges[input.LA(1)+1];
						if ( d.isAcceptState && d.prediction==s.prediction ) {
							// we can carve it out.
							s.edges[input.LA(1)+1] = ERROR; // IGNORE really not error
						}
					}
					if ( dfa_debug ) {
						System.out.println("back from DFA update, alt="+alt+", dfa=\n"+dfa.toString(parser.getTokenNames()));
						//dump(dfa);
					}
					// action already executed
					if ( dfa_debug ) System.out.println("DFA decision "+dfa.decision+
															" predicts "+alt);
					return alt; // we've updated DFA, exec'd action, and have our deepest answer
				}
				// fall through; another thread gave us the edge
			}
			DFAState target = s.edges[t+1];
			if ( target == ERROR ) {
				throw noViableAlt(input, outerContext, s.configs, startIndex);
			}
			s = target;
			if (!s.requiresFullContext && !s.isAcceptState) {
				input.consume();
				t = input.LA(1);
			}
		}

		// Before jumping to prediction, check to see if there are
		// disambiguating predicates to evaluate
		if ( s.predicates!=null ) {
			// rewind input so pred's LT(i) calls make sense
			input.seek(startIndex);
			// since we don't report ambiguities in execDFA, we never need to
			// use complete predicate evaluation here
			BitSet alts = evalSemanticContext(s.predicates, outerContext, false);
			if (alts.isEmpty()) {
				throw noViableAlt(input, outerContext, s.configs, startIndex);
			}

			return alts.nextSetBit(0);
		}

		if ( dfa_debug ) System.out.println("DFA decision "+dfa.decision+
											" predicts "+acceptState.prediction);
		return acceptState.prediction;
	}

	/** Performs ATN simulation to compute a predicted alternative based
	 *  upon the remaining input, but also updates the DFA cache to avoid
	 *  having to traverse the ATN again for the same input sequence.

	 There are some key conditions we're looking for after computing a new
	 set of ATN configs (proposed DFA state):
	       * if the set is empty, there is no viable alternative for current symbol
	       * does the state uniquely predict an alternative?
	       * does the state have a conflict that would prevent us from
	         putting it on the work list?
	       * if in non-greedy decision is there a config at a rule stop state?

	 We also have some key operations to do:
	       * add an edge from previous DFA state to potentially new DFA state, D,
	         upon current symbol but only if adding to work list, which means in all
	         cases except no viable alternative (and possibly non-greedy decisions?)
	       * collecting predicates and adding semantic context to DFA accept states
	       * adding rule context to context-sensitive DFA accept states
	       * consuming an input symbol
	       * reporting a conflict
	       * reporting an ambiguity
	       * reporting a context sensitivity
	       * reporting insufficient predicates

	 We should isolate those operations, which are side-effecting, to the
	 main work loop. We can isolate lots of code into other functions, but
	 they should be side effect free. They can return package that
	 indicates whether we should report something, whether we need to add a
	 DFA edge, whether we need to augment accept state with semantic
	 context or rule invocation context. Actually, it seems like we always
	 add predicates if they exist, so that can simply be done in the main
	 loop for any accept state creation or modification request.

	 cover these cases:
	    dead end
	    single alt
	    single alt + preds
	    conflict
	    conflict + preds

	 TODO: greedy + those

	 */
	public int execATN(@NotNull DFA dfa, @NotNull DFAState s0,
					   @NotNull TokenStream input, int startIndex,
					   ParserRuleContext<?> outerContext)
	{
		// caller is expected to have write lock on dfa
		if ( debug || debug_list_atn_decisions) {
			System.out.println("execATN decision "+dfa.decision+
							   " exec LA(1)=="+ getLookaheadName(input)+
							   " line "+input.LT(1).getLine()+":"+input.LT(1).getCharPositionInLine());
		}
		ATN_failover++;

		ATNConfigSet previous = s0.configs;
		DFAState previousD = s0;
		ATNConfigSet fullCtxSet;

		if ( debug ) System.out.println("s0 = "+s0);

		int t = input.LA(1);

        DecisionState decState = atn.getDecisionState(dfa.decision);

		while (true) { // while more work
//			System.out.println("REACH "+getLookaheadName(input));
			ATNConfigSet reach = computeReachSet(previous, t,
												 false);
			if ( reach==null ) {
				// if any configs in previous dipped into outer context, that
				// means that input up to t actually finished entry rule
				// at least for SLL decision. Full LL doesn't dip into outer
				// so don't need special case.
				// We will get an error no matter what so delay until after
				// decision; better error message. Also, no reachable target
				// ATN states in SLL implies LL will also get nowhere.
				// If conflict in states that dip out, choose min since we
				// will get error no matter what.
				int alt = getAltThatFinishedDecisionEntryRule(previousD.configs);
				if ( alt!=ATN.INVALID_ALT_NUMBER ) {
					// return w/o altering DFA
					return alt;
				}
				throw noViableAlt(input, outerContext, previous, startIndex);
			}

			// create new target state; we'll add to DFA after it's complete
			DFAState D = new DFAState(reach);

			// It's often the case that D will already exist in the DFA
			// and so it's a waste to compute all of the fields over the next
			// big chunk of code. However, I tried inserting the following
			// short circuit, but it didn't have much effect. I
			// figured that this would be a big impact for multi threading,
			// but it also didn't see much of an impact.
//			synchronized (dfa) {
//				DFAState existing = dfa.states.get(D);
//				if ( existing!=null ) {
//					addDFAEdge(dfa, previousD, t, existing);
//					if ( existing.isAcceptState ) return existing.prediction;
//					previous = D.configs;
//					previousD = D;
//					input.consume();
//					t = input.LA(1);
//					continue;
//				}
//			}

			Collection<BitSet> altSubSets = getConflictingAltSubsets(reach);
//			System.out.println("SLL altsets: "+altSubSets);

			int predictedAlt = getUniqueAlt(altSubSets);

			if ( debug ) {
				System.out.println("SLL altSubSets="+altSubSets+
								   ", configs="+reach+
								   ", predict="+predictedAlt+", allSubsetsConflict="+
								   allSubsetsConflict(altSubSets)+", conflictingAlts="+
								   getConflictingAlts(reach));
			}

			if ( predictedAlt!=ATN.INVALID_ALT_NUMBER ) {
				// NO CONFLICT, UNIQUE PREDICTED ALT
				D.isAcceptState = true;
				D.configs.uniqueAlt = predictedAlt;
				D.prediction = predictedAlt;
			}
			else {
				// MORE THAN ONE VIABLE ALTERNATIVE
				if ( allSubsetsConflict(altSubSets) ) {
					D.configs.conflictingAlts = getConflictingAlts(reach);
					if ( outerContext == ParserRuleContext.EMPTY || // in grammar start rule
						 !D.configs.dipsIntoOuterContext ||         // didn't fall out of rule
						 SLL )                                      // forcing SLL only
					{
						// SPECIAL CASE WHERE SLL KNOWS CONFLICT IS AMBIGUITY
						if ( !D.configs.hasSemanticContext ) {
							reportAmbiguity(dfa, D, startIndex, input.index(),
											D.configs.conflictingAlts, D.configs);
						}
						D.isAcceptState = true;
						D.prediction = D.configs.conflictingAlts.nextSetBit(0);
						if ( debug ) System.out.println("RESOLVED TO "+D.prediction+" for "+D);
						predictedAlt = D.prediction;
						// Falls through to check predicates below
					}
					else {
						// SLL CONFLICT; RETRY WITH FULL LL CONTEXT
						// (it's possible SLL with preds could resolve to single alt
						//  which would mean we could avoid full LL, but not worth
						//  code complexity.)
						if ( debug ) System.out.println("RETRY with outerContext="+outerContext);
						// don't look up context in cache now since we're just creating state D
						ATNConfigSet s0_closure =
							computeStartState(dfa.atnStartState,
											  outerContext,
											  true);
						predictedAlt = execATNWithFullContext(dfa, D, s0_closure,
															  input, startIndex,
															  outerContext,
															  D.configs.conflictingAlts.nextSetBit(0));
						// not accept state: isCtxSensitive
						D.requiresFullContext = true; // always force DFA to ATN simulate
						D.prediction = ATN.INVALID_ALT_NUMBER;
						addDFAEdge(dfa, previousD, t, D);
						return predictedAlt; // all done with preds, etc...
					}
				}
			}

			if ( D.isAcceptState && D.configs.hasSemanticContext ) {
				// We need to test all predicates, even in DFA states that
				// uniquely predict alternative.
				int nalts = decState.getNumberOfTransitions();
				// Update DFA so reach becomes accept state with (predicate,alt)
				// pairs if preds found for conflicting alts
				BitSet altsToCollectPredsFrom = getConflictingAltsOrUniqueAlt(D.configs);
				SemanticContext[] altToPred = getPredsForAmbigAlts(altsToCollectPredsFrom, D.configs, nalts);
				if ( altToPred!=null ) {
					D.predicates = getPredicatePredictions(altsToCollectPredsFrom, altToPred);
					D.prediction = ATN.INVALID_ALT_NUMBER; // make sure we use preds
				}
				else {
					// There are preds in configs but they might go away
					// when OR'd together like {p}? || NONE == NONE. If neither
					// alt has preds, resolve to min alt
					D.prediction = altsToCollectPredsFrom.nextSetBit(0);
				}

				if ( D.predicates!=null ) {
					int stopIndex = input.index();
					input.seek(startIndex);
					BitSet alts = evalSemanticContext(D.predicates, outerContext, true);
					D.prediction = ATN.INVALID_ALT_NUMBER; // indicate we have preds
					addDFAEdge(dfa, previousD, t, D);
					switch (alts.cardinality()) {
					case 0:
						throw noViableAlt(input, outerContext, D.configs, startIndex);

					case 1:
						return alts.nextSetBit(0);

					default:
						// report ambiguity after predicate evaluation to make sure the correct
						// set of ambig alts is reported.
						reportAmbiguity(dfa, D, startIndex, stopIndex, alts, D.configs);
						return alts.nextSetBit(0);
					}
				}
			}

			// all adds to dfa are done after we've created full D state
			addDFAEdge(dfa, previousD, t, D);
			if ( D.isAcceptState ) return predictedAlt;

			previous = reach;
			previousD = D;
			input.consume();
			t = input.LA(1);
		}
	}

	// comes back with reach.uniqueAlt set to a valid alt
	public int execATNWithFullContext(DFA dfa,
									  DFAState D, // how far we got before failing over
									  @NotNull ATNConfigSet s0,
									  @NotNull TokenStream input, int startIndex,
									  ParserRuleContext<?> outerContext,
									  int SLL_min_alt) // todo: is this in D as min ambig alts?
	{
		// caller must have write lock on dfa
		retry_with_context++;
		reportAttemptingFullContext(dfa, s0, startIndex, input.index());

		if ( debug || debug_list_atn_decisions ) {
			System.out.println("execATNWithFullContext "+s0);
		}
		boolean fullCtx = true;
		ATNConfigSet reach = null;
		ATNConfigSet previous = s0;
		input.seek(startIndex);
		int t = input.LA(1);
		while (true) { // while more work
//			System.out.println("LL REACH "+getLookaheadName(input)+
//							   " from configs.size="+previous.size()+
//							   " line "+input.LT(1).getLine()+":"+input.LT(1).getCharPositionInLine());
			reach = computeReachSet(previous, t, fullCtx);
			if ( reach==null ) {
				// if any configs in previous dipped into outer context, that
				// means that input up to t actually finished entry rule
				// at least for LL decision. Full LL doesn't dip into outer
				// so don't need special case.
				// We will get an error no matter what so delay until after
				// decision; better error message. Also, no reachable target
				// ATN states in SLL implies LL will also get nowhere.
				// If conflict in states that dip out, choose min since we
				// will get error no matter what.
				int alt = getAltThatFinishedDecisionEntryRule(previous);
				if ( alt!=ATN.INVALID_ALT_NUMBER ) {
					return alt;
				}
				throw noViableAlt(input, outerContext, previous, startIndex);
			}

			Collection<BitSet> altSubSets = getConflictingAltSubsets(reach);

			if ( debug ) {
				System.out.println("LL altSubSets="+altSubSets+
								   ", predict="+getUniqueAlt(altSubSets)+
								   ", resolvesToJustOneViableAlt="+
								   resolvesToJustOneViableAlt(altSubSets));
			}

//			System.out.println("altSubSets: "+altSubSets);
			reach.uniqueAlt = getUniqueAlt(altSubSets);
			if ( reach.uniqueAlt!=ATN.INVALID_ALT_NUMBER ) break;
			if ( resolvesToJustOneViableAlt(altSubSets) ) break;
			previous = reach;
			input.consume();
			t = input.LA(1);
		}

		if ( reach.uniqueAlt != ATN.INVALID_ALT_NUMBER ) {
			retry_with_context_indicates_no_conflict++;
			reportContextSensitivity(dfa, reach, startIndex, input.index());
			if ( reach.uniqueAlt == SLL_min_alt ) {
				retry_with_context_predicts_same_as_alt++;
			}
			return reach.uniqueAlt;
		}

		// We do not check predicates here because we have checked them
		// on-the-fly when doing full context prediction.

		// At this point, we know that we have conflicting configurations.
		// But, that does not mean that there is no way forward without
		// a conflict. It's possible to have nonconflicting alt subsets; e.g.,
		//
		//    LL altSubSets=[{1, 2}, {1, 2}, {1}, {1, 2}]
		//
		// from
		//
		//    [(17,1,[5 $]), (13,1,[5 10 $]), (21,1,[5 10 $]), (11,1,[$]),
		//     (13,2,[5 10 $]), (21,2,[5 10 $]), (11,2,[$])]
		//
		// In this case, (17,1,[5 $]) indicates there is some next sequence
		// that would resolve this without conflict to alternative 1. Any
		// other viable next sequence, however, is associated with a conflict.
		// We stop looking for input because no amount of further lookahead
		// will alter the fact that we should predict alternative 1.
		// We just can't say for sure that there is an ambiguity without
		// looking further.

		if ( /* TODO: len(all subsets)>1 or input consistent with a subset with len=1 */ true ) {
			reportAmbiguity(dfa, D, startIndex, input.index(), getConflictingAlts(reach), reach);
		}

		return getConflictingAlts(reach).nextSetBit(0);
	}

	protected ATNConfigSet computeReachSet(ATNConfigSet closure, int t,
										   boolean fullCtx)
	{
		if ( debug ) System.out.println("in computeReachSet, starting closure: " + closure);
		ATNConfigSet reach = new ATNConfigSet(fullCtx);
		Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
		ATNConfigSet intermediate = new ATNConfigSet(fullCtx);
		// First figure out where we can reach on input t
		for (ATNConfig c : closure) {
			if ( debug ) System.out.println("testing "+getTokenName(t)+" at "+c.toString());
			int n = c.state.getNumberOfTransitions();
			for (int ti=0; ti<n; ti++) {               // for each transition
				Transition trans = c.state.transition(ti);
				ATNState target = getReachableTarget(trans, t);
				if ( target!=null ) {
					intermediate.add(new ATNConfig(c, target), mergeCache);
				}
			}
		}
		// Now figure out where the closure can take us, but only if we'll
		// need to continue looking for more input.
		if ( intermediate.size()==1 ) {
			// Don't pursue the closure if there is just one state.
			// It can only have one alternative; just add to result
			// Also don't pursue the closure if there is unique alternative
			// among the configurations.
			reach = new ATNConfigSet(intermediate);
		}
		else if ( getUniqueAlt(intermediate)==1 ) {
			// Also don't pursue the closure if there is unique alternative
			// among the configurations.
			reach = new ATNConfigSet(intermediate);
		}
		else {
			for (ATNConfig c : intermediate) {
				closure(c, reach, closureBusy, false, fullCtx);
			}
		}

		if ( reach.size()==0 ) return null;
		return reach;
	}

	@NotNull
	public ATNConfigSet computeStartState(@NotNull ATNState p,
										  @Nullable RuleContext ctx,
										  boolean fullCtx)
	{
		// always at least the implicit call to start rule
		PredictionContext initialContext = PredictionContext.fromRuleContext(ctx);
		ATNConfigSet configs = new ATNConfigSet(fullCtx);

		for (int i=0; i<p.getNumberOfTransitions(); i++) {
			ATNState target = p.transition(i).target;
			ATNConfig c = new ATNConfig(target, i+1, initialContext);
			Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
			closure(c, configs, closureBusy, true, fullCtx);
		}

		return configs;
	}

	@Nullable
	public ATNState getReachableTarget(@NotNull Transition trans, int ttype) {
		switch (trans.getSerializationType()) {
		case Transition.ATOM:
			AtomTransition at = (AtomTransition)trans;
			if ( at.label == ttype ) {
				return at.target;
			}
			return null;

		case Transition.SET:
			SetTransition st = (SetTransition)trans;
			if ( st.set.contains(ttype) ) {
				return st.target;
			}
			return null;

		case Transition.NOT_SET:
			NotSetTransition nst = (NotSetTransition)trans;
			if ( !nst.set.contains(ttype) ) {
				return nst.target;
			}
			return null;

		case Transition.RANGE:
			RangeTransition rt = (RangeTransition)trans;
			if ( ttype>=rt.from && ttype<=rt.to ) {
				return rt.target;
			}
			return null;

		case Transition.WILDCARD:
			if (ttype != Token.EOF) {
				return trans.target;
			}
			return null;

		default:
			return null;
		}
	}

	public SemanticContext[] getPredsForAmbigAlts(@NotNull BitSet ambigAlts,
												  @NotNull ATNConfigSet configs,
												  int nalts)
	{
		// REACH=[1|1|[]|0:0, 1|2|[]|0:1]
		/* altToPred starts as an array of all null contexts. The entry at index i
		 * corresponds to alternative i. altToPred[i] may have one of three values:
		 *   1. null: no ATNConfig c is found such that c.alt==i
		 *   2. SemanticContext.NONE: At least one ATNConfig c exists such that
		 *      c.alt==i and c.semanticContext==SemanticContext.NONE. In other words,
		 *      alt i has at least one unpredicated config.
		 *   3. Non-NONE Semantic Context: There exists at least one, and for all
		 *      ATNConfig c such that c.alt==i, c.semanticContext!=SemanticContext.NONE.
		 *
		 * From this, it is clear that NONE||anything==NONE.
		 */
		SemanticContext[] altToPred = new SemanticContext[nalts + 1];
		for (ATNConfig c : configs) {
			if ( ambigAlts.get(c.alt) ) {
				altToPred[c.alt] = SemanticContext.or(altToPred[c.alt], c.semanticContext);
			}
		}

		int nPredAlts = 0;
		for (int i = 1; i <= nalts; i++) {
			if (altToPred[i] == null) {
				altToPred[i] = SemanticContext.NONE;
			}
			else if (altToPred[i] != SemanticContext.NONE) {
				nPredAlts++;
			}
		}

//		// Optimize away p||p and p&&p TODO: optimize() was a no-op
//		for (int i = 0; i < altToPred.length; i++) {
//			altToPred[i] = altToPred[i].optimize();
//		}

		// nonambig alts are null in altToPred
		if ( nPredAlts==0 ) altToPred = null;
		if ( debug ) System.out.println("getPredsForAmbigAlts result "+Arrays.toString(altToPred));
		return altToPred;
	}

	public List<DFAState.PredPrediction> getPredicatePredictions(BitSet ambigAlts,
																 SemanticContext[] altToPred)
	{
		List<DFAState.PredPrediction> pairs = new ArrayList<DFAState.PredPrediction>();
		boolean containsPredicate = false;
		for (int i = 1; i < altToPred.length; i++) {
			SemanticContext pred = altToPred[i];

			// unpredicated is indicated by SemanticContext.NONE
			assert pred != null;

			if (ambigAlts!=null && ambigAlts.get(i)) {
				pairs.add(new DFAState.PredPrediction(pred, i));
			}
			if ( pred!=SemanticContext.NONE ) containsPredicate = true;
		}

		if ( !containsPredicate ) {
			pairs = null;
		}

//		System.out.println(Arrays.toString(altToPred)+"->"+pairs);
		return pairs;
	}

	public int getAltThatFinishedDecisionEntryRule(ATNConfigSet configs) {
		IntervalSet alts = new IntervalSet();
		for (ATNConfig c : configs) {
			if ( c.reachesIntoOuterContext>0 ) alts.add(c.alt);
		}
		if ( alts.size()==0 ) return ATN.INVALID_ALT_NUMBER;
		return alts.getMinElement();
	}

	/** Look through a list of predicate/alt pairs, returning alts for the
	 *  pairs that win. A {@code NONE} predicate indicates an alt containing an
	 *  unpredicated config which behaves as "always true." If !complete
	 *  then we stop at the first predicate that evaluates to true. This
	 *  includes pairs with null predicates.
	 */
	public BitSet evalSemanticContext(List<DFAState.PredPrediction> predPredictions,
									  ParserRuleContext<?> outerContext,
									  boolean complete)
	{
		BitSet predictions = new BitSet();
		for (DFAState.PredPrediction pair : predPredictions) {
			if ( pair.pred==SemanticContext.NONE ) {
				predictions.set(pair.alt);
				if (!complete) {
					break;
				}
				continue;
			}

			boolean predicateEvaluationResult = pair.pred.eval(parser, outerContext);
			if ( debug || dfa_debug ) {
				System.out.println("eval pred "+pair+"="+predicateEvaluationResult);
			}

			if ( predicateEvaluationResult ) {
				if ( debug || dfa_debug ) System.out.println("PREDICT "+pair.alt);
				predictions.set(pair.alt);
				if (!complete) {
					break;
				}
			}
		}

		return predictions;
	}


	/* TODO: If we are doing predicates, there is no point in pursuing
		 closure operations if we reach a DFA state that uniquely predicts
		 alternative. We will not be caching that DFA state and it is a
		 waste to pursue the closure. Might have to advance when we do
		 ambig detection thought :(
		  */

	protected void closure(@NotNull ATNConfig config,
						   @NotNull ATNConfigSet configs,
						   @NotNull Set<ATNConfig> closureBusy,
						   boolean collectPredicates,
						   boolean fullCtx)
	{
		final int initialDepth = 0;
		closureCheckingStopStateAndLoopRecursion(config, configs, closureBusy, collectPredicates,
												 fullCtx,
												 initialDepth);
	}

	protected void closureCheckingStopStateAndLoopRecursion(@NotNull ATNConfig config,
															@NotNull ATNConfigSet configs,
															@NotNull Set<ATNConfig> closureBusy,
															boolean collectPredicates,
															boolean fullCtx,
															int depth)
	{
		if ( debug ) System.out.println("closure("+config.toString(parser,true)+")");

		if ( !closureBusy.add(config) ) return; // avoid infinite recursion

		if ( config.state instanceof RuleStopState ) {
			// We hit rule end. If we have context info, use it
			// run thru all possible stack tops in ctx
			if ( config.context!=null && !config.context.isEmpty() ) {
				for (SingletonPredictionContext ctx : config.context) {
					if ( ctx.invokingState==PredictionContext.EMPTY_FULL_CTX_INVOKING_STATE ) {
						// we have no context info, just chase follow links (if greedy)
						if ( debug ) System.out.println("FALLING off rule "+
														getRuleName(config.state.ruleIndex));
						closure_(config, configs, closureBusy, collectPredicates,
								 fullCtx, depth);
						continue;
					}
					ATNState invokingState = atn.states.get(ctx.invokingState);
					RuleTransition rt = (RuleTransition)invokingState.transition(0);
					ATNState retState = rt.followState;
					PredictionContext newContext = ctx.parent; // "pop" invoking state
					ATNConfig c = new ATNConfig(retState, config.alt, newContext,
												config.semanticContext);
					// While we have context to pop back from, we may have
					// gotten that context AFTER having falling off a rule.
					// Make sure we track that we are now out of context.
					c.reachesIntoOuterContext = config.reachesIntoOuterContext;
					assert depth > Integer.MIN_VALUE;
					closureCheckingStopStateAndLoopRecursion(c, configs, closureBusy, collectPredicates,
															 fullCtx, depth - 1);
				}
				return;
			}
			else {
				// else if we have no context info, just chase follow links (if greedy)
				if ( debug ) System.out.println("FALLING off rule "+
												getRuleName(config.state.ruleIndex));
			}
		}

		closure_(config, configs, closureBusy, collectPredicates,
				 fullCtx, depth);
	}

	/** Do the actual work of walking epsilon edges */
	protected void closure_(@NotNull ATNConfig config,
							@NotNull ATNConfigSet configs,
							@NotNull Set<ATNConfig> closureBusy,
							boolean collectPredicates,
							boolean fullCtx,
							int depth)
	{
		ATNState p = config.state;
		// optimization
		if ( !p.onlyHasEpsilonTransitions() ) {
            configs.add(config, mergeCache);
			if ( config.semanticContext!=null && config.semanticContext!= SemanticContext.NONE ) {
				configs.hasSemanticContext = true;
			}
			if ( config.reachesIntoOuterContext>0 ) {
				configs.dipsIntoOuterContext = true;
			}
//            if ( debug ) System.out.println("added config "+configs);
        }

		for (int i=0; i<p.getNumberOfTransitions(); i++) {
			Transition t = p.transition(i);
			boolean continueCollecting =
				!(t instanceof ActionTransition) && collectPredicates;
			ATNConfig c = getEpsilonTarget(config, t, continueCollecting,
										   depth == 0, fullCtx);
			if ( c!=null ) {
				int newDepth = depth;
				if ( config.state instanceof RuleStopState) {
					// target fell off end of rule; mark resulting c as having dipped into outer context
					// We can't get here if incoming config was rule stop and we had context
					// track how far we dip into outer context.  Might
					// come in handy and we avoid evaluating context dependent
					// preds if this is > 0.
					c.reachesIntoOuterContext++;
					configs.dipsIntoOuterContext = true; // TODO: can remove? only care when we add to set per middle of this method
					assert newDepth > Integer.MIN_VALUE;
					newDepth--;
					if ( debug ) System.out.println("dips into outer ctx: "+c);
				}
				else if (t instanceof RuleTransition) {
					// latch when newDepth goes negative - once we step out of the entry context we can't return
					if (newDepth >= 0) {
						newDepth++;
					}
				}

				closureCheckingStopStateAndLoopRecursion(c, configs, closureBusy, continueCollecting,
														 fullCtx, newDepth);
			}
		}
	}

	@NotNull
	public String getRuleName(int index) {
		if ( parser!=null && index>=0 ) return parser.getRuleNames()[index];
		return "<rule "+index+">";
	}

	@Nullable
	public ATNConfig getEpsilonTarget(@NotNull ATNConfig config,
									  @NotNull Transition t,
									  boolean collectPredicates,
									  boolean inContext,
									  boolean fullCtx)
	{
		switch (t.getSerializationType()) {
		case Transition.RULE:
			return ruleTransition(config, t);

		case Transition.PREDICATE:
			return predTransition(config, (PredicateTransition)t,
								  collectPredicates,
								  inContext,
								  fullCtx);

		case Transition.ACTION:
			return actionTransition(config, (ActionTransition)t);

		case Transition.EPSILON:
			return new ATNConfig(config, t.target);

		default:
			return null;
		}
	}

	@NotNull
	public ATNConfig actionTransition(@NotNull ATNConfig config, @NotNull ActionTransition t) {
		if ( debug ) System.out.println("ACTION edge "+t.ruleIndex+":"+t.actionIndex);
		return new ATNConfig(config, t.target);
	}

	@Nullable
	public ATNConfig predTransition(@NotNull ATNConfig config,
									@NotNull PredicateTransition pt,
									boolean collectPredicates,
									boolean inContext,
									boolean fullCtx)
	{
		if ( debug ) {
			System.out.println("PRED (collectPredicates="+collectPredicates+") "+
                    pt.ruleIndex+":"+pt.predIndex+
					", ctx dependent="+pt.isCtxDependent);
			if ( parser != null ) {
                System.out.println("context surrounding pred is "+
                                   parser.getRuleInvocationStack());
            }
		}

		ATNConfig c = null;
		if ( collectPredicates &&
			 (!pt.isCtxDependent || (pt.isCtxDependent&&inContext)) )
		{
			if ( fullCtx ) {
				// In full context mode, we can evaluate predicates on-the-fly
				// during closure, which dramatically reduces the size of
				// the config sets. It also obviates the need to test predicates
				// later during conflict resolution.
				int currentPosition = _input.index();
				_input.seek(_startIndex);
				boolean predSucceeds = pt.getPredicate().eval(parser, _outerContext);
				_input.seek(currentPosition);
				if ( predSucceeds ) {
					c = new ATNConfig(config, pt.target); // no pred context
				}
			}
			else {
				SemanticContext newSemCtx =
					SemanticContext.and(config.semanticContext, pt.getPredicate());
				c = new ATNConfig(config, pt.target, newSemCtx);
			}
		}
		else {
			c = new ATNConfig(config, pt.target);
		}

		if ( debug ) System.out.println("config from pred transition="+c);
        return c;
	}

	@NotNull
	public ATNConfig ruleTransition(@NotNull ATNConfig config, @NotNull Transition t) {
		if ( debug ) {
			System.out.println("CALL rule "+getRuleName(t.target.ruleIndex)+
							   ", ctx="+config.context);
		}
		PredictionContext newContext =
			SingletonPredictionContext.create(config.context, config.state.stateNumber);
		return new ATNConfig(config, t.target, newContext);
	}

	/**
	 SLL prediction termination.

	 There are two cases: the usual combined SLL+LL parsing and
	 pure SLL parsing that has no fail over to full LL.

	 COMBINED SLL+LL PARSING

	 SLL can decide to give up any point, even immediately,
	 failing over to full LL.  To be as efficient as possible,
	 though, SLL only fails over when it's positive it can't get
	 anywhere on more lookahead without seeing a conflict.

	 Assuming combined SLL+LL parsing, an SLL confg set with only
	 conflicting subsets should failover to full LL, even if the
	 config sets don't resolve to the same alternative like {1,2}
	 and {3,4}.  If there is at least one nonconflicting set of
	 configs, SLL can continue with the hopes that more lookahead
	 will resolve via one of those nonconflicting configs.

	 Here's the prediction termination rule them: SLL (for SLL+LL
	 parsing) stops when it sees only conflicting config subsets.
	 In contrast, full LL keeps going when there is uncertainty.

	 PREDICATES IN SLL+LL PARSING

	 SLL does not evaluate predicates until after it reaches a DFA
	 stop state because it needs to create the DFA cache that
	 works in all (semantic) situations.  (In contrast, full LL
	 evaluates predicates collected during start state computation
	 so it can ignore predicates thereafter.) This means that SLL
	 termination can totally ignore semantic predicates.

	 Of course, implementation-wise, ATNConfigSets combine stack
	 contexts but not semantic predicate contexts so we might see
	 two configs like this:

	 (s, 1, x, {}), (s, 1, x', {p})

	 Before testing these configurations against others, we have
	 to merge x and x' (w/o modifying the existing configs). For
	 example, we test (x+x')==x'' when looking for conflicts in
	 the following configs.

	 (s, 1, x, {}), (s, 1, x', {p}), (s, 2, x'', {})

	 If the configuration set has predicates, which we can test
	 quickly, this algorithm can make a copy of the configs and
	 strip out all of the predicates so that a standard
	 ATNConfigSet will emerge everything ignoring
	 predicates.

	 CASES:

	 * no conflicts & > 1 alt in set => continue

	 * (s, 1, x), (s, 2, x), (s, 3, z)
	   (s', 1, y), (s', 2, y)
	   yields nonconflicting set {3} (and 2 conflicting)
	     => continue

	 * (s, 1, x), (s, 2, x),
	   (s', 1, y), (s', 2, y)
	   (s'', 1, z)
	   yields nonconflicting set {1} (and 2 conflicting)
	     => continue

	 * (s, 1, x), (s, 2, x),
	   (s', 1, y), (s', 2, y)
	   yields conflicting sets only
	     => stop and fail over to full LL

	 COMBINED SLL+LL PARSING

	 To handle pure SLL parsing, we can simply make the assumption
	 that an SLL conflict is a "real" full LL conflict. That way,
	 this algorithm can behave exactly like the full LL case
	 except for the presence of semantic predicates. But, we
	 ignore the semantic predicates anyway. So, we make a copy
	 stripped of semantic predicates and then call needMoreLookaheadSLL()
	 instead.

	 Pure SLL lookahead must continue like full LL if the union of
	 resolved alt sets from nonconflicting and conflicting subsets
	 has more than one alt. That means there's more than one
	 viable alternative (even in the presence of conflicts) and we
	 have to keep going since there is no fail over to a more
	 powerful algorithm.

	 The function implementation tries to bail out as soon as
	 possible. That means we can stop as soon as we see a nonconflicting
	 alt set (1 alt in it). We know to continue in that case.

	 // First, map (s,_,x,_) -> altset for all configs
	 for c in configs:
	   map[c] U= c.alt  # map hash/equals uses s and x, not	alt and	not pred
	 return (there exists len(m)==1 for some m in map.values)

	 or

	 boolean continue(configs):
	   for c in configs:
	     map[c] U= c.alt  # map hash/equals uses s and x, not alt and not pred
	   for altset in map.values:
	     if len(altset)==1: return true;
	   return false; # all sets conflict with len(viable_alts)>1, stop

	*/
	public boolean needMoreLookaheadSLL(@NotNull ATNConfigSet configs) {
		// pure SLL mode parsing
		if ( SLL ) {
			if ( configs.hasSemanticContext ) {
				// dup configs, tossing out semantic predicates
				ATNConfigSet dup = new ATNConfigSet();
				for (ATNConfig c : configs) {
					c = new ATNConfig(c,SemanticContext.NONE);
					dup.add(c);
				}
				configs = dup;
			}
			// do usual full LL termination test
			return needMoreLookaheadLL(configs);
		}
		// combined SLL+LL mode parsing
//		System.out.println("SLL configs: "+configs);
		// map (s,_,x,_) -> altset for all configs
		Collection<BitSet> altsets = getConflictingAltSubsets(configs);
//		System.out.println("SLL altsets: "+altsets);
		return hasNonConflictingAltSet(altsets);
	}

	public boolean allSubsetsConflict(Collection<BitSet> altsets) {
		return !hasNonConflictingAltSet(altsets);
	}

	/** return (there exists len(m)==1 for some m in altsets) */
	public boolean hasNonConflictingAltSet(Collection<BitSet> altsets) {
		for (BitSet alts : altsets) {
			if ( alts.cardinality()==1 ) { // more than 1 viable alt
//				System.out.println("SLL go; found nonconflicting alt: "+alts);
				return true; // use more lookahead
			}
		}
//		System.out.println("SLL stop");
		return false; // all sets conflict with len(viable_alts)>1, stop
	}

	public int getUniqueAlt(Collection<BitSet> altsets) {
		BitSet all = getAlts(altsets);
		if ( all.cardinality()==1 ) return all.nextSetBit(0);
		return ATN.INVALID_ALT_NUMBER;
	}

	public BitSet getAlts(Collection<BitSet> altsets) {
		BitSet all = new BitSet();
		for (BitSet alts : altsets) {
			all.or(alts);
		}
		return all;
	}

	/**
	 Full LL prediction termination.

	 Can we stop looking ahead during ATN simulation or is there some
	 uncertainty as to which alternative we will ultimately pick, after
	 consuming more input?  Even if there are partial conflicts, we might
	 know that everything is going to resolve to the same minimum
	 alt. That means we can stop since no more lookahead will change that
	 fact. On the other hand, there might be multiple conflicts that
	 resolve to different minimums.  That means we need more look ahead to
	 decide which of those alternatives we should predict.

	 The basic idea is to split the set of configurations down into a set
	 with all configs that don't conflict with each other and then
	 (possibly multiple) subsets of configurations that are mutually
	 conflicting. Then reduce each set of configurations to the set of
	 possible alternatives.  This is just the set of all alternatives
	 within the nonconflicting set. It is the minimum alt of each
	 conflicting subset. If the union of these alternatives sets is a
	 singleton, then no amount of more lookahead will help us. We will
	 always pick that alternative. If, however, there are more
	 alternatives then we are uncertain which alt to predict and must
	 continue looking for resolution. We may or may not discover an
	 ambiguity in the future, even if there are no conflicting subsets
	 this round.

	 The biggest sin is to terminate early because it means we've made a
	 decision but were uncertain as to the eventual outcome. We haven't
	 used enough lookahead.  On the other hand, announcing a conflict too
	 late is no big deal; you will still have the conflict. It's just
	 inefficient.

	 Semantic predicates for full LL aren't involved in this decision
	 because the predicates are evaluated during start state computation.
	 This set of configurations was derived from the initial subset with
	 configurations holding false predicate stripped out.

	 CONFLICTING CONFIGS

	 Two configurations, (s, i, x) and (s, j, x'), conflict when i!=j but
	 x = x'. Because we merge all (s, i, _) configurations together, that
	 means that there are at most n configurations associated with state s
	 for n possible alternatives in the decision. The merged stacks
	 complicate the comparison of config contexts, x and x'. Sam checks to
	 see if one is a subset of the other by calling merge and checking to
	 see if the merged result is either x or x'. If the x associated with
	 lowest alternative i is the superset, then i is the only possible
	 prediction since the others resolve to min i as well. If, however, x
	 is associated with j>i then at least one stack configuration for j is
	 not in conflict with alt i. The algorithm should keep going, looking
	 for more lookahead due to the uncertainty.

	 For simplicity, I'm doing a simple equality check between x and x'
	 that lets the algorithm continue to consume lookahead longer than
	 necessary. I will check to see if it is efficient enough. The reason
	 I like the simple equality is of course the simplicity but also
	 because that is the test you need to detect the alternatives that are
	 actually in conflict.  If all states report the same conflicting alt
	 set, then we know we have the real ambiguity set.

	 CONTINUE/STOP RULE

	 Ok, here's the decision to keep going: continue if union of resolved
	 alt sets from nonconflicting and conflicting subsets has more than
	 one alt. The complete set of alternatives, [i for (_,i,_)], tells us
	 which alternatives are still in the running for the amount of input
	 we've consumed at this point. The conflicting sets let us to strip
	 away configurations that won't lead to more states (because we
	 resolve conflicts to the configuration with a minimum alternate for
	 given conflicting set.)  The set of viable alternatives for a
	 configuration set is therefore a subset of the complete [i for
	 (_,i,_)] set.

	 CASES:

	 * no conflicts & > 1 alt in set => continue

	 * (s, 1, x), (s, 2, x), (s, 3, z)
	   (s', 1, y), (s', 2, y)
	   yields nonconflicting set {3} U conflicting sets {1} U {1} = {1,3}
	     => continue

	 * (s, 1, x), (s, 2, x),
	   (s', 1, y), (s', 2, y)
	   (s'', 1, z)
	   yields nonconflicting set you this {1} U conflicting sets {1} U {1} = {1}
	     => stop and predict 1, announce ambiguity {1,2}

	 * (s, 1, x), (s, 2, x),
	   (s', 1, y), (s', 2, y)
	   yields conflicting sets {1} U {1} = {1}
	     => stop and predict 1, announce ambiguity {1,2}

	 * (s, 1, x), (s, 2, x)
	   (s', 2, y), (s', 3, y)
	   yields conflicting sets {1} U {2} = {1,2}
	     => continue

	 * (s, 1, x), (s, 2, x)
	   (s', 3, y), (s', 4, y)
	   yields conflicting sets {1} U {3} = {1,3}
	     => continue

	 The function implementation tries to bail out as soon as
	 possible. That means we can stop as soon as our viable alt set gets
	 more than a single alternative in it.

	 // First, map (s,_,x,_) -> altset for all configs
	 for c in configs:
	   map[c] U= c.alt  # map hash/equals uses s and x, not	alt and	not pred
	 viable_alts = [min(m) for m in map.values]
	 continue if len(viable_alts)>1

	 or

	 boolean continue(configs):
	   for c in configs:
	     map[c] U= c.alt  # map hash/equals uses s and x, not alt and not pred
	   viable_alts = set()
	   for altset in map.values:
	     viable_alts.add(min(altset))
	     if len(viable_alts)>1: return true
	   return false; # len(viable_alts)==1, stop
	 */
	public boolean needMoreLookaheadLL(@NotNull ATNConfigSet configs) {
//		System.out.println("configs: "+configs);
		// map (s,_,x,_) -> altset for all configs
		Collection<BitSet> altsets = getConflictingAltSubsets(configs);
//		System.out.println("altsets: "+altsets);
		BitSet viableAlts = new BitSet();
		for (BitSet alts : altsets) {
			int minAlt = alts.nextSetBit(0);
			viableAlts.set(minAlt);
			if ( viableAlts.cardinality()>1 ) { // more than 1 viable alt
//				System.out.println("go; viableAlts="+viableAlts);
				return true; // try using more lookahead
			}
		}
//		System.out.println("stop");
		return false; // len(viable_alts)==1, stop
	}

	/** Get the conflicting alt subsets from a configuration set.
	 * for c in configs:
     *    map[c] U= c.alt  # map hash/equals uses s and x, not alt and not pred
	 */
	public Collection<BitSet> getConflictingAltSubsets(ATNConfigSet configs) {
		AltAndContextMap configToAlts = new AltAndContextMap();
		for (ATNConfig c : configs) {
			BitSet alts = configToAlts.get(c);
			if ( alts==null ) {
				alts = new BitSet();
				configToAlts.put(c, alts);
			}
			alts.set(c.alt);
		}
		return configToAlts.values();
	}

	public boolean resolvesToJustOneViableAlt(Collection<BitSet> altsets) {
		return !hasMoreThanOneViableAlt(altsets);
	}

	public boolean hasMoreThanOneViableAlt(Collection<BitSet> altsets) {
		BitSet viableAlts = new BitSet();
		for (BitSet alts : altsets) {
			int minAlt = alts.nextSetBit(0);
			viableAlts.set(minAlt);
			if ( viableAlts.cardinality()>1 ) { // more than 1 viable alt
				return true;
			}
		}
		return false;
	}

	public BitSet getConflictingAlts(ATNConfigSet configs) {
		Collection<BitSet> altsets = getConflictingAltSubsets(configs);
		return getAlts(altsets);
	}

	/**
	 * From grammar:

	 s' : s s ;
	 s : x? | x ;
	 x : 'a' ;

	 config list: (4,1), (11,1,4), (7,1), (3,1,1), (4,1,1), (8,1,1), (7,1,1),
	 (8,2), (11,2,8), (11,1,[8 1])

	 state to config list:

	 3  -> (3,1,1)
	 4  -> (4,1), (4,1,1)
	 7  -> (7,1), (7,1,1)
	 8  -> (8,1,1), (8,2)
	 11 -> (11,1,4), (11,2,8), (11,1,8 1)

	 Walk and find state config lists with > 1 alt. If none, no conflict. return null. Here, states 11
	 and 8 have lists with both alts 1 and 2. Must check these config lists for conflicting configs.

	 Sam pointed out a problem with the previous definition, v3, of
	 ambiguous states. If we have another state associated with conflicting
	 alternatives, we should keep going. For example, the following grammar

	 s : (ID | ID ID?) ';' ;

	 When the ATN simulation reaches the state before ';', it has a DFA
	 state that looks like: [12|1|[], 6|2|[], 12|2|[]]. Naturally
	 12|1|[] and 12|2|[] conflict, but we cannot stop processing this node
	 because alternative to has another way to continue, via [6|2|[]].
	 The key is that we have a single state that has config's only associated
	 with a single alternative, 2, and crucially the state transitions
	 among the configurations are all non-epsilon transitions. That means
	 we don't consider any conflicts that include alternative 2. So, we
	 ignore the conflict between alts 1 and 2. We ignore a set of
	 conflicting alts when there is an intersection with an alternative
	 associated with a single alt state in the state->config-list map.

	 It's also the case that we might have two conflicting configurations but
	 also a 3rd nonconflicting configuration for a different alternative:
	 [1|1|[], 1|2|[], 8|3|[]]. This can come about from grammar:

	 a : A | A | A B ;

	 After matching input A, we reach the stop state for rule A, state 1.
	 State 8 is the state right before B. Clearly alternatives 1 and 2
	 conflict and no amount of further lookahead will separate the two.
	 However, alternative 3 will be able to continue and so we do not
	 stop working on this state. In the previous example, we're concerned
	 with states associated with the conflicting alternatives. Here alt
	 3 is not associated with the conflicting configs, but since we can continue
	 looking for input reasonably, I don't declare the state done. We
	 ignore a set of conflicting alts when we have an alternative
	 that we still need to pursue.

	 So, in summary, as long as there is a single configuration that is
	 not conflicting with any other configuration for that state, then
	 there is more input we can use to keep going. E.g.,
	 s->[(s,1,[x]), (s,2,[x]), (s,2,[y])]
	 s->[(s,1,_)]
	 s->[(s,1,[y]), (s,2,[x])]
	 Regardless of what goes on for the other states, this is
	 sufficient to force us to add this new state to the ATN-to-DFA work list.

	 TODO: split into "has nonconflict config--add to work list" and getambigalts functions

	 TODO: now we know contexts are merged, can we optimize?  Use big int -> config array?
	 */
	@Nullable
	public IntervalSet getConflictingAlts_old(@NotNull ATNConfigSet configs) {
		if ( debug ) System.out.println("### check ambiguous  "+configs);
//		System.out.println("getConflictingAlts; set size="+configs.size());
		// First get a list of configurations for each state.
		// Most of the time, each state will have one associated configuration.
		MultiMap<Integer, ATNConfig> stateToConfigListMap = new MultiMap<Integer, ATNConfig>();
		Map<Integer, IntervalSet> stateToAltListMap = new HashMap<Integer, IntervalSet>();

		for (ATNConfig c : configs) {
			stateToConfigListMap.map(c.state.stateNumber, c);
			IntervalSet alts = stateToAltListMap.get(c.state.stateNumber);
			if ( alts==null ) {
				alts = new IntervalSet();
				stateToAltListMap.put(c.state.stateNumber, alts);
			}
			alts.add(c.alt);
		}
		// potential conflicts are states, s, with > 1 configurations and diff alts
		// find all alts with potential conflicts
		int numPotentialConflicts = 0;
		IntervalSet altsToIgnore = new IntervalSet();
		for (int state : stateToConfigListMap.keySet()) { // for each state
			IntervalSet alts = stateToAltListMap.get(state);
			if ( alts.size()==1 ) {
				if ( !atn.states.get(state).onlyHasEpsilonTransitions() ) {
					List<ATNConfig> configsPerState = stateToConfigListMap.get(state);
					ATNConfig anyConfig = configsPerState.get(0);
					altsToIgnore.add(anyConfig.alt);
					if ( debug ) System.out.println("### one alt and all non-ep: "+configsPerState);
				}
				// remove state's configurations from further checking; no issues with them.
				// (can't remove as it's concurrent modification; set to null)
//				return null;
				stateToConfigListMap.put(state, null);
			}
			else {
				numPotentialConflicts++;
			}
		}

		if ( debug ) System.out.println("### altsToIgnore: "+altsToIgnore);
		if ( debug ) System.out.println("### stateToConfigListMap="+stateToConfigListMap);

		if ( numPotentialConflicts==0 ) {
			return null;
		}

		// compare each pair of configs in sets for states with > 1 alt in config list, looking for
		// (s, i, ctx) and (s, j, ctx') where ctx==ctx' or one is suffix of the other.
		IntervalSet ambigAlts = new IntervalSet();
		for (int state : stateToConfigListMap.keySet()) {
			List<ATNConfig> configsPerState = stateToConfigListMap.get(state);
			if (configsPerState == null) continue;
			IntervalSet alts = stateToAltListMap.get(state);
// Sam's correction to ambig def is here:
			if ( !altsToIgnore.isNil() && alts.and(altsToIgnore).size()<=1 ) {
//				System.err.println("ignoring alt since "+alts+"&"+altsToIgnore+
//								   ".size is "+alts.and(altsToIgnore).size());
				continue;
			}
			int size = configsPerState.size();
			for (int i = 0; i < size; i++) {
				ATNConfig c = configsPerState.get(i);
				for (int j = i+1; j < size; j++) {
					ATNConfig d = configsPerState.get(j);
					if ( c.alt != d.alt ) {
						boolean conflicting = c.context.equals(d.context);
						if ( conflicting ) {
							if ( debug ) {
								System.out.println("we reach state "+c.state.stateNumber+
												   " in rule "+
												   (parser !=null ? getRuleName(c.state.ruleIndex) :"n/a")+
												   " alts "+c.alt+","+d.alt+" from ctx "+c.context.toString(parser)
												   +" and "+ d.context.toString(parser));
							}
							ambigAlts.add(c.alt);
							ambigAlts.add(d.alt);
						}
					}
				}
			}
		}

		if ( debug ) System.out.println("### ambigAlts="+ambigAlts);

		if ( ambigAlts.isNil() ) return null;

		// are any configs not represented in ambig alt sets
//		for (ATNConfig config : configs) {
//			if (!ambigAlts.contains(config.alt)) {
//				return null;
//			}
//		}
		return ambigAlts;
	}

	protected BitSet getConflictingAltsOrUniqueAlt(ATNConfigSet configs) {
		BitSet conflictingAlts;
		if ( configs.uniqueAlt!= ATN.INVALID_ALT_NUMBER ) {
			conflictingAlts = new BitSet();
			conflictingAlts.set(configs.uniqueAlt);
		}
		else {
			conflictingAlts = configs.conflictingAlts;
		}
		return conflictingAlts;
	}

//	protected int resolveToMinAlt(@NotNull DFAState D, IntervalSet conflictingAlts) {
//		// kill dead alts so we don't chase them ever
////		killAlts(conflictingAlts, D.configset);
//		D.prediction = conflictingAlts.getMinElement();
//		if ( debug ) System.out.println("RESOLVED TO "+D.prediction+" for "+D);
//		return D.prediction;
//	}

	protected int resolveNongreedyToExitBranch(@NotNull ATNConfigSet reach,
											   @NotNull IntervalSet conflictingAlts)
	{
		// exit branch is alt 2 always; alt 1 is entry or loopback branch
		// since we're predicting, create DFA accept state for exit alt
		int exitAlt = 2;
		conflictingAlts.remove(exitAlt);
		// kill dead alts so we don't chase them ever
//		killAlts(conflictingAlts, reach);
		if ( debug ) System.out.println("RESOLVED TO "+reach);
		return exitAlt;
	}

	@NotNull
	public String getTokenName(int t) {
		if ( t==Token.EOF ) return "EOF";
		if ( parser!=null && parser.getTokenNames()!=null ) {
			String[] tokensNames = parser.getTokenNames();
			if ( t>=tokensNames.length ) {
				System.err.println(t+" ttype out of range: "+ Arrays.toString(tokensNames));
				System.err.println(((CommonTokenStream)parser.getInputStream()).getTokens());
			}
			else {
				return tokensNames[t]+"<"+t+">";
			}
		}
		return String.valueOf(t);
	}

	public String getLookaheadName(TokenStream input) {
		return getTokenName(input.LA(1));
	}

	public void dumpDeadEndConfigs(@NotNull NoViableAltException nvae) {
		System.err.println("dead end configs: ");
		for (ATNConfig c : nvae.deadEndConfigs) {
			String trans = "no edges";
			if ( c.state.getNumberOfTransitions()>0 ) {
				Transition t = c.state.transition(0);
				if ( t instanceof AtomTransition) {
					AtomTransition at = (AtomTransition)t;
					trans = "Atom "+getTokenName(at.label);
				}
				else if ( t instanceof SetTransition ) {
					SetTransition st = (SetTransition)t;
					boolean not = st instanceof NotSetTransition;
					trans = (not?"~":"")+"Set "+st.set.toString();
				}
			}
			System.err.println(c.toString(parser, true)+":"+trans);
		}
	}

	@NotNull
	public NoViableAltException noViableAlt(@NotNull TokenStream input,
											@NotNull ParserRuleContext<?> outerContext,
											@NotNull ATNConfigSet configs,
											int startIndex)
	{
		return new NoViableAltException(parser, input,
											input.get(startIndex),
											input.LT(1),
											configs, outerContext);
	}

	public static int getUniqueAlt(@NotNull ATNConfigSet configs) {
		int alt = ATN.INVALID_ALT_NUMBER;
		for (ATNConfig c : configs) {
			if ( alt == ATN.INVALID_ALT_NUMBER ) {
				alt = c.alt; // found first alt
			}
			else if ( c.alt!=alt ) {
				return ATN.INVALID_ALT_NUMBER;
			}
		}
		return alt;
	}

	@Nullable
	public boolean configWithAltAtStopState(@NotNull Collection<ATNConfig> configs, int alt) {
		for (ATNConfig c : configs) {
			if ( c.alt == alt ) {
				if ( c.state.getClass() == RuleStopState.class ) {
					return true;
				}
			}
		}
		return false;
	}

	protected void addDFAEdge(@NotNull DFA dfa,
							  @Nullable DFAState from,
							  int t,
							  @Nullable DFAState to)
	{
		if ( debug ) System.out.println("EDGE "+from+" -> "+to+" upon "+getTokenName(t));
		if ( from==null || t < -1 || to == null ) return;
		to = addDFAState(dfa, to); // used existing if possible not incoming
		synchronized (dfa) {
			if ( from.edges==null ) {
				from.edges = new DFAState[atn.maxTokenType+1+1]; // TODO: make adaptive
			}
			from.edges[t+1] = to; // connect
		}
		if ( debug ) System.out.println("DFA=\n"+dfa.toString(parser!=null?parser.getTokenNames():null));
	}

	/** Add D if not there and return D. Return previous if already present. */
	protected DFAState addDFAState(@NotNull DFA dfa, @NotNull DFAState D) {
		synchronized (dfa) {
			DFAState existing = dfa.states.get(D);
			if ( existing!=null ) return existing;

			D.stateNumber = dfa.states.size();
			synchronized (sharedContextCache) {
				D.configs.optimizeConfigs(this);
			}
			D.configs.setReadonly(true);
			dfa.states.put(D, D);
			if ( debug ) System.out.println("adding new DFA state: "+D);
			return D;
		}
	}

	public void reportAttemptingFullContext(DFA dfa, ATNConfigSet configs, int startIndex, int stopIndex) {
        if ( debug || retry_debug ) {
			Interval interval = Interval.of(startIndex, stopIndex);
			System.out.println("reportAttemptingFullContext decision="+dfa.decision+":"+configs+
                               ", input="+parser.getTokenStream().getText(interval));
        }
        if ( parser!=null ) parser.getErrorListenerDispatch().reportAttemptingFullContext(parser, dfa, startIndex, stopIndex, configs);
    }

	public void reportContextSensitivity(DFA dfa, ATNConfigSet configs, int startIndex, int stopIndex) {
        if ( debug || retry_debug ) {
			Interval interval = Interval.of(startIndex, stopIndex);
            System.out.println("reportContextSensitivity decision="+dfa.decision+":"+configs+
                               ", input="+parser.getTokenStream().getText(interval));
        }
        if ( parser!=null ) parser.getErrorListenerDispatch().reportContextSensitivity(parser, dfa, startIndex, stopIndex, configs);
    }

    /** If context sensitive parsing, we know it's ambiguity not conflict */
    public void reportAmbiguity(@NotNull DFA dfa, DFAState D, int startIndex, int stopIndex,
								@NotNull BitSet ambigAlts,
								@NotNull ATNConfigSet configs)
	{
		if ( debug || retry_debug ) {
//			ParserATNPathFinder finder = new ParserATNPathFinder(parser, atn);
//			int i = 1;
//			for (Transition t : dfa.atnStartState.transitions) {
//				System.out.println("ALT "+i+"=");
//				System.out.println(startIndex+".."+stopIndex+", len(input)="+parser.getInputStream().size());
//				TraceTree path = finder.trace(t.target, parser.getContext(), (TokenStream)parser.getInputStream(),
//											  startIndex, stopIndex);
//				if ( path!=null ) {
//					System.out.println("path = "+path.toStringTree());
//					for (TraceTree leaf : path.leaves) {
//						List<ATNState> states = path.getPathToNode(leaf);
//						System.out.println("states="+states);
//					}
//				}
//				i++;
//			}
			Interval interval = Interval.of(startIndex, stopIndex);
			System.out.println("reportAmbiguity "+
							   ambigAlts+":"+configs+
                               ", input="+parser.getTokenStream().getText(interval));
        }
        if ( parser!=null ) parser.getErrorListenerDispatch().reportAmbiguity(parser, dfa, startIndex, stopIndex,
																			  ambigAlts, configs);
    }

	public void setSLL(boolean SLL) {
		this.SLL = SLL;
	}
}
