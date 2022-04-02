import Hash from "./Hash.js";

export default class BitSet {
    constructor() {
        this.data = [];
    }

    add(value) {
        this.data[value] = true;
    }

    or(set) {
        const bits = this;
        Object.keys(set.data).map(function (alt) {
            bits.add(alt);
        });
    }

    remove(value) {
        delete this.data[value];
    }

    contains(value) {
        return this.data[value] === true;
    }

    values() {
        return Object.keys(this.data);
    }

    minValue() {
        return Math.min.apply(null, this.values());
    }

    hashCode() {
        const hash = new Hash();
        hash.update(this.values());
        return hash.finish();
    }

    equals(other) {
        if (!(other instanceof BitSet)) {
            return false;
        }
        return this.hashCode() === other.hashCode();
    }

    toString() {
        return "{" + this.values().join(", ") + "}";
    }

    get length(){
        return this.values().length;
    }
}
