// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"strconv"
	"strings"
)

type Interval struct {
	Start int
	Stop  int
}

/* Inclusive interval*/
func NewInterval(start, stop int) *Interval {
	i := new(Interval)
	i.Start = start
	i.Stop = stop
	return i
}

func (i *Interval) Contains(item int) bool {
	return item >= i.Start && item < i.Stop
}

// Does this start completely before other? Disjoint
func (i *Interval) StartsBeforeDisjoint(other *Interval) bool{
	return i.Start < other.Start && i.Stop < other.Start
}

// Does this start at or before other? Nondisjoint
func (i *Interval) StartsBeforeNonDisjoint(other *Interval) bool{
	return i.Start <= other.Start && i.Stop >= other.Start
}

// Does this.a start after other.b? May or may not be disjoint
func (i *Interval) StartsAfter(other *Interval) bool{
	return i.Start > other.Start
}

// Does this start completely after other? Disjoint
func (i *Interval) StartsAfterDisjoint(other *Interval) bool{
	return i.Start > other.Stop
}

// Does this start after other? NonDisjoint
func (i *Interval) StartsAfterNonDisjoint(other *Interval) bool{
	return i.Start>other.Start && i.Start <= other.Stop // i.Stop>=other.Stop implied
}

// Are both ranges disjoint? I.e., no overlap?
func (i *Interval) Disjoint(other *Interval) bool{
	return i.StartsBeforeDisjoint(other) || i.StartsAfterDisjoint(other)
}

// Are two intervals adjacent such as 0..41 and 42..42?
func (i *Interval) Adjacent(other *Interval) bool{
	return i.Start == other.Stop+1 || i.Stop == other.Start-1;
}

func (i *Interval) ProperlyContains(other *Interval) bool{
	return other.Start >= i.Start && other.Stop <= i.Stop
}

// Return the interval computed from combining this and other
func (i *Interval) Union(other *Interval) *Interval{
	return NewInterval(intMin(i.Start, other.Start), intMax(i.Stop, other.Stop))
}

// Return the interval in common between this and other
func (i *Interval) Intersection(other *Interval) *Interval{
	return NewInterval(intMax(i.Start, other.Start), intMin(i.Stop, other.Stop))
}
//  Return the interval with elements from this not in other;
//  other must not be totally enclosed (properly contained)
//  within this, which would result in two disjoint intervals
//  instead of the single one returned by this method.
func (i *Interval) DifferenceNotProperlyContained(other *Interval) *Interval{
	var diff *Interval = nil
	if other.StartsBeforeDisjoint(i){
		diff = NewInterval(intMax(i.Start, other.Stop +1), i.Stop)
	}else if other.StartsAfterNonDisjoint(i){
		diff = NewInterval(i.Start, other.Start -1)
	}
	return diff
}

func (i *Interval) String() string {
	if i.Start == i.Stop {
		return strconv.Itoa(i.Start)
	}

	return strconv.Itoa(i.Start) + ".." + strconv.Itoa(i.Stop)
}

func (i *Interval) length() int {
	return intMax(i.Stop - i.Start + 1,0)
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

	return i.intervals[0].Start
}

func (i *IntervalSet) addOne(v int) {
	i.addInterval(NewInterval(v, v))
}

func (i *IntervalSet) addRange(l, h int) {
	i.addInterval(NewInterval(l, h))
}

func (i *IntervalSet) addInterval(v *Interval) {
	if i.intervals == nil {
		i.intervals = make([]*Interval, 0)
		i.intervals = append(i.intervals, v)
	} else {
		// find insert pos
		for k, interval := range i.intervals {
			// distinct range -> insert
			if v.Stop < interval.Start {
				i.intervals = append(i.intervals[0:k], append([]*Interval{v}, i.intervals[k:]...)...)
				return
			} else if v.Stop == interval.Start {
				i.intervals[k].Start = v.Start
				return
			} else if v.Start <= interval.Stop {
				i.intervals[k] = NewInterval(intMin(interval.Start, v.Start), intMax(interval.Stop, v.Stop))

				// if not applying to end, merge potential overlaps
				if k < len(i.intervals)-1 {
					l := i.intervals[k]
					r := i.intervals[k+1]
					// if r contained in l
					if l.Stop >= r.Stop {
						i.intervals = append(i.intervals[0:k+1], i.intervals[k+2:]...)
					} else if l.Stop >= r.Start { // partial overlap
						i.intervals[k] = NewInterval(l.Start, r.Stop)
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
			i.addInterval(NewInterval(i2.Start, i2.Stop))
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
		if i.intervals[k].Contains(item) {
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
	if v.Start == v.Stop {
		i.removeOne(v.Start)
	} else if i.intervals != nil {
		for k := 0; k < len(i.intervals); k++ {
			ni := i.intervals[k]
			// intervals are ordered
			if v.Stop <= ni.Start {
				return
			} else if v.Start > ni.Start && v.Stop < ni.Stop {
				i.intervals[k] = NewInterval(ni.Start, v.Start-1)
				x := NewInterval(v.Stop+1, ni.Stop)
				// i.intervals.splice(k, 0, x)
				i.intervals = append(i.intervals[0:k+1], append([]*Interval{x}, i.intervals[k+1:]...)...)
				return
			} else if v.Start <= ni.Start && v.Stop >= ni.Stop {
				//                i.intervals.splice(k, 1)
				i.intervals = append(i.intervals[0:k], i.intervals[k+1:]...)
				k = k - 1
				continue
			} else if v.Start < ni.Stop {
				i.intervals[k] = NewInterval(ni.Start, v.Start-1)
			} else if v.Stop < ni.Stop {
				i.intervals[k] = NewInterval(v.Stop+1, ni.Stop)
			}
		}
	}
}

func (i *IntervalSet) removeOne(v int) {
	if i.intervals != nil {
		for k := 0; k < len(i.intervals); k++ {
			ki := i.intervals[k]
			// intervals i ordered
			if v < ki.Start {
				return
			} else if v == ki.Start && v == ki.Stop {
				//				i.intervals.splice(k, 1)
				i.intervals = append(i.intervals[0:k], i.intervals[k+1:]...)
				return
			} else if v == ki.Start {
				i.intervals[k] = NewInterval(ki.Start+1, ki.Stop)
				return
			} else if v == ki.Stop {
				i.intervals[k] = NewInterval(ki.Start, ki.Stop-1)
				return
			} else if v < ki.Stop {
				x := NewInterval(ki.Start, v-1)
				ki.Start = v + 1
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
		if v.Stop == v.Start+1 {
			if v.Start == TokenEOF {
				names = append(names, "<EOF>")
			} else {
				names = append(names, ("'" + string(v.Start) + "'"))
			}
		} else {
			names = append(names, "'"+string(v.Start)+"'..'"+string(v.Stop)+"'")
		}
	}

	if len(names) == 0{
		return "{}"
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
		if v.Stop == v.Start+1 {
			if v.Start == TokenEOF {
				names = append(names, "<EOF>")
			} else {
				names = append(names, strconv.Itoa(v.Start))
			}
		} else {
			names = append(names, strconv.Itoa(v.Start)+".."+strconv.Itoa(v.Stop))
		}
	}

	if len(names) == 0{
		return "{}"
	}

	if len(names) > 1 {
		return "{" + strings.Join(names, ", ") + "}"
	}

	return names[0]
}

func (i *IntervalSet) toTokenString(literalNames []string, symbolicNames []string) string {
	names := make([]string, 0)
	for _, v := range i.intervals {
		for j := v.Start; j < v.Stop; j++ {
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
