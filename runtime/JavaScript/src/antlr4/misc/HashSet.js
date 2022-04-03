import standardHashCodeFunction from "../utils/standardHashCodeFunction.js";
import standardEqualsFunction from "../utils/standardEqualsFunction.js";
import arrayToString from "../utils/arrayToString.js";

export default class HashSet {
    constructor(hashFunction, equalsFunction) {
        this.data = {};
        this.hashFunction = hashFunction || standardHashCodeFunction;
        this.equalsFunction = equalsFunction || standardEqualsFunction;
    }

    add(value) {
        const hash = this.hashFunction(value);
        const key = "hash_" + hash;
        if (key in this.data) {
            const values = this.data[key];
            for (let i = 0; i < values.length; i++) {
                if (this.equalsFunction(value, values[i])) {
                    return values[i];
                }
            }
            values.push(value);
            return value;
        } else {
            this.data[key] = [value];
            return value;
        }
    }

    contains(value) {
        return this.get(value) != null;
    }

    get(value) {
        const hash = this.hashFunction(value);
        const key = "hash_" + hash;
        if (key in this.data) {
            const values = this.data[key];
            for (let i = 0; i < values.length; i++) {
                if (this.equalsFunction(value, values[i])) {
                    return values[i];
                }
            }
        }
        return null;
    }

    values() {
        let l = [];
        for (const key in this.data) {
            if (key.indexOf("hash_") === 0) {
                l = l.concat(this.data[key]);
            }
        }
        return l;
    }

    toString() {
        return arrayToString(this.values());
    }

    get length(){
        let l = 0;
        for (const key in this.data) {
            if (key.indexOf("hash_") === 0) {
                l = l + this.data[key].length;
            }
        }
        return l;
    }
}
