package antlr4
import (
	"math"
	"fmt"
)


//
// Specialized {@link Set}{@code <}{@link ATNConfig}{@code >} that can track
// info about the set, with support for combining similar configurations using a
// graph-structured stack.
///

func hashATNConfig(c *ATNConfig) string {
	return c.shortHashString()
}

func equalATNConfigs(a, b *ATNConfig) bool {
	if ( a==b ) {
		return true
	}
	if ( a==nil || b==nil ) {
		return false
	}
	return a.state.stateNumber==b.state.stateNumber && a.alt==b.alt && a.semanticContext.equals(b.semanticContext)
}

type ATNConfigSet struct {
	readOnly bool
	fullCtx bool
	configLookup Set
	conflictingAlts BitSet
	cachedHashString string
	hasSemanticContext bool
	dipsIntoOuterContext bool
	configs []*ATNConfig
	uniqueAlt int
}

func NewATNConfigSet(fullCtx bool) *ATNConfigSet {

	this := new(ATNConfigSet)

	this.InitATNConfigSet(fullCtx)

	return this
}

func (a *ATNConfigSet) InitATNConfigSet(fullCtx bool) {

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
	// Track the elements as they are added to the set supports get(i)///
	a.configs = make([]*ATNConfig)

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
func (this *ATNConfigSet) add(config *ATNConfig, mergeCache DoubleDict) bool {

	if (this.readOnly) {
		panic("This set is readonly")
	}
	if (config.semanticContext != SemanticContextNONE) {
		this.hasSemanticContext = true
	}
	if (config.reachesIntoOuterContext > 0) {
		this.dipsIntoOuterContext = true
	}
	var existing *ATNConfig = this.configLookup.add(config).(*ATNConfig)
	if (existing == config) {
		this.cachedHashString = "-1"
		this.configs = append(this.configs, config )// track order here
		return true
	}
	// a previous (s,i,pi,_), merge with it and save result
	var rootIsWildcard = !this.fullCtx
	var merged = merge(existing.context, config.context, rootIsWildcard, mergeCache)
	// no need to check for existing.context, config.context in cache
	// since only way to create Newgraphs is "call rule" and here. We
	// cache at both places.
	existing.reachesIntoOuterContext = math.Max( existing.reachesIntoOuterContext, config.reachesIntoOuterContext)
	// make sure to preserve the precedence filter suppression during the merge
	if (config.precedenceFilterSuppressed) {
		existing.precedenceFilterSuppressed = true
	}
	existing.context = merged // replace context no need to alt mapping
	return true
}

func (this *ATNConfigSet) getStates() {
	var states = NewSet(nil,nil)
	for i := 0; i < len(this.configs); i++ {
		states.add(this.configs[i].state)
	}
	return states
}

func (this *ATNConfigSet) getPredicates() []SemanticContext {
	var preds = make([]SemanticContext)
	for i := 0; i < len(this.configs); i++ {
		var c = this.configs[i].semanticContext
		if (c != SemanticContextNONE) {
			preds = append(preds, c.semanticContext)
		}
	}
	return preds
}

func (this *ATNConfigSet) getItems() []*ATNConfig {
	return this.configs
}

func (this *ATNConfigSet) optimizeConfigs(interpreter *ATNSimulator) {
	if (this.readOnly) {
		panic("This set is readonly")
	}
	if (this.configLookup.length == 0) {
		return
	}
	for i := 0; i < len(this.configs); i++ {
		var config = this.configs[i]
		config.context = interpreter.getCachedContext(config.context)
	}
}

func (this *ATNConfigSet) addAll(coll []*ATNConfig) bool{
	for i := 0; i < len(coll); i++ {
		this.add(coll[i])
	}
	return false
}

func (this *ATNConfigSet) equals(other interface{}) bool {
	if (this == other) {
		return true
	} else if _, ok := other.(*ATNConfigSet); !ok {
		return false
	}

	other2 := other.(*ATNConfigSet)
	return this.configs != nil &&
			this.configs.equals(other2.configs) &&
			this.fullCtx == other2.fullCtx &&
			this.uniqueAlt == other2.uniqueAlt &&
			this.conflictingAlts == other2.conflictingAlts &&
			this.hasSemanticContext == other2.hasSemanticContext &&
			this.dipsIntoOuterContext == other2.dipsIntoOuterContext
}

func (this *ATNConfigSet) hashString() string {
	if (this.readOnly) {
		if (this.cachedHashString == "-1") {
			this.cachedHashString = this.hashConfigs()
		}
		return this.cachedHashString
	} else {
		return this.hashConfigs()
	}
}

func (this *ATNConfigSet) hashConfigs() {
	var s = ""
	for _, c := range this.configs {
		s += fmt.Sprint(c)
	}
	return s
}

func (this *ATNConfigSet) length() int {
	return this.configs.length
}

func (this *ATNConfigSet) isEmpty() bool {
	return this.configs.length == 0
}

func (this *ATNConfigSet) contains(item *ATNConfig ) bool {
	if (this.configLookup == nil) {
		panic("This method is not implemented for readonly sets.")
	}
	return this.configLookup.contains(item)
}

func (this *ATNConfigSet) containsFast(item *ATNConfig ) bool {
	if (this.configLookup == nil) {
		panic("This method is not implemented for readonly sets.")
	}
	return this.configLookup.containsFast(item)
}

func (this *ATNConfigSet) clear() {
	if (this.readOnly) {
		panic("This set is readonly")
	}
	this.configs = make([]*ATNConfig)
	this.cachedHashString = "-1"
	this.configLookup = NewSet(hashATNConfig, equalATNConfigs)
}

func (this *ATNConfigSet) setReadonly(readOnly bool) {
	this.readOnly = readOnly
	if (readOnly) {
		this.configLookup = nil // can't mod, no need for lookup cache
	}
}

func (this *ATNConfigSet) toString() string {
	panic("not implemented")
	return ""
//	return Utils.arrayToString(this.configs) +
//		(this.hasSemanticContext ? ",hasSemanticContext=" + this.hasSemanticContext : "") +
//		(this.uniqueAlt != ATN.INVALID_ALT_NUMBER ? ",uniqueAlt=" + this.uniqueAlt : "") +
//		(this.conflictingAlts != nil ? ",conflictingAlts=" + this.conflictingAlts : "") +
//		(this.dipsIntoOuterContext ? ",dipsIntoOuterContext" : "")
}

type OrderedATNConfigSet struct {
	ATNConfigSet
}


func NewOrderedATNConfigSet() *OrderedATNConfigSet {

	this := new(OrderedATNConfigSet)

	this.InitATNConfigSet(false)
	this.configLookup = NewSet(nil, nil)

	return this
}



