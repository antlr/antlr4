package antlr4

import (
	"fmt"
	"errors"
	"strings"
	"hash/fnv"
)


// A simple integer stack

type IntStack []int

var ErrEmptyStack = errors.New("Stack is empty")

func (s *IntStack) Pop() (int, error) {
	l := len(*s)-1
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



func arrayToString(a []interface{}) string{
	return fmt.Sprintf( a )
}


func (s *string) hashCode() int {
	h := fnv.New32a()
	h.Write([]byte((*s)))
	return h.Sum32()
}


type Set struct {
	data map[int][]interface{}
	hashfunc func(interface{}) string
	equalsfunc func(interface{},interface{}) bool
}

func NewSet(hashFunction func(interface{}) string, equalsFunction func(interface{},interface{}) bool) *Set {

	s := new(Set)

	s.data = make(map[string]interface{})

	if (hashFunction == nil){
		s.hashfunc = standardHashFunction
	} else {
		s.hashfunc = hashFunction
	}

	if (equalsFunction == nil){
		s.equalsfunc = standardEqualsFunction
	} else {
		s.equalsfunc = equalsFunction
	}

	return s
}

func standardEqualsFunction(a interface{}, b interface{}) bool {
	return a == b
}

func standardHashFunction(a interface{}) string {
	return a.hashString()
}

func (this *Set) length() int {
	return len(this.data)
}

func (this *Set) add(value interface{}) {
	var hash = this.hashFunction(value)
	var key = "hash_" + hash.hashCode()

	if key_,values := range this.data {
		for i=0; i<len(values); i++) {
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

func (this *Set) contains(value interface{}) bool {
	var hash = this.hashFunction(value)
	var key = hash.hashCode()
	if k,values := range this.data {
		for i :=0; i < len(values); i++ {
			if(this.equalsFunction(value, values[i])) {
				return true
			}
		}
	}
	return false
}

func (this *Set) values() []interface{} {
	var l = make([]interface{}, len(this.data))

	for key,_ := range this.data {
		if(key.indexOf("hash_")==0) {
			l = l.concat(this.data[key])
		}
	}
	return l
}

func (this *Set) toString() string {
	return arrayToString(this.values())
}




type BitSet struct {
	this.data = []
	return this
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
	if(!_, ok := other.(BitSet); ok) {
		return false
	}
	return this.hashString()==other.hashString()
}

Object.defineProperty(BitSet.prototype, "length", {
	get : function() {
		return this.values().length
	}
})

func (this *BitSet) toString() string {
	return "{" + strings.Join(this.values(), ", ") + "}"
}




type AltDict struct {
	data map[string]interface{}
}

func NewAltDict() *AltDict {
	d := new(AltDict)
	d.data = make(map[string]interface{})
	return d
}

func (this *AltDict) get(key string) interface{} {
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
	for _,v := range this.data {
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

func (this *DoubleDict) get(a string, b string) interface{} {
	var d = this.data[a] || nil

	if (d == nil){
		return nil
	}

	return d[b]
}

func (this *DoubleDict) set(a, b string, o interface{}) {
	var d = this.data[a]

	if(d==nil) {
		d = make(map[string]interface{})
		this.data[a] = d
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

//exports.isArray = func (entity) {
//	return Object.prototype.toString.call( entity ) == '[object Array]'
//}

exports.titleCase = function(str) {
	return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1)})
}







