package antlr4

import (
	"fmt"
)

type LL1Analyzer struct {
	atn *ATN
}

func NewLL1Analyzer(atn *ATN) *LL1Analyzer {
	la := new(LL1Analyzer)
	la.atn = atn
	return la
}

//* Special value added to the lookahead sets to indicate that we hit
//  a predicate during analysis if {@code seeThruPreds==false}.
///
const (
	LL1AnalyzerHIT_PRED = TokenInvalidType
)

//*
// Calculates the SLL(1) expected lookahead set for each outgoing transition
// of an {@link ATNState}. The returned array has one element for each
// outgoing transition in {@code s}. If the closure from transition
// <em>i</em> leads to a semantic predicate before Matching a symbol, the
// element at index <em>i</em> of the result will be {@code nil}.
//
// @param s the ATN state
// @return the expected symbols for each outgoing transition of {@code s}.
func (la *LL1Analyzer) getDecisionLookahead(s IATNState) []*IntervalSet {
	if s == nil {
		return nil
	}
	var count = len(s.GetTransitions())
	var look = make([]*IntervalSet, count)
	for alt := 0; alt < count; alt++ {
		look[alt] = NewIntervalSet()
		var lookBusy = NewSet(nil, nil)
		var seeThruPreds = false // fail to get lookahead upon pred
		la._LOOK(s.GetTransitions()[alt].getTarget(), nil, PredictionContextEMPTY, look[alt], lookBusy, NewBitSet(), seeThruPreds, false)
		// Wipe out lookahead for la alternative if we found nothing
		// or we had a predicate when we !seeThruPreds
		if look[alt].length() == 0 || look[alt].contains(LL1AnalyzerHIT_PRED) {
			look[alt] = nil
		}
	}
	return look
}

//*
// Compute set of tokens that can follow {@code s} in the ATN in the
// specified {@code ctx}.
//
// <p>If {@code ctx} is {@code nil} and the end of the rule containing
// {@code s} is reached, {@link Token//EPSILON} is added to the result set.
// If {@code ctx} is not {@code nil} and the end of the outermost rule is
// reached, {@link Token//EOF} is added to the result set.</p>
//
// @param s the ATN state
// @param stopState the ATN state to stop at. This can be a
// {@link BlockEndState} to detect epsilon paths through a closure.
// @param ctx the complete parser context, or {@code nil} if the context
// should be ignored
//
// @return The set of tokens that can follow {@code s} in the ATN in the
// specified {@code ctx}.
///
func (la *LL1Analyzer) LOOK(s, stopState IATNState, ctx IRuleContext) *IntervalSet {
	var r = NewIntervalSet()
	var seeThruPreds = true // ignore preds get all lookahead
	var lookContext IPredictionContext
	if ctx != nil {
		lookContext = predictionContextFromRuleContext(s.GetATN(), ctx)
	}
	fmt.Println("DEBUG 5")
//	fmt.Println("DEBUG" + lookContext.String())
	fmt.Println(s)
	fmt.Println(stopState)
	fmt.Println(lookContext)
	fmt.Println(r)
	fmt.Println(seeThruPreds)
	fmt.Println("=====")
	la._LOOK(s, stopState, lookContext, r, NewSet(nil, nil), NewBitSet(), seeThruPreds, true)
	fmt.Println(r)
	return r
}

//*
// Compute set of tokens that can follow {@code s} in the ATN in the
// specified {@code ctx}.
//
// <p>If {@code ctx} is {@code nil} and {@code stopState} or the end of the
// rule containing {@code s} is reached, {@link Token//EPSILON} is added to
// the result set. If {@code ctx} is not {@code nil} and {@code addEOF} is
// {@code true} and {@code stopState} or the end of the outermost rule is
// reached, {@link Token//EOF} is added to the result set.</p>
//
// @param s the ATN state.
// @param stopState the ATN state to stop at. This can be a
// {@link BlockEndState} to detect epsilon paths through a closure.
// @param ctx The outer context, or {@code nil} if the outer context should
// not be used.
// @param look The result lookahead set.
// @param lookBusy A set used for preventing epsilon closures in the ATN
// from causing a stack overflow. Outside code should pass
// {@code NewSet<ATNConfig>} for la argument.
// @param calledRuleStack A set used for preventing left recursion in the
// ATN from causing a stack overflow. Outside code should pass
// {@code NewBitSet()} for la argument.
// @param seeThruPreds {@code true} to true semantic predicates as
// implicitly {@code true} and "see through them", otherwise {@code false}
// to treat semantic predicates as opaque and add {@link //HIT_PRED} to the
// result if one is encountered.
// @param addEOF Add {@link Token//EOF} to the result if the end of the
// outermost context is reached. This parameter has no effect if {@code ctx}
// is {@code nil}.


func (la *LL1Analyzer) __LOOK(s, stopState IATNState, ctx IPredictionContext, look *IntervalSet, lookBusy *Set, calledRuleStack *BitSet, seeThruPreds, addEOF bool, i int){

	returnState := la.atn.states[ctx.getReturnState(i)]

	removed := calledRuleStack.contains(returnState.GetRuleIndex())

	defer func() {
		if removed {
			calledRuleStack.add(returnState.GetRuleIndex())
		}
	}()

	calledRuleStack.remove(returnState.GetRuleIndex())
	la._LOOK(returnState, stopState, ctx.GetParent(i), look, lookBusy, calledRuleStack, seeThruPreds, addEOF)

}

func (la *LL1Analyzer) _LOOK(s, stopState IATNState, ctx IPredictionContext, look *IntervalSet, lookBusy *Set, calledRuleStack *BitSet, seeThruPreds, addEOF bool) {

	c := NewATNConfig6(s, 0, ctx)

	if lookBusy.contains(c) {
		return
	}

	lookBusy.add(c)

	if s == stopState {
		fmt.Println("DEBUG 6")
		if ctx == nil {
			look.addOne(TokenEpsilon)
			return
		} else if ctx.isEmpty() && addEOF {
			look.addOne(TokenEOF)
			return
		}
	}

	_, ok := s.(*RuleStopState)

	if ok {
		if ctx == nil {
			look.addOne(TokenEpsilon)
			return
		} else if ctx.isEmpty() && addEOF {
			look.addOne(TokenEOF)
			return
		}

		if ctx != PredictionContextEMPTY {
			fmt.Println("DEBUG 7")

			// run thru all possible stack tops in ctx
			for i := 0; i < ctx.length(); i++ {

				returnState := la.atn.states[ctx.getReturnState(i)]
				la.__LOOK(returnState, stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF, i)

			}
			return
		}
	}

	n := len(s.GetTransitions())

	for i := 0; i < n; i++ {
		t := s.GetTransitions()[i]

		if t1, ok := t.(*RuleTransition); ok {
			fmt.Println("DEBUG 8")

			if calledRuleStack.contains(t1.getTarget().GetRuleIndex()) {
				continue
			}

			newContext := SingletonPredictionContextCreate(ctx, t1.followState.GetStateNumber())

			la.___LOOK(stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF, t1)

			fmt.Println(look)
//
//			defer func() {
//				calledRuleStack.remove(t1.getTarget().GetRuleIndex())
//			}()
//
//			calledRuleStack.add(t1.getTarget().GetRuleIndex())
//			la._LOOK(t1.getTarget(), stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF)

		} else if t2, ok := t.(IAbstractPredicateTransition); ok {
			fmt.Println("DEBUG 9")
			if seeThruPreds {
				la._LOOK(t2.getTarget(), stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF)
			} else {
				look.addOne(LL1AnalyzerHIT_PRED)
			}
		} else if t.getIsEpsilon() {
			fmt.Println("DEBUG 10")
			la._LOOK(t.getTarget(), stopState, ctx, look, lookBusy, calledRuleStack, seeThruPreds, addEOF)
		} else if _, ok := t.(*WildcardTransition); ok {
			fmt.Println("DEBUG 11")
			look.addRange(TokenMinUserTokenType, la.atn.maxTokenType)
		} else {
			fmt.Println("DEBUG 12")
			set := t.getLabel()
			fmt.Println(set)
			if set != nil {
				if _, ok := t.(*NotSetTransition); ok {
					set = set.complement(TokenMinUserTokenType, la.atn.maxTokenType)
				}
				look.addSet(set)
			}
		}
	}
}

func (la *LL1Analyzer) ___LOOK(stopState IATNState, ctx IPredictionContext, look *IntervalSet, lookBusy *Set, calledRuleStack *BitSet, seeThruPreds, addEOF bool, t1 *RuleTransition) {

	newContext := SingletonPredictionContextCreate(ctx, t1.followState.GetStateNumber())

	defer func() {
		calledRuleStack.remove(t1.getTarget().GetRuleIndex())
	}()

	calledRuleStack.add(t1.getTarget().GetRuleIndex())
	la._LOOK(t1.getTarget(), stopState, newContext, look, lookBusy, calledRuleStack, seeThruPreds, addEOF)


}
