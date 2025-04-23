import {ATNState} from "./index.js";

export declare class DecisionState extends ATNState {
    decision: number;
    nonGreedy: boolean;
}
