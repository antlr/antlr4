package antlr4

import (
	"fmt"
	"strconv"
	"strings"
)

type ParserATNSimulator struct {
	*ATNSimulator

	parser         IParser
	predictionMode int
	_input         TokenStream
	_startIndex    int
	_dfa           *DFA
	decisionToDFA  []*DFA
	mergeCache     *DoubleDict
	_outerContext  IParserRuleContext
}

func NewParserATNSimulator(parser IParser, atn *ATN, decisionToDFA []*DFA, sharedContextCache *PredictionContextCache) *ParserATNSimulator {

	this := new(ParserATNSimulator)

	this.InitParserATNSimulator(parser, atn, decisionToDFA, sharedContextCache)

	return this
}

func (this *ParserATNSimulator) InitParserATNSimulator(parser IParser, atn *ATN, decisionToDFA []*DFA, sharedContextCache *PredictionContextCache) {

	this.InitATNSimulator(atn, sharedContextCache)

	this.parser = parser
	this.decisionToDFA = decisionToDFA
	// SLL, LL, or LL + exact ambig detection?//
	this.predictionMode = PredictionModeLL
	// LAME globals to avoid parameters!!!!! I need these down deep in predTransition
	this._input = nil
	this._startIndex = 0
	this._outerContext = nil
	this._dfa = nil
	// Each prediction operation uses a cache for merge of prediction contexts.
	//  Don't keep around as it wastes huge amounts of memory. DoubleKeyMap
	//  isn't Synchronized but we're ok since two threads shouldn't reuse same
	//  parser/atnsim object because it can only handle one input at a time.
	//  This maps graphs a and b to merged result c. (a,b)&rarrc. We can avoid
	//  the merge if we ever see a and b again.  Note that (b,a)&rarrc should
	//  also be examined during cache lookup.
	//
	this.mergeCache = nil

}

var ParserATNSimulatorprototypedebug = false
var ParserATNSimulatorprototypedebug_list_atn_decisions = false
var ParserATNSimulatorprototypedfa_debug = false
var ParserATNSimulatorprototyperetry_debug = false

func (this *ParserATNSimulator) reset() {
}

func (this *ParserATNSimulator) AdaptivePredict(input TokenStream, decision int, outerContext IParserRuleContext) int {

	if ParserATNSimulatorprototypedebug || ParserATNSimulatorprototypedebug_list_atn_decisions {
		fmt.Println("AdaptivePredict decision " + strconv.Itoa(decision) +
			" exec LA(1)==" + this.getLookaheadName(input) +
			" line " + strconv.Itoa(input.LT(1).line) + ":" +
			strconv.Itoa(input.LT(1).column))
	}

	this._input = input
	this._startIndex = input.index()
	this._outerContext = outerContext

	var dfa = this.decisionToDFA[decision]
	this._dfa = dfa
	var m = input.mark()
	var index = input.index()

	defer func() {
		this._dfa = nil
		this.mergeCache = nil // wack cache after each prediction
		input.seek(index)
		input.release(m)
	}()

	// Now we are certain to have a specific decision's DFA
	// But, do we still need an initial state?
	var s0 *DFAState
	if dfa.precedenceDfa {
		// the start state for a precedence DFA depends on the current
		// parser precedence, and is provided by a DFA method.
		s0 = dfa.getPrecedenceStartState(this.parser.getPrecedence())
	} else {
		// the start state for a "regular" DFA is just s0
		s0 = dfa.s0
	}

	if s0 == nil {
		if outerContext == nil {
			outerContext = RuleContextEMPTY
		}
		if ParserATNSimulatorprototypedebug || ParserATNSimulatorprototypedebug_list_atn_decisions {
			fmt.Println("predictATN decision " + strconv.Itoa(dfa.decision) +
				" exec LA(1)==" + this.getLookaheadName(input) +
				", outerContext=" + outerContext.toString(this.parser.getRuleNames(), nil))
		}
		// If this is not a precedence DFA, we check the ATN start state
		// to determine if this ATN start state is the decision for the
		// closure block that determines whether a precedence rule
		// should continue or complete.

		var t2 IATNState = dfa.atnStartState
		t, ok := t2.(*StarLoopEntryState)
		if !dfa.precedenceDfa && ok {
			if t.precedenceRuleDecision {
				dfa.setPrecedenceDfa(true)
			}
		}
		var fullCtx = false
		var s0_closure = this.computeStartState(dfa.atnStartState, RuleContextEMPTY, fullCtx)

		if dfa.precedenceDfa {
			// If this is a precedence DFA, we use applyPrecedenceFilter
			// to convert the computed start state to a precedence start
			// state. We then use DFA.setPrecedenceStartState to set the
			// appropriate start state for the precedence level rather
			// than simply setting DFA.s0.
			//
			s0_closure = this.applyPrecedenceFilter(s0_closure)
			s0 = this.addDFAState(dfa, NewDFAState(-1, s0_closure))
			dfa.setPrecedenceStartState(this.parser.getPrecedence(), s0)
		} else {
			s0 = this.addDFAState(dfa, NewDFAState(-1, s0_closure))
			dfa.s0 = s0
		}
	}
	var alt = this.execATN(dfa, s0, input, index, outerContext)
	if ParserATNSimulatorprototypedebug {
		fmt.Println("DFA after predictATN: " + dfa.toString(this.parser.GetLiteralNames(), nil))
	}
	return alt

}

// Performs ATN simulation to compute a predicted alternative based
//  upon the remaining input, but also updates the DFA cache to avoid
//  having to traverse the ATN again for the same input sequence.

// There are some key conditions we're looking for after computing a new
// set of ATN configs (proposed DFA state):
// if the set is empty, there is no viable alternative for current symbol
// does the state uniquely predict an alternative?
// does the state have a conflict that would prevent us from
//   putting it on the work list?

// We also have some key operations to do:
// add an edge from previous DFA state to potentially NewDFA state, D,
//   upon current symbol but only if adding to work list, which means in all
//   cases except no viable alternative (and possibly non-greedy decisions?)
// collecting predicates and adding semantic context to DFA accept states
// adding rule context to context-sensitive DFA accept states
// consuming an input symbol
// reporting a conflict
// reporting an ambiguity
// reporting a context sensitivity
// reporting insufficient predicates

// cover these cases:
//    dead end
//    single alt
//    single alt + preds
//    conflict
//    conflict + preds
//
func (this *ParserATNSimulator) execATN(dfa *DFA, s0 *DFAState, input TokenStream, startIndex int, outerContext IParserRuleContext) int {

	if ParserATNSimulatorprototypedebug || ParserATNSimulatorprototypedebug_list_atn_decisions {
		fmt.Println("execATN decision " + strconv.Itoa(dfa.decision) +
			" exec LA(1)==" + this.getLookaheadName(input) +
			" line " + strconv.Itoa(input.LT(1).line) + ":" + strconv.Itoa(input.LT(1).column))
	}

	var previousD = s0

	if ParserATNSimulatorprototypedebug {
		fmt.Println("s0 = " + s0.toString())
	}
	var t = input.LA(1)
	for true { // for more work
		var D = this.getExistingTarGetState(previousD, t)
		if D == nil {
			D = this.computeTarGetState(dfa, previousD, t)
		}
		if D == ATNSimulatorERROR {
			// if any configs in previous dipped into outer context, that
			// means that input up to t actually finished entry rule
			// at least for SLL decision. Full LL doesn't dip into outer
			// so don't need special case.
			// We will get an error no matter what so delay until after
			// decision better error message. Also, no reachable target
			// ATN states in SLL implies LL will also get nowhere.
			// If conflict in states that dip out, choose min since we
			// will get error no matter what.
			e := this.noViableAlt(input, outerContext, previousD.configs, startIndex)
			input.seek(startIndex)
			alt := this.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previousD.configs, outerContext)
			if alt != ATNINVALID_ALT_NUMBER {
				return alt
			} else {
				panic(e)
			}
		}
		if D.requiresFullContext && this.predictionMode != PredictionModeSLL {
			// IF PREDS, MIGHT RESOLVE TO SINGLE ALT => SLL (or syntax error)
			var conflictingAlts *BitSet = D.configs.conflictingAlts
			if D.predicates != nil {
				if ParserATNSimulatorprototypedebug {
					fmt.Println("DFA state has preds in DFA sim LL failover")
				}
				var conflictIndex = input.index()
				if conflictIndex != startIndex {
					input.seek(startIndex)
				}
				conflictingAlts = this.evalSemanticContext(D.predicates, outerContext, true)
				if conflictingAlts.length() == 1 {
					if ParserATNSimulatorprototypedebug {
						fmt.Println("Full LL avoided")
					}
					return conflictingAlts.minValue()
				}
				if conflictIndex != startIndex {
					// restore the index so reporting the fallback to full
					// context occurs with the index at the correct spot
					input.seek(conflictIndex)
				}
			}
			if ParserATNSimulatorprototypedfa_debug {
				fmt.Println("ctx sensitive state " + outerContext.toString(nil, nil) + " in " + D.toString())
			}
			var fullCtx = true
			var s0_closure = this.computeStartState(dfa.atnStartState, outerContext, fullCtx)
			this.reportAttemptingFullContext(dfa, conflictingAlts, D.configs, startIndex, input.index())
			alt := this.execATNWithFullContext(dfa, D, s0_closure, input, startIndex, outerContext)
			return alt
		}
		if D.isAcceptState {
			if D.predicates == nil {
				return D.prediction
			}
			var stopIndex = input.index()
			input.seek(startIndex)
			var alts = this.evalSemanticContext(D.predicates, outerContext, true)
			if alts.length() == 0 {
				panic(this.noViableAlt(input, outerContext, D.configs, startIndex))
			} else if alts.length() == 1 {
				return alts.minValue()
			} else {
				// report ambiguity after predicate evaluation to make sure the correct set of ambig alts is reported.
				this.reportAmbiguity(dfa, D, startIndex, stopIndex, false, alts, D.configs)
				return alts.minValue()
			}
		}
		previousD = D

		if t != TokenEOF {
			input.Consume()
			t = input.LA(1)
		}
	}

	panic("Should not have reached this state")
}

// Get an existing target state for an edge in the DFA. If the target state
// for the edge has not yet been computed or is otherwise not available,
// this method returns {@code nil}.
//
// @param previousD The current DFA state
// @param t The next input symbol
// @return The existing target DFA state for the given input symbol
// {@code t}, or {@code nil} if the target state for this edge is not
// already cached

func (this *ParserATNSimulator) getExistingTarGetState(previousD *DFAState, t int) *DFAState {
	var edges = previousD.edges
	if edges == nil {
		return nil
	} else {
		return edges[t+1]
	}
}

// Compute a target state for an edge in the DFA, and attempt to add the
// computed state and corresponding edge to the DFA.
//
// @param dfa The DFA
// @param previousD The current DFA state
// @param t The next input symbol
//
// @return The computed target DFA state for the given input symbol
// {@code t}. If {@code t} does not lead to a valid DFA state, this method
// returns {@link //ERROR}.

func (this *ParserATNSimulator) computeTarGetState(dfa *DFA, previousD *DFAState, t int) *DFAState {
	var reach = this.computeReachSet(previousD.configs, t, false)

	if reach == nil {
		this.addDFAEdge(dfa, previousD, t, ATNSimulatorERROR)
		return ATNSimulatorERROR
	}
	// create Newtarget state we'll add to DFA after it's complete
	var D = NewDFAState(-1, reach)

	var predictedAlt = this.getUniqueAlt(reach)

	if ParserATNSimulatorprototypedebug {
		var altSubSets = PredictionModegetConflictingAltSubsets(reach)
		fmt.Println("SLL altSubSets=" + fmt.Sprint(altSubSets) +
			", previous=" + previousD.configs.toString() +
			", configs=" + reach.toString() +
			", predict=" + strconv.Itoa(predictedAlt) +
			", allSubsetsConflict=" +
			fmt.Sprint(PredictionModeallSubsetsConflict(altSubSets)) +
			", conflictingAlts=" + this.getConflictingAlts(reach).toString())
	}
	if predictedAlt != ATNINVALID_ALT_NUMBER {
		// NO CONFLICT, UNIQUELY PREDICTED ALT
		D.isAcceptState = true
		D.configs.uniqueAlt = predictedAlt
		D.prediction = predictedAlt
	} else if PredictionModehasSLLConflictTerminatingPrediction(this.predictionMode, reach) {
		// MORE THAN ONE VIABLE ALTERNATIVE
		D.configs.conflictingAlts = this.getConflictingAlts(reach)
		D.requiresFullContext = true
		// in SLL-only mode, we will stop at this state and return the minimum alt
		D.isAcceptState = true
		D.prediction = D.configs.conflictingAlts.minValue()
	}
	if D.isAcceptState && D.configs.hasSemanticContext {
		this.predicateDFAState(D, this.atn.getDecisionState(dfa.decision))
		if D.predicates != nil {
			D.prediction = ATNINVALID_ALT_NUMBER
		}
	}
	// all adds to dfa are done after we've created full D state
	D = this.addDFAEdge(dfa, previousD, t, D)
	return D
}

func (this *ParserATNSimulator) predicateDFAState(dfaState *DFAState, decisionState *DecisionState) {
	// We need to test all predicates, even in DFA states that
	// uniquely predict alternative.
	var nalts = len(decisionState.getTransitions())
	// Update DFA so reach becomes accept state with (predicate,alt)
	// pairs if preds found for conflicting alts
	var altsToCollectPredsFrom = this.getConflictingAltsOrUniqueAlt(dfaState.configs)
	var altToPred = this.getPredsForAmbigAlts(altsToCollectPredsFrom, dfaState.configs, nalts)
	if altToPred != nil {
		dfaState.predicates = this.getPredicatePredictions(altsToCollectPredsFrom, altToPred)
		dfaState.prediction = ATNINVALID_ALT_NUMBER // make sure we use preds
	} else {
		// There are preds in configs but they might go away
		// when OR'd together like {p}? || NONE == NONE. If neither
		// alt has preds, resolve to min alt
		dfaState.prediction = altsToCollectPredsFrom.minValue()
	}
}

// comes back with reach.uniqueAlt set to a valid alt
func (this *ParserATNSimulator) execATNWithFullContext(dfa *DFA, D *DFAState, s0 *ATNConfigSet, input TokenStream, startIndex int, outerContext IParserRuleContext) int {

	if ParserATNSimulatorprototypedebug || ParserATNSimulatorprototypedebug_list_atn_decisions {
		fmt.Println("execATNWithFullContext " + s0.toString())
	}

	var fullCtx = true
	var foundExactAmbig = false
	var reach *ATNConfigSet = nil
	var previous = s0
	input.seek(startIndex)
	var t = input.LA(1)
	var predictedAlt = -1

	for true { // for more work
		reach = this.computeReachSet(previous, t, fullCtx)
		if reach == nil {
			// if any configs in previous dipped into outer context, that
			// means that input up to t actually finished entry rule
			// at least for LL decision. Full LL doesn't dip into outer
			// so don't need special case.
			// We will get an error no matter what so delay until after
			// decision better error message. Also, no reachable target
			// ATN states in SLL implies LL will also get nowhere.
			// If conflict in states that dip out, choose min since we
			// will get error no matter what.
			var e = this.noViableAlt(input, outerContext, previous, startIndex)
			input.seek(startIndex)
			var alt = this.getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(previous, outerContext)
			if alt != ATNINVALID_ALT_NUMBER {
				return alt
			} else {
				panic(e)
			}
		}
		var altSubSets = PredictionModegetConflictingAltSubsets(reach)
		if ParserATNSimulatorprototypedebug {
			fmt.Println("LL altSubSets=" + fmt.Sprint(altSubSets) + ", predict=" +
				strconv.Itoa(PredictionModegetUniqueAlt(altSubSets)) + ", resolvesToJustOneViableAlt=" +
				fmt.Sprint(PredictionModeresolvesToJustOneViableAlt(altSubSets)))
		}
		reach.uniqueAlt = this.getUniqueAlt(reach)
		// unique prediction?
		if reach.uniqueAlt != ATNINVALID_ALT_NUMBER {
			predictedAlt = reach.uniqueAlt
			break
		} else if this.predictionMode != PredictionModeLL_EXACT_AMBIG_DETECTION {
			predictedAlt = PredictionModeresolvesToJustOneViableAlt(altSubSets)
			if predictedAlt != ATNINVALID_ALT_NUMBER {
				break
			}
		} else {
			// In exact ambiguity mode, we never try to terminate early.
			// Just keeps scarfing until we know what the conflict is
			if PredictionModeallSubsetsConflict(altSubSets) && PredictionModeallSubsetsEqual(altSubSets) {
				foundExactAmbig = true
				predictedAlt = PredictionModegetSingleViableAlt(altSubSets)
				break
			}
			// else there are multiple non-conflicting subsets or
			// we're not sure what the ambiguity is yet.
			// So, keep going.
		}
		previous = reach
		if t != TokenEOF {
			input.Consume()
			t = input.LA(1)
		}
	}
	// If the configuration set uniquely predicts an alternative,
	// without conflict, then we know that it's a full LL decision
	// not SLL.
	if reach.uniqueAlt != ATNINVALID_ALT_NUMBER {
		this.reportContextSensitivity(dfa, predictedAlt, reach, startIndex, input.index())
		return predictedAlt
	}
	// We do not check predicates here because we have checked them
	// on-the-fly when doing full context prediction.

	//
	// In non-exact ambiguity detection mode, we might	actually be able to
	// detect an exact ambiguity, but I'm not going to spend the cycles
	// needed to check. We only emit ambiguity warnings in exact ambiguity
	// mode.
	//
	// For example, we might know that we have conflicting configurations.
	// But, that does not mean that there is no way forward without a
	// conflict. It's possible to have nonconflicting alt subsets as in:

	// altSubSets=[{1, 2}, {1, 2}, {1}, {1, 2}]

	// from
	//
	//    [(17,1,[5 $]), (13,1,[5 10 $]), (21,1,[5 10 $]), (11,1,[$]),
	//     (13,2,[5 10 $]), (21,2,[5 10 $]), (11,2,[$])]
	//
	// In this case, (17,1,[5 $]) indicates there is some next sequence that
	// would resolve this without conflict to alternative 1. Any other viable
	// next sequence, however, is associated with a conflict.  We stop
	// looking for input because no amount of further lookahead will alter
	// the fact that we should predict alternative 1.  We just can't say for
	// sure that there is an ambiguity without looking further.

	this.reportAmbiguity(dfa, D, startIndex, input.index(), foundExactAmbig, nil, reach)

	return predictedAlt
}

func (this *ParserATNSimulator) computeReachSet(closure *ATNConfigSet, t int, fullCtx bool) *ATNConfigSet {
	if ParserATNSimulatorprototypedebug {
		fmt.Println("in computeReachSet, starting closure: " + closure.toString())
	}
	if this.mergeCache == nil {
		this.mergeCache = NewDoubleDict()
	}
	var intermediate = NewATNConfigSet(fullCtx)

	// Configurations already in a rule stop state indicate reaching the end
	// of the decision rule (local context) or end of the start rule (full
	// context). Once reached, these configurations are never updated by a
	// closure operation, so they are handled separately for the performance
	// advantage of having a smaller intermediate set when calling closure.
	//
	// For full-context reach operations, separate handling is required to
	// ensure that the alternative Matching the longest overall sequence is
	// chosen when multiple such configurations can Match the input.

	var skippedStopStates []*ATNConfig = nil

	// First figure out where we can reach on input t
	for i := 0; i < len(closure.configs); i++ {

		var c = closure.configs[i]
		if ParserATNSimulatorprototypedebug {
			fmt.Println("testing " + this.GetTokenName(t) + " at " + c.toString())
		}

		_, ok := c.GetState().(*RuleStopState)

		if ok {
			if fullCtx || t == TokenEOF {
				if skippedStopStates == nil {
					skippedStopStates = make([]*ATNConfig, 0)
				}
				skippedStopStates = append(skippedStopStates, c.(*ATNConfig))
				if ParserATNSimulatorprototypedebug {
					fmt.Println("added " + c.toString() + " to skippedStopStates")
				}
			}
			continue
		}

		for j := 0; j < len(c.GetState().getTransitions()); j++ {
			var trans = c.GetState().getTransitions()[j]
			var target = this.getReachableTarget(trans, t)
			if target != nil {
				var cfg = NewATNConfig4(c, target)
				intermediate.add(cfg, this.mergeCache)
				if ParserATNSimulatorprototypedebug {
					fmt.Println("added " + cfg.toString() + " to intermediate")
				}
			}
		}
	}
	// Now figure out where the reach operation can take us...
	var reach *ATNConfigSet = nil

	// This block optimizes the reach operation for intermediate sets which
	// trivially indicate a termination state for the overall
	// AdaptivePredict operation.
	//
	// The conditions assume that intermediate
	// contains all configurations relevant to the reach set, but this
	// condition is not true when one or more configurations have been
	// withheld in skippedStopStates, or when the current symbol is EOF.
	//
	if skippedStopStates == nil && t != TokenEOF {
		if len(intermediate.configs) == 1 {
			// Don't pursue the closure if there is just one state.
			// It can only have one alternative just add to result
			// Also don't pursue the closure if there is unique alternative
			// among the configurations.
			reach = intermediate
		} else if this.getUniqueAlt(intermediate) != ATNINVALID_ALT_NUMBER {
			// Also don't pursue the closure if there is unique alternative
			// among the configurations.
			reach = intermediate
		}
	}
	// If the reach set could not be trivially determined, perform a closure
	// operation on the intermediate set to compute its initial value.
	//
	if reach == nil {
		reach = NewATNConfigSet(fullCtx)
		var closureBusy = NewSet(nil, nil)
		var treatEofAsEpsilon = t == TokenEOF
		for k := 0; k < len(intermediate.configs); k++ {
			this.closure(intermediate.configs[k], reach, closureBusy, false, fullCtx, treatEofAsEpsilon)
		}
	}
	if t == TokenEOF {
		// After consuming EOF no additional input is possible, so we are
		// only interested in configurations which reached the end of the
		// decision rule (local context) or end of the start rule (full
		// context). Update reach to contain only these configurations. This
		// handles both explicit EOF transitions in the grammar and implicit
		// EOF transitions following the end of the decision or start rule.
		//
		// When reach==intermediate, no closure operation was performed. In
		// this case, removeAllConfigsNotInRuleStopState needs to check for
		// reachable rule stop states as well as configurations already in
		// a rule stop state.
		//
		// This is handled before the configurations in skippedStopStates,
		// because any configurations potentially added from that list are
		// already guaranteed to meet this condition whether or not it's
		// required.
		//
		reach = this.removeAllConfigsNotInRuleStopState(reach, reach == intermediate)
	}
	// If skippedStopStates!=nil, then it contains at least one
	// configuration. For full-context reach operations, these
	// configurations reached the end of the start rule, in which case we
	// only add them back to reach if no configuration during the current
	// closure operation reached such a state. This ensures AdaptivePredict
	// chooses an alternative Matching the longest overall sequence when
	// multiple alternatives are viable.
	//
	if skippedStopStates != nil && ((!fullCtx) || (!PredictionModehasConfigInRuleStopState(reach))) {
		for l := 0; l < len(skippedStopStates); l++ {
			reach.add(skippedStopStates[l], this.mergeCache)
		}
	}
	if len(reach.configs) == 0 {
		return nil
	} else {
		return reach
	}
}

//
// Return a configuration set containing only the configurations from
// {@code configs} which are in a {@link RuleStopState}. If all
// configurations in {@code configs} are already in a rule stop state, this
// method simply returns {@code configs}.
//
// <p>When {@code lookToEndOfRule} is true, this method uses
// {@link ATN//nextTokens} for each configuration in {@code configs} which is
// not already in a rule stop state to see if a rule stop state is reachable
// from the configuration via epsilon-only transitions.</p>
//
// @param configs the configuration set to update
// @param lookToEndOfRule when true, this method checks for rule stop states
// reachable by epsilon-only transitions from each configuration in
// {@code configs}.
//
// @return {@code configs} if all configurations in {@code configs} are in a
// rule stop state, otherwise return a Newconfiguration set containing only
// the configurations from {@code configs} which are in a rule stop state
//
func (this *ParserATNSimulator) removeAllConfigsNotInRuleStopState(configs *ATNConfigSet, lookToEndOfRule bool) *ATNConfigSet {
	if PredictionModeallConfigsInRuleStopStates(configs) {
		return configs
	}
	var result = NewATNConfigSet(configs.fullCtx)
	for i := 0; i < len(configs.configs); i++ {
		var config = configs.configs[i]

		_, ok := config.GetState().(*RuleStopState)

		if ok {
			result.add(config, this.mergeCache)
			continue
		}
		if lookToEndOfRule && config.GetState().getEpsilonOnlyTransitions() {
			var nextTokens = this.atn.nextTokens(config.GetState(), nil)
			if nextTokens.contains(TokenEpsilon) {
				var endOfRuleState = this.atn.ruleToStopState[config.GetState().getRuleIndex()]
				result.add(NewATNConfig4(config, endOfRuleState), this.mergeCache)
			}
		}
	}
	return result
}

func (this *ParserATNSimulator) computeStartState(p IATNState, ctx IRuleContext, fullCtx bool) *ATNConfigSet {
	// always at least the implicit call to start rule
	var initialContext = predictionContextFromRuleContext(this.atn, ctx)
	var configs = NewATNConfigSet(fullCtx)
	for i := 0; i < len(p.getTransitions()); i++ {
		var target = p.getTransitions()[i].getTarget()
		var c = NewATNConfig5(target, i+1, initialContext, nil)
		var closureBusy = NewSet(nil, nil)
		this.closure(c, configs, closureBusy, true, fullCtx, false)
	}
	return configs
}

//
// This method transforms the start state computed by
// {@link //computeStartState} to the special start state used by a
// precedence DFA for a particular precedence value. The transformation
// process applies the following changes to the start state's configuration
// set.
//
// <ol>
// <li>Evaluate the precedence predicates for each configuration using
// {@link SemanticContext//evalPrecedence}.</li>
// <li>Remove all configurations which predict an alternative greater than
// 1, for which another configuration that predicts alternative 1 is in the
// same ATN state with the same prediction context. This transformation is
// valid for the following reasons:
// <ul>
// <li>The closure block cannot contain any epsilon transitions which bypass
// the body of the closure, so all states reachable via alternative 1 are
// part of the precedence alternatives of the transformed left-recursive
// rule.</li>
// <li>The "primary" portion of a left recursive rule cannot contain an
// epsilon transition, so the only way an alternative other than 1 can exist
// in a state that is also reachable via alternative 1 is by nesting calls
// to the left-recursive rule, with the outer calls not being at the
// preferred precedence level.</li>
// </ul>
// </li>
// </ol>
//
// <p>
// The prediction context must be considered by this filter to address
// situations like the following.
// </p>
// <code>
// <pre>
// grammar TA
// prog: statement* EOF
// statement: letterA | statement letterA 'b'
// letterA: 'a'
// </pre>
// </code>
// <p>
// If the above grammar, the ATN state immediately before the token
// reference {@code 'a'} in {@code letterA} is reachable from the left edge
// of both the primary and closure blocks of the left-recursive rule
// {@code statement}. The prediction context associated with each of these
// configurations distinguishes between them, and prevents the alternative
// which stepped out to {@code prog} (and then back in to {@code statement}
// from being eliminated by the filter.
// </p>
//
// @param configs The configuration set computed by
// {@link //computeStartState} as the start state for the DFA.
// @return The transformed configuration set representing the start state
// for a precedence DFA at a particular precedence level (determined by
// calling {@link Parser//getPrecedence}).
//
func (this *ParserATNSimulator) applyPrecedenceFilter(configs *ATNConfigSet) *ATNConfigSet {

	var statesFromAlt1 = make(map[int]IPredictionContext)
	var configSet = NewATNConfigSet(configs.fullCtx)

	for i := 0; i < len(configs.configs); i++ {
		config := configs.configs[i]
		// handle alt 1 first
		if config.getAlt() != 1 {
			continue
		}
		var updatedContext = config.getSemanticContext().evalPrecedence(this.parser, this._outerContext)
		if updatedContext == nil {
			// the configuration was eliminated
			continue
		}
		statesFromAlt1[config.GetState().GetStateNumber()] = config.getContext()
		if updatedContext != config.getSemanticContext() {
			configSet.add(NewATNConfig2(config, updatedContext), this.mergeCache)
		} else {
			configSet.add(config, this.mergeCache)
		}
	}
	for i := 0; i < len(configs.configs); i++ {
		config := configs.configs[i]
		if config.getAlt() == 1 {
			// already handled
			continue
		}
		// In the future, this elimination step could be updated to also
		// filter the prediction context for alternatives predicting alt>1
		// (basically a graph subtraction algorithm).
		if !config.getPrecedenceFilterSuppressed() {
			var context = statesFromAlt1[config.GetState().GetStateNumber()]
			if context != nil && context.equals(config.getContext()) {
				// eliminated
				continue
			}
		}
		configSet.add(config, this.mergeCache)
	}
	return configSet
}

func (this *ParserATNSimulator) getReachableTarget(trans ITransition, ttype int) IATNState {
	if trans.Matches(ttype, 0, this.atn.maxTokenType) {
		return trans.getTarget()
	} else {
		return nil
	}
}

func (this *ParserATNSimulator) getPredsForAmbigAlts(ambigAlts *BitSet, configs *ATNConfigSet, nalts int) []SemanticContext {

	var altToPred = make([]SemanticContext, nalts+1)
	for i := 0; i < len(configs.configs); i++ {
		var c = configs.configs[i]
		if ambigAlts.contains(c.getAlt()) {
			altToPred[c.getAlt()] = SemanticContextorContext(altToPred[c.getAlt()], c.getSemanticContext())
		}
	}
	var nPredAlts = 0
	for i := 1; i < nalts+1; i++ {
		var pred = altToPred[i]
		if pred == nil {
			altToPred[i] = SemanticContextNONE
		} else if pred != SemanticContextNONE {
			nPredAlts += 1
		}
	}
	// nonambig alts are nil in altToPred
	if nPredAlts == 0 {
		altToPred = nil
	}
	if ParserATNSimulatorprototypedebug {
		fmt.Println("getPredsForAmbigAlts result " + fmt.Sprint(altToPred))
	}
	return altToPred
}

func (this *ParserATNSimulator) getPredicatePredictions(ambigAlts *BitSet, altToPred []SemanticContext) []*PredPrediction {
	var pairs = make([]*PredPrediction, 0)
	var containsPredicate = false
	for i := 1; i < len(altToPred); i++ {
		var pred = altToPred[i]
		// unpredicated is indicated by SemanticContextNONE
		if ambigAlts != nil && ambigAlts.contains(i) {
			pairs = append(pairs, NewPredPrediction(pred, i))
		}
		if pred != SemanticContextNONE {
			containsPredicate = true
		}
	}
	if !containsPredicate {
		return nil
	}
	return pairs
}

//
// This method is used to improve the localization of error messages by
// choosing an alternative rather than panicing a
// {@link NoViableAltException} in particular prediction scenarios where the
// {@link //ERROR} state was reached during ATN simulation.
//
// <p>
// The default implementation of this method uses the following
// algorithm to identify an ATN configuration which successfully parsed the
// decision entry rule. Choosing such an alternative ensures that the
// {@link ParserRuleContext} returned by the calling rule will be complete
// and valid, and the syntax error will be reported later at a more
// localized location.</p>
//
// <ul>
// <li>If a syntactically valid path or paths reach the end of the decision rule and
// they are semantically valid if predicated, return the min associated alt.</li>
// <li>Else, if a semantically invalid but syntactically valid path exist
// or paths exist, return the minimum associated alt.
// </li>
// <li>Otherwise, return {@link ATN//INVALID_ALT_NUMBER}.</li>
// </ul>
//
// <p>
// In some scenarios, the algorithm described above could predict an
// alternative which will result in a {@link FailedPredicateException} in
// the parser. Specifically, this could occur if the <em>only</em> configuration
// capable of successfully parsing to the end of the decision rule is
// blocked by a semantic predicate. By choosing this alternative within
// {@link //AdaptivePredict} instead of panicing a
// {@link NoViableAltException}, the resulting
// {@link FailedPredicateException} in the parser will identify the specific
// predicate which is preventing the parser from successfully parsing the
// decision rule, which helps developers identify and correct logic errors
// in semantic predicates.
// </p>
//
// @param configs The ATN configurations which were valid immediately before
// the {@link //ERROR} state was reached
// @param outerContext The is the \gamma_0 initial parser context from the paper
// or the parser stack at the instant before prediction commences.
//
// @return The value to return from {@link //AdaptivePredict}, or
// {@link ATN//INVALID_ALT_NUMBER} if a suitable alternative was not
// identified and {@link //AdaptivePredict} should report an error instead.
//
func (this *ParserATNSimulator) getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(configs *ATNConfigSet, outerContext IParserRuleContext) int {
	var cfgs = this.splitAccordingToSemanticValidity(configs, outerContext)
	var semValidConfigs = cfgs[0]
	var semInvalidConfigs = cfgs[1]
	var alt = this.getAltThatFinishedDecisionEntryRule(semValidConfigs)
	if alt != ATNINVALID_ALT_NUMBER { // semantically/syntactically viable path exists
		return alt
	}
	// Is there a syntactically valid path with a failed pred?
	if len(semInvalidConfigs.configs) > 0 {
		alt = this.getAltThatFinishedDecisionEntryRule(semInvalidConfigs)
		if alt != ATNINVALID_ALT_NUMBER { // syntactically viable path exists
			return alt
		}
	}
	return ATNINVALID_ALT_NUMBER
}

func (this *ParserATNSimulator) getAltThatFinishedDecisionEntryRule(configs *ATNConfigSet) int {
	var alts = NewIntervalSet()

	for i := 0; i < len(configs.configs); i++ {
		var c = configs.configs[i]
		_, ok := c.GetState().(*RuleStopState)

		if c.getReachesIntoOuterContext() > 0 || (ok && c.getContext().hasEmptyPath()) {
			alts.addOne(c.getAlt())
		}
	}
	if alts.length() == 0 {
		return ATNINVALID_ALT_NUMBER
	} else {
		return alts.first()
	}
}

// Walk the list of configurations and split them according to
//  those that have preds evaluating to true/false.  If no pred, assume
//  true pred and include in succeeded set.  Returns Pair of sets.
//
//  Create a NewSet so as not to alter the incoming parameter.
//
//  Assumption: the input stream has been restored to the starting point
//  prediction, which is where predicates need to evaluate.

type ATNConfigSetPair struct {
	item0, item1 *ATNConfigSet
}

func (this *ParserATNSimulator) splitAccordingToSemanticValidity(configs *ATNConfigSet, outerContext IParserRuleContext) []*ATNConfigSet {
	var succeeded = NewATNConfigSet(configs.fullCtx)
	var failed = NewATNConfigSet(configs.fullCtx)

	for i := 0; i < len(configs.configs); i++ {
		var c = configs.configs[i]
		if c.getSemanticContext() != SemanticContextNONE {
			var predicateEvaluationResult = c.getSemanticContext().evaluate(this.parser, outerContext)
			if predicateEvaluationResult {
				succeeded.add(c, nil)
			} else {
				failed.add(c, nil)
			}
		} else {
			succeeded.add(c, nil)
		}
	}
	return []*ATNConfigSet{succeeded, failed}
}

// Look through a list of predicate/alt pairs, returning alts for the
//  pairs that win. A {@code NONE} predicate indicates an alt containing an
//  unpredicated config which behaves as "always true." If !complete
//  then we stop at the first predicate that evaluates to true. This
//  includes pairs with nil predicates.
//
func (this *ParserATNSimulator) evalSemanticContext(predPredictions []*PredPrediction, outerContext IParserRuleContext, complete bool) *BitSet {
	var predictions = NewBitSet()
	for i := 0; i < len(predPredictions); i++ {
		var pair = predPredictions[i]
		if pair.pred == SemanticContextNONE {
			predictions.add(pair.alt)
			if !complete {
				break
			}
			continue
		}
		var predicateEvaluationResult = pair.pred.evaluate(this.parser, outerContext)
		if ParserATNSimulatorprototypedebug || ParserATNSimulatorprototypedfa_debug {
			fmt.Println("eval pred " + pair.toString() + "=" + fmt.Sprint(predicateEvaluationResult))
		}
		if predicateEvaluationResult {
			if ParserATNSimulatorprototypedebug || ParserATNSimulatorprototypedfa_debug {
				fmt.Println("PREDICT " + fmt.Sprint(pair.alt))
			}
			predictions.add(pair.alt)
			if !complete {
				break
			}
		}
	}
	return predictions
}

// TODO: If we are doing predicates, there is no point in pursuing
//     closure operations if we reach a DFA state that uniquely predicts
//     alternative. We will not be caching that DFA state and it is a
//     waste to pursue the closure. Might have to advance when we do
//     ambig detection thought :(
//

func (this *ParserATNSimulator) closure(config IATNConfig, configs *ATNConfigSet, closureBusy *Set, collectPredicates, fullCtx, treatEofAsEpsilon bool) {
	var initialDepth = 0
	this.closureCheckingStopState(config, configs, closureBusy, collectPredicates,
		fullCtx, initialDepth, treatEofAsEpsilon)
}

func (this *ParserATNSimulator) closureCheckingStopState(config IATNConfig, configs *ATNConfigSet, closureBusy *Set, collectPredicates, fullCtx bool, depth int, treatEofAsEpsilon bool) {

	if ParserATNSimulatorprototypedebug {
		fmt.Println("closure(" + config.toString() + ")") //config.toString(this.parser,true) + ")")
		fmt.Println("configs(" + configs.toString() + ")")
		if config.getReachesIntoOuterContext() > 50 {
			panic("problem")
		}
	}

	_, ok := config.GetState().(*RuleStopState)
	if ok {
		// We hit rule end. If we have context info, use it
		// run thru all possible stack tops in ctx
		if !config.getContext().isEmpty() {
			for i := 0; i < config.getContext().length(); i++ {
				if config.getContext().getReturnState(i) == PredictionContextEMPTY_RETURN_STATE {
					if fullCtx {
						configs.add(NewATNConfig1(config, config.GetState(), PredictionContextEMPTY), this.mergeCache)
						continue
					} else {
						// we have no context info, just chase follow links (if greedy)
						if ParserATNSimulatorprototypedebug {
							fmt.Println("FALLING off rule " + this.getRuleName(config.GetState().getRuleIndex()))
						}
						this.closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon)
					}
					continue
				}
				returnState := this.atn.states[config.getContext().getReturnState(i)]
				newContext := config.getContext().GetParent(i) // "pop" return state

				c := NewATNConfig5(returnState, config.getAlt(), newContext, config.getSemanticContext())
				// While we have context to pop back from, we may have
				// gotten that context AFTER having falling off a rule.
				// Make sure we track that we are now out of context.
				c.setReachesIntoOuterContext(config.getReachesIntoOuterContext())
				this.closureCheckingStopState(c, configs, closureBusy, collectPredicates, fullCtx, depth-1, treatEofAsEpsilon)
			}
			return
		} else if fullCtx {
			// reached end of start rule
			configs.add(config, this.mergeCache)
			return
		} else {
			// else if we have no context info, just chase follow links (if greedy)
			if ParserATNSimulatorprototypedebug {
				fmt.Println("FALLING off rule " + this.getRuleName(config.GetState().getRuleIndex()))
			}
		}
	}
	this.closure_(config, configs, closureBusy, collectPredicates, fullCtx, depth, treatEofAsEpsilon)
}

// Do the actual work of walking epsilon edges//
func (this *ParserATNSimulator) closure_(config IATNConfig, configs *ATNConfigSet, closureBusy *Set, collectPredicates, fullCtx bool, depth int, treatEofAsEpsilon bool) {
	var p = config.GetState()
	// optimization
	if !p.getEpsilonOnlyTransitions() {
		configs.add(config, this.mergeCache)
		// make sure to not return here, because EOF transitions can act as
		// both epsilon transitions and non-epsilon transitions.
	}
	for i := 0; i < len(p.getTransitions()); i++ {
		var t = p.getTransitions()[i]
		_, ok := t.(*ActionTransition)
		var continueCollecting = collectPredicates && !ok
		var c = this.getEpsilonTarget(config, t, continueCollecting, depth == 0, fullCtx, treatEofAsEpsilon)
		if c != nil {
			if !t.getIsEpsilon() && closureBusy.add(c) != c {
				// avoid infinite recursion for EOF* and EOF+
				continue
			}
			var newDepth = depth
			t2, ok := t.(*EpsilonTransition)
			if ok {
				// target fell off end of rule mark resulting c as having dipped into outer context
				// We can't get here if incoming config was rule stop and we had context
				// track how far we dip into outer context.  Might
				// come in handy and we avoid evaluating context dependent
				// preds if this is > 0.

				if closureBusy.add(c) != c {
					// avoid infinite recursion for right-recursive rules
					continue
				}

				if this._dfa != nil && this._dfa.precedenceDfa {
					if t2.outermostPrecedenceReturn == this._dfa.atnStartState.getRuleIndex() {
						c.precedenceFilterSuppressed = true
					}
				}

				c.setReachesIntoOuterContext(c.getReachesIntoOuterContext() + 1)
				configs.dipsIntoOuterContext = true // TODO: can remove? only care when we add to set per middle of this method
				newDepth -= 1
				if ParserATNSimulatorprototypedebug {
					fmt.Println("dips into outer ctx: " + c.toString())
				}
			} else if _, ok := t.(*RuleTransition); ok {
				// latch when newDepth goes negative - once we step out of the entry context we can't return
				if newDepth >= 0 {
					newDepth += 1
				}
			}
			this.closureCheckingStopState(c, configs, closureBusy, continueCollecting, fullCtx, newDepth, treatEofAsEpsilon)
		}
	}
}

func (this *ParserATNSimulator) getRuleName(index int) string {
	if this.parser != nil && index >= 0 {
		return this.parser.getRuleNames()[index]
	} else {
		return "<rule " + fmt.Sprint(index) + ">"
	}
}

func (this *ParserATNSimulator) getEpsilonTarget(config IATNConfig, t ITransition, collectPredicates, inContext, fullCtx, treatEofAsEpsilon bool) *ATNConfig {

	switch t.getSerializationType() {
	case TransitionRULE:
		return this.ruleTransition(config, t.(*RuleTransition))
	case TransitionPRECEDENCE:
		return this.precedenceTransition(config, t.(*PrecedencePredicateTransition), collectPredicates, inContext, fullCtx)
	case TransitionPREDICATE:
		return this.predTransition(config, t.(*PredicateTransition), collectPredicates, inContext, fullCtx)
	case TransitionACTION:
		return this.actionTransition(config, t.(*ActionTransition))
	case TransitionEPSILON:
		return NewATNConfig4(config, t.getTarget())
	case TransitionATOM:
		// EOF transitions act like epsilon transitions after the first EOF
		// transition is traversed
		if treatEofAsEpsilon {
			if t.Matches(TokenEOF, 0, 1) {
				return NewATNConfig4(config, t.getTarget())
			}
		}
		return nil
	case TransitionRANGE:
		// EOF transitions act like epsilon transitions after the first EOF
		// transition is traversed
		if treatEofAsEpsilon {
			if t.Matches(TokenEOF, 0, 1) {
				return NewATNConfig4(config, t.getTarget())
			}
		}
		return nil
	case TransitionSET:
		// EOF transitions act like epsilon transitions after the first EOF
		// transition is traversed
		if treatEofAsEpsilon {
			if t.Matches(TokenEOF, 0, 1) {
				return NewATNConfig4(config, t.getTarget())
			}
		}
		return nil
	default:
		return nil
	}
}

func (this *ParserATNSimulator) actionTransition(config IATNConfig, t *ActionTransition) *ATNConfig {
	if ParserATNSimulatorprototypedebug {
		fmt.Println("ACTION edge " + strconv.Itoa(t.ruleIndex) + ":" + strconv.Itoa(t.actionIndex))
	}
	return NewATNConfig4(config, t.getTarget())
}

func (this *ParserATNSimulator) precedenceTransition(config IATNConfig,
	pt *PrecedencePredicateTransition, collectPredicates, inContext, fullCtx bool) *ATNConfig {

	if ParserATNSimulatorprototypedebug {
		fmt.Println("PRED (collectPredicates=" + fmt.Sprint(collectPredicates) + ") " +
			strconv.Itoa(pt.precedence) + ">=_p, ctx dependent=true")
		if this.parser != nil {
			fmt.Println("context surrounding pred is " + fmt.Sprint(this.parser.getRuleInvocationStack(nil)))
		}
	}
	var c *ATNConfig = nil
	if collectPredicates && inContext {
		if fullCtx {
			// In full context mode, we can evaluate predicates on-the-fly
			// during closure, which dramatically reduces the size of
			// the config sets. It also obviates the need to test predicates
			// later during conflict resolution.
			var currentPosition = this._input.index()
			this._input.seek(this._startIndex)
			var predSucceeds = pt.getPredicate().evaluate(this.parser, this._outerContext)
			this._input.seek(currentPosition)
			if predSucceeds {
				c = NewATNConfig4(config, pt.getTarget()) // no pred context
			}
		} else {
			newSemCtx := SemanticContextandContext(config.getSemanticContext(), pt.getPredicate())
			c = NewATNConfig3(config, pt.getTarget(), newSemCtx)
		}
	} else {
		c = NewATNConfig4(config, pt.getTarget())
	}
	if ParserATNSimulatorprototypedebug {
		fmt.Println("config from pred transition=" + c.toString())
	}
	return c
}

func (this *ParserATNSimulator) predTransition(config IATNConfig, pt *PredicateTransition, collectPredicates, inContext, fullCtx bool) *ATNConfig {

	if ParserATNSimulatorprototypedebug {
		fmt.Println("PRED (collectPredicates=" + fmt.Sprint(collectPredicates) + ") " + strconv.Itoa(pt.ruleIndex) +
			":" + strconv.Itoa(pt.predIndex) + ", ctx dependent=" + fmt.Sprint(pt.isCtxDependent))
		if this.parser != nil {
			fmt.Println("context surrounding pred is " + fmt.Sprint(this.parser.getRuleInvocationStack(nil)))
		}
	}
	var c *ATNConfig = nil
	if collectPredicates && ((pt.isCtxDependent && inContext) || !pt.isCtxDependent) {
		if fullCtx {
			// In full context mode, we can evaluate predicates on-the-fly
			// during closure, which dramatically reduces the size of
			// the config sets. It also obviates the need to test predicates
			// later during conflict resolution.
			var currentPosition = this._input.index()
			this._input.seek(this._startIndex)
			var predSucceeds = pt.getPredicate().evaluate(this.parser, this._outerContext)
			this._input.seek(currentPosition)
			if predSucceeds {
				c = NewATNConfig4(config, pt.getTarget()) // no pred context
			}
		} else {
			var newSemCtx = SemanticContextandContext(config.getSemanticContext(), pt.getPredicate())
			c = NewATNConfig3(config, pt.getTarget(), newSemCtx)
		}
	} else {
		c = NewATNConfig4(config, pt.getTarget())
	}
	if ParserATNSimulatorprototypedebug {
		fmt.Println("config from pred transition=" + c.toString())
	}
	return c
}

func (this *ParserATNSimulator) ruleTransition(config IATNConfig, t *RuleTransition) *ATNConfig {
	if ParserATNSimulatorprototypedebug {
		fmt.Println("CALL rule " + this.getRuleName(t.getTarget().getRuleIndex()) + ", ctx=" + config.getContext().toString())
	}
	var returnState = t.followState
	var newContext = SingletonPredictionContextcreate(config.getContext(), returnState.GetStateNumber())
	return NewATNConfig1(config, t.getTarget(), newContext)
}

func (this *ParserATNSimulator) getConflictingAlts(configs *ATNConfigSet) *BitSet {
	var altsets = PredictionModegetConflictingAltSubsets(configs)
	return PredictionModegetAlts(altsets)
}

// Sam pointed out a problem with the previous definition, v3, of
// ambiguous states. If we have another state associated with conflicting
// alternatives, we should keep going. For example, the following grammar
//
// s : (ID | ID ID?) ''
//
// When the ATN simulation reaches the state before '', it has a DFA
// state that looks like: [12|1|[], 6|2|[], 12|2|[]]. Naturally
// 12|1|[] and 12|2|[] conflict, but we cannot stop processing this node
// because alternative to has another way to continue, via [6|2|[]].
// The key is that we have a single state that has config's only associated
// with a single alternative, 2, and crucially the state transitions
// among the configurations are all non-epsilon transitions. That means
// we don't consider any conflicts that include alternative 2. So, we
// ignore the conflict between alts 1 and 2. We ignore a set of
// conflicting alts when there is an intersection with an alternative
// associated with a single alt state in the state&rarrconfig-list map.
//
// It's also the case that we might have two conflicting configurations but
// also a 3rd nonconflicting configuration for a different alternative:
// [1|1|[], 1|2|[], 8|3|[]]. This can come about from grammar:
//
// a : A | A | A B
//
// After Matching input A, we reach the stop state for rule A, state 1.
// State 8 is the state right before B. Clearly alternatives 1 and 2
// conflict and no amount of further lookahead will separate the two.
// However, alternative 3 will be able to continue and so we do not
// stop working on this state. In the previous example, we're concerned
// with states associated with the conflicting alternatives. Here alt
// 3 is not associated with the conflicting configs, but since we can continue
// looking for input reasonably, I don't declare the state done. We
// ignore a set of conflicting alts when we have an alternative
// that we still need to pursue.
//

func (this *ParserATNSimulator) getConflictingAltsOrUniqueAlt(configs *ATNConfigSet) *BitSet {
	var conflictingAlts *BitSet = nil
	if configs.uniqueAlt != ATNINVALID_ALT_NUMBER {
		conflictingAlts = NewBitSet()
		conflictingAlts.add(configs.uniqueAlt)
	} else {
		conflictingAlts = configs.conflictingAlts
	}
	return conflictingAlts
}

func (this *ParserATNSimulator) GetTokenName(t int) string {
	if t == TokenEOF {
		return "EOF"
	}
	if this.parser != nil && this.parser.GetLiteralNames() != nil {
		if t >= len(this.parser.GetLiteralNames()) {
			fmt.Println(strconv.Itoa(t) + " ttype out of range: " + strings.Join(this.parser.GetLiteralNames(), ","))
			//            fmt.Println(this.parser.getInputStream().GetTokens())
		} else {
			return this.parser.GetLiteralNames()[t] + "<" + strconv.Itoa(t) + ">"
		}
	}
	return "" + strconv.Itoa(t)
}

func (this *ParserATNSimulator) getLookaheadName(input TokenStream) string {
	return this.GetTokenName(input.LA(1))
}

// Used for debugging in AdaptivePredict around execATN but I cut
//  it out for clarity now that alg. works well. We can leave this
//  "dead" code for a bit.
//
func (this *ParserATNSimulator) dumpDeadEndConfigs(nvae *NoViableAltException) {

	panic("Not implemented")

	//    fmt.Println("dead end configs: ")
	//    var decs = nvae.deadEndConfigs
	//
	//    for i:=0; i<len(decs); i++ {
	//
	//    	var c = decs[i]
	//        var trans = "no edges"
	//        if (len(c.state.getTransitions())>0) {
	//            var t = c.state.getTransitions()[0]
	//            if t2, ok := t.(*AtomTransition); ok {
	//                trans = "Atom "+ this.GetTokenName(t2.label)
	//            } else if t3, ok := t.(SetTransition); ok {
	//                _, ok := t.(*NotSetTransition)
	//
	//                var s string
	//                if (ok){
	//                    s = "~"
	//                }
	//
	//                trans = s + "Set " + t3.set
	//            }
	//        }
	//        fmt.Errorf(c.toString(this.parser, true) + ":" + trans)
	//    }
}

func (this *ParserATNSimulator) noViableAlt(input TokenStream, outerContext IParserRuleContext, configs *ATNConfigSet, startIndex int) *NoViableAltException {
	return NewNoViableAltException(this.parser, input, input.get(startIndex), input.LT(1), configs, outerContext)
}

func (this *ParserATNSimulator) getUniqueAlt(configs *ATNConfigSet) int {
	var alt = ATNINVALID_ALT_NUMBER
	for i := 0; i < len(configs.configs); i++ {
		var c = configs.configs[i]
		if alt == ATNINVALID_ALT_NUMBER {
			alt = c.getAlt() // found first alt
		} else if c.getAlt() != alt {
			return ATNINVALID_ALT_NUMBER
		}
	}
	return alt
}

//
// Add an edge to the DFA, if possible. This method calls
// {@link //addDFAState} to ensure the {@code to} state is present in the
// DFA. If {@code from} is {@code nil}, or if {@code t} is outside the
// range of edges that can be represented in the DFA tables, this method
// returns without adding the edge to the DFA.
//
// <p>If {@code to} is {@code nil}, this method returns {@code nil}.
// Otherwise, this method returns the {@link DFAState} returned by calling
// {@link //addDFAState} for the {@code to} state.</p>
//
// @param dfa The DFA
// @param from The source state for the edge
// @param t The input symbol
// @param to The target state for the edge
//
// @return If {@code to} is {@code nil}, this method returns {@code nil}
// otherwise this method returns the result of calling {@link //addDFAState}
// on {@code to}
//
func (this *ParserATNSimulator) addDFAEdge(dfa *DFA, from_ *DFAState, t int, to *DFAState) *DFAState {
	if ParserATNSimulatorprototypedebug {
		fmt.Println("EDGE " + from_.toString() + " -> " + to.toString() + " upon " + this.GetTokenName(t))
	}
	if to == nil {
		return nil
	}
	to = this.addDFAState(dfa, to) // used existing if possible not incoming
	if from_ == nil || t < -1 || t > this.atn.maxTokenType {
		return to
	}
	if from_.edges == nil {
		from_.edges = make([]*DFAState, this.atn.maxTokenType+1+1)
	}
	from_.edges[t+1] = to // connect

	if ParserATNSimulatorprototypedebug {
		var names []string
		if this.parser != nil {
			names = this.parser.GetLiteralNames()
		}

		fmt.Println("DFA=\n" + dfa.toString(names, nil))
	}
	return to
}

//
// Add state {@code D} to the DFA if it is not already present, and return
// the actual instance stored in the DFA. If a state equivalent to {@code D}
// is already in the DFA, the existing state is returned. Otherwise this
// method returns {@code D} after adding it to the DFA.
//
// <p>If {@code D} is {@link //ERROR}, this method returns {@link //ERROR} and
// does not change the DFA.</p>
//
// @param dfa The dfa
// @param D The DFA state to add
// @return The state stored in the DFA. This will be either the existing
// state if {@code D} is already in the DFA, or {@code D} itself if the
// state was not already present.
//
func (this *ParserATNSimulator) addDFAState(dfa *DFA, D *DFAState) *DFAState {
	if D == ATNSimulatorERROR {
		return D
	}
	var hash = D.hashString()
	var existing, ok = dfa.GetStates()[hash]
	if ok {
		return existing
	}
	D.stateNumber = len(dfa.GetStates())
	if !D.configs.readOnly {
		D.configs.optimizeConfigs(this.ATNSimulator)
		D.configs.setReadonly(true)
	}
	dfa.GetStates()[hash] = D
	if ParserATNSimulatorprototypedebug {
		fmt.Println("adding NewDFA state: " + D.toString())
	}
	return D
}

func (this *ParserATNSimulator) reportAttemptingFullContext(dfa *DFA, conflictingAlts *BitSet, configs *ATNConfigSet, startIndex, stopIndex int) {
	if ParserATNSimulatorprototypedebug || ParserATNSimulatorprototyperetry_debug {
		var interval = NewInterval(startIndex, stopIndex+1)
		fmt.Println("reportAttemptingFullContext decision=" + strconv.Itoa(dfa.decision) + ":" + configs.toString() +
			", input=" + this.parser.GetTokenStream().GetTextFromInterval(interval))
	}
	if this.parser != nil {
		this.parser.getErrorListenerDispatch().reportAttemptingFullContext(this.parser, dfa, startIndex, stopIndex, conflictingAlts, configs)
	}
}

func (this *ParserATNSimulator) reportContextSensitivity(dfa *DFA, prediction int, configs *ATNConfigSet, startIndex, stopIndex int) {
	if ParserATNSimulatorprototypedebug || ParserATNSimulatorprototyperetry_debug {
		var interval = NewInterval(startIndex, stopIndex+1)
		fmt.Println("reportContextSensitivity decision=" + strconv.Itoa(dfa.decision) + ":" + configs.toString() +
			", input=" + this.parser.GetTokenStream().GetTextFromInterval(interval))
	}
	if this.parser != nil {
		this.parser.getErrorListenerDispatch().reportContextSensitivity(this.parser, dfa, startIndex, stopIndex, prediction, configs)
	}
}

// If context sensitive parsing, we know it's ambiguity not conflict//
func (this *ParserATNSimulator) reportAmbiguity(dfa *DFA, D *DFAState, startIndex, stopIndex int,
	exact bool, ambigAlts *BitSet, configs *ATNConfigSet) {
	if ParserATNSimulatorprototypedebug || ParserATNSimulatorprototyperetry_debug {
		var interval = NewInterval(startIndex, stopIndex+1)
		fmt.Println("reportAmbiguity " + ambigAlts.toString() + ":" + configs.toString() +
			", input=" + this.parser.GetTokenStream().GetTextFromInterval(interval))
	}
	if this.parser != nil {
		this.parser.getErrorListenerDispatch().reportAmbiguity(this.parser, dfa, startIndex, stopIndex, exact, ambigAlts, configs)
	}
}
