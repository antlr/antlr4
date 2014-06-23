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

function Set() {
	this.data = {};
	return this;
}

Object.defineProperty(Set.prototype, "length", {
	get : function() {
		return this.values().length;
	}
});

Set.prototype.add = function(value) {
	var key = "hash_" + value.hashString().hashCode();
	if(key in this.data) {
		var values = this.data[key];
		for(var i=0;i<values.length; i++) {
			if(value.equals(values[i])) {
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
	var key = "hash_" + value.hashString().hashCode();
	if(key in this.data) {
		var values = this.data[key];
		for(var i=0;i<values.length; i++) {
			if(value.equals(values[i])) {
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

function BitSet() {
	this.data = [];
	return this;
}

Object.defineProperty(BitSet.prototype, "length", {
	get : function() {
		return this.data.length;
	}
});

BitSet.prototype.add = function(value) {
	this.data[value] = true;
};


function Dict() {
	this.data = {};
	return this;
}

Dict.prototype.get = function(key) {
	if(key in this.data){
		return this.data[key];
	} else {
		return null;
	}
};

Dict.prototype.put = function(key, value) {
	this.data[key] = value;
};

Dict.prototype.values = function() {
	var data = this.data;
	var keys = Object.keys(this.data);
	return keys.map(function(key) {
		return data[key];
	});
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

exports.Dict = Dict;
exports.Set = Set;
exports.BitSet = BitSet;
exports.AltDict = AltDict;
