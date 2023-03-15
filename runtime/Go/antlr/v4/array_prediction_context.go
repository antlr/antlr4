package antlr

import (
	"golang.org/x/exp/slices"
	"strconv"
)

type ArrayPredictionContext struct {
	BasePredictionContext
	parents      []PredictionContext
	returnStates []int
}

func NewArrayPredictionContext(parents []PredictionContext, returnStates []int) *ArrayPredictionContext {
	// Parent can be nil only if full ctx mode and we make an array
	// from {@link //EMPTY} and non-empty. We merge {@link //EMPTY} by using
	// nil parent and
	// returnState == {@link //EmptyReturnState}.
	hash := murmurInit(1)
	for _, parent := range parents {
		hash = murmurUpdate(hash, parent.Hash())
	}
	for _, returnState := range returnStates {
		hash = murmurUpdate(hash, returnState)
	}
	hash = murmurFinish(hash, len(parents)<<1)
	
	return &ArrayPredictionContext{
		BasePredictionContext: BasePredictionContext{
			cachedHash: hash,
			pcType:     PredictionContextArray,
		},
		parents:      parents,
		returnStates: returnStates,
	}
}

func (a *ArrayPredictionContext) GetReturnStates() []int {
	return a.returnStates
}

func (a *ArrayPredictionContext) hasEmptyPath() bool {
	return a.getReturnState(a.length()-1) == BasePredictionContextEmptyReturnState
}

func (a *ArrayPredictionContext) isEmpty() bool {
	// since EmptyReturnState can only appear in the last position, we
	// don't need to verify that size==1
	return a.returnStates[0] == BasePredictionContextEmptyReturnState
}

func (a *ArrayPredictionContext) length() int {
	return len(a.returnStates)
}

func (a *ArrayPredictionContext) GetParent(index int) PredictionContext {
	return a.parents[index]
}

func (a *ArrayPredictionContext) getReturnState(index int) int {
	return a.returnStates[index]
}

// Equals is the default comparison function for ArrayPredictionContext when no specialized
// implementation is needed for a collection
func (a *ArrayPredictionContext) Equals(o Collectable[PredictionContext]) bool {
	if a == o {
		return true
	}
	other, ok := o.(*ArrayPredictionContext)
	if !ok {
		return false
	}
	if a.cachedHash != other.Hash() {
		return false // can't be same if hash is different
	}
	
	// Must compare the actual array elements and not just the array address
	// TODO: The hash hashes in all the return states anyway, to we maybe don't need to compare them here?
	return slices.Equal(a.returnStates, other.returnStates) &&
		slices.EqualFunc(a.parents, other.parents, func(x, y PredictionContext) bool {
			return x.Equals(y)
		})
}

// Hash is the default hash function for ArrayPredictionContext when no specialized
// implementation is needed for a collection
func (a *ArrayPredictionContext) Hash() int {
	return a.BasePredictionContext.cachedHash
}

func (a *ArrayPredictionContext) String() string {
	if a.isEmpty() {
		return "[]"
	}
	
	s := "["
	for i := 0; i < len(a.returnStates); i++ {
		if i > 0 {
			s = s + ", "
		}
		if a.returnStates[i] == BasePredictionContextEmptyReturnState {
			s = s + "$"
			continue
		}
		s = s + strconv.Itoa(a.returnStates[i])
		if a.parents[i] != nil {
			s = s + " " + a.parents[i].String()
		} else {
			s = s + "nil"
		}
	}
	
	return s + "]"
}
