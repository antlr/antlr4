// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
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
	}

	return strconv.Itoa(i.start) + ".." + strconv.Itoa(i.stop-1)
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
	}

	return i.intervals[0].start
}

func (i *IntervalSet) addOne(v int) {
	i.addInterval(NewInterval(v, v+1))
}

func (i *IntervalSet) addRange(l, h int) {
	i.addInterval(NewInterval(l, h+1))
}

func (i *IntervalSet) addInterval(v *Interval) {
	if i.intervals == nil {
		i.intervals = make([]*Interval, 0)
		i.intervals = append(i.intervals, v)
	} else {
		// find insert pos
		for k, interval := range i.intervals {
			// distinct range -> insert
			if v.stop < interval.start {
				i.intervals = append(i.intervals[0:k], append([]*Interval{v}, i.intervals[k:]...)...)
				return
			} else if v.stop == interval.start {
				i.intervals[k].start = v.start
				return
			} else if v.start <= interval.stop {
				i.intervals[k] = NewInterval(intMin(interval.start, v.start), intMax(interval.stop, v.stop))

				// if not applying to end, merge potential overlaps
				if k < len(i.intervals)-1 {
					l := i.intervals[k]
					r := i.intervals[k+1]
					// if r contained in l
					if l.stop >= r.stop {
						i.intervals = append(i.intervals[0:k+1], i.intervals[k+2:]...)
					} else if l.stop >= r.start { // partial overlap
						i.intervals[k] = NewInterval(l.start, r.stop)
						i.intervals = append(i.intervals[0:k+1], i.intervals[k+2:]...)
					}
				}
				return
			}
		}
		// greater than any exiting
		i.intervals = append(i.intervals, v)
	}
}

func (i *IntervalSet) addSet(other *IntervalSet) *IntervalSet {
	if other.intervals != nil {
		for k := 0; k < len(other.intervals); k++ {
			i2 := other.intervals[k]
			i.addInterval(NewInterval(i2.start, i2.stop))
		}
	}
	return i
}

func (i *IntervalSet) complement(start int, stop int) *IntervalSet {
	result := NewIntervalSet()
	result.addInterval(NewInterval(start, stop+1))
	for j := 0; j < len(i.intervals); j++ {
		result.removeRange(i.intervals[j])
	}
	return result
}

func (i *IntervalSet) contains(item int) bool {
	if i.intervals == nil {
		return false
	}
	for k := 0; k < len(i.intervals); k++ {
		if i.intervals[k].contains(item) {
			return true
		}
	}
	return false
}

func (i *IntervalSet) length() int {
	len := 0

	for _, v := range i.intervals {
		len += v.length()
	}

	return len
}

func (i *IntervalSet) removeRange(v *Interval) {
	if v.start == v.stop-1 {
		i.removeOne(v.start)
	} else if i.intervals != nil {
		k := 0
		for n := 0; n < len(i.intervals); n++ {
			ni := i.intervals[k]
			// intervals are ordered
			if v.stop <= ni.start {
				return
			} else if v.start > ni.start && v.stop < ni.stop {
				i.intervals[k] = NewInterval(ni.start, v.start)
				x := NewInterval(v.stop, ni.stop)
				// i.intervals.splice(k, 0, x)
				i.intervals = append(i.intervals[0:k], append([]*Interval{x}, i.intervals[k:]...)...)
				return
			} else if v.start <= ni.start && v.stop >= ni.stop {
				//                i.intervals.splice(k, 1)
				i.intervals = append(i.intervals[0:k], i.intervals[k+1:]...)
				k = k - 1 // need another pass
			} else if v.start < ni.stop {
				i.intervals[k] = NewInterval(ni.start, v.start)
			} else if v.stop < ni.stop {
				i.intervals[k] = NewInterval(v.stop, ni.stop)
			}
			k++
		}
	}
}

func (i *IntervalSet) removeOne(v int) {
	if i.intervals != nil {
		for k := 0; k < len(i.intervals); k++ {
			ki := i.intervals[k]
			// intervals i ordered
			if v < ki.start {
				return
			} else if v == ki.start && v == ki.stop-1 {
				//				i.intervals.splice(k, 1)
				i.intervals = append(i.intervals[0:k], i.intervals[k+1:]...)
				return
			} else if v == ki.start {
				i.intervals[k] = NewInterval(ki.start+1, ki.stop)
				return
			} else if v == ki.stop-1 {
				i.intervals[k] = NewInterval(ki.start, ki.stop-1)
				return
			} else if v < ki.stop-1 {
				x := NewInterval(ki.start, v)
				ki.start = v + 1
				//				i.intervals.splice(k, 0, x)
				i.intervals = append(i.intervals[0:k], append([]*Interval{x}, i.intervals[k:]...)...)
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
	}

	return i.toIndexString()
}

func (i *IntervalSet) toCharString() string {
	names := make([]string, len(i.intervals))

	for j := 0; j < len(i.intervals); j++ {
		v := i.intervals[j]
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
	}

	return names[0]
}

func (i *IntervalSet) toIndexString() string {

	names := make([]string, 0)
	for j := 0; j < len(i.intervals); j++ {
		v := i.intervals[j]
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
	}

	return names[0]
}

func (i *IntervalSet) toTokenString(literalNames []string, symbolicNames []string) string {
	names := make([]string, 0)
	for _, v := range i.intervals {
		for j := v.start; j < v.stop; j++ {
			names = append(names, i.elementName(literalNames, symbolicNames, j))
		}
	}
	if len(names) > 1 {
		return "{" + strings.Join(names, ", ") + "}"
	}

	return names[0]
}

func (i *IntervalSet) elementName(literalNames []string, symbolicNames []string, a int) string {
	if a == TokenEOF {
		return "<EOF>"
	} else if a == TokenEpsilon {
		return "<EPSILON>"
	} else {
		if a < len(literalNames) && literalNames[a] != "" {
			return literalNames[a]
		}

		return symbolicNames[a]
	}
}
