package antlr4

//var RuleContext = require('./RuleContext').RuleContext

type PredictionContext struct {
	cachedHashString string
}

func NewPredictionContext(cachedHashString string) *PredictionContext {

	pc := new(PredictionContext)

	pc.cachedHashString = cachedHashString

	return pc
}

// Represents {@code $} in local context prediction, which means wildcard.
// {@code//+x =//}.
// /
const (
	PredictionContext.EMPTY = nil
)


// Represents {@code $} in an array in full context mode, when {@code $}
// doesn't mean wildcard: {@code $ + x = [$,x]}. Here,
// {@code $} = {@link //EMPTY_RETURN_STATE}.
// /
PredictionContext.EMPTY_RETURN_STATE = 0x7FFFFFFF

PredictionContext.globalNodeCount = 1
PredictionContext.id = PredictionContext.globalNodeCount

// Stores the computed hash code of this {@link PredictionContext}. The hash
// code is computed in parts to match the following reference algorithm.
//
// <pre>
// private int referenceHashCode() {
// int hash = {@link MurmurHash//initialize MurmurHash.initialize}({@link
// //INITIAL_HASH})
//
// for (int i = 0 i &lt {@link //size()} i++) {
// hash = {@link MurmurHash//update MurmurHash.update}(hash, {@link //getParent
// getParent}(i))
// }
//
// for (int i = 0 i &lt {@link //size()} i++) {
// hash = {@link MurmurHash//update MurmurHash.update}(hash, {@link
// //getReturnState getReturnState}(i))
// }
//
// hash = {@link MurmurHash//finish MurmurHash.finish}(hash, 2// {@link
// //size()})
// return hash
// }
// </pre>
// /

// This means only the {@link //EMPTY} context is in set.
func (this *PredictionContext) isEmpty() {
	return this == PredictionContext.EMPTY
}

func (this *PredictionContext) hasEmptyPath() {
	return this.getReturnState(this.length - 1) == PredictionContext.EMPTY_RETURN_STATE
}

func (this *PredictionContext) hashString() {
	return this.cachedHashString
}

func calculateHashString(parent, returnState) {
	return "" + parent + returnState
}

type calculateEmptyHashString struct {
	return ""
}

// Used to cache {@link PredictionContext} objects. Its used for the shared
// context cash associated with contexts in DFA states. This cache
// can be used for both lexers and parsers.

type PredictionContextCache struct {
	this.cache = {}
	return this
}

// Add a context to the cache and return it. If the context already exists,
// return that one instead and do not add a Newcontext to the cache.
// Protect shared cache from unsafe thread access.
//
func (this *PredictionContextCache) add(ctx) {
	if (ctx == PredictionContext.EMPTY) {
		return PredictionContext.EMPTY
	}
	var existing = this.cache[ctx] || nil
	if (existing != nil) {
		return existing
	}
	this.cache[ctx] = ctx
	return ctx
}

func (this *PredictionContextCache) get(ctx) {
	return this.cache[ctx] || nil
}

Object.defineProperty(PredictionContextCache.prototype, "length", {
	get : function() {
		return this.cache.length
	}
})

func SingletonPredictionContext(parent, returnState) {
	var hashString = parent != nil ? calculateHashString(parent, returnState)
			: calculateEmptyHashString()
	PredictionContext.call(this, hashString)
	this.parentCtx = parent
	this.returnState = returnState
}

//SingletonPredictionContext.prototype = Object.create(PredictionContext.prototype)
SingletonPredictionContext.prototype.contructor = SingletonPredictionContext

SingletonPredictionContext.create = function(parent, returnState) {
	if (returnState == PredictionContext.EMPTY_RETURN_STATE && parent == nil) {
		// someone can pass in the bits of an array ctx that mean $
		return PredictionContext.EMPTY
	} else {
		return NewSingletonPredictionContext(parent, returnState)
	}
}

Object.defineProperty(SingletonPredictionContext.prototype, "length", {
	get : function() {
		return 1
	}
})

func (this *SingletonPredictionContext) getParent(index) {
	return this.parentCtx
}

func (this *SingletonPredictionContext) getReturnState(index) {
	return this.returnState
}

func (this *SingletonPredictionContext) equals(other) {
	if (this == other) {
		return true
	} else if (!_, ok := other.(SingletonPredictionContext); ok) {
		return false
	} else if (this.hashString() != other.hashString()) {
		return false // can't be same if hash is different
	} else {
		if(this.returnState != other.returnState)
            return false
        else if(this.parentCtx==nil)
            return other.parentCtx==nil
		else
            return this.parentCtx.equals(other.parentCtx)
	}
}

func (this *SingletonPredictionContext) hashString() {
	return this.cachedHashString
}

func (this *SingletonPredictionContext) toString() string {
	var up = this.parentCtx == nil ? "" : this.parentCtx.toString()
	if (up.length == 0) {
		if (this.returnState == this.EMPTY_RETURN_STATE) {
			return "$"
		} else {
			return "" + this.returnState
		}
	} else {
		return "" + this.returnState + " " + up
	}
}

type EmptyPredictionContext struct {
	SingletonPredictionContext.call(this, nil, PredictionContext.EMPTY_RETURN_STATE)
	return this
}

//EmptyPredictionContext.prototype = Object.create(SingletonPredictionContext.prototype)
//EmptyPredictionContext.prototype.constructor = EmptyPredictionContext

func (this *EmptyPredictionContext) isEmpty() {
	return true
}

func (this *EmptyPredictionContext) getParent(index) {
	return nil
}

func (this *EmptyPredictionContext) getReturnState(index) {
	return this.returnState
}

func (this *EmptyPredictionContext) equals(other) {
	return this == other
}

func (this *EmptyPredictionContext) toString() string {
	return "$"
}

PredictionContext.EMPTY = NewEmptyPredictionContext()

func ArrayPredictionContext(parents, returnStates) {
	// Parent can be nil only if full ctx mode and we make an array
	// from {@link //EMPTY} and non-empty. We merge {@link //EMPTY} by using
	// nil parent and
	// returnState == {@link //EMPTY_RETURN_STATE}.
	var hash = calculateHashString(parents, returnStates)
	PredictionContext.call(this, hash)
	this.parents = parents
	this.returnStates = returnStates
	return this
}

//ArrayPredictionContext.prototype = Object.create(PredictionContext.prototype)
//ArrayPredictionContext.prototype.constructor = ArrayPredictionContext

func (this *ArrayPredictionContext) isEmpty() {
	// since EMPTY_RETURN_STATE can only appear in the last position, we
	// don't need to verify that size==1
	return this.returnStates[0] == PredictionContext.EMPTY_RETURN_STATE
}

Object.defineProperty(ArrayPredictionContext.prototype, "length", {
	get : function() {
		return this.returnStates.length
	}
})

func (this *ArrayPredictionContext) getParent(index) {
	return this.parents[index]
}

func (this *ArrayPredictionContext) getReturnState(index) {
	return this.returnStates[index]
}

func (this *ArrayPredictionContext) equals(other) {
	if (this == other) {
		return true
	} else if (!_, ok := other.(ArrayPredictionContext); ok) {
		return false
	} else if (this.hashString != other.hashString()) {
		return false // can't be same if hash is different
	} else {
		return this.returnStates == other.returnStates &&
				this.parents == other.parents
	}
}

func (this *ArrayPredictionContext) toString() string {
	if (this.isEmpty()) {
		return "[]"
	} else {
		var s = "["
		for i := 0; i < len(this.returnStates); i++ {
			if (i > 0) {
				s = s + ", "
			}
			if (this.returnStates[i] == PredictionContext.EMPTY_RETURN_STATE) {
				s = s + "$"
				continue
			}
			s = s + this.returnStates[i]
			if (this.parents[i] != nil) {
				s = s + " " + this.parents[i]
			} else {
				s = s + "nil"
			}
		}
		return s + "]"
	}
}

// Convert a {@link RuleContext} tree to a {@link PredictionContext} graph.
// Return {@link //EMPTY} if {@code outerContext} is empty or nil.
// /
func predictionContextFromRuleContext(atn *ATN, outerContext *RuleContext) {
	if (outerContext == nil) {
		outerContext = RuleContext.EMPTY
	}
	// if we are in RuleContext of start rule, s, then PredictionContext
	// is EMPTY. Nobody called us. (if we are empty, return empty)
	if (outerContext.parentCtx == nil || outerContext == RuleContext.EMPTY) {
		return PredictionContext.EMPTY
	}
	// If we have a parent, convert it to a PredictionContext graph
	var parent = predictionContextFromRuleContext(atn, outerContext.parentCtx)
	var state = atn.states[outerContext.invokingState]
	var transition = state.transitions[0]
	return SingletonPredictionContext.create(parent, transition.followState.stateNumber)
}

func calculateListsHashString(parents, returnStates) {
	var s = ""
	parents.map(function(p) {
		s = s + p
	})
	returnStates.map(function(r) {
		s = s + r
	})
	return s
}

func merge(a, b, rootIsWildcard, mergeCache) {
	// share same graph if both same
	if (a == b) {
		return a
	}
	if (a instanceof SingletonPredictionContext && b instanceof SingletonPredictionContext) {
		return mergeSingletons(a, b, rootIsWildcard, mergeCache)
	}
	// At least one of a or b is array
	// If one is $ and rootIsWildcard, return $ as// wildcard
	if (rootIsWildcard) {
		if _, ok := a.(EmptyPredictionContext); ok {
			return a
		}
		if _, ok := b.(EmptyPredictionContext); ok {
			return b
		}
	}
	// convert singleton so both are arrays to normalize
	if _, ok := a.(SingletonPredictionContext); ok {
		a = NewArrayPredictionContext([a.getParent()], [a.returnState])
	}
	if _, ok := b.(SingletonPredictionContext); ok {
		b = NewArrayPredictionContext([b.getParent()], [b.returnState])
	}
	return mergeArrays(a, b, rootIsWildcard, mergeCache)
}

//
// Merge two {@link SingletonPredictionContext} instances.
//
// <p>Stack tops equal, parents merge is same return left graph.<br>
// <embed src="images/SingletonMerge_SameRootSamePar.svg"
// type="image/svg+xml"/></p>
//
// <p>Same stack top, parents differ merge parents giving array node, then
// remainders of those graphs. A Newroot node is created to point to the
// merged parents.<br>
// <embed src="images/SingletonMerge_SameRootDiffPar.svg"
// type="image/svg+xml"/></p>
//
// <p>Different stack tops pointing to same parent. Make array node for the
// root where both element in the root point to the same (original)
// parent.<br>
// <embed src="images/SingletonMerge_DiffRootSamePar.svg"
// type="image/svg+xml"/></p>
//
// <p>Different stack tops pointing to different parents. Make array node for
// the root where each element points to the corresponding original
// parent.<br>
// <embed src="images/SingletonMerge_DiffRootDiffPar.svg"
// type="image/svg+xml"/></p>
//
// @param a the first {@link SingletonPredictionContext}
// @param b the second {@link SingletonPredictionContext}
// @param rootIsWildcard {@code true} if this is a local-context merge,
// otherwise false to indicate a full-context merge
// @param mergeCache
// /
func mergeSingletons(a, b, rootIsWildcard, mergeCache) {
	if (mergeCache != nil) {
		var previous = mergeCache.get(a, b)
		if (previous != nil) {
			return previous
		}
		previous = mergeCache.get(b, a)
		if (previous != nil) {
			return previous
		}
	}

	var rootMerge = mergeRoot(a, b, rootIsWildcard)
	if (rootMerge != nil) {
		if (mergeCache != nil) {
			mergeCache.set(a, b, rootMerge)
		}
		return rootMerge
	}
	if (a.returnState == b.returnState) {
		var parent = merge(a.parentCtx, b.parentCtx, rootIsWildcard, mergeCache)
		// if parent is same as existing a or b parent or reduced to a parent,
		// return it
		if (parent == a.parentCtx) {
			return a // ax + bx = ax, if a=b
		}
		if (parent == b.parentCtx) {
			return b // ax + bx = bx, if a=b
		}
		// else: ax + ay = a'[x,y]
		// merge parents x and y, giving array node with x,y then remainders
		// of those graphs. dup a, a' points at merged array
		// Newjoined parent so create Newsingleton pointing to it, a'
		var spc = SingletonPredictionContext.create(parent, a.returnState)
		if (mergeCache != nil) {
			mergeCache.set(a, b, spc)
		}
		return spc
	} else { // a != b payloads differ
		// see if we can collapse parents due to $+x parents if local ctx
		var singleParent = nil
		if (a == b || (a.parentCtx != nil && a.parentCtx == b.parentCtx)) { // ax +
																				// bx =
																				// [a,b]x
			singleParent = a.parentCtx
		}
		if (singleParent != nil) { // parents are same
			// sort payloads and use same parent
			var payloads = [ a.returnState, b.returnState ]
			if (a.returnState > b.returnState) {
				payloads[0] = b.returnState
				payloads[1] = a.returnState
			}
			var parents = [ singleParent, singleParent ]
			var apc = NewArrayPredictionContext(parents, payloads)
			if (mergeCache != nil) {
				mergeCache.set(a, b, apc)
			}
			return apc
		}
		// parents differ and can't merge them. Just pack together
		// into array can't merge.
		// ax + by = [ax,by]
		var payloads = [ a.returnState, b.returnState ]
		var parents = [ a.parentCtx, b.parentCtx ]
		if (a.returnState > b.returnState) { // sort by payload
			payloads[0] = b.returnState
			payloads[1] = a.returnState
			parents = [ b.parentCtx, a.parentCtx ]
		}
		var a_ = NewArrayPredictionContext(parents, payloads)
		if (mergeCache != nil) {
			mergeCache.set(a, b, a_)
		}
		return a_
	}
}

//
// Handle case where at least one of {@code a} or {@code b} is
// {@link //EMPTY}. In the following diagrams, the symbol {@code $} is used
// to represent {@link //EMPTY}.
//
// <h2>Local-Context Merges</h2>
//
// <p>These local-context merge operations are used when {@code rootIsWildcard}
// is true.</p>
//
// <p>{@link //EMPTY} is superset of any graph return {@link //EMPTY}.<br>
// <embed src="images/LocalMerge_EmptyRoot.svg" type="image/svg+xml"/></p>
//
// <p>{@link //EMPTY} and anything is {@code //EMPTY}, so merged parent is
// {@code //EMPTY} return left graph.<br>
// <embed src="images/LocalMerge_EmptyParent.svg" type="image/svg+xml"/></p>
//
// <p>Special case of last merge if local context.<br>
// <embed src="images/LocalMerge_DiffRoots.svg" type="image/svg+xml"/></p>
//
// <h2>Full-Context Merges</h2>
//
// <p>These full-context merge operations are used when {@code rootIsWildcard}
// is false.</p>
//
// <p><embed src="images/FullMerge_EmptyRoots.svg" type="image/svg+xml"/></p>
//
// <p>Must keep all contexts {@link //EMPTY} in array is a special value (and
// nil parent).<br>
// <embed src="images/FullMerge_EmptyRoot.svg" type="image/svg+xml"/></p>
//
// <p><embed src="images/FullMerge_SameRoot.svg" type="image/svg+xml"/></p>
//
// @param a the first {@link SingletonPredictionContext}
// @param b the second {@link SingletonPredictionContext}
// @param rootIsWildcard {@code true} if this is a local-context merge,
// otherwise false to indicate a full-context merge
// /
func mergeRoot(a, b, rootIsWildcard) {
	if (rootIsWildcard) {
		if (a == PredictionContext.EMPTY) {
			return PredictionContext.EMPTY // // + b =//
		}
		if (b == PredictionContext.EMPTY) {
			return PredictionContext.EMPTY // a +// =//
		}
	} else {
		if (a == PredictionContext.EMPTY && b == PredictionContext.EMPTY) {
			return PredictionContext.EMPTY // $ + $ = $
		} else if (a == PredictionContext.EMPTY) { // $ + x = [$,x]
			var payloads = [ b.returnState,
					PredictionContext.EMPTY_RETURN_STATE ]
			var parents = [ b.parentCtx, nil ]
			return NewArrayPredictionContext(parents, payloads)
		} else if (b == PredictionContext.EMPTY) { // x + $ = [$,x] ($ is always first if present)
			var payloads = [ a.returnState, PredictionContext.EMPTY_RETURN_STATE ]
			var parents = [ a.parentCtx, nil ]
			return NewArrayPredictionContext(parents, payloads)
		}
	}
	return nil
}

//
// Merge two {@link ArrayPredictionContext} instances.
//
// <p>Different tops, different parents.<br>
// <embed src="images/ArrayMerge_DiffTopDiffPar.svg" type="image/svg+xml"/></p>
//
// <p>Shared top, same parents.<br>
// <embed src="images/ArrayMerge_ShareTopSamePar.svg" type="image/svg+xml"/></p>
//
// <p>Shared top, different parents.<br>
// <embed src="images/ArrayMerge_ShareTopDiffPar.svg" type="image/svg+xml"/></p>
//
// <p>Shared top, all shared parents.<br>
// <embed src="images/ArrayMerge_ShareTopSharePar.svg"
// type="image/svg+xml"/></p>
//
// <p>Equal tops, merge parents and reduce top to
// {@link SingletonPredictionContext}.<br>
// <embed src="images/ArrayMerge_EqualTop.svg" type="image/svg+xml"/></p>
// /
func mergeArrays(a, b, rootIsWildcard, mergeCache) {
	if (mergeCache != nil) {
		var previous = mergeCache.get(a, b)
		if (previous != nil) {
			return previous
		}
		previous = mergeCache.get(b, a)
		if (previous != nil) {
			return previous
		}
	}
	// merge sorted payloads a + b => M
	var i = 0 // walks a
	var j = 0 // walks b
	var k = 0 // walks target M array

	var mergedReturnStates = []
	var mergedParents = []
	// walk and merge to yield mergedParents, mergedReturnStates
	for (i < a.returnStates.length && j < b.returnStates.length) {
		var a_parent = a.parents[i]
		var b_parent = b.parents[j]
		if (a.returnStates[i] == b.returnStates[j]) {
			// same payload (stack tops are equal), must yield merged singleton
			var payload = a.returnStates[i]
			// $+$ = $
			var bothDollars = payload == PredictionContext.EMPTY_RETURN_STATE &&
					a_parent == nil && b_parent == nil
			var ax_ax = (a_parent != nil && b_parent != nil && a_parent == b_parent) // ax+ax
																							// ->
																							// ax
			if (bothDollars || ax_ax) {
				mergedParents[k] = a_parent // choose left
				mergedReturnStates[k] = payload
			} else { // ax+ay -> a'[x,y]
				var mergedParent = merge(a_parent, b_parent, rootIsWildcard, mergeCache)
				mergedParents[k] = mergedParent
				mergedReturnStates[k] = payload
			}
			i += 1 // hop over left one as usual
			j += 1 // but also skip one in right side since we merge
		} else if (a.returnStates[i] < b.returnStates[j]) { // copy a[i] to M
			mergedParents[k] = a_parent
			mergedReturnStates[k] = a.returnStates[i]
			i += 1
		} else { // b > a, copy b[j] to M
			mergedParents[k] = b_parent
			mergedReturnStates[k] = b.returnStates[j]
			j += 1
		}
		k += 1
	}
	// copy over any payloads remaining in either array
	if (i < a.returnStates.length) {
		for p := i p < a.returnStates.length p++) {
			mergedParents[k] = a.parents[p]
			mergedReturnStates[k] = a.returnStates[p]
			k += 1
		}
	} else {
		for p := j p < b.returnStates.length p++) {
			mergedParents[k] = b.parents[p]
			mergedReturnStates[k] = b.returnStates[p]
			k += 1
		}
	}
	// trim merged if we combined a few that had same stack tops
	if (k < mergedParents.length) { // write index < last position trim
		if (k == 1) { // for just one merged element, return singleton top
			var a_ = SingletonPredictionContext.create(mergedParents[0],
					mergedReturnStates[0])
			if (mergeCache != nil) {
				mergeCache.set(a, b, a_)
			}
			return a_
		}
		mergedParents = mergedParents.slice(0, k)
		mergedReturnStates = mergedReturnStates.slice(0, k)
	}

	var M = NewArrayPredictionContext(mergedParents, mergedReturnStates)

	// if we created same array as a or b, return that instead
	// TODO: track whether this is possible above during merge sort for speed
	if (M == a) {
		if (mergeCache != nil) {
			mergeCache.set(a, b, a)
		}
		return a
	}
	if (M == b) {
		if (mergeCache != nil) {
			mergeCache.set(a, b, b)
		}
		return b
	}
	combineCommonParents(mergedParents)

	if (mergeCache != nil) {
		mergeCache.set(a, b, M)
	}
	return M
}

//
// Make pass over all <em>M</em> {@code parents} merge any {@code equals()}
// ones.
// /
func combineCommonParents(parents) {
	var uniqueParents = {}

	for p := 0; p < len(parents); p++ {
		var parent = parents[p]
		if (!(parent in uniqueParents)) {
			uniqueParents[parent] = parent
		}
	}
	for q := 0; q < len(parents); q++ {
		parents[q] = uniqueParents[parents[q]]
	}
}

func getCachedPredictionContext(context, contextCache, visited) {
	if (context.isEmpty()) {
		return context
	}
	var existing = visited[context] || nil
	if (existing != nil) {
		return existing
	}
	existing = contextCache.get(context)
	if (existing != nil) {
		visited[context] = existing
		return existing
	}
	var changed = false
	var parents = []
	for i := 0; i < len(parents); i++ {
		var parent = getCachedPredictionContext(context.getParent(i), contextCache, visited)
		if (changed || parent != context.getParent(i)) {
			if (!changed) {
				parents = []
				for j := 0; j < len(context); j++ {
					parents[j] = context.getParent(j)
				}
				changed = true
			}
			parents[i] = parent
		}
	}
	if (!changed) {
		contextCache.add(context)
		visited[context] = context
		return context
	}
	var updated = nil
	if (parents.length == 0) {
		updated = PredictionContext.EMPTY
	} else if (parents.length == 1) {
		updated = SingletonPredictionContext.create(parents[0], context.getReturnState(0))
	} else {
		updated = NewArrayPredictionContext(parents, context.returnStates)
	}
	contextCache.add(updated)
	visited[updated] = updated
	visited[context] = updated

	return updated
}

// ter's recursive version of Sam's getAllNodes()
func getAllContextNodes(context, nodes, visited) {
	if (nodes == nil) {
		nodes = []
		return getAllContextNodes(context, nodes, visited)
	} else if (visited == nil) {
		visited = {}
		return getAllContextNodes(context, nodes, visited)
	} else {
		if (context == nil || visited[context] != nil) {
			return nodes
		}
		visited[context] = context
		nodes.push(context)
		for i := 0; i < len(context); i++ {
			getAllContextNodes(context.getParent(i), nodes, visited)
		}
		return nodes
	}
}







