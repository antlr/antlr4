import HashMap from "../misc/HashMap.js";

export default class DoubleDict {

    constructor(defaultMapCtor) {
        this.defaultMapCtor = defaultMapCtor || HashMap;
        this.cacheMap = new this.defaultMapCtor();
    }

    get(a, b) {
        const d = this.cacheMap.get(a) || null;
        return d === null ? null : (d.get(b) || null);
    }

    set(a, b, o) {
        let d = this.cacheMap.get(a) || null;
        if (d === null) {
            d = new this.defaultMapCtor();
            this.cacheMap.set(a, d);
        }
        d.set(b, o);
    }

}
