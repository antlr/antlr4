package antlr

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

	d := new(DFASerializer)

	d.dfa = dfa
	d.literalNames = literalNames
	d.symbolicNames = symbolicNames

	return d
}

func (d *DFASerializer) String() string {

	if d.dfa.s0 == nil {
		return ""
	}

	var buf = ""
	var states = d.dfa.sortedStates()
	for _, s := range states {
		if s.edges != nil {
			var n = len(s.edges)
			for j := 0; j < n; j++ {
				var t = s.edges[j]
				if t != nil && t.stateNumber != 0x7FFFFFFF {
					buf += d.GetStateString(s)
					buf += "-"
					buf += d.getEdgeLabel(j)
					buf += "->"
					buf += d.GetStateString(t)
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

func (d *DFASerializer) getEdgeLabel(i int) string {
	if i == 0 {
		return "EOF"
	} else if d.literalNames != nil && i-1 < len(d.literalNames) {
		return d.literalNames[i-1]
	} else if d.symbolicNames != nil && i-1 < len(d.symbolicNames) {
		return d.symbolicNames[i-1]
	}

	return strconv.Itoa(i - 1)
}

func (d *DFASerializer) GetStateString(s *DFAState) string {

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
		}

		return baseStateStr + "=>" + fmt.Sprint(s.prediction)
	}

	return baseStateStr
}

type LexerDFASerializer struct {
	*DFASerializer
}

func NewLexerDFASerializer(dfa *DFA) *LexerDFASerializer {

	l := new(LexerDFASerializer)

	l.DFASerializer = NewDFASerializer(dfa, nil, nil)

	return l
}

func (l *LexerDFASerializer) getEdgeLabel(i int) string {
	return "'" + string(i) + "'"
}

func (l *LexerDFASerializer) String() string {

	if l.dfa.s0 == nil {
		return ""
	}

	var buf = ""
	var states = l.dfa.sortedStates()
	for i := 0; i < len(states); i++ {
		var s = states[i]
		if s.edges != nil {
			var n = len(s.edges)
			for j := 0; j < n; j++ {
				var t = s.edges[j]
				if t != nil && t.stateNumber != 0x7FFFFFFF {
					buf += l.GetStateString(s)
					buf += "-"
					buf += l.getEdgeLabel(j)
					buf += "->"
					buf += l.GetStateString(t)
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
