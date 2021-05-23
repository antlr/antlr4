// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import "fmt"

// ATNConfigSet extends ATNConfig in the form of an unordered set.
type ATNConfigSet interface {
	hash() int
	Add(ATNConfig, *DoubleDict) bool
	AddAll([]ATNConfig) bool

	getStates() *set
	GetPredicates() []SemanticContext
	GetItems() []ATNConfig

	OptimizeConfigs(interpreter *BaseATNSimulator)

	Equals(other interface{}) bool

	Length() int
	IsEmpty() bool
	Contains(ATNConfig) bool
	ContainsFast(ATNConfig) bool
	Clear()
	String() string

	HasSemanticContext() bool
	SetHasSemanticContext(v bool)

	ReadOnly() bool
	SetReadOnly(bool)

	getConflictingAlts() *bitSet
	setConflictingAlts(*bitSet)

	FullContext() bool

	GetUniqueAlt() int
	SetUniqueAlt(int)

	GetDipsIntoOuterContext() bool
	SetDipsIntoOuterContext(bool)
}

// BaseATNConfigSet is a specialized set of ATNConfig that tracks information
// about its elements and can combine similar configurations using a
// graph-structured stack.
type BaseATNConfigSet struct {
	cachedHash int

	// configLookup is used to determine whether two BaseATNConfigSets are equal. We
	// need all configurations with the same (s, i, _, semctx) to be equal. A key
	// effectively doubles the number of objects associated with ATNConfigs. All
	// keys are hashed by (s, i, _, pi), not including the context. Wiped out when
	// read-only because a set becomes a DFA state.
	configLookup *set

	// configs is the added elements.
	configs []ATNConfig

	// TODO: These fields make me pretty uncomfortable, but it is nice to pack up
	// info together because it saves recomputation. Can we track conflicts as they
	// are added to save scanning configs later?
	conflictingAlts *bitSet

	// dipsIntoOuterContext is used by parsers and lexers. In a lexer, it indicates
	// we hit a pred while computing a closure operation. Do not make a DFA state
	// from the BaseATNConfigSet in this case. TODO: How is this used by parsers?
	dipsIntoOuterContext bool

	// fullCtx is whether it is part of a full context LL prediction. Used to
	// determine how to merge $. It is a wildcard with SLL, but not for an LL
	// context merge.
	fullCtx bool

	// Used in parser and lexer. In lexer, it indicates we hit a pred
	// while computing a closure operation. Don't make a DFA state from a.
	hasSemanticContext bool

	// readOnly is whether it is read-only. Do not
	// allow any code to manipulate the set if true because DFA states will point at
	// sets and those must not change. It not protect other fields; conflictingAlts
	// in particular, which is assigned after readOnly.
	readOnly bool

	// TODO: These fields make me pretty uncomfortable, but it is nice to pack up
	// info together because it saves recomputation. Can we track conflicts as they
	// are added to save scanning configs later?
	uniqueAlt int
}

// NewBaseATNConfigSet returns a new instance of BaseATNConfigSet.
func NewBaseATNConfigSet(fullCtx bool) *BaseATNConfigSet {
	return &BaseATNConfigSet{
		cachedHash:   -1,
		configLookup: newSet(nil, equalATNConfigs),
		fullCtx:      fullCtx,
	}
}

// Add merges contexts with existing configs for (s, i, pi, _), where s is the
// ATNConfig.state, i is the ATNConfig.alt, and pi is the
// ATNConfig.semanticContext. We use (s,i,pi) as the key. Updates
// dipsIntoOuterContext and hasSemanticContext when necessary.
func (b *BaseATNConfigSet) Add(config ATNConfig, mergeCache *DoubleDict) bool {
	if b.readOnly {
		panic("set is read-only")
	}

	if config.GetSemanticContext() != SemanticContextNone {
		b.hasSemanticContext = true
	}

	if config.GetReachesIntoOuterContext() > 0 {
		b.dipsIntoOuterContext = true
	}

	existing := b.configLookup.add(config).(ATNConfig)

	if existing == config {
		b.cachedHash = -1
		b.configs = append(b.configs, config) // Track order here

		return true
	}

	// Merge a previous (s, i, pi, _) with it and save the result
	rootIsWildcard := !b.fullCtx
	merged := merge(existing.GetContext(), config.GetContext(), rootIsWildcard, mergeCache)

	// No need to check for existing.context because config.context is in the cache,
	// since the only way to create new graphs is the "call rule" and here. We cache
	// at both places.
	existing.SetReachesIntoOuterContext(intMax(existing.GetReachesIntoOuterContext(), config.GetReachesIntoOuterContext()))

	// Preserve the precedence filter suppression during the merge
	if config.getPrecedenceFilterSuppressed() {
		existing.setPrecedenceFilterSuppressed(true)
	}

	// Replace the context because there is no need to do alt mapping
	existing.SetContext(merged)

	return true
}

// getStates returns the config states contained in this object.
func (b *BaseATNConfigSet) getStates() *set {
	states := newSet(nil, nil)

	for i := 0; i < len(b.configs); i++ {
		states.add(b.configs[i].GetState())
	}

	return states
}

// HasSemanticContext returns true if this config has a semantic context.
func (b *BaseATNConfigSet) HasSemanticContext() bool {
	return b.hasSemanticContext
}

// SetHasSemanticContext sets whether this config has a semantic context.
func (b *BaseATNConfigSet) SetHasSemanticContext(v bool) {
	b.hasSemanticContext = v
}

// GetPredicates returns a slice with the predicates of this object's elements.
func (b *BaseATNConfigSet) GetPredicates() []SemanticContext {
	preds := make([]SemanticContext, 0)

	for i := 0; i < len(b.configs); i++ {
		c := b.configs[i].GetSemanticContext()

		if c != SemanticContextNone {
			preds = append(preds, c)
		}
	}

	return preds
}

// GetItems returns the configurations contained in this object.
func (b *BaseATNConfigSet) GetItems() []ATNConfig {
	return b.configs
}

// OptimizeConfigs optimizes the elements in this object by retrieving the
// cached contexts in the given interpreter. The method will panic if the
// object is read-only.
func (b *BaseATNConfigSet) OptimizeConfigs(interpreter *BaseATNSimulator) {
	if b.readOnly {
		panic("set is read-only")
	}

	if b.configLookup.length() == 0 {
		return
	}

	for _, config := range b.configs {
		config.SetContext(interpreter.getCachedContext(config.GetContext()))
	}
}

// AddAll adds all the elements in the given slice.
func (b *BaseATNConfigSet) AddAll(coll []ATNConfig) bool {
	for i := 0; i < len(coll); i++ {
		b.Add(coll[i], nil)
	}

	return false
}

// Equals returns true if the given element is equal to this one.
func (b *BaseATNConfigSet) Equals(other interface{}) bool {
	if b == other {
		return true
	} else if _, ok := other.(*BaseATNConfigSet); !ok {
		return false
	}

	other2 := other.(*BaseATNConfigSet)

	return b.configs != nil &&
		// TODO: b.configs.equals(other2.configs) && // TODO: Is b necessary?
		b.fullCtx == other2.fullCtx &&
		b.uniqueAlt == other2.uniqueAlt &&
		b.conflictingAlts == other2.conflictingAlts &&
		b.hasSemanticContext == other2.hasSemanticContext &&
		b.dipsIntoOuterContext == other2.dipsIntoOuterContext
}

func (b *BaseATNConfigSet) hash() int {
	if b.readOnly {
		if b.cachedHash == -1 {
			b.cachedHash = b.hashCodeConfigs()
		}

		return b.cachedHash
	}

	return b.hashCodeConfigs()
}

func (b *BaseATNConfigSet) hashCodeConfigs() int {
	h := murmurInit(1)
	for _, c := range b.configs {
		if c != nil {
			h = murmurUpdate(h, c.hash())
		}
	}
	return murmurFinish(h, len(b.configs))
}

// Length returns the number of configs in this object.
func (b *BaseATNConfigSet) Length() int {
	return len(b.configs)
}

// IsEmpty returns true if this object doesn't contain any configs.
func (b *BaseATNConfigSet) IsEmpty() bool {
	return len(b.configs) == 0
}

// Contains returns true if the given item is contained in this set.
func (b *BaseATNConfigSet) Contains(item ATNConfig) bool {
	if b.configLookup == nil {
		panic("not implemented for read-only sets")
	}

	return b.configLookup.contains(item)
}

// ContainsFast returns true if the given item is contained in this set.
func (b *BaseATNConfigSet) ContainsFast(item ATNConfig) bool {
	if b.configLookup == nil {
		panic("not implemented for read-only sets")
	}

	return b.configLookup.contains(item) // TODO: containsFast is not implemented for Set
}

// Clear removes all contained objects from this set.
func (b *BaseATNConfigSet) Clear() {
	if b.readOnly {
		panic("set is read-only")
	}

	b.configs = make([]ATNConfig, 0)
	b.cachedHash = -1
	b.configLookup = newSet(nil, equalATNConfigs)
}

// FullContext returns true if this set is a full context
func (b *BaseATNConfigSet) FullContext() bool {
	return b.fullCtx
}

// GetDipsIntoOuterContext returns whether this set dips into its outer
// context.
func (b *BaseATNConfigSet) GetDipsIntoOuterContext() bool {
	return b.dipsIntoOuterContext
}

// SetDipsIntoOuterContext sets whether this set dips into it's outer context.
func (b *BaseATNConfigSet) SetDipsIntoOuterContext(v bool) {
	b.dipsIntoOuterContext = v
}

// TODO: This *seems* to be what it does, based on the usage

// GetUniqueAlt returns this set's unique alternative. This number identifies
// this alternative as distinct.
func (b *BaseATNConfigSet) GetUniqueAlt() int {
	return b.uniqueAlt
}

// SetUniqueAlt sets the unique alternative for this set.This number identifies
// this alternative as distinct.
func (b *BaseATNConfigSet) SetUniqueAlt(v int) {
	b.uniqueAlt = v
}

// getConflictingAlts returns the conflicting alternatives in this config.
func (b *BaseATNConfigSet) getConflictingAlts() *bitSet {
	return b.conflictingAlts
}

// setConflictingAlts sets the conflicting alternatives in this config.
func (b *BaseATNConfigSet) setConflictingAlts(v *bitSet) {
	b.conflictingAlts = v
}

// ReadOnly returns whether this set is read-only.
func (b *BaseATNConfigSet) ReadOnly() bool {
	return b.readOnly
}

// SetReadOnly controls whether this set is read-only.
func (b *BaseATNConfigSet) SetReadOnly(readOnly bool) {
	b.readOnly = readOnly

	if readOnly {
		b.configLookup = nil // Read only, so no need for the lookup cache
	}
}

// String implements the stringer interface. The returned string has the format:
//
//		[<configs>],<flags>
func (b *BaseATNConfigSet) String() string {
	s := "["

	for i, c := range b.configs {
		s += c.String()

		if i != len(b.configs)-1 {
			s += ", "
		}
	}

	s += "]"

	if b.hasSemanticContext {
		s += ",hasSemanticContext=" + fmt.Sprint(b.hasSemanticContext)
	}

	if b.uniqueAlt != ATNInvalidAltNumber {
		s += ",uniqueAlt=" + fmt.Sprint(b.uniqueAlt)
	}

	if b.conflictingAlts != nil {
		s += ",conflictingAlts=" + b.conflictingAlts.String()
	}

	if b.dipsIntoOuterContext {
		s += ",dipsIntoOuterContext"
	}

	return s
}

// OrderedATNConfigSet extends BaseATNConfigSet.
type OrderedATNConfigSet struct {
	*BaseATNConfigSet
}

// NewOrderedATNConfigSet returns a new instance of OrderedATNConfigSet.
func NewOrderedATNConfigSet() *OrderedATNConfigSet {
	b := NewBaseATNConfigSet(false)

	b.configLookup = newSet(nil, nil)

	return &OrderedATNConfigSet{BaseATNConfigSet: b}
}

func equalATNConfigs(a, b interface{}) bool {
	if a == nil || b == nil {
		return false
	}

	if a == b {
		return true
	}

	var ai, ok = a.(ATNConfig)
	var bi, ok1 = b.(ATNConfig)

	if !ok || !ok1 {
		return false
	}

	nums := ai.GetState().GetStateNumber() == bi.GetState().GetStateNumber()
	alts := ai.GetAlt() == bi.GetAlt()
	cons := ai.GetSemanticContext().equals(bi.GetSemanticContext())

	return nums && alts && cons
}
