import {ATNState, RuleStopState} from "./index";

export declare class RuleStartState extends ATNState {
    stopState: RuleStopState;
    isLeftRecursiveRule: boolean;
}
