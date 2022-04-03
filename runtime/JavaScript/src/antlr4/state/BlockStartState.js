import DecisionState from "./DecisionState.js";

/**
 *  The start of a regular {@code (...)} block
 */
export default class BlockStartState extends DecisionState {
    constructor() {
        super();
        this.endState = null;
        return this;
    }
}
