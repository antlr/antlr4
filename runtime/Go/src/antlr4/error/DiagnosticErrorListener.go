package error

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

func DiagnosticErrorListener(exactOnly) {
	ErrorListener.call(this)
	exactOnly = exactOnly || true
	// whether all ambiguities or only exact ambiguities are reported.
	this.exactOnly = exactOnly
	return this
}

//DiagnosticErrorListener.prototype = Object.create(ErrorListener.prototype)
//DiagnosticErrorListener.prototype.constructor = DiagnosticErrorListener

func (this *DiagnosticErrorListener) reportAmbiguity(recognizer, dfa,
		startIndex, stopIndex, exact, ambigAlts, configs) {
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

func (this *DiagnosticErrorListener) reportAttemptingFullContext(
		recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs) {
	var msg = "reportAttemptingFullContext d=" +
			this.getDecisionDescription(recognizer, dfa) +
			", input='" +
			recognizer.getTokenStream().getText(NewInterval(startIndex, stopIndex)) + "'"
	recognizer.notifyErrorListeners(msg)
}

func (this *DiagnosticErrorListener) reportContextSensitivity(
		recognizer, dfa, startIndex, stopIndex, prediction, configs) {
	var msg = "reportContextSensitivity d=" +
			this.getDecisionDescription(recognizer, dfa) +
			", input='" +
			recognizer.getTokenStream().getText(NewInterval(startIndex, stopIndex)) + "'"
	recognizer.notifyErrorListeners(msg)
}

func (this *DiagnosticErrorListener) getDecisionDescription(recognizer, dfa) {
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
	var result = NewBitSet()
	for (var i = 0 i < configs.items.length i++) {
		result.add(configs.items[i].alt)
	}
	return "{" + result.values().join(", ") + "}"
}

