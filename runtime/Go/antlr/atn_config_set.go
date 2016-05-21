package antlr

import (
	"fmt"
)

type ATNConfigSet interface {
	Hasher

	Add(ATNConfig, *DoubleDict) bool
	AddAll([]ATNConfig) bool

	GetStates() *Set
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

	GetConflictingAlts() *BitSet
	SetConflictingAlts(*BitSet)

	FullContext() bool

	GetUniqueAlt() int
	SetUniqueAlt(int)

	GetDipsIntoOuterContext() bool
	SetDipsIntoOuterContext(bool)
}

// Specialized {@link Set}{@code <}{@link ATNConfig}{@code >} that can track
// info about the set, with support for combining similar configurations using a
// graph-structured stack.

type BaseATNConfigSet struct {
	readOnly             bool
	fullCtx              bool
	configLookup         *Set
	conflictingAlts      *BitSet
	cachedHashString     string
	hasSemanticContext   bool
	dipsIntoOuterContext bool
	configs              []ATNConfig
	uniqueAlt            int
}

func NewBaseATNConfigSet(fullCtx bool) *BaseATNConfigSet {

	a := new(BaseATNConfigSet)

	// The reason that we need a.is because we don't want the hash map to use
	// the standard hash code and equals. We need all configurations with the
	// same
	// {@code (s,i,_,semctx)} to be equal. Unfortunately, a.key effectively
	// doubles
	// the number of objects associated with ATNConfigs. The other solution is
	// to
	// use a hash table that lets us specify the equals/hashcode operation.
	// All configs but hashed by (s, i, _, pi) not including context. Wiped out
	// when we go readonly as a.set becomes a DFA state.
	a.configLookup = NewSet(hashATNConfig, equalATNConfigs)
	// Indicates that a.configuration set is part of a full context
	// LL prediction. It will be used to determine how to merge $. With SLL
	// it's a wildcard whereas it is not for LL context merge.
	a.fullCtx = fullCtx
	// Indicates that the set of configurations is read-only. Do not
	// allow any code to manipulate the set DFA states will point at
	// the sets and they must not change. a.does not protect the other
	// fields in particular, conflictingAlts is set after
	// we've made a.readonly.
	a.readOnly = false
	// Track the elements as they are added to the set supports Get(i)///
	a.configs = make([]ATNConfig, 0)

	// TODO: these fields make me pretty uncomfortable but nice to pack up info
	// together, saves recomputation
	// TODO: can we track conflicts as they are added to save scanning configs
	// later?
	a.uniqueAlt = 0
	a.conflictingAlts = nil

	// Used in parser and lexer. In lexer, it indicates we hit a pred
	// while computing a closure operation. Don't make a DFA state from a.
	a.hasSemanticContext = false
	a.dipsIntoOuterContext = false

	a.cachedHashString = "-1"

	return a
}

// Adding a Newconfig means merging contexts with existing configs for
// {@code (s, i, pi, _)}, where {@code s} is the
// {@link ATNConfig//state}, {@code i} is the {@link ATNConfig//alt}, and
// {@code pi} is the {@link ATNConfig//semanticContext}. We use
// {@code (s,i,pi)} as key.
//
// <p>This method updates {@link //dipsIntoOuterContext} and
// {@link //hasSemanticContext} when necessary.</p>
// /
func (b *BaseATNConfigSet) Add(config ATNConfig, mergeCache *DoubleDict) bool {

	if b.readOnly {
		panic("This set is readonly")
	}
	if config.GetSemanticContext() != SemanticContextNone {
		b.hasSemanticContext = true
	}
	if config.GetReachesIntoOuterContext() > 0 {
		b.dipsIntoOuterContext = true
	}
	var existing = b.configLookup.add(config).(ATNConfig)
	if existing == config {
		b.cachedHashString = "-1"
		b.configs = append(b.configs, config) // track order here
		return true
	}
	// a previous (s,i,pi,_), merge with it and save result
	var rootIsWildcard = !b.fullCtx
	var merged = merge(existing.GetContext(), config.GetContext(), rootIsWildcard, mergeCache)
	// no need to check for existing.context, config.context in cache
	// since only way to create Newgraphs is "call rule" and here. We
	// cache at both places.
	existing.SetReachesIntoOuterContext(intMax(existing.GetReachesIntoOuterContext(), config.GetReachesIntoOuterContext()))
	// make sure to preserve the precedence filter suppression during the merge
	if config.getPrecedenceFilterSuppressed() {
		existing.setPrecedenceFilterSuppressed(true)
	}
	existing.SetContext(merged) // replace context no need to alt mapping

	return true
}

func (b *BaseATNConfigSet) GetStates() *Set {
	var states = NewSet(nil, nil)
	for i := 0; i < len(b.configs); i++ {
		states.add(b.configs[i].GetState())
	}
	return states
}

func (b *BaseATNConfigSet) HasSemanticContext() bool {
	return b.hasSemanticContext
}

func (b *BaseATNConfigSet) SetHasSemanticContext(v bool) {
	b.hasSemanticContext = v
}

func (b *BaseATNConfigSet) GetPredicates() []SemanticContext {
	var preds = make([]SemanticContext, 0)
	for i := 0; i < len(b.configs); i++ {
		c := b.configs[i].GetSemanticContext()
		if c != SemanticContextNone {
			preds = append(preds, c)
		}
	}
	return preds
}

func (b *BaseATNConfigSet) GetItems() []ATNConfig {
	return b.configs
}

func (b *BaseATNConfigSet) OptimizeConfigs(interpreter *BaseATNSimulator) {
	if b.readOnly {
		panic("This set is readonly")
	}
	if b.configLookup.length() == 0 {
		return
	}
	for i := 0; i < len(b.configs); i++ {
		var config = b.configs[i]
		config.SetContext(interpreter.getCachedContext(config.GetContext()))
	}
}

func (b *BaseATNConfigSet) AddAll(coll []ATNConfig) bool {
	for i := 0; i < len(coll); i++ {
		b.Add(coll[i], nil)
	}
	return false
}

func (b *BaseATNConfigSet) Equals(other interface{}) bool {
	if b == other {
		return true
	} else if _, ok := other.(*BaseATNConfigSet); !ok {
		return false
	}

	other2 := other.(*BaseATNConfigSet)

	return b.configs != nil &&
		//			b.configs.equals(other2.configs) && // TODO is b necessary?
		b.fullCtx == other2.fullCtx &&
		b.uniqueAlt == other2.uniqueAlt &&
		b.conflictingAlts == other2.conflictingAlts &&
		b.hasSemanticContext == other2.hasSemanticContext &&
		b.dipsIntoOuterContext == other2.dipsIntoOuterContext
}

func (b *BaseATNConfigSet) Hash() string {
	if b.readOnly {
		if b.cachedHashString == "-1" {
			b.cachedHashString = b.hashConfigs()
		}
		return b.cachedHashString
	}

	return b.hashConfigs()
}

func (b *BaseATNConfigSet) hashConfigs() string {
	var s = ""
	for _, c := range b.configs {
		s += fmt.Sprint(c)
	}
	return s
}

func (b *BaseATNConfigSet) Length() int {
	return len(b.configs)
}

func (b *BaseATNConfigSet) IsEmpty() bool {
	return len(b.configs) == 0
}

func (b *BaseATNConfigSet) Contains(item ATNConfig) bool {
	if b.configLookup == nil {
		panic("This method is not implemented for readonly sets.")
	}
	return b.configLookup.contains(item)
}

func (b *BaseATNConfigSet) ContainsFast(item ATNConfig) bool {
	if b.configLookup == nil {
		panic("This method is not implemented for readonly sets.")
	}
	return b.configLookup.contains(item) // TODO containsFast is not implemented for Set
}

func (b *BaseATNConfigSet) Clear() {
	if b.readOnly {
		panic("This set is readonly")
	}
	b.configs = make([]ATNConfig, 0)
	b.cachedHashString = "-1"
	b.configLookup = NewSet(hashATNConfig, equalATNConfigs)
}

func (b *BaseATNConfigSet) FullContext() bool {
	return b.fullCtx
}

func (b *BaseATNConfigSet) GetDipsIntoOuterContext() bool {
	return b.dipsIntoOuterContext
}

func (b *BaseATNConfigSet) SetDipsIntoOuterContext(v bool) {
	b.dipsIntoOuterContext = v
}

func (b *BaseATNConfigSet) GetUniqueAlt() int {
	return b.uniqueAlt
}

func (b *BaseATNConfigSet) SetUniqueAlt(v int) {
	b.uniqueAlt = v
}

func (b *BaseATNConfigSet) GetConflictingAlts() *BitSet {
	return b.conflictingAlts
}

func (b *BaseATNConfigSet) SetConflictingAlts(v *BitSet) {
	b.conflictingAlts = v
}

func (b *BaseATNConfigSet) ReadOnly() bool {
	return b.readOnly
}

func (b *BaseATNConfigSet) SetReadOnly(readOnly bool) {
	b.readOnly = readOnly
	if readOnly {
		b.configLookup = nil // can't mod, no need for lookup cache
	}
}

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

type OrderedATNConfigSet struct {
	*BaseATNConfigSet
}

func NewOrderedATNConfigSet() *OrderedATNConfigSet {

	o := new(OrderedATNConfigSet)

	o.BaseATNConfigSet = NewBaseATNConfigSet(false)
	o.configLookup = NewSet(nil, nil)

	return o
}

func hashATNConfig(c interface{}) string {
	return c.(ATNConfig).shortHash()
}

func equalATNConfigs(a, b interface{}) bool {

	if a == nil || b == nil {
		return false
	}

	if a == b {
		return true
	}

	ai, ok := a.(ATNConfig)
	bi, ok1 := b.(ATNConfig)

	if !ok || !ok1 {
		return false
	}

	return ai.GetState().GetStateNumber() == bi.GetState().GetStateNumber() &&
		ai.GetAlt() == bi.GetAlt() &&
		ai.GetSemanticContext().equals(bi.GetSemanticContext())
}
