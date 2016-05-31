package antlr

import (
	"errors"
	"fmt"
	"hash/fnv"
	"sort"
	"strings"
	//	"regexp"
	//	"bytes"
	//	"encoding/gob"
	"strconv"
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

type IntStack []int

var ErrEmptyStack = errors.New("Stack is empty")

func (s *IntStack) Pop() (int, error) {
	l := len(*s) - 1
	if l < 0 {
		return 0, ErrEmptyStack
	}
	v := (*s)[l]
	*s = (*s)[0:l]
	return v, nil
}

func (s *IntStack) Push(e int) {
	*s = append(*s, e)
}

type Set struct {
	data           map[string][]interface{}
	hashFunction   func(interface{}) string
	equalsFunction func(interface{}, interface{}) bool
}

func NewSet(hashFunction func(interface{}) string, equalsFunction func(interface{}, interface{}) bool) *Set {

	s := new(Set)

	s.data = make(map[string][]interface{})

	if hashFunction == nil {
		s.hashFunction = standardHashFunction
	} else {
		s.hashFunction = hashFunction
	}

	if equalsFunction == nil {
		s.equalsFunction = standardEqualsFunction
	} else {
		s.equalsFunction = equalsFunction
	}

	return s
}

func standardEqualsFunction(a interface{}, b interface{}) bool {

	ac, oka := a.(Comparable)
	bc, okb := b.(Comparable)

	if !oka || !okb {
		panic("Not Comparable")
	}

	return ac.equals(bc)
}

func standardHashFunction(a interface{}) string {
	h, ok := a.(Hasher)

	if ok {
		return h.Hash()
	}

	panic("Not Hasher")
}

//func getBytes(key interface{}) ([]byte, error) {
//	var buf bytes.Buffer
//	enc := gob.NewEncoder(&buf)
//	err := enc.Encode(key)
//	if err != nil {
//		return nil, err
//	}
//	return buf.Bytes(), nil
//}

type Hasher interface {
	Hash() string
}

func hashCode(s string) string {
	h := fnv.New32a()
	h.Write([]byte((s)))
	return fmt.Sprint(h.Sum32())
}

func (s *Set) length() int {
	return len(s.data)
}

func (s *Set) add(value interface{}) interface{} {

	var hash = s.hashFunction(value)
	var key = "hash_" + hashCode(hash)

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

func (s *Set) contains(value interface{}) bool {

	hash := s.hashFunction(value)
	key := "hash_" + hashCode(hash)

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

func (s *Set) values() []interface{} {
	var l = make([]interface{}, 0)

	for key := range s.data {
		if strings.Index(key, "hash_") == 0 {
			l = append(l, s.data[key]...)
		}
	}
	return l
}

func (s *Set) String() string {

	r := ""

	for _, av := range s.data {
		for _, v := range av {
			r += fmt.Sprint(v)
		}
	}

	return r
}

type BitSet struct {
	data map[int]bool
}

func NewBitSet() *BitSet {
	b := new(BitSet)
	b.data = make(map[int]bool)
	return b
}

func (b *BitSet) add(value int) {
	b.data[value] = true
}

func (b *BitSet) clear(index int) {
	delete(b.data, index)
}

func (b *BitSet) or(set *BitSet) {
	for k := range set.data {
		b.add(k)
	}
}

func (b *BitSet) remove(value int) {
	delete(b.data, value)
}

func (b *BitSet) contains(value int) bool {
	return b.data[value] == true
}

func (b *BitSet) values() []int {
	ks := make([]int, len(b.data))
	i := 0
	for k := range b.data {
		ks[i] = k
		i++
	}
	sort.Ints(ks)
	return ks
}

func (b *BitSet) minValue() int {
	min := 2147483647

	for k := range b.data {
		if k < min {
			min = k
		}
	}

	return min
}

func (b *BitSet) equals(other interface{}) bool {
	otherBitSet, ok := other.(*BitSet)
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

func (b *BitSet) length() int {
	return len(b.data)
}

func (b *BitSet) String() string {
	vals := b.values()
	valsS := make([]string, len(vals))

	for i, val := range vals {
		valsS[i] = strconv.Itoa(val)
	}
	return "{" + strings.Join(valsS, ", ") + "}"
}

type AltDict struct {
	data map[string]interface{}
}

func NewAltDict() *AltDict {
	d := new(AltDict)
	d.data = make(map[string]interface{})
	return d
}

func (a *AltDict) Get(key string) interface{} {
	key = "k-" + key
	return a.data[key]
}

func (a *AltDict) put(key string, value interface{}) {
	key = "k-" + key
	a.data[key] = value
}

func (a *AltDict) values() []interface{} {
	vs := make([]interface{}, len(a.data))
	i := 0
	for _, v := range a.data {
		vs[i] = v
		i++
	}
	return vs
}

type DoubleDict struct {
	data map[string]map[string]interface{}
}

func NewDoubleDict() *DoubleDict {
	dd := new(DoubleDict)
	dd.data = make(map[string]map[string]interface{})
	return dd
}

func (d *DoubleDict) Get(a string, b string) interface{} {
	var data = d.data[a]

	if data == nil {
		return nil
	}

	return data[b]
}

func (d *DoubleDict) set(a, b string, o interface{}) {
	var data = d.data[a]

	if data == nil {
		data = make(map[string]interface{})
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

func TitleCase(str string) string {

	//	func (re *Regexp) ReplaceAllStringFunc(src string, repl func(string) string) string
	//	return str.replace(//g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1)})

	panic("Not implemented")

	//	re := regexp.MustCompile("\w\S*")
	//	return re.ReplaceAllStringFunc(str, func(s string) {
	//		return strings.ToUpper(s[0:1]) + s[1:2]
	//	})

}
