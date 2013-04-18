/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.DoubleKeyMap;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
 alternative. If no alternatives are viable, we throw an exception.

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
	public static final boolean debug = false;
	public static final boolean debug_list_atn_decisions = false;
	public static final boolean dfa_debug = false;
	public static final boolean retry_debug = false;

	public static int ATN_failover = 0;
	public static int predict_calls = 0;
	public static int retry_with_context = 0;
	public static int retry_with_context_indicates_no_conflict = 0;
	public static int retry_with_context_predicts_same_alt = 0;
	public static int retry_with_context_from_dfa = 0;

	@Nullable
	protected final Parser parser;

	@NotNull
	public final DFA[] decisionToDFA;

	/** SLL, LL, or LL + exact ambig detection? */
	protected PredictionMode mode = PredictionMode.LL;

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
	protected ParserRuleContext _outerContext;

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
							   @Nullable ParserRuleContext outerContext)
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

		// Now we are certain to have a specific decision's DFA
		// But, do we still need an initial state?
		if ( dfa.s0==null ) {
			try {
				return predictATN(dfa, input, outerContext);
			}
			finally {
				mergeCache = null; // wack cache after each prediction
			}
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
						  @Nullable ParserRuleContext outerContext)
	{
		if ( outerContext==null ) outerContext = ParserRuleContext.EMPTY;
		if ( debug || debug_list_atn_decisions )  {
			System.out.println("predictATN decision "+dfa.decision+
							   " exec LA(1)=="+ getLookaheadName(input) +
							   ", outerContext="+outerContext.toString(parser));
		}
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
                       @Nullable ParserRuleContext outerContext)
    {
		if ( outerContext==null ) outerContext = ParserRuleContext.EMPTY;
		if ( dfa_debug ) {
			System.out.println("execDFA decision "+dfa.decision+
							   " exec LA(1)=="+ getLookaheadName(input) +
							   ", outerContext="+outerContext.toString(parser));
		}
		if ( dfa_debug ) System.out.print(dfa.toString(parser.getTokenNames()));
		DFAState acceptState = null;
		DFAState s = s0;

		int t = input.LA(1);
		while ( true ) {
			if ( dfa_debug ) System.out.println("DFA state "+s.stateNumber+" LA(1)=="+getLookaheadName(input));
			if ( s.requiresFullContext && mode != PredictionMode.SLL ) {
				// IF PREDS, MIGHT RESOLVE TO SINGLE ALT => SLL (or syntax error)
				if ( s.predicates!=null ) {
					if ( debug ) System.out.println("DFA state has preds in DFA sim LL failover");
					int conflictIndex = input.index();
					if (conflictIndex != startIndex) {
						input.seek(startIndex);
					}

					BitSet alts = evalSemanticContext(s.predicates, outerContext, true);
					if ( alts.cardinality()==1 ) {
						if ( debug ) System.out.println("Full LL avoided");
						return alts.nextSetBit(0);
					}

					if (conflictIndex != startIndex) {
						// restore the index so reporting the fallback to full
						// context occurs with the index at the correct spot
						input.seek(conflictIndex);
					}
				}

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

				alt = execATN(dfa, s, input, startIndex, outerContext);
				// this adds edge even if next state is accept for
				// same alt; e.g., s0-A->:s1=>2-B->:s2=>2
				// TODO: This next stuff kills edge, but extra states remain. :(
				if ( s.isAcceptState && alt!=ATN.INVALID_ALT_NUMBER ) {
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
			DFAState target = s.edges[t+1];
			if ( target == ERROR ) {
				throw noViableAlt(input, outerContext, s.configs, startIndex);
			}
			s = target;
			//TODO: can't be acceptstate here; rm that part of test?
			if (!s.requiresFullContext && !s.isAcceptState && t != IntStream.EOF) {
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

	 cover these cases:
	    dead end
	    single alt
	    single alt + preds
	    conflict
	    conflict + preds
	 */
	public int execATN(@NotNull DFA dfa, @NotNull DFAState s0,
					   @NotNull TokenStream input, int startIndex,
					   ParserRuleContext outerContext)
	{
		if ( debug || debug_list_atn_decisions) {
			System.out.println("execATN decision "+dfa.decision+
							   " exec LA(1)=="+ getLookaheadName(input)+
							   " line "+input.LT(1).getLine()+":"+input.LT(1).getCharPositionInLine());
		}
		ATN_failover++;

		ATNConfigSet previous = s0.configs;
		DFAState previousD = s0;

		if ( debug ) System.out.println("s0 = "+s0);

		int t = input.LA(1);

        DecisionState decState = atn.getDecisionState(dfa.decision);

		while (true) { // while more work
			DFAState D = null;
			if (previousD.edges != null && t + 1 >= 0 && t + 1 < previousD.edges.length) {
				D = previousD.edges[t + 1];
				if ( D == ERROR ) {
					throw noViableAlt(input, outerContext, previous, startIndex);
				}
			}

			if (D == null) {
	//			System.out.println("REACH "+getLookaheadName(input));
				ATNConfigSet reach = computeReachSet(previous, t, false);
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
				D = new DFAState(reach);

				int predictedAlt = getUniqueAlt(reach);

				if ( debug ) {
					Collection<BitSet> altSubSets = PredictionMode.getConflictingAltSubsets(reach);
					System.out.println("SLL altSubSets="+altSubSets+
									   ", configs="+reach+
									   ", predict="+predictedAlt+", allSubsetsConflict="+
										   PredictionMode.allSubsetsConflict(altSubSets)+", conflictingAlts="+
									   getConflictingAlts(reach));
				}

				if ( predictedAlt!=ATN.INVALID_ALT_NUMBER ) {
					// NO CONFLICT, UNIQUELY PREDICTED ALT
					D.isAcceptState = true;
					D.configs.uniqueAlt = predictedAlt;
					D.prediction = predictedAlt;
				}
				else if ( PredictionMode.hasSLLConflictTerminatingPrediction(mode, reach) ) {
					// MORE THAN ONE VIABLE ALTERNATIVE
					D.configs.conflictingAlts = getConflictingAlts(reach);
					if ( mode == PredictionMode.SLL ) {
						// stop w/o failover for sure
						if ( outerContext == ParserRuleContext.EMPTY || // in grammar start rule
							 !D.configs.dipsIntoOuterContext )          // didn't fall out of rule
						{
							// SPECIAL CASE WHERE SLL KNOWS CONFLICT IS AMBIGUITY
							// report even if preds
							reportAmbiguity(dfa, D, startIndex, input.index(),
											D.configs.conflictingAlts, D.configs);
						}
						// always stop at D
						D.isAcceptState = true;
						D.prediction = D.configs.conflictingAlts.nextSetBit(0);
						if ( debug ) System.out.println("SLL RESOLVED TO "+D.prediction+" for "+D);
						predictedAlt = D.prediction;
						// Falls through to check predicates below
					}
					else {
						// IF PREDS, MIGHT RESOLVE TO SINGLE ALT => SLL (or syntax error)
						if ( D.configs.hasSemanticContext ) {
							predicateDFAState(D, decState);
							if (D.predicates != null) {
								int conflictIndex = input.index();
								if (conflictIndex != startIndex) {
									input.seek(startIndex);
								}

								BitSet alts = evalSemanticContext(D.predicates, outerContext, true);
								if ( alts.cardinality()==1 ) {
									if ( debug ) System.out.println("Full LL avoided");
									return alts.nextSetBit(0);
								}

								if (conflictIndex != startIndex) {
									// restore the index so reporting the fallback to full
									// context occurs with the index at the correct spot
									input.seek(conflictIndex);
								}
							}
						}

						// RETRY WITH FULL LL CONTEXT
						if ( debug ) System.out.println("RETRY with outerContext="+outerContext);
						ATNConfigSet s0_closure =
							computeStartState(dfa.atnStartState,
											  outerContext,
											  true);
						predictedAlt = execATNWithFullContext(dfa, D, s0_closure,
															  input, startIndex,
															  outerContext,
															  D.configs.conflictingAlts.nextSetBit(0));
						// TODO: if true conflict found and same answer as we got with SLL,
						// then make it non ctx sensitive DFA state

						// not accept state: isCtxSensitive
						D.requiresFullContext = true; // always force DFA to ATN simulate
						D.prediction = ATN.INVALID_ALT_NUMBER;
						addDFAEdge(dfa, previousD, t, D);
						return predictedAlt; // all done with preds, etc...
					}
				}

				if ( D.isAcceptState && D.configs.hasSemanticContext ) {
					predicateDFAState(D, decState);
					if (D.predicates != null) {
						D.prediction = ATN.INVALID_ALT_NUMBER;
					}
				}

				// all adds to dfa are done after we've created full D state
				D = addDFAEdge(dfa, previousD, t, D);
			}
			else if ( D.requiresFullContext && mode != PredictionMode.SLL ) {
				// TODO: copied from execDFA, could be simplified

				// IF PREDS, MIGHT RESOLVE TO SINGLE ALT => SLL (or syntax error)
				if ( D.predicates!=null ) {
					if ( debug ) System.out.println("DFA state has preds in DFA sim LL failover");
					int conflictIndex = input.index();
					if (conflictIndex != startIndex) {
						input.seek(startIndex);
					}

					BitSet alts = evalSemanticContext(D.predicates, outerContext, true);
					if ( alts.cardinality()==1 ) {
						if ( debug ) System.out.println("Full LL avoided");
						return alts.nextSetBit(0);
					}

					if (conflictIndex != startIndex) {
						// restore the index so reporting the fallback to full
						// context occurs with the index at the correct spot
						input.seek(conflictIndex);
					}
				}

				if ( dfa_debug ) System.out.println("ctx sensitive state "+outerContext+" in "+D);
				boolean fullCtx = true;
				ATNConfigSet s0_closure =
					computeStartState(dfa.atnStartState, outerContext,
									  fullCtx);
				int alt = execATNWithFullContext(dfa, D, s0_closure,
												 input, startIndex,
												 outerContext,
												 ATN.INVALID_ALT_NUMBER);
				return alt;
			}

			if ( D.isAcceptState ) {
				if (D.predicates == null) {
					return D.prediction;
				}

				int stopIndex = input.index();
				input.seek(startIndex);
				BitSet alts = evalSemanticContext(D.predicates, outerContext, true);
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

			previous = D.configs;
			previousD = D;

			if (t != IntStream.EOF) {
				input.consume();
				t = input.LA(1);
			}
		}
	}

	protected void predicateDFAState(DFAState dfaState, DecisionState decisionState) {
		// We need to test all predicates, even in DFA states that
		// uniquely predict alternative.
		int nalts = decisionState.getNumberOfTransitions();
		// Update DFA so reach becomes accept state with (predicate,alt)
		// pairs if preds found for conflicting alts
		BitSet altsToCollectPredsFrom = getConflictingAltsOrUniqueAlt(dfaState.configs);
		SemanticContext[] altToPred = getPredsForAmbigAlts(altsToCollectPredsFrom, dfaState.configs, nalts);
		if ( altToPred!=null ) {
			dfaState.predicates = getPredicatePredictions(altsToCollectPredsFrom, altToPred);
			dfaState.prediction = ATN.INVALID_ALT_NUMBER; // make sure we use preds
		}
		else {
			// There are preds in configs but they might go away
			// when OR'd together like {p}? || NONE == NONE. If neither
			// alt has preds, resolve to min alt
			dfaState.prediction = altsToCollectPredsFrom.nextSetBit(0);
		}
	}

	// comes back with reach.uniqueAlt set to a valid alt
	public int execATNWithFullContext(DFA dfa,
									  DFAState D, // how far we got before failing over
									  @NotNull ATNConfigSet s0,
									  @NotNull TokenStream input, int startIndex,
									  ParserRuleContext outerContext,
									  int SLL_min_alt) // todo: is this in D as min ambig alts?
	{
		retry_with_context++;
		reportAttemptingFullContext(dfa, s0, startIndex, input.index());

		if ( debug || debug_list_atn_decisions ) {
			System.out.println("execATNWithFullContext "+s0);
		}
		boolean fullCtx = true;
		boolean foundExactAmbig = false;
		ATNConfigSet reach = null;
		ATNConfigSet previous = s0;
		input.seek(startIndex);
		int t = input.LA(1);
		int predictedAlt;
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

			Collection<BitSet> altSubSets = PredictionMode.getConflictingAltSubsets(reach);
			if ( debug ) {
				System.out.println("LL altSubSets="+altSubSets+
								   ", predict="+PredictionMode.getUniqueAlt(altSubSets)+
								   ", resolvesToJustOneViableAlt="+
									   PredictionMode.resolvesToJustOneViableAlt(altSubSets));
			}

//			System.out.println("altSubSets: "+altSubSets);
			reach.uniqueAlt = getUniqueAlt(reach);
			// unique prediction?
			if ( reach.uniqueAlt!=ATN.INVALID_ALT_NUMBER ) {
				predictedAlt = reach.uniqueAlt;
				break;
			}
			if ( mode != PredictionMode.LL_EXACT_AMBIG_DETECTION ) {
				predictedAlt = PredictionMode.resolvesToJustOneViableAlt(altSubSets);
				if ( predictedAlt != ATN.INVALID_ALT_NUMBER ) {
					break;
				}
			}
			else {
				// In exact ambiguity mode, we never try to terminate early.
				// Just keeps scarfing until we know what the conflict is
				if ( PredictionMode.allSubsetsConflict(altSubSets) &&
					 PredictionMode.allSubsetsEqual(altSubSets) )
				{
					foundExactAmbig = true;
					predictedAlt = PredictionMode.getSingleViableAlt(altSubSets);
					break;
				}
				// else there are multiple non-conflicting subsets or
				// we're not sure what the ambiguity is yet.
				// So, keep going.
			}

			previous = reach;
			if (t != IntStream.EOF) {
				input.consume();
				t = input.LA(1);
			}
		}

		// If the configuration set uniquely predicts an alternative,
		// without conflict, then we know that it's a full LL decision
		// not SLL.
		if ( reach.uniqueAlt != ATN.INVALID_ALT_NUMBER ) {
			retry_with_context_indicates_no_conflict++;
			reportContextSensitivity(dfa, reach, startIndex, input.index());
			if ( predictedAlt == SLL_min_alt ) {
				retry_with_context_predicts_same_alt++;
			}
			return predictedAlt;
		}

		// We do not check predicates here because we have checked them
		// on-the-fly when doing full context prediction.

		/*
		In non-exact ambiguity detection mode, we might	actually be able to
		detect an exact ambiguity, but I'm not going to spend the cycles
		needed to check. We only emit ambiguity warnings in exact ambiguity
		mode.

		For example, we might know that we have conflicting configurations.
		But, that does not mean that there is no way forward without a
		conflict. It's possible to have nonconflicting alt subsets as in:

		   LL altSubSets=[{1, 2}, {1, 2}, {1}, {1, 2}]

		from

		   [(17,1,[5 $]), (13,1,[5 10 $]), (21,1,[5 10 $]), (11,1,[$]),
			(13,2,[5 10 $]), (21,2,[5 10 $]), (11,2,[$])]

		In this case, (17,1,[5 $]) indicates there is some next sequence that
		would resolve this without conflict to alternative 1. Any other viable
		next sequence, however, is associated with a conflict.  We stop
		looking for input because no amount of further lookahead will alter
		the fact that we should predict alternative 1.  We just can't say for
		sure that there is an ambiguity without looking further.
		*/
		if ( foundExactAmbig ) {
			reportAmbiguity(dfa, D, startIndex, input.index(), getConflictingAlts(reach), reach);
		}

		return predictedAlt;
	}

	protected ATNConfigSet computeReachSet(ATNConfigSet closure, int t,
										   boolean fullCtx)
	{
		if ( debug ) System.out.println("in computeReachSet, starting closure: " + closure);
		ATNConfigSet intermediate = new ATNConfigSet(fullCtx);

		/* Configurations already in a rule stop state indicate reaching the end
		 * of the decision rule (local context) or end of the start rule (full
		 * context). Once reached, these configurations are never updated by a
		 * closure operation, so they are handled separately for the performance
		 * advantage of having a smaller intermediate set when calling closure.
		 *
		 * For full-context reach operations, separate handling is required to
		 * ensure that the alternative matching the longest overall sequence is
		 * chosen when multiple such configurations can match the input.
		 */
		List<ATNConfig> skippedStopStates = null;

		// First figure out where we can reach on input t
		for (ATNConfig c : closure) {
			if ( debug ) System.out.println("testing "+getTokenName(t)+" at "+c.toString());

			if (c.state instanceof RuleStopState) {
				assert c.context.isEmpty();
				if (fullCtx || t == IntStream.EOF) {
					if (skippedStopStates == null) {
						skippedStopStates = new ArrayList<ATNConfig>();
					}

					skippedStopStates.add(c);
				}

				continue;
			}

			int n = c.state.getNumberOfTransitions();
			for (int ti=0; ti<n; ti++) {               // for each transition
				Transition trans = c.state.transition(ti);
				ATNState target = getReachableTarget(trans, t);
				if ( target!=null ) {
					intermediate.add(new ATNConfig(c, target), mergeCache);
				}
			}
		}

		// Now figure out where the reach operation can take us...

		ATNConfigSet reach = null;

		/* This block optimizes the reach operation for intermediate sets which
		 * trivially indicate a termination state for the overall
		 * adaptivePredict operation.
		 *
		 * The conditions assume that intermediate
		 * contains all configurations relevant to the reach set, but this
		 * condition is not true when one or more configurations have been
		 * withheld in skippedStopStates.
		 */
		if (skippedStopStates == null) {
			if ( intermediate.size()==1 ) {
				// Don't pursue the closure if there is just one state.
				// It can only have one alternative; just add to result
				// Also don't pursue the closure if there is unique alternative
				// among the configurations.
				reach = intermediate;
			}
			else if ( getUniqueAlt(intermediate)!=ATN.INVALID_ALT_NUMBER ) {
				// Also don't pursue the closure if there is unique alternative
				// among the configurations.
				reach = intermediate;
			}
		}

		/* If the reach set could not be trivially determined, perform a closure
		 * operation on the intermediate set to compute its initial value.
		 */
		if (reach == null) {
			reach = new ATNConfigSet(fullCtx);
			Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
			for (ATNConfig c : intermediate) {
				closure(c, reach, closureBusy, false, fullCtx);
			}
		}

		if (t == IntStream.EOF) {
			/* After consuming EOF no additional input is possible, so we are
			 * only interested in configurations which reached the end of the
			 * decision rule (local context) or end of the start rule (full
			 * context). Update reach to contain only these configurations. This
			 * handles both explicit EOF transitions in the grammar and implicit
			 * EOF transitions following the end of the decision or start rule.
			 *
			 * When reach==intermediate, no closure operation was performed. In
			 * this case, removeAllConfigsNotInRuleStopState needs to check for
			 * reachable rule stop states as well as configurations already in
			 * a rule stop state.
			 *
			 * This is handled before the configurations in skippedStopStates,
			 * because any configurations potentially added from that list are
			 * already guaranteed to meet this condition whether or not it's
			 * required.
			 */
			reach = removeAllConfigsNotInRuleStopState(reach, reach == intermediate);
		}

		/* If skippedStopStates is not null, then it contains at least one
		 * configuration. For full-context reach operations, these
		 * configurations reached the end of the start rule, in which case we
		 * only add them back to reach if no configuration during the current
		 * closure operation reached such a state. This ensures adaptivePredict
		 * chooses an alternative matching the longest overall sequence when
		 * multiple alternatives are viable.
		 */
		if (skippedStopStates != null && (!fullCtx || !PredictionMode.hasConfigInRuleStopState(reach))) {
			assert !skippedStopStates.isEmpty();
			for (ATNConfig c : skippedStopStates) {
				reach.add(c, mergeCache);
			}
		}

		if ( reach.isEmpty() ) return null;
		return reach;
	}

	/**
	 * Return a configuration set containing only the configurations from
	 * {@code configs} which are in a {@link RuleStopState}. If all
	 * configurations in {@code configs} are already in a rule stop state, this
	 * method simply returns {@code configs}.
	 * <p/>
	 * When {@code lookToEndOfRule} is true, this method uses
	 * {@link ATN#nextTokens} for each configuration in {@code configs} which is
	 * not already in a rule stop state to see if a rule stop state is reachable
	 * from the configuration via epsilon-only transitions.
	 *
	 * @param configs the configuration set to update
	 * @param lookToEndOfRule when true, this method checks for rule stop states
	 * reachable by epsilon-only transitions from each configuration in
	 * {@code configs}.
	 *
	 * @return {@code configs} if all configurations in {@code configs} are in a
	 * rule stop state, otherwise return a new configuration set containing only
	 * the configurations from {@code configs} which are in a rule stop state
	 */
	@NotNull
	protected ATNConfigSet removeAllConfigsNotInRuleStopState(@NotNull ATNConfigSet configs, boolean lookToEndOfRule) {
		if (PredictionMode.allConfigsInRuleStopStates(configs)) {
			return configs;
		}

		ATNConfigSet result = new ATNConfigSet(configs.fullCtx);
		for (ATNConfig config : configs) {
			if (config.state instanceof RuleStopState) {
				result.add(config, mergeCache);
				continue;
			}

			if (lookToEndOfRule && config.state.onlyHasEpsilonTransitions()) {
				IntervalSet nextTokens = atn.nextTokens(config.state);
				if (nextTokens.contains(Token.EPSILON)) {
					ATNState endOfRuleState = atn.ruleToStopState[config.state.ruleIndex];
					result.add(new ATNConfig(config, endOfRuleState), mergeCache);
				}
			}
		}

		return result;
	}

	@NotNull
	public ATNConfigSet computeStartState(@NotNull ATNState p,
										  @Nullable RuleContext ctx,
										  boolean fullCtx)
	{
		// always at least the implicit call to start rule
		PredictionContext initialContext = PredictionContext.fromRuleContext(atn, ctx);
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
		if (trans.matches(ttype, 0, atn.maxTokenType)) {
			return trans.target;
		}

		return null;
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

	public DFAState.PredPrediction[] getPredicatePredictions(BitSet ambigAlts,
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
		return pairs.toArray(new DFAState.PredPrediction[pairs.size()]);
	}

	public int getAltThatFinishedDecisionEntryRule(ATNConfigSet configs) {
		IntervalSet alts = new IntervalSet();
		for (ATNConfig c : configs) {
			if ( c.reachesIntoOuterContext>0 || (c.state instanceof RuleStopState && c.context.hasEmptyPath()) ) {
				alts.add(c.alt);
			}
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
	public BitSet evalSemanticContext(@NotNull DFAState.PredPrediction[] predPredictions,
									  ParserRuleContext outerContext,
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
		closureCheckingStopState(config, configs, closureBusy, collectPredicates,
								 fullCtx,
								 initialDepth);
		assert !fullCtx || !configs.dipsIntoOuterContext;
	}

	protected void closureCheckingStopState(@NotNull ATNConfig config,
											@NotNull ATNConfigSet configs,
											@NotNull Set<ATNConfig> closureBusy,
											boolean collectPredicates,
											boolean fullCtx,
											int depth)
	{
		if ( debug ) System.out.println("closure("+config.toString(parser,true)+")");

		if ( depth != 0 && !closureBusy.add(config) ) return; // avoid infinite recursion

		if ( config.state instanceof RuleStopState ) {
			// We hit rule end. If we have context info, use it
			// run thru all possible stack tops in ctx
			if ( !config.context.isEmpty() ) {
				for (SingletonPredictionContext ctx : config.context) {
					if ( ctx.returnState==PredictionContext.EMPTY_RETURN_STATE ) {
						if (fullCtx) {
							configs.add(new ATNConfig(config, config.state, PredictionContext.EMPTY), mergeCache);
							continue;
						}
						else {
							// we have no context info, just chase follow links (if greedy)
							if ( debug ) System.out.println("FALLING off rule "+
															getRuleName(config.state.ruleIndex));
							closure_(config, configs, closureBusy, collectPredicates,
									 fullCtx, depth);
						}
						continue;
					}
					ATNState returnState = atn.states.get(ctx.returnState);
					PredictionContext newContext = ctx.parent; // "pop" return state
					ATNConfig c = new ATNConfig(returnState, config.alt, newContext,
												config.semanticContext);
					// While we have context to pop back from, we may have
					// gotten that context AFTER having falling off a rule.
					// Make sure we track that we are now out of context.
					c.reachesIntoOuterContext = config.reachesIntoOuterContext;
					assert depth > Integer.MIN_VALUE;
					closureCheckingStopState(c, configs, closureBusy, collectPredicates,
											 fullCtx, depth - 1);
				}
				return;
			}
			else if (fullCtx) {
				// reached end of start rule
				configs.add(config, mergeCache);
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
					assert !fullCtx;
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

				closureCheckingStopState(c, configs, closureBusy, continueCollecting,
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
			return ruleTransition(config, (RuleTransition)t);

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
	public ATNConfig ruleTransition(@NotNull ATNConfig config, @NotNull RuleTransition t) {
		if ( debug ) {
			System.out.println("CALL rule "+getRuleName(t.target.ruleIndex)+
							   ", ctx="+config.context);
		}

		ATNState returnState = t.followState;
		PredictionContext newContext =
			SingletonPredictionContext.create(config.context, returnState.stateNumber);
		return new ATNConfig(config, t.target, newContext);
	}

	public BitSet getConflictingAlts(ATNConfigSet configs) {
		Collection<BitSet> altsets = PredictionMode.getConflictingAltSubsets(configs);
		return PredictionMode.getAlts(altsets);
	}

	/**
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
	 */

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
		for (ATNConfig c : nvae.getDeadEndConfigs()) {
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
											@NotNull ParserRuleContext outerContext,
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

	protected DFAState addDFAEdge(@NotNull DFA dfa,
								  @Nullable DFAState from,
								  int t,
								  @Nullable DFAState to)
	{
		if ( debug ) System.out.println("EDGE "+from+" -> "+to+" upon "+getTokenName(t));
		if ( from==null || t < -1 || to == null ) return to;
		to = addDFAState(dfa, to); // used existing if possible not incoming
		synchronized (from) {
			if ( from.edges==null ) {
				from.edges = new DFAState[atn.maxTokenType+1+1]; // TODO: make adaptive
			}
			from.edges[t+1] = to; // connect
		}
		if ( debug ) System.out.println("DFA=\n"+dfa.toString(parser!=null?parser.getTokenNames():null));
		return to;
	}

	/** Add D if not there and return D. Return previous if already present. */
	protected DFAState addDFAState(@NotNull DFA dfa, @NotNull DFAState D) {
		synchronized (dfa.states) {
			DFAState existing = dfa.states.get(D);
			if ( existing!=null ) return existing;

			D.stateNumber = dfa.states.size();
			if (!D.configs.isReadonly()) {
				D.configs.optimizeConfigs(this);
				D.configs.setReadonly(true);
			}
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

	public void setPredictionMode(PredictionMode mode) {
		this.mode = mode;
	}

	public PredictionMode getPredictionMode() { return mode; }
}
