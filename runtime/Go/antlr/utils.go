// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"bytes"
	"errors"
	"fmt"
	"hash/fnv"
	"sort"
	"strconv"
	"strings"
)

func intMin(a, b int) int {
	if a < b {
		return a
	}
	return b
}

func intMax(a, b int) int {
	if a > b {
		return a
	}
	return b
}

// A simple integer stack

type intStack []int

var errEmptyStack = errors.New("Stack is empty")

func (s *intStack) pop() (int, error) {
	l := len(*s) - 1
	if l < 0 {
		return 0, errEmptyStack
	}
	v := (*s)[l]
	*s = (*s)[0:l]
	return v, nil
}

func (s *intStack) push(e int) {
	*s = append(*s, e)
}

type set struct {
	data           map[int][]interface{}
	hashFunction   func(interface{}) int
	equalsFunction func(interface{}, interface{}) bool
}

func newSet(hashFunction func(interface{}) int, equalsFunction func(interface{}, interface{}) bool) *set {
	s := new(set)

	s.data = make(map[int][]interface{})

	if hashFunction == nil {
		s.hashFunction = hasherHash
	} else {
		s.hashFunction = hashFunction
	}

	if equalsFunction == nil {
		s.equalsFunction = comparableHash
	} else {
		s.equalsFunction = equalsFunction
	}

	return s
}

func comparableHash(a interface{}, b interface{}) bool {

	ac, oka := a.(Comparable)
	bc, okb := b.(Comparable)

	if !oka || !okb {
		panic("Not Comparable")
	}

	return ac.equals(bc)
}

func hasherHash(a interface{}) int {
	h, ok := a.(Hasher)

	if ok {
		return h.Hash()
	}

	panic("Not Hasher")
}

type Hasher interface {
	Hash() int
}

func hashCode(s string) int {
	h := fnv.New32a()
	h.Write([]byte((s)))
	return int(h.Sum32())
}

func (s *set) length() int {
	return len(s.data)
}

func (s *set) add(value interface{}) interface{} {

	key := s.hashFunction(value)
	values := s.data[key]

	if s.data[key] != nil {

		for i := 0; i < len(values); i++ {
			if s.equalsFunction(value, values[i]) {
				return values[i]
			}
		}

		s.data[key] = append(s.data[key], value)
		return value
	}

	s.data[key] = []interface{}{value}
	return value
}

func (s *set) contains(value interface{}) bool {
	key := s.hashFunction(value)

	values := s.data[key]

	if s.data[key] != nil {
		for i := 0; i < len(values); i++ {
			if s.equalsFunction(value, values[i]) {
				return true
			}
		}
	}
	return false
}

func (s *set) values() []interface{} {
	l := make([]interface{}, 10)

	for key := range s.data {
		l = append(l, s.data[key]...)
	}
	return l
}

func (s *set) String() string {
	r := ""

	for _, av := range s.data {
		for _, v := range av {
			r += fmt.Sprint(v)
		}
	}

	return r
}

type bitSet struct {
	data map[int]bool
}

func newBitSet() *bitSet {
	b := new(bitSet)
	b.data = make(map[int]bool)
	return b
}

func (b *bitSet) add(value int) {
	b.data[value] = true
}

func (b *bitSet) clear(index int) {
	delete(b.data, index)
}

func (b *bitSet) or(set *bitSet) {
	for k := range set.data {
		b.add(k)
	}
}

func (b *bitSet) remove(value int) {
	delete(b.data, value)
}

func (b *bitSet) contains(value int) bool {
	return b.data[value] == true
}

func (b *bitSet) values() []int {
	ks := make([]int, len(b.data))
	i := 0
	for k := range b.data {
		ks[i] = k
		i++
	}
	sort.Ints(ks)
	return ks
}

func (b *bitSet) minValue() int {
	min := 2147483647

	for k := range b.data {
		if k < min {
			min = k
		}
	}

	return min
}

func (b *bitSet) equals(other interface{}) bool {
	otherBitSet, ok := other.(*bitSet)
	if !ok {
		return false
	}

	if len(b.data) != len(otherBitSet.data) {
		return false
	}

	for k, v := range b.data {
		if otherBitSet.data[k] != v {
			return false
		}
	}

	return true
}

func (b *bitSet) length() int {
	return len(b.data)
}

func (b *bitSet) String() string {
	vals := b.values()
	valsS := make([]string, len(vals))

	for i, val := range vals {
		valsS[i] = strconv.Itoa(val)
	}
	return "{" + strings.Join(valsS, ", ") + "}"
}

type altDict struct {
	data map[string]interface{}
}

func newAltDict() *altDict {
	d := new(altDict)
	d.data = make(map[string]interface{})
	return d
}

func (a *altDict) Get(key string) interface{} {
	key = "k-" + key
	return a.data[key]
}

func (a *altDict) put(key string, value interface{}) {
	key = "k-" + key
	a.data[key] = value
}

func (a *altDict) values() []interface{} {
	vs := make([]interface{}, len(a.data))
	i := 0
	for _, v := range a.data {
		vs[i] = v
		i++
	}
	return vs
}

type doubleDict struct {
	data map[int]map[int]interface{}
}

func newDoubleDict() *doubleDict {
	dd := new(doubleDict)
	dd.data = make(map[int]map[int]interface{})
	return dd
}

func (d *doubleDict) get(a, b int) interface{} {
	data := d.data[a]

	if data == nil {
		return nil
	}

	return data[b]
}

func (d *doubleDict) set(a, b int, o interface{}) {
	data := d.data[a]

	if data == nil {
		data = make(map[int]interface{})
		d.data[a] = data
	}

	data[b] = o
}

func EscapeWhitespace(s string, escapeSpaces bool) string {

	s = strings.Replace(s, "\t", "\\t", -1)
	s = strings.Replace(s, "\n", "\\n", -1)
	s = strings.Replace(s, "\r", "\\r", -1)
	if escapeSpaces {
		s = strings.Replace(s, " ", "\u00B7", -1)
	}
	return s
}

func TerminalNodeToStringArray(sa []TerminalNode) []string {
	st := make([]string, len(sa))

	for i, s := range sa {
		st[i] = fmt.Sprintf("%v", s)
	}

	return st
}

func PrintArrayJavaStyle(sa []string) string {
	var buffer bytes.Buffer

	buffer.WriteString("[")

	for i, s := range sa {
		buffer.WriteString(s)
		if i != len(sa)-1 {
			buffer.WriteString(", ")
		}
	}

	buffer.WriteString("]")

	return buffer.String()
}

// murmur hash
const (
	c1_32 = 0xCC9E2D51
	c2_32 = 0x1B873593
	n1_32 = 0xE6546B64
)

func murmurInit(seed int) int {
	return seed
}

func murmurUpdate(h1 int, k1 int) int {
	k1 *= c1_32
	k1 = (k1 << 15) | (k1 >> 17) // rotl32(k1, 15)
	k1 *= c2_32

	h1 ^= k1
	h1 = (h1 << 13) | (h1 >> 19) // rotl32(h1, 13)
	h1 = h1*5 + 0xe6546b64
	return h1
}

func murmurFinish(h1 int, numberOfWords int) int {
	h1 ^= (numberOfWords * 4)
	h1 ^= h1 >> 16
	h1 *= 0x85ebca6b
	h1 ^= h1 >> 13
	h1 *= 0xc2b2ae35
	h1 ^= h1 >> 16

	return h1
}
