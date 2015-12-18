package antlr4
import "strings"

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

type DiagnosticErrorListener struct {
	ErrorListener

	exactOnly bool
}

func NewDiagnosticErrorListener(exactOnly bool) *DiagnosticErrorListener {

	n := new(DiagnosticErrorListener)

	// whether all ambiguities or only exact ambiguities are reported.
	n.exactOnly = exactOnly
	return n
}

func (this *DiagnosticErrorListener) reportAmbiguity(recognizer *Parser, dfa *DFA, startIndex, stopIndex int, exact bool, ambigAlts *BitSet, configs *ATNConfigSet) {
	if (this.exactOnly && !exact) {
		return
	}
	var msg = "reportAmbiguity d=" +
			this.getDecisionDescription(recognizer, dfa) +
			": ambigAlts=" +
			this.getConflictingAlts(ambigAlts, configs) +
			", input='" +
			recognizer.getTokenStream().getTextFromInterval(NewInterval(startIndex, stopIndex)) + "'"
	recognizer.notifyErrorListeners(msg, nil, nil)
}

func (this *DiagnosticErrorListener) reportAttemptingFullContext(recognizer *Parser, dfa *DFA, startIndex, stopIndex int, conflictingAlts *BitSet, configs *ATNConfigSet) {

	var msg = "reportAttemptingFullContext d=" +
			this.getDecisionDescription(recognizer, dfa) +
			", input='" +
			recognizer.getTokenStream().getTextFromInterval(NewInterval(startIndex, stopIndex)) + "'"
	recognizer.notifyErrorListeners(msg, nil, nil)
}

func (this *DiagnosticErrorListener) reportContextSensitivity(recognizer *Parser, dfa *DFA, startIndex, stopIndex, prediction int, configs *ATNConfigSet) {
	var msg = "reportContextSensitivity d=" +
			this.getDecisionDescription(recognizer, dfa) +
			", input='" +
			recognizer.getTokenStream().getTextFromInterval(NewInterval(startIndex, stopIndex)) + "'"
	recognizer.notifyErrorListeners(msg, nil, nil)
}

func (this *DiagnosticErrorListener) getDecisionDescription(recognizer *Parser, dfa *DFA) {
	var decision = dfa.decision
	var ruleIndex = dfa.atnStartState.ruleIndex

	var ruleNames = recognizer.getRuleNames()
	if (ruleIndex < 0 || ruleIndex >= len(ruleNames)) {
		return "" + decision
	}
	var ruleName = ruleNames[ruleIndex] || nil
	if (ruleName == nil || len(ruleName) == 0) {
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
func (this *DiagnosticErrorListener) getConflictingAlts(reportedAlts *BitSet, set *ATNConfigSet) *BitSet {
	if (reportedAlts != nil) {
		return reportedAlts
	}
	var result = NewBitSet()
	for i := 0; i < len(set.configs); i++ {
		result.add(set.configs[i].alt)
	}
	return "{" + strings.Join(result.values(), ", ") + "}"
}