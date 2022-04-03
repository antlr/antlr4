import ATNState from "./ATNState.js";

export default class BasicState extends ATNState {
    constructor() {
        super();
        this.stateType = ATNState.BASIC;
    }
}
