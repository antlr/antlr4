import ATNState from "./ATNState.js";

/**
 * Terminal node of a simple {@code (a|b|c)} block
 */
export default class BlockEndState extends ATNState {
    constructor() {
        super();
        this.stateType = ATNState.BLOCK_END;
        this.startState = null;
        return this;
    }
}
