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

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public abstract class ATNSimulator {
	public static final int SERIALIZED_NON_GREEDY_MASK = 0x8000;
	public static final int SERIALIZED_STATE_TYPE_MASK = 0x7FFF;

	public static final char RULE_VARIANT_DELIMITER = '$';
	public static final String RULE_LF_VARIANT_MARKER =  "$lf$";

	/** Must distinguish between missing edge and edge we know leads nowhere */
	@NotNull
	public static final DFAState ERROR;
	@NotNull
	public final ATN atn;

	/** The context cache maps all PredictionContext objects that are equals()
	 *  to a single cached copy. This cache is shared across all contexts
	 *  in all ATNConfigs in all DFA states.  We rebuild each ATNConfigSet
	 *  to use only cached nodes/graphs in addDFAState(). We don't want to
	 *  fill this during closure() since there are lots of contexts that
	 *  pop up but are not used ever again. It also greatly slows down closure().
	 *
	 *  This cache makes a huge difference in memory and a little bit in speed.
	 *  For the Java grammar on java.*, it dropped the memory requirements
	 *  at the end from 25M to 16M. We don't store any of the full context
	 *  graphs in the DFA because they are limited to local context only,
	 *  but apparently there's a lot of repetition there as well. We optimize
	 *  the config contexts before storing the config set in the DFA states
	 *  by literally rebuilding them with cached subgraphs only.
	 *
	 *  I tried a cache for use during closure operations, that was
	 *  whacked after each adaptivePredict(). It cost a little bit
	 *  more time I think and doesn't save on the overall footprint
	 *  so it's not worth the complexity.
 	 */
	protected final PredictionContextCache sharedContextCache;

	static {
		ERROR = new DFAState(new ATNConfigSet());
		ERROR.stateNumber = Integer.MAX_VALUE;
	}

	public ATNSimulator(@NotNull ATN atn,
						@NotNull PredictionContextCache sharedContextCache)
	{
		this.atn = atn;
		this.sharedContextCache = sharedContextCache;
	}

	public abstract void reset();

	public PredictionContext getCachedContext(PredictionContext context) {
		if ( sharedContextCache==null ) return context;

		IdentityHashMap<PredictionContext, PredictionContext> visited =
			new IdentityHashMap<PredictionContext, PredictionContext>();
		return PredictionContext.getCachedContext(context,
												  sharedContextCache,
												  visited);
	}

	public static ATN deserialize(@NotNull char[] data) {
		ATN atn = new ATN();
		List<IntervalSet> sets = new ArrayList<IntervalSet>();
		int p = 0;
		atn.grammarType = toInt(data[p++]);
		atn.maxTokenType = toInt(data[p++]);

		//
		// STATES
		//
		List<Pair<LoopEndState, Integer>> loopBackStateNumbers = new ArrayList<Pair<LoopEndState, Integer>>();
		List<Pair<BlockStartState, Integer>> endStateNumbers = new ArrayList<Pair<BlockStartState, Integer>>();
		int nstates = toInt(data[p++]);
		for (int i=1; i<=nstates; i++) {
			int stype = toInt(data[p++]);
			// ignore bad type of states
			if ( stype==ATNState.INVALID_TYPE ) {
				atn.addState(null);
				continue;
			}

			boolean nonGreedy = (stype & SERIALIZED_NON_GREEDY_MASK) != 0;
			stype &= SERIALIZED_STATE_TYPE_MASK;
			ATNState s = stateFactory(stype, i);
			if (s instanceof DecisionState) {
				((DecisionState)s).nonGreedy = nonGreedy;
			}
			s.ruleIndex = toInt(data[p++]);
			if ( stype == ATNState.LOOP_END ) { // special case
				int loopBackStateNumber = toInt(data[p++]);
				loopBackStateNumbers.add(new Pair<LoopEndState, Integer>((LoopEndState)s, loopBackStateNumber));
			}
			else if (s instanceof BlockStartState) {
				int endStateNumber = toInt(data[p++]);
				endStateNumbers.add(new Pair<BlockStartState, Integer>((BlockStartState)s, endStateNumber));
			}
			atn.addState(s);
		}

		// delay the assignment of loop back and end states until we know all the state instances have been initialized
		for (Pair<LoopEndState, Integer> pair : loopBackStateNumbers) {
			pair.a.loopBackState = atn.states.get(pair.b);
		}

		for (Pair<BlockStartState, Integer> pair : endStateNumbers) {
			pair.a.endState = (BlockEndState)atn.states.get(pair.b);
		}

		//
		// RULES
		//
		int nrules = toInt(data[p++]);
		if ( atn.grammarType == ATN.LEXER ) {
			atn.ruleToTokenType = new int[nrules];
			atn.ruleToActionIndex = new int[nrules];
		}
		atn.ruleToStartState = new RuleStartState[nrules];
		for (int i=0; i<nrules; i++) {
			int s = toInt(data[p++]);
			RuleStartState startState = (RuleStartState)atn.states.get(s);
			startState.leftFactored = toInt(data[p++]) != 0;
			atn.ruleToStartState[i] = startState;
			if ( atn.grammarType == ATN.LEXER ) {
				int tokenType = toInt(data[p++]);
				atn.ruleToTokenType[i] = tokenType;
				int actionIndex = toInt(data[p++]);
				atn.ruleToActionIndex[i] = actionIndex;
			}
		}

		atn.ruleToStopState = new RuleStopState[nrules];
		for (ATNState state : atn.states) {
			if (!(state instanceof RuleStopState)) {
				continue;
			}

			RuleStopState stopState = (RuleStopState)state;
			atn.ruleToStopState[state.ruleIndex] = stopState;
			atn.ruleToStartState[state.ruleIndex].stopState = stopState;
		}

		//
		// MODES
		//
		int nmodes = toInt(data[p++]);
		for (int i=0; i<nmodes; i++) {
			int s = toInt(data[p++]);
			atn.modeToStartState.add((TokensStartState)atn.states.get(s));
		}

		//
		// SETS
		//
		int nsets = toInt(data[p++]);
		for (int i=1; i<=nsets; i++) {
			int nintervals = toInt(data[p]);
			p++;
			IntervalSet set = new IntervalSet();
			sets.add(set);
			for (int j=1; j<=nintervals; j++) {
				set.add(toInt(data[p]), toInt(data[p + 1]));
				p += 2;
			}
		}

		//
		// EDGES
		//
		int nedges = toInt(data[p++]);
		for (int i=1; i<=nedges; i++) {
			int src = toInt(data[p]);
			int trg = toInt(data[p+1]);
			int ttype = toInt(data[p+2]);
			int arg1 = toInt(data[p+3]);
			int arg2 = toInt(data[p+4]);
			int arg3 = toInt(data[p+5]);
			Transition trans = edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
//			System.out.println("EDGE "+trans.getClass().getSimpleName()+" "+
//							   src+"->"+trg+
//					   " "+Transition.serializationNames[ttype]+
//					   " "+arg1+","+arg2+","+arg3);
			ATNState srcState = atn.states.get(src);
			srcState.addTransition(trans);
			p += 6;
		}

		// edges for rule stop states can be derived, so they aren't serialized
		for (ATNState state : atn.states) {
			boolean returningToLeftFactored = state.ruleIndex >= 0 && atn.ruleToStartState[state.ruleIndex].leftFactored;
			for (int i = 0; i < state.getNumberOfTransitions(); i++) {
				Transition t = state.transition(i);
				if (!(t instanceof RuleTransition)) {
					continue;
				}

				RuleTransition ruleTransition = (RuleTransition)t;
				boolean returningFromLeftFactored = atn.ruleToStartState[ruleTransition.target.ruleIndex].leftFactored;
				if (!returningFromLeftFactored && returningToLeftFactored) {
					continue;
				}

				atn.ruleToStopState[ruleTransition.target.ruleIndex].addTransition(new EpsilonTransition(ruleTransition.followState));
			}
		}

		for (ATNState state : atn.states) {
			if (state instanceof BlockStartState) {
				// we need to know the end state to set its start state
				if (((BlockStartState)state).endState == null) {
					throw new IllegalStateException();
				}

				// block end states can only be associated to a single block start state
				if (((BlockStartState)state).endState.startState != null) {
					throw new IllegalStateException();
				}

				((BlockStartState)state).endState.startState = (BlockStartState)state;
			}

			if (state instanceof PlusLoopbackState) {
				PlusLoopbackState loopbackState = (PlusLoopbackState)state;
				for (int i = 0; i < loopbackState.getNumberOfTransitions(); i++) {
					ATNState target = loopbackState.transition(i).target;
					if (target instanceof PlusBlockStartState) {
						((PlusBlockStartState)target).loopBackState = loopbackState;
					}
				}
			}
			else if (state instanceof StarLoopbackState) {
				StarLoopbackState loopbackState = (StarLoopbackState)state;
				for (int i = 0; i < loopbackState.getNumberOfTransitions(); i++) {
					ATNState target = loopbackState.transition(i).target;
					if (target instanceof StarLoopEntryState) {
						((StarLoopEntryState)target).loopBackState = loopbackState;
					}
				}
			}
		}

		//
		// DECISIONS
		//
		int ndecisions = toInt(data[p++]);
		for (int i=1; i<=ndecisions; i++) {
			int s = toInt(data[p++]);
			DecisionState decState = (DecisionState)atn.states.get(s);
			atn.decisionToState.add(decState);
			decState.decision = i-1;
		}

		verifyATN(atn);
		return atn;
	}

	private static void verifyATN(ATN atn) {
		// verify assumptions
		for (ATNState state : atn.states) {
			if (state == null) {
				continue;
			}

			if (state instanceof PlusBlockStartState) {
				if (((PlusBlockStartState)state).loopBackState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof StarLoopEntryState) {
				if (((StarLoopEntryState)state).loopBackState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof LoopEndState) {
				if (((LoopEndState)state).loopBackState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof RuleStartState) {
				if (((RuleStartState)state).stopState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof BlockStartState) {
				if (((BlockStartState)state).endState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof BlockEndState) {
				if (((BlockEndState)state).startState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof DecisionState) {
				DecisionState decisionState = (DecisionState)state;
				if (decisionState.getNumberOfTransitions() > 1 && decisionState.decision < 0) {
					throw new IllegalStateException();
				}
			}
		}
	}

	public static int toInt(char c) {
		return c==65535 ? -1 : c;
	}

	public static Transition edgeFactory(@NotNull ATN atn,
										 int type, int src, int trg,
										 int arg1, int arg2, int arg3,
										 List<IntervalSet> sets)
	{
		ATNState target = atn.states.get(trg);
		switch (type) {
			case Transition.EPSILON : return new EpsilonTransition(target);
			case Transition.RANGE : return new RangeTransition(target, arg1, arg2);
			case Transition.RULE :
				RuleTransition rt = new RuleTransition((RuleStartState)atn.states.get(arg1), arg2, target);
				return rt;
			case Transition.PREDICATE :
				PredicateTransition pt = new PredicateTransition(target, arg1, arg2, arg3 != 0);
				return pt;
			case Transition.ATOM : return new AtomTransition(target, arg1);
			case Transition.ACTION :
				ActionTransition a = new ActionTransition(target, arg1, arg2, arg3 != 0);
				return a;
			case Transition.SET : return new SetTransition(target, sets.get(arg1));
			case Transition.NOT_SET : return new NotSetTransition(target, sets.get(arg1));
			case Transition.WILDCARD : return new WildcardTransition(target);
		}
		return null;
	}

	public static ATNState stateFactory(int type, int stateNumber) {
		ATNState s = null;
		switch (type) {
			case ATNState.INVALID_TYPE: return null;
			case ATNState.BASIC : s = new ATNState(); break;
			case ATNState.RULE_START : s = new RuleStartState(); break;
			case ATNState.BLOCK_START : s = new BlockStartState(); break;
			case ATNState.PLUS_BLOCK_START : s = new PlusBlockStartState(); break;
			case ATNState.STAR_BLOCK_START : s = new StarBlockStartState(); break;
			case ATNState.TOKEN_START : s = new TokensStartState(); break;
			case ATNState.RULE_STOP : s = new RuleStopState(); break;
			case ATNState.BLOCK_END : s = new BlockEndState(); break;
			case ATNState.STAR_LOOP_BACK : s = new StarLoopbackState(); break;
			case ATNState.STAR_LOOP_ENTRY : s = new StarLoopEntryState(); break;
			case ATNState.PLUS_LOOP_BACK : s = new PlusLoopbackState(); break;
			case ATNState.LOOP_END : s = new LoopEndState(); break;
            default :
                System.err.println("invalid state type in ATN deserialization: "+type+" for state "+stateNumber);
                break;
		}
		s.stateNumber = stateNumber;
		return s;
	}

/*
	public static void dump(DFA dfa, Grammar g) {
		DOTGenerator dot = new DOTGenerator(g);
		String output = dot.getDOT(dfa, false);
		System.out.println(output);
	}

	public static void dump(DFA dfa) {
		dump(dfa, null);
	}
	 */
}
