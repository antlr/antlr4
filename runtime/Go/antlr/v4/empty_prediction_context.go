package antlr

var _emptyPredictionContextHash int

func init() {
	_emptyPredictionContextHash = murmurInit(1)
	_emptyPredictionContextHash = murmurFinish(_emptyPredictionContextHash, 0)
}

func calculateEmptyHash() int {
	return _emptyPredictionContextHash
}

type EmptyPredictionContext struct {
	BaseSingletonPredictionContext
}


func (e *EmptyPredictionContext) length() int {
	return 1
}

func (e *EmptyPredictionContext) isEmpty() bool {
	return true
}

//func (e *EmptyPredictionContext) GetParent(_ int) PredictionContext {
//	return nil
//}

func (e *EmptyPredictionContext) getReturnState(_ int) int {
	return e.returnState
}

func (e *EmptyPredictionContext) Hash() int {
	return e.cachedHash
}

func (e *EmptyPredictionContext) Equals(other Collectable[PredictionContext]) bool {
	return e == other
}

func (e *EmptyPredictionContext) String() string {
	return "$"
}
