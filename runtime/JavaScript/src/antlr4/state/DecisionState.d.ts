import {ATNState} from "./index";

export default class DecisionState extends ATNState {
    decision: number;
    nonGreedy: boolean;
}
