package antlr

import (
	"testing"
)

// Test for Issue #3319
// To run:
//
//   cd antlr4/runtime/Go/antlr/v4
//   go test
//
// In the old runtime code, the test would crash because it would try
// to compare a *LexerActionExecutor with nil, causing a nil pointer dereference.
// It only happens if there were different states that had equal stateNumber mod 16,
// and you created that ATNConfig with a nil LexerActionExecutor. That's why this
// test code has a hardwired constant of 16.

func TestCompare(t *testing.T) {
	var set = NewOrderedATNConfigSet()
	var s0 = NewATNState()
	var s1 = NewATNState()
	var s2 = NewATNState()
	var s3 = NewATNState()
	var s16 = NewATNState()
	s16.SetStateNumber(16)
	var s17 = NewATNState()
	s17.SetStateNumber(17)
	var s18 = NewATNState()
	s18.SetStateNumber(18)
	var s19 = NewATNState()
	s19.SetStateNumber(19)
	var la0 = NewBaseLexerAction(1)
	var la1 = NewBaseLexerAction(2)
	var laa = make([]LexerAction, 2)
	laa[0] = la0
	laa[1] = la1
	var ae = NewLexerActionExecutor(laa)
	set.Add(NewATNConfig5(s0, 0, BasePredictionContextEMPTY, ae), nil)
	set.Add(NewATNConfig5(s0, 1, BasePredictionContextEMPTY, ae), nil)
	set.Add(NewATNConfig5(s0, 2, BasePredictionContextEMPTY, ae), nil)
	set.Add(NewATNConfig5(s1, 0, BasePredictionContextEMPTY, ae), nil)
	set.Add(NewATNConfig5(s1, 1, BasePredictionContextEMPTY, ae), nil)
	set.Add(NewATNConfig5(s1, 2, BasePredictionContextEMPTY, ae), nil)
	set.Add(NewATNConfig5(s2, 0, BasePredictionContextEMPTY, ae), nil)
	set.Add(NewATNConfig5(s2, 1, BasePredictionContextEMPTY, ae), nil)
	set.Add(NewATNConfig5(s2, 2, BasePredictionContextEMPTY, ae), nil)
	set.Add(NewATNConfig5(s3, 0, BasePredictionContextEMPTY, ae), nil)
	set.Add(NewATNConfig5(s3, 1, BasePredictionContextEMPTY, ae), nil)
	set.Add(NewATNConfig5(s3, 2, BasePredictionContextEMPTY, ae), nil)
	
	set.Add(NewATNConfig5(s0, 0, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s0, 1, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s0, 2, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s1, 0, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s1, 1, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s1, 2, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s2, 0, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s2, 1, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s2, 2, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s3, 0, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s3, 1, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s3, 2, BasePredictionContextEMPTY, nil), nil)
	
	set.Add(NewATNConfig5(s16, 0, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s16, 1, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s16, 2, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s17, 0, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s17, 1, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s17, 2, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s18, 0, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s18, 1, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s18, 2, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s19, 0, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s19, 1, BasePredictionContextEMPTY, nil), nil)
	set.Add(NewATNConfig5(s19, 2, BasePredictionContextEMPTY, nil), nil)
}
