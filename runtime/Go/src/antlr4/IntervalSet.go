package antlr4

import (
	"strings"
	"strconv"
)

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
		return strconv.Itoa(i.start)
	} else {
		return strconv.Itoa(i.start) + ".." + strconv.Itoa(i.stop-1)
	}
}

func (i *Interval) length() int {
	return i.stop - i.start
}

type IntervalSet struct {
	intervals []Interval
	readOnly bool
}

func NewIntervalSet() *IntervalSet {

	i := new(IntervalSet)

	i.intervals = nil
	i.readOnly = false

	return i
}

func (i *IntervalSet) first(v int) int {
	if (i.intervals == nil || len(i.intervals)==0) {
		return TokenInvalidType
	} else {
		return i.intervals[0].start
	}
}

func (i *IntervalSet) addOne(v int) {
	i.addInterval(NewInterval(v, v + 1))
}

func (i *IntervalSet) addRange(l int, h int) {
	i.addInterval(NewInterval(l, h + 1))
}

func (i *IntervalSet) addInterval(v Interval) {
	if (i.intervals == nil) {
		i.intervals = make([]Interval, 0)
		append( i.intervals, v )
	} else {
		// find insert pos
		for k := 0; k < len(i.intervals); k++ {
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
				i.intervals[k] = NewInterval(Math.min(i.start, v.start), Math.max(i.stop, v.stop))
				i.reduce(k)
				return
			}
		}
		// greater than any existing
		i.intervals.push(v)
	}
}

func (i *IntervalSet) addSet(other IntervalSet) *IntervalSet {
	if (other.intervals != nil) {
		for k := 0; k < len(other.intervals); k++ {
			var i = other.intervals[k]
			i.addInterval(NewInterval(i.start, i.stop))
		}
	}
	return i
}

func (i *IntervalSet) reduce(k int) {
	// only need to reduce if k is not the last
	if (k < len(i.intervals) - 1) {
		var l = i.intervals[k]
		var r = i.intervals[k + 1]
		// if r contained in l
		if (l.stop >= r.stop) {
			i.intervals.pop(k + 1)
			i.reduce(k)
		} else if (l.stop >= r.start) {
			i.intervals[k] = NewInterval(l.start, r.stop)
			i.intervals.pop(k + 1)
		}
	}
}

func (is *IntervalSet) complement(start int, stop int) *IntervalSet {
    var result = NewIntervalSet()
    result.addInterval(NewInterval(start,stop+1))
    for i := 0; i< len(is.intervals); i++ {
        result.removeRange(is.intervals[i])
    }
    return result
}

func (i *IntervalSet) contains(item Interval) bool {
	if (i.intervals == nil) {
		return false
	} else {
		for k := 0; k < len(i.intervals); k++ {
			if(i.intervals[k].contains(item)) {
				return true
			}
		}
		return false
	}
}

func (is *IntervalSet) length() int {
	len := 0

	for _,v := range is.intervals {
		len += v.length()
	}

	return len
}

func (i *IntervalSet) removeRange(v Interval) {
    if v.start==v.stop-1 {
        i.removeOne(v.start)
    } else if (i.intervals!=nil) {
        k:= 0
        for n :=0; n<len( i.intervals ); n++ {
            var i = i.intervals[k]
            // intervals are ordered
            if (v.stop<=i.start) {
                return
            }
            // check for including range, split it
            else if(v.start>i.start && v.stop<i.stop) {
                i.intervals[k] = NewInterval(i.start, v.start)
                var x = NewInterval(v.stop, i.stop)
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
                i.intervals[k] = NewInterval(i.start, v.start)
            }
            // check for upper boundary
            else if(v.stop<i.stop) {
                i.intervals[k] = NewInterval(v.stop, i.stop)
            }
            k += 1
        }
    }
}

func (is *IntervalSet) removeOne(v *Interval) {
	if(v.start==v.stop-1) {
		is.removeOne(v.start)
	} else if (is.intervals!=nil) {
		var k = 0
		for n := 0; n < len(is.intervals); n++ {
			i := is.intervals[k]
			// intervals are ordered
			if v.stop<=i.start {
				return
			}
			// check for including range, split it
			else if(v.start>i.start && v.stop<i.stop) {
				is.intervals[k] = NewInterval(i.start, v.start)
				var x = NewInterval(v.stop, i.stop)
				is.intervals.splice(k, 0, x)
				return
			}
			// check for included range, remove it
			else if(v.start<=i.start && v.stop>=i.stop) {
				is.intervals.splice(k, 1)
				k = k - 1; // need another pass
			}
			// check for lower boundary
			else if(v.start<i.stop) {
				is.intervals[k] = NewInterval(i.start, v.start)
			}
			// check for upper boundary
			else if(v.stop<i.stop) {
				is.intervals[k] = NewInterval(v.stop, i.stop)
			}
			k += 1
		}
	}
}

func (i *IntervalSet) toString(literalNames []string, symbolicNames []string, elemsAreChar bool) string {
	literalNames = literalNames || nil
	symbolicNames = symbolicNames || nil
	elemsAreChar = elemsAreChar || false
	if (i.intervals == nil) {
		return "{}"
	} else if(literalNames!=nil || symbolicNames!=nil) {
		return i.toTokenString(literalNames, symbolicNames)
	} else if(elemsAreChar) {
		return i.toCharString()
	} else {
		return i.toIndexString()
	}
}

func (is *IntervalSet) toCharString() {
	var names = make([]string, len(is.intervals))

	for i := 0; i < len( is.intervals ); i++ {
		var v = is.intervals[i]
		if(v.stop==v.start+1) {
			if ( v.start== TokenEOF ) {
				append(names, "<EOF>")
			} else {
				append(names, ("'" + String.fromCharCode(v.start) + "'"))
			}
		} else {
			append(names, "'" + String.fromCharCode(v.start) + "'..'" + String.fromCharCode(v.stop-1) + "'")
		}
	}
	if (len(names) > 1) {
		return "{" + strings.Join(names, ", ") + "}"
	} else {
		return names[0]
	}
}


func (i *IntervalSet) toIndexString() {
	var names = []
	for (var i = 0 i < len( i.intervals ) i++) {
		var v = i.intervals[i]
		if(v.stop==v.start+1) {
			if ( v.start==TokenEOF ) {
				names.push("<EOF>")
			} else {
				names.push(v.start.toString())
			}
		} else {
			names.push(v.start.toString() + ".." + (v.stop-1).toString())
		}
	}
	if (names.length > 1) {
		return "{" + string.Join(names, ", ") + "}"
	} else {
		return names[0]
	}
}


func (i *IntervalSet) toTokenString(literalNames []string, symbolicNames []string) string {
	var names = []
	for i := 0; i < len( i.intervals ); i++ {
		var v = i.intervals[i]
		for j := v.start; j < v.stop; j++ {
			names.push(i.elementName(literalNames, symbolicNames, j))
		}
	}
	if (names.length > 1) {
		return "{" + names.join(", ") + "}"
	} else {
		return names[0]
	}
}

func (i *IntervalSet) elementName(literalNames []string, symbolicNames []string, a int) string {
	if (a == TokenEOF) {
		return "<EOF>"
	} else if (a == TokenEpsilon) {
		return "<EPSILON>"
	} else {
		return literalNames[a] || symbolicNames[a]
	}
}



