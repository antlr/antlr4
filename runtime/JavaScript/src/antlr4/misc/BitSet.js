/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import HashCode from "./HashCode.js";
import equalArrays from "../utils/equalArrays.js";

export default class BitSet {

    constructor() {
        this.data = new Uint32Array(1);
    }

    set(index) {
        BitSet._checkIndex(index)
        this._resize(index);
        this.data[index >>> 5] |= 1 << index % 32;
    }

    get(index) {
        BitSet._checkIndex(index)
        const slot = index >>> 5;
        if (slot >= this.data.length) {
            return false;
        }
        return (this.data[slot] & 1 << index % 32) !== 0;
    }

    clear(index) {
        BitSet._checkIndex(index)
        const slot = index >>> 5;
        if (slot < this.data.length) {
            this.data[slot] &= ~(1 << index);
        }
    }

    or(set) {
        const minCount = Math.min(this.data.length, set.data.length);
        for (let k = 0; k < minCount; ++k) {
            this.data[k] |= set.data[k];
        }
        if (this.data.length < set.data.length) {
            this._resize((set.data.length << 5) - 1);
            const c = set.data.length;
            for (let k = minCount; k < c; ++k) {
                this.data[k] = set.data[k];
            }
        }
    }

    values() {
        const result = new Array(this.length);
        let pos = 0;
        const length = this.data.length;
        for (let k = 0; k < length; ++k) {
            let l = this.data[k];
            while (l !== 0) {
                const t = l & -l;
                result[pos++] = (k << 5) + BitSet._bitCount(t - 1);
                l ^= t;
            }
        }
        return result;
    }

    minValue() {
        for (let k = 0; k < this.data.length; ++k) {
            let l = this.data[k];
            if (l !== 0) {
                let result = 0;
                while ((l & 1) === 0) {
                    result++;
                    l >>= 1;
                }
                return result + (32 * k);
            }
        }
        return 0;
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

    get length() {
        return this.data.map(l => BitSet._bitCount(l)).reduce((s, v) => s + v, 0);
    }

    _resize(index) {
        const count = index + 32 >>> 5;
        if (count <= this.data.length) {
            return;
        }
        const data = new Uint32Array(count);
        data.set(this.data);
        data.fill(0, this.data.length);
        this.data = data;
    }

    static _checkIndex(index) {
        if (index < 0)
            throw new RangeError("index cannot be negative");
    }

    static _bitCount(l) {
        // see https://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetParallel
        let count = 0;
        l = l - ((l >> 1) & 0x55555555);
        l = (l & 0x33333333) + ((l >> 2) & 0x33333333);
        l = (l + (l >> 4)) & 0x0f0f0f0f;
        l = l + (l >> 8);
        l = l + (l >> 16);
        return count + l & 0x3f;
    }
}
