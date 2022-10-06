import {ATNState} from "./index";
import RuleStopState from "./RuleStopState";

export default class RuleStartState extends ATNState {
    stopState: RuleStopState;
    isLeftRecursiveRule: boolean;
}
