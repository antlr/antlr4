import HashCode from "./HashCode.js";
import equalArrays from "../utils/equalArrays.js";

export default class BitSet {

    constructor() {
        this.data = [];
    }

    add(value) {
        this.data[value] = true;
    }

    or(set) {
        Object.keys(set.data).map(alt => this.add(alt), this);
    }

    remove(value) {
        delete this.data[value];
    }

    has(value) {
        return this.data[value] === true;
    }

    values() {
        return Object.keys(this.data);
    }

    minValue() {
        return Math.min.apply(null, this.values());
    }

    hashCode() {
        return HashCode.hashStuff(this.values());
    }

    equals(other) {
        return other instanceof BitSet && equalArrays(this.data, other.data);
    }

    toString() {
        return "{" + this.values().join(", ") + "}";
    }

    get length(){
        return this.values().length;
    }
}
