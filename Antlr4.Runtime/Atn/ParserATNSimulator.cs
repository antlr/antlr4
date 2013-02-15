/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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
using System.Collections.Generic;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Atn
{
	/// <summary>The embodiment of the adaptive LL(*) parsing strategy.</summary>
	/// <remarks>
	/// The embodiment of the adaptive LL(*) parsing strategy.
	/// The basic complexity of the adaptive strategy makes it harder to
	/// understand. We begin with ATN simulation to build paths in a
	/// DFA. Subsequent prediction requests go through the DFA first. If
	/// they reach a state without an edge for the current symbol, the
	/// algorithm fails over to the ATN simulation to complete the DFA
	/// path for the current input (until it finds a conflict state or
	/// uniquely predicting state).
	/// All of that is done without using the outer context because we
	/// want to create a DFA that is not dependent upon the rule
	/// invocation stack when we do a prediction.  One DFA works in all
	/// contexts. We avoid using context not necessarily because it
	/// slower, although it can be, but because of the DFA caching
	/// problem.  The closure routine only considers the rule invocation
	/// stack created during prediction beginning in the entry rule.  For
	/// example, if prediction occurs without invoking another rule's
	/// ATN, there are no context stacks in the configurations. When this
	/// leads to a conflict, we don't know if it's an ambiguity or a
	/// weakness in the strong LL(*) parsing strategy (versus full
	/// LL(*)).
	/// So, we simply retry the ATN simulation again, this time
	/// using full outer context and filling a dummy DFA (to avoid
	/// polluting the context insensitive DFA). Configuration context
	/// stacks will be the full invocation stack from the start rule. If
	/// we get a conflict using full context, then we can definitively
	/// say we have a true ambiguity for that input sequence. If we don't
	/// get a conflict, it implies that the decision is sensitive to the
	/// outer context. (It is not context-sensitive in the sense of
	/// context sensitive grammars.) We create a special DFA accept state
	/// that maps rule context to a predicted alternative. That is the
	/// only modification needed to handle full LL(*) prediction. In
	/// general, full context prediction will use more lookahead than
	/// necessary, but it pays to share the same DFA. For a schedule
	/// proof that full context prediction uses that most the same amount
	/// of lookahead as a context insensitive prediction, see the comment
	/// on method retryWithContext().
	/// So, the strategy is complex because we bounce back and forth from
	/// the ATN to the DFA, simultaneously performing predictions and
	/// extending the DFA according to previously unseen input
	/// sequences. The retry with full context is a recursive call to the
	/// same function naturally because it does the same thing, just with
	/// a different initial context. The problem is, that we need to pass
	/// in a "full context mode" parameter so that it knows to report
	/// conflicts differently. It also knows not to do a retry, to avoid
	/// infinite recursion, if it is already using full context.
	/// Retry a simulation using full outer context.
	/// One of the key assumptions here is that using full context
	/// can use at most the same amount of input as a simulation
	/// that is not useful context (i.e., it uses all possible contexts
	/// that could invoke our entry rule. I believe that this is true
	/// and the proof might go like this.
	/// THEOREM:  The amount of input consumed during a full context
	/// simulation is at most the amount of input consumed during a
	/// non full context simulation.
	/// PROOF: Let D be the DFA state at which non-context simulation
	/// terminated. That means that D does not have a configuration for
	/// which we can legally pursue more input. (It is legal to work only
	/// on configurations for which there is no conflict with another
	/// configuration.) Now we restrict ourselves to following ATN edges
	/// associated with a single context. Choose any DFA state D' along
	/// the path (same input) to D. That state has either the same number
	/// of configurations or fewer. (If the number of configurations is
	/// the same, then we have degenerated to the non-context case.) Now
	/// imagine that we restrict to following edges associated with
	/// another single context and that we reach DFA state D'' for the
	/// same amount of input as D'. The non-context simulation merges D'
	/// and D''. The union of the configuration sets either has the same
	/// number of configurations as both D' and D'' or it has more. If it
	/// has the same number, we are no worse off and the merge does not
	/// force us to look for more input than we would otherwise have to
	/// do. If the union has more configurations, it can introduce
	/// conflicts but not new alternatives--we cannot conjure up alternatives
	/// by computing closure on the DFA state.  Here are the cases for
	/// D' union D'':
	/// 1. No increase in configurations, D' = D''
	/// 2. Add configuration that introduces a new alternative number.
	/// This cannot happen because no new alternatives are introduced
	/// while computing closure, even during start state computation.
	/// 3. D'' adds a configuration that does not conflict with any
	/// configuration in D'.  Simulating without context would then have
	/// forced us to use more lookahead than D' (full context) alone.
	/// 3. D'' adds a configuration that introduces a conflict with a
	/// configuration in D'. There are 2 cases:
	/// a. The conflict does not cause termination (D' union D''
	/// is added to the work list). Again no context simulation requires
	/// more input.
	/// b. The conflict does cause termination, but this cannot happen.
	/// By definition, we know that with ALL contexts merged we
	/// don't terminate until D and D' uses less input than D. Therefore
	/// no context simulation requires more input than full context
	/// simulation.
	/// We have covered all the cases and there is never a situation where
	/// a single, full context simulation requires more input than a
	/// no context simulation.
	/// I spent a bunch of time thinking about this problem after finding
	/// a case where context-sensitive ATN simulation looks beyond what they
	/// no context simulation uses. the no context simulation for if then else
	/// stops at the else whereas full context scans through to the end of the
	/// statement to decide that the "else statement" clause is ambiguous. And
	/// sometimes it is not ambiguous! Ok, I made an untrue assumption in my
	/// proof which I won't bother going to. the important thing is what I'm
	/// going to do about it. I thought I had a simple answer, but nope. It
	/// turns out that the if then else case is perfect example of something
	/// that has the following characteristics:
	/// no context conflicts at k=1
	/// full context at k=(1 + length of statement) can be both ambiguous and not
	/// ambiguous depending on the input, though I think from different contexts.
	/// But, the good news is that the k=1 case is a special case in that
	/// SLL(1) and LL(1) have exactly the same power so we can conclude that
	/// conflicts at k=1 are true ambiguities and we do not need to pursue
	/// context-sensitive parsing. That covers a huge number of cases
	/// including the if then else clause and the predicated precedence
	/// parsing mechanism. whew! because that could be extremely expensive if
	/// we had to do context.
	/// Further, there is no point in doing full context if none of the
	/// configurations dip into the outer context. This nicely handles cases
	/// such as super constructor calls versus function calls. One grammar
	/// might look like this:
	/// ctorBody : '{' superCall? stat* '}' ;
	/// Or, you might see something like
	/// stat : superCall ';' | expression ';' | ... ;
	/// In both cases I believe that no closure operations will dip into the
	/// outer context. In the first case ctorBody in the worst case will stop
	/// at the '}'. In the 2nd case it should stop at the ';'. Both cases
	/// should stay within the entry rule and not dip into the outer context.
	/// So, we now cover what I hope is the vast majority of the cases (in
	/// particular the very important precedence parsing case). Anything that
	/// needs k&gt;1 and dips into the outer context requires a full context
	/// retry. In this case, I'm going to start out with a brain-dead solution
	/// which is to mark the DFA state as context-sensitive when I get a
	/// conflict. Any further DFA simulation that reaches that state will
	/// launch an ATN simulation to get the prediction, without updating the
	/// DFA or storing any context information. Later, I can make this more
	/// efficient, but at least in this case I can guarantee that it will
	/// always do the right thing. We are not making any assumptions about
	/// lookahead depth.
	/// Ok, writing this up so I can put in a comment.
	/// Upon conflict in the no context simulation:
	/// if k=1, report ambiguity and resolve to the minimum conflicting alternative
	/// if k=1 and predicates, no report and include the predicate to
	/// predicted alternative map in the DFA state
	/// if k=* and we did not dip into the outer context, report ambiguity
	/// and resolve to minimum conflicting alternative
	/// if k&gt;1 and we dip into outer context, retry with full context
	/// if conflict, report ambiguity and resolve to minimum conflicting
	/// alternative, mark DFA as context-sensitive
	/// If no conflict, report ctx sensitivity and mark DFA as context-sensitive
	/// Technically, if full context k is less than no context k, we can
	/// reuse the conflicting DFA state so we don't have to create special
	/// DFA paths branching from context, but we can leave that for
	/// optimization later if necessary.
	/// if non-greedy, no report and resolve to the exit alternative
	/// By default we do full context-sensitive LL(*) parsing not
	/// Strong LL(*) parsing. If we fail with Strong LL(*) we
	/// try full LL(*). That means we rewind and use context information
	/// when closure operations fall off the end of the rule that
	/// holds the decision were evaluating
	/// </remarks>
	public class ParserATNSimulator : ATNSimulator
	{
		public const bool debug = false;

		public const bool dfa_debug = false;

		public const bool retry_debug = false;

		private PredictionMode predictionMode = PredictionMode.Ll;

		public bool force_global_context = false;

		public bool always_try_local_context = true;

		public bool optimize_unique_closure = true;

		public bool optimize_ll1 = true;

		public bool optimize_hidden_conflicted_configs = false;

		public bool optimize_tail_calls = true;

		public bool tail_call_preserves_sll = true;

		public bool treat_sllk1_conflict_as_ambiguity = false;

		public static bool optimize_closure_busy = true;

		[Nullable]
		protected internal readonly Parser parser;

		/// <summary>
		/// When
		/// <code>true</code>
		/// , ambiguous alternatives are reported when they are
		/// encountered within
		/// <see cref="ExecATN(Antlr4.Runtime.Dfa.DFA, Antlr4.Runtime.TokenStream, int, SimulatorState)
		/// 	">ExecATN(Antlr4.Runtime.Dfa.DFA, Antlr4.Runtime.TokenStream, int, SimulatorState)
		/// 	</see>
		/// . When
		/// <code>false</code>
		/// , these messages
		/// are suppressed. The default is
		/// <code>false</code>
		/// .
		/// <p>
		/// When messages about ambiguous alternatives are not required, setting this
		/// to
		/// <code>false</code>
		/// enables additional internal optimizations which may lose
		/// this information.
		/// </summary>
		public bool reportAmbiguities = false;

		/// <summary>
		/// By default we do full context-sensitive LL(*) parsing not
		/// Strong LL(*) parsing.
		/// </summary>
		/// <remarks>
		/// By default we do full context-sensitive LL(*) parsing not
		/// Strong LL(*) parsing. If we fail with Strong LL(*) we
		/// try full LL(*). That means we rewind and use context information
		/// when closure operations fall off the end of the rule that
		/// holds the decision were evaluating.
		/// </remarks>
		protected internal bool userWantsCtxSensitive = true;

		/// <summary>Testing only!</summary>
		public ParserATNSimulator(ATN atn) : this(null, atn)
		{
		}

		public ParserATNSimulator(Parser parser, ATN atn) : base(atn)
		{
			this.parser = parser;
		}

		[NotNull]
		public PredictionMode GetPredictionMode()
		{
			return predictionMode;
		}

		public void SetPredictionMode(PredictionMode predictionMode)
		{
			this.predictionMode = predictionMode;
		}

		public override void Reset()
		{
		}

		public virtual int AdaptivePredict(TokenStream input, int decision, ParserRuleContext
			 outerContext)
		{
			return AdaptivePredict(input, decision, outerContext, false);
		}

		public virtual int AdaptivePredict(TokenStream input, int decision, ParserRuleContext
			 outerContext, bool useContext)
		{
			DFA dfa = atn.decisionToDFA[decision];
			System.Diagnostics.Debug.Assert(dfa != null);
			if (optimize_ll1 && !dfa.IsEmpty())
			{
				int ll_1 = input.La(1);
				if (ll_1 >= 0 && ll_1 <= short.MaxValue)
				{
					int key = (decision << 16) + ll_1;
					int alt = atn.LL1Table.Get(key);
					if (alt != null)
					{
						return alt;
					}
				}
			}
			if (force_global_context)
			{
				useContext = true;
			}
			else
			{
				if (!always_try_local_context)
				{
					useContext |= dfa != null && dfa.IsContextSensitive();
				}
			}
			userWantsCtxSensitive = useContext || (predictionMode != PredictionMode.Sll && outerContext
				 != null && !atn.decisionToState[decision].sll);
			if (outerContext == null)
			{
				outerContext = ParserRuleContext.EmptyContext();
			}
			SimulatorState state = null;
			if (!dfa.IsEmpty())
			{
				state = GetStartState(dfa, input, outerContext, useContext);
			}
			if (state == null)
			{
				return PredictATN(dfa, input, outerContext, useContext);
			}
			else
			{
				//dump(dfa);
				// start with the DFA
				int m = input.Mark();
				int index = input.Index();
				try
				{
					int alt = ExecDFA(dfa, input, index, state);
					return alt;
				}
				finally
				{
					input.Seek(index);
					input.Release(m);
				}
			}
		}

		public virtual SimulatorState GetStartState(DFA dfa, TokenStream input, ParserRuleContext
			 outerContext, bool useContext)
		{
			if (!useContext)
			{
				if (dfa.s0.Get() == null)
				{
					return null;
				}
				return new SimulatorState(outerContext, dfa.s0.Get(), false, outerContext);
			}
			ParserRuleContext remainingContext = outerContext;
			System.Diagnostics.Debug.Assert(outerContext != null);
			DFAState s0 = dfa.s0full.Get();
			while (remainingContext != null && s0 != null && s0.IsContextSensitive())
			{
				remainingContext = SkipTailCalls(remainingContext);
				s0 = s0.GetContextTarget(GetReturnState(remainingContext));
				if (remainingContext.IsEmpty())
				{
					System.Diagnostics.Debug.Assert(s0 == null || !s0.IsContextSensitive());
				}
				else
				{
					remainingContext = ((ParserRuleContext)remainingContext.GetParent());
				}
			}
			if (s0 == null)
			{
				return null;
			}
			return new SimulatorState(outerContext, s0, useContext, remainingContext);
		}

		public virtual int PredictATN(DFA dfa, TokenStream input, ParserRuleContext outerContext
			, bool useContext)
		{
			if (outerContext == null)
			{
				outerContext = ParserRuleContext.EmptyContext();
			}
			int alt = 0;
			int m = input.Mark();
			int index = input.Index();
			try
			{
				SimulatorState state = ComputeStartState(dfa, outerContext, useContext);
				if (state.s0.isAcceptState)
				{
					return ExecDFA(dfa, input, index, state);
				}
				else
				{
					alt = ExecATN(dfa, input, index, state);
				}
			}
			catch (NoViableAltException nvae)
			{
				throw;
			}
			finally
			{
				input.Seek(index);
				input.Release(m);
			}
			return alt;
		}

		public virtual int ExecDFA(DFA dfa, TokenStream input, int startIndex, SimulatorState
			 state)
		{
			ParserRuleContext outerContext = state.outerContext;
			DFAState acceptState = null;
			DFAState s = state.s0;
			int t = input.La(1);
			ParserRuleContext remainingOuterContext = state.remainingOuterContext;
			while (true)
			{
				if (state.useContext)
				{
					while (s.IsContextSymbol(t))
					{
						DFAState next = null;
						if (remainingOuterContext != null)
						{
							remainingOuterContext = SkipTailCalls(remainingOuterContext);
							next = s.GetContextTarget(GetReturnState(remainingOuterContext));
						}
						if (next == null)
						{
							// fail over to ATN
							SimulatorState initialState = new SimulatorState(state.outerContext, s, state.useContext
								, remainingOuterContext);
							return ExecATN(dfa, input, startIndex, initialState);
						}
						remainingOuterContext = ((ParserRuleContext)remainingOuterContext.GetParent());
						s = next;
					}
				}
				if (s.isAcceptState)
				{
					if (s.predicates != null)
					{
					}
					acceptState = s;
					// keep going unless we're at EOF or state only has one alt number
					// mentioned in configs; check if something else could match
					// TODO: don't we always stop? only lexer would keep going
					// TODO: v3 dfa don't do this.
					break;
				}
				// t is not updated if one of these states is reached
				System.Diagnostics.Debug.Assert(!s.isAcceptState);
				// if no edge, pop over to ATN interpreter, update DFA and return
				DFAState target = s.GetTarget(t);
				if (target == null)
				{
					if (dfa_debug && t >= 0)
					{
						System.Console.Out.WriteLine("no edge for " + parser.GetTokenNames()[t]);
					}
					int alt;
					SimulatorState initialState = new SimulatorState(outerContext, s, state.useContext
						, remainingOuterContext);
					alt = ExecATN(dfa, input, startIndex, initialState);
					// this adds edge even if next state is accept for
					// same alt; e.g., s0-A->:s1=>2-B->:s2=>2
					// TODO: This next stuff kills edge, but extra states remain. :(
					if (s.isAcceptState && alt != -1)
					{
						DFAState d = s.GetTarget(input.La(1));
						if (d.isAcceptState && d.prediction == s.prediction)
						{
							// we can carve it out.
							s.SetTarget(input.La(1), Error);
						}
					}
					// IGNORE really not error
					//dump(dfa);
					// action already executed
					return alt;
				}
				else
				{
					// we've updated DFA, exec'd action, and have our deepest answer
					if (target == Error)
					{
						throw NoViableAlt(input, outerContext, s.configs, startIndex);
					}
				}
				s = target;
				if (!s.isAcceptState && t != IntStream.Eof)
				{
					input.Consume();
					t = input.La(1);
				}
			}
			//		if ( acceptState==null ) {
			//			if ( debug ) System.out.println("!!! no viable alt in dfa");
			//			return -1;
			//		}
			if (acceptState.configs.GetConflictingAlts() != null)
			{
				if (dfa.atnStartState is DecisionState)
				{
					if (!userWantsCtxSensitive || !acceptState.configs.GetDipsIntoOuterContext() || (
						treat_sllk1_conflict_as_ambiguity && input.Index() == startIndex))
					{
					}
					else
					{
						// we don't report the ambiguity again
						//if ( !acceptState.configset.hasSemanticContext() ) {
						//	reportAmbiguity(dfa, acceptState, startIndex, input.index(), acceptState.configset.getConflictingAlts(), acceptState.configset);
						//}
						System.Diagnostics.Debug.Assert(!state.useContext);
						// Before attempting full context prediction, check to see if there are
						// disambiguating or validating predicates to evaluate which allow an
						// immediate decision
						if (acceptState.predicates != null)
						{
							int conflictIndex = input.Index();
							if (conflictIndex != startIndex)
							{
								input.Seek(startIndex);
							}
							BitSet predictions = EvalSemanticContext(s.predicates, outerContext, true);
							if (predictions.Cardinality() == 1)
							{
								return predictions.NextSetBit(0);
							}
							if (conflictIndex != startIndex)
							{
								// restore the index so reporting the fallback to full
								// context occurs with the index at the correct spot
								input.Seek(conflictIndex);
							}
						}
						if (reportAmbiguities)
						{
							SimulatorState fullContextState = ComputeStartState(dfa, outerContext, true);
							ReportAttemptingFullContext(dfa, fullContextState, startIndex, input.Index());
						}
						input.Seek(startIndex);
						return AdaptivePredict(input, dfa.decision, outerContext, true);
					}
				}
			}
			// Before jumping to prediction, check to see if there are
			// disambiguating or validating predicates to evaluate
			if (s.predicates != null)
			{
				int stopIndex = input.Index();
				if (startIndex != stopIndex)
				{
					input.Seek(startIndex);
				}
				BitSet alts = EvalSemanticContext(s.predicates, outerContext, reportAmbiguities &&
					 predictionMode == PredictionMode.LlExactAmbigDetection);
				switch (alts.Cardinality())
				{
					case 0:
					{
						throw NoViableAlt(input, outerContext, s.configs, startIndex);
					}

					case 1:
					{
						return alts.NextSetBit(0);
					}

					default:
					{
						// report ambiguity after predicate evaluation to make sure the correct
						// set of ambig alts is reported.
						if (startIndex != stopIndex)
						{
							input.Seek(stopIndex);
						}
						ReportAmbiguity(dfa, s, startIndex, stopIndex, alts, s.configs);
						return alts.NextSetBit(0);
						break;
					}
				}
			}
			return acceptState.prediction;
		}

		/// <summary>
		/// Performs ATN simulation to compute a predicted alternative based
		/// upon the remaining input, but also updates the DFA cache to avoid
		/// having to traverse the ATN again for the same input sequence.
		/// </summary>
		/// <remarks>
		/// Performs ATN simulation to compute a predicted alternative based
		/// upon the remaining input, but also updates the DFA cache to avoid
		/// having to traverse the ATN again for the same input sequence.
		/// There are some key conditions we're looking for after computing a new
		/// set of ATN configs (proposed DFA state):
		/// if the set is empty, there is no viable alternative for current symbol
		/// does the state uniquely predict an alternative?
		/// does the state have a conflict that would prevent us from
		/// putting it on the work list?
		/// if in non-greedy decision is there a config at a rule stop state?
		/// We also have some key operations to do:
		/// add an edge from previous DFA state to potentially new DFA state, D,
		/// upon current symbol but only if adding to work list, which means in all
		/// cases except no viable alternative (and possibly non-greedy decisions?)
		/// collecting predicates and adding semantic context to DFA accept states
		/// adding rule context to context-sensitive DFA accept states
		/// consuming an input symbol
		/// reporting a conflict
		/// reporting an ambiguity
		/// reporting a context sensitivity
		/// reporting insufficient predicates
		/// We should isolate those operations, which are side-effecting, to the
		/// main work loop. We can isolate lots of code into other functions, but
		/// they should be side effect free. They can return package that
		/// indicates whether we should report something, whether we need to add a
		/// DFA edge, whether we need to augment accept state with semantic
		/// context or rule invocation context. Actually, it seems like we always
		/// add predicates if they exist, so that can simply be done in the main
		/// loop for any accept state creation or modification request.
		/// cover these cases:
		/// dead end
		/// single alt
		/// single alt + preds
		/// conflict
		/// conflict + preds
		/// TODO: greedy + those
		/// </remarks>
		public virtual int ExecATN(DFA dfa, TokenStream input, int startIndex, SimulatorState
			 initialState)
		{
			ParserRuleContext outerContext = initialState.outerContext;
			bool useContext = initialState.useContext;
			int t = input.La(1);
			DecisionState decState = atn.GetDecisionState(dfa.decision);
			SimulatorState previous = initialState;
			PredictionContextCache contextCache = new PredictionContextCache();
			while (true)
			{
				// while more work
				SimulatorState nextState = ComputeReachSet(dfa, previous, t, contextCache);
				if (nextState == null)
				{
					return HandleNoViableAlt(input, startIndex, previous);
				}
				DFAState D = nextState.s0;
				ATNConfigSet reach = D.configs;
				// predicted alt => accept state
				System.Diagnostics.Debug.Assert(D.isAcceptState || GetUniqueAlt(reach) == ATN.InvalidAltNumber
					);
				// conflicted => accept state
				System.Diagnostics.Debug.Assert(D.isAcceptState || D.configs.GetConflictingAlts()
					 == null);
				if (D.isAcceptState)
				{
					int predictedAlt = reach.GetConflictingAlts() == null ? GetUniqueAlt(reach) : ATN
						.InvalidAltNumber;
					if (predictedAlt != ATN.InvalidAltNumber)
					{
						if (optimize_ll1 && input.Index() == startIndex && nextState.outerContext == nextState
							.remainingOuterContext && dfa.decision >= 0 && !D.configs.HasSemanticContext())
						{
							if (t >= 0 && t <= short.MaxValue)
							{
								int key = (dfa.decision << 16) + t;
								atn.LL1Table.Put(key, predictedAlt);
							}
						}
						if (useContext && always_try_local_context)
						{
							ReportContextSensitivity(dfa, nextState, startIndex, input.Index());
						}
					}
					else
					{
						if (D.configs.GetConflictingAlts() != null)
						{
							predictedAlt = D.prediction;
							//						int k = input.index() - startIndex + 1; // how much input we used
							//						System.out.println("used k="+k);
							if (!userWantsCtxSensitive || !D.configs.GetDipsIntoOuterContext() || (treat_sllk1_conflict_as_ambiguity
								 && input.Index() == startIndex))
							{
								if (reportAmbiguities && !D.configs.HasSemanticContext())
								{
									ReportAmbiguity(dfa, D, startIndex, input.Index(), D.configs.GetConflictingAlts()
										, D.configs);
								}
							}
							else
							{
								System.Diagnostics.Debug.Assert(!useContext);
								System.Diagnostics.Debug.Assert(D.isAcceptState);
								if (D.configs.HasSemanticContext())
								{
									int nalts = decState.GetNumberOfTransitions();
									DFAState.PredPrediction[] predPredictions = D.predicates;
									if (predPredictions != null)
									{
										int conflictIndex = input.Index();
										if (conflictIndex != startIndex)
										{
											input.Seek(startIndex);
										}
										// always use complete evaluation here since we'll want to retry with full context if still ambiguous
										BitSet alts = EvalSemanticContext(predPredictions, outerContext, true);
										if (alts.Cardinality() == 1)
										{
											return alts.NextSetBit(0);
										}
										if (conflictIndex != startIndex)
										{
											// restore the index so reporting the fallback to full
											// context occurs with the index at the correct spot
											input.Seek(conflictIndex);
										}
									}
								}
								SimulatorState fullContextState = ComputeStartState(dfa, outerContext, true);
								if (reportAmbiguities)
								{
									ReportAttemptingFullContext(dfa, fullContextState, startIndex, input.Index());
								}
								input.Seek(startIndex);
								return ExecATN(dfa, input, startIndex, fullContextState);
							}
						}
					}
					if (D.predicates != null)
					{
						int stopIndex = input.Index();
						if (startIndex != stopIndex)
						{
							input.Seek(startIndex);
						}
						BitSet alts = EvalSemanticContext(D.predicates, outerContext, reportAmbiguities &&
							 predictionMode == PredictionMode.LlExactAmbigDetection);
						D.prediction = ATN.InvalidAltNumber;
						switch (alts.Cardinality())
						{
							case 0:
							{
								throw NoViableAlt(input, outerContext, D.configs, startIndex);
							}

							case 1:
							{
								return alts.NextSetBit(0);
							}

							default:
							{
								// report ambiguity after predicate evaluation to make sure the correct
								// set of ambig alts is reported.
								if (startIndex != stopIndex)
								{
									input.Seek(stopIndex);
								}
								ReportAmbiguity(dfa, D, startIndex, stopIndex, alts, D.configs);
								return alts.NextSetBit(0);
								break;
							}
						}
					}
					return predictedAlt;
				}
				previous = nextState;
				if (t != IntStream.Eof)
				{
					input.Consume();
					t = input.La(1);
				}
			}
		}

		protected internal virtual int HandleNoViableAlt(TokenStream input, int startIndex
			, SimulatorState previous)
		{
			if (previous.s0 != null)
			{
				BitSet alts = new BitSet();
				foreach (ATNConfig config in previous.s0.configs)
				{
					if (config.GetReachesIntoOuterContext() || config.GetState() is RuleStopState)
					{
						alts.Set(config.GetAlt());
					}
				}
				if (!alts.IsEmpty())
				{
					return alts.NextSetBit(0);
				}
			}
			throw NoViableAlt(input, previous.outerContext, previous.s0.configs, startIndex);
		}

		protected internal virtual SimulatorState ComputeReachSet(DFA dfa, SimulatorState
			 previous, int t, PredictionContextCache contextCache)
		{
			bool useContext = previous.useContext;
			ParserRuleContext remainingGlobalContext = previous.remainingOuterContext;
			DFAState s = previous.s0;
			if (useContext)
			{
				while (s.IsContextSymbol(t))
				{
					DFAState next = null;
					if (remainingGlobalContext != null)
					{
						remainingGlobalContext = SkipTailCalls(remainingGlobalContext);
						next = s.GetContextTarget(GetReturnState(remainingGlobalContext));
					}
					if (next == null)
					{
						break;
					}
					remainingGlobalContext = ((ParserRuleContext)remainingGlobalContext.GetParent());
					s = next;
				}
			}
			System.Diagnostics.Debug.Assert(!s.isAcceptState);
			if (s.isAcceptState)
			{
				return new SimulatorState(previous.outerContext, s, useContext, remainingGlobalContext
					);
			}
			DFAState s0 = s;
			DFAState existingTarget = s0 != null ? s0.GetTarget(t) : null;
			if (existingTarget != null)
			{
				return new SimulatorState(previous.outerContext, existingTarget, useContext, remainingGlobalContext
					);
			}
			IList<ATNConfig> closureConfigs = new AList<ATNConfig>(s0.configs);
			IntegerList contextElements = null;
			ATNConfigSet reach = new ATNConfigSet();
			bool stepIntoGlobal;
			do
			{
				bool hasMoreContext = !useContext || remainingGlobalContext != null;
				if (!hasMoreContext)
				{
					reach.SetOutermostConfigSet(true);
				}
				ATNConfigSet reachIntermediate = new ATNConfigSet();
				IList<ATNConfig> skippedStopStates = null;
				foreach (ATNConfig c in closureConfigs)
				{
					if (c.GetState() is RuleStopState)
					{
						System.Diagnostics.Debug.Assert(c.GetContext().IsEmpty());
						if (useContext && !c.GetReachesIntoOuterContext() || t == IntStream.Eof)
						{
							if (skippedStopStates == null)
							{
								skippedStopStates = new AList<ATNConfig>();
							}
							skippedStopStates.AddItem(c);
						}
						continue;
					}
					int n = c.GetState().GetNumberOfOptimizedTransitions();
					for (int ti = 0; ti < n; ti++)
					{
						// for each optimized transition
						Transition trans = c.GetState().GetOptimizedTransition(ti);
						ATNState target = GetReachableTarget(c, trans, t);
						if (target != null)
						{
							reachIntermediate.Add(c.Transform(target), contextCache);
						}
					}
				}
				if (optimize_unique_closure && skippedStopStates == null && reachIntermediate.GetUniqueAlt
					() != ATN.InvalidAltNumber)
				{
					reachIntermediate.SetOutermostConfigSet(reach.IsOutermostConfigSet());
					reach = reachIntermediate;
					break;
				}
				bool collectPredicates = false;
				Closure(reachIntermediate, reach, collectPredicates, hasMoreContext, contextCache
					);
				stepIntoGlobal = reach.GetDipsIntoOuterContext();
				if (t == IntStream.Eof)
				{
					reach = RemoveAllConfigsNotInRuleStopState(reach, contextCache);
				}
				if (skippedStopStates != null && (!useContext || !PredictionMode.HasConfigInRuleStopState
					(reach)))
				{
					System.Diagnostics.Debug.Assert(!skippedStopStates.IsEmpty());
					foreach (ATNConfig c_1 in skippedStopStates)
					{
						reach.Add(c_1, contextCache);
					}
				}
				if (useContext && stepIntoGlobal)
				{
					reach.Clear();
					remainingGlobalContext = SkipTailCalls(remainingGlobalContext);
					int nextContextElement = GetReturnState(remainingGlobalContext);
					if (contextElements == null)
					{
						contextElements = new IntegerList();
					}
					if (remainingGlobalContext.IsEmpty())
					{
						remainingGlobalContext = null;
					}
					else
					{
						remainingGlobalContext = ((ParserRuleContext)remainingGlobalContext.GetParent());
					}
					contextElements.Add(nextContextElement);
					if (nextContextElement != PredictionContext.EmptyFullStateKey)
					{
						for (int i = 0; i < closureConfigs.Count; i++)
						{
							closureConfigs.Set(i, closureConfigs[i].AppendContext(nextContextElement, contextCache
								));
						}
					}
				}
			}
			while (useContext && stepIntoGlobal);
			if (reach.IsEmpty())
			{
				return null;
			}
			DFAState dfaState = null;
			if (s0 != null)
			{
				dfaState = AddDFAEdge(dfa, s0, t, contextElements, reach, contextCache);
			}
			System.Diagnostics.Debug.Assert(!useContext || !dfaState.configs.GetDipsIntoOuterContext
				());
			return new SimulatorState(previous.outerContext, dfaState, useContext, remainingGlobalContext
				);
		}

		/// <summary>
		/// Return a configuration set containing only the configurations from
		/// <code>configs</code>
		/// which are in a
		/// <see cref="RuleStopState">RuleStopState</see>
		/// . If all
		/// configurations in
		/// <code>configs</code>
		/// are already in a rule stop state, this
		/// method simply returns
		/// <code>configs</code>
		/// .
		/// </summary>
		/// <param name="configs">the configuration set to update</param>
		/// <param name="contextCache">
		/// the
		/// <see cref="PredictionContext">PredictionContext</see>
		/// cache
		/// </param>
		/// <returns>
		/// 
		/// <code>configs</code>
		/// if all configurations in
		/// <code>configs</code>
		/// are in a
		/// rule stop state, otherwise return a new configuration set containing only
		/// the configurations from
		/// <code>configs</code>
		/// which are in a rule stop state
		/// </returns>
		[NotNull]
		protected internal virtual ATNConfigSet RemoveAllConfigsNotInRuleStopState(ATNConfigSet
			 configs, PredictionContextCache contextCache)
		{
			if (PredictionMode.AllConfigsInRuleStopStates(configs))
			{
				return configs;
			}
			ATNConfigSet result = new ATNConfigSet();
			foreach (ATNConfig config in configs)
			{
				if (!(config.GetState() is RuleStopState))
				{
					continue;
				}
				result.Add(config, contextCache);
			}
			return result;
		}

		[NotNull]
		public virtual SimulatorState ComputeStartState(DFA dfa, ParserRuleContext globalContext
			, bool useContext)
		{
			DFAState s0 = useContext ? dfa.s0full.Get() : dfa.s0.Get();
			if (s0 != null)
			{
				if (!useContext)
				{
					return new SimulatorState(globalContext, s0, useContext, globalContext);
				}
				s0.SetContextSensitive(atn);
			}
			int decision = dfa.decision;
			ATNState p = dfa.atnStartState;
			int previousContext = 0;
			ParserRuleContext remainingGlobalContext = globalContext;
			PredictionContext initialContext = useContext ? PredictionContext.EmptyFull : PredictionContext
				.EmptyLocal;
			// always at least the implicit call to start rule
			PredictionContextCache contextCache = new PredictionContextCache();
			if (useContext)
			{
				while (s0 != null && s0.IsContextSensitive() && remainingGlobalContext != null)
				{
					DFAState next;
					remainingGlobalContext = SkipTailCalls(remainingGlobalContext);
					if (remainingGlobalContext.IsEmpty())
					{
						next = s0.GetContextTarget(PredictionContext.EmptyFullStateKey);
						previousContext = PredictionContext.EmptyFullStateKey;
						remainingGlobalContext = null;
					}
					else
					{
						previousContext = GetReturnState(remainingGlobalContext);
						next = s0.GetContextTarget(previousContext);
						initialContext = initialContext.AppendContext(previousContext, contextCache);
						remainingGlobalContext = ((ParserRuleContext)remainingGlobalContext.GetParent());
					}
					if (next == null)
					{
						break;
					}
					s0 = next;
				}
			}
			if (s0 != null && !s0.IsContextSensitive())
			{
				return new SimulatorState(globalContext, s0, useContext, remainingGlobalContext);
			}
			ATNConfigSet configs = new ATNConfigSet();
			while (true)
			{
				ATNConfigSet reachIntermediate = new ATNConfigSet();
				int n = p.GetNumberOfTransitions();
				for (int ti = 0; ti < n; ti++)
				{
					// for each transition
					ATNState target = p.Transition(ti).target;
					reachIntermediate.AddItem(ATNConfig.Create(target, ti + 1, initialContext));
				}
				bool hasMoreContext = remainingGlobalContext != null;
				if (!hasMoreContext)
				{
					configs.SetOutermostConfigSet(true);
				}
				bool collectPredicates = true;
				Closure(reachIntermediate, configs, collectPredicates, hasMoreContext, contextCache
					);
				bool stepIntoGlobal = configs.GetDipsIntoOuterContext();
				DFAState next = AddDFAState(dfa, configs, contextCache);
				if (s0 == null)
				{
					AtomicReference<DFAState> reference = useContext ? dfa.s0full : dfa.s0;
					if (!reference.CompareAndSet(null, next))
					{
						next = reference.Get();
					}
				}
				else
				{
					s0.SetContextTarget(previousContext, next);
				}
				s0 = next;
				if (!useContext || !stepIntoGlobal)
				{
					break;
				}
				// TODO: make sure it distinguishes empty stack states
				next.SetContextSensitive(atn);
				configs.Clear();
				remainingGlobalContext = SkipTailCalls(remainingGlobalContext);
				int nextContextElement = GetReturnState(remainingGlobalContext);
				if (remainingGlobalContext.IsEmpty())
				{
					remainingGlobalContext = null;
				}
				else
				{
					remainingGlobalContext = ((ParserRuleContext)remainingGlobalContext.GetParent());
				}
				if (nextContextElement != PredictionContext.EmptyFullStateKey)
				{
					initialContext = initialContext.AppendContext(nextContextElement, contextCache);
				}
				previousContext = nextContextElement;
			}
			return new SimulatorState(globalContext, s0, useContext, remainingGlobalContext);
		}

		[Nullable]
		public virtual ATNState GetReachableTarget(ATNConfig source, Transition trans, int
			 ttype)
		{
			if (trans.Matches(ttype, 0, atn.maxTokenType))
			{
				return trans.target;
			}
			return null;
		}

		/// <summary>collect and set D's semantic context</summary>
		public virtual DFAState.PredPrediction[] PredicateDFAState(DFAState D, ATNConfigSet
			 configs, int nalts)
		{
			BitSet conflictingAlts = GetConflictingAltsFromConfigSet(configs);
			SemanticContext[] altToPred = GetPredsForAmbigAlts(conflictingAlts, configs, nalts
				);
			// altToPred[uniqueAlt] is now our validating predicate (if any)
			DFAState.PredPrediction[] predPredictions = null;
			if (altToPred != null)
			{
				// we have a validating predicate; test it
				// Update DFA so reach becomes accept state with predicate
				predPredictions = GetPredicatePredictions(conflictingAlts, altToPred);
				D.predicates = predPredictions;
				D.prediction = ATN.InvalidAltNumber;
			}
			// make sure we use preds
			return predPredictions;
		}

		public virtual SemanticContext[] GetPredsForAmbigAlts(BitSet ambigAlts, ATNConfigSet
			 configs, int nalts)
		{
			// REACH=[1|1|[]|0:0, 1|2|[]|0:1]
			SemanticContext[] altToPred = new SemanticContext[nalts + 1];
			int n = altToPred.Length;
			foreach (ATNConfig c in configs)
			{
				if (ambigAlts.Get(c.GetAlt()))
				{
					altToPred[c.GetAlt()] = SemanticContext.Or(altToPred[c.GetAlt()], c.GetSemanticContext
						());
				}
			}
			int nPredAlts = 0;
			for (int i = 0; i < n; i++)
			{
				if (altToPred[i] == null)
				{
					altToPred[i] = SemanticContext.None;
				}
				else
				{
					if (altToPred[i] != SemanticContext.None)
					{
						nPredAlts++;
					}
				}
			}
			// nonambig alts are null in altToPred
			if (nPredAlts == 0)
			{
				altToPred = null;
			}
			return altToPred;
		}

		public virtual DFAState.PredPrediction[] GetPredicatePredictions(BitSet ambigAlts
			, SemanticContext[] altToPred)
		{
			IList<DFAState.PredPrediction> pairs = new AList<DFAState.PredPrediction>();
			bool containsPredicate = false;
			for (int i = 1; i < altToPred.Length; i++)
			{
				SemanticContext pred = altToPred[i];
				// unpredicated is indicated by SemanticContext.NONE
				System.Diagnostics.Debug.Assert(pred != null);
				// find first unpredicated but ambig alternative, if any.
				// Only ambiguous alternatives will have SemanticContext.NONE.
				// Any unambig alts or ambig naked alts after first ambig naked are ignored
				// (null, i) means alt i is the default prediction
				// if no (null, i), then no default prediction.
				if (ambigAlts != null && ambigAlts.Get(i) && pred == SemanticContext.None)
				{
					pairs.AddItem(new DFAState.PredPrediction(null, i));
				}
				else
				{
					if (pred != SemanticContext.None)
					{
						containsPredicate = true;
						pairs.AddItem(new DFAState.PredPrediction(pred, i));
					}
				}
			}
			if (!containsPredicate)
			{
				pairs = null;
			}
			//		System.out.println(Arrays.toString(altToPred)+"->"+pairs);
			return Sharpen.Collections.ToArray(pairs, new DFAState.PredPrediction[pairs.Count
				]);
		}

		/// <summary>
		/// Look through a list of predicate/alt pairs, returning alts for the
		/// pairs that win.
		/// </summary>
		/// <remarks>
		/// Look through a list of predicate/alt pairs, returning alts for the
		/// pairs that win. A
		/// <code>null</code>
		/// predicate indicates an alt containing an
		/// unpredicated config which behaves as "always true."
		/// </remarks>
		public virtual BitSet EvalSemanticContext(DFAState.PredPrediction[] predPredictions
			, ParserRuleContext outerContext, bool complete)
		{
			BitSet predictions = new BitSet();
			foreach (DFAState.PredPrediction pair in predPredictions)
			{
				if (pair.pred == null)
				{
					predictions.Set(pair.alt);
					if (!complete)
					{
						break;
					}
					continue;
				}
				bool evaluatedResult = pair.pred.Eval(parser, outerContext);
				if (debug || dfa_debug)
				{
					System.Console.Out.WriteLine("eval pred " + pair + "=" + evaluatedResult);
				}
				if (evaluatedResult)
				{
					if (debug || dfa_debug)
					{
						System.Console.Out.WriteLine("PREDICT " + pair.alt);
					}
					predictions.Set(pair.alt);
					if (!complete)
					{
						break;
					}
				}
			}
			return predictions;
		}

		protected internal virtual void Closure(ATNConfigSet sourceConfigs, ATNConfigSet 
			configs, bool collectPredicates, bool hasMoreContext, PredictionContextCache contextCache
			)
		{
			if (contextCache == null)
			{
				contextCache = PredictionContextCache.Uncached;
			}
			ATNConfigSet currentConfigs = sourceConfigs;
			ICollection<ATNConfig> closureBusy = new HashSet<ATNConfig>();
			while (currentConfigs.Count > 0)
			{
				ATNConfigSet intermediate = new ATNConfigSet();
				foreach (ATNConfig config in currentConfigs)
				{
					if (optimize_closure_busy && !closureBusy.AddItem(config))
					{
						continue;
					}
					Closure(config, configs, intermediate, closureBusy, collectPredicates, hasMoreContext
						, contextCache, 0);
				}
				currentConfigs = intermediate;
			}
		}

		protected internal virtual void Closure(ATNConfig config, ATNConfigSet configs, ATNConfigSet
			 intermediate, ICollection<ATNConfig> closureBusy, bool collectPredicates, bool 
			hasMoreContexts, PredictionContextCache contextCache, int depth)
		{
			if (!optimize_closure_busy && !closureBusy.AddItem(config))
			{
				return;
			}
			// avoid infinite recursion
			if (config.GetState() is RuleStopState)
			{
				// We hit rule end. If we have context info, use it
				if (!config.GetContext().IsEmpty())
				{
					bool hasEmpty = config.GetContext().HasEmpty();
					int nonEmptySize = config.GetContext().Size() - (hasEmpty ? 1 : 0);
					for (int i = 0; i < nonEmptySize; i++)
					{
						PredictionContext newContext = config.GetContext().GetParent(i);
						// "pop" return state
						ATNState returnState = atn.states[config.GetContext().GetReturnState(i)];
						ATNConfig c = ATNConfig.Create(returnState, config.GetAlt(), newContext, config.GetSemanticContext
							());
						// While we have context to pop back from, we may have
						// gotten that context AFTER having fallen off a rule.
						// Make sure we track that we are now out of context.
						c.SetOuterContextDepth(config.GetOuterContextDepth());
						System.Diagnostics.Debug.Assert(depth > int.MinValue);
						if (optimize_closure_busy && c.GetContext().IsEmpty() && !closureBusy.AddItem(c))
						{
							continue;
						}
						Closure(c, configs, intermediate, closureBusy, collectPredicates, hasMoreContexts
							, contextCache, depth - 1);
					}
					if (!hasEmpty || !hasMoreContexts)
					{
						return;
					}
					config = config.Transform(config.GetState(), PredictionContext.EmptyLocal);
				}
				else
				{
					if (!hasMoreContexts)
					{
						configs.Add(config, contextCache);
						return;
					}
					else
					{
						// else if we have no context info, just chase follow links (if greedy)
						if (config.GetContext() == PredictionContext.EmptyFull)
						{
							// no need to keep full context overhead when we step out
							config = config.Transform(config.GetState(), PredictionContext.EmptyLocal);
						}
					}
				}
			}
			ATNState p = config.GetState();
			// optimization
			if (!p.OnlyHasEpsilonTransitions())
			{
				configs.Add(config, contextCache);
			}
			for (int i_1 = 0; i_1 < p.GetNumberOfOptimizedTransitions(); i_1++)
			{
				Transition t = p.GetOptimizedTransition(i_1);
				bool continueCollecting = !(t is Antlr4.Runtime.Atn.ActionTransition) && collectPredicates;
				ATNConfig c = GetEpsilonTarget(config, t, continueCollecting, depth == 0, contextCache
					);
				if (c != null)
				{
					if (t is Antlr4.Runtime.Atn.RuleTransition)
					{
						if (intermediate != null && !collectPredicates)
						{
							intermediate.Add(c, contextCache);
							continue;
						}
					}
					if (optimize_closure_busy)
					{
						bool checkClosure = false;
						switch (c.GetState().GetStateType())
						{
							case ATNState.StarLoopEntry:
							case ATNState.BlockEnd:
							case ATNState.LoopEnd:
							{
								checkClosure = true;
								break;
							}

							case ATNState.PlusBlockStart:
							{
								checkClosure = true;
								break;
							}

							case ATNState.RuleStop:
							{
								checkClosure = c.GetContext().IsEmpty();
								break;
							}

							default:
							{
								break;
								break;
							}
						}
						if (checkClosure && !closureBusy.AddItem(c))
						{
							continue;
						}
					}
					int newDepth = depth;
					if (config.GetState() is RuleStopState)
					{
						// target fell off end of rule; mark resulting c as having dipped into outer context
						// We can't get here if incoming config was rule stop and we had context
						// track how far we dip into outer context.  Might
						// come in handy and we avoid evaluating context dependent
						// preds if this is > 0.
						c.SetOuterContextDepth(c.GetOuterContextDepth() + 1);
						System.Diagnostics.Debug.Assert(newDepth > int.MinValue);
						newDepth--;
					}
					else
					{
						if (t is Antlr4.Runtime.Atn.RuleTransition)
						{
							if (optimize_tail_calls && ((Antlr4.Runtime.Atn.RuleTransition)t).optimizedTailCall
								 && (!tail_call_preserves_sll || !PredictionContext.IsEmptyLocal(config.GetContext
								())))
							{
								System.Diagnostics.Debug.Assert(c.GetContext() == config.GetContext());
								if (newDepth == 0)
								{
									// the pop/push of a tail call would keep the depth
									// constant, except we latch if it goes negative
									newDepth--;
									if (!tail_call_preserves_sll && PredictionContext.IsEmptyLocal(config.GetContext(
										)))
									{
										// make sure the SLL config "dips into the outer context" or prediction may not fall back to LL on conflict
										c.SetOuterContextDepth(c.GetOuterContextDepth() + 1);
									}
								}
							}
							else
							{
								// latch when newDepth goes negative - once we step out of the entry context we can't return
								if (newDepth >= 0)
								{
									newDepth++;
								}
							}
						}
					}
					Closure(c, configs, intermediate, closureBusy, continueCollecting, hasMoreContexts
						, contextCache, newDepth);
				}
			}
		}

		[NotNull]
		public virtual string GetRuleName(int index)
		{
			if (parser != null && index >= 0)
			{
				return parser.GetRuleNames()[index];
			}
			return "<rule " + index + ">";
		}

		[Nullable]
		public virtual ATNConfig GetEpsilonTarget(ATNConfig config, Transition t, bool collectPredicates
			, bool inContext, PredictionContextCache contextCache)
		{
			switch (t.GetSerializationType())
			{
				case Transition.Rule:
				{
					return RuleTransition(config, (Antlr4.Runtime.Atn.RuleTransition)t, contextCache);
				}

				case Transition.Precedence:
				{
					return PrecedenceTransition(config, (PrecedencePredicateTransition)t, collectPredicates
						, inContext);
				}

				case Transition.Predicate:
				{
					return PredTransition(config, (PredicateTransition)t, collectPredicates, inContext
						);
				}

				case Transition.Action:
				{
					return ActionTransition(config, (Antlr4.Runtime.Atn.ActionTransition)t);
				}

				case Transition.Epsilon:
				{
					return config.Transform(t.target);
				}

				default:
				{
					return null;
					break;
				}
			}
		}

		[NotNull]
		public virtual ATNConfig ActionTransition(ATNConfig config, Antlr4.Runtime.Atn.ActionTransition
			 t)
		{
			return config.Transform(t.target);
		}

		[Nullable]
		public virtual ATNConfig PrecedenceTransition(ATNConfig config, PrecedencePredicateTransition
			 pt, bool collectPredicates, bool inContext)
		{
			ATNConfig c = null;
			if (collectPredicates && inContext)
			{
				SemanticContext newSemCtx = SemanticContext.And(config.GetSemanticContext(), pt.GetPredicate
					());
				c = config.Transform(pt.target, newSemCtx);
			}
			else
			{
				c = config.Transform(pt.target);
			}
			return c;
		}

		[Nullable]
		public virtual ATNConfig PredTransition(ATNConfig config, PredicateTransition pt, 
			bool collectPredicates, bool inContext)
		{
			ATNConfig c;
			if (collectPredicates && (!pt.isCtxDependent || (pt.isCtxDependent && inContext)))
			{
				SemanticContext newSemCtx = SemanticContext.And(config.GetSemanticContext(), pt.GetPredicate
					());
				c = config.Transform(pt.target, newSemCtx);
			}
			else
			{
				c = config.Transform(pt.target);
			}
			return c;
		}

		[NotNull]
		public virtual ATNConfig RuleTransition(ATNConfig config, Antlr4.Runtime.Atn.RuleTransition
			 t, PredictionContextCache contextCache)
		{
			ATNState returnState = t.followState;
			PredictionContext newContext;
			if (optimize_tail_calls && t.optimizedTailCall && (!tail_call_preserves_sll || !PredictionContext
				.IsEmptyLocal(config.GetContext())))
			{
				newContext = config.GetContext();
			}
			else
			{
				if (contextCache != null)
				{
					newContext = contextCache.GetChild(config.GetContext(), returnState.stateNumber);
				}
				else
				{
					newContext = config.GetContext().GetChild(returnState.stateNumber);
				}
			}
			return config.Transform(t.target, newContext);
		}

		private sealed class _IComparer_1557 : IComparer<ATNConfig>
		{
			public _IComparer_1557()
			{
			}

			public int Compare(ATNConfig o1, ATNConfig o2)
			{
				int diff = o1.GetState().GetNonStopStateNumber() - o2.GetState().GetNonStopStateNumber
					();
				if (diff != 0)
				{
					return diff;
				}
				diff = o1.GetAlt() - o2.GetAlt();
				if (diff != 0)
				{
					return diff;
				}
				return 0;
			}
		}

		private static readonly IComparer<ATNConfig> StateAltSortComparator = new _IComparer_1557
			();

		private BitSet IsConflicted(ATNConfigSet configset, PredictionContextCache contextCache
			)
		{
			if (configset.GetUniqueAlt() != ATN.InvalidAltNumber || configset.Count <= 1)
			{
				return null;
			}
			IList<ATNConfig> configs = new AList<ATNConfig>(configset);
			configs.Sort(StateAltSortComparator);
			bool exact = !configset.GetDipsIntoOuterContext() && predictionMode == PredictionMode
				.LlExactAmbigDetection;
			BitSet alts = new BitSet();
			int minAlt = configs[0].GetAlt();
			alts.Set(minAlt);
			// quick check 1 & 2 => if we assume #1 holds and check #2 against the
			// minAlt from the first state, #2 will fail if the assumption was
			// incorrect
			int currentState = configs[0].GetState().GetNonStopStateNumber();
			for (int i = 0; i < configs.Count; i++)
			{
				ATNConfig config = configs[i];
				int stateNumber = config.GetState().GetNonStopStateNumber();
				if (stateNumber != currentState)
				{
					if (config.GetAlt() != minAlt)
					{
						return null;
					}
					currentState = stateNumber;
				}
			}
			BitSet representedAlts = null;
			if (exact)
			{
				currentState = configs[0].GetState().GetNonStopStateNumber();
				// get the represented alternatives of the first state
				representedAlts = new BitSet();
				int maxAlt = minAlt;
				for (int i_1 = 0; i_1 < configs.Count; i_1++)
				{
					ATNConfig config = configs[i_1];
					if (config.GetState().GetNonStopStateNumber() != currentState)
					{
						break;
					}
					int alt = config.GetAlt();
					representedAlts.Set(alt);
					maxAlt = alt;
				}
				// quick check #3:
				currentState = configs[0].GetState().GetNonStopStateNumber();
				int currentAlt = minAlt;
				for (int i_2 = 0; i_2 < configs.Count; i_2++)
				{
					ATNConfig config = configs[i_2];
					int stateNumber = config.GetState().GetNonStopStateNumber();
					int alt = config.GetAlt();
					if (stateNumber != currentState)
					{
						if (currentAlt != maxAlt)
						{
							return null;
						}
						currentState = stateNumber;
						currentAlt = minAlt;
					}
					else
					{
						if (alt != currentAlt)
						{
							if (alt != representedAlts.NextSetBit(currentAlt + 1))
							{
								return null;
							}
							currentAlt = alt;
						}
					}
				}
			}
			currentState = configs[0].GetState().GetNonStopStateNumber();
			int firstIndexCurrentState = 0;
			int lastIndexCurrentStateMinAlt = 0;
			PredictionContext joinedCheckContext = configs[0].GetContext();
			for (int i_3 = 1; i_3 < configs.Count; i_3++)
			{
				ATNConfig config = configs[i_3];
				if (config.GetAlt() != minAlt)
				{
					break;
				}
				if (config.GetState().GetNonStopStateNumber() != currentState)
				{
					break;
				}
				lastIndexCurrentStateMinAlt = i_3;
				joinedCheckContext = contextCache.Join(joinedCheckContext, configs[i_3].GetContext
					());
			}
			for (int i_4 = lastIndexCurrentStateMinAlt + 1; i_4 < configs.Count; i_4++)
			{
				ATNConfig config = configs[i_4];
				ATNState state = config.GetState();
				alts.Set(config.GetAlt());
				if (state.GetNonStopStateNumber() != currentState)
				{
					currentState = state.GetNonStopStateNumber();
					firstIndexCurrentState = i_4;
					lastIndexCurrentStateMinAlt = i_4;
					joinedCheckContext = config.GetContext();
					for (int j = firstIndexCurrentState + 1; j < configs.Count; j++)
					{
						ATNConfig config2 = configs[j];
						if (config2.GetAlt() != minAlt)
						{
							break;
						}
						if (config2.GetState().GetNonStopStateNumber() != currentState)
						{
							break;
						}
						lastIndexCurrentStateMinAlt = j;
						joinedCheckContext = contextCache.Join(joinedCheckContext, config2.GetContext());
					}
					i_4 = lastIndexCurrentStateMinAlt;
					continue;
				}
				PredictionContext joinedCheckContext2 = config.GetContext();
				int currentAlt = config.GetAlt();
				int lastIndexCurrentStateCurrentAlt = i_4;
				for (int j_1 = lastIndexCurrentStateCurrentAlt + 1; j_1 < configs.Count; j_1++)
				{
					ATNConfig config2 = configs[j_1];
					if (config2.GetAlt() != currentAlt)
					{
						break;
					}
					if (config2.GetState().GetNonStopStateNumber() != currentState)
					{
						break;
					}
					lastIndexCurrentStateCurrentAlt = j_1;
					joinedCheckContext2 = contextCache.Join(joinedCheckContext2, config2.GetContext()
						);
				}
				i_4 = lastIndexCurrentStateCurrentAlt;
				if (exact)
				{
					if (!joinedCheckContext.Equals(joinedCheckContext2))
					{
						return null;
					}
				}
				else
				{
					PredictionContext check = contextCache.Join(joinedCheckContext, joinedCheckContext2
						);
					if (!joinedCheckContext.Equals(check))
					{
						return null;
					}
				}
				if (!exact && optimize_hidden_conflicted_configs)
				{
					for (int j = firstIndexCurrentState; j_1 <= lastIndexCurrentStateMinAlt; j_1++)
					{
						ATNConfig checkConfig = configs[j_1];
						if (checkConfig.GetSemanticContext() != SemanticContext.None && !checkConfig.GetSemanticContext
							().Equals(config.GetSemanticContext()))
						{
							continue;
						}
						if (joinedCheckContext != checkConfig.GetContext())
						{
							PredictionContext check = contextCache.Join(checkConfig.GetContext(), config.GetContext
								());
							if (!checkConfig.GetContext().Equals(check))
							{
								continue;
							}
						}
						config.SetHidden(true);
					}
				}
			}
			return alts;
		}

		protected internal virtual BitSet GetConflictingAltsFromConfigSet(ATNConfigSet configs
			)
		{
			BitSet conflictingAlts = configs.GetConflictingAlts();
			if (conflictingAlts == null && configs.GetUniqueAlt() != ATN.InvalidAltNumber)
			{
				conflictingAlts = new BitSet();
				conflictingAlts.Set(configs.GetUniqueAlt());
			}
			return conflictingAlts;
		}

		protected internal virtual int ResolveToMinAlt(DFAState D, BitSet conflictingAlts
			)
		{
			// kill dead alts so we don't chase them ever
			//		killAlts(conflictingAlts, D.configset);
			D.prediction = conflictingAlts.NextSetBit(0);
			return D.prediction;
		}

		[NotNull]
		public virtual string GetTokenName(int t)
		{
			if (t == Token.Eof)
			{
				return "EOF";
			}
			if (parser != null && parser.GetTokenNames() != null)
			{
				string[] tokensNames = parser.GetTokenNames();
				if (t >= tokensNames.Length)
				{
					System.Console.Error.WriteLine(t + " ttype out of range: " + Arrays.ToString(tokensNames
						));
					System.Console.Error.WriteLine(((CommonTokenStream)((TokenStream)parser.GetInputStream
						())).GetTokens());
				}
				else
				{
					return tokensNames[t] + "<" + t + ">";
				}
			}
			return t.ToString();
		}

		public virtual string GetLookaheadName(TokenStream input)
		{
			return GetTokenName(input.La(1));
		}

		public virtual void DumpDeadEndConfigs(NoViableAltException nvae)
		{
			System.Console.Error.WriteLine("dead end configs: ");
			foreach (ATNConfig c in nvae.GetDeadEndConfigs())
			{
				string trans = "no edges";
				if (c.GetState().GetNumberOfOptimizedTransitions() > 0)
				{
					Transition t = c.GetState().GetOptimizedTransition(0);
					if (t is AtomTransition)
					{
						AtomTransition at = (AtomTransition)t;
						trans = "Atom " + GetTokenName(at.label);
					}
					else
					{
						if (t is SetTransition)
						{
							SetTransition st = (SetTransition)t;
							bool not = st is NotSetTransition;
							trans = (not ? "~" : string.Empty) + "Set " + st.set.ToString();
						}
					}
				}
				System.Console.Error.WriteLine(c.ToString(parser, true) + ":" + trans);
			}
		}

		[NotNull]
		public virtual NoViableAltException NoViableAlt(TokenStream input, ParserRuleContext
			 outerContext, ATNConfigSet configs, int startIndex)
		{
			return new NoViableAltException(parser, input, input.Get(startIndex), input.Lt(1)
				, configs, outerContext);
		}

		public virtual int GetUniqueAlt(ICollection<ATNConfig> configs)
		{
			int alt = ATN.InvalidAltNumber;
			foreach (ATNConfig c in configs)
			{
				if (alt == ATN.InvalidAltNumber)
				{
					alt = c.GetAlt();
				}
				else
				{
					// found first alt
					if (c.GetAlt() != alt)
					{
						return ATN.InvalidAltNumber;
					}
				}
			}
			return alt;
		}

		public virtual bool ConfigWithAltAtStopState(ICollection<ATNConfig> configs, int 
			alt)
		{
			foreach (ATNConfig c in configs)
			{
				if (c.GetAlt() == alt)
				{
					if (c.GetState() is RuleStopState)
					{
						return true;
					}
				}
			}
			return false;
		}

		[NotNull]
		protected internal virtual DFAState AddDFAEdge(DFA dfa, DFAState fromState, int t
			, IntegerList contextTransitions, ATNConfigSet toConfigs, PredictionContextCache
			 contextCache)
		{
			System.Diagnostics.Debug.Assert(dfa.IsContextSensitive() || contextTransitions ==
				 null || contextTransitions.IsEmpty());
			DFAState from = fromState;
			DFAState to = AddDFAState(dfa, toConfigs, contextCache);
			if (contextTransitions != null)
			{
				foreach (int context in contextTransitions.ToArray())
				{
					if (context == PredictionContext.EmptyFullStateKey)
					{
						if (from.configs.IsOutermostConfigSet())
						{
							continue;
						}
					}
					from.SetContextSensitive(atn);
					from.SetContextSymbol(t);
					DFAState next = from.GetContextTarget(context);
					if (next != null)
					{
						from = next;
						continue;
					}
					next = AddDFAContextState(dfa, from.configs, context, contextCache);
					System.Diagnostics.Debug.Assert(context != PredictionContext.EmptyFullStateKey ||
						 next.configs.IsOutermostConfigSet());
					from.SetContextTarget(context, next);
					from = next;
				}
			}
			AddDFAEdge(from, t, to);
			return to;
		}

		protected internal virtual void AddDFAEdge(DFAState p, int t, DFAState q)
		{
			if (p != null)
			{
				p.SetTarget(t, q);
			}
		}

		/// <summary>See comment on LexerInterpreter.addDFAState.</summary>
		/// <remarks>See comment on LexerInterpreter.addDFAState.</remarks>
		[NotNull]
		protected internal virtual DFAState AddDFAContextState(DFA dfa, ATNConfigSet configs
			, int returnContext, PredictionContextCache contextCache)
		{
			if (returnContext != PredictionContext.EmptyFullStateKey)
			{
				ATNConfigSet contextConfigs = new ATNConfigSet();
				foreach (ATNConfig config in configs)
				{
					contextConfigs.AddItem(config.AppendContext(returnContext, contextCache));
				}
				return AddDFAState(dfa, contextConfigs, contextCache);
			}
			else
			{
				System.Diagnostics.Debug.Assert(!configs.IsOutermostConfigSet(), "Shouldn't be adding a duplicate edge."
					);
				configs = configs.Clone(true);
				configs.SetOutermostConfigSet(true);
				return AddDFAState(dfa, configs, contextCache);
			}
		}

		/// <summary>See comment on LexerInterpreter.addDFAState.</summary>
		/// <remarks>See comment on LexerInterpreter.addDFAState.</remarks>
		[NotNull]
		protected internal virtual DFAState AddDFAState(DFA dfa, ATNConfigSet configs, PredictionContextCache
			 contextCache)
		{
			if (!configs.IsReadOnly())
			{
				configs.OptimizeConfigs(this);
			}
			DFAState proposed = CreateDFAState(configs);
			DFAState existing = dfa.states.Get(proposed);
			if (existing != null)
			{
				return existing;
			}
			if (!configs.IsReadOnly())
			{
				if (configs.GetConflictingAlts() == null)
				{
					configs.SetConflictingAlts(IsConflicted(configs, contextCache));
					if (optimize_hidden_conflicted_configs && configs.GetConflictingAlts() != null)
					{
						int size = configs.Count;
						configs.StripHiddenConfigs();
						if (configs.Count < size)
						{
							proposed = CreateDFAState(configs);
							existing = dfa.states.Get(proposed);
							if (existing != null)
							{
								return existing;
							}
						}
					}
				}
			}
			DFAState newState = CreateDFAState(configs.Clone(true));
			DecisionState decisionState = atn.GetDecisionState(dfa.decision);
			int predictedAlt = GetUniqueAlt(configs);
			if (predictedAlt != ATN.InvalidAltNumber)
			{
				newState.isAcceptState = true;
				newState.prediction = predictedAlt;
			}
			else
			{
				if (configs.GetConflictingAlts() != null)
				{
					newState.isAcceptState = true;
					newState.prediction = ResolveToMinAlt(newState, newState.configs.GetConflictingAlts
						());
				}
			}
			if (newState.isAcceptState && configs.HasSemanticContext())
			{
				PredicateDFAState(newState, configs, decisionState.GetNumberOfTransitions());
			}
			DFAState added = dfa.AddState(newState);
			if (debug && added == newState)
			{
				System.Console.Out.WriteLine("adding new DFA state: " + newState);
			}
			return added;
		}

		[NotNull]
		protected internal virtual DFAState CreateDFAState(ATNConfigSet configs)
		{
			return new DFAState(configs, -1, atn.maxTokenType);
		}

		//	public void reportConflict(int startIndex, int stopIndex,
		//							   @NotNull IntervalSet alts,
		//							   @NotNull ATNConfigSet configs)
		//	{
		//		if ( debug || retry_debug ) {
		//			System.out.println("reportConflict "+alts+":"+configs+
		//							   ", input="+parser.getInputString(startIndex, stopIndex));
		//		}
		//		if ( parser!=null ) parser.getErrorHandler().reportConflict(parser, startIndex, stopIndex, alts, configs);
		//	}
		public virtual void ReportAttemptingFullContext(DFA dfa, SimulatorState initialState
			, int startIndex, int stopIndex)
		{
			if (debug || retry_debug)
			{
				Interval interval = Interval.Of(startIndex, stopIndex);
				System.Console.Out.WriteLine("reportAttemptingFullContext decision=" + dfa.decision
					 + ":" + initialState.s0.configs + ", input=" + ((TokenStream)parser.GetInputStream
					()).GetText(interval));
			}
			if (parser != null)
			{
				((ParserErrorListener)parser.GetErrorListenerDispatch()).ReportAttemptingFullContext
					(parser, dfa, startIndex, stopIndex, initialState);
			}
		}

		public virtual void ReportContextSensitivity(DFA dfa, SimulatorState acceptState, 
			int startIndex, int stopIndex)
		{
			if (debug || retry_debug)
			{
				Interval interval = Interval.Of(startIndex, stopIndex);
				System.Console.Out.WriteLine("reportContextSensitivity decision=" + dfa.decision 
					+ ":" + acceptState.s0.configs + ", input=" + ((TokenStream)parser.GetInputStream
					()).GetText(interval));
			}
			if (parser != null)
			{
				((ParserErrorListener)parser.GetErrorListenerDispatch()).ReportContextSensitivity
					(parser, dfa, startIndex, stopIndex, acceptState);
			}
		}

		/// <summary>If context sensitive parsing, we know it's ambiguity not conflict</summary>
		public virtual void ReportAmbiguity(DFA dfa, DFAState D, int startIndex, int stopIndex
			, BitSet ambigAlts, ATNConfigSet configs)
		{
			if (debug || retry_debug)
			{
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
				Interval interval = Interval.Of(startIndex, stopIndex);
				System.Console.Out.WriteLine("reportAmbiguity " + ambigAlts + ":" + configs + ", input="
					 + ((TokenStream)parser.GetInputStream()).GetText(interval));
			}
			if (parser != null)
			{
				((ParserErrorListener)parser.GetErrorListenerDispatch()).ReportAmbiguity(parser, 
					dfa, startIndex, stopIndex, ambigAlts, configs);
			}
		}

		protected internal int GetReturnState(RuleContext context)
		{
			if (context.IsEmpty())
			{
				return PredictionContext.EmptyFullStateKey;
			}
			ATNState state = atn.states[context.invokingState];
			Antlr4.Runtime.Atn.RuleTransition transition = (Antlr4.Runtime.Atn.RuleTransition
				)state.Transition(0);
			return transition.followState.stateNumber;
		}

		protected internal ParserRuleContext SkipTailCalls(ParserRuleContext context)
		{
			if (!optimize_tail_calls)
			{
				return context;
			}
			while (!context.IsEmpty())
			{
				ATNState state = atn.states[context.invokingState];
				System.Diagnostics.Debug.Assert(state.GetNumberOfTransitions() == 1 && state.Transition
					(0).GetSerializationType() == Transition.Rule);
				Antlr4.Runtime.Atn.RuleTransition transition = (Antlr4.Runtime.Atn.RuleTransition
					)state.Transition(0);
				if (!transition.tailCall)
				{
					break;
				}
				context = ((ParserRuleContext)context.GetParent());
			}
			return context;
		}
	}
}
