/*
 * [The "BSD license"]
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * Copyright (c) 2015 Janyou
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */



/** A parser simulator that mimics what ANTLR's generated
 *  parser code does. A ParserATNSimulator is used to make
 *  predictions via adaptivePredict but this class moves a pointer through the
 *  ATN to simulate parsing. ParserATNSimulator just
 *  makes us efficient rather than having to backtrack, for example.
 *
 *  This properly creates parse trees even for left recursive rules.
 *
 *  We rely on the left recursive rule invocation and special predicate
 *  transitions to make left recursive rules work.
 *
 *  See TestParserInterpreter for examples.
 */

public class ParserInterpreter: Parser {
    internal final var grammarFileName: String
    internal final var atn: ATN
    /** This identifies StarLoopEntryState's that begin the (...)*
     *  precedence loops of left recursive rules.
     */
    internal final var statesNeedingLeftRecursionContext: BitSet

    internal final var decisionToDFA: [DFA]
    // not shared like it is for generated parsers
    internal final var sharedContextCache: PredictionContextCache =
    PredictionContextCache()

    ////@Deprecated
    internal final var tokenNames: [String]
    internal final var ruleNames: [String]

    private final var vocabulary: Vocabulary

    /** Tracks LR rules for adjusting the contexts */
    internal final var _parentContextStack: Array<(ParserRuleContext?, Int)> =
    Array<(ParserRuleContext?, Int)>()

    /** We need a map from (decision,inputIndex)->forced alt for computing ambiguous
     *  parse trees. For now, we allow exactly one override.
     */
    internal var overrideDecision: Int = -1
    internal var overrideDecisionInputIndex: Int = -1
    internal var overrideDecisionAlt: Int = -1

    /** A copy constructor that creates a new parser interpreter by reusing
     *  the fields of a previous interpreter.
     *
     *  @since 4.5.1
     *
     *  @param old The interpreter to copy
     */
    public init(_ old: ParserInterpreter) throws {

        self.atn = old.atn
        self.grammarFileName = old.grammarFileName
        self.statesNeedingLeftRecursionContext = old.statesNeedingLeftRecursionContext
        self.decisionToDFA = old.decisionToDFA
        self.tokenNames = old.tokenNames
        self.ruleNames = old.ruleNames
        self.vocabulary = old.vocabulary
        try  super.init(old.getTokenStream()!)
        setInterpreter(ParserATNSimulator(self, atn,
                decisionToDFA,
                sharedContextCache))
    }

    /**
     * @deprecated Use {@link #ParserInterpreter(String, org.antlr.v4.runtime.Vocabulary, java.util.Collection, org.antlr.v4.runtime.atn.ATN, org.antlr.v4.runtime.TokenStream)} instead.
     */
    //@Deprecated
    public convenience init(_ grammarFileName: String, _ tokenNames: Array<String?>?,
                            _ ruleNames: Array<String>, _ atn: ATN, _ input: TokenStream) throws {
        try self.init(grammarFileName, Vocabulary.fromTokenNames(tokenNames), ruleNames, atn, input)
    }

    public init(_ grammarFileName: String, _ vocabulary: Vocabulary,
                _ ruleNames: Array<String>, _ atn: ATN, _ input: TokenStream) throws {

        self.grammarFileName = grammarFileName
        self.atn = atn
        self.tokenNames = [String]()//    new String[atn.maxTokenType];
        let length = tokenNames.count
        for i in 0..<length {
            tokenNames[i] = vocabulary.getDisplayName(i)
        }

        self.ruleNames = ruleNames
        self.vocabulary = vocabulary
        self.decisionToDFA = [DFA]() //new DFA[atn.getNumberOfDecisions()];
        let decisionToDFALength = decisionToDFA.count
        for i in 0..<decisionToDFALength {
            decisionToDFA[i] = DFA(atn.getDecisionState(i)!, i)
        }

        // identify the ATN states where pushNewRecursionContext() must be called
        self.statesNeedingLeftRecursionContext = try! BitSet(atn.states.count)
        for  state in atn.states {
            if let state = state as? StarLoopEntryState {
                if state.precedenceRuleDecision {
                    try! self.statesNeedingLeftRecursionContext.set(state.stateNumber)
                }
            }

        }
        try super.init(input)
        // get atn simulator that knows how to do predictions
        setInterpreter(ParserATNSimulator(self, atn,
                decisionToDFA,
                sharedContextCache))
    }

    override
    public func getATN() -> ATN {
        return atn
    }

//	override
    ////@Deprecated
    public func getTokenNames() -> [String] {
        return tokenNames
    }

    override
    public func getVocabulary() -> Vocabulary {
        return vocabulary
    }

    override
    public func getRuleNames() -> [String] {
        return ruleNames
    }

    override
    public func getGrammarFileName() -> String {
        return grammarFileName
    }

    /** Begin parsing at startRuleIndex */
    public func parse(_ startRuleIndex: Int) throws -> ParserRuleContext {
        let startRuleStartState: RuleStartState = atn.ruleToStartState[startRuleIndex]

        let rootContext: InterpreterRuleContext = InterpreterRuleContext(nil, ATNState.INVALID_STATE_NUMBER, startRuleIndex)
        if startRuleStartState.isPrecedenceRule {
            try    enterRecursionRule(rootContext, startRuleStartState.stateNumber, startRuleIndex, 0)
        } else {
            try enterRule(rootContext, startRuleStartState.stateNumber, startRuleIndex)
        }

        while true {
            let p: ATNState = getATNState()!
            switch p.getStateType() {
            case ATNState.RULE_STOP:
                // pop; return from rule
                if _ctx!.isEmpty() {
                    if startRuleStartState.isPrecedenceRule {
                        let result: ParserRuleContext = _ctx!
                        let parentContext: (ParserRuleContext?, Int) = _parentContextStack.pop()
                        try unrollRecursionContexts(parentContext.0!)
                        return result
                    } else {
                        try exitRule()
                        return rootContext
                    }
                }

                try visitRuleStopState(p)
                break

            default:
                do {
                    try self.visitState(p)
                }
                 catch ANTLRException.recognition(let e) {
                    setState(self.atn.ruleToStopState[p.ruleIndex!].stateNumber)
                    getContext()!.exception = e
                    getErrorHandler().reportError(self, e)
                    try getErrorHandler().recover(self, e)
                }

                break
            }
        }
    }

    override
    public func enterRecursionRule(_ localctx: ParserRuleContext, _ state: Int, _ ruleIndex: Int, _ precedence: Int) throws {
        let pair: (ParserRuleContext?, Int) = (_ctx, localctx.invokingState)
        _parentContextStack.push(pair)
        try super.enterRecursionRule(localctx, state, ruleIndex, precedence)
    }

    internal func getATNState() -> ATNState? {
        return atn.states[getState()]
    }

    internal func visitState(_ p: ATNState) throws {
        var altNum: Int
        if p.getNumberOfTransitions() > 1 {
            try getErrorHandler().sync(self)
            let decision: Int = (p as! DecisionState).decision
            if decision == overrideDecision && _input.index() == overrideDecisionInputIndex {
                altNum = overrideDecisionAlt
            } else {
                altNum = try getInterpreter().adaptivePredict(_input, decision, _ctx)
            }
        } else {
            altNum = 1
        }

        let transition: Transition = p.transition(altNum - 1)
        switch transition.getSerializationType() {
        case Transition.EPSILON:
            if try statesNeedingLeftRecursionContext.get(p.stateNumber) &&
                    !(transition.target is LoopEndState) {
                // We are at the start of a left recursive rule's (...)* loop
                // but it's not the exit branch of loop.
                let ctx: InterpreterRuleContext = InterpreterRuleContext(
                _parentContextStack.last!.0, //peek()
                        _parentContextStack.last!.1, //peek()

                        _ctx!.getRuleIndex())
                try    pushNewRecursionContext(ctx, atn.ruleToStartState[p.ruleIndex!].stateNumber, _ctx!.getRuleIndex())
            }
            break

        case Transition.ATOM:
            try match((transition as! AtomTransition).label)
            break

        case Transition.RANGE: fallthrough
        case Transition.SET: fallthrough
        case Transition.NOT_SET:
            if !transition.matches(try _input.LA(1), CommonToken.MIN_USER_TOKEN_TYPE, 65535) {
                try _errHandler.recoverInline(self)
            }
            try matchWildcard()
            break

        case Transition.WILDCARD:
            try matchWildcard()
            break

        case Transition.RULE:
            let ruleStartState: RuleStartState = transition.target as! RuleStartState
            let ruleIndex: Int = ruleStartState.ruleIndex!
            let ctx: InterpreterRuleContext = InterpreterRuleContext(_ctx, p.stateNumber, ruleIndex)
            if ruleStartState.isPrecedenceRule {
                try enterRecursionRule(ctx, ruleStartState.stateNumber, ruleIndex, (transition as! RuleTransition).precedence)
            } else {
                try enterRule(ctx, transition.target.stateNumber, ruleIndex)
            }
            break

        case Transition.PREDICATE:
            let predicateTransition: PredicateTransition = transition as! PredicateTransition
            if try !sempred(_ctx!, predicateTransition.ruleIndex, predicateTransition.predIndex) {

                throw try ANTLRException.recognition(e: FailedPredicateException(self))

            }

            break

        case Transition.ACTION:
            let actionTransition: ActionTransition = transition as! ActionTransition
            try action(_ctx, actionTransition.ruleIndex, actionTransition.actionIndex)
            break

        case Transition.PRECEDENCE:
            if !precpred(_ctx!, (transition as! PrecedencePredicateTransition).precedence) {

                throw try ANTLRException.recognition(e: FailedPredicateException(self, "precpred(_ctx,\((transition as! PrecedencePredicateTransition).precedence))"))

            }
            break

        default:
            throw ANTLRError.unsupportedOperation(msg: "Unrecognized ATN transition type.")

        }

        setState(transition.target.stateNumber)
    }

    internal func visitRuleStopState(_ p: ATNState) throws {
        let ruleStartState: RuleStartState = atn.ruleToStartState[p.ruleIndex!]
        if ruleStartState.isPrecedenceRule {
            let parentContext: (ParserRuleContext?, Int) = _parentContextStack.pop()
            try unrollRecursionContexts(parentContext.0!)
            setState(parentContext.1)
        } else {
            try exitRule()
        }

        let ruleTransition: RuleTransition = atn.states[getState()]!.transition(0) as! RuleTransition
        setState(ruleTransition.followState.stateNumber)
    }

    /** Override this parser interpreters normal decision-making process
     *  at a particular decision and input token index. Instead of
     *  allowing the adaptive prediction mechanism to choose the
     *  first alternative within a block that leads to a successful parse,
     *  force it to take the alternative, 1..n for n alternatives.
     *
     *  As an implementation limitation right now, you can only specify one
     *  override. This is sufficient to allow construction of different
     *  parse trees for ambiguous input. It means re-parsing the entire input
     *  in general because you're never sure where an ambiguous sequence would
     *  live in the various parse trees. For example, in one interpretation,
     *  an ambiguous input sequence would be matched completely in expression
     *  but in another it could match all the way back to the root.
     *
     *  s : e '!'? ;
     *  e : ID
     *    | ID '!'
     *    ;
     *
     *  Here, x! can be matched as (s (e ID) !) or (s (e ID !)). In the first
     *  case, the ambiguous sequence is fully contained only by the root.
     *  In the second case, the ambiguous sequences fully contained within just
     *  e, as in: (e ID !).
     *
     *  Rather than trying to optimize this and make
     *  some intelligent decisions for optimization purposes, I settled on
     *  just re-parsing the whole input and then using
     *  {link Trees#getRootOfSubtreeEnclosingRegion} to find the minimal
     *  subtree that contains the ambiguous sequence. I originally tried to
     *  record the call stack at the point the parser detected and ambiguity but
     *  left recursive rules create a parse tree stack that does not reflect
     *  the actual call stack. That impedance mismatch was enough to make
     *  it it challenging to restart the parser at a deeply nested rule
     *  invocation.
     *
     *  Only parser interpreters can override decisions so as to avoid inserting
     *  override checking code in the critical ALL(*) prediction execution path.
     *
     *  @since 4.5.1
     */
    public func addDecisionOverride(_ decision: Int, _ tokenIndex: Int, _ forcedAlt: Int) {
        overrideDecision = decision
        overrideDecisionInputIndex = tokenIndex
        overrideDecisionAlt = forcedAlt
    }
}
