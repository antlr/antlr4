import ATNState from "./ATNState";

declare class DecisionState extends ATNState {
    decision: number;
    nonGreedy: boolean;
}

export default DecisionState;
