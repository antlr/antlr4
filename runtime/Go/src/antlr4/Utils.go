package antlr4

import (
	"errors"
	"fmt"
	"hash/fnv"
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

func standardHashFunction(a interface{}) string {
	h, ok := a.(Hasher)

	if ok {
		return h.Hash()
	}

	return fmt.Sprint(a)
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

func standardEqualsFunction(a interface{}, b interface{}) bool {
	return standardHashFunction(a) == standardHashFunction(b)
}

func (this *Set) length() int {
	return len(this.data)
}

func (this *Set) add(value interface{}) interface{} {

	var hash = this.hashFunction(value)
	var key = "hash_" + hashCode(hash)

	values := this.data[key]

	if this.data[key] != nil {

		for i := 0; i < len(values); i++ {
			if this.equalsFunction(value, values[i]) {
				return values[i]
			}
		}

		this.data[key] = append(this.data[key], value)
		return value
	}

	this.data[key] = []interface{}{value}
	return value
}

func (this *Set) contains(value interface{}) bool {

	hash := this.hashFunction(value)
	key := "hash_" + hashCode(hash)

	values := this.data[key]

	if this.data[key] != nil {

		for i := 0; i < len(values); i++ {
			if this.equalsFunction(value, values[i]) {
				return true
			}
		}
	}
	return false
}

func (this *Set) values() []interface{} {
	var l = make([]interface{}, 0)

	for key, _ := range this.data {
		if strings.Index(key, "hash_") == 0 {
			l = append(l, this.data[key]...)
		}
	}
	return l
}

func (this *Set) String() string {

	s := ""

	for _, av := range this.data {
		for _, v := range av {
			s += fmt.Sprint(v)
		}
	}

	return s
}

type BitSet struct {
	data map[int]bool
}

func NewBitSet() *BitSet {
	b := new(BitSet)
	b.data = make(map[int]bool)
	return b
}

func (this *BitSet) add(value int) {
	this.data[value] = true
}

func (this *BitSet) clear(index int) {
	delete(this.data, index)
}

func (this *BitSet) or(set *BitSet) {
	for k, _ := range set.data {
		this.add(k)
	}
}

func (this *BitSet) remove(value int) {
	delete(this.data, value)
}

func (this *BitSet) contains(value int) bool {
	return this.data[value] == true
}

func (this *BitSet) values() []int {
	ks := make([]int, len(this.data))
	i := 0
	for k, _ := range this.data {
		ks[i] = k
		i++
	}
	return ks
}

func (this *BitSet) minValue() int {
	min := 2147483647

	for k, _ := range this.data {
		if k < min {
			min = k
		}
	}

	return min
}

func (this *BitSet) equals(other interface{}) bool {
	otherBitSet, ok := other.(*BitSet)
	if !ok {
		return false
	}

	if len(this.data) != len(otherBitSet.data) {
		return false
	}

	for k, v := range this.data {
		if otherBitSet.data[k] != v {
			return false
		}
	}

	return true
}

func (this *BitSet) length() int {
	return len(this.data)
}

func (this *BitSet) String() string {
	vals := this.values()
	valsS := make([]string, len(vals))

	for i,val := range vals {
		valsS[i] = strconv.Itoa(val)
	}
	return "{" + strings.Join(valsS, ", ") + "}";
}

type AltDict struct {
	data map[string]interface{}
}

func NewAltDict() *AltDict {
	d := new(AltDict)
	d.data = make(map[string]interface{})
	return d
}

func (this *AltDict) Get(key string) interface{} {
	key = "k-" + key
	return this.data[key]
}

func (this *AltDict) put(key string, value interface{}) {
	key = "k-" + key
	this.data[key] = value
}

func (this *AltDict) values() []interface{} {
	vs := make([]interface{}, len(this.data))
	i := 0
	for _, v := range this.data {
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

func (this *DoubleDict) Get(a string, b string) interface{} {
	var d = this.data[a]

	if d == nil {
		return nil
	}

	return d[b]
}

func (this *DoubleDict) set(a, b string, o interface{}) {
	var d = this.data[a]

	if d == nil {
		d = make(map[string]interface{})
		this.data[a] = d
	}

	d[b] = o
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
	return ""

}
