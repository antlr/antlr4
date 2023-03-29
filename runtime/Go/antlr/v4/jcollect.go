package antlr

// Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

import (
	"fmt"
	"sort"
)

// Collectable is an interface that a struct should implement if it is to be
// usable as a key in these collections.
type Collectable[T any] interface {
	Hash() int
	Equals(other Collectable[T]) bool
}

type Comparator[T any] interface {
	Hash1(o T) int
	Equals2(T, T) bool
}

type JStatRec struct {
	MaxSize          int
	CurSize          int
	Gets             int
	GetHits          int
	GetMisses        int
	GetHashConflicts int
	GetNoEnt         int
	Puts             int
	PutHits          int
	PutMisses        int
	PutHashConflicts int
	MaxSlotSize      int
	Description      string
	Signature        string
}

var JStats []*JStatRec

// JStore implements a container that allows the use of a struct to calculate the key
// for a collection of values akin to map. This is not meant to be a full-blown HashMap but just
// serve the needs of the ANTLR Go runtime.
//
// For ease of porting the logic of the runtime from the master target (Java), this collection
// operates in a similar way to Java, in that it can use any struct that supplies a Hash() and Equals()
// function as the key. The values are stored in a standard go map which internally is a form of hashmap
// itself, the key for the go map is the hash supplied by the key object. The collection is able to deal with
// hash conflicts by using a simple slice of values associated with the hash code indexed bucket. That isn't
// particularly efficient, but it is simple, and it works. As this is specifically for the ANTLR runtime, and
// we understand the requirements, then this is fine - this is not a general purpose collection.
type JStore[T any, C Comparator[T]] struct {
	store      map[int][]T
	len        int
	comparator Comparator[T]
	stats      JStatRec
}

func NewJStore[T any, C Comparator[T]](comparator Comparator[T], desc string) *JStore[T, C] {
	
	if comparator == nil {
		panic("comparator cannot be nil")
	}
	
	s := &JStore[T, C]{
		store:      make(map[int][]T, 1),
		comparator: comparator,
		stats: JStatRec{
			Description: desc,
		},
	}
	s.stats.Signature = fmt.Sprintf("%+v", s)
	JStats = append(JStats, &s.stats)
	return s
}

// Put will store given value in the collection. Note that the key for storage is generated from
// the value itself - this is specifically because that is what ANTLR needs - this would not be useful
// as any kind of general collection.
//
// If the key has a hash conflict, then the value will be added to the slice of values associated with the
// hash, unless the value is already in the slice, in which case the existing value is returned. Value equivalence is
// tested by calling the equals() method on the key.
//
// # If the given value is already present in the store, then the existing value is returned as v and exists is set to true
//
// If the given value is not present in the store, then the value is added to the store and returned as v and exists is set to false.
func (s *JStore[T, C]) Put(value T) (v T, exists bool) {
	
	s.stats.Puts++
	kh := s.comparator.Hash1(value)
	
	var hClash bool
	for _, v1 := range s.store[kh] {
		hClash = true
		if s.comparator.Equals2(value, v1) {
			s.stats.PutHits++
			s.stats.PutHashConflicts++
			return v1, true
		}
		s.stats.PutMisses++
	}
	if hClash {
		s.stats.PutHashConflicts++
	}
	s.store[kh] = append(s.store[kh], value)
	s.len++
	s.stats.CurSize = s.len
	if s.len > s.stats.MaxSize {
		s.stats.MaxSize = s.len
	}
	return value, false
}

// Get will return the value associated with the key - the type of the key is the same type as the value
// which would not generally be useful, but this is a specific thing for ANTLR where the key is
// generated using the object we are going to store.
func (s *JStore[T, C]) Get(key T) (T, bool) {
	s.stats.Gets++
	kh := s.comparator.Hash1(key)
	var hClash bool
	for _, v := range s.store[kh] {
		hClash = true
		if s.comparator.Equals2(key, v) {
			s.stats.GetHits++
			s.stats.GetHashConflicts++
			return v, true
		}
		s.stats.GetMisses++
	}
	if hClash {
		s.stats.GetHashConflicts++
	}
	s.stats.GetNoEnt++
	return key, false
}

// Contains returns true if the given key is present in the store
func (s *JStore[T, C]) Contains(key T) bool {
	_, present := s.Get(key)
	return present
}

func (s *JStore[T, C]) SortedSlice(less func(i, j T) bool) []T {
	vs := make([]T, 0, len(s.store))
	for _, v := range s.store {
		vs = append(vs, v...)
	}
	sort.Slice(vs, func(i, j int) bool {
		return less(vs[i], vs[j])
	})
	
	return vs
}

func (s *JStore[T, C]) Each(f func(T) bool) {
	for _, e := range s.store {
		for _, v := range e {
			f(v)
		}
	}
}

func (s *JStore[T, C]) Len() int {
	return s.len
}

func (s *JStore[T, C]) Values() []T {
	vs := make([]T, 0, len(s.store))
	for _, e := range s.store {
		vs = append(vs, e...)
	}
	return vs
}

type entry[K, V any] struct {
	key K
	val V
}

type JMap[K, V any, C Comparator[K]] struct {
	store      map[int][]*entry[K, V]
	len        int
	comparator Comparator[K]
}

func NewJMap[K, V any, C Comparator[K]](comparator Comparator[K]) *JMap[K, V, C] {
	return &JMap[K, V, C]{
		store:      make(map[int][]*entry[K, V], 1),
		comparator: comparator,
	}
}

func (m *JMap[K, V, C]) Put(key K, val V) (V, bool) {
	kh := m.comparator.Hash1(key)
	
	for _, e := range m.store[kh] {
		if m.comparator.Equals2(e.key, key) {
			return e.val, true
		}
	}
	m.store[kh] = append(m.store[kh], &entry[K, V]{key, val})
	m.len++
	return val, false
}

func (m *JMap[K, V, C]) Values() []V {
	vs := make([]V, 0, len(m.store))
	for _, e := range m.store {
		for _, v := range e {
			vs = append(vs, v.val)
		}
	}
	return vs
}

func (m *JMap[K, V, C]) Get(key K) (V, bool) {
	
	var none V
	kh := m.comparator.Hash1(key)
	for _, e := range m.store[kh] {
		if m.comparator.Equals2(e.key, key) {
			return e.val, true
		}
	}
	return none, false
}

func (m *JMap[K, V, C]) Len() int {
	return m.len
}

func (m *JMap[K, V, C]) Delete(key K) {
	kh := m.comparator.Hash1(key)
	for i, e := range m.store[kh] {
		if m.comparator.Equals2(e.key, key) {
			m.store[kh] = append(m.store[kh][:i], m.store[kh][i+1:]...)
			m.len--
			return
		}
	}
}

func (m *JMap[K, V, C]) Clear() {
	m.store = make(map[int][]*entry[K, V])
}

type JPCMap struct {
	store *JMap[*PredictionContext, *JMap[*PredictionContext, *PredictionContext, *ObjEqComparator[*PredictionContext]], *ObjEqComparator[*PredictionContext]]
	size  int
}

func NewJPCMap() *JPCMap {
	return &JPCMap{
		store: NewJMap[*PredictionContext, *JMap[*PredictionContext, *PredictionContext, *ObjEqComparator[*PredictionContext]], *ObjEqComparator[*PredictionContext]](pContextEqInst),
	}
}

func (pcm *JPCMap) Get(k1, k2 *PredictionContext) (*PredictionContext, bool) {
	
	// Do we have a map stored by k1?
	//
	m2, present := pcm.store.Get(k1)
	if present {
		// We found a map of values corresponding to k1, so now we need to look up k2 in that map
		//
		return m2.Get(k2)
	}
	return nil, false
}

func (pcm *JPCMap) Put(k1, k2, v *PredictionContext) {
	
	// First does a map already exist for k1?
	//
	if m2, present := pcm.store.Get(k1); present {
		_, present = m2.Put(k2, v)
		if !present {
			pcm.size++
		}
	} else {
		// No map found for k1, so we create it, add in our value, then store is
		//
		m2 = NewJMap[*PredictionContext, *PredictionContext, *ObjEqComparator[*PredictionContext]](pContextEqInst)
		m2.Put(k2, v)
		pcm.store.Put(k1, m2)
		pcm.size++
	}
	if pcm.size%100000 == 0 {
		fmt.Printf("JPCMap(%p) size  : %d\n", pcm, pcm.size)
	}
}
