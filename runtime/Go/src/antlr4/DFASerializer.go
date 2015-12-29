package antlr4

import (
	"fmt"
	"strconv"
)

// A DFA walker that knows how to dump them to serialized strings.

type DFASerializer struct {
	dfa                         *DFA
	literalNames, symbolicNames []string
}

func NewDFASerializer(dfa *DFA, literalNames, symbolicNames []string) *DFASerializer {

	if literalNames == nil {
		literalNames = make([]string, 0)
	}

	if symbolicNames == nil {
		symbolicNames = make([]string, 0)
	}

	this := new(DFASerializer)

	this.dfa = dfa
	this.literalNames = literalNames
	this.symbolicNames = symbolicNames

	return this
}

func (this *DFASerializer) String() string {

	if this.dfa.s0 == nil {
		return ""
	}

	var buf = ""
	var states = this.dfa.sortedStates()
	for _,s := range states {
		if s.edges != nil {
			var n = len(s.edges)
			for j := 0; j < n; j++ {
				var t = s.edges[j]
				if t != nil && t.stateNumber != 0x7FFFFFFF {
					buf += this.GetStateString(s)
					buf += "-"
					buf += this.getEdgeLabel(j)
					buf += "->"
					buf += this.GetStateString(t)
					buf += "\n"
				}
			}
		}
	}
	if len(buf) == 0 {
		return ""
	}

	return buf
}

func (this *DFASerializer) getEdgeLabel(i int) string {
	if i == 0 {
		return "EOF"
	} else if this.literalNames != nil && i - 1 < len(this.literalNames) {
		return this.literalNames[i-1]
	} else if this.symbolicNames != nil && i - 1 < len(this.symbolicNames) {
		return this.symbolicNames[i-1]
	} else {
		return strconv.Itoa(i-1)
	}
}

func (this *DFASerializer) GetStateString(s *DFAState) string {

	var a, b string

	if s.isAcceptState {
		a = ":"
	}

	if s.requiresFullContext {
		b = "^"
	}

	var baseStateStr = a + "s" + strconv.Itoa(s.stateNumber) + b
	if s.isAcceptState {
		if s.predicates != nil {
			return baseStateStr + "=>" + fmt.Sprint(s.predicates)
		} else {
			return baseStateStr + "=>" + fmt.Sprint(s.prediction)
		}
	} else {
		return baseStateStr
	}
}



type LexerDFASerializer struct {
	*DFASerializer
}

func NewLexerDFASerializer(dfa *DFA) *LexerDFASerializer {

	this := new(LexerDFASerializer)

	this.DFASerializer = NewDFASerializer(dfa, nil, nil)

	return this
}

func (this *LexerDFASerializer) getEdgeLabel(i int) string {
	return "'" + string(i) + "'"
}

func (this *LexerDFASerializer) String() string {

	if this.dfa.s0 == nil {
		return ""
	}

	var buf = ""
	var states = this.dfa.sortedStates()
	for i := 0; i < len(states); i++ {
		var s = states[i]
		if s.edges != nil {
			var n = len(s.edges)
			for j := 0; j < n; j++ {
				var t = s.edges[j]
				if t != nil && t.stateNumber != 0x7FFFFFFF {
					buf += this.GetStateString(s)
					buf += "-"
					buf += this.getEdgeLabel(j)
					buf += "->"
					buf += this.GetStateString(t)
					buf += "\n"
				}
			}
		}
	}
	if len(buf) == 0 {
		return ""
	}

	return buf
}
