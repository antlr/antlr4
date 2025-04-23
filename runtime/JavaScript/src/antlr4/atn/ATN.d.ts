import {IntervalSet} from "../misc/index.js";
import {RuleContext} from "../context/index.js";
import {ATNState, DecisionState, RuleStartState, RuleStopState} from "../state.js";

export declare class ATN {

    static INVALID_ALT_NUMBER: number;

    states: ATNState[];
    decisionToState: DecisionState[];
    ruleToStartState: RuleStartState[];
    ruleToStopState: RuleStopState[];

    getExpectedTokens(stateNumber: number, ctx: RuleContext ): IntervalSet;
    nextTokens(atnState: ATNState, ctx?: RuleContext): IntervalSet;

}
