/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

function arrayToString(a) {
    return "[" + a.join(", ") + "]";
}

String.prototype.seed = String.prototype.seed || Math.round(Math.random() * Math.pow(2, 32));

String.prototype.hashCode = function () {
    var remainder, bytes, h1, h1b, c1, c1b, c2, c2b, k1, i,
        key = this.toString();

    remainder = key.length & 3; // key.length % 4
    bytes = key.length - remainder;
    h1 = String.prototype.seed;
    c1 = 0xcc9e2d51;
    c2 = 0x1b873593;
    i = 0;

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
    return a.equals(b);
}

function standardHashCodeFunction(a) {
    return a.hashCode();
}

function Set(hashFunction, equalsFunction) {
    this.data = {};
    this.hashFunction = hashFunction || standardHashCodeFunction;
    this.equalsFunction = equalsFunction || standardEqualsFunction;
    return this;
}

Object.defineProperty(Set.prototype, "length", {
    get: function () {
        var l = 0;
        for (var key in this.data) {
            if (key.indexOf("hash_") === 0) {
                l = l + this.data[key].length;
            }
        }
        return l;
    }
});

Set.prototype.add = function (value) {
    var hash = this.hashFunction(value);
    var key = "hash_" + hash;
    if (key in this.data) {
        var values = this.data[key];
        for (var i = 0; i < values.length; i++) {
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
};

Set.prototype.contains = function (value) {
    return this.get(value) != null;
};

Set.prototype.get = function (value) {
    var hash = this.hashFunction(value);
    var key = "hash_" + hash;
    if (key in this.data) {
        var values = this.data[key];
        for (var i = 0; i < values.length; i++) {
            if (this.equalsFunction(value, values[i])) {
                return values[i];
            }
        }
    }
    return null;
};

Set.prototype.values = function () {
    var l = [];
    for (var key in this.data) {
        if (key.indexOf("hash_") === 0) {
            l = l.concat(this.data[key]);
        }
    }
    return l;
};

Set.prototype.toString = function () {
    return arrayToString(this.values());
};

function BitSet() {
    this.data = [];
    return this;
}

BitSet.prototype.add = function (value) {
    this.data[value] = true;
};

BitSet.prototype.or = function (set) {
    var bits = this;
    Object.keys(set.data).map(function (alt) {
        bits.add(alt);
    });
};

BitSet.prototype.remove = function (value) {
    delete this.data[value];
};

BitSet.prototype.contains = function (value) {
    return this.data[value] === true;
};

BitSet.prototype.values = function () {
    return Object.keys(this.data);
};

BitSet.prototype.minValue = function () {
    return Math.min.apply(null, this.values());
};

BitSet.prototype.hashCode = function () {
    var hash = new Hash();
    hash.update(this.values());
    return hash.finish();
};

BitSet.prototype.equals = function (other) {
    if (!(other instanceof BitSet)) {
        return false;
    }
    return this.hashCode() === other.hashCode();
};

Object.defineProperty(BitSet.prototype, "length", {
    get: function () {
        return this.values().length;
    }
});

BitSet.prototype.toString = function () {
    return "{" + this.values().join(", ") + "}";
};

function Map(hashFunction, equalsFunction) {
    this.data = {};
    this.hashFunction = hashFunction || standardHashCodeFunction;
    this.equalsFunction = equalsFunction || standardEqualsFunction;
    return this;
}

Object.defineProperty(Map.prototype, "length", {
    get: function () {
        var l = 0;
        for (var hashKey in this.data) {
            if (hashKey.indexOf("hash_") === 0) {
                l = l + this.data[hashKey].length;
            }
        }
        return l;
    }
});

Map.prototype.put = function (key, value) {
    var hashKey = "hash_" + this.hashFunction(key);
    if (hashKey in this.data) {
        var entries = this.data[hashKey];
        for (var i = 0; i < entries.length; i++) {
            var entry = entries[i];
            if (this.equalsFunction(key, entry.key)) {
                var oldValue = entry.value;
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
};

Map.prototype.containsKey = function (key) {
    var hashKey = "hash_" + this.hashFunction(key);
    if(hashKey in this.data) {
        var entries = this.data[hashKey];
        for (var i = 0; i < entries.length; i++) {
            var entry = entries[i];
            if (this.equalsFunction(key, entry.key))
                return true;
        }
    }
    return false;
};

Map.prototype.get = function (key) {
    var hashKey = "hash_" + this.hashFunction(key);
    if(hashKey in this.data) {
        var entries = this.data[hashKey];
        for (var i = 0; i < entries.length; i++) {
            var entry = entries[i];
            if (this.equalsFunction(key, entry.key))
                return entry.value;
        }
    }
    return null;
};

Map.prototype.entries = function () {
    var l = [];
    for (var key in this.data) {
        if (key.indexOf("hash_") === 0) {
            l = l.concat(this.data[key]);
        }
    }
    return l;
};


Map.prototype.getKeys = function () {
    return this.entries().map(function(e) {
        return e.key;
    });
};


Map.prototype.getValues = function () {
    return this.entries().map(function(e) {
            return e.value;
    });
};


Map.prototype.toString = function () {
    var ss = this.entries().map(function(entry) {
        return '{' + entry.key + ':' + entry.value + '}';
    });
    return '[' + ss.join(", ") + ']';
};


function AltDict() {
    this.data = {};
    return this;
}


AltDict.prototype.get = function (key) {
    key = "k-" + key;
    if (key in this.data) {
        return this.data[key];
    } else {
        return null;
    }
};

AltDict.prototype.put = function (key, value) {
    key = "k-" + key;
    this.data[key] = value;
};

AltDict.prototype.values = function () {
    var data = this.data;
    var keys = Object.keys(this.data);
    return keys.map(function (key) {
        return data[key];
    });
};

function DoubleDict(defaultMapCtor) {
    this.defaultMapCtor = defaultMapCtor || Map;
    this.cacheMap = new this.defaultMapCtor();
    return this;
}

function Hash() {
    this.count = 0;
    this.hash = 0;
    return this;
}

Hash.prototype.update = function () {
    for(var i=0;i<arguments.length;i++) {
        var value = arguments[i];
        if (value == null)
            continue;
        if(Array.isArray(value))
            this.update.apply(this, value);
        else {
            var k = 0;
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
            var hash = this.hash ^ k;
            hash = (hash << 13) | (hash >>> (32 - 13));
            hash = hash * 5 + 0xE6546B64;
            this.hash = hash;
        }
    }
};

Hash.prototype.finish = function () {
    var hash = this.hash ^ (this.count * 4);
    hash = hash ^ (hash >>> 16);
    hash = hash * 0x85EBCA6B;
    hash = hash ^ (hash >>> 13);
    hash = hash * 0xC2B2AE35;
    hash = hash ^ (hash >>> 16);
    return hash;
};

function hashStuff() {
    var hash = new Hash();
    hash.update.apply(hash, arguments);
    return hash.finish();
}

DoubleDict.prototype.get = function (a, b) {
    var d = this.cacheMap.get(a) || null;
    return d === null ? null : (d.get(b) || null);
};

DoubleDict.prototype.set = function (a, b, o) {
    var d = this.cacheMap.get(a) || null;
    if (d === null) {
        d = new this.defaultMapCtor();
        this.cacheMap.put(a, d);
    }
    d.put(b, o);
};


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
};

function equalArrays(a, b)
{
    if (!Array.isArray(a) || !Array.isArray(b))
        return false;
    if (a == b)
        return true;
    if (a.length != b.length)
        return false;
    for (var i = 0; i < a.length; i++) {
        if (a[i] == b[i])
            continue;
        if (!a[i].equals(b[i]))
            return false;
    }
    return true;
};

exports.Hash = Hash;
exports.Set = Set;
exports.Map = Map;
exports.BitSet = BitSet;
exports.AltDict = AltDict;
exports.DoubleDict = DoubleDict;
exports.hashStuff = hashStuff;
exports.escapeWhitespace = escapeWhitespace;
exports.arrayToString = arrayToString;
exports.titleCase = titleCase;
exports.equalArrays = equalArrays;
