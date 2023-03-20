package antlr

// BasePredictionContext is the 'abstract class' for all prediction contexts and does not exist
// in its own right. All actual [PredictionContext] structs embed this and then provide their
// own methods to implement functionality.
type BasePredictionContext struct {
	cachedHash int
	pcType     int
}

func (b *BasePredictionContext) Hash() int {
	return b.cachedHash
}

func (b *BasePredictionContext) Equals(_ Collectable[PredictionContext]) bool {
	return false
}

//func (b *BasePredictionContext) GetParent(i int) PredictionContext {
//	return nil
//}

func (b *BasePredictionContext) getReturnState(i int) int {
	return 0
}

func (b *BasePredictionContext) length() int {
	return 0
}

func (b *BasePredictionContext) hasEmptyPath() bool {
	return b.getReturnState(b.length()-1) == BasePredictionContextEmptyReturnState
}

func (b *BasePredictionContext) String() string {
	return "empty prediction context"
}

func (b *BasePredictionContext) isEmpty() bool {
	return false
}

func (b *BasePredictionContext) Type() int {
	return b.pcType
}
