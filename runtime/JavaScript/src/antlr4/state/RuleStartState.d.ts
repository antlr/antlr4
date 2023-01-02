import ATNState from "./ATNState";
import RuleStopState from "./RuleStopState";

declare class RuleStartState extends ATNState {
    stopState: RuleStopState;
    isLeftRecursiveRule: boolean;
}

export default RuleStartState;
