/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

function valueToString(v) {
    return v === null ? "null" : v;
}

function arrayToString(a) {
    return Array.isArray(a) ? ("[" + a.map(valueToString).join(", ") + "]") : "null";
}

String.prototype.seed = String.prototype.seed || Math.round(Math.random() * Math.pow(2, 32));

String.prototype.hashCode = function () {
    const key = this.toString();
    let h1b, k1;

    const remainder = key.length & 3; // key.length % 4
    const bytes = key.length - remainder;
    let h1 = String.prototype.seed;
    const c1 = 0xcc9e2d51;
    const c2 = 0x1b873593;
    let i = 0;

    while (i < bytes) {
        k1 =
            ((key.charCodeAt(i) & 0xff)) |
            ((key.charCodeAt(++i) & 0xff) << 8) |
            ((key.charCodeAt(++i) & 0xff) << 16) |
            ((key.charCodeAt(++i) & 0xff) << 24);
        ++i;

        k1 = ((((k1 & 0xffff) * c1) + ((((k1 >>> 16) * c1) & 0xffff) << 16))) & 0xffffffff;
        k1 = (k1 << 15) | (k1 >>> 17);
        k1 = ((((k1 & 0xffff) * c2) + ((((k1 >>> 16) * c2) & 0xffff) << 16))) & 0xffffffff;

        h1 ^= k1;
        h1 = (h1 << 13) | (h1 >>> 19);
        h1b = ((((h1 & 0xffff) * 5) + ((((h1 >>> 16) * 5) & 0xffff) << 16))) & 0xffffffff;
        h1 = (((h1b & 0xffff) + 0x6b64) + ((((h1b >>> 16) + 0xe654) & 0xffff) << 16));
    }

    k1 = 0;

    switch (remainder) {
        case 3:
            k1 ^= (key.charCodeAt(i + 2) & 0xff) << 16;
        case 2:
            k1 ^= (key.charCodeAt(i + 1) & 0xff) << 8;
        case 1:
            k1 ^= (key.charCodeAt(i) & 0xff);

            k1 = (((k1 & 0xffff) * c1) + ((((k1 >>> 16) * c1) & 0xffff) << 16)) & 0xffffffff;
            k1 = (k1 << 15) | (k1 >>> 17);
            k1 = (((k1 & 0xffff) * c2) + ((((k1 >>> 16) * c2) & 0xffff) << 16)) & 0xffffffff;
            h1 ^= k1;
    }

    h1 ^= key.length;

    h1 ^= h1 >>> 16;
    h1 = (((h1 & 0xffff) * 0x85ebca6b) + ((((h1 >>> 16) * 0x85ebca6b) & 0xffff) << 16)) & 0xffffffff;
    h1 ^= h1 >>> 13;
    h1 = ((((h1 & 0xffff) * 0xc2b2ae35) + ((((h1 >>> 16) * 0xc2b2ae35) & 0xffff) << 16))) & 0xffffffff;
    h1 ^= h1 >>> 16;

    return h1 >>> 0;
};

function standardEqualsFunction(a, b) {
    return a ? a.equals(b) : a==b;
}

function standardHashCodeFunction(a) {
    return a ? a.hashCode() : -1;
}

class Set {
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


class BitSet {
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


class Map {
    constructor(hashFunction, equalsFunction) {
        this.data = {};
        this.hashFunction = hashFunction || standardHashCodeFunction;
        this.equalsFunction = equalsFunction || standardEqualsFunction;
    }

    put(key, value) {
        const hashKey = "hash_" + this.hashFunction(key);
        if (hashKey in this.data) {
            const entries = this.data[hashKey];
            for (let i = 0; i < entries.length; i++) {
                const entry = entries[i];
                if (this.equalsFunction(key, entry.key)) {
                    const oldValue = entry.value;
                    entry.value = value;
                    return oldValue;
                }
            }
            entries.push({key:key, value:value});
            return value;
        } else {
            this.data[hashKey] = [{key:key, value:value}];
            return value;
        }
    }

    containsKey(key) {
        const hashKey = "hash_" + this.hashFunction(key);
        if(hashKey in this.data) {
            const entries = this.data[hashKey];
            for (let i = 0; i < entries.length; i++) {
                const entry = entries[i];
                if (this.equalsFunction(key, entry.key))
                    return true;
            }
        }
        return false;
    }

    get(key) {
        const hashKey = "hash_" + this.hashFunction(key);
        if(hashKey in this.data) {
            const entries = this.data[hashKey];
            for (let i = 0; i < entries.length; i++) {
                const entry = entries[i];
                if (this.equalsFunction(key, entry.key))
                    return entry.value;
            }
        }
        return null;
    }

    entries() {
        let l = [];
        for (const key in this.data) {
            if (key.indexOf("hash_") === 0) {
                l = l.concat(this.data[key]);
            }
        }
        return l;
    }

    getKeys() {
        return this.entries().map(function(e) {
            return e.key;
        });
    }

    getValues() {
        return this.entries().map(function(e) {
                return e.value;
        });
    }

    toString() {
        const ss = this.entries().map(function(entry) {
            return '{' + entry.key + ':' + entry.value + '}';
        });
        return '[' + ss.join(", ") + ']';
    }

    get length(){
        let l = 0;
        for (const hashKey in this.data) {
            if (hashKey.indexOf("hash_") === 0) {
                l = l + this.data[hashKey].length;
            }
        }
        return l;
    }
}


class AltDict {
    constructor() {
        this.data = {};
    }

    get(key) {
        key = "k-" + key;
        if (key in this.data) {
            return this.data[key];
        } else {
            return null;
        }
    }

    put(key, value) {
        key = "k-" + key;
        this.data[key] = value;
    }

    values() {
        const data = this.data;
        const keys = Object.keys(this.data);
        return keys.map(function (key) {
            return data[key];
        });
    }
}


class DoubleDict {
    constructor(defaultMapCtor) {
        this.defaultMapCtor = defaultMapCtor || Map;
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
            this.cacheMap.put(a, d);
        }
        d.put(b, o);
    }
}

class Hash {
    constructor() {
        this.count = 0;
        this.hash = 0;
    }

    update() {
        for(let i=0;i<arguments.length;i++) {
            const value = arguments[i];
            if (value == null)
                continue;
            if(Array.isArray(value))
                this.update.apply(this, value);
            else {
                let k = 0;
                switch (typeof(value)) {
                    case 'undefined':
                    case 'function':
                        continue;
                    case 'number':
                    case 'boolean':
                        k = value;
                        break;
                    case 'string':
                        k = value.hashCode();
                        break;
                    default:
                        if(value.updateHashCode)
                            value.updateHashCode(this);
                        else
                            console.log("No updateHashCode for " + value.toString())
                        continue;
                }
                k = k * 0xCC9E2D51;
                k = (k << 15) | (k >>> (32 - 15));
                k = k * 0x1B873593;
                this.count = this.count + 1;
                let hash = this.hash ^ k;
                hash = (hash << 13) | (hash >>> (32 - 13));
                hash = hash * 5 + 0xE6546B64;
                this.hash = hash;
            }
        }
    }

    finish() {
        let hash = this.hash ^ (this.count * 4);
        hash = hash ^ (hash >>> 16);
        hash = hash * 0x85EBCA6B;
        hash = hash ^ (hash >>> 13);
        hash = hash * 0xC2B2AE35;
        hash = hash ^ (hash >>> 16);
        return hash;
    }
}

function hashStuff() {
    const hash = new Hash();
    hash.update.apply(hash, arguments);
    return hash.finish();
}


function escapeWhitespace(s, escapeSpaces) {
    s = s.replace(/\t/g, "\\t")
         .replace(/\n/g, "\\n")
         .replace(/\r/g, "\\r");
    if (escapeSpaces) {
        s = s.replace(/ /g, "\u00B7");
    }
    return s;
}

function titleCase(str) {
    return str.replace(/\w\S*/g, function (txt) {
        return txt.charAt(0).toUpperCase() + txt.substr(1);
    });
}

function equalArrays(a, b) {
    if (!Array.isArray(a) || !Array.isArray(b))
        return false;
    if (a === b)
        return true;
    if (a.length !== b.length)
        return false;
    for (let i = 0; i < a.length; i++) {
        if (a[i] === b[i])
            continue;
        if (!a[i].equals || !a[i].equals(b[i]))
            return false;
    }
    return true;
}

module.exports = {
    Hash,
    Set,
    Map,
    BitSet,
    AltDict,
    DoubleDict,
    hashStuff,
    escapeWhitespace,
    arrayToString,
    titleCase,
    equalArrays
}
