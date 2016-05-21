package antlr4

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
func (this *BaseATNConfigSet) Add(config ATNConfig, mergeCache *DoubleDict) bool {

	if this.readOnly {
		panic("This set is readonly")
	}
	if config.GetSemanticContext() != SemanticContextNone {
		this.hasSemanticContext = true
	}
	if config.GetReachesIntoOuterContext() > 0 {
		this.dipsIntoOuterContext = true
	}
	var existing = this.configLookup.add(config).(ATNConfig)
	if existing == config {
		this.cachedHashString = "-1"
		this.configs = append(this.configs, config) // track order here
		return true
	}
	// a previous (s,i,pi,_), merge with it and save result
	var rootIsWildcard = !this.fullCtx
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

func (this *BaseATNConfigSet) GetStates() *Set {
	var states = NewSet(nil, nil)
	for i := 0; i < len(this.configs); i++ {
		states.add(this.configs[i].GetState())
	}
	return states
}

func (this *BaseATNConfigSet) HasSemanticContext() bool {
	return this.hasSemanticContext
}

func (this *BaseATNConfigSet) SetHasSemanticContext(v bool) {
	this.hasSemanticContext = v
}

func (this *BaseATNConfigSet) GetPredicates() []SemanticContext {
	var preds = make([]SemanticContext, 0)
	for i := 0; i < len(this.configs); i++ {
		c := this.configs[i].GetSemanticContext()
		if c != SemanticContextNone {
			preds = append(preds, c)
		}
	}
	return preds
}

func (this *BaseATNConfigSet) GetItems() []ATNConfig {
	return this.configs
}

func (this *BaseATNConfigSet) OptimizeConfigs(interpreter *BaseATNSimulator) {
	if this.readOnly {
		panic("This set is readonly")
	}
	if this.configLookup.length() == 0 {
		return
	}
	for i := 0; i < len(this.configs); i++ {
		var config = this.configs[i]
		config.SetContext(interpreter.getCachedContext(config.GetContext()))
	}
}

func (this *BaseATNConfigSet) AddAll(coll []ATNConfig) bool {
	for i := 0; i < len(coll); i++ {
		this.Add(coll[i], nil)
	}
	return false
}

func (this *BaseATNConfigSet) Equals(other interface{}) bool {
	if this == other {
		return true
	} else if _, ok := other.(*BaseATNConfigSet); !ok {
		return false
	}

	other2 := other.(*BaseATNConfigSet)

	return this.configs != nil &&
		//			this.configs.equals(other2.configs) && // TODO is this necessary?
		this.fullCtx == other2.fullCtx &&
		this.uniqueAlt == other2.uniqueAlt &&
		this.conflictingAlts == other2.conflictingAlts &&
		this.hasSemanticContext == other2.hasSemanticContext &&
		this.dipsIntoOuterContext == other2.dipsIntoOuterContext
}

func (this *BaseATNConfigSet) Hash() string {
	if this.readOnly {
		if this.cachedHashString == "-1" {
			this.cachedHashString = this.hashConfigs()
		}
		return this.cachedHashString
	} else {
		return this.hashConfigs()
	}
}

func (this *BaseATNConfigSet) hashConfigs() string {
	var s = ""
	for _, c := range this.configs {
		s += fmt.Sprint(c)
	}
	return s
}

func (this *BaseATNConfigSet) Length() int {
	return len(this.configs)
}

func (this *BaseATNConfigSet) IsEmpty() bool {
	return len(this.configs) == 0
}

func (this *BaseATNConfigSet) Contains(item ATNConfig) bool {
	if this.configLookup == nil {
		panic("This method is not implemented for readonly sets.")
	}
	return this.configLookup.contains(item)
}

func (this *BaseATNConfigSet) ContainsFast(item ATNConfig) bool {
	if this.configLookup == nil {
		panic("This method is not implemented for readonly sets.")
	}
	return this.configLookup.contains(item) // TODO containsFast is not implemented for Set
}

func (this *BaseATNConfigSet) Clear() {
	if this.readOnly {
		panic("This set is readonly")
	}
	this.configs = make([]ATNConfig, 0)
	this.cachedHashString = "-1"
	this.configLookup = NewSet(hashATNConfig, equalATNConfigs)
}

func (this *BaseATNConfigSet) FullContext() bool {
	return this.fullCtx
}

func (this *BaseATNConfigSet) GetDipsIntoOuterContext() bool {
	return this.dipsIntoOuterContext
}

func (this *BaseATNConfigSet) SetDipsIntoOuterContext(v bool) {
	this.dipsIntoOuterContext = v
}

func (this *BaseATNConfigSet) GetUniqueAlt() int {
	return this.uniqueAlt
}

func (this *BaseATNConfigSet) SetUniqueAlt(v int) {
	this.uniqueAlt = v
}

func (this *BaseATNConfigSet) GetConflictingAlts() *BitSet {
	return this.conflictingAlts
}

func (this *BaseATNConfigSet) SetConflictingAlts(v *BitSet) {
	this.conflictingAlts = v
}

func (this *BaseATNConfigSet) ReadOnly() bool {
	return this.readOnly
}

func (this *BaseATNConfigSet) SetReadOnly(readOnly bool) {
	this.readOnly = readOnly
	if readOnly {
		this.configLookup = nil // can't mod, no need for lookup cache
	}
}

func (this *BaseATNConfigSet) String() string {
	s := "["

	for i, c := range this.configs {
		s += c.String()
		if i != len(this.configs)-1 {
			s += ", "
		}
	}

	s += "]"

	if this.hasSemanticContext {
		s += ",hasSemanticContext=" + fmt.Sprint(this.hasSemanticContext)
	}

	if this.uniqueAlt != ATNInvalidAltNumber {
		s += ",uniqueAlt=" + fmt.Sprint(this.uniqueAlt)
	}

	if this.conflictingAlts != nil {
		s += ",conflictingAlts=" + this.conflictingAlts.String()
	}

	if this.dipsIntoOuterContext {
		s += ",dipsIntoOuterContext"
	}

	return s
}

type OrderedATNConfigSet struct {
	*BaseATNConfigSet
}

func NewOrderedATNConfigSet() *OrderedATNConfigSet {

	this := new(OrderedATNConfigSet)

	this.BaseATNConfigSet = NewBaseATNConfigSet(false)
	this.configLookup = NewSet(nil, nil)

	return this
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
