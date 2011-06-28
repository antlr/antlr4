package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.*;

/** */
public class ATN {
	public static final int INVALID_ALT_NUMBER = 0;
//	public static final int INVALID_DECISION_NUMBER = -1;

	public static final int PARSER = 1;
	public static final int LEXER = 2;
	public static final int TREE_PARSER = 3;

	public List<ATNState> states = new ArrayList<ATNState>();

	/** Each subrule/rule is a decision point and we must track them so we
	 *  can go back later and build DFA predictors for them.  This includes
	 *  all the rules, subrules, optional blocks, ()+, ()* etc...
	 */
	public List<DecisionState> decisionToState = new ArrayList<DecisionState>();

	public RuleStartState[] ruleToStartState;
	public RuleStopState[] ruleToStopState;

	public Map<String, TokensStartState> modeNameToStartState =
		new LinkedHashMap<String, TokensStartState>();

	// runtime for parsers, lexers
	public int grammarType; // ATN.LEXER, ...
	public int maxTokenType;

	// runtime for lexer only
	public int[] ruleToTokenType;
	public int[] ruleToActionIndex;
	public List<TokensStartState> modeToStartState = new ArrayList<TokensStartState>();

	/** used during construction from grammar AST */
	int stateNumber = 0;

	/** Used for runtime deserialization of ATNs from strings */
	public ATN() { }

	public IntervalSet nextTokens(RuleContext ctx) {
		return nextTokens(ctx.s, ctx);
	}

	public IntervalSet nextTokens(int stateNumber, RuleContext ctx) {
		ATNState s = states.get(stateNumber);
		if ( s == null ) return null;
		LL1Analyzer anal = new LL1Analyzer(this);
		IntervalSet next = anal.LOOK(s, ctx);
		return next;
	}

	public void addState(ATNState state) {
		state.atn = this;
		states.add(state);
		state.stateNumber = stateNumber++;
	}

	public int defineDecisionState(DecisionState s) {
		decisionToState.add(s);
		s.decision = decisionToState.size()-1;
		return s.decision;
	}

	public int getNumberOfDecisions() {
		return decisionToState.size();
	}
}
