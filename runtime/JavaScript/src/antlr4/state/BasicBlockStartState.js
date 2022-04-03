import ATNState from "./ATNState.js";
import BlockStartState from "./BlockStartState.js";

export default class BasicBlockStartState extends BlockStartState {
    constructor() {
        super();
        this.stateType = ATNState.BLOCK_START;
        return this;
    }
}
