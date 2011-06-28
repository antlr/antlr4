package org.antlr.v4.runtime.atn;

import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.tool.Rule;

import java.util.*;

/** */
// TODO: split into runtime / analysis time?
public class ATN {
	public static final int INVALID_ALT_NUMBER = -1;
	public static final int INVALID_DECISION_NUMBER = -1;

	public List<ATNState> states = new ArrayList<ATNState>();
	public List<ATNState> rules = new ArrayList<ATNState>(); // rule index to start state

	/** Each subrule/rule is a decision point and we must track them so we
	 *  can go back later and build DFA predictors for them.  This includes
	 *  all the rules, subrules, optional blocks, ()+, ()* etc...
	 */
	public List<DecisionState> decisionToATNState = new ArrayList<DecisionState>();

	public Map<Rule, RuleStartState> ruleToStartState = new LinkedHashMap<Rule, RuleStartState>();
	public Map<Rule, RuleStopState> ruleToStopState = new LinkedHashMap<Rule, RuleStopState>();
	public Map<String, TokensStartState> modeNameToStartState =
		new LinkedHashMap<String, TokensStartState>();

	// runtime
	public int grammarType; // ANTLRParser.LEXER, ...
	public List<TokensStartState> modeToStartState = new ArrayList<TokensStartState>();

	// runtime for lexer
	public List<Integer> ruleToTokenType = new ArrayList<Integer>();
	public List<Integer> ruleToActionIndex = new ArrayList<Integer>();

	public int maxTokenType;

	int stateNumber = 0;

	// TODO: for runtime all we need is states, decisionToATNState I think

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
		decisionToATNState.add(s);
		s.decision = decisionToATNState.size()-1;
		return s.decision;
	}

	public int getNumberOfDecisions() {
		return decisionToATNState.size();
	}
}
