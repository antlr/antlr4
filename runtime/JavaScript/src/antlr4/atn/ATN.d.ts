import {IntervalSet} from "../misc";
import {RuleContext} from "../context";
import {ATNState, DecisionState, RuleStartState, RuleStopState} from "../state";

export declare class ATN {

    static INVALID_ALT_NUMBER: number;

    states: ATNState[];
    decisionToState: DecisionState[];
    ruleToStartState: RuleStartState[];
    ruleToStopState: RuleStopState[];

    getExpectedTokens(stateNumber: number, ctx: RuleContext ): IntervalSet;
    nextTokens(atnState: ATNState, ctx?: RuleContext): IntervalSet;

}
