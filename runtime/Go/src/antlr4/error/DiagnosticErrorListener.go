package error
import (
	"antlr4"
	"antlr4/atn"
	"antlr4/dfa"
)

//
// This implementation of {@link ANTLRErrorListener} can be used to identify
// certain potential correctness and performance problems in grammars. "Reports"
// are made by calling {@link Parser//notifyErrorListeners} with the appropriate
// message.
//
// <ul>
// <li><b>Ambiguities</b>: These are cases where more than one path through the
// grammar can match the input.</li>
// <li><b>Weak context sensitivity</b>: These are cases where full-context
// prediction resolved an SLL conflict to a unique alternative which equaled the
// minimum alternative of the SLL conflict.</li>
// <li><b>Strong (forced) context sensitivity</b>: These are cases where the
// full-context prediction resolved an SLL conflict to a unique alternative,
// <em>and</em> the minimum alternative of the SLL conflict was found to not be
// a truly viable alternative. Two-stage parsing cannot be used for inputs where
// this situation occurs.</li>
// </ul>

//var BitSet = require('./../Utils').BitSet
//var ErrorListener = require('./ErrorListener').ErrorListener
//var Interval = require('./../IntervalSet').Interval

type DiagnosticErrorListener struct {
	ErrorListener
	exactOnly bool
}

func DiagnosticErrorListener(exactOnly bool) {

	n := new(DiagnosticErrorListener)

	// whether all ambiguities or only exact ambiguities are reported.
	n.exactOnly = exactOnly
	return n
}

//DiagnosticErrorListener.prototype = Object.create(ErrorListener.prototype)
//DiagnosticErrorListener.prototype.constructor = DiagnosticErrorListener

func (this *DiagnosticErrorListener) reportAmbiguity(recognizer *antlr4.Parser, dfa *dfa.DFA, startIndex, stopIndex int, exact bool, ambigAlts *antlr4.BitSet, configs *atn.ATNConfigSet) {
	if (this.exactOnly && !exact) {
		return
	}
	var msg = "reportAmbiguity d=" +
			this.getDecisionDescription(recognizer, dfa) +
			": ambigAlts=" +
			this.getConflictingAlts(ambigAlts, configs) +
			", input='" +
			recognizer.getTokenStream().getText(NewInterval(startIndex, stopIndex)) + "'"
	recognizer.notifyErrorListeners(msg)
}

func (this *DiagnosticErrorListener) reportAttemptingFullContext(recognizer *antlr4.Parser, dfa *dfa.DFA, startIndex, stopIndex int, conflictingAlts *antlr4.BitSet, configs *atn.ATNConfigSet) {

	var msg = "reportAttemptingFullContext d=" +
			this.getDecisionDescription(recognizer, dfa) +
			", input='" +
			recognizer.getTokenStream().getText(NewInterval(startIndex, stopIndex)) + "'"
	recognizer.notifyErrorListeners(msg)
}

func (this *DiagnosticErrorListener) reportContextSensitivity(recognizer *antlr4.Parser, dfa *dfa.DFA, startIndex, stopIndex, prediction int, configs *atn.ATNConfigSet) {
	var msg = "reportContextSensitivity d=" +
			this.getDecisionDescription(recognizer, dfa) +
			", input='" +
			recognizer.getTokenStream().getText(NewInterval(startIndex, stopIndex)) + "'"
	recognizer.notifyErrorListeners(msg)
}

func (this *DiagnosticErrorListener) getDecisionDescription(recognizer *antlr4.Parser, dfa *dfa.DFA) {
	var decision = dfa.decision
	var ruleIndex = dfa.atnStartState.ruleIndex

	var ruleNames = recognizer.ruleNames
	if (ruleIndex < 0 || ruleIndex >= ruleNames.length) {
		return "" + decision
	}
	var ruleName = ruleNames[ruleIndex] || nil
	if (ruleName == nil || ruleName.length == 0) {
		return "" + decision
	}
	return "" + decision + " (" + ruleName + ")"
}

//
// Computes the set of conflicting or ambiguous alternatives from a
// configuration set, if that information was not already provided by the
// parser.
//
// @param reportedAlts The set of conflicting or ambiguous alternatives, as
// reported by the parser.
// @param configs The conflicting or ambiguous configuration set.
// @return Returns {@code reportedAlts} if it is not {@code nil}, otherwise
// returns the set of alternatives represented in {@code configs}.
//
func (this *DiagnosticErrorListener) getConflictingAlts(reportedAlts, configs) {
	if (reportedAlts != nil) {
		return reportedAlts
	}
	var result = antlr4.NewBitSet()
	for i := 0; i < len(configs.items); i++ {
		result.add(configs.items[i].alt)
	}
	return "{" + result.values().join(", ") + "}"
}