package antlr

import "strconv"

type SingletonPredictionContext interface {
	PredictionContext
}

type BaseSingletonPredictionContext struct {
	BasePredictionContext
	parentCtx   PredictionContext
	returnState int
}



func (b *BaseSingletonPredictionContext) length() int {
	return 1
}

func (b *BaseSingletonPredictionContext) GetParent(_ int) PredictionContext {
	return b.parentCtx
}

func (b *BaseSingletonPredictionContext) getReturnState(_ int) int {
	return b.returnState
}

func (b *BaseSingletonPredictionContext) hasEmptyPath() bool {
	return b.returnState == BasePredictionContextEmptyReturnState
}

func (b *BaseSingletonPredictionContext) Hash() int {
	return b.cachedHash
}

//func (b *BaseSingletonPredictionContext) Equals(other Collectable[*PredictionContext]) bool {
//	if b == other {
//		return true
//	}
//	if _, ok := other.(*BaseSingletonPredictionContext); !ok {
//		return false
//	}
//
//	otherP := other.(*BaseSingletonPredictionContext)
//
//	if b.cachedHash != otherP.Hash() {
//		return false // Can't be same if hash is different
//	}
//
//	if b.returnState != otherP.getReturnState(0) {
//		return false
//	}
//
//	// Both parents must be empty if one is
//	if b.parentCtx.isEmpty() {
//		return otherP.parentCtx.isEmpty()
//	}
//
//	return b.parentCtx.Equals(otherP.parentCtx)
//}

func (b *BaseSingletonPredictionContext) String() string {
	var up string
	
	if b.parentCtx.isEmpty() {
		up = ""
	} else {
		up = b.parentCtx.String()
	}
	
	if len(up) == 0 {
		if b.returnState == BasePredictionContextEmptyReturnState {
			return "$"
		}
		
		return strconv.Itoa(b.returnState)
	}
	
	return strconv.Itoa(b.returnState) + " " + up
}
