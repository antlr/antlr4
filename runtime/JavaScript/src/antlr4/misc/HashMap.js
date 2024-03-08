/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import standardEqualsFunction from "../utils/standardEqualsFunction.js";
import standardHashCodeFunction from "../utils/standardHashCodeFunction.js";

const DEFAULT_LOAD_FACTOR = 0.75;
const INITIAL_CAPACITY = 16

export default class HashMap {

    constructor(hashFunction, equalsFunction) {
        this.buckets = new Array(INITIAL_CAPACITY);
        this.threshold = Math.floor(INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        this.itemCount = 0;
        this.hashFunction = hashFunction || standardHashCodeFunction;
        this.equalsFunction = equalsFunction || standardEqualsFunction;
    }

    set(key, value) {
        this._expand();
        const slot = this._getSlot(key);
        let bucket = this.buckets[slot];
        if (!bucket) {
            bucket = [[key, value]];
            this.buckets[slot] = bucket;
            this.itemCount++;
            return value;
        }
        const existing = bucket.find(pair => this.equalsFunction(pair[0], key), this);
        if(existing) {
            const result = existing[1];
            existing[1] = value;
            return result;
        } else {
            bucket.push([key, value]);
            this.itemCount++;
            return value;
        }
    }

    containsKey(key) {
        const bucket = this._getBucket(key);
        if(!bucket) {
            return false;
        }
        const existing = bucket.find(pair => this.equalsFunction(pair[0], key), this);
        return !!existing;
    }

    get(key) {
        const bucket = this._getBucket(key);
        if(!bucket) {
            return null;
        }
        const existing = bucket.find(pair => this.equalsFunction(pair[0], key), this);
        return existing ? existing[1] : null;
    }

    entries() {
        return this.buckets.filter(b => b != null).flat(1);
    }

    getKeys() {
        return this.entries().map(pair => pair[0]);
    }

    getValues() {
        return this.entries().map(pair => pair[1]);
    }

    toString() {
        const ss = this.entries().map(e => '{' + e[0] + ':' + e[1] + '}');
        return '[' + ss.join(", ") + ']';
    }

    get length() {
        return this.itemCount;
    }

    _getSlot(key) {
        const hash = this.hashFunction(key);
        return hash & this.buckets.length - 1;
    }
    _getBucket(key) {
        return this.buckets[this._getSlot(key)];
    }

    _expand() {
        if (this.itemCount <= this.threshold) {
            return;
        }
        const old_buckets = this.buckets;
        const newCapacity = this.buckets.length * 2;
        this.buckets = new Array(newCapacity);
        this.threshold = Math.floor(newCapacity * DEFAULT_LOAD_FACTOR);
        for (const bucket of old_buckets) {
            if (!bucket) {
                continue;
            }
            for (const pair of bucket) {
                const slot = this._getSlot(pair[0]);
                let newBucket = this.buckets[slot];
                if (!newBucket) {
                    newBucket = [];
                    this.buckets[slot] = newBucket;
                }
                newBucket.push(pair);
            }
        }
    }

}
