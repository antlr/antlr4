import BlockStartState from "./BlockStartState.js";
import ATNState from "./ATNState.js";

/**
 * The block that begins a closure loop
 */
export default class StarBlockStartState extends BlockStartState {
    constructor() {
        super();
        this.stateType = ATNState.STAR_BLOCK_START;
        return this;
    }
}
