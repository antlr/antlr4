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
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.ArrayList;
import java.util.Arrays;
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
 ATN, there are no context stacks in the configurations. When this
 leads to a conflict, we don't know if it's an ambiguity or a
 weakness in the strong LL(*) parsing strategy (versus full
 LL(*)).

 So, we simply rewind and retry the ATN simulation again, this time
 using full outer context without adding to the DFA. Configuration context
 stacks will be the full invocation stack from the start rule. If
 we get a conflict using full context, then we can definitively
 say we have a true ambiguity for that input sequence. If we don't
 get a conflict, it implies that the decision is sensitive to the
 outer context. (It is not context-sensitive in the sense of
 context sensitive grammars.) We create a special DFA accept state
 that maps rule context to a predicted alternative. That is the
 only modification needed to handle full LL(*) vs SLL(*) prediction.

 So, the strategy is complex because we bounce back and forth from
 the ATN to the DFA, simultaneously performing predictions and
 extending the DFA according to previously unseen input
 sequences.

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

 When we are forced to do full context parsing, I mark the DFA state
 with isCtxSensitive=true when we reach conflict in SLL prediction.
 Any further DFA simulation that reaches that state will
 launch an ATN simulation to get the prediction, without updating the
 DFA or storing any context information.

 Predicates can be tested during SLL mode when we are sure that
 the conflicted state is a true ambiguity not an unknown conflict.
 This only happens with the special context circumstances mentioned above.
*/
public class ParserATNSimulator<Symbol extends Token> extends ATNSimulator {
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

	@Nullable
	protected final Parser parser;

	@NotNull
	public final DFA[] decisionToDFA;

	/** Do only local context prediction (SLL(k) style). */
	public boolean SLL = false;

	// LAME globals to avoid parameters!!!!! I need these down deep in predTransition
	protected TokenStream _input;
	protected int _startIndex;
	protected ParserRuleContext<?> _outerContext;

	/** Testing only! */
	public ParserATNSimulator(@NotNull ATN atn, @NotNull DFA[] decisionToDFA) {
		this(null, atn, decisionToDFA);
	}

	public ParserATNSimulator(@Nullable Parser parser, @NotNull ATN atn, @NotNull DFA[] decisionToDFA) {
		super(atn);
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
		_input = input;
		_startIndex = input.index();
		_outerContext = outerContext;
		predict_calls++;
		DFA dfa = decisionToDFA[decision];
		// First, synchronize on the array of DFA for this parser
		// so that we can get the DFA for a decision or create and set one
		if ( dfa==null || dfa.s0==null ) { // only create one if not there
			synchronized (decisionToDFA) {
				dfa = decisionToDFA[decision];
				if ( dfa==null || dfa.s0==null ) { // the usual double-check
					DecisionState startState = atn.decisionToState.get(decision);
					decisionToDFA[decision] = dfa = new DFA(startState, decision);
				}
			}
			// Now we are certain to have a specific decision's DFA
			// Synchronize on the DFA so that nobody can read or write
			// to it while we updated during ATN simulation
			synchronized (decisionToDFA[decision]) {
				return predictATN(dfa, input, outerContext);
			}
		}

		// We can start with an existing DFA
		synchronized (decisionToDFA[decision]) {
			// Only enter the DFA simulation if nobody else is playing with it.
			// This blocks multiple readonly simulations of the same DFA but that's
			// unlikely to happen a lot
			int m = input.mark();
			int index = input.index();
			try {
				int alt = execDFA(dfa, dfa.s0, input, index, outerContext);
				return alt;
			}
			finally {
				input.seek(index);
				input.release(m);
			}
		}
	}

	public int predictATN(@NotNull DFA dfa, @NotNull TokenStream input,
						  @Nullable ParserRuleContext<?> outerContext)
	{
		// caller must ensure current thread is sync'd on dfa
		if ( outerContext==null ) outerContext = ParserRuleContext.EMPTY;
		if ( debug || debug_list_atn_decisions )  {
			System.out.println("predictATN decision "+dfa.decision+
							   " exec LA(1)=="+ getLookaheadName(input) +
							   ", outerContext="+outerContext.toString(parser));
		}
		DecisionState decState = atn.getDecisionState(dfa.decision);
		boolean greedy = decState.isGreedy;
		boolean loopsSimulateTailRecursion = false;
		boolean fullCtx = false;
		ATNConfigSet s0_closure =
			computeStartState(dfa.atnStartState,
							  ParserRuleContext.EMPTY,
							  greedy, loopsSimulateTailRecursion,
							  fullCtx);
		dfa.s0 = addDFAState(dfa, s0_closure);

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
		// caller must ensure current thread is sync'd on dfa
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
		boolean greedy = decState.isGreedy;

		int t = input.LA(1);
	loop:
		while ( true ) {
			if ( dfa_debug ) System.out.println("DFA state "+s.stateNumber+" LA(1)=="+getLookaheadName(input));
			if ( s.isCtxSensitive && !SLL ) {
				if ( dfa_debug ) System.out.println("ctx sensitive state "+outerContext+" in "+s);
				boolean loopsSimulateTailRecursion = true;
				boolean fullCtx = true;
				ATNConfigSet s0_closure =
					computeStartState(dfa.atnStartState, outerContext,
									  greedy, loopsSimulateTailRecursion,
									  fullCtx);
				retry_with_context_from_dfa++;
				ATNConfigSet fullCtxSet =
					execATNWithFullContext(dfa, s, s0_closure,
										   input, startIndex,
										   outerContext,
										   ATN.INVALID_ALT_NUMBER,
										   greedy);
				return fullCtxSet.uniqueAlt;
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
			assert !s.isCtxSensitive && !s.isAcceptState;

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
			DFAState target = s.edges[t+1];
			if ( target == ERROR ) {
				throw noViableAlt(input, outerContext, s.configs, startIndex);
			}
			s = target;
			if (!s.isCtxSensitive && !s.isAcceptState) {
				input.consume();
				t = input.LA(1);
			}
		}
//		if ( acceptState==null ) {
//			if ( debug ) System.out.println("!!! no viable alt in dfa");
//			return -1;
//		}

		// Before jumping to prediction, check to see if there are
		// disambiguating predicates to evaluate
		if ( s.predicates!=null ) {
			// rewind input so pred's LT(i) calls make sense
			input.seek(startIndex);
			// since we don't report ambiguities in execDFA, we never need to
			// use complete predicate evaluation here
			IntervalSet alts = evalSemanticContext(s.predicates, outerContext, false);
			if (alts.isNil()) {
				throw noViableAlt(input, outerContext, s.configs, startIndex);
			}

			return alts.getMinElement();
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
		if ( debug || debug_list_atn_decisions) {
			System.out.println("execATN decision "+dfa.decision+
							   " exec LA(1)=="+ getLookaheadName(input)+
							   " line "+input.LT(1).getLine()+":"+input.LT(1).getCharPositionInLine());
		}
		ATN_failover++;

		ATNConfigSet previous = s0.configs;
		DFAState D;
		ATNConfigSet fullCtxSet;

		if ( debug ) System.out.println("s0 = "+s0);

		int t = input.LA(1);

        DecisionState decState = atn.getDecisionState(dfa.decision);
		boolean greedy = decState.isGreedy;

		while (true) { // while more work
			boolean loopsSimulateTailRecursion = false;
//			System.out.println("REACH "+getLookaheadName(input));
			ATNConfigSet reach = computeReachSet(previous, t,
												 greedy,
												 loopsSimulateTailRecursion,
												 false);
			if ( reach==null ) throw noViableAlt(input, outerContext, previous, startIndex);
			D = addDFAEdge(dfa, previous, t, reach); // always adding edge even if to a conflict state
			int predictedAlt = getUniqueAlt(reach);
			if ( predictedAlt!=ATN.INVALID_ALT_NUMBER ) {
				D.isAcceptState = true;
				D.configs.uniqueAlt = predictedAlt;
				D.prediction = predictedAlt;
			}
			else {
				D.configs.conflictingAlts = getConflictingAlts(reach, false);
				if ( D.configs.conflictingAlts!=null ) {
					if ( greedy ) {
//						int k = input.index() - startIndex + 1; // how much input we used
//						System.out.println("used k="+k);
						if ( outerContext == ParserRuleContext.EMPTY || // in grammar start rule
							 !D.configs.dipsIntoOuterContext ||         // didn't fall out of rule
							 SLL )                                      // not forcing SLL only
						{
							if ( !D.configs.hasSemanticContext ) {
								reportAmbiguity(dfa, D, startIndex, input.index(),
												D.configs.conflictingAlts, D.configs);
							}
							D.isAcceptState = true;
							predictedAlt = resolveToMinAlt(D, D.configs.conflictingAlts);
						}
						else {
							if ( debug ) System.out.println("RETRY with outerContext="+outerContext);
							loopsSimulateTailRecursion = true;
							ATNConfigSet s0_closure =
								computeStartState(dfa.atnStartState,
												  outerContext,
												  greedy,
												  loopsSimulateTailRecursion,
												  true);
							fullCtxSet = execATNWithFullContext(dfa, D, s0_closure,
																input, startIndex,
																outerContext,
																D.configs.conflictingAlts.getMinElement(),
																greedy);
							// not accept state: isCtxSensitive
							D.isCtxSensitive = true; // always force DFA to ATN simulate
							D.prediction = predictedAlt = fullCtxSet.uniqueAlt;
							return predictedAlt; // all done with preds, etc...
						}
					}
					else {
						// upon ambiguity for nongreedy, default to exit branch to avoid inf loop
						// this handles case where we find ambiguity that stops DFA construction
						// before a config hits rule stop state. Was leaving prediction blank.
						int exitAlt = 2;
						D.isAcceptState = true; // when ambig or ctx sens or nongreedy or .* loop hitting rule stop
						D.prediction = predictedAlt = exitAlt;
					}
				}
			}

			if ( !greedy ) {
				int exitAlt = 2;
				if ( predictedAlt != ATN.INVALID_ALT_NUMBER && configWithAltAtStopState(reach, 1) ) {
					if ( debug ) System.out.println("nongreedy loop but unique alt "+D.configs.uniqueAlt+" at "+reach);
					// reaches end via .* means nothing after.
					D.isAcceptState = true;
					D.prediction = predictedAlt = exitAlt;
				}
				else {// if we reached end of rule via exit branch and decision nongreedy, we matched
					if ( configWithAltAtStopState(reach, exitAlt) ) {
						if ( debug ) System.out.println("nongreedy at stop state for exit branch");
						D.isAcceptState = true;
						D.prediction = predictedAlt = exitAlt;
					}
				}
			}

			if ( D.isAcceptState && D.configs.hasSemanticContext ) {
				// We need to test all predicates, even in DFA states that
				// uniquely predict alternative. We can only get a conflict
				// when we're sure that it's an ambiguity not conflict.
				int nalts = decState.getNumberOfTransitions();
				List<DFAState.PredPrediction> predPredictions =
					predicateDFAState(D, D.configs, outerContext, nalts);
				if ( predPredictions!=null ) {
					int stopIndex = input.index();
					input.seek(startIndex);
					IntervalSet alts = evalSemanticContext(predPredictions, outerContext, true);
					D.prediction = ATN.INVALID_ALT_NUMBER;
					switch (alts.size()) {
					case 0:
						throw noViableAlt(input, outerContext, D.configs, startIndex);

					case 1:
						return alts.getMinElement();

					default:
						// report ambiguity after predicate evaluation to make sure the correct
						// set of ambig alts is reported.
						reportAmbiguity(dfa, D, startIndex, stopIndex, alts, D.configs);

						return alts.getMinElement();
					}
				}
			}

			if ( D.isAcceptState ) return predictedAlt;

			previous = reach;
			input.consume();
			t = input.LA(1);
		}
	}

	// comes back with reach.uniqueAlt set to a valid alt
	public ATNConfigSet execATNWithFullContext(DFA dfa,
											   DFAState D, // how far we got before failing over
											   @NotNull ATNConfigSet s0,
											   @NotNull TokenStream input, int startIndex,
											   ParserRuleContext<?> outerContext,
											   int SLL_min_alt, // todo: is this in D as min ambig alts?
											   boolean greedy)
	{
		// caller must ensure current thread is sync'd on dfa
		retry_with_context++;
		reportAttemptingFullContext(dfa, s0, startIndex, input.index());

		if ( debug || debug_list_atn_decisions ) {
			System.out.println("execATNWithFullContext "+s0+", greedy="+greedy);
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
			reach = computeReachSet(previous, t, greedy, true, fullCtx);
			if ( reach==null ) {
				throw noViableAlt(input, outerContext, previous, startIndex);
			}
			reach.uniqueAlt = getUniqueAlt(reach);
			if ( reach.uniqueAlt!=ATN.INVALID_ALT_NUMBER ) break;
			reach.conflictingAlts = getConflictingAlts(reach, fullCtx);
			if ( reach.conflictingAlts!=null ) break;
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
			return reach;
		}

		// We do not check predicates here because we have checked them
		// on-the-fly when doing full context prediction.

		// must have conflict
		reportAmbiguity(dfa, D, startIndex, input.index(), reach.conflictingAlts, reach);

		reach.uniqueAlt = reach.conflictingAlts.getMinElement();

		return reach;
	}

	protected ATNConfigSet computeReachSet(ATNConfigSet closure, int t,
										   boolean greedy,
										   boolean loopsSimulateTailRecursion,
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
					intermediate.add(new ATNConfig(c, target));
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
			reach = new ATNConfigSet(intermediate, null);
		}
		else if ( ParserATNSimulator.getUniqueAlt(intermediate)==1 ) {
			// Also don't pursue the closure if there is unique alternative
			// among the configurations.
			reach = new ATNConfigSet(intermediate, null);
		}
		else {
			for (ATNConfig c : intermediate) {
				closure(c, reach, closureBusy, false, greedy,
						loopsSimulateTailRecursion, fullCtx);
			}
		}

		if ( reach.size()==0 ) return null;
		return reach;
	}

	@NotNull
	public ATNConfigSet computeStartState(@NotNull ATNState p,
										  @Nullable RuleContext ctx,
										  boolean greedy,
										  boolean loopsSimulateTailRecursion,
										  boolean fullCtx)
	{
		// always at least the implicit call to start rule
		PredictionContext initialContext = PredictionContext.fromRuleContext(ctx);
		ATNConfigSet configs = new ATNConfigSet(fullCtx);

		for (int i=0; i<p.getNumberOfTransitions(); i++) {
			ATNState target = p.transition(i).target;
			ATNConfig c = new ATNConfig(target, i+1, initialContext);
			Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
			closure(c, configs, closureBusy, true, greedy,
					loopsSimulateTailRecursion, fullCtx);
		}

		return configs;
	}

	@Nullable
	public ATNState getReachableTarget(@NotNull Transition trans, int ttype) {
		if ( trans instanceof AtomTransition ) {
			AtomTransition at = (AtomTransition)trans;
			if ( at.label == ttype ) {
				return at.target;
			}
		}
		else if ( trans instanceof SetTransition ) {
			SetTransition st = (SetTransition)trans;
			boolean not = trans instanceof NotSetTransition;
			if ( !not && st.set.contains(ttype) || not && !st.set.contains(ttype) ) {
				return st.target;
			}
		}
		else if ( trans instanceof RangeTransition ) { // TODO: can't happen in parser, right? remove
			RangeTransition rt = (RangeTransition)trans;
			if ( ttype>=rt.from && ttype<=rt.to ) return rt.target;
		}
		else if ( trans instanceof WildcardTransition && ttype!=Token.EOF ) {
			return trans.target;
		}
		return null;
	}

	/** collect and set D's semantic context */
	public List<DFAState.PredPrediction> predicateDFAState(DFAState D,
														   ATNConfigSet configs,
														   RuleContext outerContext,
														   int nalts)
	{
		IntervalSet conflictingAlts = getConflictingAltsFromConfigSet(configs);
		if ( debug ) System.out.println("predicateDFAState "+D);
		SemanticContext[] altToPred = getPredsForAmbigAlts(conflictingAlts, configs, nalts);
		// altToPred[uniqueAlt] is now our validating predicate (if any)
		List<DFAState.PredPrediction> predPredictions = null;
		if ( altToPred!=null ) {
			// Update DFA so reach becomes accept state with predicate
			predPredictions = getPredicatePredictions(conflictingAlts, altToPred);
			D.predicates = predPredictions;
			D.prediction = ATN.INVALID_ALT_NUMBER; // make sure we use preds
		}
		return predPredictions;
	}

	public SemanticContext[] getPredsForAmbigAlts(@NotNull IntervalSet ambigAlts,
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
			if ( ambigAlts.contains(c.alt) ) {
				altToPred[c.alt] = SemanticContext.or(altToPred[c.alt], c.semanticContext);
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

//		// Optimize away p||p and p&&p TODO: optimize() was a no-op
//		for (int i = 0; i < altToPred.length; i++) {
//			altToPred[i] = altToPred[i].optimize();
//		}

		// nonambig alts are null in altToPred
		if ( nPredAlts==0 ) altToPred = null;
		if ( debug ) System.out.println("getPredsForAmbigAlts result "+Arrays.toString(altToPred));
		return altToPred;
	}

	public List<DFAState.PredPrediction> getPredicatePredictions(IntervalSet ambigAlts,
																 SemanticContext[] altToPred)
	{
		List<DFAState.PredPrediction> pairs = new ArrayList<DFAState.PredPrediction>();
		boolean containsPredicate = false;
		for (int i = 1; i < altToPred.length; i++) {
			SemanticContext pred = altToPred[i];

			// unpredicated is indicated by SemanticContext.NONE
			assert pred != null;

			if (ambigAlts!=null && ambigAlts.contains(i)) {
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

	/** Look through a list of predicate/alt pairs, returning alts for the
	 *  pairs that win. A {@code null} predicate indicates an alt containing an
	 *  unpredicated config which behaves as "always true." If !complete
	 *  then we stop at the first predicate that evaluates to true. This
	 *  includes pairs with null predicates.
	 */
	public IntervalSet evalSemanticContext(List<DFAState.PredPrediction> predPredictions,
										   ParserRuleContext<?> outerContext,
										   boolean complete)
	{
		IntervalSet predictions = new IntervalSet();
		for (DFAState.PredPrediction pair : predPredictions) {
			if ( pair.pred==null ) {
				System.err.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
				predictions.add(pair.alt);
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
				predictions.add(pair.alt);
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

	// TODO: loopsSimulateTailRecursion might not be necessary. seems slow without it. see what that is 12/29/11
	protected void closure(@NotNull ATNConfig config,
						   @NotNull ATNConfigSet configs,
						   @NotNull Set<ATNConfig> closureBusy,
						   boolean collectPredicates,
						   boolean greedy,
						   boolean loopsSimulateTailRecursion,
						   boolean fullCtx)
	{
		final int initialDepth = 0;
		closureCheckingStopStateAndLoopRecursion(config, configs, closureBusy, collectPredicates, greedy,
												 loopsSimulateTailRecursion,
												 fullCtx,
												 initialDepth);
	}

	protected void closureCheckingStopStateAndLoopRecursion(@NotNull ATNConfig config,
															@NotNull ATNConfigSet configs,
															@NotNull Set<ATNConfig> closureBusy,
															boolean collectPredicates,
															boolean greedy,
															boolean loopsSimulateTailRecursion,
															boolean fullCtx,
															int depth)
	{
		if ( debug ) System.out.println("closure("+config.toString(parser,true)+")");

		if ( !closureBusy.add(config) ) return; // avoid infinite recursion

		if ( config.state instanceof RuleStopState ) {
			if ( !greedy ) {
				// don't see past end of a rule for any nongreedy decision
				if ( debug ) System.out.println("NONGREEDY at stop state of "+
												getRuleName(config.state.ruleIndex));
				configs.add(config);
				return;
			}
			// We hit rule end. If we have context info, use it
			// run thru all possible stack tops in ctx
			if ( config.context!=null && !config.context.isEmpty() ) {
				for (SingletonPredictionContext ctx : config.context) {
					if ( ctx.invokingState==PredictionContext.EMPTY_FULL_CTX_INVOKING_STATE ) {
						// we have no context info, just chase follow links (if greedy)
						if ( debug ) System.out.println("FALLING off rule "+
														getRuleName(config.state.ruleIndex));
						closure_(config, configs, closureBusy, collectPredicates, greedy,
								 loopsSimulateTailRecursion, fullCtx, depth);
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
					closureCheckingStopStateAndLoopRecursion(c, configs, closureBusy, collectPredicates, greedy,
															 loopsSimulateTailRecursion,
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
		else if ( loopsSimulateTailRecursion ) {
			if ( config.state.getClass()==StarLoopbackState.class ||
				 config.state.getClass()==PlusLoopbackState.class )
			{
				config.context =
					new SingletonPredictionContext(config.context, config.state.stateNumber);
				// alter config; it's ok, since all calls to closure pass in a fresh config for us to chase
				if ( debug ) System.out.println("Loop back; push "+config.state.stateNumber+", stack="+config.context);
			}
			else if ( config.state.getClass()==LoopEndState.class ) {
				if ( debug ) System.out.print("Loop end; pop, stack=" + config.context);
				LoopEndState end = (LoopEndState)config.state;
				// pop all the way back until we don't see the loopback state anymore
				config.context = config.context.popAll(end.loopBackStateNumber,
													   configs.fullCtx);
				if ( debug ) System.out.println(" becomes "+config.context);
			}
		}

		closure_(config, configs, closureBusy, collectPredicates, greedy,
				 loopsSimulateTailRecursion, fullCtx, depth);
	}

	/** Do the actual work of walking epsilon edges */
	protected void closure_(@NotNull ATNConfig config,
							@NotNull ATNConfigSet configs,
							@NotNull Set<ATNConfig> closureBusy,
							boolean collectPredicates,
							boolean greedy,
							boolean loopsSimulateTailRecursion,
							boolean fullCtx,
							int depth)
	{
		ATNState p = config.state;
		// optimization
		if ( !p.onlyHasEpsilonTransitions() ) {
            configs.add(config);
			if ( config.semanticContext!=null && config.semanticContext!= SemanticContext.NONE ) {
				configs.hasSemanticContext = true;
			}
			if ( config.reachesIntoOuterContext>0 ) {
				configs.dipsIntoOuterContext = true;
			}
            if ( debug ) System.out.println("added config "+configs);
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

				closureCheckingStopStateAndLoopRecursion(c, configs, closureBusy, continueCollecting, greedy,
														 loopsSimulateTailRecursion,
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
		if ( t instanceof RuleTransition ) {
			return ruleTransition(config, t);
		}
		else if ( t instanceof PredicateTransition ) {
			return predTransition(config, (PredicateTransition)t,
								  collectPredicates,
								  inContext,
								  fullCtx);
		}
		else if ( t instanceof ActionTransition ) {
			return actionTransition(config, (ActionTransition)t);
		}
		else if ( t.isEpsilon() ) {
			return new ATNConfig(config, t.target);
		}
		return null;
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
			new SingletonPredictionContext(config.context, config.state.stateNumber);
		return new ATNConfig(config, t.target, newContext);
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

	 TODO: split into "has nonconflict config--add to work list" and getambigalts
	 functions
	 */
	@Nullable
	public IntervalSet getConflictingAlts(@NotNull ATNConfigSet configs, boolean fullCtx) {
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

	protected IntervalSet getConflictingAltsFromConfigSet(ATNConfigSet configs) {
		IntervalSet conflictingAlts;
		if ( configs.uniqueAlt!= ATN.INVALID_ALT_NUMBER ) {
			conflictingAlts = IntervalSet.of(configs.uniqueAlt);
		}
		else {
			conflictingAlts = configs.conflictingAlts;
		}
		return conflictingAlts;
	}

	protected int resolveToMinAlt(@NotNull DFAState D, IntervalSet conflictingAlts) {
		// kill dead alts so we don't chase them ever
//		killAlts(conflictingAlts, D.configset);
		D.prediction = conflictingAlts.getMinElement();
		if ( debug ) System.out.println("RESOLVED TO "+D.prediction+" for "+D);
		return D.prediction;
	}

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

	public static int getUniqueAlt(@NotNull Collection<ATNConfig> configs) {
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

	@NotNull
	protected DFAState addDFAEdge(@NotNull DFA dfa,
								  @NotNull ATNConfigSet p,
								  int t,
								  @NotNull ATNConfigSet q)
	{
		DFAState from = addDFAState(dfa, p);
		DFAState to = addDFAState(dfa, q);
		if ( debug ) System.out.println("EDGE "+from+" -> "+to+" upon "+getTokenName(t));
		addDFAEdge(from, t, to);
		if ( debug ) System.out.println("DFA=\n"+dfa.toString(parser!=null?parser.getTokenNames():null));
		return to;
	}

	protected void addDFAEdge(@Nullable DFAState p, int t, @Nullable DFAState q) {
		if ( p==null || t < -1 || q == null ) return;
		if ( p.edges==null ) {
			p.edges = new DFAState[atn.maxTokenType+1+1]; // TODO: make adaptive
		}
		p.edges[t+1] = q; // connect
	}

	/** See comment on LexerInterpreter.addDFAState. */
	@NotNull
	protected DFAState addDFAState(@NotNull DFA dfa, @NotNull ATNConfigSet configs) {
		DFAState proposed = new DFAState(configs);
		DFAState existing = dfa.states.get(proposed);
		if ( existing!=null ) return existing;

		DFAState newState = proposed;
		newState.stateNumber = dfa.states.size();
		configs.optimizeConfigs(this);
		configs.setReadonly(true);
		newState.configs = configs;
		dfa.states.put(newState, newState);
		if ( debug ) System.out.println("adding new DFA state: "+newState);
		return newState;
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
								@NotNull IntervalSet ambigAlts,
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
