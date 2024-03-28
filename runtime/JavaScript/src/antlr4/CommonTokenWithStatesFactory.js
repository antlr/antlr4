import CommonTokenFactory from "./CommonTokenFactory.js";
import CommonTokenWithStates from "./CommonTokenWithStates.js";

export default class CommonTokenWithStatesFactory extends CommonTokenFactory {
    constructor(copyText) {
        super(copyText);
        this.commonToken = CommonTokenWithStates;
    }

}

CommonTokenWithStatesFactory.DEFAULT = new CommonTokenWithStatesFactory();
