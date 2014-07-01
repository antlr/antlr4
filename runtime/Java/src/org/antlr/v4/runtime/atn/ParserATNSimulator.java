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

import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.FailedPredicateException;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The embodiment of the adaptive LL(*), ALL(*), parsing strategy.
 *
 * <p>
 * The basic complexity of the adaptive strategy makes it harder to understand.
 * We begin with ATN simulation to build paths in a DFA. Subsequent prediction
 * requests go through the DFA first. If they reach a state without an edge for
 * the current symbol, the algorithm fails over to the ATN simulation to
 * complete the DFA path for the current input (until it finds a conflict state
 * or uniquely predicting state).</p>
 *
 * <p>
 * All of that is done without using the outer context because we want to create
 * a DFA that is not dependent upon the rule invocation stack when we do a
 * prediction. One DFA works in all contexts. We avoid using context not
 * necessarily because it's slower, although it can be, but because of the DFA
 * caching problem. The closure routine only considers the rule invocation stack
 * created during prediction beginning in the decision rule. For example, if
 * prediction occurs without invoking another rule's ATN, there are no context
 * stacks in the configurations. When lack of context leads to a conflict, we
 * don't know if it's an ambiguity or a weakness in the strong LL(*) parsing
 * strategy (versus full LL(*)).</p>
 *
 * <p>
 * When SLL yields a configuration set with conflict, we rewind the input and
 * retry the ATN simulation, this time using full outer context without adding
 * to the DFA. Configuration context stacks will be the full invocation stacks
 * from the start rule. If we get a conflict using full context, then we can
 * definitively say we have a true ambiguity for that input sequence. If we
 * don't get a conflict, it implies that the decision is sensitive to the outer
 * context. (It is not context-sensitive in the sense of context-sensitive
 * grammars.)</p>
 *
 * <p>
 * The next time we reach this DFA state with an SLL conflict, through DFA
 * simulation, we will again retry the ATN simulation using full context mode.
 * This is slow because we can't save the results and have to "interpret" the
 * ATN each time we get that input.</p>
 *
 * <p>
 * <strong>CACHING FULL CONTEXT PREDICTIONS</strong></p>
 *
 * <p>
 * We could cache results from full context to predicted alternative easily and
 * that saves a lot of time but doesn't work in presence of predicates. The set
 * of visible predicates from the ATN start state changes depending on the
 * context, because closure can fall off the end of a rule. I tried to cache
 * tuples (stack context, semantic context, predicted alt) but it was slower
 * than interpreting and much more complicated. Also required a huge amount of
 * memory. The goal is not to create the world's fastest parser anyway. I'd like
 * to keep this algorithm simple. By launching multiple threads, we can improve
 * the speed of parsing across a large number of files.</p>
 *
 * <p>
 * There is no strict ordering between the amount of input used by SLL vs LL,
 * which makes it really hard to build a cache for full context. Let's say that
 * we have input A B C that leads to an SLL conflict with full context X. That
 * implies that using X we might only use A B but we could also use A B C D to
 * resolve conflict. Input A B C D could predict alternative 1 in one position
 * in the input and A B C E could predict alternative 2 in another position in
 * input. The conflicting SLL configurations could still be non-unique in the
 * full context prediction, which would lead us to requiring more input than the
 * original A B C.	To make a	prediction cache work, we have to track	the exact
 * input	used during the previous prediction. That amounts to a cache that maps
 * X to a specific DFA for that context.</p>
 *
 * <p>
 * Something should be done for left-recursive expression predictions. They are
 * likely LL(1) + pred eval. Easier to do the whole SLL unless error and retry
 * with full LL thing Sam does.</p>
 *
 * <p>
 * <strong>AVOIDING FULL CONTEXT PREDICTION</strong></p>
 *
 * <p>
 * We avoid doing full context retry when the outer context is empty, we did not
 * dip into the outer context by falling off the end of the decision state rule,
 * or when we force SLL mode.</p>
 *
 * <p>
 * As an example of the not dip into outer context case, consider as super
 * constructor calls versus function calls. One grammar might look like
 * this:</p>
 *
 * <pre>
 * ctorBody
 *   : '{' superCall? stat* '}'
 *   ;
 * </pre>
 *
 * <p>
 * Or, you might see something like</p>
 *
 * <pre>
 * stat
 *   : superCall ';'
 *   | expression ';'
 *   | ...
 *   ;
 * </pre>
 *
 * <p>
 * In both cases I believe that no closure operations will dip into the outer
 * context. In the first case ctorBody in the worst case will stop at the '}'.
 * In the 2nd case it should stop at the ';'. Both cases should stay within the
 * entry rule and not dip into the outer context.</p>
 *
 * <p>
 * <strong>PREDICATES</strong></p>
 *
 * <p>
 * Predicates are always evaluated if present in either SLL or LL both. SLL and
 * LL simulation deals with predicates differently. SLL collects predicates as
 * it performs closure operations like ANTLR v3 did. It delays predicate
 * evaluation until it reaches and accept state. This allows us to cache the SLL
 * ATN simulation whereas, if we had evaluated predicates on-the-fly during
 * closure, the DFA state configuration sets would be different and we couldn't
 * build up a suitable DFA.</p>
 *
 * <p>
 * When building a DFA accept state during ATN simulation, we evaluate any
 * predicates and return the sole semantically valid alternative. If there is
 * more than 1 alternative, we report an ambiguity. If there are 0 alternatives,
 * we throw an exception. Alternatives without predicates act like they have
 * true predicates. The simple way to think about it is to strip away all
 * alternatives with false predicates and choose the minimum alternative that
 * remains.</p>
 *
 * <p>
 * When we start in the DFA and reach an accept state that's predicated, we test
 * those and return the minimum semantically viable alternative. If no
 * alternatives are viable, we throw an exception.</p>
 *
 * <p>
 * During full LL ATN simulation, closure always evaluates predicates and
 * on-the-fly. This is crucial to reducing the configuration set size during
 * closure. It hits a landmine when parsing with the Java grammar, for example,
 * without this on-the-fly evaluation.</p>
 *
 * <p>
 * <strong>SHARING DFA</strong></p>
 *
 * <p>
 * All instances of the same parser share the same decision DFAs through a
 * static field. Each instance gets its own ATN simulator but they share the
 * same {@link ATN#decisionToDFA} field. They also share a
 * {@link PredictionContextCache} object that makes sure that all
 * {@link PredictionContext} objects are shared among the DFA states. This makes
 * a big size difference.</p>
 *
 * <p>
 * <strong>THREAD SAFETY</strong></p>
 *
 * <p>
 * The {@link ParserATNSimulator} locks on the {@link ATN#decisionToDFA} field when
 * it adds a new DFA object to that array. {@link #addDFAEdge}
 * locks on the DFA for the current decision when setting the
 * {@link DFAState#edges} field. {@link #addDFAState} locks on
 * the DFA for the current decision when looking up a DFA state to see if it
 * already exists. We must make sure that all requests to add DFA states that
 * are equivalent result in the same shared DFA object. This is because lots of
 * threads will be trying to update the DFA at once. The
 * {@link #addDFAState} method also locks inside the DFA lock
 * but this time on the shared context cache when it rebuilds the
 * configurations' {@link PredictionContext} objects using cached
 * subgraphs/nodes. No other locking occurs, even during DFA simulation. This is
 * safe as long as we can guarantee that all threads referencing
 * {@code s.edge[t]} get the same physical target {@link DFAState}, or
 * {@code null}. Once into the DFA, the DFA simulation does not reference the
 * {@link DFA#states} map. It follows the {@link DFAState#edges} field to new
 * targets. The DFA simulator will either find {@link DFAState#edges} to be
 * {@code null}, to be non-{@code null} and {@code dfa.edges[t]} null, or
 * {@code dfa.edges[t]} to be non-null. The
 * {@link #addDFAEdge} method could be racing to set the field
 * but in either case the DFA simulator works; if {@code null}, and requests ATN
 * simulation. It could also race trying to get {@code dfa.edges[t]}, but either
 * way it will work because it's not doing a test and set operation.</p>
 *
 * <p>
 * <strong>Starting with SLL then failing to combined SLL/LL (Two-Stage
 * Parsing)</strong></p>
 *
 * <p>
 * Sam pointed out that if SLL does not give a syntax error, then there is no
 * point in doing full LL, which is slower. We only have to try LL if we get a
 * syntax error. For maximum speed, Sam starts the parser set to pure SLL
 * mode with the {@link BailErrorStrategy}:</p>
 *
 * <pre>
 * parser.{@link Parser#getInterpreter() getInterpreter()}.{@link #setPredictionMode setPredictionMode}{@code (}{@link PredictionMode#SLL}{@code )};
 * parser.{@link Parser#setErrorHandler setErrorHandler}(new {@link BailErrorStrategy}());
 * </pre>
 *
 * <p>
 * If it does not get a syntax error, then we're done. If it does get a syntax
 * error, we need to retry with the combined SLL/LL strategy.</p>
 *
 * <p>
 * The reason this works is as follows. If there are no SLL conflicts, then the
 * grammar is SLL (at least for that input set). If there is an SLL conflict,
 * the full LL analysis must yield a set of viable alternatives which is a
 * subset of the alternatives reported by SLL. If the LL set is a singleton,
 * then the grammar is LL but not SLL. If the LL set is the same size as the SLL
 * set, the decision is SLL. If the LL set has size &gt; 1, then that decision
 * is truly ambiguous on the current input. If the LL set is smaller, then the
 * SLL conflict resolution might choose an alternative that the full LL would
 * rule out as a possibility based upon better context information. If that's
 * the case, then the SLL parse will definitely get an error because the full LL
 * analysis says it's not viable. If SLL conflict resolution chooses an
 * alternative within the LL set, them both SLL and LL would choose the same
 * alternative because they both choose the minimum of multiple conflicting
 * alternatives.</p>
 *
 * <p>
 * Let's say we have a set of SLL conflicting alternatives {@code {1, 2, 3}} and
 * a smaller LL set called <em>s</em>. If <em>s</em> is {@code {2, 3}}, then SLL
 * parsing will get an error because SLL will pursue alternative 1. If
 * <em>s</em> is {@code {1, 2}} or {@code {1, 3}} then both SLL and LL will
 * choose the same alternative because alternative one is the minimum of either
 * set. If <em>s</em> is {@code {2}} or {@code {3}} then SLL will get a syntax
 * error. If <em>s</em> is {@code {1}} then SLL will succeed.</p>
 *
 * <p>
 * Of course, if the input is invalid, then we will get an error for sure in
 * both SLL and LL parsing. Erroneous input will therefore require 2 passes over
 * the input.</p>
 */
public class ParserATNSimulator extends ATNSimulator {
	public static final boolean debug = false;
	public static final boolean dfa_debug = false;
	public static final boolean retry_debug = false;

	@NotNull
	private PredictionMode predictionMode = PredictionMode.LL;
	public boolean force_global_context = false;
	public boolean always_try_local_context = true;

	/**
	 * Determines whether the DFA is used for full-context predictions. When
	 * {@code true}, the DFA stores transition information for both full-context
	 * and SLL parsing; otherwise, the DFA only stores SLL transition
	 * information.
	 *
	 * <p>
	 * For some grammars, enabling the full-context DFA can result in a
	 * substantial performance improvement. However, this improvement typically
	 * comes at the expense of memory used for storing the cached DFA states,
	 * configuration sets, and prediction contexts.</p>
	 *
	 * <p>
	 * The default value is {@code false}.</p>
	 */
	public boolean enable_global_context_dfa = false;
	public boolean optimize_unique_closure = true;
	public boolean optimize_ll1 = true;
	public boolean optimize_hidden_conflicted_configs = false;
	public boolean optimize_tail_calls = true;
	public boolean tail_call_preserves_sll = true;
	public boolean treat_sllk1_conflict_as_ambiguity = false;

	@Nullable
	protected final Parser parser;

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

	public ParserATNSimulator(@Nullable Parser parser, @NotNull ATN atn) {
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

	public int adaptivePredict(@NotNull TokenStream input, int decision,
							   @Nullable ParserRuleContext outerContext)
	{
		return adaptivePredict(input, decision, outerContext, false);
	}

	public int adaptivePredict(@NotNull TokenStream input,
							   int decision,
							   @Nullable ParserRuleContext outerContext,
							   boolean useContext)
	{
		DFA dfa = atn.decisionToDFA[decision];
		assert dfa != null;
		if (optimize_ll1 && !dfa.isPrecedenceDfa() && !dfa.isEmpty()) {
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

		SimulatorState state = null;
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

	protected SimulatorState getStartState(@NotNull DFA dfa,
										@NotNull TokenStream input,
										@NotNull ParserRuleContext outerContext,
										boolean useContext) {

		if (!useContext) {
			if (dfa.isPrecedenceDfa()) {
				// the start state for a precedence DFA depends on the current
				// parser precedence, and is provided by a DFA method.
				DFAState state = dfa.getPrecedenceStartState(parser.getPrecedence(), false);
				if (state == null) {
					return null;
				}

				return new SimulatorState(outerContext, state, false, outerContext);
			}
			else {
				if (dfa.s0.get() == null) {
					return null;
				}

				return new SimulatorState(outerContext, dfa.s0.get(), false, outerContext);
			}
		}

		if (!enable_global_context_dfa) {
			return null;
		}

		ParserRuleContext remainingContext = outerContext;
		assert outerContext != null;
		DFAState s0;
		if (dfa.isPrecedenceDfa()) {
			s0 = dfa.getPrecedenceStartState(parser.getPrecedence(), true);
		}
		else {
			s0 = dfa.s0full.get();
		}

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

		return new SimulatorState(outerContext, s0, useContext, remainingContext);
	}

	protected int execDFA(@NotNull DFA dfa,
					   @NotNull TokenStream input, int startIndex,
					   @NotNull SimulatorState state)
    {
		ParserRuleContext outerContext = state.outerContext;
		if ( dfa_debug ) System.out.println("DFA decision "+dfa.decision+
											" exec LA(1)=="+ getLookaheadName(input) +
											", outerContext="+outerContext.toString(parser));
		if ( dfa_debug ) System.out.print(dfa.toString(parser.getTokenNames(), parser.getRuleNames()));
		DFAState acceptState = null;
		DFAState s = state.s0;

		int t = input.LA(1);
		ParserRuleContext remainingOuterContext = state.remainingOuterContext;

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
						SimulatorState initialState = new SimulatorState(state.outerContext, s, state.useContext, remainingOuterContext);
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
			DFAState target = getExistingTargetState(s, t);
			if ( target == null ) {
				if ( dfa_debug && t>=0 ) System.out.println("no edge for "+parser.getTokenNames()[t]);
				int alt;
				if ( dfa_debug ) {
					Interval interval = Interval.of(startIndex, parser.getInputStream().index());
					System.out.println("ATN exec upon "+
									   parser.getInputStream().getText(interval) +
									   " at DFA state "+s.stateNumber);
				}

				SimulatorState initialState = new SimulatorState(outerContext, s, state.useContext, remainingOuterContext);
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
				SimulatorState errorState = new SimulatorState(outerContext, s, state.useContext, remainingOuterContext);
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
						SimulatorState conflictState = new SimulatorState(outerContext, acceptState, state.useContext, remainingOuterContext);
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
					   @NotNull TokenStream input, int startIndex,
					   @NotNull SimulatorState initialState)
	{
		if ( debug ) System.out.println("execATN decision "+dfa.decision+" exec LA(1)=="+ getLookaheadName(input));

		final ParserRuleContext outerContext = initialState.outerContext;
		final boolean useContext = initialState.useContext;

		int t = input.LA(1);

		SimulatorState previous = initialState;

		PredictionContextCache contextCache = new PredictionContextCache();
		while (true) { // while more work
			SimulatorState nextState = computeReachSet(dfa, previous, t, contextCache);
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
						&& !dfa.isPrecedenceDfa()
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
					SimulatorState fullContextState = computeStartState(dfa, outerContext, true);
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

	/**
	 * This method is used to improve the localization of error messages by
	 * choosing an alternative rather than throwing a
	 * {@link NoViableAltException} in particular prediction scenarios where the
	 * {@link #ERROR} state was reached during ATN simulation.
	 *
	 * <p>
	 * The default implementation of this method uses the following
	 * algorithm to identify an ATN configuration which successfully parsed the
	 * decision entry rule. Choosing such an alternative ensures that the
	 * {@link ParserRuleContext} returned by the calling rule will be complete
	 * and valid, and the syntax error will be reported later at a more
	 * localized location.</p>
	 *
	 * <ul>
	 * <li>If no configuration in {@code configs} reached the end of the
	 * decision rule, return {@link ATN#INVALID_ALT_NUMBER}.</li>
	 * <li>If all configurations in {@code configs} which reached the end of the
	 * decision rule predict the same alternative, return that alternative.</li>
	 * <li>If the configurations in {@code configs} which reached the end of the
	 * decision rule predict multiple alternatives (call this <em>S</em>),
	 * choose an alternative in the following order.
	 * <ol>
	 * <li>Filter the configurations in {@code configs} to only those
	 * configurations which remain viable after evaluating semantic predicates.
	 * If the set of these filtered configurations which also reached the end of
	 * the decision rule is not empty, return the minimum alternative
	 * represented in this set.</li>
	 * <li>Otherwise, choose the minimum alternative in <em>S</em>.</li>
	 * </ol>
	 * </li>
	 * </ul>
	 *
	 * <p>
	 * In some scenarios, the algorithm described above could predict an
	 * alternative which will result in a {@link FailedPredicateException} in
	 * parser. Specifically, this could occur if the <em>only</em> configuration
	 * capable of successfully parsing to the end of the decision rule is
	 * blocked by a semantic predicate. By choosing this alternative within
	 * {@link #adaptivePredict} instead of throwing a
	 * {@link NoViableAltException}, the resulting
	 * {@link FailedPredicateException} in the parser will identify the specific
	 * predicate which is preventing the parser from successfully parsing the
	 * decision rule, which helps developers identify and correct logic errors
	 * in semantic predicates.
	 * </p>
	 *
	 * @param input The input {@link TokenStream}
	 * @param startIndex The start index for the current prediction, which is
	 * the input index where any semantic context in {@code configs} should be
	 * evaluated
	 * @param previous The ATN simulation state immediately before the
	 * {@link #ERROR} state was reached
	 *
	 * @return The value to return from {@link #adaptivePredict}, or
	 * {@link ATN#INVALID_ALT_NUMBER} if a suitable alternative was not
	 * identified and {@link #adaptivePredict} should report an error instead.
	 */
	protected int handleNoViableAlt(@NotNull TokenStream input, int startIndex, @NotNull SimulatorState previous) {
		if (previous.s0 != null) {
			BitSet alts = new BitSet();
			int maxAlt = 0;
			for (ATNConfig config : previous.s0.configs) {
				if (config.getReachesIntoOuterContext() || config.getState() instanceof RuleStopState) {
					alts.set(config.getAlt());
					maxAlt = Math.max(maxAlt, config.getAlt());
				}
			}

			switch (alts.cardinality()) {
			case 0:
				break;

			case 1:
				return alts.nextSetBit(0);

			default:
				if (!previous.s0.configs.hasSemanticContext()) {
					// configs doesn't contain any predicates, so the predicate
					// filtering code below would be pointless
					return alts.nextSetBit(0);
				}

				/*
				 * Try to find a configuration set that not only dipped into the outer
				 * context, but also isn't eliminated by a predicate.
				 */
				ATNConfigSet filteredConfigs = new ATNConfigSet();
				for (ATNConfig config : previous.s0.configs) {
					if (config.getReachesIntoOuterContext() || config.getState() instanceof RuleStopState) {
						filteredConfigs.add(config);
					}
				}

				/* The following code blocks are adapted from predicateDFAState with
				 * the following key changes.
				 *
				 *  1. The code operates on an ATNConfigSet rather than a DFAState.
				 *  2. Predicates are collected for all alternatives represented in
				 *     filteredConfigs, rather than restricting the evaluation to
				 *     conflicting and/or unique configurations.
				 */
				SemanticContext[] altToPred = getPredsForAmbigAlts(alts, filteredConfigs, maxAlt);
				if (altToPred != null) {
					DFAState.PredPrediction[] predicates = getPredicatePredictions(alts, altToPred);
					if (predicates != null) {
						int stopIndex = input.index();
						try {
							input.seek(startIndex);
							BitSet filteredAlts = evalSemanticContext(predicates, previous.outerContext, false);
							if (!filteredAlts.isEmpty()) {
								return filteredAlts.nextSetBit(0);
							}
						}
						finally {
							input.seek(stopIndex);
						}
					}
				}

				return alts.nextSetBit(0);
			}
		}

		throw noViableAlt(input, previous.outerContext, previous.s0.configs, startIndex);
	}

	protected SimulatorState computeReachSet(DFA dfa, SimulatorState previous, int t, PredictionContextCache contextCache) {
		final boolean useContext = previous.useContext;
		ParserRuleContext remainingGlobalContext = previous.remainingOuterContext;

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
			return new SimulatorState(previous.outerContext, s, useContext, remainingGlobalContext);
		}

		final DFAState s0 = s;

		DFAState target = getExistingTargetState(s0, t);
		if (target == null) {
			Tuple2<DFAState, ParserRuleContext> result = computeTargetState(dfa, s0, remainingGlobalContext, t, useContext, contextCache);
			target = result.getItem1();
			remainingGlobalContext = result.getItem2();
		}

		if (target == ERROR) {
			return null;
		}

		assert !useContext || !target.configs.getDipsIntoOuterContext();
		return new SimulatorState(previous.outerContext, target, useContext, remainingGlobalContext);
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
	protected Tuple2<DFAState, ParserRuleContext> computeTargetState(@NotNull DFA dfa, @NotNull DFAState s, ParserRuleContext remainingGlobalContext, int t, boolean useContext, PredictionContextCache contextCache) {
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
						reachIntermediate.add(c.transform(target, false), contextCache);
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
			 * withheld in skippedStopStates, or when the current symbol is EOF.
			 */
			if (optimize_unique_closure && skippedStopStates == null && t != Token.EOF && reachIntermediate.getUniqueAlt() != ATN.INVALID_ALT_NUMBER) {
				reachIntermediate.setOutermostConfigSet(reach.isOutermostConfigSet());
				reach = reachIntermediate;
				break;
			}

			/* If the reach set could not be trivially determined, perform a closure
			 * operation on the intermediate set to compute its initial value.
			 */
			final boolean collectPredicates = false;
			boolean treatEofAsEpsilon = t == Token.EOF;
			closure(reachIntermediate, reach, collectPredicates, hasMoreContext, contextCache, treatEofAsEpsilon);
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
	protected SimulatorState computeStartState(DFA dfa,
											ParserRuleContext globalContext,
											boolean useContext)
	{
		DFAState s0 =
			dfa.isPrecedenceDfa() ? dfa.getPrecedenceStartState(parser.getPrecedence(), useContext) :
			useContext ? dfa.s0full.get() :
			dfa.s0.get();

		if (s0 != null) {
			if (!useContext) {
				return new SimulatorState(globalContext, s0, useContext, globalContext);
			}

			s0.setContextSensitive(atn);
		}

		final int decision = dfa.decision;
		@NotNull
		final ATNState p = dfa.atnStartState;

		int previousContext = 0;
		ParserRuleContext remainingGlobalContext = globalContext;
		PredictionContext initialContext = useContext ? PredictionContext.EMPTY_FULL : PredictionContext.EMPTY_LOCAL; // always at least the implicit call to start rule
		PredictionContextCache contextCache = new PredictionContextCache();
		if (useContext) {
			if (!enable_global_context_dfa) {
				while (remainingGlobalContext != null) {
					if (remainingGlobalContext.isEmpty()) {
						previousContext = PredictionContext.EMPTY_FULL_STATE_KEY;
						remainingGlobalContext = null;
					}
					else {
						previousContext = getReturnState(remainingGlobalContext);
						initialContext = initialContext.appendContext(previousContext, contextCache);
						remainingGlobalContext = remainingGlobalContext.getParent();
					}
				}
			}

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
			return new SimulatorState(globalContext, s0, useContext, remainingGlobalContext);
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
			closure(reachIntermediate, configs, collectPredicates, hasMoreContext, contextCache, false);
			boolean stepIntoGlobal = configs.getDipsIntoOuterContext();

			DFAState next;
			if (useContext && !enable_global_context_dfa) {
				s0 = addDFAState(dfa, configs, contextCache);
				break;
			}
			else if (s0 == null) {
				if (!dfa.isPrecedenceDfa() && dfa.atnStartState instanceof StarLoopEntryState) {
					if (((StarLoopEntryState)dfa.atnStartState).precedenceRuleDecision) {
						dfa.setPrecedenceDfa(true);
					}
				}

				if (!dfa.isPrecedenceDfa()) {
					AtomicReference<DFAState> reference = useContext ? dfa.s0full : dfa.s0;
					next = addDFAState(dfa, configs, contextCache);
					if (!reference.compareAndSet(null, next)) {
						next = reference.get();
					}
				}
				else {
					/* If this is a precedence DFA, we use applyPrecedenceFilter
					 * to convert the computed start state to a precedence start
					 * state. We then use DFA.setPrecedenceStartState to set the
					 * appropriate start state for the precedence level rather
					 * than simply setting DFA.s0.
					 */
					configs = applyPrecedenceFilter(configs, globalContext, contextCache);
					next = addDFAState(dfa, configs, contextCache);
					dfa.setPrecedenceStartState(parser.getPrecedence(), useContext, next);
				}
			}
			else {
				if (dfa.isPrecedenceDfa()) {
					configs = applyPrecedenceFilter(configs, globalContext, contextCache);
				}

				next = addDFAState(dfa, configs, contextCache);
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

		return new SimulatorState(globalContext, s0, useContext, remainingGlobalContext);
	}

	/**
	 * This method transforms the start state computed by
	 * {@link #computeStartState} to the special start state used by a
	 * precedence DFA for a particular precedence value. The transformation
	 * process applies the following changes to the start state's configuration
	 * set.
	 *
	 * <ol>
	 * <li>Evaluate the precedence predicates for each configuration using
	 * {@link SemanticContext#evalPrecedence}.</li>
	 * <li>Remove all configurations which predict an alternative greater than
	 * 1, for which another configuration that predicts alternative 1 is in the
	 * same ATN state with the same prediction context. This transformation is
	 * valid for the following reasons:
	 * <ul>
	 * <li>The closure block cannot contain any epsilon transitions which bypass
	 * the body of the closure, so all states reachable via alternative 1 are
	 * part of the precedence alternatives of the transformed left-recursive
	 * rule.</li>
	 * <li>The "primary" portion of a left recursive rule cannot contain an
	 * epsilon transition, so the only way an alternative other than 1 can exist
	 * in a state that is also reachable via alternative 1 is by nesting calls
	 * to the left-recursive rule, with the outer calls not being at the
	 * preferred precedence level.</li>
	 * </ul>
	 * </li>
	 * </ol>
	 *
	 * <p>
	 * The prediction context must be considered by this filter to address
	 * situations like the following.
	 * </p>
	 * <code>
	 * <pre>
	 * grammar TA;
	 * prog: statement* EOF;
	 * statement: letterA | statement letterA 'b' ;
	 * letterA: 'a';
	 * </pre>
	 * </code>
	 * <p>
	 * If the above grammar, the ATN state immediately before the token
	 * reference {@code 'a'} in {@code letterA} is reachable from the left edge
	 * of both the primary and closure blocks of the left-recursive rule
	 * {@code statement}. The prediction context associated with each of these
	 * configurations distinguishes between them, and prevents the alternative
	 * which stepped out to {@code prog} (and then back in to {@code statement}
	 * from being eliminated by the filter.
	 * </p>
	 *
	 * @param configs The configuration set computed by
	 * {@link #computeStartState} as the start state for the DFA.
	 * @return The transformed configuration set representing the start state
	 * for a precedence DFA at a particular precedence level (determined by
	 * calling {@link Parser#getPrecedence}).
	 */
	@NotNull
	protected ATNConfigSet applyPrecedenceFilter(@NotNull ATNConfigSet configs, ParserRuleContext globalContext, PredictionContextCache contextCache) {
		Map<Integer, PredictionContext> statesFromAlt1 = new HashMap<Integer, PredictionContext>();
		ATNConfigSet configSet = new ATNConfigSet();
		for (ATNConfig config : configs) {
			// handle alt 1 first
			if (config.getAlt() != 1) {
				continue;
			}

			SemanticContext updatedContext = config.getSemanticContext().evalPrecedence(parser, globalContext);
			if (updatedContext == null) {
				// the configuration was eliminated
				continue;
			}

			statesFromAlt1.put(config.getState().stateNumber, config.getContext());
			if (updatedContext != config.getSemanticContext()) {
				configSet.add(config.transform(config.getState(), updatedContext, false), contextCache);
			}
			else {
				configSet.add(config, contextCache);
			}
		}

		for (ATNConfig config : configs) {
			if (config.getAlt() == 1) {
				// already handled
				continue;
			}

			/* In the future, this elimination step could be updated to also
			 * filter the prediction context for alternatives predicting alt>1
			 * (basically a graph subtraction algorithm).
			 */
			PredictionContext context = statesFromAlt1.get(config.getState().stateNumber);
			if (context != null && context.equals(config.getContext())) {
				// eliminated
				continue;
			}

			configSet.add(config, contextCache);
		}

		return configSet;
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
			return null;
		}

//		System.out.println(Arrays.toString(altToPred)+"->"+pairs);
		return pairs.toArray(new DFAState.PredPrediction[pairs.size()]);
	}

	/** Look through a list of predicate/alt pairs, returning alts for the
	 *  pairs that win. A {@code null} predicate indicates an alt containing an
	 *  unpredicated config which behaves as "always true."
	 */
	protected BitSet evalSemanticContext(@NotNull DFAState.PredPrediction[] predPredictions,
										   ParserRuleContext outerContext,
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
						   @Nullable PredictionContextCache contextCache,
						   boolean treatEofAsEpsilon)
	{
		if (contextCache == null) {
			contextCache = PredictionContextCache.UNCACHED;
		}

		ATNConfigSet currentConfigs = sourceConfigs;
		Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
		while (currentConfigs.size() > 0) {
			ATNConfigSet intermediate = new ATNConfigSet();
			for (ATNConfig config : currentConfigs) {
				closure(config, configs, intermediate, closureBusy, collectPredicates, hasMoreContext, contextCache, 0, treatEofAsEpsilon);
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
						   int depth,
						   boolean treatEofAsEpsilon)
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
					closure(c, configs, intermediate, closureBusy, collectPredicates, hasMoreContexts, contextCache, depth - 1, treatEofAsEpsilon);
				}

				if (!hasEmpty || !hasMoreContexts) {
					return;
				}

				config = config.transform(config.getState(), PredictionContext.EMPTY_LOCAL, false);
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
					config = config.transform(config.getState(), PredictionContext.EMPTY_LOCAL, false);
				}
			}
		}

		ATNState p = config.getState();
		// optimization
		if ( !p.onlyHasEpsilonTransitions() ) {
            configs.add(config, contextCache);
			// make sure to not return here, because EOF transitions can act as
			// both epsilon transitions and non-epsilon transitions.
            if ( debug ) System.out.println("added config "+configs);
        }

        for (int i=0; i<p.getNumberOfOptimizedTransitions(); i++) {
            Transition t = p.getOptimizedTransition(i);
            boolean continueCollecting =
				!(t instanceof ActionTransition) && collectPredicates;
            ATNConfig c = getEpsilonTarget(config, t, continueCollecting, depth == 0, contextCache, treatEofAsEpsilon);
			if ( c!=null ) {
				if (t instanceof RuleTransition) {
					if (intermediate != null && !collectPredicates) {
						intermediate.add(c, contextCache);
						continue;
					}
				}

				if (!t.isEpsilon() && !closureBusy.add(c)) {
					// avoid infinite recursion for EOF* and EOF+
					continue;
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

				closure(c, configs, intermediate, closureBusy, continueCollecting, hasMoreContexts, contextCache, newDepth, treatEofAsEpsilon);
			}
		}
	}

	@NotNull
	public String getRuleName(int index) {
		if ( parser!=null && index>=0 ) return parser.getRuleNames()[index];
		return "<rule "+index+">";
	}

	@Nullable
	protected ATNConfig getEpsilonTarget(@NotNull ATNConfig config, @NotNull Transition t, boolean collectPredicates, boolean inContext, PredictionContextCache contextCache, boolean treatEofAsEpsilon) {
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
			return config.transform(t.target, false);

		case Transition.ATOM:
		case Transition.RANGE:
		case Transition.SET:
			// EOF transitions act like epsilon transitions after the first EOF
			// transition is traversed
			if (treatEofAsEpsilon) {
				if (t.matches(Token.EOF, 0, 1)) {
					return config.transform(t.target, false);
				}
			}

			return null;

		default:
			return null;
		}
	}

	@NotNull
	protected ATNConfig actionTransition(@NotNull ATNConfig config, @NotNull ActionTransition t) {
		if ( debug ) System.out.println("ACTION edge "+t.ruleIndex+":"+t.actionIndex);
		return config.transform(t.target, false);
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
            c = config.transform(pt.target, newSemCtx, false);
        }
		else {
			c = config.transform(pt.target, false);
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
            c = config.transform(pt.target, newSemCtx, false);
        }
		else {
			c = config.transform(pt.target, false);
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

		return config.transform(t.target, newContext, false);
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

	public String getLookaheadName(TokenStream input) {
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
	protected NoViableAltException noViableAlt(@NotNull TokenStream input,
											@NotNull ParserRuleContext outerContext,
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
		assert contextTransitions == null || contextTransitions.isEmpty() || dfa.isContextSensitive();

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
		final boolean enableDfa = enable_global_context_dfa || !configs.isOutermostConfigSet();
		if (enableDfa) {
			if (!configs.isReadOnly()) {
				configs.optimizeConfigs(this);
			}

			DFAState proposed = createDFAState(configs);
			DFAState existing = dfa.states.get(proposed);
			if ( existing!=null ) return existing;
		}

		if (!configs.isReadOnly()) {
			if (configs.getConflictingAlts() == null) {
				configs.setConflictingAlts(isConflicted(configs, contextCache));
				if (optimize_hidden_conflicted_configs && configs.getConflictingAlts() != null) {
					int size = configs.size();
					configs.stripHiddenConfigs();
					if (enableDfa && configs.size() < size) {
						DFAState proposed = createDFAState(configs);
						DFAState existing = dfa.states.get(proposed);
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

		if (!enableDfa) {
			return newState;
		}

		DFAState added = dfa.addState(newState);
        if ( debug && added == newState ) System.out.println("adding new DFA state: "+newState);
		return added;
	}

	@NotNull
	protected DFAState createDFAState(@NotNull ATNConfigSet configs) {
		return new DFAState(configs, -1, atn.maxTokenType);
	}

	protected void reportAttemptingFullContext(@NotNull DFA dfa, @Nullable BitSet conflictingAlts, @NotNull SimulatorState conflictState, int startIndex, int stopIndex) {
        if ( debug || retry_debug ) {
			Interval interval = Interval.of(startIndex, stopIndex);
            System.out.println("reportAttemptingFullContext decision="+dfa.decision+":"+conflictState.s0.configs+
                               ", input="+parser.getInputStream().getText(interval));
        }
        if ( parser!=null ) parser.getErrorListenerDispatch().reportAttemptingFullContext(parser, dfa, startIndex, stopIndex, conflictingAlts, conflictState);
    }

	protected void reportContextSensitivity(@NotNull DFA dfa, int prediction, @NotNull SimulatorState acceptState, int startIndex, int stopIndex) {
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

	protected final int getReturnState(RuleContext context) {
		if (context.isEmpty()) {
			return PredictionContext.EMPTY_FULL_STATE_KEY;
		}

		ATNState state = atn.states.get(context.invokingState);
		RuleTransition transition = (RuleTransition)state.transition(0);
		return transition.followState.stateNumber;
	}

	protected final ParserRuleContext skipTailCalls(ParserRuleContext context) {
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

	public Parser getParser() {
		return parser;
	}
}
