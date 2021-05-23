// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
// Use of this file is governed by the BSD 3-clause license that
// can be found in the LICENSE.txt file in the project root.

package antlr

import (
	"strconv"
)

// DiagnosticErrorListener listens to erros produced by the parser.
//
// This implementation of ANTLRErrorListener can be used to identify
// certain potential correctness and performance problems in grammars. "reports"
// are made by calling Parser//NotifyErrorListeners with the appropriate
// message.
//
// · Ambiguities: These are cases where more than one path through the grammar
// can Match the input.
//
// · Weak context sensitivity: These are cases where full-context prediction
// resolved an SLL conflict to a unique alternative which equaled the minimum
// alternative of the SLL conflict.
//
// · Strong (forced) context sensitivity: These are cases where the
// full-context prediction resolved an SLL conflict to a unique alternative,
// <em>and</em> the minimum alternative of the SLL conflict was found to not be
// a truly viable alternative. Two-stage parsing cannot be used for inputs where
// the situation occurs.
type DiagnosticErrorListener struct {
	*DefaultErrorListener

	exactOnly bool
}

// NewDiagnosticErrorListener returns a new instance of DiagnosticErrorListener.
// Whether all ambiguities or only exact ambiguities are Reported is represented
// by exactOnly
func NewDiagnosticErrorListener(exactOnly bool) *DiagnosticErrorListener {
	return &DiagnosticErrorListener{exactOnly: exactOnly}
}

// ReportAmbiguity reports a parsing ambiguity
func (d *DiagnosticErrorListener) ReportAmbiguity(recognizer Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *bitSet, configs ATNConfigSet) {
	if d.exactOnly && !exact {
		return
	}
	msg := "reportAmbiguity d=" +
		d.getDecisionDescription(recognizer, dfa) +
		": ambigAlts=" +
		d.getConflictingAlts(ambigAlts, configs).String() +
		", input='" +
		recognizer.GetTokenStream().GetTextFromInterval(NewInterval(startIndex, stopIndex)) + "'"
	recognizer.NotifyErrorListeners(msg, nil, nil)
}

// ReportAttemptingFullContext reports attempting full context.
func (d *DiagnosticErrorListener) ReportAttemptingFullContext(recognizer Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *bitSet, configs ATNConfigSet) {

	msg := "reportAttemptingFullContext d=" +
		d.getDecisionDescription(recognizer, dfa) +
		", input='" +
		recognizer.GetTokenStream().GetTextFromInterval(NewInterval(startIndex, stopIndex)) + "'"
	recognizer.NotifyErrorListeners(msg, nil, nil)
}

// ReportContextSensitivity reports context sensitivity.
func (d *DiagnosticErrorListener) ReportContextSensitivity(recognizer Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs ATNConfigSet) {
	msg := "reportContextSensitivity d=" +
		d.getDecisionDescription(recognizer, dfa) +
		", input='" +
		recognizer.GetTokenStream().GetTextFromInterval(NewInterval(startIndex, stopIndex)) + "'"
	recognizer.NotifyErrorListeners(msg, nil, nil)
}

func (d *DiagnosticErrorListener) getDecisionDescription(recognizer Parser, dfa *DFA) string {
	decision := dfa.decision
	ruleIndex := dfa.atnStartState.GetRuleIndex()

	ruleNames := recognizer.GetRuleNames()
	if ruleIndex < 0 || ruleIndex >= len(ruleNames) {
		return strconv.Itoa(decision)
	}
	ruleName := ruleNames[ruleIndex]
	if ruleName == "" {
		return strconv.Itoa(decision)
	}
	return strconv.Itoa(decision) + " (" + ruleName + ")"
}

//
// Computes the set of conflicting or ambiguous alternatives from a
// configuration set, if that information was not already provided by the
// parser.
//
// @param ReportedAlts The set of conflicting or ambiguous alternatives, as
// Reported by the parser.
// @param configs The conflicting or ambiguous configuration set.
// @return Returns ReportedAlts if it is not nil, otherwise
// returns the set of alternatives represented in configs.
//
func (d *DiagnosticErrorListener) getConflictingAlts(ReportedAlts *bitSet, s ATNConfigSet) *bitSet {
	if ReportedAlts != nil {
		return ReportedAlts
	}
	result := newBitSet()
	for _, c := range s.GetItems() {
		result.add(c.GetAlt())
	}

	return result
}
