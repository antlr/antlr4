func arrayToString(a) {
	return "[" + a.join(", ") + "]"
}

func (this *String) hashCode(s) {
	var hash = 0
	if (this.length == 0) {
		return hash
	}
	for (var i = 0 i < this.length i++) {
		var character = this.charCodeAt(i)
		hash = ((hash << 5) - hash) + character
		hash = hash & hash // Convert to 32bit integer
	}
	return hash
}

func standardEqualsFunction(a,b) {
	return a.equals(b)
}

func standardHashFunction(a) {
	return a.hashString()
}

func Set(hashFunction, equalsFunction) {
	this.data = {}
	this.hashfunc = hashfunc || standardHashFunction
	this.equalsfunc = equalsfunc || standardEqualsFunction
	return this
}

Object.defineProperty(Set.prototype, "length", {
	get : function() {
		return this.values().length
	}
})

func (this *Set) add(value) {
	var hash = this.hashFunction(value)
	var key = "hash_" + hash.hashCode()
	if(key in this.data) {
		var i
		var values = this.data[key]
		for(i=0i<values.length i++) {
			if(this.equalsFunction(value, values[i])) {
				return values[i]
			}
		}
		values.push(value)
		return value
	} else {
		this.data[key] = [ value ]
		return value
	}
}

func (this *Set) contains(value) {
	var hash = this.hashFunction(value)
	var key = hash.hashCode()
	if(key in this.data) {
		var i
		var values = this.data[key]
		for(i=0i<values.length i++) {
			if(this.equalsFunction(value, values[i])) {
				return true
			}
		}
	}
	return false
}

func (this *Set) values() {
	var l = []
	for(var key in this.data) {
		if(key.indexOf("hash_")==0) {
			l = l.concat(this.data[key])
		}
	}
	return l
}

func (this *Set) toString() {
	return arrayToString(this.values())
}

type BitSet struct {
	this.data = []
	return this
}

func (this *BitSet) add(value) {
	this.data[value] = true
}

func (this *BitSet) or(set) {
	var bits = this
	Object.keys(set.data).map( function(alt) { bits.add(alt) })
}

func (this *BitSet) remove(value) {
	delete this.data[value]
}

func (this *BitSet) contains(value) {
	return this.data[value] == true
}

func (this *BitSet) values() {
	return Object.keys(this.data)
}

func (this *BitSet) minValue() {
	return Math.min.apply(nil, this.values())
}

func (this *BitSet) hashString() {
	return this.values().toString()
}

func (this *BitSet) equals(other) {
	if(!(other instanceof BitSet)) {
		return false
	}
	return this.hashString()==other.hashString()
}

Object.defineProperty(BitSet.prototype, "length", {
	get : function() {
		return this.values().length
	}
})

func (this *BitSet) toString() {
	return "{" + this.values().join(", ") + "}"
}

type AltDict struct {
	this.data = {}
	return this
}

func (this *AltDict) get(key) {
	key = "k-" + key
	if(key in this.data){
		return this.data[key]
	} else {
		return nil
	}
}

func (this *AltDict) put(key, value) {
	key = "k-" + key
	this.data[key] = value
}

func (this *AltDict) values() {
	var data = this.data
	var keys = Object.keys(this.data)
	return keys.map(function(key) {
		return data[key]
	})
}

type DoubleDict struct {
	return this
}

func (this *DoubleDict) get(a, b) {
	var d = this[a] || nil
	return d==nil ? nil : (d[b] || nil)
}

func (this *DoubleDict) set(a, b, o) {
	var d = this[a] || nil
	if(d==nil) {
		d = {}
		this[a] = d
	}
	d[b] = o
}


func escapeWhitespace(s, escapeSpaces) {
	s = s.replace("\t","\\t")
	s = s.replace("\n","\\n")
	s = s.replace("\r","\\r")
	if(escapeSpaces) {
		s = s.replace(" ","\u00B7")
	}
	return s
}

exports.isArray = func (entity) {
	return Object.prototype.toString.call( entity ) == '[object Array]'
}

exports.titleCase = function(str) {
	return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1)})
}







