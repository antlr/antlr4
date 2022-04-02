import ATNConfigSet from "./ATNConfigSet.js";
import CustomizedSet from "../utils/CustomizedSet.js";

export default class OrderedATNConfigSet extends ATNConfigSet {
    constructor() {
        super();
        this.configLookup = new CustomizedSet();
    }
}
