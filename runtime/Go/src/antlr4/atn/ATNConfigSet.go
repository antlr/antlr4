package atn

//
// Specialized {@link Set}{@code <}{@link ATNConfig}{@code >} that can track
// info about the set, with support for combining similar configurations using a
// graph-structured stack.
///

//var ATN = require('./ATN').ATN
//var Utils = require('./../Utils')
var Set = Utils.Set
//var SemanticContext = require('./SemanticContext').SemanticContext
//var merge = require('./../PredictionContext').merge

func hashATNConfig(c) {
	return c.shortHashString()
}

func equalATNConfigs(a, b) {
	if ( a==b ) {
		return true
	}
	if ( a==nil || b==nil ) {
		return false
	}
	return a.state.stateNumber==b.state.stateNumber &&
		a.alt==b.alt && a.semanticContext.equals(b.semanticContext)
}


func ATNConfigSet(fullCtx) {
	//
	// The reason that we need this is because we don't want the hash map to use
	// the standard hash code and equals. We need all configurations with the
	// same
	// {@code (s,i,_,semctx)} to be equal. Unfortunately, this key effectively
	// doubles
	// the number of objects associated with ATNConfigs. The other solution is
	// to
	// use a hash table that lets us specify the equals/hashcode operation.
	// All configs but hashed by (s, i, _, pi) not including context. Wiped out
	// when we go readonly as this set becomes a DFA state.
	this.configLookup = NewSet(hashATNConfig, equalATNConfigs)
	// Indicates that this configuration set is part of a full context
	// LL prediction. It will be used to determine how to merge $. With SLL
	// it's a wildcard whereas it is not for LL context merge.
	this.fullCtx = fullCtx == undefined ? true : fullCtx
	// Indicates that the set of configurations is read-only. Do not
	// allow any code to manipulate the set DFA states will point at
	// the sets and they must not change. This does not protect the other
	// fields in particular, conflictingAlts is set after
	// we've made this readonly.
	this.readOnly = false
	// Track the elements as they are added to the set supports get(i)///
	this.configs = []

	// TODO: these fields make me pretty uncomfortable but nice to pack up info
	// together, saves recomputation
	// TODO: can we track conflicts as they are added to save scanning configs
	// later?
	this.uniqueAlt = 0
	this.conflictingAlts = nil

	// Used in parser and lexer. In lexer, it indicates we hit a pred
	// while computing a closure operation. Don't make a DFA state from this.
	this.hasSemanticContext = false
	this.dipsIntoOuterContext = false

	this.cachedHashString = "-1"

	return this
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
func (this *ATNConfigSet) add(config, mergeCache) {
	if (mergeCache == undefined) {
		mergeCache = nil
	}
	if (this.readOnly) {
		panic "This set is readonly"
	}
	if (config.semanticContext != SemanticContext.NONE) {
		this.hasSemanticContext = true
	}
	if (config.reachesIntoOuterContext > 0) {
		this.dipsIntoOuterContext = true
	}
	var existing = this.configLookup.add(config)
	if (existing == config) {
		this.cachedHashString = "-1"
		this.configs.push(config) // track order here
		return true
	}
	// a previous (s,i,pi,_), merge with it and save result
	var rootIsWildcard = !this.fullCtx
	var merged = merge(existing.context, config.context, rootIsWildcard, mergeCache)
	// no need to check for existing.context, config.context in cache
	// since only way to create Newgraphs is "call rule" and here. We
	// cache at both places.
	existing.reachesIntoOuterContext = Math.max( existing.reachesIntoOuterContext, config.reachesIntoOuterContext)
	// make sure to preserve the precedence filter suppression during the merge
	if (config.precedenceFilterSuppressed) {
		existing.precedenceFilterSuppressed = true
	}
	existing.context = merged // replace context no need to alt mapping
	return true
}

func (this *ATNConfigSet) getStates() {
	var states = NewSet()
	for (var i = 0 i < this.configs.length i++) {
		states.add(this.configs[i].state)
	}
	return states
}

func (this *ATNConfigSet) getPredicates() {
	var preds = []
	for (var i = 0 i < this.configs.length i++) {
		var c = this.configs[i].semanticContext
		if (c != SemanticContext.NONE) {
			preds.push(c.semanticContext)
		}
	}
	return preds
}

Object.defineProperty(ATNConfigSet.prototype, "items", {
	get : function() {
		return this.configs
	}
})

func (this *ATNConfigSet) optimizeConfigs(interpreter) {
	if (this.readOnly) {
		panic "This set is readonly"
	}
	if (this.configLookup.length == 0) {
		return
	}
	for (var i = 0 i < this.configs.length i++) {
		var config = this.configs[i]
		config.context = interpreter.getCachedContext(config.context)
	}
}

func (this *ATNConfigSet) addAll(coll) {
	for (var i = 0 i < coll.length i++) {
		this.add(coll[i])
	}
	return false
}

func (this *ATNConfigSet) equals(other) {
	if (this == other) {
		return true
	} else if (!(other instanceof ATNConfigSet)) {
		return false
	}
	return this.configs != nil && this.configs.equals(other.configs) &&
			this.fullCtx == other.fullCtx &&
			this.uniqueAlt == other.uniqueAlt &&
			this.conflictingAlts == other.conflictingAlts &&
			this.hasSemanticContext == other.hasSemanticContext &&
			this.dipsIntoOuterContext == other.dipsIntoOuterContext
}

func (this *ATNConfigSet) hashString() {
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
	this.configs.map(function(c) {
		s += c.toString()
	})
	return s
}

Object.defineProperty(ATNConfigSet.prototype, "length", {
	get : function() {
		return this.configs.length
	}
})

func (this *ATNConfigSet) isEmpty() {
	return this.configs.length == 0
}

func (this *ATNConfigSet) contains(item) {
	if (this.configLookup == nil) {
		panic "This method is not implemented for readonly sets."
	}
	return this.configLookup.contains(item)
}

func (this *ATNConfigSet) containsFast(item) {
	if (this.configLookup == nil) {
		panic "This method is not implemented for readonly sets."
	}
	return this.configLookup.containsFast(item)
}

func (this *ATNConfigSet) clear() {
	if (this.readOnly) {
		panic "This set is readonly"
	}
	this.configs = []
	this.cachedHashString = "-1"
	this.configLookup = NewSet()
}

func (this *ATNConfigSet) setReadonly(readOnly) {
	this.readOnly = readOnly
	if (readOnly) {
		this.configLookup = nil // can't mod, no need for lookup cache
	}
}

func (this *ATNConfigSet) toString() {
	return Utils.arrayToString(this.configs) +
		(this.hasSemanticContext ? ",hasSemanticContext=" + this.hasSemanticContext : "") +
		(this.uniqueAlt != ATN.INVALID_ALT_NUMBER ? ",uniqueAlt=" + this.uniqueAlt : "") +
		(this.conflictingAlts != nil ? ",conflictingAlts=" + this.conflictingAlts : "") +
		(this.dipsIntoOuterContext ? ",dipsIntoOuterContext" : "")
}

type OrderedATNConfigSet struct {
	ATNConfigSet.call(this)
	this.configLookup = NewSet()
	return this
}

//OrderedATNConfigSet.prototype = Object.create(ATNConfigSet.prototype)
//OrderedATNConfigSet.prototype.constructor = OrderedATNConfigSet



