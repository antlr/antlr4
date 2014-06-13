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

function Dict() {
	return this;
}

function Set() {
	this.data = {};
	return this;
}

Set.prototype.add = function(value) {
	var key = "hash_" + value.hashString().hashCode();
	if(key in this.data) {
		for(var i=0;i<this.data.length; i++) {
			if(value.equals(this.data[i])) {
				return this.data[i];
			}
		}
		this.data[key].push(value);
		return value;
	} else {
		this.data[key] = [ value ];
		return value;
	}
};

exports.Dict = Dict;
exports.Set = Set;