import ATNState from "./ATNState.js";

export default class DecisionState extends ATNState {
    constructor() {
        super();
        this.decision = -1;
        this.nonGreedy = false;
        return this;
    }
}
