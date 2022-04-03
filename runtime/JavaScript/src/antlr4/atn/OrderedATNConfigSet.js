import ATNConfigSet from "./ATNConfigSet.js";
import HashSet from "../misc/HashSet.js";

export default class OrderedATNConfigSet extends ATNConfigSet {
    constructor() {
        super();
        this.configLookup = new HashSet();
    }
}
