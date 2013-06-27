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
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.Tuple;

import org.antlr.v4.runtime.misc.Tuple2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 The embodiment of the adaptive LL(*) parsing strategy.

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
 contexts. We avoid using context not necessarily because it
 slower, although it can be, but because of the DFA caching
 problem.  The closure routine only considers the rule invocation
 stack created during prediction beginning in the entry rule.  For
 example, if prediction occurs without invoking another rule's
 ATN, there are no context stacks in the configurations. When this
 leads to a conflict, we don't know if it's an ambiguity or a
 weakness in the strong LL(*) parsing strategy (versus full
 LL(*)).

 So, we simply retry the ATN simulation again, this time
 using full outer context and filling a dummy DFA (to avoid
 polluting the context insensitive DFA). Configuration context
 stacks will be the full invocation stack from the start rule. If
 we get a conflict using full context, then we can definitively
 say we have a true ambiguity for that input sequence. If we don't
 get a conflict, it implies that the decision is sensitive to the
 outer context. (It is not context-sensitive in the sense of
 context sensitive grammars.) We create a special DFA accept state
 that maps rule context to a predicted alternative. That is the
 only modification needed to handle full LL(*) prediction. In
 general, full context prediction will use more lookahead than
 necessary, but it pays to share the same DFA. For a schedule
 proof that full context prediction uses that most the same amount
 of lookahead as a context insensitive prediction, see the comment
 on method retryWithContext().

 So, the strategy is complex because we bounce back and forth from
 the ATN to the DFA, simultaneously performing predictions and
 extending the DFA according to previously unseen input
 sequences. The retry with full context is a recursive call to the
 same function naturally because it does the same thing, just with
 a different initial context. The problem is, that we need to pass
 in a "full context mode" parameter so that it knows to report
 conflicts differently. It also knows not to do a retry, to avoid
 infinite recursion, if it is already using full context.

 Retry a simulation using full outer context.
	 *
	 *  One of the key assumptions here is that using full context
	 *  can use at most the same amount of input as a simulation
	 *  that is not useful context (i.e., it uses all possible contexts
	 *  that could invoke our entry rule. I believe that this is true
	 *  and the proof might go like this.
	 *
	 *  THEOREM:  The amount of input consumed during a full context
	 *  simulation is at most the amount of input consumed during a
	 *  non full context simulation.
	 *
	 *  PROOF: Let D be the DFA state at which non-context simulation
	 *  terminated. That means that D does not have a configuration for
	 *  which we can legally pursue more input. (It is legal to work only
	 *  on configurations for which there is no conflict with another
	 *  configuration.) Now we restrict ourselves to following ATN edges
	 *  associated with a single context. Choose any DFA state D' along
	 *  the path (same input) to D. That state has either the same number
	 *  of configurations or fewer. (If the number of configurations is
	 *  the same, then we have degenerated to the non-context case.) Now
	 *  imagine that we restrict to following edges associated with
	 *  another single context and that we reach DFA state D'' for the
	 *  same amount of input as D'. The non-context simulation merges D'
	 *  and D''. The union of the configuration sets either has the same
	 *  number of configurations as both D' and D'' or it has more. If it
	 *  has the same number, we are no worse off and the merge does not
	 *  force us to look for more input than we would otherwise have to
	 *  do. If the union has more configurations, it can introduce
	 *  conflicts but not new alternatives--we cannot conjure up alternatives
	 *  by computing closure on the DFA state.  Here are the cases for
	 *  D' union D'':
	 *
	 *  1. No increase in configurations, D' = D''
	 *  2. Add configuration that introduces a new alternative number.
	 *     This cannot happen because no new alternatives are introduced
	 *     while computing closure, even during start state computation.
	 *  3. D'' adds a configuration that does not conflict with any
	 *     configuration in D'.  Simulating without context would then have
	 *     forced us to use more lookahead than D' (full context) alone.
	 *  3. D'' adds a configuration that introduces a conflict with a
	 *     configuration in D'. There are 2 cases:
	 *     a. The conflict does not cause termination (D' union D''
	 *        is added to the work list). Again no context simulation requires
	 *        more input.
	 *     b. The conflict does cause termination, but this cannot happen.
	 *        By definition, we know that with ALL contexts merged we
	 *        don't terminate until D and D' uses less input than D. Therefore
	 *        no context simulation requires more input than full context
	 *        simulation.
	 *
	 *  We have covered all the cases and there is never a situation where
	 *  a single, full context simulation requires more input than a
	 *  no context simulation.

	 I spent a bunch of time thinking about this problem after finding
	 a case where context-sensitive ATN simulation looks beyond what they
	 no context simulation uses. the no context simulation for if then else
	 stops at the else whereas full context scans through to the end of the
	 statement to decide that the "else statement" clause is ambiguous. And
	 sometimes it is not ambiguous! Ok, I made an untrue assumption in my
	 proof which I won't bother going to. the important thing is what I'm
	 going to do about it. I thought I had a simple answer, but nope. It
	 turns out that the if then else case is perfect example of something
	 that has the following characteristics:

	 * no context conflicts at k=1
	 * full context at k=(1 + length of statement) can be both ambiguous and not
	   ambiguous depending on the input, though I think from different contexts.

	 But, the good news is that the k=1 case is a special case in that
	 SLL(1) and LL(1) have exactly the same power so we can conclude that
	 conflicts at k=1 are true ambiguities and we do not need to pursue
	 context-sensitive parsing. That covers a huge number of cases
	 including the if then else clause and the predicated precedence
	 parsing mechanism. whew! because that could be extremely expensive if
	 we had to do context.

	 Further, there is no point in doing full context if none of the
	 configurations dip into the outer context. This nicely handles cases
	 such as super constructor calls versus function calls. One grammar
	 might look like this:

	 ctorBody : '{' superCall? stat* '}' ;

	 Or, you might see something like

	 stat : superCall ';' | expression ';' | ... ;

	 In both cases I believe that no closure operations will dip into the
	 outer context. In the first case ctorBody in the worst case will stop
	 at the '}'. In the 2nd case it should stop at the ';'. Both cases
	 should stay within the entry rule and not dip into the outer context.

	 So, we now cover what I hope is the vast majority of the cases (in
	 particular the very important precedence parsing case). Anything that
	 needs k>1 and dips into the outer context requires a full context
	 retry. In this case, I'm going to start out with a brain-dead solution
	 which is to mark the DFA state as context-sensitive when I get a
	 conflict. Any further DFA simulation that reaches that state will
	 launch an ATN simulation to get the prediction, without updating the
	 DFA or storing any context information. Later, I can make this more
	 efficient, but at least in this case I can guarantee that it will
	 always do the right thing. We are not making any assumptions about
	 lookahead depth.

	 Ok, writing this up so I can put in a comment.

	 Upon conflict in the no context simulation:

	 * if k=1, report ambiguity and resolve to the minimum conflicting alternative

	 * if k=1 and predicates, no report and include the predicate to
	   predicted alternative map in the DFA state

	 * if k=* and we did not dip into the outer context, report ambiguity
	   and resolve to minimum conflicting alternative

	 * if k>1 and we dip into outer context, retry with full context
		 * if conflict, report ambiguity and resolve to minimum conflicting
		   alternative, mark DFA as context-sensitive
		 * If no conflict, report ctx sensitivity and mark DFA as context-sensitive
		 * Technically, if full context k is less than no context k, we can
			 reuse the conflicting DFA state so we don't have to create special
			 DFA paths branching from context, but we can leave that for
			 optimization later if necessary.

	 * if non-greedy, no report and resolve to the exit alternative
 *
 * 	By default we do full context-sensitive LL(*) parsing not
 	 *  Strong LL(*) parsing. If we fail with Strong LL(*) we
 	 *  try full LL(*). That means we rewind and use context information
 	 *  when closure operations fall off the end of the rule that
 	 *  holds the decision were evaluating
*/
public class ParserATNSimulator<Symbol extends Token> extends ATNSimulator {
	public static final boolean debug = false;
	public static final boolean dfa_debug = false;
	public static final boolean retry_debug = false;

	@NotNull
	private PredictionMode predictionMode = PredictionMode.LL;
	public boolean force_global_context = false;
	public boolean always_try_local_context = true;

	public boolean optimize_unique_closure = true;
	public boolean optimize_ll1 = true;
	public boolean optimize_hidden_conflicted_configs = false;
	public boolean optimize_tail_calls = true;
	public boolean tail_call_preserves_sll = true;
	public boolean treat_sllk1_conflict_as_ambiguity = false;

	@Nullable
	protected final Parser<Symbol> parser;

	/**
	 * When {@code true}, ambiguous alternatives are reported when they are
	 * encountered within {@link #execATN}. When {@code false}, these messages
	 * are suppressed. The default is {@code false}.
	 * <p>
	 * When messages about ambiguous alternatives are not required, setting this
	 * to {@code false} enables additional internal optimizations which may lose
	 * this information.
	 */
	public boolean reportAmbiguities = false;

	/** By default we do full context-sensitive LL(*) parsing not
	 *  Strong LL(*) parsing. If we fail with Strong LL(*) we
	 *  try full LL(*). That means we rewind and use context information
	 *  when closure operations fall off the end of the rule that
	 *  holds the decision were evaluating.
	 */
	protected boolean userWantsCtxSensitive = true;

	/** Testing only! */
	public ParserATNSimulator(@NotNull ATN atn) {
		this(null, atn);
	}

	public ParserATNSimulator(@Nullable Parser<Symbol> parser, @NotNull ATN atn) {
		super(atn);
		this.parser = parser;
	}

	@NotNull
	public final PredictionMode getPredictionMode() {
		return predictionMode;
	}

	public final void setPredictionMode(@NotNull PredictionMode predictionMode) {
		this.predictionMode = predictionMode;
	}

	@Override
	public void reset() {
	}

	public int adaptivePredict(@NotNull TokenStream<? extends Symbol> input, int decision,
							   @Nullable ParserRuleContext<Symbol> outerContext)
	{
		return adaptivePredict(input, decision, outerContext, false);
	}

	public int adaptivePredict(@NotNull TokenStream<? extends Symbol> input,
							   int decision,
							   @Nullable ParserRuleContext<Symbol> outerContext,
							   boolean useContext)
	{
		DFA dfa = atn.decisionToDFA[decision];
		assert dfa != null;
		if (optimize_ll1 && !dfa.isEmpty()) {
			int ll_1 = input.LA(1);
			if (ll_1 >= 0 && ll_1 <= Short.MAX_VALUE) {
				int key = (decision << 16) + ll_1;
				Integer alt = atn.LL1Table.get(key);
				if (alt != null) {
					return alt;
				}
			}
		}

		if (force_global_context) {
			useContext = true;
		}
		else if (!always_try_local_context) {
			useContext |= dfa.isContextSensitive();
		}

		userWantsCtxSensitive = useContext || (predictionMode != PredictionMode.SLL && outerContext != null && !atn.decisionToState.get(decision).sll);
		if (outerContext == null) {
			outerContext = ParserRuleContext.emptyContext();
		}

		SimulatorState<Symbol> state = null;
		if (!dfa.isEmpty()) {
			state = getStartState(dfa, input, outerContext, useContext);
		}

		if ( state==null ) {
			if ( outerContext==null ) outerContext = ParserRuleContext.emptyContext();
			if ( debug ) System.out.println("ATN decision "+dfa.decision+
											" exec LA(1)=="+ getLookaheadName(input) +
											", outerContext="+outerContext.toString(parser));

			state = computeStartState(dfa, outerContext, useContext);
		}

		int m = input.mark();
		int index = input.index();
		try {
			int alt = execDFA(dfa, input, index, state);
			if ( debug ) System.out.println("DFA after predictATN: "+dfa.toString(parser.getTokenNames(), parser.getRuleNames()));
			return alt;
		}
		finally {
			input.seek(index);
			input.release(m);
		}
	}

	protected SimulatorState<Symbol> getStartState(@NotNull DFA dfa,
										@NotNull TokenStream<? extends Symbol> input,
										@NotNull ParserRuleContext<Symbol> outerContext,
										boolean useContext) {

		if (!useContext) {
			if (dfa.s0.get() == null) {
				return null;
			}

			return new SimulatorState<Symbol>(outerContext, dfa.s0.get(), false, outerContext);
		}

		ParserRuleContext<Symbol> remainingContext = outerContext;
		assert outerContext != null;
		DFAState s0 = dfa.s0full.get();
		while (remainingContext != null && s0 != null && s0.isContextSensitive()) {
			remainingContext = skipTailCalls(remainingContext);
			s0 = s0.getContextTarget(getReturnState(remainingContext));
			if (remainingContext.isEmpty()) {
				assert s0 == null || !s0.isContextSensitive();
			}
			else {
				remainingContext = remainingContext.getParent();
			}
		}

		if (s0 == null) {
			return null;
		}

		return new SimulatorState<Symbol>(outerContext, s0, useContext, remainingContext);
	}

	protected int execDFA(@NotNull DFA dfa,
					   @NotNull TokenStream<? extends Symbol> input, int startIndex,
					   @NotNull SimulatorState<Symbol> state)
    {
		ParserRuleContext<Symbol> outerContext = state.outerContext;
		if ( dfa_debug ) System.out.println("DFA decision "+dfa.decision+
											" exec LA(1)=="+ getLookaheadName(input) +
											", outerContext="+outerContext.toString(parser));
		if ( dfa_debug ) System.out.print(dfa.toString(parser.getTokenNames(), parser.getRuleNames()));
		DFAState acceptState = null;
		DFAState s = state.s0;

		int t = input.LA(1);
		ParserRuleContext<Symbol> remainingOuterContext = state.remainingOuterContext;

		while ( true ) {
			if ( dfa_debug ) System.out.println("DFA state "+s.stateNumber+" LA(1)=="+getLookaheadName(input));
			if ( state.useContext ) {
				while ( s.isContextSymbol(t) ) {
					DFAState next = null;
					if (remainingOuterContext != null) {
						remainingOuterContext = skipTailCalls(remainingOuterContext);
						next = s.getContextTarget(getReturnState(remainingOuterContext));
					}

					if ( next == null ) {
						// fail over to ATN
						SimulatorState<Symbol> initialState = new SimulatorState<Symbol>(state.outerContext, s, state.useContext, remainingOuterContext);
						return execATN(dfa, input, startIndex, initialState);
					}

					remainingOuterContext = remainingOuterContext.getParent();
					s = next;
				}
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
			assert !s.isAcceptState;

			// if no edge, pop over to ATN interpreter, update DFA and return
			DFAState target = s.getTarget(t);
			if ( target == null ) {
				if ( dfa_debug && t>=0 ) System.out.println("no edge for "+parser.getTokenNames()[t]);
				int alt;
				if ( dfa_debug ) {
					Interval interval = Interval.of(startIndex, parser.getInputStream().index());
					System.out.println("ATN exec upon "+
									   parser.getInputStream().getText(interval) +
									   " at DFA state "+s.stateNumber);
				}

				SimulatorState<Symbol> initialState = new SimulatorState<Symbol>(outerContext, s, state.useContext, remainingOuterContext);
				alt = execATN(dfa, input, startIndex, initialState);
				// this adds edge even if next state is accept for
				// same alt; e.g., s0-A->:s1=>2-B->:s2=>2
				// TODO: This next stuff kills edge, but extra states remain. :(
				if ( s.isAcceptState && alt!=-1 ) {
					DFAState d = s.getTarget(input.LA(1));
					if ( d.isAcceptState && d.prediction==s.prediction ) {
						// we can carve it out.
						s.setTarget(input.LA(1), ERROR); // IGNORE really not error
					}
				}
				if ( dfa_debug ) {
					System.out.println("back from DFA update, alt="+alt+", dfa=\n"+dfa.toString(parser.getTokenNames(), parser.getRuleNames()));
					//dump(dfa);
				}
				// action already executed
				if ( dfa_debug ) System.out.println("DFA decision "+dfa.decision+
													" predicts "+alt);
				return alt; // we've updated DFA, exec'd action, and have our deepest answer
			}
			else if ( target == ERROR ) {
				SimulatorState<Symbol> errorState = new SimulatorState<Symbol>(outerContext, s, state.useContext, remainingOuterContext);
				return handleNoViableAlt(input, startIndex, errorState);
			}
			s = target;
			if (!s.isAcceptState && t != IntStream.EOF) {
				input.consume();
				t = input.LA(1);
			}
		}
//		if ( acceptState==null ) {
//			if ( debug ) System.out.println("!!! no viable alt in dfa");
//			return -1;
//		}

		if ( acceptState.configs.getConflictingAlts()!=null ) {
			if ( dfa.atnStartState instanceof DecisionState ) {
				if (!userWantsCtxSensitive ||
					!acceptState.configs.getDipsIntoOuterContext() ||
					(treat_sllk1_conflict_as_ambiguity && input.index() == startIndex))
				{
					// we don't report the ambiguity again
					//if ( !acceptState.configset.hasSemanticContext() ) {
					//	reportAmbiguity(dfa, acceptState, startIndex, input.index(), acceptState.configset.getConflictingAlts(), acceptState.configset);
					//}
				}
				else {
					assert !state.useContext;

					// Before attempting full context prediction, check to see if there are
					// disambiguating or validating predicates to evaluate which allow an
					// immediate decision
					BitSet conflictingAlts = null;
					if ( acceptState.predicates!=null ) {
						int conflictIndex = input.index();
						if (conflictIndex != startIndex) {
							input.seek(startIndex);
						}

						conflictingAlts = evalSemanticContext(s.predicates, outerContext, true);
						if ( conflictingAlts.cardinality() == 1 ) {
							return conflictingAlts.nextSetBit(0);
						}

						if (conflictIndex != startIndex) {
							// restore the index so reporting the fallback to full
							// context occurs with the index at the correct spot
							input.seek(conflictIndex);
						}
					}

					if (reportAmbiguities) {
						SimulatorState<Symbol> conflictState = new SimulatorState<Symbol>(outerContext, acceptState, state.useContext, remainingOuterContext);
						reportAttemptingFullContext(dfa, conflictingAlts, conflictState, startIndex, input.index());
					}

					input.seek(startIndex);
					return adaptivePredict(input, dfa.decision, outerContext, true);
				}
			}
		}

		// Before jumping to prediction, check to see if there are
		// disambiguating or validating predicates to evaluate
		if ( s.predicates != null ) {
			int stopIndex = input.index();
			if (startIndex != stopIndex) {
				input.seek(startIndex);
			}

			BitSet alts = evalSemanticContext(s.predicates, outerContext, reportAmbiguities && predictionMode == PredictionMode.LL_EXACT_AMBIG_DETECTION);
			switch (alts.cardinality()) {
			case 0:
				throw noViableAlt(input, outerContext, s.configs, startIndex);

			case 1:
				return alts.nextSetBit(0);

			default:
				// report ambiguity after predicate evaluation to make sure the correct
				// set of ambig alts is reported.
				if (startIndex != stopIndex) {
					input.seek(stopIndex);
				}

				reportAmbiguity(dfa, s, startIndex, stopIndex, predictionMode == PredictionMode.LL_EXACT_AMBIG_DETECTION, alts, s.configs);
				return alts.nextSetBit(0);
			}
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
	protected int execATN(@NotNull DFA dfa,
					   @NotNull TokenStream<? extends Symbol> input, int startIndex,
					   @NotNull SimulatorState<Symbol> initialState)
	{
		if ( debug ) System.out.println("execATN decision "+dfa.decision+" exec LA(1)=="+ getLookaheadName(input));

		final ParserRuleContext<Symbol> outerContext = initialState.outerContext;
		final boolean useContext = initialState.useContext;

		int t = input.LA(1);

		SimulatorState<Symbol> previous = initialState;

		PredictionContextCache contextCache = new PredictionContextCache();
		while (true) { // while more work
			SimulatorState<Symbol> nextState = computeReachSet(dfa, previous, t, contextCache);
			if (nextState == null) {
				addDFAEdge(previous.s0, input.LA(1), ERROR);
				return handleNoViableAlt(input, startIndex, previous);
			}

			DFAState D = nextState.s0;

			// predicted alt => accept state
			assert D.isAcceptState || getUniqueAlt(D.configs) == ATN.INVALID_ALT_NUMBER;
			// conflicted => accept state
			assert D.isAcceptState || D.configs.getConflictingAlts() == null;

			if (D.isAcceptState) {
				BitSet conflictingAlts = D.configs.getConflictingAlts();
				int predictedAlt = conflictingAlts == null ? getUniqueAlt(D.configs) : ATN.INVALID_ALT_NUMBER;
				if ( predictedAlt!=ATN.INVALID_ALT_NUMBER ) {
					if (optimize_ll1
						&& input.index() == startIndex
						&& nextState.outerContext == nextState.remainingOuterContext
						&& dfa.decision >= 0
						&& !D.configs.hasSemanticContext())
					{
						if (t >= 0 && t <= Short.MAX_VALUE) {
							int key = (dfa.decision << 16) + t;
							atn.LL1Table.put(key, predictedAlt);
						}
					}

					if (useContext && always_try_local_context) {
						reportContextSensitivity(dfa, predictedAlt, nextState, startIndex, input.index());
					}
				}

				predictedAlt = D.prediction;
//				int k = input.index() - startIndex + 1; // how much input we used
//				System.out.println("used k="+k);
				boolean attemptFullContext = conflictingAlts != null && userWantsCtxSensitive;
				if (attemptFullContext) {
					if (predictionMode == PredictionMode.LL_EXACT_AMBIG_DETECTION) {
						attemptFullContext = !useContext
							&& (D.configs.getDipsIntoOuterContext() || D.configs.getConflictingAlts().cardinality() > 2)
							&& (!treat_sllk1_conflict_as_ambiguity || input.index() != startIndex);
					}
					else {
						attemptFullContext = D.configs.getDipsIntoOuterContext()
							&& (!treat_sllk1_conflict_as_ambiguity || input.index() != startIndex);
					}
				}

				if ( D.configs.hasSemanticContext() ) {
					DFAState.PredPrediction[] predPredictions = D.predicates;
					if (predPredictions != null) {
						int conflictIndex = input.index();
						if (conflictIndex != startIndex) {
							input.seek(startIndex);
						}

						// use complete evaluation here if we'll want to retry with full context if still ambiguous
						conflictingAlts = evalSemanticContext(predPredictions, outerContext, attemptFullContext || reportAmbiguities);
						switch (conflictingAlts.cardinality()) {
						case 0:
							throw noViableAlt(input, outerContext, D.configs, startIndex);

						case 1:
							return conflictingAlts.nextSetBit(0);

						default:
							break;
						}

						if (conflictIndex != startIndex) {
							// restore the index so reporting the fallback to full
							// context occurs with the index at the correct spot
							input.seek(conflictIndex);
						}
					}
				}

				if (!attemptFullContext) {
					if (conflictingAlts != null) {
						if (reportAmbiguities && conflictingAlts.cardinality() > 1) {
							reportAmbiguity(dfa, D, startIndex, input.index(), predictionMode == PredictionMode.LL_EXACT_AMBIG_DETECTION, conflictingAlts, D.configs);
						}

						predictedAlt = conflictingAlts.nextSetBit(0);
					}

					return predictedAlt;
				}
				else {
					assert !useContext;
					assert D.isAcceptState;

					if ( debug ) System.out.println("RETRY with outerContext="+outerContext);
					SimulatorState<Symbol> fullContextState = computeStartState(dfa, outerContext, true);
					if (reportAmbiguities) {
						reportAttemptingFullContext(dfa, conflictingAlts, nextState, startIndex, input.index());
					}

					input.seek(startIndex);
					return execATN(dfa, input, startIndex, fullContextState);
				}
			}

			previous = nextState;

			if (t != IntStream.EOF) {
				input.consume();
				t = input.LA(1);
			}
		}
	}

	protected int handleNoViableAlt(@NotNull TokenStream<? extends Symbol> input, int startIndex, @NotNull SimulatorState<Symbol> previous) {
		if (previous.s0 != null) {
			BitSet alts = new BitSet();
			for (ATNConfig config : previous.s0.configs) {
				if (config.getReachesIntoOuterContext() || config.getState() instanceof RuleStopState) {
					alts.set(config.getAlt());
				}
			}

			if (!alts.isEmpty()) {
				return alts.nextSetBit(0);
			}
		}

		throw noViableAlt(input, previous.outerContext, previous.s0.configs, startIndex);
	}

	protected SimulatorState<Symbol> computeReachSet(DFA dfa, SimulatorState<Symbol> previous, int t, PredictionContextCache contextCache) {
		final boolean useContext = previous.useContext;
		ParserRuleContext<Symbol> remainingGlobalContext = previous.remainingOuterContext;

		DFAState s = previous.s0;
		if ( useContext ) {
			while ( s.isContextSymbol(t) ) {
				DFAState next = null;
				if (remainingGlobalContext != null) {
					remainingGlobalContext = skipTailCalls(remainingGlobalContext);
					next = s.getContextTarget(getReturnState(remainingGlobalContext));
				}

				if ( next == null ) {
					break;
				}

				remainingGlobalContext = remainingGlobalContext.getParent();
				s = next;
			}
		}

		assert !s.isAcceptState;
		if ( s.isAcceptState ) {
			return new SimulatorState<Symbol>(previous.outerContext, s, useContext, remainingGlobalContext);
		}

		final DFAState s0 = s;

		DFAState target = getExistingTargetState(s0, t);
		if (target == null) {
			Tuple2<DFAState, ParserRuleContext<Symbol>> result = computeTargetState(dfa, s0, remainingGlobalContext, t, useContext, contextCache);
			target = result.getItem1();
			remainingGlobalContext = result.getItem2();
		}

		if (target == ERROR) {
			return null;
		}

		assert !useContext || !target.configs.getDipsIntoOuterContext();
		return new SimulatorState<Symbol>(previous.outerContext, target, useContext, remainingGlobalContext);
	}

	/**
	 * Get an existing target state for an edge in the DFA. If the target state
	 * for the edge has not yet been computed or is otherwise not available,
	 * this method returns {@code null}.
	 *
	 * @param s The current DFA state
	 * @param t The next input symbol
	 * @return The existing target DFA state for the given input symbol
	 * {@code t}, or {@code null} if the target state for this edge is not
	 * already cached
	 */
	@Nullable
	protected DFAState getExistingTargetState(@NotNull DFAState s, int t) {
		return s.getTarget(t);
	}

	/**
	 * Compute a target state for an edge in the DFA, and attempt to add the
	 * computed state and corresponding edge to the DFA.
	 *
	 * @param dfa
	 * @param s The current DFA state
	 * @param remainingGlobalContext
	 * @param t The next input symbol
	 * @param useContext
	 * @param contextCache
	 *
	 * @return The computed target DFA state for the given input symbol
	 * {@code t}. If {@code t} does not lead to a valid DFA state, this method
	 * returns {@link #ERROR}.
	 */
	@NotNull
	protected Tuple2<DFAState, ParserRuleContext<Symbol>> computeTargetState(@NotNull DFA dfa, @NotNull DFAState s, ParserRuleContext<Symbol> remainingGlobalContext, int t, boolean useContext, PredictionContextCache contextCache) {
		List<ATNConfig> closureConfigs = new ArrayList<ATNConfig>(s.configs);
		IntegerList contextElements = null;
		ATNConfigSet reach = new ATNConfigSet();
		boolean stepIntoGlobal;
		do {
			boolean hasMoreContext = !useContext || remainingGlobalContext != null;
			if (!hasMoreContext) {
				reach.setOutermostConfigSet(true);
			}

			ATNConfigSet reachIntermediate = new ATNConfigSet();

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

			for (ATNConfig c : closureConfigs) {
				if ( debug ) System.out.println("testing "+getTokenName(t)+" at "+c.toString());

				if (c.getState() instanceof RuleStopState) {
					assert c.getContext().isEmpty();
					if (useContext && !c.getReachesIntoOuterContext() || t == IntStream.EOF) {
						if (skippedStopStates == null) {
							skippedStopStates = new ArrayList<ATNConfig>();
						}

						skippedStopStates.add(c);
					}

					continue;
				}

				int n = c.getState().getNumberOfOptimizedTransitions();
				for (int ti=0; ti<n; ti++) {               // for each optimized transition
					Transition trans = c.getState().getOptimizedTransition(ti);
					ATNState target = getReachableTarget(c, trans, t);
					if ( target!=null ) {
						reachIntermediate.add(c.transform(target), contextCache);
					}
				}
			}


			/* This block optimizes the reach operation for intermediate sets which
			 * trivially indicate a termination state for the overall
			 * adaptivePredict operation.
			 *
			 * The conditions assume that intermediate
			 * contains all configurations relevant to the reach set, but this
			 * condition is not true when one or more configurations have been
			 * withheld in skippedStopStates.
			 */
			if (optimize_unique_closure && skippedStopStates == null && reachIntermediate.getUniqueAlt() != ATN.INVALID_ALT_NUMBER) {
				reachIntermediate.setOutermostConfigSet(reach.isOutermostConfigSet());
				reach = reachIntermediate;
				break;
			}

			/* If the reach set could not be trivially determined, perform a closure
			 * operation on the intermediate set to compute its initial value.
			 */
			final boolean collectPredicates = false;
			closure(reachIntermediate, reach, collectPredicates, hasMoreContext, contextCache);
			stepIntoGlobal = reach.getDipsIntoOuterContext();

			if (t == IntStream.EOF) {
				/* After consuming EOF no additional input is possible, so we are
				 * only interested in configurations which reached the end of the
				 * decision rule (local context) or end of the start rule (full
				 * context). Update reach to contain only these configurations. This
				 * handles both explicit EOF transitions in the grammar and implicit
				 * EOF transitions following the end of the decision or start rule.
				 *
				 * This is handled before the configurations in skippedStopStates,
				 * because any configurations potentially added from that list are
				 * already guaranteed to meet this condition whether or not it's
				 * required.
				 */
				reach = removeAllConfigsNotInRuleStopState(reach, contextCache);
			}

			/* If skippedStopStates is not null, then it contains at least one
			 * configuration. For full-context reach operations, these
			 * configurations reached the end of the start rule, in which case we
			 * only add them back to reach if no configuration during the current
			 * closure operation reached such a state. This ensures adaptivePredict
			 * chooses an alternative matching the longest overall sequence when
			 * multiple alternatives are viable.
			 */
			if (skippedStopStates != null && (!useContext || !PredictionMode.hasConfigInRuleStopState(reach))) {
				assert !skippedStopStates.isEmpty();
				for (ATNConfig c : skippedStopStates) {
					reach.add(c, contextCache);
				}
			}

			if (useContext && stepIntoGlobal) {
				reach.clear();

				remainingGlobalContext = skipTailCalls(remainingGlobalContext);
				int nextContextElement = getReturnState(remainingGlobalContext);
				if (contextElements == null) {
					contextElements = new IntegerList();
				}

				if (remainingGlobalContext.isEmpty()) {
					remainingGlobalContext = null;
				} else {
					remainingGlobalContext = remainingGlobalContext.getParent();
				}

				contextElements.add(nextContextElement);
				if (nextContextElement != PredictionContext.EMPTY_FULL_STATE_KEY) {
					for (int i = 0; i < closureConfigs.size(); i++) {
						closureConfigs.set(i, closureConfigs.get(i).appendContext(nextContextElement, contextCache));
					}
				}
			}
		} while (useContext && stepIntoGlobal);

		if (reach.isEmpty()) {
			addDFAEdge(s, t, ERROR);
			return Tuple.create(ERROR, remainingGlobalContext);
		}

		DFAState result = addDFAEdge(dfa, s, t, contextElements, reach, contextCache);
		return Tuple.create(result, remainingGlobalContext);
	}

	/**
	 * Return a configuration set containing only the configurations from
	 * {@code configs} which are in a {@link RuleStopState}. If all
	 * configurations in {@code configs} are already in a rule stop state, this
	 * method simply returns {@code configs}.
	 *
	 * @param configs the configuration set to update
	 * @param contextCache the {@link PredictionContext} cache
	 *
	 * @return {@code configs} if all configurations in {@code configs} are in a
	 * rule stop state, otherwise return a new configuration set containing only
	 * the configurations from {@code configs} which are in a rule stop state
	 */
	@NotNull
	protected ATNConfigSet removeAllConfigsNotInRuleStopState(@NotNull ATNConfigSet configs, PredictionContextCache contextCache) {
		if (PredictionMode.allConfigsInRuleStopStates(configs)) {
			return configs;
		}

		ATNConfigSet result = new ATNConfigSet();
		for (ATNConfig config : configs) {
			if (!(config.getState() instanceof RuleStopState)) {
				continue;
			}

			result.add(config, contextCache);
		}

		return result;
	}

	@NotNull
	protected SimulatorState<Symbol> computeStartState(DFA dfa,
											ParserRuleContext<Symbol> globalContext,
											boolean useContext)
	{
		DFAState s0 = useContext ? dfa.s0full.get() : dfa.s0.get();
		if (s0 != null) {
			if (!useContext) {
				return new SimulatorState<Symbol>(globalContext, s0, useContext, globalContext);
			}

			s0.setContextSensitive(atn);
		}

		final int decision = dfa.decision;
		@NotNull
		final ATNState p = dfa.atnStartState;

		int previousContext = 0;
		ParserRuleContext<Symbol> remainingGlobalContext = globalContext;
		PredictionContext initialContext = useContext ? PredictionContext.EMPTY_FULL : PredictionContext.EMPTY_LOCAL; // always at least the implicit call to start rule
		PredictionContextCache contextCache = new PredictionContextCache();
		if (useContext) {
			while (s0 != null && s0.isContextSensitive() && remainingGlobalContext != null) {
				DFAState next;
				remainingGlobalContext = skipTailCalls(remainingGlobalContext);
				if (remainingGlobalContext.isEmpty()) {
					next = s0.getContextTarget(PredictionContext.EMPTY_FULL_STATE_KEY);
					previousContext = PredictionContext.EMPTY_FULL_STATE_KEY;
					remainingGlobalContext = null;
				}
				else {
					previousContext = getReturnState(remainingGlobalContext);
					next = s0.getContextTarget(previousContext);
					initialContext = initialContext.appendContext(previousContext, contextCache);
					remainingGlobalContext = remainingGlobalContext.getParent();
				}

				if (next == null) {
					break;
				}

				s0 = next;
			}
		}

		if (s0 != null && !s0.isContextSensitive()) {
			return new SimulatorState<Symbol>(globalContext, s0, useContext, remainingGlobalContext);
		}

		ATNConfigSet configs = new ATNConfigSet();
		while (true) {
			ATNConfigSet reachIntermediate = new ATNConfigSet();
			int n = p.getNumberOfTransitions();
			for (int ti=0; ti<n; ti++) {
				// for each transition
				ATNState target = p.transition(ti).target;
				reachIntermediate.add(ATNConfig.create(target, ti + 1, initialContext));
			}

			boolean hasMoreContext = remainingGlobalContext != null;
			if (!hasMoreContext) {
				configs.setOutermostConfigSet(true);
			}

			final boolean collectPredicates = true;
			closure(reachIntermediate, configs, collectPredicates, hasMoreContext, contextCache);
			boolean stepIntoGlobal = configs.getDipsIntoOuterContext();

			DFAState next = addDFAState(dfa, configs, contextCache);
			if (s0 == null) {
				AtomicReference<DFAState> reference = useContext ? dfa.s0full : dfa.s0;
				if (!reference.compareAndSet(null, next)) {
					next = reference.get();
				}
			}
			else {
				s0.setContextTarget(previousContext, next);
			}
			s0 = next;

			if (!useContext || !stepIntoGlobal) {
				break;
			}

			// TODO: make sure it distinguishes empty stack states
			next.setContextSensitive(atn);

			configs.clear();
			remainingGlobalContext = skipTailCalls(remainingGlobalContext);
			int nextContextElement = getReturnState(remainingGlobalContext);

			if (remainingGlobalContext.isEmpty()) {
				remainingGlobalContext = null;
			} else {
				remainingGlobalContext = remainingGlobalContext.getParent();
			}

			if (nextContextElement != PredictionContext.EMPTY_FULL_STATE_KEY) {
				initialContext = initialContext.appendContext(nextContextElement, contextCache);
			}

			previousContext = nextContextElement;
		}

		return new SimulatorState<Symbol>(globalContext, s0, useContext, remainingGlobalContext);
	}

	@Nullable
	protected ATNState getReachableTarget(@NotNull ATNConfig source, @NotNull Transition trans, int ttype) {
		if (trans.matches(ttype, 0, atn.maxTokenType)) {
			return trans.target;
		}

		return null;
	}

	/** collect and set D's semantic context */
	protected DFAState.PredPrediction[] predicateDFAState(DFAState D,
													   ATNConfigSet configs,
													   int nalts)
	{
		BitSet conflictingAlts = getConflictingAltsFromConfigSet(configs);
		if ( debug ) System.out.println("predicateDFAState "+D);
		SemanticContext[] altToPred = getPredsForAmbigAlts(conflictingAlts, configs, nalts);
		// altToPred[uniqueAlt] is now our validating predicate (if any)
		DFAState.PredPrediction[] predPredictions = null;
		if ( altToPred!=null ) {
			// we have a validating predicate; test it
			// Update DFA so reach becomes accept state with predicate
			predPredictions = getPredicatePredictions(conflictingAlts, altToPred);
			D.predicates = predPredictions;
			D.prediction = ATN.INVALID_ALT_NUMBER; // make sure we use preds
		}
		return predPredictions;
	}

	protected SemanticContext[] getPredsForAmbigAlts(@NotNull BitSet ambigAlts,
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
		SemanticContext[] altToPred = new SemanticContext[nalts +1];
		int n = altToPred.length;
		for (ATNConfig c : configs) {
			if ( ambigAlts.get(c.getAlt()) ) {
				altToPred[c.getAlt()] = SemanticContext.or(altToPred[c.getAlt()], c.getSemanticContext());
			}
		}

		int nPredAlts = 0;
		for (int i = 0; i < n; i++) {
			if (altToPred[i] == null) {
				altToPred[i] = SemanticContext.NONE;
			}
			else if (altToPred[i] != SemanticContext.NONE) {
				nPredAlts++;
			}
		}

		// nonambig alts are null in altToPred
		if ( nPredAlts==0 ) altToPred = null;
		if ( debug ) System.out.println("getPredsForAmbigAlts result "+Arrays.toString(altToPred));
		return altToPred;
	}

	protected DFAState.PredPrediction[] getPredicatePredictions(BitSet ambigAlts, SemanticContext[] altToPred) {
		List<DFAState.PredPrediction> pairs = new ArrayList<DFAState.PredPrediction>();
		boolean containsPredicate = false;
		for (int i = 1; i < altToPred.length; i++) {
			SemanticContext pred = altToPred[i];

			// unpredicated is indicated by SemanticContext.NONE
			assert pred != null;

			// find first unpredicated but ambig alternative, if any.
			// Only ambiguous alternatives will have SemanticContext.NONE.
			// Any unambig alts or ambig naked alts after first ambig naked are ignored
			// (null, i) means alt i is the default prediction
			// if no (null, i), then no default prediction.
			if (ambigAlts!=null && ambigAlts.get(i) && pred==SemanticContext.NONE) {
				pairs.add(new DFAState.PredPrediction(null, i));
			}
			else if ( pred!=SemanticContext.NONE ) {
				containsPredicate = true;
				pairs.add(new DFAState.PredPrediction(pred, i));
			}
		}

		if ( !containsPredicate ) {
			pairs = null;
		}

//		System.out.println(Arrays.toString(altToPred)+"->"+pairs);
		return pairs.toArray(new DFAState.PredPrediction[pairs.size()]);
	}

	/** Look through a list of predicate/alt pairs, returning alts for the
	 *  pairs that win. A {@code null} predicate indicates an alt containing an
	 *  unpredicated config which behaves as "always true."
	 */
	protected BitSet evalSemanticContext(@NotNull DFAState.PredPrediction[] predPredictions,
										   ParserRuleContext<Symbol> outerContext,
										   boolean complete)
	{
		BitSet predictions = new BitSet();
		for (DFAState.PredPrediction pair : predPredictions) {
			if ( pair.pred==null ) {
				predictions.set(pair.alt);
				if (!complete) {
					break;
				}

				continue;
			}

			boolean evaluatedResult = pair.pred.eval(parser, outerContext);
			if ( debug || dfa_debug ) {
				System.out.println("eval pred "+pair+"="+evaluatedResult);
			}

			if ( evaluatedResult ) {
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

	protected void closure(ATNConfigSet sourceConfigs,
						   @NotNull ATNConfigSet configs,
						   boolean collectPredicates,
						   boolean hasMoreContext,
						   @Nullable PredictionContextCache contextCache)
	{
		if (contextCache == null) {
			contextCache = PredictionContextCache.UNCACHED;
		}

		ATNConfigSet currentConfigs = sourceConfigs;
		Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
		while (currentConfigs.size() > 0) {
			ATNConfigSet intermediate = new ATNConfigSet();
			for (ATNConfig config : currentConfigs) {
				closure(config, configs, intermediate, closureBusy, collectPredicates, hasMoreContext, contextCache, 0);
			}

			currentConfigs = intermediate;
		}
	}

	protected void closure(@NotNull ATNConfig config,
						   @NotNull ATNConfigSet configs,
						   @Nullable ATNConfigSet intermediate,
						   @NotNull Set<ATNConfig> closureBusy,
						   boolean collectPredicates,
						   boolean hasMoreContexts,
						   @NotNull PredictionContextCache contextCache,
						   int depth)
	{
		if ( debug ) System.out.println("closure("+config.toString(parser,true)+")");

		if ( config.getState() instanceof RuleStopState ) {
			// We hit rule end. If we have context info, use it
			if ( !config.getContext().isEmpty() ) {
				boolean hasEmpty = config.getContext().hasEmpty();
				int nonEmptySize = config.getContext().size() - (hasEmpty ? 1 : 0);
				for (int i = 0; i < nonEmptySize; i++) {
					PredictionContext newContext = config.getContext().getParent(i); // "pop" return state
					ATNState returnState = atn.states.get(config.getContext().getReturnState(i));
					ATNConfig c = ATNConfig.create(returnState, config.getAlt(), newContext, config.getSemanticContext());
					// While we have context to pop back from, we may have
					// gotten that context AFTER having fallen off a rule.
					// Make sure we track that we are now out of context.
					c.setOuterContextDepth(config.getOuterContextDepth());
					assert depth > Integer.MIN_VALUE;
					closure(c, configs, intermediate, closureBusy, collectPredicates, hasMoreContexts, contextCache, depth - 1);
				}

				if (!hasEmpty || !hasMoreContexts) {
					return;
				}

				config = config.transform(config.getState(), PredictionContext.EMPTY_LOCAL);
			}
			else if (!hasMoreContexts) {
				configs.add(config, contextCache);
				return;
			}
			else {
				// else if we have no context info, just chase follow links (if greedy)
				if ( debug ) System.out.println("FALLING off rule "+
												getRuleName(config.getState().ruleIndex));

				if (config.getContext() == PredictionContext.EMPTY_FULL) {
					// no need to keep full context overhead when we step out
					config = config.transform(config.getState(), PredictionContext.EMPTY_LOCAL);
				}
			}
		}

		ATNState p = config.getState();
		// optimization
		if ( !p.onlyHasEpsilonTransitions() ) {
            configs.add(config, contextCache);
            if ( debug ) System.out.println("added config "+configs);
        }

        for (int i=0; i<p.getNumberOfOptimizedTransitions(); i++) {
            Transition t = p.getOptimizedTransition(i);
            boolean continueCollecting =
				!(t instanceof ActionTransition) && collectPredicates;
            ATNConfig c = getEpsilonTarget(config, t, continueCollecting, depth == 0, contextCache);
			if ( c!=null ) {
				if (t instanceof RuleTransition) {
					if (intermediate != null && !collectPredicates) {
						intermediate.add(c, contextCache);
						continue;
					}
				}

				int newDepth = depth;
				if ( config.getState() instanceof RuleStopState ) {
					// target fell off end of rule; mark resulting c as having dipped into outer context
					// We can't get here if incoming config was rule stop and we had context
					// track how far we dip into outer context.  Might
					// come in handy and we avoid evaluating context dependent
					// preds if this is > 0.

					if (!closureBusy.add(c)) {
						// avoid infinite recursion for right-recursive rules
						continue;
					}

					c.setOuterContextDepth(c.getOuterContextDepth() + 1);

					assert newDepth > Integer.MIN_VALUE;
					newDepth--;
					if ( debug ) System.out.println("dips into outer ctx: "+c);
				}
				else if (t instanceof RuleTransition) {
					if (optimize_tail_calls && ((RuleTransition)t).optimizedTailCall && (!tail_call_preserves_sll || !PredictionContext.isEmptyLocal(config.getContext()))) {
						assert c.getContext() == config.getContext();
						if (newDepth == 0) {
							// the pop/push of a tail call would keep the depth
							// constant, except we latch if it goes negative
							newDepth--;
							if (!tail_call_preserves_sll && PredictionContext.isEmptyLocal(config.getContext())) {
								// make sure the SLL config "dips into the outer context" or prediction may not fall back to LL on conflict
								c.setOuterContextDepth(c.getOuterContextDepth() + 1);
							}
						}
					}
					else {
						// latch when newDepth goes negative - once we step out of the entry context we can't return
						if (newDepth >= 0) {
							newDepth++;
						}
					}
				}

				closure(c, configs, intermediate, closureBusy, continueCollecting, hasMoreContexts, contextCache, newDepth);
			}
		}
	}

	@NotNull
	public String getRuleName(int index) {
		if ( parser!=null && index>=0 ) return parser.getRuleNames()[index];
		return "<rule "+index+">";
	}

	@Nullable
	protected ATNConfig getEpsilonTarget(@NotNull ATNConfig config, @NotNull Transition t, boolean collectPredicates, boolean inContext, PredictionContextCache contextCache) {
		switch (t.getSerializationType()) {
		case Transition.RULE:
			return ruleTransition(config, (RuleTransition)t, contextCache);

		case Transition.PRECEDENCE:
			return precedenceTransition(config, (PrecedencePredicateTransition)t, collectPredicates, inContext);

		case Transition.PREDICATE:
			return predTransition(config, (PredicateTransition)t, collectPredicates, inContext);

		case Transition.ACTION:
			return actionTransition(config, (ActionTransition)t);

		case Transition.EPSILON:
			return config.transform(t.target);

		default:
			return null;
		}
	}

	@NotNull
	protected ATNConfig actionTransition(@NotNull ATNConfig config, @NotNull ActionTransition t) {
		if ( debug ) System.out.println("ACTION edge "+t.ruleIndex+":"+t.actionIndex);
		return config.transform(t.target);
	}

	@Nullable
	protected ATNConfig precedenceTransition(@NotNull ATNConfig config,
									@NotNull PrecedencePredicateTransition pt,
									boolean collectPredicates,
									boolean inContext)
	{
		if ( debug ) {
			System.out.println("PRED (collectPredicates="+collectPredicates+") "+
                    pt.precedence+">=_p"+
					", ctx dependent=true");
			if ( parser != null ) {
                System.out.println("context surrounding pred is "+
                                   parser.getRuleInvocationStack());
            }
		}

        ATNConfig c = null;
        if (collectPredicates && inContext) {
            SemanticContext newSemCtx = SemanticContext.and(config.getSemanticContext(), pt.getPredicate());
            c = config.transform(pt.target, newSemCtx);
        }
		else {
			c = config.transform(pt.target);
		}

		if ( debug ) System.out.println("config from pred transition="+c);
        return c;
	}

	@Nullable
	protected ATNConfig predTransition(@NotNull ATNConfig config,
									@NotNull PredicateTransition pt,
									boolean collectPredicates,
									boolean inContext)
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

        ATNConfig c;
        if ( collectPredicates &&
			 (!pt.isCtxDependent || (pt.isCtxDependent&&inContext)) )
		{
            SemanticContext newSemCtx = SemanticContext.and(config.getSemanticContext(), pt.getPredicate());
            c = config.transform(pt.target, newSemCtx);
        }
		else {
			c = config.transform(pt.target);
		}

		if ( debug ) System.out.println("config from pred transition="+c);
        return c;
	}

	@NotNull
	protected ATNConfig ruleTransition(@NotNull ATNConfig config, @NotNull RuleTransition t, @Nullable PredictionContextCache contextCache) {
		if ( debug ) {
			System.out.println("CALL rule "+getRuleName(t.target.ruleIndex)+
							   ", ctx="+config.getContext());
		}

		ATNState returnState = t.followState;
		PredictionContext newContext;

		if (optimize_tail_calls && t.optimizedTailCall && (!tail_call_preserves_sll || !PredictionContext.isEmptyLocal(config.getContext()))) {
			newContext = config.getContext();
		}
		else if (contextCache != null) {
			newContext = contextCache.getChild(config.getContext(), returnState.stateNumber);
		}
		else {
			newContext = config.getContext().getChild(returnState.stateNumber);
		}

		return config.transform(t.target, newContext);
	}

	private static final Comparator<ATNConfig> STATE_ALT_SORT_COMPARATOR =
		new Comparator<ATNConfig>() {

			@Override
			public int compare(ATNConfig o1, ATNConfig o2) {
				int diff = o1.getState().getNonStopStateNumber() - o2.getState().getNonStopStateNumber();
				if (diff != 0) {
					return diff;
				}

				diff = o1.getAlt() - o2.getAlt();
				if (diff != 0) {
					return diff;
				}

				return 0;
			}

		};

	private BitSet isConflicted(@NotNull ATNConfigSet configset, PredictionContextCache contextCache) {
		if (configset.getUniqueAlt() != ATN.INVALID_ALT_NUMBER || configset.size() <= 1) {
			return null;
		}

		List<ATNConfig> configs = new ArrayList<ATNConfig>(configset);
		Collections.sort(configs, STATE_ALT_SORT_COMPARATOR);

		boolean exact = !configset.getDipsIntoOuterContext() && predictionMode == PredictionMode.LL_EXACT_AMBIG_DETECTION;
		BitSet alts = new BitSet();
		int minAlt = configs.get(0).getAlt();
		alts.set(minAlt);

		/* Quick checks come first (single pass, no context joining):
		 *  1. Make sure first config in the sorted list predicts the minimum
		 *     represented alternative.
		 *  2. Make sure every represented state has at least one configuration
		 *     which predicts the minimum represented alternative.
		 *  3. (exact only) make sure every represented state has at least one
		 *     configuration which predicts each represented alternative.
		 */

		// quick check 1 & 2 => if we assume #1 holds and check #2 against the
		// minAlt from the first state, #2 will fail if the assumption was
		// incorrect
		int currentState = configs.get(0).getState().getNonStopStateNumber();
		for (int i = 0; i < configs.size(); i++) {
			ATNConfig config = configs.get(i);
			int stateNumber = config.getState().getNonStopStateNumber();
			if (stateNumber != currentState) {
				if (config.getAlt() != minAlt) {
					return null;
				}

				currentState = stateNumber;
			}
		}

		BitSet representedAlts = null;
		if (exact) {
			currentState = configs.get(0).getState().getNonStopStateNumber();

			// get the represented alternatives of the first state
			representedAlts = new BitSet();
			int maxAlt = minAlt;
			for (int i = 0; i < configs.size(); i++) {
				ATNConfig config = configs.get(i);
				if (config.getState().getNonStopStateNumber() != currentState) {
					break;
				}

				int alt = config.getAlt();
				representedAlts.set(alt);
				maxAlt = alt;
			}

			// quick check #3:
			currentState = configs.get(0).getState().getNonStopStateNumber();
			int currentAlt = minAlt;
			for (int i = 0; i < configs.size(); i++) {
				ATNConfig config = configs.get(i);
				int stateNumber = config.getState().getNonStopStateNumber();
				int alt = config.getAlt();
				if (stateNumber != currentState) {
					if (currentAlt != maxAlt) {
						return null;
					}

					currentState = stateNumber;
					currentAlt = minAlt;
				}
				else if (alt != currentAlt) {
					if (alt != representedAlts.nextSetBit(currentAlt + 1)) {
						return null;
					}
					
					currentAlt = alt;
				}
			}
		}

		currentState = configs.get(0).getState().getNonStopStateNumber();
		int firstIndexCurrentState = 0;
		int lastIndexCurrentStateMinAlt = 0;
		PredictionContext joinedCheckContext = configs.get(0).getContext();
		for (int i = 1; i < configs.size(); i++) {
			ATNConfig config = configs.get(i);
			if (config.getAlt() != minAlt) {
				break;
			}

			if (config.getState().getNonStopStateNumber() != currentState) {
				break;
			}

			lastIndexCurrentStateMinAlt = i;
			joinedCheckContext = contextCache.join(joinedCheckContext, configs.get(i).getContext());
		}

		for (int i = lastIndexCurrentStateMinAlt + 1; i < configs.size(); i++) {
			ATNConfig config = configs.get(i);
			ATNState state = config.getState();
			alts.set(config.getAlt());
			if (state.getNonStopStateNumber() != currentState) {
				currentState = state.getNonStopStateNumber();
				firstIndexCurrentState = i;
				lastIndexCurrentStateMinAlt = i;
				joinedCheckContext = config.getContext();
				for (int j = firstIndexCurrentState + 1; j < configs.size(); j++) {
					ATNConfig config2 = configs.get(j);
					if (config2.getAlt() != minAlt) {
						break;
					}

					if (config2.getState().getNonStopStateNumber() != currentState) {
						break;
					}

					lastIndexCurrentStateMinAlt = j;
					joinedCheckContext = contextCache.join(joinedCheckContext, config2.getContext());
				}

				i = lastIndexCurrentStateMinAlt;
				continue;
			}

			PredictionContext joinedCheckContext2 = config.getContext();
			int currentAlt = config.getAlt();
			int lastIndexCurrentStateCurrentAlt = i;
			for (int j = lastIndexCurrentStateCurrentAlt + 1; j < configs.size(); j++) {
				ATNConfig config2 = configs.get(j);
				if (config2.getAlt() != currentAlt) {
					break;
				}

				if (config2.getState().getNonStopStateNumber() != currentState) {
					break;
				}

				lastIndexCurrentStateCurrentAlt = j;
				joinedCheckContext2 = contextCache.join(joinedCheckContext2, config2.getContext());
			}

			i = lastIndexCurrentStateCurrentAlt;

			if (exact) {
				if (!joinedCheckContext.equals(joinedCheckContext2)) {
					return null;
				}
			}
			else {
				PredictionContext check = contextCache.join(joinedCheckContext, joinedCheckContext2);
				if (!joinedCheckContext.equals(check)) {
					return null;
				}
			}

			if (!exact && optimize_hidden_conflicted_configs) {
				for (int j = firstIndexCurrentState; j <= lastIndexCurrentStateMinAlt; j++) {
					ATNConfig checkConfig = configs.get(j);

					if (checkConfig.getSemanticContext() != SemanticContext.NONE
						&& !checkConfig.getSemanticContext().equals(config.getSemanticContext()))
					{
						continue;
					}

					if (joinedCheckContext != checkConfig.getContext()) {
						PredictionContext check = contextCache.join(checkConfig.getContext(), config.getContext());
						if (!checkConfig.getContext().equals(check)) {
							continue;
						}
					}

					config.setHidden(true);
				}
			}
		}

		return alts;
	}

	protected BitSet getConflictingAltsFromConfigSet(ATNConfigSet configs) {
		BitSet conflictingAlts = configs.getConflictingAlts();
		if ( conflictingAlts == null && configs.getUniqueAlt()!= ATN.INVALID_ALT_NUMBER ) {
			conflictingAlts = new BitSet();
			conflictingAlts.set(configs.getUniqueAlt());
		}

		return conflictingAlts;
	}

	protected int resolveToMinAlt(@NotNull DFAState D, BitSet conflictingAlts) {
		// kill dead alts so we don't chase them ever
//		killAlts(conflictingAlts, D.configset);
		D.prediction = conflictingAlts.nextSetBit(0);
		if ( debug ) System.out.println("RESOLVED TO "+D.prediction+" for "+D);
		return D.prediction;
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

	public String getLookaheadName(TokenStream<?> input) {
		return getTokenName(input.LA(1));
	}

	public void dumpDeadEndConfigs(@NotNull NoViableAltException nvae) {
		System.err.println("dead end configs: ");
		for (ATNConfig c : nvae.getDeadEndConfigs()) {
			String trans = "no edges";
			if ( c.getState().getNumberOfOptimizedTransitions()>0 ) {
				Transition t = c.getState().getOptimizedTransition(0);
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
	protected NoViableAltException noViableAlt(@NotNull TokenStream<? extends Symbol> input,
											@NotNull ParserRuleContext<Symbol> outerContext,
											@NotNull ATNConfigSet configs,
											int startIndex)
	{
		return new NoViableAltException(parser, input,
											input.get(startIndex),
											input.LT(1),
											configs, outerContext);
	}

	protected int getUniqueAlt(@NotNull Collection<ATNConfig> configs) {
		int alt = ATN.INVALID_ALT_NUMBER;
		for (ATNConfig c : configs) {
			if ( alt == ATN.INVALID_ALT_NUMBER ) {
				alt = c.getAlt(); // found first alt
			}
			else if ( c.getAlt()!=alt ) {
				return ATN.INVALID_ALT_NUMBER;
			}
		}
		return alt;
	}

	protected boolean configWithAltAtStopState(@NotNull Collection<ATNConfig> configs, int alt) {
		for (ATNConfig c : configs) {
			if ( c.getAlt() == alt ) {
				if ( c.getState() instanceof RuleStopState ) {
					return true;
				}
			}
		}
		return false;
	}

	@NotNull
	protected DFAState addDFAEdge(@NotNull DFA dfa,
								  @NotNull DFAState fromState,
								  int t,
								  IntegerList contextTransitions,
								  @NotNull ATNConfigSet toConfigs,
								  PredictionContextCache contextCache)
	{
		assert dfa.isContextSensitive() || contextTransitions == null || contextTransitions.isEmpty();

		DFAState from = fromState;
		DFAState to = addDFAState(dfa, toConfigs, contextCache);

		if (contextTransitions != null) {
			for (int context : contextTransitions.toArray()) {
				if (context == PredictionContext.EMPTY_FULL_STATE_KEY) {
					if (from.configs.isOutermostConfigSet()) {
						continue;
					}
				}

				from.setContextSensitive(atn);
				from.setContextSymbol(t);
				DFAState next = from.getContextTarget(context);
				if (next != null) {
					from = next;
					continue;
				}

				next = addDFAContextState(dfa, from.configs, context, contextCache);
				assert context != PredictionContext.EMPTY_FULL_STATE_KEY || next.configs.isOutermostConfigSet();
				from.setContextTarget(context, next);
				from = next;
			}
		}

        if ( debug ) System.out.println("EDGE "+from+" -> "+to+" upon "+getTokenName(t));
		addDFAEdge(from, t, to);
		if ( debug ) System.out.println("DFA=\n"+dfa.toString(parser!=null?parser.getTokenNames():null, parser!=null?parser.getRuleNames():null));
		return to;
	}

	protected void addDFAEdge(@Nullable DFAState p, int t, @Nullable DFAState q) {
		if ( p!=null ) {
			p.setTarget(t, q);
		}
	}

	/** See comment on LexerInterpreter.addDFAState. */
	@NotNull
	protected DFAState addDFAContextState(@NotNull DFA dfa, @NotNull ATNConfigSet configs, int returnContext, PredictionContextCache contextCache) {
		if (returnContext != PredictionContext.EMPTY_FULL_STATE_KEY) {
			ATNConfigSet contextConfigs = new ATNConfigSet();
			for (ATNConfig config : configs) {
				contextConfigs.add(config.appendContext(returnContext, contextCache));
			}

			return addDFAState(dfa, contextConfigs, contextCache);
		}
		else {
			assert !configs.isOutermostConfigSet() : "Shouldn't be adding a duplicate edge.";
			configs = configs.clone(true);
			configs.setOutermostConfigSet(true);
			return addDFAState(dfa, configs, contextCache);
		}
	}

	/** See comment on LexerInterpreter.addDFAState. */
	@NotNull
	protected DFAState addDFAState(@NotNull DFA dfa, @NotNull ATNConfigSet configs, PredictionContextCache contextCache) {
		if (!configs.isReadOnly()) {
			configs.optimizeConfigs(this);
		}

		DFAState proposed = createDFAState(configs);
		DFAState existing = dfa.states.get(proposed);
		if ( existing!=null ) return existing;

		if (!configs.isReadOnly()) {
			if (configs.getConflictingAlts() == null) {
				configs.setConflictingAlts(isConflicted(configs, contextCache));
				if (optimize_hidden_conflicted_configs && configs.getConflictingAlts() != null) {
					int size = configs.size();
					configs.stripHiddenConfigs();
					if (configs.size() < size) {
						proposed = createDFAState(configs);
						existing = dfa.states.get(proposed);
						if ( existing!=null ) return existing;
					}
				}
			}
		}

		DFAState newState = createDFAState(configs.clone(true));
		DecisionState decisionState = atn.getDecisionState(dfa.decision);
		int predictedAlt = getUniqueAlt(configs);
		if ( predictedAlt!=ATN.INVALID_ALT_NUMBER ) {
			newState.isAcceptState = true;
			newState.prediction = predictedAlt;
		} else if (configs.getConflictingAlts() != null) {
			newState.isAcceptState = true;
			newState.prediction = resolveToMinAlt(newState, newState.configs.getConflictingAlts());
		}

		if (newState.isAcceptState && configs.hasSemanticContext()) {
			predicateDFAState(newState, configs, decisionState.getNumberOfTransitions());
		}

		DFAState added = dfa.addState(newState);
        if ( debug && added == newState ) System.out.println("adding new DFA state: "+newState);
		return added;
	}

	@NotNull
	protected DFAState createDFAState(@NotNull ATNConfigSet configs) {
		return new DFAState(configs, -1, atn.maxTokenType);
	}

	protected void reportAttemptingFullContext(DFA dfa, @Nullable BitSet conflictingAlts, SimulatorState<Symbol> conflictState, int startIndex, int stopIndex) {
        if ( debug || retry_debug ) {
			Interval interval = Interval.of(startIndex, stopIndex);
            System.out.println("reportAttemptingFullContext decision="+dfa.decision+":"+conflictState.s0.configs+
                               ", input="+parser.getInputStream().getText(interval));
        }
        if ( parser!=null ) parser.getErrorListenerDispatch().reportAttemptingFullContext(parser, dfa, startIndex, stopIndex, conflictingAlts, conflictState);
    }

	protected void reportContextSensitivity(DFA dfa, int prediction, SimulatorState<Symbol> acceptState, int startIndex, int stopIndex) {
        if ( debug || retry_debug ) {
			Interval interval = Interval.of(startIndex, stopIndex);
            System.out.println("reportContextSensitivity decision="+dfa.decision+":"+acceptState.s0.configs+
                               ", input="+parser.getInputStream().getText(interval));
        }
        if ( parser!=null ) parser.getErrorListenerDispatch().reportContextSensitivity(parser, dfa, startIndex, stopIndex, prediction, acceptState);
    }

    /** If context sensitive parsing, we know it's ambiguity not conflict */
    protected void reportAmbiguity(@NotNull DFA dfa, DFAState D, int startIndex, int stopIndex,
								   boolean exact,
								   @Nullable BitSet ambigAlts,
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
                               ", input="+parser.getInputStream().getText(interval));
        }
        if ( parser!=null ) parser.getErrorListenerDispatch().reportAmbiguity(parser, dfa, startIndex, stopIndex,
																			  exact, ambigAlts, configs);
    }

	protected final int getReturnState(RuleContext<?> context) {
		if (context.isEmpty()) {
			return PredictionContext.EMPTY_FULL_STATE_KEY;
		}

		ATNState state = atn.states.get(context.invokingState);
		RuleTransition transition = (RuleTransition)state.transition(0);
		return transition.followState.stateNumber;
	}

	protected final <T extends Token> ParserRuleContext<T> skipTailCalls(ParserRuleContext<T> context) {
		if (!optimize_tail_calls) {
			return context;
		}

		while (!context.isEmpty()) {
			ATNState state = atn.states.get(context.invokingState);
			assert state.getNumberOfTransitions() == 1 && state.transition(0).getSerializationType() == Transition.RULE;
			RuleTransition transition = (RuleTransition)state.transition(0);
			if (!transition.tailCall) {
				break;
			}

			context = context.getParent();
		}

		return context;
	}

}
