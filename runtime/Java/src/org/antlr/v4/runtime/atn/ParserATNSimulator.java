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

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.antlr.v4.runtime.tree.ASTNodeStream;
import org.antlr.v4.runtime.tree.BufferedASTNodeStream;
import org.antlr.v4.runtime.tree.TreeParser;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.*;

public class ParserATNSimulator<Symbol> extends ATNSimulator {
	public static boolean debug = false;
	public static boolean dfa_debug = false;

	public static int ATN_failover = 0;
	public static int predict_calls = 0;
	public static int retry_with_context = 0;
	public static int retry_with_context_indicates_no_conflict = 0;

	public static boolean buildDFA = false;

	@Nullable
	protected final BaseRecognizer<Symbol> parser;

	@NotNull
	public final Map<RuleContext, DFA[]> ctxToDFAs;
	public Map<RuleContext, DFA>[] decisionToDFAPerCtx; // TODO: USE THIS ONE
	@NotNull
	public final DFA[] decisionToDFA;
	protected boolean userWantsCtxSensitive = false;

	/** This is the original context upon entry to the ATN simulator.
	 *  ATNConfig objects carry on tracking the new context derived from
	 *  the decision point. This field is used instead of passing the value
	 *  around to the various functions, which would be confusing. Its
	 *  value is reset upon prediction call to adaptivePredict() or the
	 *  predictATN/DFA methods.
	 *
	 *  The full stack at any moment is [config.outerContext + config.context].
	 */
	@NotNull
	protected RuleContext outerContext = RuleContext.EMPTY;
	@Nullable
	protected ATNConfig prevAccept; // TODO Move down? used to avoid passing int down and back up in method calls
	protected int prevAcceptIndex = -1;

	public ParserATNSimulator(@NotNull ATN atn) {
		this(null, atn);
	}

	public ParserATNSimulator(@Nullable BaseRecognizer<Symbol> parser, @NotNull ATN atn) {
		super(atn);
		this.parser = parser;
		ctxToDFAs = new HashMap<RuleContext, DFA[]>();
		// TODO (sam): why distinguish on parser != null?
		decisionToDFA = new DFA[atn.getNumberOfDecisions() + (parser != null ? 1 : 0)];
//		DOTGenerator dot = new DOTGenerator(null);
//		System.out.println(dot.getDOT(atn.rules.get(0), parser.getRuleNames()));
//		System.out.println(dot.getDOT(atn.rules.get(1), parser.getRuleNames()));
	}

	public int adaptivePredict(@NotNull SymbolStream<Symbol> input, int decision, @Nullable RuleContext outerContext) {
		predict_calls++;
		DFA dfa = decisionToDFA[decision];
		if ( !buildDFA || dfa==null || dfa.s0==null ) {
			ATNState startState = atn.decisionToState.get(decision);
			decisionToDFA[decision] = dfa = new DFA(startState);
			dfa.decision = decision;
			return predictATN(dfa, input, outerContext, false);
		}
		else {
			//dump(dfa);
			// start with the DFA
			int m = input.mark();
			int index = input.index();
			try {
				int alt = execDFA(input, dfa, index, dfa.s0, outerContext);
				return alt;
			}
			finally {
				input.seek(index);
				input.release(m);
			}
		}
	}

	public void reset() {
		userWantsCtxSensitive = false;
		outerContext = RuleContext.EMPTY;
		prevAccept = null;
		prevAcceptIndex = -1;
	}

	public int predictATN(@NotNull DFA dfa, @NotNull SymbolStream<Symbol> input,
						  @Nullable RuleContext outerContext,
						  boolean useContext)
	{
		if ( outerContext==null ) outerContext = RuleContext.EMPTY;
		this.outerContext = outerContext;
		if ( debug ) System.out.println("ATN decision "+dfa.decision+
										" exec LA(1)=="+ getLookaheadName(input) +
										", outerContext="+outerContext.toString(parser));
		RuleContext ctx = RuleContext.EMPTY;
		if ( useContext ) ctx = outerContext;
		OrderedHashSet<ATNConfig> s0_closure =
			computeStartState(dfa.decision, dfa.atnStartState, ctx);
		dfa.s0 = addDFAState(dfa, s0_closure);
		if ( prevAccept!=null ) {
			dfa.s0.isAcceptState = true;
			dfa.s0.prediction = prevAccept.alt;
		}

		int alt = 0;
		int m = input.mark();
		int index = input.index();
		try {
			alt = execATN(input, dfa, index, s0_closure, useContext);
		}
		catch (NoViableAltException nvae) {
			if ( debug ) dumpDeadEndConfigs(nvae);
			throw nvae;
		}
		finally {
			input.seek(index);
			input.release(m);
		}
		if ( debug ) System.out.println("DFA after predictATN: "+dfa.toString());
		return alt;
	}

	// doesn't create DFA when matching
	public int matchATN(@NotNull SymbolStream<Symbol> input,
						@NotNull ATNState startState)
	{
		DFA dfa = new DFA(startState);
		RuleContext ctx = RuleContext.EMPTY;
		OrderedHashSet<ATNConfig> s0_closure =
			computeStartState(dfa.decision, startState, ctx);
		return execATN(input, dfa, input.index(), s0_closure, false);
	}

	public int execDFA(@NotNull SymbolStream<Symbol> input, @NotNull DFA dfa,
					   int startIndex,
					   @NotNull DFAState s0,
                       @Nullable RuleContext outerContext)
    {
//		dump(dfa);
		if ( outerContext==null ) outerContext = RuleContext.EMPTY;
		this.outerContext = outerContext;
		if ( dfa_debug ) System.out.println("DFA decision "+dfa.decision+
											" exec LA(1)=="+ getLookaheadName(input) +
											", outerContext="+outerContext.toString(parser));
		DFAState prevAcceptState = null;
		DFAState s = s0;
		int t = input.LA(1);
	loop:
		while ( true ) {
			if ( dfa_debug ) System.out.println("DFA state "+s.stateNumber+" LA(1)=="+t);
			// TODO: ctxSensitive
			if ( s.isCtxSensitive ) {
				Integer predI = s.ctxToPrediction.get(outerContext);
				if ( dfa_debug ) System.out.println("ctx sensitive state "+outerContext+"->"+predI+
								 				    " in "+s);
				if ( predI!=null ) return predI;
//				System.out.println("start all over with ATN; can't use DFA");
				// start all over with ATN; can't use DFA
				input.seek(startIndex);
				DFA throwAwayDFA = new DFA(dfa.atnStartState);
				int alt = execATN(input, throwAwayDFA, startIndex, s0.configs, false);
				s.ctxToPrediction.put(outerContext, alt);
				return alt;
			}
			if ( s.isAcceptState ) {
				if ( s.predicates!=null ) {
					if ( dfa_debug ) System.out.println("accept "+s);
				}
				else {
					if ( dfa_debug ) System.out.println("accept; predict "+s.prediction +" in state "+s.stateNumber);
				}
				prevAcceptState = s;
				// keep going unless we're at EOF or state only has one alt number
				// mentioned in configs; check if something else could match
				// TODO: don't we always stop? only lexer would keep going
				// TODO: v3 dfa don't do this.
				if ( s.complete || t==CharStream.EOF ) break;
			}
			// if no edge, pop over to ATN interpreter, update DFA and return
			if ( s.edges == null || t >= s.edges.length || t < -1 || s.edges[t+1] == null ) {
				if ( dfa_debug ) System.out.println("no edge for "+t);
				int alt = -1;
				if ( dfa_debug ) {
					System.out.println("ATN exec upon "+
									   getInputString(input, startIndex) +
									   " at DFA state "+s.stateNumber);
				}
				try {
					alt = execATN(input, dfa, startIndex, s.configs, false);
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
						System.out.println("back from DFA update, alt="+alt+", dfa=\n"+dfa);
						//dump(dfa);
					}
					// action already executed
					if ( dfa_debug ) System.out.println("DFA decision "+dfa.decision+
														" predicts "+alt);
					return alt; // we've updated DFA, exec'd action, and have our deepest answer
				}
				catch (NoViableAltException nvae) {
					if ( buildDFA ) addDFAEdge(s, t, ERROR);
					throw nvae;
				}
			}
			DFAState target = s.edges[t+1];
			if ( target == ERROR ) {
				throw noViableAlt(input, outerContext, s.configs, startIndex);
			}
			s = target;
			input.consume();
			t = input.LA(1);
		}
		if ( prevAcceptState==null ) {
			if ( debug ) System.out.println("!!! no viable alt in dfa");
			return -1;
		}

		// TODO: Factor this code that is very similar to ATN version
		// Before jumping to prediction, check to see if there are
		// disambiguating or validating predicates to evaluate
		if ( s.predicates!=null ) {
			// rewind input so pred's LT(i) calls make sense
			input.seek(startIndex);
			int predictedAlt = evalSemanticContext(s.predicates);
			if ( predictedAlt!=ATN.INVALID_ALT_NUMBER ) {
				return predictedAlt;
			}
			throw noViableAlt(input, outerContext, s.configs, startIndex);
		}

		if ( dfa_debug ) System.out.println("DFA decision "+dfa.decision+
											" predicts "+prevAcceptState.prediction);
		return prevAcceptState.prediction;
	}

	public String getInputString(@NotNull SymbolStream<Symbol> input, int start) {
		if ( input instanceof TokenStream ) {
			return ((TokenStream)input).toString(start,input.index());
		}
		else if ( input instanceof ASTNodeStream) {
			return ((ASTNodeStream<Symbol>)input).toString(input.get(start),input.LT(1));
		}
		return "n/a";
	}

	public int execATN(@NotNull SymbolStream<Symbol> input,
					   @NotNull DFA dfa,
					   int startIndex,
					   @NotNull OrderedHashSet<ATNConfig> s0,
					   boolean useContext)
	{
		if ( debug ) System.out.println("execATN decision "+dfa.decision+" exec LA(1)=="+ getLookaheadName(input));
		ATN_failover++;
		OrderedHashSet<ATNConfig> closure = new OrderedHashSet<ATNConfig>();

		closure.addAll(s0);

		if ( debug ) System.out.println("start state closure="+closure);

		int t = input.LA(1);
		if ( t==Token.EOF && prevAccept!=null ) {
			// computeStartState must have reached end of rule
			return prevAccept.alt;
		}

		@NotNull DecisionState decState = null;
		//if ( atn.decisionToState.size()>0 )
        decState = atn.decisionToState.get(dfa.decision);
		if ( debug ) System.out.println("decision state = "+decState);

		prevAccept = null;
		prevAcceptIndex = -1;
		OrderedHashSet<ATNConfig> reach = new OrderedHashSet<ATNConfig>();

		do { // while more work
			if ( debug ) System.out.println("in reach starting closure: " + closure);
			int ncl = closure.size();
			for (int ci=0; ci<ncl; ci++) { // TODO: foreach
				ATNConfig c = closure.get(ci);
				if ( debug ) System.out.println("testing "+getTokenName(t)+" at "+c.toString());
				int n = c.state.getNumberOfTransitions();
				for (int ti=0; ti<n; ti++) {               // for each transition
					Transition trans = c.state.transition(ti);
					ATNState target = getReachableTarget(trans, t);
					if ( target!=null ) {
						Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
						closure(new ATNConfig(c, target), reach, decState, closureBusy,
                                c.semanticContext, false);
					}
				}
			}

			// resolve ambig in DFAState for reach
			Set<Integer> ambigAlts = getAmbiguousAlts(reach);
			if ( ambigAlts!=null ) {
				if ( debug ) {
					int i = -1;
					if ( outerContext!=null && outerContext.s>=0 ) {
						i = atn.states.get(outerContext.s).ruleIndex;
					}
					String rname = getRuleName(i);
					System.out.println("AMBIG dec "+dfa.decision+" in "+rname+" for alt "+ambigAlts+" upon "+
									   getInputString(input, startIndex));
					System.out.println("REACH="+reach);
				}

                // can we resolve with predicates?
                SemanticContext[] altToPred =
					getPredsForAmbigAlts(decState, ambigAlts, reach);
                if ( altToPred!=null ) {
					// We need at least n-1 predicates for n ambiguous alts
					if ( tooFewPredicates(altToPred) ) {
						reportInsufficientPredicates(startIndex, input.index(),
													 ambigAlts, altToPred, reach);
					}
					List<DFAState.PredPrediction> predPredictions =
						getPredicatePredictions(altToPred);
					if ( buildDFA ) {
						DFAState accept = addDFAEdge(dfa, closure, t, reach);
						makeAcceptState(accept, predPredictions);
					}
					// rewind input so pred's LT(i) calls make sense
					input.seek(startIndex);
                    int uniqueAlt = evalSemanticContext(predPredictions);
					if ( uniqueAlt==ATN.INVALID_ALT_NUMBER ) {
						// no true pred and/or no uncovered alt
						// to fall back on. must announce parsing error.
						throw noViableAlt(input, outerContext, closure, startIndex);
					}
                    return uniqueAlt;
                }

				dfa.conflict = true; // at least one DFA state is ambiguous
				if ( !userWantsCtxSensitive ) reportConflict(startIndex, input.index(), ambigAlts, reach);

//				ATNState loc = atn.states.get(outerContext.s);
//				String rname = recog.getRuleNames()[loc.ruleIndex];
//				System.out.println("AMBIG orig="+outerContext.toString((BaseRecognizer)recog)+" for alt "+ambigAlts+" upon "+
//								   input.toString(startIndex, input.index()));
				if ( !userWantsCtxSensitive || useContext ) {
					// resolve ambiguity
					if ( decState.isGreedy ) {
						// if greedy, resolve in favor of alt coming first
						resolveToMinAlt(reach, ambigAlts);
					}
					else {
						// if nongreedy loop, always pick exit branch to match
						// what follows instead of re-entering loop
						resolveNongreedyToExitBranch(reach, ambigAlts);
					}
				}
				else {
					return retryWithContext(input, dfa, startIndex, outerContext,
											closure, t, reach, ambigAlts);
				}
			}

			// if reach predicts single alt, can stop

			int uniqueAlt = getUniqueAlt(reach);
			if ( uniqueAlt!=ATN.INVALID_ALT_NUMBER ) {
				if ( debug ) System.out.println("PREDICT alt "+uniqueAlt+
												" decision "+dfa.decision+
												" at index "+input.index());
				if ( !buildDFA ) return uniqueAlt;
				// edge from closure-t->reach now
                DFAState accept = addDFAEdge(dfa, closure, t, reach);
				// now check to see if we have a validating predicate.
				// We know that it's validating because there is only
				// one predicted alternative
				Set<Integer> uniqueAltSet = new HashSet<Integer>();
				uniqueAltSet.add(uniqueAlt);
				SemanticContext[] altToPred =
					getPredsForAmbigAlts(decState, uniqueAltSet, reach);
				// altToPred[uniqueAlt] is now our validating predicate (if any)
				if ( altToPred!=null ) {
					// we have a validating predicate; test it
					// Update DFA so reach becomes accept state with predicate
					List<DFAState.PredPrediction> predPredictions =
						getPredicatePredictions(altToPred);
					makeAcceptState(accept, predPredictions);
					// rewind input so pred's LT(i) calls make sense
					input.seek(startIndex);
					boolean validated = altToPred[uniqueAlt].eval(parser, outerContext);
					if ( debug || dfa_debug ) {
						System.out.println("eval alt "+uniqueAlt+" pred "+
											   altToPred[uniqueAlt]+"="+ validated);
					}
					if ( !validated ) {
						throw noViableAlt(input, outerContext, closure, startIndex);
					}
					return uniqueAlt;
				}
				makeAcceptState(accept, uniqueAlt);
				return uniqueAlt;
			}

			if ( decState!=null && !decState.isGreedy ) {
				// if we reached end of rule via exit branch, we matched
				int exitAlt = 2;
				ATNConfig cstop = configWithAltAtStopState(reach, exitAlt);
				if ( cstop!=null ) {
					if ( debug ) System.out.println("nongreedy at stop state for exit branch");
					prevAccept = cstop;
					prevAcceptIndex = input.index();
					break;
				}
			}

			if ( reach.size()==0 ) {
				break;
			}

			// If we matched t anywhere, need to consume and add closer-t->reach DFA edge
			// else error if no previous accept
			input.consume();
			if ( buildDFA ) addDFAEdge(dfa, closure, t, reach);
			t = input.LA(1);

			// swap to avoid reallocating space
			OrderedHashSet<ATNConfig> tmp = reach;
			reach = closure;
			closure = tmp;
			reach.clear(); // TODO: THIS MIGHT BE SLOW! kills each element; realloc might be faster
		} while ( true );

		if ( prevAccept==null ) {
//			System.out.println("no viable token at input "+ getLookaheadName(input) +", index "+input.index());
			throw noViableAlt(input, outerContext, closure, startIndex);
		}

		if ( debug ) System.out.println("PREDICT " + prevAccept + " index " + prevAccept.alt);
		return prevAccept.alt;
	}

	/** Look through a list of predicate/alt pairs, returning alt for the
	 *  first pair that wins. A null predicate indicates the default
	 *  prediction for disambiguating predicates.
	 */
    public int evalSemanticContext(@NotNull List<DFAState.PredPrediction> predPredictions) {
		for (DFAState.PredPrediction pair : predPredictions) {
			if ( pair.pred==null ) return pair.alt; // default prediction
			if ( debug || dfa_debug ) {
				System.out.println("eval pred "+pair+"="+pair.pred.eval(parser, outerContext));
			}
			if ( pair.pred.eval(parser, outerContext) ) {
				if ( debug || dfa_debug ) System.out.println("PREDICT "+pair.alt);
				return pair.alt;
			}
		}
		if ( debug || dfa_debug ) {
			System.out.println("failed validating predicate");
		}
		// no prediction; either all predicates are false and
		// all alternatives were guarded, or a validating predicate failed.
		return ATN.INVALID_ALT_NUMBER;
	}

    protected int resolveToMinAlt(@NotNull OrderedHashSet<ATNConfig> reach, @NotNull Set<Integer> ambigAlts) {
		int min = getMinAlt(ambigAlts);
		// create DFA accept state for resolved alt
		ambigAlts.remove(min);
		// kill dead alts so we don't chase them ever
		killAlts(ambigAlts, reach);
		if ( debug ) System.out.println("RESOLVED TO "+reach);
		return min;
	}

	protected int resolveNongreedyToExitBranch(@NotNull OrderedHashSet<ATNConfig> reach, @NotNull Set<Integer> ambigAlts) {
		// exit branch is alt 2 always; alt 1 is entry or loopback branch
		// since we're predicting, create DFA accept state for exit alt
		int exitAlt = 2;
		ambigAlts.remove(exitAlt);
		// kill dead alts so we don't chase them ever
		killAlts(ambigAlts, reach);
		if ( debug ) System.out.println("RESOLVED TO "+reach);
		return exitAlt;
	}

	public int retryWithContext(@NotNull SymbolStream<Symbol> input,
								@NotNull DFA dfa,
								int startIndex,
								@NotNull RuleContext originalContext,
								@NotNull OrderedHashSet<ATNConfig> closure,
								int t,
								@NotNull OrderedHashSet<ATNConfig> reach,
								@NotNull Set<Integer> ambigAlts)
	{
		// ASSUMES PREDICT ONLY
		retry_with_context++;
		int old_k = input.index();
		// retry using context, if any; if none, kill all but min as before
		if ( debug ) System.out.println("RETRY "+ getInputString(input, startIndex) +
										" with ctx="+ originalContext);
		int min = getMinAlt(ambigAlts);
		if ( originalContext==RuleContext.EMPTY ) {
			// no point in retrying with ctx since it's same.
			// this implies that we have a true ambiguity
			reportAmbiguity(startIndex, input.index(), ambigAlts, reach);
			return min;
		}
		// otherwise we have to retry with context, filling in tmp DFA.
		// if it comes back with conflict, we have a true ambiguity
		input.seek(startIndex); // rewind
		DFA ctx_dfa = new DFA(dfa.atnStartState);
		int ctx_alt = predictATN(ctx_dfa, input, originalContext, true);
		if ( debug ) System.out.println("retry predicts "+ctx_alt+" vs "+getMinAlt(ambigAlts)+
										" with conflict="+ctx_dfa.conflict+
										" dfa="+ctx_dfa);


		if ( ctx_dfa.conflict ) {
//			System.out.println("retry gives ambig for "+input.toString(startIndex, input.index()));
			reportAmbiguity(startIndex, input.index(), ambigAlts, reach);
		}
		else {
//			System.out.println("NO ambig for "+input.toString(startIndex, input.index()));
//			System.out.println(ctx_dfa.toString(parser.getTokenNames()));
			if ( old_k != input.index() ) {
				System.out.println("ACK!!!!!!!! diff k; old="+(old_k-startIndex+1)+", new="+(input.index()-startIndex+1));
			}
			retry_with_context_indicates_no_conflict++;
			reportContextSensitivity(startIndex, input.index(), ambigAlts, reach);
		}
		// it's not context-sensitive; true ambig. fall thru to strip dead alts

		// TODO: if ambig, why turn on ctx sensitive?

		int predictedAlt = ctx_alt;
		if ( buildDFA) {
			DFAState reachTarget = addDFAEdge(dfa, closure, t, reach);
			reachTarget.isCtxSensitive = true;
			if ( reachTarget.ctxToPrediction==null ) {
				reachTarget.ctxToPrediction = new LinkedHashMap<RuleContext, Integer>();
			}
			reachTarget.ctxToPrediction.put(originalContext, predictedAlt);
		}
//					System.out.println("RESOLVE to "+predictedAlt);
		//System.out.println(reachTarget.ctxToPrediction.size()+" size of ctx map");
		return predictedAlt;
	}

	@NotNull
	public OrderedHashSet<ATNConfig> computeStartState(int decision, @NotNull ATNState p,
                                                       @Nullable RuleContext ctx)
    {
		RuleContext initialContext = ctx; // always at least the implicit call to start rule
		OrderedHashSet<ATNConfig> configs = new OrderedHashSet<ATNConfig>();
		prevAccept = null; // might reach end rule; track
		prevAcceptIndex = -1;

		DecisionState decState = null;
		if ( atn.decisionToState.size()>0 ) decState = atn.decisionToState.get(decision);

		for (int i=0; i<p.getNumberOfTransitions(); i++) {
			ATNState target = p.transition(i).target;
			ATNConfig c = new ATNConfig(target, i+1, initialContext);
			Set<ATNConfig> closureBusy = new HashSet<ATNConfig>();
			closure(c, configs, decState, closureBusy, SemanticContext.NONE, true);
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
		else if ( trans instanceof RangeTransition ) {
			RangeTransition rt = (RangeTransition)trans;
			if ( ttype>=rt.from && ttype<=rt.to ) return rt.target;
		}
		else if ( trans instanceof WildcardTransition && ttype!=Token.EOF ) {
			return trans.target;
		}
		return null;
	}

	/* TODO: If we are doing predicates, there is no point in pursuing
	 closure operations if we reach a DFA state that uniquely predicts
	 alternative. We will not be caching that DFA state and it is a
	 waste to pursue the closure. Might have to advance when we do
	 ambig detection thought :(
	  */

	protected void closure(@NotNull ATNConfig config,
						   @NotNull OrderedHashSet<ATNConfig> configs,
						   @Nullable DecisionState decState,
						   @NotNull Set<ATNConfig> closureBusy,
                           @NotNull SemanticContext semanticContext,
                           boolean collectPredicates)
	{
		if ( debug ) System.out.println("closure("+config+")");

		if ( !closureBusy.add(config) ) return; // avoid infinite recursion

		if ( config.state instanceof RuleStopState ) {
			// We hit rule end. If we have context info, use it
			if ( config.context!=null && !config.context.isEmpty() ) {
				RuleContext newContext = config.context.parent; // "pop" invoking state
				ATNState invokingState = atn.states.get(config.context.invokingState);
				RuleTransition rt = (RuleTransition)invokingState.transition(0);
				ATNState retState = rt.followState;
				ATNConfig c = new ATNConfig(retState, config.alt, newContext);
				// While we have context to pop back from, we may have
				// gotten that context AFTER having fallen off a rule.
				// Make sure we track that we are now out of context.
				c.reachesIntoOuterContext = config.reachesIntoOuterContext;
				closure(c, configs, decState, closureBusy, semanticContext, collectPredicates);
				return;
			}
			else {
				// else if we have no context info, just chase follow links (if greedy)
				if ( decState!=null && !decState.isGreedy ) {
					if ( debug ) System.out.println("nongreedy decision state = "+decState);
					if ( debug ) System.out.println("NONGREEDY at stop state of "+
													getRuleName(config.state.ruleIndex));
					// don't purse past end of a rule for any nongreedy decision
					configs.add(config);
					return;
				}
				if ( debug ) System.out.println("FALLING off rule "+
												getRuleName(config.state.ruleIndex));
			}
		}

		ATNState p = config.state;
		// optimization
		if ( !p.onlyHasEpsilonTransitions() ) configs.add(config);

        for (int i=0; i<p.getNumberOfTransitions(); i++) {
            Transition t = p.transition(i);
            boolean continueCollecting =
				!(t instanceof ActionTransition) && collectPredicates;
            ATNConfig c = getEpsilonTarget(config, t, continueCollecting);
			if ( c!=null ) {
				if ( config.state instanceof RuleStopState ) {
					// fell off end of rule.
					// track how far we dip into outer context.  Might
					// come in handy and we avoid evaluating context dependent
					// preds if this is > 0.
					c.reachesIntoOuterContext++;
				}
				closure(c, configs, decState, closureBusy, semanticContext, continueCollecting);
			}
		}
	}

	@NotNull
	private String getRuleName(int index) {
		if ( parser!=null && index>=0 ) return parser.getRuleNames()[index];
		return "<rule "+index+">";
	}

	@Nullable
	public ATNConfig getEpsilonTarget(@NotNull ATNConfig config, @NotNull Transition t, boolean collectPredicates) {
		if ( t instanceof RuleTransition ) {
			return ruleTransition(config, t);
		}
		else if ( t instanceof PredicateTransition ) {
			return predTransition(config, (PredicateTransition)t, collectPredicates);
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
	public ATNConfig predTransition(@NotNull ATNConfig config, @NotNull PredicateTransition pt,
									boolean collectPredicates)
	{
		if ( debug ) {
			System.out.println("PRED (collectPredicates="+collectPredicates+") "+
                    pt.ruleIndex+":"+pt.predIndex+
					", ctx dependent="+pt.isCtxDependent);
			if ( parser != null ) {
                System.out.println("context surrounding pred is "+
                                   parser.getRuleInvocationStack());
                System.out.println("config.context="+config.context.toString(parser));
            }
		}
		// We know the correct context in exactly one spot: in the original
		// rule that invokes the ATN simulation. We know we are in this rule
		// when the context stack is empty and we've not dipped into
		// the outer context.
		boolean inContext =
			config.context==RuleContext.EMPTY && config.reachesIntoOuterContext==0;
//		RuleContext ctx = null;
//		if ( inContext ) ctx = outerContext;

        ATNConfig c;
        if ( collectPredicates &&
			 (!pt.isCtxDependent || (pt.isCtxDependent&&inContext)) )
		{
            SemanticContext newSemCtx = SemanticContext.and(config.semanticContext, pt.getPredicate());
            c = new ATNConfig(config, pt.target, newSemCtx);
        }
		else {
			c = new ATNConfig(config, pt.target, SemanticContext.NONE);
		}

		if ( debug ) System.out.println("config from pred transition="+c);
        return c;

		// We see through the predicate if:
        //  0) we have no parser to eval preds
		//	1) we are ignoring them
		//	2) we aren't ignoring them and it is not context dependent and
		//	   pred is true
        //	3) we aren't ignoring them, it is context dependent, but
      	//     we know the context and pred is true
        //	4) we aren't ignoring them, it is context dependent, but we don't know context
//		ATNConfig c = null;
//		boolean seeThroughPred =
//            parser==null ||
//            !collectPredicates ||
//			(collectPredicates&&!pt.isCtxDependent&&parser.sempred(ctx, pt.ruleIndex, pt.predIndex))||
//            (collectPredicates&&pt.isCtxDependent&&inContext&&parser.sempred(ctx, pt.ruleIndex, pt.predIndex)||
//            (collectPredicates&&pt.isCtxDependent&&!inContext));
//		if ( seeThroughPred ) {
//			c = new ATNConfig(config, pt.target, pt.getSemanticContext());
//			c.traversedPredicate = true;
//		}
//		return c;
	}

	@NotNull
	public ATNConfig ruleTransition(@NotNull ATNConfig config, @NotNull Transition t) {
		if ( debug ) {
			System.out.println("CALL rule "+getRuleName(t.target.ruleIndex)+
							   ", ctx="+config.context);
		}
		ATNState p = config.state;
		RuleContext newContext =
			new RuleContext(config.context, p.stateNumber,  t.target.stateNumber);
		return new ATNConfig(config, t.target, newContext);
	}

	public void reportConflict(int startIndex, int stopIndex, @NotNull Set<Integer> alts, @NotNull OrderedHashSet<ATNConfig> configs) {
		if ( debug ) {
			System.out.println("reportConflict "+alts+":"+configs);
		}
		if ( parser!=null ) parser.reportConflict(startIndex, stopIndex, alts, configs);
	}

	public void reportContextSensitivity(int startIndex, int stopIndex,
										 @NotNull Set<Integer> alts,
										 @NotNull OrderedHashSet<ATNConfig> configs)
	{
		if ( parser!=null ) parser.reportContextSensitivity(startIndex, stopIndex, alts, configs);
	}

	/** If context sensitive parsing, we know it's ambiguity not conflict */
	public void reportAmbiguity(int startIndex, int stopIndex,
								@NotNull Set<Integer> ambigAlts,
								@NotNull OrderedHashSet<ATNConfig> configs)
	{
		if ( debug ) {
			System.out.println("reportAmbiguity "+
								   ambigAlts+":"+configs);
		}
		if ( parser!=null ) parser.reportAmbiguity(startIndex, stopIndex, ambigAlts, configs);
	}

	public void reportInsufficientPredicates(int startIndex, int stopIndex,
											 @NotNull Set<Integer> ambigAlts,
											 @NotNull SemanticContext[] altToPred,
											 @NotNull OrderedHashSet<ATNConfig> configs)
	{
		if ( debug ) {
			System.out.println("reportInsufficientPredicates "+
								   ambigAlts+":"+Arrays.toString(altToPred));
		}
		if ( parser!=null ) {
			parser.reportInsufficientPredicates(startIndex, stopIndex, ambigAlts,
												altToPred, configs);
		}
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
	public ATNConfig configWithAltAtStopState(@NotNull Collection<ATNConfig> configs, int alt) {
		for (ATNConfig c : configs) {
			if ( c.alt == alt ) {
				if ( c.state.getClass() == RuleStopState.class ) {
					return c;
				}
			}
		}
		return null;
	}

	@Nullable
	public Set<Integer> getAmbiguousAlts(@NotNull OrderedHashSet<ATNConfig> configs) {
//		System.err.println("check ambiguous "+configs);
		Set<Integer> ambigAlts = null;
		// First get a list of configurations for each state.
		// Most of the time, each state will have one associated configuration.
		MultiMap<Integer, ATNConfig> stateToConfigListMap =
			new MultiMap<Integer, ATNConfig>();
		for (ATNConfig c : configs) {
			stateToConfigListMap.map(c.state.stateNumber, c);
		}
		// potential conflicts are states with > 1 configuration and diff alts
		for (List<ATNConfig> configsPerAlt : stateToConfigListMap.values()) {
			ATNConfig goal = configsPerAlt.get(0);
			int size = configsPerAlt.size();
			for (int i=1; i< size; i++) {
				ATNConfig c = configsPerAlt.get(i);
				if ( c.alt!=goal.alt ) {
					//System.out.println("chk stack "+goal+", "+c);
					boolean sameCtx =
						(goal.context==null&&c.context==null) ||
						goal.context.equals(c.context) ||
						c.context.conflictsWith(goal.context);
					if ( sameCtx ) {
						if ( debug ) {
							System.out.println("we reach state "+c.state.stateNumber+
							   " in rule "+
							   (parser !=null ? getRuleName(c.state.ruleIndex) :"n/a")+
							   " alts "+goal.alt+","+c.alt+" from ctx "+goal.context.toString(parser)
							   +" and "+ c.context.toString(parser));
						}
						if ( ambigAlts==null ) ambigAlts = new HashSet<Integer>();
						ambigAlts.add(goal.alt);
						ambigAlts.add(c.alt);
					}
				}
			}
		}
		if ( ambigAlts!=null ) {
			//System.err.println("ambig upon "+input.toString(startIndex, input.index()));
		}
		return ambigAlts;
	}

    public SemanticContext[] getPredsForAmbigAlts(@NotNull DecisionState decState,
                                                  @NotNull Set<Integer> ambigAlts,
                                                  @NotNull OrderedHashSet<ATNConfig> configs)
    {
        // REACH=[1|1|[]|0:0, 1|2|[]|0:1]
        if ( debug ) System.out.println("getPredsForAmbigAlts decision "+decState.decision);
        int nalts = decState.getNumberOfTransitions();
        SemanticContext[] altToPred = new SemanticContext[nalts +1];
        for (int alt : ambigAlts) { altToPred[alt] = SemanticContext.NONE; }
		int nPredAlts = 0;
        for (ATNConfig c : configs) {
            if ( c.semanticContext!=SemanticContext.NONE && ambigAlts.contains(c.alt) ) {
                altToPred[c.alt] = SemanticContext.or(altToPred[c.alt], c.semanticContext);
                c.resolveWithPredicate = true;
				nPredAlts++;
            }
        }

		// Optimize away p||p and p&&p
		for (int i = 0; i < altToPred.length; i++) {
			if ( altToPred[i]!=null ) altToPred[i] = altToPred[i].optimize();
//			if ( altToPred[i] == SemanticContext.NONE ) altToPred[i] = null;
		}

		// nonambig alts are null in altToPred
        if ( nPredAlts==0 ) altToPred = null;
		if ( debug ) System.out.println("getPredsForAmbigAlts result "+Arrays.toString(altToPred));
        return altToPred;
    }

	public List<DFAState.PredPrediction> getPredicatePredictions(SemanticContext[] altToPred) {
		List<DFAState.PredPrediction> pairs = new ArrayList<DFAState.PredPrediction>();
		int firstUnpredicated = ATN.INVALID_ALT_NUMBER;
		for (int i = 1; i < altToPred.length; i++) {
			SemanticContext pred = altToPred[i];
			// find first on predicated alternative, if any.
			// Only ambiguous alternatives will have SemanticContext.NONE.
			// All other alternatives will have null predicates in altToPred
			if ( pred==SemanticContext.NONE && firstUnpredicated==ATN.INVALID_ALT_NUMBER ) {
				firstUnpredicated = i;
			}
			if ( pred!=null && pred!=SemanticContext.NONE ) {
				pairs.add(new DFAState.PredPrediction(pred, i));
			}
		}
		if ( pairs.size()==0 ) pairs = null;
		else if ( firstUnpredicated!=ATN.INVALID_ALT_NUMBER ) {
			// add default prediction if we found null predicate
			pairs.add(new DFAState.PredPrediction(null, firstUnpredicated));
		}
//		System.out.println(Arrays.toString(altToPred)+"->"+pairs);
		return pairs;
	}

	public boolean tooFewPredicates(SemanticContext[] altToPred) {
		int unpredicated = 0;
		for (int i = 1; i < altToPred.length; i++) {
			if ( altToPred[i]==SemanticContext.NONE ) unpredicated++;
		}
		return unpredicated > 1;
	}
	public static int getMinAlt(@NotNull Set<Integer> ambigAlts) {
		int min = Integer.MAX_VALUE;
		for (int alt : ambigAlts) {
			if ( alt < min ) min = alt;
		}
		return min;
	}

	public static void killAlts(@NotNull Set<Integer> alts, @NotNull OrderedHashSet<ATNConfig> configs) {
		int i = 0;
		while ( i<configs.size() ) {
			ATNConfig c = configs.get(i);
			if ( alts.contains(c.alt) ) {
				configs.remove(i);
			}
			else i++;
		}
	}

	protected DFAState addDFAEdge(@NotNull DFA dfa,
								  @NotNull OrderedHashSet<ATNConfig> p,
								  int t,
								  @NotNull OrderedHashSet<ATNConfig> q)
	{
		DFAState from = addDFAState(dfa, p);
		DFAState to = addDFAState(dfa, q);
        if ( debug ) System.out.println("EDGE "+from+" -> "+to+" upon "+getTokenName(t));
		addDFAEdge(from, t, to);
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
	@Nullable
	protected DFAState addDFAState(@NotNull DFA dfa, @NotNull OrderedHashSet<ATNConfig> configs) {
		DFAState proposed = new DFAState(configs);
		DFAState existing = dfa.states.get(proposed);
		if ( existing!=null ) return existing;

		DFAState newState = proposed;

		newState.stateNumber = dfa.states.size();
		newState.configs = new OrderedHashSet<ATNConfig>();
		newState.configs.addAll(configs);
		dfa.states.put(newState, newState);
        if ( debug ) System.out.println("adding new DFA state: "+newState);
		return newState;
	}

    public void makeAcceptState(@NotNull DFAState accept, int uniqueAlt) {
   		accept.isAcceptState = true;
   		accept.prediction = uniqueAlt;
   		accept.complete = true;
   	}

    public void makeAcceptState(@NotNull DFAState accept,
								List<DFAState.PredPrediction> predPredictions)
	{
   		accept.isAcceptState = true;
   		accept.complete = true;
		accept.prediction = ATN.INVALID_ALT_NUMBER;
		accept.predicates = predPredictions;
	}

	@NotNull
	public String getTokenName(int t) {
		if ( t==-1 ) return "EOF";
		if ( parser!=null && parser.getTokenNames()!=null ) {
			String[] tokensNames = parser.getTokenNames();
			if ( t>=tokensNames.length ) {
				System.err.println(t+" ttype out of range: "+Arrays.toString(tokensNames));
				System.err.println(((CommonTokenStream)parser.getInputStream()).getTokens());
			}
			else {
				return tokensNames[t]+"<"+t+">";
			}
		}
		return String.valueOf(t);
	}

	public String getLookaheadName(SymbolStream<Symbol> input) {
		return getTokenName(input.LA(1));
	}

	public void setContextSensitive(boolean ctxSensitive) {
		this.userWantsCtxSensitive = ctxSensitive;
	}

	public void dumpDeadEndConfigs(@NotNull NoViableAltException nvae) {
		System.err.println("dead end configs: ");
		for (ATNConfig c : nvae.deadEndConfigs) {
			Transition t = c.state.transition(0);
			String trans = "";
			if ( t instanceof AtomTransition) {
				AtomTransition at = (AtomTransition)t;
				trans = "Atom "+getTokenName(at.label);
			}
			else if ( t instanceof SetTransition ) {
				SetTransition st = (SetTransition)t;
				boolean not = st instanceof NotSetTransition;
				trans = (not?"~":"")+"Set "+st.set.toString();
			}
			System.err.println(c.toString(parser, true)+":"+trans);
		}
	}

	@NotNull
	public NoViableAltException noViableAlt(@NotNull SymbolStream<Symbol> input, @NotNull RuleContext outerContext,
								@NotNull OrderedHashSet<ATNConfig> configs, int startIndex)
	{
		if ( parser instanceof TreeParser) {
			Symbol startNode = null;
			if ( input instanceof BufferedASTNodeStream ) {
				startNode = input.get(startIndex);
			}
			return new NoViableTreeGrammarAltException(parser,
													  (ASTNodeStream<Symbol>)input,
													  startNode,
													  input.LT(1),
													  configs, outerContext);
		}
		else {
			return new NoViableAltException(parser, input,
										   (Token)input.get(startIndex),
										   (Token)input.LT(1),
										   input.LT(1),
										   configs, outerContext);
		}
	}
}
