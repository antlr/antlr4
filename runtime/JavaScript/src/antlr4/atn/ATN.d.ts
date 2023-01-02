import IntervalSet from "../misc/IntervalSet";
import RuleContext from "../context/RuleContext";
import ATNState from "../state/ATNState";
import DecisionState from "../state/DecisionState";
import RuleStartState from "../state/RuleStartState";
import RuleStopState from "../state/RuleStopState";

declare class ATN {

    static INVALID_ALT_NUMBER: number;

    states: ATNState[];
    decisionToState: DecisionState[];
    ruleToStartState: RuleStartState[];
    ruleToStopState: RuleStopState[];

    getExpectedTokens(stateNumber: number, ctx: RuleContext ): IntervalSet;
    nextTokens(atnState: ATNState, ctx?: RuleContext): IntervalSet;

}

export default ATN;
