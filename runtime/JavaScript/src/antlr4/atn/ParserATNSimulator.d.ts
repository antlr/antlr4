/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { DFA, PredPrediction } from "../dfa"
import { DFAState } from "../dfa/DFAState"
import { NoViableAltException } from "../error"
import { Parser } from "../Parser"
import { ParserRuleContext } from "../ParserRuleContext"
import { PredictionContextCache, PredictionContext } from "../PredictionContext"
import { RuleContext } from "../RuleContext"
import { TokenStream } from "../TokenStream"
import { DoubleDict, BitSet, Set } from "../Utils"

import { ATN } from "./ATN"
import { ATNConfig } from "./ATNConfig"
import { ATNConfigSet } from "./ATNConfigSet"
import { ATNSimulator } from "./ATNSimulator"
import { DecisionState, ATNState } from "./ATNState"
import { PredictionMode } from "./PredictionMode"
import { SemanticContext } from "./SemanticContext"
import { ActionTransition, PrecedencePredicateTransition, PredicateTransition, RuleTransition, Transition } from "./Transition"

export declare class ParserATNSimulator extends ATNSimulator {
    parser: Parser
    decisionToDFA: Array<DFA>
    predictionMode: PredictionMode
    debug: boolean
    debug_closure: boolean
    debug_add: boolean
    debug_list_atn_decisions: boolean
    dfa_debug: boolean
    retry_debug: boolean

    protected _input: TokenStream | null
    protected _startIndex: number
    protected _outerContext: ParserRuleContext | null
    protected _dfa: DFA | null
    protected mergeCache: DoubleDict<PredictionContext, PredictionContext, PredictionContext>

    constructor(parser: Parser, atn: ATN, decisionToDFA: Array<DFA>, sharedContextCache: PredictionContextCache)

    reset(): void
    adaptivePredict(input: TokenStream, decision: number, outerContext: ParserRuleContext): number
    getRuleNames(index: number): string
    precedenceTransition(config: ATNConfig, pt: PrecedencePredicateTransition, collectPredicates: boolean, inContext: boolean, fullCtx: boolean): ATNConfig | null
    getTokenName(t: number): string
    getLookaheadName(input: TokenStream): string
    dumpDeadEndConfigs(nvae: NoViableAltException): void

    protected execATN(dfa: DFA, s0: DFAState, input: TokenStream, startIndex: number, outerContext: ParserRuleContext): number | null
    protected getExistingTargetState(previousD: DFAState, t: number): DFAState | null
    protected computeTargetState(dfa: DFA, previousD: DFAState, t: number): DFAState
    protected predicateDFAState(dfaState: DFAState, decisionState: DecisionState): void
    protected execATNWithFullContext(dfa: DFA, D: DFAState, s0: ATNConfigSet, input: TokenStream, startIndex: number, outerContext: ParserATNSimulator): number | null
    protected computeReachSet(closure: ATNConfigSet, t: number, fullCtx: boolean): ATNConfigSet | null
    protected removeAllConfigsNotInRuleStopState(configs: ATNConfigSet, lookToEndOfRule: boolean): ATNConfigSet
    protected computeStartState(p: ATNState, ctx: RuleContext, fullCtx: boolean): ATNConfigSet
    protected applyPrecedenceFilter(configs: ATNConfigSet): ATNConfigSet
    protected getReachableTarget(trans: Transition, ttype: number): ATNState | null
    protected getPredsForAmbigAlts(ambigAlts: BitSet, configs: ATNConfigSet, nalts: number): Array<SemanticContext> | null
    protected getPredicatePredictions(ambigAlts: BitSet | null, altToPred: Array<SemanticContext>): Array<PredPrediction> | null
    protected getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule(configs: ATNConfigSet, outerContext: ParserRuleContext): number
    protected getAltThatFinishedDecisionEntryRule(configs: ATNConfigSet): number
    protected splitAccordingToSemanticValidity(configs: ATNConfigSet, outerContext: ParserRuleContext): [ATNConfigSet, ATNConfigSet]
    protected evalSemanticContext(predPredictions: Array<PredPrediction>, outerContext: ParserRuleContext, complete: boolean): BitSet
    protected closure(config: ATNConfig, configs: ATNConfigSet, closureBusy: Set<ATNConfig>, collectPredicates: boolean, fullCtx: boolean, treatEofAsEpsilon: boolean): void
    protected closureCheckingStopState(config: ATNConfig, configs: ATNConfigSet, closureBusy: Set<ATNConfig>, collectPredicates: boolean, fullCtx: boolean, depth: number, treatEofAsEpsilon: boolean): void
    protected closure_(config: ATNConfig, configs: ATNConfigSet, closureBusy: Set<ATNConfig>, collectPredicates: boolean, fullCtx: boolean, depth: number, treatEofAsEpsilon: boolean): void
    protected canDropLoopEntryEdgeInLeftRecursiveRule(config: ATNConfig): boolean
    protected getEpsilonTarget(config: ATNConfig, t: Transition, collectPredicates: boolean, inContext: boolean, fullCtx: boolean, treatEofAsEpsilon: boolean): ATNConfig | null
    protected actionTransition(config: ATNConfig, t: ActionTransition): ATNConfig
    protected predTransition(config: ATNConfig, pt: PredicateTransition, collectPredicates: boolean, inContext: boolean, fullCtx: boolean): ATNConfig | null
    protected ruleTransition(config: ATNConfig, t: RuleTransition): ATNConfig
    protected getConflictingAlts(configs: ATNConfigSet): BitSet
    protected getConflictingAltsOrUniqueAlt(configs: ATNConfigSet): BitSet
    protected noViableAlt(input: TokenStream, outerContext: ParserRuleContext, configs: ATNConfigSet, startIndex: number): NoViableAltException
    protected getUniqueAlt(configs: ATNConfigSet): number
    protected addDFAEdge(dfa: DFA, from_: DFAState, t: number, to: DFAState): DFAState
    protected addDFAEdge(dfa: DFA, from_: DFAState, t: number, to: null): null
    protected addDFAState(dfa: DFA, D: DFAState): DFAState
    protected reportAttemptingFullContext(dfa: DFA, conflictingAlts: BitSet | null, configs: ATNConfigSet, startIndex: number, stopIndex: number): void
    protected reportContextSensitivity(dfa: DFA, prediction: number, configs: ATNConfigSet, startIndex: number, stopIndex: number): void
    protected reportAmbiguity(dfa: DFA, D: DFAState, startIndex: number, stopIndex: number, exact: boolean, ambigAlts: BitSet | null, configs: ATNConfigSet): void
}
