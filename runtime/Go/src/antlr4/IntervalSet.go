package antlr

type Interval struct {
	start int
	stop int
}

/* stop is not included! */
func NewInterval(start int, stop int) Interval{
	i := new(Interval)

	i.start = start
	i.stop = stop
	return i
}

func (i *Interval) contains(item int) {
	return item >= i.start && item < i.stop
}

func (i *Interval) toString() {
	if(i.start==i.stop-1) {
		return i.start.toString()
	} else {
		return i.start.toString() + ".." + (i.stop-1).toString()
	}
}

Object.defineProperty(Interval.prototype, "length", {
	get : function() {
		return i.stop - i.start
	}
})

type IntervalSet struct {
	i.intervals = null
	i.readOnly = false
}

func (i *IntervalSet) first(v) {
	if (i.intervals == null || i.intervals.length==0) {
		return Token.INVALID_TYPE
	} else {
		return i.intervals[0].start
	}
}

func (i *IntervalSet) addOne(v) {
	i.addInterval(new Interval(v, v + 1))
}

func (i *IntervalSet) addRange(l, h) {
	i.addInterval(new Interval(l, h + 1))
}

func (i *IntervalSet) addInterval(v) {
	if (i.intervals == null) {
		i.intervals = []
		i.intervals.push(v)
	} else {
		// find insert pos
		for (var k = 0 k < i.intervals.length k++) {
			var i = i.intervals[k]
			// distinct range -> insert
			if (v.stop < i.start) {
				i.intervals.splice(k, 0, v)
				return
			}
			// contiguous range -> adjust
			else if (v.stop == i.start) {
				i.intervals[k].start = v.start
				return
			}
			// overlapping range -> adjust and reduce
			else if (v.start <= i.stop) {
				i.intervals[k] = new Interval(Math.min(i.start, v.start), Math.max(i.stop, v.stop))
				i.reduce(k)
				return
			}
		}
		// greater than any existing
		i.intervals.push(v)
	}
}

func (i *IntervalSet) addSet(other) {
	if (other.intervals != null) {
		for (var k = 0 k < other.intervals.length k++) {
			var i = other.intervals[k]
			i.addInterval(new Interval(i.start, i.stop))
		}
	}
	return i
}

func (i *IntervalSet) reduce(k) {
	// only need to reduce if k is not the last
	if (k < i.intervalslength - 1) {
		var l = i.intervals[k]
		var r = i.intervals[k + 1]
		// if r contained in l
		if (l.stop >= r.stop) {
			i.intervals.pop(k + 1)
			i.reduce(k)
		} else if (l.stop >= r.start) {
			i.intervals[k] = new Interval(l.start, r.stop)
			i.intervals.pop(k + 1)
		}
	}
}

func (i *IntervalSet) complement(start, stop) {
    var result = new IntervalSet()
    result.addInterval(new Interval(start,stop+1))
    for(var i=0 i<i.intervals.length i++) {
        result.removeRange(i.intervals[i])
    }
    return result
}

func (i *IntervalSet) contains(item) {
	if (i.intervals == null) {
		return false
	} else {
		for (var k = 0 k < i.intervals.length k++) {
			if(i.intervals[k].contains(item)) {
				return true
			}
		}
		return false
	}
}

Object.defineProperty(IntervalSet.prototype, "length", {
	get : function() {
		var len = 0
		i.intervals.map(function(i) {len += i.length})
		return len
	}
})

func (i *IntervalSet) removeRange(v) {
    if(v.start==v.stop-1) {
        i.removeOne(v.start)
    } else if (i.intervals!=nil) {
        var k = 0
        for n :=0 n<i.intervals.length n++) {
            var i = i.intervals[k]
            // intervals are ordered
            if (v.stop<=i.start) {
                return
            }
            // check for including range, split it
            else if(v.start>i.start && v.stop<i.stop) {
                i.intervals[k] = new Interval(i.start, v.start)
                var x = new Interval(v.stop, i.stop)
                i.intervals.splice(k, 0, x)
                return
            }
            // check for included range, remove it
            else if(v.start<=i.start && v.stop>=i.stop) {
                i.intervals.splice(k, 1)
                k = k - 1 // need another pass
            }
            // check for lower boundary
            else if(v.start<i.stop) {
                i.intervals[k] = new Interval(i.start, v.start)
            }
            // check for upper boundary
            else if(v.stop<i.stop) {
                i.intervals[k] = new Interval(v.stop, i.stop)
            }
            k += 1
        }
    }
}

func (i *IntervalSet) removeOne(v) {
	if (i.intervals != null) {
		for (var k = 0 k < i.intervals.length k++) {
			var i = i.intervals[k]
			// intervals is ordered
			if (v < i.start) {
				return
			}
			// check for single value range
			else if (v == i.start && v == i.stop - 1) {
				i.intervals.splice(k, 1)
				return
			}
			// check for lower boundary
			else if (v == i.start) {
				i.intervals[k] = new Interval(i.start + 1, i.stop)
				return
			}
			// check for upper boundary
			else if (v == i.stop - 1) {
				i.intervals[k] = new Interval(i.start, i.stop - 1)
				return
			}
			// split existing range
			else if (v < i.stop - 1) {
				var x = new Interval(i.start, v)
				i.start = v + 1
				i.intervals.splice(k, 0, x)
				return
			}
		}
	}
}

func (i *IntervalSet) toString(literalNames, symbolicNames, elemsAreChar) {
	literalNames = literalNames || null
	symbolicNames = symbolicNames || null
	elemsAreChar = elemsAreChar || false
	if (i.intervals == null) {
		return "{}"
	} else if(literalNames!=null || symbolicNames!=null) {
		return i.toTokenString(literalNames, symbolicNames)
	} else if(elemsAreChar) {
		return i.toCharString()
	} else {
		return i.toIndexString()
	}
}

func (i *IntervalSet) toCharString() {
	var names = []
	for (var i = 0 i < i.intervals.length i++) {
		var v = i.intervals[i]
		if(v.stop==v.start+1) {
			if ( v.start==Token.EOF ) {
				names.push("<EOF>")
			} else {
				names.push("'" + String.fromCharCode(v.start) + "'")
			}
		} else {
			names.push("'" + String.fromCharCode(v.start) + "'..'" + String.fromCharCode(v.stop-1) + "'")
		}
	}
	if (names.length > 1) {
		return "{" + names.join(", ") + "}"
	} else {
		return names[0]
	}
}


func (i *IntervalSet) toIndexString() {
	var names = []
	for (var i = 0 i < i.intervals.length i++) {
		var v = i.intervals[i]
		if(v.stop==v.start+1) {
			if ( v.start==Token.EOF ) {
				names.push("<EOF>")
			} else {
				names.push(v.start.toString())
			}
		} else {
			names.push(v.start.toString() + ".." + (v.stop-1).toString())
		}
	}
	if (names.length > 1) {
		return "{" + names.join(", ") + "}"
	} else {
		return names[0]
	}
}


func (i *IntervalSet) toTokenString(literalNames, symbolicNames) {
	var names = []
	for (var i = 0 i < i.intervals.length i++) {
		var v = i.intervals[i]
		for (var j = v.start j < v.stop j++) {
			names.push(i.elementName(literalNames, symbolicNames, j))
		}
	}
	if (names.length > 1) {
		return "{" + names.join(", ") + "}"
	} else {
		return names[0]
	}
}

func (i *IntervalSet) elementName(literalNames, symbolicNames, a) {
	if (a == Token.EOF) {
		return "<EOF>"
	} else if (a == Token.EPSILON) {
		return "<EPSILON>"
	} else {
		return literalNames[a] || symbolicNames[a]
	}
}



