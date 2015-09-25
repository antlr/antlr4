function arrayToString(a) {
	return "[" + a.join(", ") + "]";
}

String.prototype.hashCode = function(s) {
	var hash = 0;
	if (this.length === 0) {
		return hash;
	}
	for (var i = 0; i < this.length; i++) {
		var character = this.charCodeAt(i);
		hash = ((hash << 5) - hash) + character;
		hash = hash & hash; // Convert to 32bit integer
	}
	return hash;
};

function standardEqualsFunction(a,b) {
	return a.equals(b);
}

function standardHashFunction(a) {
	return a.hashString();
}

function Set(hashFunction, equalsFunction) {
	this.data = {};
	this.hashFunction = hashFunction || standardHashFunction;
	this.equalsFunction = equalsFunction || standardEqualsFunction;
	return this;
}

Object.defineProperty(Set.prototype, "length", {
	get : function() {
		return this.values().length;
	}
});

Set.prototype.add = function(value) {
	var hash = this.hashFunction(value);
	var key = "hash_" + hash.hashCode();
	if(key in this.data) {
		var i;
		var values = this.data[key];
		for(i=0;i<values.length; i++) {
			if(this.equalsFunction(value, values[i])) {
				return values[i];
			}
		}
		values.push(value);
		return value;
	} else {
		this.data[key] = [ value ];
		return value;
	}
};

Set.prototype.contains = function(value) {
	var hash = this.hashFunction(value);
	var key = hash.hashCode();
	if(key in this.data) {
		var i;
		var values = this.data[key];
		for(i=0;i<values.length; i++) {
			if(this.equalsFunction(value, values[i])) {
				return true;
			}
		}
	}
	return false;
};

Set.prototype.values = function() {
	var l = [];
	for(var key in this.data) {
		if(key.indexOf("hash_")===0) {
			l = l.concat(this.data[key]);
		}
	}
	return l;
};

Set.prototype.toString = function() {
	return arrayToString(this.values());
};

function BitSet() {
	this.data = [];
	return this;
}

BitSet.prototype.add = function(value) {
	this.data[value] = true;
};

BitSet.prototype.or = function(set) {
	var bits = this;
	Object.keys(set.data).map( function(alt) { bits.add(alt); });
};

BitSet.prototype.remove = function(value) {
	delete this.data[value];
};

BitSet.prototype.contains = function(value) {
	return this.data[value] === true;
};

BitSet.prototype.values = function() {
	return Object.keys(this.data);
};

BitSet.prototype.minValue = function() {
	return Math.min.apply(null, this.values());
};

BitSet.prototype.hashString = function() {
	return this.values().toString();
};

BitSet.prototype.equals = function(other) {
	if(!(other instanceof BitSet)) {
		return false;
	}
	return this.hashString()===other.hashString();
};

Object.defineProperty(BitSet.prototype, "length", {
	get : function() {
		return this.values().length;
	}
});

BitSet.prototype.toString = function() {
	return "{" + this.values().join(", ") + "}";
};

function AltDict() {
	this.data = {};
	return this;
}

AltDict.prototype.get = function(key) {
	key = "k-" + key;
	if(key in this.data){
		return this.data[key];
	} else {
		return null;
	}
};

AltDict.prototype.put = function(key, value) {
	key = "k-" + key;
	this.data[key] = value;
};

AltDict.prototype.values = function() {
	var data = this.data;
	var keys = Object.keys(this.data);
	return keys.map(function(key) {
		return data[key];
	});
};

function DoubleDict() {
	return this;
}

DoubleDict.prototype.get = function(a, b) {
	var d = this[a] || null;
	return d===null ? null : (d[b] || null);
};

DoubleDict.prototype.set = function(a, b, o) {
	var d = this[a] || null;
	if(d===null) {
		d = {};
		this[a] = d;
	}
	d[b] = o;
};


function escapeWhitespace(s, escapeSpaces) {
	s = s.replace("\t","\\t");
	s = s.replace("\n","\\n");
	s = s.replace("\r","\\r");
	if(escapeSpaces) {
		s = s.replace(" ","\u00B7");
	}
	return s;
}

exports.isArray = function (entity) {
	return Object.prototype.toString.call( entity ) === '[object Array]'
};

exports.titleCase = function(str) {
	return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1);});
};

exports.Set = Set;
exports.BitSet = BitSet;
exports.AltDict = AltDict;
exports.DoubleDict = DoubleDict;
exports.escapeWhitespace = escapeWhitespace;
exports.arrayToString = arrayToString;
