// Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"fmt"
)

// PredictionContext defines the interface that must be implemented by any flavor of prediction context.
type PredictionContext interface {
	Hash() int
	Equals(collectable Collectable[PredictionContext]) bool
	GetParent(int) PredictionContext
	getReturnState(int) int
	length() int
	isEmpty() bool
	hasEmptyPath() bool
	String() string
	Type() int
}

const (
	// BasePredictionContextEmptyReturnState represents {@code $} in an array in full context mode, $
	// doesn't mean wildcard:
	//
	//   $ + x = [$,x]
	//
	// Here,
	//
	//   $ = EmptyReturnState
	BasePredictionContextEmptyReturnState = 0x7FFFFFFF
)

// TODO: JI These are meant to be atomics - this does not seem to match the Java runtime here
//goland:noinspection GoUnusedGlobalVariable
var (
	BasePredictionContextglobalNodeCount = 1
	BasePredictionContextid              = BasePredictionContextglobalNodeCount
)

const (
	PredictionContextEmpty = iota
	PredictionContextSingleton
	PredictionContextArray
)

func calculateHash(parent PredictionContext, returnState int) int {
	h := murmurInit(1)
	h = murmurUpdate(h, parent.Hash())
	h = murmurUpdate(h, returnState)
	return murmurFinish(h, 2)
}

// Convert a {@link RuleContext} tree to a {@link BasePredictionContext} graph.
// Return {@link //EMPTY} if {@code outerContext} is empty or nil.
// /
func predictionContextFromRuleContext(a *ATN, outerContext RuleContext) PredictionContext {
	if outerContext == nil {
		outerContext = ParserRuleContextEmpty
	}
	// if we are in RuleContext of start rule, s, then BasePredictionContext
	// is EMPTY. Nobody called us. (if we are empty, return empty)
	if outerContext.GetParent() == nil || outerContext == ParserRuleContextEmpty {
		return BasePredictionContextEMPTY
	}
	// If we have a parent, convert it to a BasePredictionContext graph
	parent := predictionContextFromRuleContext(a, outerContext.GetParent().(RuleContext))
	state := a.states[outerContext.GetInvokingState()]
	transition := state.GetTransitions()[0]
	
	return SingletonBasePredictionContextCreate(parent, transition.(*RuleTransition).followState.GetStateNumber())
}

func merge(a, b PredictionContext, rootIsWildcard bool, mergeCache *DoubleDict) PredictionContext {
	
	// Share same graph if both same
	//
	if a == b || a.Equals(b) {
		return a
	}
	
	// In Java, EmptyPredictionContext inherits from SingletonPredictionContext, and so the test
	// in java for SingletonPredictionContext will succeed and a new ArrayPredictionContext will be created
	// from it.
	// In go, EmptyPredictionContext does not equate to SingletonPredictionContext and so that conversion
	// will fail. We need to test for both Empty and Singleton and create an ArrayPredictionContext from
	// either of them.
	ac, ok1 := a.(*BaseSingletonPredictionContext)
	bc, ok2 := b.(*BaseSingletonPredictionContext)
	
	if ok1 && ok2 {
		return mergeSingletons(ac, bc, rootIsWildcard, mergeCache)
	}
	// At least one of a or b is array
	// If one is $ and rootIsWildcard, return $ as wildcard
	if rootIsWildcard {
		if a.isEmpty() {
			return a
		}
		if b.isEmpty() {
			return b
		}
	}
	
	// Convert either Singleton or Empty to arrays, so that we can merge them
	var ara, arb *ArrayPredictionContext
	
	ara = convertToArray(a)
	arb = convertToArray(b)
	return mergeArrays(ara, arb, rootIsWildcard, mergeCache)
}

func convertToArray(pc PredictionContext) *ArrayPredictionContext {
	switch pc.Type() {
	case PredictionContextEmpty:
		return NewArrayPredictionContext([]PredictionContext{}, []int{})
	case PredictionContextSingleton:
		return NewArrayPredictionContext([]PredictionContext{pc.GetParent(0)}, []int{pc.getReturnState(0)})
	default:
		// Already an array
	}
	return pc.(*ArrayPredictionContext)
}

// mergeSingletons merges two [SingletonBasePredictionContext] instances.
//
// Stack tops equal, parents merge is same return left graph.
// <embed src="images/SingletonMerge_SameRootSamePar.svg"
// type="image/svg+xml"/></p>
//
// <p>Same stack top, parents differ merge parents giving array node, then
// remainders of those graphs. A new root node is created to point to the
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
// @param a the first {@link SingletonBasePredictionContext}
// @param b the second {@link SingletonBasePredictionContext}
// @param rootIsWildcard {@code true} if this is a local-context merge,
// otherwise false to indicate a full-context merge
// @param mergeCache
// /
func mergeSingletons(a, b *BaseSingletonPredictionContext, rootIsWildcard bool, mergeCache *DoubleDict) PredictionContext {
	if mergeCache != nil {
		previous := mergeCache.Get(a.Hash(), b.Hash())
		if previous != nil {
			return previous.(PredictionContext)
		}
		previous = mergeCache.Get(b.Hash(), a.Hash())
		if previous != nil {
			return previous.(PredictionContext)
		}
	}
	
	rootMerge := mergeRoot(a, b, rootIsWildcard)
	if rootMerge != nil {
		if mergeCache != nil {
			mergeCache.set(a.Hash(), b.Hash(), rootMerge)
		}
		return rootMerge
	}
	if a.returnState == b.returnState {
		parent := merge(a.parentCtx, b.parentCtx, rootIsWildcard, mergeCache)
		// if parent is same as existing a or b parent or reduced to a parent,
		// return it
		if parent == a.parentCtx {
			return a // ax + bx = ax, if a=b
		}
		if parent == b.parentCtx {
			return b // ax + bx = bx, if a=b
		}
		// else: ax + ay = a'[x,y]
		// merge parents x and y, giving array node with x,y then remainders
		// of those graphs. dup a, a' points at merged array.
		// New joined parent so create a new singleton pointing to it, a'
		spc := SingletonBasePredictionContextCreate(parent, a.returnState)
		if mergeCache != nil {
			mergeCache.set(a.Hash(), b.Hash(), spc)
		}
		return spc
	}
	// a != b payloads differ
	// see if we can collapse parents due to $+x parents if local ctx
	var singleParent PredictionContext
	if a == b || (a.parentCtx != nil && a.parentCtx == b.parentCtx) { // ax +
		// bx =
		// [a,b]x
		singleParent = a.parentCtx
	}
	if singleParent != nil { // parents are same
		// sort payloads and use same parent
		payloads := []int{a.returnState, b.returnState}
		if a.returnState > b.returnState {
			payloads[0] = b.returnState
			payloads[1] = a.returnState
		}
		parents := []PredictionContext{singleParent, singleParent}
		apc := NewArrayPredictionContext(parents, payloads)
		if mergeCache != nil {
			mergeCache.set(a.Hash(), b.Hash(), apc)
		}
		return apc
	}
	// parents differ and can't merge them. Just pack together
	// into array can't merge.
	// ax + by = [ax,by]
	payloads := []int{a.returnState, b.returnState}
	parents := []PredictionContext{a.parentCtx, b.parentCtx}
	if a.returnState > b.returnState { // sort by payload
		payloads[0] = b.returnState
		payloads[1] = a.returnState
		parents = []PredictionContext{b.parentCtx, a.parentCtx}
	}
	apc := NewArrayPredictionContext(parents, payloads)
	if mergeCache != nil {
		mergeCache.set(a.Hash(), b.Hash(), apc)
	}
	return apc
}

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
// @param a the first {@link SingletonBasePredictionContext}
// @param b the second {@link SingletonBasePredictionContext}
// @param rootIsWildcard {@code true} if this is a local-context merge,
// otherwise false to indicate a full-context merge
// /
func mergeRoot(a, b SingletonPredictionContext, rootIsWildcard bool) PredictionContext {
	if rootIsWildcard {
		if a.isEmpty() {
			return BasePredictionContextEMPTY // // + b =//
		}
		if b.isEmpty() {
			return BasePredictionContextEMPTY // a +// =//
		}
	} else {
		if a.isEmpty() && b.isEmpty() {
			return BasePredictionContextEMPTY // $ + $ = $
		} else if a.isEmpty() { // $ + x = [$,x]
			payloads := []int{b.getReturnState(-1), BasePredictionContextEmptyReturnState}
			parents := []PredictionContext{b.GetParent(-1), nil}
			return NewArrayPredictionContext(parents, payloads)
		} else if b.isEmpty() { // x + $ = [$,x] ($ is always first if present)
			payloads := []int{a.getReturnState(-1), BasePredictionContextEmptyReturnState}
			parents := []PredictionContext{a.GetParent(-1), nil}
			return NewArrayPredictionContext(parents, payloads)
		}
	}
	return nil
}

// Merge two {@link ArrayBasePredictionContext} instances.
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
// {@link SingletonBasePredictionContext}.<br>
// <embed src="images/ArrayMerge_EqualTop.svg" type="image/svg+xml"/></p>
//
//goland:noinspection GoBoolExpressions
func mergeArrays(a, b *ArrayPredictionContext, rootIsWildcard bool, mergeCache *DoubleDict) PredictionContext {
	if mergeCache != nil {
		previous := mergeCache.Get(a.Hash(), b.Hash())
		if previous != nil {
			if ParserATNSimulatorTraceATNSim {
				fmt.Println("mergeArrays a=" + a.String() + ",b=" + b.String() + " -> previous")
			}
			return previous.(PredictionContext)
		}
		previous = mergeCache.Get(b.Hash(), a.Hash())
		if previous != nil {
			if ParserATNSimulatorTraceATNSim {
				fmt.Println("mergeArrays a=" + a.String() + ",b=" + b.String() + " -> previous")
			}
			return previous.(PredictionContext)
		}
	}
	// merge sorted payloads a + b => M
	i := 0 // walks a
	j := 0 // walks b
	k := 0 // walks target M array
	
	mergedReturnStates := make([]int, len(a.returnStates)+len(b.returnStates))
	mergedParents := make([]PredictionContext, len(a.returnStates)+len(b.returnStates))
	// walk and merge to yield mergedParents, mergedReturnStates
	for i < len(a.returnStates) && j < len(b.returnStates) {
		aParent := a.parents[i]
		bParent := b.parents[j]
		if a.returnStates[i] == b.returnStates[j] {
			// same payload (stack tops are equal), must yield merged singleton
			payload := a.returnStates[i]
			// $+$ = $
			bothDollars := payload == BasePredictionContextEmptyReturnState && aParent == nil && bParent == nil
			axAX := aParent != nil && bParent != nil && aParent == bParent // ax+ax
			// ->
			// ax
			if bothDollars || axAX {
				mergedParents[k] = aParent // choose left
				mergedReturnStates[k] = payload
			} else { // ax+ay -> a'[x,y]
				mergedParent := merge(aParent, bParent, rootIsWildcard, mergeCache)
				mergedParents[k] = mergedParent
				mergedReturnStates[k] = payload
			}
			i++ // hop over left one as usual
			j++ // but also Skip one in right side since we merge
		} else if a.returnStates[i] < b.returnStates[j] { // copy a[i] to M
			mergedParents[k] = aParent
			mergedReturnStates[k] = a.returnStates[i]
			i++
		} else { // b > a, copy b[j] to M
			mergedParents[k] = bParent
			mergedReturnStates[k] = b.returnStates[j]
			j++
		}
		k++
	}
	// copy over any payloads remaining in either array
	if i < len(a.returnStates) {
		for p := i; p < len(a.returnStates); p++ {
			mergedParents[k] = a.parents[p]
			mergedReturnStates[k] = a.returnStates[p]
			k++
		}
	} else {
		for p := j; p < len(b.returnStates); p++ {
			mergedParents[k] = b.parents[p]
			mergedReturnStates[k] = b.returnStates[p]
			k++
		}
	}
	// trim merged if we combined a few that had same stack tops
	if k < len(mergedParents) { // write index < last position trim
		if k == 1 { // for just one merged element, return singleton top
			pc := SingletonBasePredictionContextCreate(mergedParents[0], mergedReturnStates[0])
			if mergeCache != nil {
				mergeCache.set(a.Hash(), b.Hash(), pc)
			}
			return pc
		}
		mergedParents = mergedParents[0:k]
		mergedReturnStates = mergedReturnStates[0:k]
	}
	
	M := NewArrayPredictionContext(mergedParents, mergedReturnStates)
	
	// if we created same array as a or b, return that instead
	// TODO: JI track whether this is possible above during merge sort for speed
	// TODO: JI VERY IMPORTANT - In go, I do not think we can just do M == xx as M is a brand new allocation. This could be causing allocation problems
	if M == a {
		if mergeCache != nil {
			mergeCache.set(a.Hash(), b.Hash(), a)
		}
		if ParserATNSimulatorTraceATNSim {
			fmt.Println("mergeArrays a=" + a.String() + ",b=" + b.String() + " -> a")
		}
		return a
	}
	if M.Equals(b) {
		if mergeCache != nil {
			mergeCache.set(a.Hash(), b.Hash(), b)
		}
		if ParserATNSimulatorTraceATNSim {
			fmt.Println("mergeArrays a=" + a.String() + ",b=" + b.String() + " -> b")
		}
		return b
	}
	combineCommonParents(mergedParents)
	
	if mergeCache != nil {
		mergeCache.set(a.Hash(), b.Hash(), M)
	}
	if ParserATNSimulatorTraceATNSim {
		fmt.Println("mergeArrays a=" + a.String() + ",b=" + b.String() + " -> " + M.String())
	}
	return M
}

// Make pass over all <em>M</em> {@code parents} merge any {@code equals()}
// ones.
// /
func combineCommonParents(parents []PredictionContext) {
	uniqueParents := make(map[PredictionContext]PredictionContext)
	
	for p := 0; p < len(parents); p++ {
		parent := parents[p]
		if uniqueParents[parent] == nil {
			uniqueParents[parent] = parent
		}
	}
	for q := 0; q < len(parents); q++ {
		parents[q] = uniqueParents[parents[q]]
	}
}

func getCachedBasePredictionContext(context PredictionContext, contextCache *PredictionContextCache, visited map[PredictionContext]PredictionContext) PredictionContext {
	
	if context.isEmpty() {
		return context
	}
	existing := visited[context]
	if existing != nil {
		return existing
	}
	existing = contextCache.Get(context)
	if existing != nil {
		visited[context] = existing
		return existing
	}
	changed := false
	parents := make([]PredictionContext, context.length())
	for i := 0; i < len(parents); i++ {
		parent := getCachedBasePredictionContext(context.GetParent(i), contextCache, visited)
		if changed || parent != context.GetParent(i) {
			if !changed {
				parents = make([]PredictionContext, context.length())
				for j := 0; j < context.length(); j++ {
					parents[j] = context.GetParent(j)
				}
				changed = true
			}
			parents[i] = parent
		}
	}
	if !changed {
		contextCache.add(context)
		visited[context] = context
		return context
	}
	var updated PredictionContext
	if len(parents) == 0 {
		updated = BasePredictionContextEMPTY
	} else if len(parents) == 1 {
		updated = SingletonBasePredictionContextCreate(parents[0], context.getReturnState(0))
	} else {
		updated = NewArrayPredictionContext(parents, context.(*ArrayPredictionContext).GetReturnStates())
	}
	contextCache.add(updated)
	visited[updated] = updated
	visited[context] = updated
	
	return updated
}
