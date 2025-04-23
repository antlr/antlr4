import {ATNState, RuleStopState} from "./index.js";

export declare class RuleStartState extends ATNState {
    stopState: RuleStopState;
    isLeftRecursiveRule: boolean;
}
