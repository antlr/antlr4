import ATNState from "./ATNState.js";

/**
 * Mark the end of a * or + loop
 */
export default class LoopEndState extends ATNState {
    constructor() {
        super();
        this.stateType = ATNState.LOOP_END;
        this.loopBackState = null;
        return this;
    }
}
