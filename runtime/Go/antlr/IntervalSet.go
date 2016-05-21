package antlr4

import (
	"fmt"
	"strconv"
	"strings"
)

type Interval struct {
	start int
	stop  int
}

/* stop is not included! */
func NewInterval(start, stop int) *Interval {
	i := new(Interval)

	i.start = start
	i.stop = stop
	return i
}

func (i *Interval) contains(item int) bool {
	return item >= i.start && item < i.stop
}

func (i *Interval) String() string {
	if i.start == i.stop-1 {
		return strconv.Itoa(i.start)
	} else {
		return strconv.Itoa(i.start) + ".." + strconv.Itoa(i.stop-1)
	}
}

func (i *Interval) length() int {
	return i.stop - i.start
}

type IntervalSet struct {
	intervals []*Interval
	readOnly  bool
}

func NewIntervalSet() *IntervalSet {

	i := new(IntervalSet)

	i.intervals = nil
	i.readOnly = false

	return i
}

func (i *IntervalSet) first() int {
	if len(i.intervals) == 0 {
		return TokenInvalidType
	} else {
		return i.intervals[0].start
	}
}

func (i *IntervalSet) addOne(v int) {
	i.addInterval(NewInterval(v, v+1))
}

func (i *IntervalSet) addRange(l, h int) {
	i.addInterval(NewInterval(l, h+1))
}

func (is *IntervalSet) addInterval(v *Interval) {
	if PortDebug {
		fmt.Println("addInterval" + v.String())
	}
	if is.intervals == nil {
		is.intervals = make([]*Interval, 0)
		is.intervals = append(is.intervals, v)
	} else {
		// find insert pos
		for k := 0; k < len(is.intervals); k++ {
			var i = is.intervals[k]
			// distinct range -> insert
			if v.stop < i.start {
				// is.intervals = splice(k, 0, v)
				is.intervals = append(is.intervals[0:k], append([]*Interval{v}, is.intervals[k:]...)...)
				return
			} else if v.stop == i.start {
				is.intervals[k].start = v.start
				return
			} else if v.start <= i.stop {
				is.intervals[k] = NewInterval(intMin(i.start, v.start), intMax(i.stop, v.stop))
				is.reduce(k)
				return
			}
		}
		// greater than any existing
		is.intervals = append(is.intervals, v)
	}
}

func (i *IntervalSet) addSet(other *IntervalSet) *IntervalSet {
	if PortDebug {
		fmt.Println("addSet")
	}
	if other.intervals != nil {
		if PortDebug {
			fmt.Println(len(other.intervals))
		}
		for k := 0; k < len(other.intervals); k++ {
			var i2 = other.intervals[k]
			i.addInterval(NewInterval(i2.start, i2.stop))
		}
	}
	return i
}

func (i *IntervalSet) reduce(k int) {
	// only need to reduce if k is not the last
	if k < len(i.intervals)-1 {
		var l = i.intervals[k]
		var r = i.intervals[k+1]
		// if r contained in l
		if l.stop >= r.stop {
			i.intervals = i.intervals[0 : len(i.intervals)-1] // pop(k + 1)
			i.reduce(k)
		} else if l.stop >= r.start {
			i.intervals[k] = NewInterval(l.start, r.stop)
			i.intervals = i.intervals[0 : len(i.intervals)-1] // i.intervals.pop(k + 1)
		}
	}
}

func (is *IntervalSet) complement(start int, stop int) *IntervalSet {
	var result = NewIntervalSet()
	result.addInterval(NewInterval(start, stop+1))
	for i := 0; i < len(is.intervals); i++ {
		result.removeRange(is.intervals[i])
	}
	return result
}

func (i *IntervalSet) contains(item int) bool {
	if i.intervals == nil {
		return false
	} else {
		for k := 0; k < len(i.intervals); k++ {
			if i.intervals[k].contains(item) {
				return true
			}
		}
		return false
	}
}

func (is *IntervalSet) length() int {
	len := 0

	for _, v := range is.intervals {
		len += v.length()
	}

	return len
}

func (is *IntervalSet) removeRange(v *Interval) {
	if v.start == v.stop-1 {
		is.removeOne(v.start)
	} else if is.intervals != nil {
		k := 0
		for n := 0; n < len(is.intervals); n++ {
			var i = is.intervals[k]
			// intervals are ordered
			if v.stop <= i.start {
				return
			} else if v.start > i.start && v.stop < i.stop {
				is.intervals[k] = NewInterval(i.start, v.start)
				var x = NewInterval(v.stop, i.stop)
				// is.intervals.splice(k, 0, x)
				is.intervals = append(is.intervals[0:k], append([]*Interval{x}, is.intervals[k:]...)...)
				return
			} else if v.start <= i.start && v.stop >= i.stop {
				//                is.intervals.splice(k, 1)
				is.intervals = append(is.intervals[0:k], is.intervals[k+1:]...)
				k = k - 1 // need another pass
			} else if v.start < i.stop {
				is.intervals[k] = NewInterval(i.start, v.start)
			} else if v.stop < i.stop {
				is.intervals[k] = NewInterval(v.stop, i.stop)
			}
			k += 1
		}
	}
}

func (is *IntervalSet) removeOne(v int) {
	if is.intervals != nil {
		for k := 0; k < len(is.intervals); k++ {
			var i = is.intervals[k]
			// intervals is ordered
			if v < i.start {
				return
			} else if v == i.start && v == i.stop-1 {
				//				is.intervals.splice(k, 1);
				is.intervals = append(is.intervals[0:k], is.intervals[k+1:]...)
				return
			} else if v == i.start {
				is.intervals[k] = NewInterval(i.start+1, i.stop)
				return
			} else if v == i.stop-1 {
				is.intervals[k] = NewInterval(i.start, i.stop-1)
				return
			} else if v < i.stop-1 {
				var x = NewInterval(i.start, v)
				i.start = v + 1
				//				is.intervals.splice(k, 0, x);
				is.intervals = append(is.intervals[0:k], append([]*Interval{x}, is.intervals[k:]...)...)
				return
			}
		}
	}
}

func (i *IntervalSet) String() string {
	return i.StringVerbose(nil, nil, false)
}

func (i *IntervalSet) StringVerbose(literalNames []string, symbolicNames []string, elemsAreChar bool) string {

	if i.intervals == nil {
		return "{}"
	} else if literalNames != nil || symbolicNames != nil {
		return i.toTokenString(literalNames, symbolicNames)
	} else if elemsAreChar {
		return i.toCharString()
	} else {
		return i.toIndexString()
	}
}

func (is *IntervalSet) toCharString() string {
	var names = make([]string, len(is.intervals))

	for i := 0; i < len(is.intervals); i++ {
		var v = is.intervals[i]
		if v.stop == v.start+1 {
			if v.start == TokenEOF {
				names = append(names, "<EOF>")
			} else {
				names = append(names, ("'" + string(v.start) + "'"))
			}
		} else {
			names = append(names, "'"+string(v.start)+"'..'"+string(v.stop-1)+"'")
		}
	}
	if len(names) > 1 {
		return "{" + strings.Join(names, ", ") + "}"
	} else {
		return names[0]
	}
}

func (is *IntervalSet) toIndexString() string {

	var names = make([]string, 0)
	for i := 0; i < len(is.intervals); i++ {
		var v = is.intervals[i]
		if v.stop == v.start+1 {
			if v.start == TokenEOF {
				names = append(names, "<EOF>")
			} else {
				names = append(names, strconv.Itoa(v.start))
			}
		} else {
			names = append(names, strconv.Itoa(v.start)+".."+strconv.Itoa(v.stop-1))
		}
	}
	if len(names) > 1 {
		return "{" + strings.Join(names, ", ") + "}"
	} else {
		return names[0]
	}
}

func (is *IntervalSet) toTokenString(literalNames []string, symbolicNames []string) string {
	var names = make([]string, 0)
	for _,v := range is.intervals {
		for j := v.start; j < v.stop; j++ {
			names = append(names, is.elementName(literalNames, symbolicNames, j))
		}
	}
	if len(names) > 1 {
		return "{" + strings.Join(names, ", ") + "}"
	} else {
		return names[0]
	}
}

func (i *IntervalSet) elementName(literalNames []string, symbolicNames []string, a int) string {
	if a == TokenEOF {
		return "<EOF>"
	} else if a == TokenEpsilon {
		return "<EPSILON>"
	} else {
		if a < len(literalNames) && literalNames[a] != "" {
			return literalNames[a]
		} else {
			return symbolicNames[a]
		}
	}
}
