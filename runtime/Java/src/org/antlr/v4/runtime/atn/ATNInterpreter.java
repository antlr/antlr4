package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.*;

import java.util.*;

public abstract class ATNInterpreter {
	/** Must distinguish between missing edge and edge we know leads nowhere */
	public static DFAState ERROR;
	public ATN atn;

	protected ATNConfig prevAccept; // TODO Move down? used to avoid passing int down and back up in method calls
	protected int prevAcceptIndex = -1;

	static {
		ERROR = new DFAState(new OrderedHashSet<ATNConfig>());
		ERROR.stateNumber = Integer.MAX_VALUE;
	}

	public ATNInterpreter(ATN atn) {
		this.atn = atn;
	}

	public static ATN deserialize(char[] data) {
		ATN atn = new ATN();
		List<IntervalSet> sets = new ArrayList<IntervalSet>();
		int p = 0;
		atn.grammarType = toInt(data[p++]);
		atn.maxTokenType = toInt(data[p++]);
		int nstates = toInt(data[p++]);
		for (int i=1; i<=nstates; i++) {
			int stype = toInt(data[p++]);
			if ( stype==0 ) continue; // ignore bad type of states
			ATNState s = stateFactory(stype, i);
			s.ruleIndex = toInt(data[p++]);
			atn.addState(s);
		}
		int nrules = toInt(data[p++]);
		if ( atn.grammarType == ATN.LEXER ) {
			atn.ruleToTokenType = new int[nrules];
			atn.ruleToActionIndex = new int[nrules];
		}
		atn.ruleToStartState = new RuleStartState[nrules];
		for (int i=0; i<nrules; i++) {
			int s = toInt(data[p++]);
			RuleStartState startState = (RuleStartState)atn.states.get(s);
			atn.ruleToStartState[i] = startState;
			if ( atn.grammarType == ATN.LEXER ) {
				int tokenType = toInt(data[p++]);
				atn.ruleToTokenType[i] = tokenType;
				int actionIndex = toInt(data[p++]);
				atn.ruleToActionIndex[i] = actionIndex;
			}
			else {
				p += 2;
			}
		}
		int nmodes = toInt(data[p++]);
		for (int i=0; i<nmodes; i++) {
			int s = toInt(data[p++]);
			atn.modeToStartState.add((TokensStartState)atn.states.get(s));
		}
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
		int nedges = toInt(data[p++]);
		for (int i=1; i<=nedges; i++) {
			int src = toInt(data[p]);
			int trg = toInt(data[p+1]);
			int ttype = toInt(data[p+2]);
			int arg1 = toInt(data[p+3]);
			int arg2 = toInt(data[p+4]);
			Transition trans = edgeFactory(atn, ttype, src, trg, arg1, arg2, sets);
			ATNState srcState = atn.states.get(src);
			srcState.addTransition(trans);
			p += 5;
		}
		int ndecisions = toInt(data[p++]);
		for (int i=1; i<=ndecisions; i++) {
			int s = toInt(data[p++]);
			DecisionState decState = (DecisionState)atn.states.get(s);
			atn.decisionToState.add((DecisionState) decState);
			decState.decision = i-1;
		}
//		System.out.println(atn.getDecoded());
		return atn;
	}

	public static int toInt(char c) {
		return c==65535 ? -1 : c;
	}

	public static Transition edgeFactory(ATN atn,
										 int type, int src, int trg,
										 int arg1, int arg2,
										 List<IntervalSet> sets)
	{
		ATNState target = atn.states.get(trg);
		switch (type) {
			case Transition.EPSILON : return new EpsilonTransition(target);
			case Transition.RANGE : return new RangeTransition(arg1, arg2, target);
			case Transition.RULE : return new RuleTransition(arg2, atn.states.get(arg1), target);
			case Transition.PREDICATE : return new PredicateTransition(target, arg1, arg2);
			case Transition.ATOM : return new AtomTransition(arg1, target);
			case Transition.ACTION : return new ActionTransition(target, arg1, arg2);
			case Transition.FORCED_ACTION : return new ActionTransition(target, arg1, arg2);
			case Transition.SET : return new SetTransition(null, sets.get(arg1), target);
			case Transition.NOT_ATOM : return new NotAtomTransition(arg1, target);
			case Transition.NOT_SET : return new NotSetTransition(null, sets.get(arg1), target);
			case Transition.WILDCARD : return new WildcardTransition(target);
		}
		return null;
	}

	public static ATNState stateFactory(int type, int stateNumber) {
		ATNState s = null;
		switch (type) {
			case ATNState.BASIC : s = new ATNState(); break;
			case ATNState.RULE_START : s = new RuleStartState(); break;
			case ATNState.BLOCK_START : s = new BlockStartState(); break;
			case ATNState.PLUS_BLOCK_START : s = new PlusBlockStartState(); break;
			case ATNState.STAR_BLOCK_START : s = new StarBlockStartState(); break;
			case ATNState.TOKEN_START : s = new TokensStartState(); break;
			case ATNState.RULE_STOP : s = new RuleStopState(); break;
			case ATNState.BLOCK_END : s = new BlockEndState(); break;
			case ATNState.STAR_LOOP_BACK : s = new StarLoopbackState(); break;
			case ATNState.PLUS_LOOP_BACK : s = new PlusLoopbackState(); break;
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
