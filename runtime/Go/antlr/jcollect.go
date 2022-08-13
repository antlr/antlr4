package antlr

import "sort"

// Collectable is an interface that a struct should implement if it is to be
// usable as a key in this collection. Cannot use the intuitive equals function
// here because the non-generic runtime has already claimed it.
type Collectable[T any] interface {
	hash() int
	gequals(other Collectable[T]) bool
}

type Comparator[T any] interface {
	hash(o T) int
	equals(T, T) bool
}

// JStore implements a container that allows the use of a struct to calculate the key
// for a collection of values akin to map. This is not meant to be a full-blown HashMap but just
// serve the needs of the ANTLR Go runtime.
//
// For ease of porting the logic of the runtime from the master target (Java), this collection
// operates in a similar way to Java, in that it can use any struct that supplies a hash() and equals()
// function as the key. The values are stored in a standard go map which internally is a form of hashmap
// itself, the key for the go map is the hash supplied by the key object. The collection is able to deal with
// hash conflicts by using a simple slice of values associated with the hash code indexed bucket. That isn't
// particularly efficient, but it is simple, and it works. As this is specifically for the ANTLR runtime, and
// we understand the requirements, then this is fine - this is not a general purpose collection.
type JStore[T any, C Comparator[T]] struct {
	store      map[int][]T
	len        int
	comparator Comparator[T]
}

func NewJStore[T any, C Comparator[T]](comparator Comparator[T]) *JStore[T, C] {

	if comparator == nil {
		panic("comparator cannot be nil")
	}

	s := &JStore[T, C]{
		store:      make(map[int][]T),
		comparator: comparator,
	}
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
func (s *JStore[T, C]) Put(value T) (v T, exists bool) { //nolint:ireturn

	kh := s.comparator.hash(value)

	for _, v := range s.store[kh] {
		if s.comparator.equals(value, v) {
			return v, true
		}
	}
	s.store[kh] = append(s.store[kh], value)
	s.len++
	return value, false
}

// Get will return the value associated with the key - the type of the key is the same type as the value
// which would not generally be useful, but this is a specific thing for ANTLR where the key is
// generated using the object we are going to store.
func (s *JStore[T, C]) Get(key T) (T, bool) { //nolint:ireturn

	kh := s.comparator.hash(key)

	for _, v := range s.store[kh] {
		if s.comparator.equals(key, v) {
			return v, true
		}
	}
	return key, false
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

func (s *JStore[T, C]) Len() int {
	return s.len
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
		store:      make(map[int][]*entry[K, V]),
		comparator: comparator,
	}
}

func (m *JMap[K, V, C]) Put(key K, val V) {
	kh := m.comparator.hash(key)
	m.store[kh] = append(m.store[kh], &entry[K, V]{key, val})
	m.len++
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
	kh := m.comparator.hash(key)
	for _, e := range m.store[kh] {
		if m.comparator.equals(e.key, key) {
			return e.val, true
		}
	}
	return none, false
}

func (m *JMap[K, V, C]) Len() int {
	return len(m.store)
}

func (m *JMap[K, V, C]) Delete(key K) {
	kh := m.comparator.hash(key)
	for i, e := range m.store[kh] {
		if m.comparator.equals(e.key, key) {
			m.store[kh] = append(m.store[kh][:i], m.store[kh][i+1:]...)
			m.len--
			return
		}
	}
}

func (m *JMap[K, V, C]) Clear() {
	m.store = make(map[int][]*entry[K, V])
}
