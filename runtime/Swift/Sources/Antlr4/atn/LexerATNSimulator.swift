/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// "dup" of ParserInterpreter
/// 

open class LexerATNSimulator: ATNSimulator {
    public static let debug = false
    public let dfa_debug = false

    public static let MIN_DFA_EDGE = 0
    public static let MAX_DFA_EDGE = 127  // forces unicode to stay in ATN

    /// 
    /// When we hit an accept state in either the DFA or the ATN, we
    /// have to notify the character stream to start buffering characters
    /// via _org.antlr.v4.runtime.IntStream#mark_ and record the current state. The current sim state
    /// includes the current index into the input, the current line,
    /// and current character position in that line. Note that the Lexer is
    /// tracking the starting line and characterization of the token. These
    /// variables track the "state" of the simulator when it hits an accept state.
    /// 
    /// We track these variables separately for the DFA and ATN simulation
    /// because the DFA simulation often has to fail over to the ATN
    /// simulation. If the ATN simulation fails, we need the DFA to fall
    /// back to its previously accepted state, if any. If the ATN succeeds,
    /// then the ATN does the accept and the DFA simulator that invoked it
    /// can simply return the predicted token type.
    /// 

    internal class SimState {
        internal var index: Int = -1
        internal var line: Int = 0
        internal var charPos: Int = -1
        internal var dfaState: DFAState?

        internal func reset() {
            index = -1
            line = 0
            charPos = -1
            dfaState = nil
        }
    }


    internal weak var recog: Lexer?

    /// 
    /// The current token's starting index into the character stream.
    /// Shared across DFA to ATN simulation in case the ATN fails and the
    /// DFA did not have a previous accept state. In this case, we use the
    /// ATN-generated exception object.
    /// 
    internal var startIndex = -1

    /// 
    /// line number 1..n within the input
    /// 
    public var line = 1

    /// 
    /// The index of the character relative to the beginning of the line 0..n-1
    /// 
    public var charPositionInLine = 0

    public private(set) final var decisionToDFA: [DFA]
    
    internal var mode = Lexer.DEFAULT_MODE
    
    /// 
    /// mutex for DFAState change
    /// 
    private let dfaStateMutex = Mutex()
    
    /// 
    /// mutex for changes to all DFAStates map
    /// 
    private let dfaStatesMutex = Mutex()

    /// 
    /// Used during DFA/ATN exec to record the most recent accept configuration info
    /// 

    internal final var prevAccept = SimState()

    public static var match_calls = 0

    public convenience init(_ atn: ATN, _ decisionToDFA: [DFA],
        _ sharedContextCache: PredictionContextCache) {
            self.init(nil, atn, decisionToDFA, sharedContextCache)
    }

    public init(_ recog: Lexer?, _ atn: ATN,
        _ decisionToDFA: [DFA],
        _ sharedContextCache: PredictionContextCache) {

            self.decisionToDFA = decisionToDFA
            self.recog = recog
            super.init(atn, sharedContextCache)
    }

    open func copyState(_ simulator: LexerATNSimulator) {
        self.charPositionInLine = simulator.charPositionInLine
        self.line = simulator.line
        self.mode = simulator.mode
        self.startIndex = simulator.startIndex
    }

    open func match(_ input: CharStream, _ mode: Int) throws -> Int {
        LexerATNSimulator.match_calls += 1

        self.mode = mode
        var mark = input.mark()
        defer {
            try! input.release(mark)
        }

        self.startIndex = input.index()
        self.prevAccept.reset()
        let dfa = decisionToDFA[mode]

        if let s0 = dfa.s0 {
            return try execATN(input, s0)
        }
        else {
            return try matchATN(input)
        }
    }

    override
    open func reset() {
        prevAccept.reset()
        startIndex = -1
        line = 1
        charPositionInLine = 0
        mode = Lexer.DEFAULT_MODE
    }

    override
    open func clearDFA() {
        for d in 0..<decisionToDFA.count {
            decisionToDFA[d] = DFA(atn.getDecisionState(d)!, d)
        }
    }

    internal func matchATN(_ input: CharStream) throws -> Int {
        let startState = atn.modeToStartState[mode]

        if LexerATNSimulator.debug {
            print("matchATN mode \(mode) start: \(startState)\n")
        }

        let old_mode = mode

        let s0_closure = try computeStartState(input, startState)
        let suppressEdge = s0_closure.hasSemanticContext
        s0_closure.hasSemanticContext = false

        let next = addDFAState(s0_closure)
        if !suppressEdge {
            decisionToDFA[mode].s0 = next
        }

        let predict = try execATN(input, next)

        if LexerATNSimulator.debug {
            print("DFA after matchATN: \(decisionToDFA[old_mode].toLexerString())")
        }

        return predict
    }

    internal func execATN(_ input: CharStream, _ ds0: DFAState) throws -> Int {
        //print("enter exec index "+input.index()+" from "+ds0.configs);
        if LexerATNSimulator.debug {
            print("start state closure=\(ds0.configs)\n")
        }

        if ds0.isAcceptState {
            // allow zero-length tokens
            captureSimState(prevAccept, input, ds0)
        }

        var t = try input.LA(1)

        var s = ds0 // s is current/from DFA state

        while true {
            // while more work
            if LexerATNSimulator.debug {
                print("execATN loop starting closure: \(s.configs)\n")
            }

            // As we move src->trg, src->trg, we keep track of the previous trg to
            // avoid looking up the DFA state again, which is expensive.
            // If the previous target was already part of the DFA, we might
            // be able to avoid doing a reach operation upon t. If s!=null,
            // it means that semantic predicates didn't prevent us from
            // creating a DFA state. Once we know s!=null, we check to see if
            // the DFA state has an edge already for t. If so, we can just reuse
            // it's configuration set; there's no point in re-computing it.
            // This is kind of like doing DFA simulation within the ATN
            // simulation because DFA simulation is really just a way to avoid
            // computing reach/closure sets. Technically, once we know that
            // we have a previously added DFA state, we could jump over to
            // the DFA simulator. But, that would mean popping back and forth
            // a lot and making things more complicated algorithmically.
            // This optimization makes a lot of sense for loops within DFA.
            // A character will take us back to an existing DFA state
            // that already has lots of edges out of it. e.g., .* in comments.
            var target: DFAState
            if let existingTarget = getExistingTargetState(s, t) {
                target = existingTarget
            } else {
                target = try computeTargetState(input, s, t)
            }

            if target == ATNSimulator.ERROR {
                break
            }

            // If this is a consumable input element, make sure to consume before
            // capturing the accept state so the input index, line, and char
            // position accurately reflect the state of the interpreter at the
            // end of the token.
            if t != BufferedTokenStream.EOF {
                try consume(input)
            }

            if target.isAcceptState {
                captureSimState(prevAccept, input, target)
                if t == BufferedTokenStream.EOF {
                    break
                }
            }

            t = try input.LA(1)
            s = target // flip; current DFA target becomes new src/from state
        }

        return try failOrAccept(prevAccept, input, s.configs, t)
    }

    /// 
    /// Get an existing target state for an edge in the DFA. If the target state
    /// for the edge has not yet been computed or is otherwise not available,
    /// this method returns `null`.
    /// 
    /// - parameter s: The current DFA state
    /// - parameter t: The next input symbol
    /// - returns: The existing target DFA state for the given input symbol
    /// `t`, or `null` if the target state for this edge is not
    /// already cached
    /// 

    internal func getExistingTargetState(_ s: DFAState, _ t: Int) -> DFAState? {
        if s.edges == nil || t < LexerATNSimulator.MIN_DFA_EDGE || t > LexerATNSimulator.MAX_DFA_EDGE {
            return nil
        }

        let target = s.edges[t - LexerATNSimulator.MIN_DFA_EDGE]
        if LexerATNSimulator.debug && target != nil {
            print("reuse state \(s.stateNumber) edge to \(target!.stateNumber)")
        }

        return target
    }

    /// 
    /// Compute a target state for an edge in the DFA, and attempt to add the
    /// computed state and corresponding edge to the DFA.
    /// 
    /// - parameter input: The input stream
    /// - parameter s: The current DFA state
    /// - parameter t: The next input symbol
    /// 
    /// - returns: The computed target DFA state for the given input symbol
    /// `t`. If `t` does not lead to a valid DFA state, this method
    /// returns _#ERROR_.
    /// 

    internal func computeTargetState(_ input: CharStream, _ s: DFAState, _ t: Int) throws -> DFAState {
        let reach = ATNConfigSet(true, isOrdered: true)

        // if we don't find an existing DFA state
        // Fill reach starting from closure, following t transitions

        try getReachableConfigSet(input, s.configs, reach, t)

        if reach.isEmpty() {
            // we got nowhere on t from s
            if !reach.hasSemanticContext {
                // we got nowhere on t, don't throw out this knowledge; it'd
                // cause a failover from DFA later.
                addDFAEdge(s, t, ATNSimulator.ERROR)
            }

            // stop when we can't match any more char
            return ATNSimulator.ERROR
        }

        // Add an edge from s to target DFA found/created for reach
        return addDFAEdge(s, t, reach)
    }

    internal func failOrAccept(_ prevAccept: SimState, _ input: CharStream,
        _ reach: ATNConfigSet, _ t: Int) throws -> Int {
            if let dfaState = prevAccept.dfaState {
                let lexerActionExecutor = dfaState.lexerActionExecutor
                try accept(input, lexerActionExecutor, startIndex,
                    prevAccept.index, prevAccept.line, prevAccept.charPos)
                return dfaState.prediction
            } else {
                // if no accept and EOF is first char, return EOF
                if t == BufferedTokenStream.EOF && input.index() == startIndex {
                    return CommonToken.EOF
                }
                throw ANTLRException.recognition(e: LexerNoViableAltException(recog, input, startIndex, reach))
            }
    }

    /// 
    /// Given a starting configuration set, figure out all ATN configurations
    /// we can reach upon input `t`. Parameter `reach` is a return
    /// parameter.
    /// 
    internal func getReachableConfigSet(_ input: CharStream, _ closureConfig: ATNConfigSet, _ reach: ATNConfigSet, _ t: Int) throws {
        // this is used to skip processing for configs which have a lower priority
        // than a config that already reached an accept state for the same rule
        var skipAlt = ATN.INVALID_ALT_NUMBER
        for c in closureConfig.configs {
            guard let c = c as? LexerATNConfig else {
                continue
            }
            let currentAltReachedAcceptState = (c.alt == skipAlt)
            if currentAltReachedAcceptState && c.hasPassedThroughNonGreedyDecision() {
                continue
            }

            if LexerATNSimulator.debug {
                print("testing \(getTokenName(t)) at \(c.toString(recog, true))\n")

            }

            let n = c.state.getNumberOfTransitions()
            for ti in 0..<n {
                // for each transition
                let trans = c.state.transition(ti)
                if let target = getReachableTarget(trans, t) {
                    var lexerActionExecutor = c.getLexerActionExecutor()
                    if lexerActionExecutor != nil {
                        lexerActionExecutor = lexerActionExecutor!.fixOffsetBeforeMatch(input.index() - startIndex)
                    }

                    let treatEofAsEpsilon = (t == BufferedTokenStream.EOF)
                    if try closure(input,
                        LexerATNConfig(c, target, lexerActionExecutor),
                        reach,
                        currentAltReachedAcceptState,
                        true,
                        treatEofAsEpsilon) {
                            // any remaining configs for this alt have a lower priority than
                            // the one that just reached an accept state.
                            skipAlt = c.alt
                            break
                    }
                }
            }
        }
    }

    internal func accept(_ input: CharStream, _ lexerActionExecutor: LexerActionExecutor?,
        _ startIndex: Int, _ index: Int, _ line: Int, _ charPos: Int) throws {
            if LexerATNSimulator.debug {
                print("ACTION \(String(describing: lexerActionExecutor))\n")
            }

            // seek to after last char in token
            try input.seek(index)
            self.line = line
            self.charPositionInLine = charPos
            //TODO: CHECK
            if let lexerActionExecutor = lexerActionExecutor, let recog = recog {
                try lexerActionExecutor.execute(recog, input, startIndex)
            }
    }


    internal func getReachableTarget(_ trans: Transition, _ t: Int) -> ATNState? {
        if trans.matches(t, Character.MIN_VALUE, Character.MAX_VALUE) {
            return trans.target
        }

        return nil
    }


    final func computeStartState(_ input: CharStream,
        _ p: ATNState) throws -> ATNConfigSet {
            let initialContext = PredictionContext.EMPTY
            let configs = ATNConfigSet(true, isOrdered: true)
            let length = p.getNumberOfTransitions()
            for i in 0..<length {
                let target = p.transition(i).target
                let c = LexerATNConfig(target, i + 1, initialContext)
                try closure(input, c, configs, false, false, false)
            }
            return configs
    }

    /// 
    /// Since the alternatives within any lexer decision are ordered by
    /// preference, this method stops pursuing the closure as soon as an accept
    /// state is reached. After the first accept state is reached by depth-first
    /// search from `config`, all other (potentially reachable) states for
    /// this rule would have a lower priority.
    /// 
    /// - returns: `true` if an accept state is reached, otherwise
    /// `false`.
    /// 
    @discardableResult
    final func closure(_ input: CharStream, _ config: LexerATNConfig, _ configs: ATNConfigSet, _ currentAltReachedAcceptState: Bool, _ speculative: Bool, _ treatEofAsEpsilon: Bool) throws -> Bool {
        var currentAltReachedAcceptState = currentAltReachedAcceptState
        if LexerATNSimulator.debug {
            print("closure(" + config.toString(recog, true) + ")")
        }

        if config.state is RuleStopState {
            if LexerATNSimulator.debug {
                if recog != nil {
                    print("closure at \(recog!.getRuleNames()[config.state.ruleIndex!]) rule stop \(config)\n")
                } else {
                    print("closure at rule stop \(config)\n")
                }
            }

            if config.context == nil || config.context!.hasEmptyPath() {
                if config.context == nil || config.context!.isEmpty() {
                    try configs.add(config)
                    return true
                } else {
                    try configs.add(LexerATNConfig(config, config.state, PredictionContext.EMPTY))
                    currentAltReachedAcceptState = true
                }
            }

            if let configContext = config.context , !configContext.isEmpty() {
                let length = configContext.size()
                for i in 0..<length {
                    if configContext.getReturnState(i) != PredictionContext.EMPTY_RETURN_STATE {
                        let newContext = configContext.getParent(i)! // "pop" return state
                        let returnState = atn.states[configContext.getReturnState(i)]
                        let c = LexerATNConfig(config, returnState!, newContext)
                        currentAltReachedAcceptState = try closure(input, c, configs, currentAltReachedAcceptState, speculative, treatEofAsEpsilon)
                    }
                }
            }

            return currentAltReachedAcceptState
        }

        // optimization
        if !config.state.onlyHasEpsilonTransitions() {
            if !currentAltReachedAcceptState || !config.hasPassedThroughNonGreedyDecision() {
                try configs.add(config)
            }
        }

        let p = config.state
        let length = p.getNumberOfTransitions()
        for i in 0..<length {
            let t = p.transition(i)
            if let c = try getEpsilonTarget(input, config, t, configs, speculative, treatEofAsEpsilon) {
                currentAltReachedAcceptState = try closure(input, c, configs, currentAltReachedAcceptState, speculative, treatEofAsEpsilon)
            }
        }

        return currentAltReachedAcceptState
    }

    // side-effect: can alter configs.hasSemanticContext

    final func getEpsilonTarget(_ input: CharStream,
        _ config: LexerATNConfig,
        _ t: Transition,
        _ configs: ATNConfigSet,
        _ speculative: Bool,
        _ treatEofAsEpsilon: Bool) throws -> LexerATNConfig? {
            var c: LexerATNConfig? = nil
            switch t.getSerializationType() {
            case Transition.RULE:
                let ruleTransition = t as! RuleTransition
                let newContext = SingletonPredictionContext.create(config.context, ruleTransition.followState.stateNumber)
                c = LexerATNConfig(config, t.target, newContext)
                break

            case Transition.PRECEDENCE:
                throw ANTLRError.unsupportedOperation(msg: "Precedence predicates are not supported in lexers.")


            case Transition.PREDICATE:
                /// 
                /// Track traversing semantic predicates. If we traverse,
                /// we cannot add a DFA state for this "reach" computation
                /// because the DFA would not test the predicate again in the
                /// future. Rather than creating collections of semantic predicates
                /// like v3 and testing them on prediction, v4 will test them on the
                /// fly all the time using the ATN not the DFA. This is slower but
                /// semantically it's not used that often. One of the key elements to
                /// this predicate mechanism is not adding DFA states that see
                /// predicates immediately afterwards in the ATN. For example,
                /// 
                /// a : ID {p1}? | ID {p2}? ;
                /// 
                /// should create the start state for rule 'a' (to save start state
                /// competition), but should not create target of ID state. The
                /// collection of ATN states the following ID references includes
                /// states reached by traversing predicates. Since this is when we
                /// test them, we cannot cash the DFA state target of ID.
                /// 
                let pt = t as! PredicateTransition
                if LexerATNSimulator.debug {
                    print("EVAL rule \(pt.ruleIndex):\(pt.predIndex)")
                }
                configs.hasSemanticContext = true
                if try evaluatePredicate(input, pt.ruleIndex, pt.predIndex, speculative) {
                    c = LexerATNConfig(config, t.target)
                }
                break

            case Transition.ACTION:
                if config.context == nil || config.context!.hasEmptyPath() {
                    // execute actions anywhere in the start rule for a token.
                    //
                    // TODO: if the entry rule is invoked recursively, some
                    // actions may be executed during the recursive call. The
                    // problem can appear when hasEmptyPath() is true but
                    // isEmpty() is false. In this case, the config needs to be
                    // split into two contexts - one with just the empty path
                    // and another with everything but the empty path.
                    // Unfortunately, the current algorithm does not allow
                    // getEpsilonTarget to return two configurations, so
                    // additional modifications are needed before we can support
                    // the split operation.
                    let lexerActionExecutor = LexerActionExecutor.append(config.getLexerActionExecutor(), atn.lexerActions[(t as! ActionTransition).actionIndex])
                    c = LexerATNConfig(config, t.target, lexerActionExecutor)
                    break
                } else {
                    // ignore actions in referenced rules
                    c = LexerATNConfig(config, t.target)
                    break
                }

            case Transition.EPSILON:
                c = LexerATNConfig(config, t.target)
                break

            case Transition.ATOM: fallthrough
            case Transition.RANGE: fallthrough
            case Transition.SET:
                if treatEofAsEpsilon {
                    if t.matches(BufferedTokenStream.EOF, Character.MIN_VALUE, Character.MAX_VALUE) {
                        c = LexerATNConfig(config, t.target)
                        break
                    }
                }

                break
            default:
                return c
            }

            return c
    }

    /// 
    /// Evaluate a predicate specified in the lexer.
    /// 
    /// If `speculative` is `true`, this method was called before
    /// _#consume_ for the matched character. This method should call
    /// _#consume_ before evaluating the predicate to ensure position
    /// sensitive values, including _org.antlr.v4.runtime.Lexer#getText_, _org.antlr.v4.runtime.Lexer#getLine_,
    /// and _org.antlr.v4.runtime.Lexer#getCharPositionInLine_, properly reflect the current
    /// lexer state. This method should restore `input` and the simulator
    /// to the original state before returning (i.e. undo the actions made by the
    /// call to _#consume_.
    /// 
    /// - parameter input: The input stream.
    /// - parameter ruleIndex: The rule containing the predicate.
    /// - parameter predIndex: The index of the predicate within the rule.
    /// - parameter speculative: `true` if the current index in `input` is
    /// one character before the predicate's location.
    /// 
    /// - returns: `true` if the specified predicate evaluates to
    /// `true`.
    /// 
    final func evaluatePredicate(_ input: CharStream, _ ruleIndex: Int, _ predIndex: Int, _ speculative: Bool) throws -> Bool {
        // assume true if no recognizer was provided
        guard let recog = recog else {
            return true
        }
        if !speculative {
            return try recog.sempred(nil, ruleIndex, predIndex)
        }

        var savedCharPositionInLine = charPositionInLine
        var savedLine = line
        var index = input.index()
        var marker = input.mark()
        do {
            try consume(input)
            defer
            {
                charPositionInLine = savedCharPositionInLine
                line = savedLine
                try! input.seek(index)
                try! input.release(marker)
            }

            return try recog.sempred(nil, ruleIndex, predIndex)
        }

    }

    final func captureSimState(_ settings: SimState,
        _ input: CharStream,
        _ dfaState: DFAState) {
            settings.index = input.index()
            settings.line = line
            settings.charPos = charPositionInLine
            settings.dfaState = dfaState
    }


    private final func addDFAEdge(_ from: DFAState,
        _ t: Int,
        _ q: ATNConfigSet) -> DFAState {
            /// 
            /// leading to this call, ATNConfigSet.hasSemanticContext is used as a
            /// marker indicating dynamic predicate evaluation makes this edge
            /// dependent on the specific input sequence, so the static edge in the
            /// DFA should be omitted. The target DFAState is still created since
            /// execATN has the ability to resynchronize with the DFA state cache
            /// following the predicate evaluation step.
            /// 
            /// TJP notes: next time through the DFA, we see a pred again and eval.
            /// If that gets us to a previously created (but dangling) DFA
            /// state, we can continue in pure DFA mode from there.
            /// 
            let suppressEdge = q.hasSemanticContext
            q.hasSemanticContext = false
            let to = addDFAState(q)

            if suppressEdge {
                return to
            }

            addDFAEdge(from, t, to)
            return to
    }

    private final func addDFAEdge(_ p: DFAState, _ t: Int, _ q: DFAState) {
        if t < LexerATNSimulator.MIN_DFA_EDGE || t > LexerATNSimulator.MAX_DFA_EDGE {
            // Only track edges within the DFA bounds
            return
        }

        if LexerATNSimulator.debug {
            print("EDGE \(p) -> \(q) upon \(t)")
        }

        dfaStateMutex.synchronized {
            if p.edges == nil {
                //  make room for tokens 1..n and -1 masquerading as index 0
                p.edges = [DFAState?](repeating: nil, count: LexerATNSimulator.MAX_DFA_EDGE - LexerATNSimulator.MIN_DFA_EDGE + 1)
            }
            p.edges[t - LexerATNSimulator.MIN_DFA_EDGE] = q // connect
        }
    }

    /// 
    /// Add a new DFA state if there isn't one with this set of
    /// configurations already. This method also detects the first
    /// configuration containing an ATN rule stop state. Later, when
    /// traversing the DFA, we will know which rule to accept.
    /// 

    final func addDFAState(_ configs: ATNConfigSet) -> DFAState {
        /// 
        /// the lexer evaluates predicates on-the-fly; by this point configs
        /// should not contain any configurations with unevaluated predicates.
        /// 
        assert(!configs.hasSemanticContext, "Expected: !configs.hasSemanticContext")

        let proposed = DFAState(configs)
        let firstConfigWithRuleStopState = configs.firstConfigWithRuleStopState

        if firstConfigWithRuleStopState != nil {
            proposed.isAcceptState = true
            proposed.lexerActionExecutor = (firstConfigWithRuleStopState as! LexerATNConfig).getLexerActionExecutor()
            proposed.prediction = atn.ruleToTokenType[firstConfigWithRuleStopState!.state.ruleIndex!]
        }

        let dfa = decisionToDFA[mode]

        return dfaStatesMutex.synchronized {
            if let existing = dfa.states[proposed] {
                return existing
            }

            let newState = proposed
            newState.stateNumber = dfa.states.count
            configs.setReadonly(true)
            newState.configs = configs
            dfa.states[newState] = newState
            return newState
        }
    }


    public final func getDFA(_ mode: Int) -> DFA {
        return decisionToDFA[mode]
    }

    /// 
    /// Get the text matched so far for the current token.
    /// 

    public func getText(_ input: CharStream) -> String {
        // index is first lookahead char, don't include.
        return try! input.getText(Interval.of(startIndex, input.index() - 1))
    }

    public func getLine() -> Int {
        return line
    }

    public func setLine(_ line: Int) {
        self.line = line
    }

    public func getCharPositionInLine() -> Int {
        return charPositionInLine
    }

    public func setCharPositionInLine(_ charPositionInLine: Int) {
        self.charPositionInLine = charPositionInLine
    }

    public func consume(_ input: CharStream) throws {
        let curChar = try input.LA(1)
        if String(Character(integerLiteral: curChar)) == "\n" {
            line += 1
            charPositionInLine = 0
        } else {
            charPositionInLine += 1
        }
        try  input.consume()
    }


    public func getTokenName(_ t: Int) -> String {
        if t == -1 {
            return "EOF"
        }
        //if ( atn.g!=null ) return atn.g.getTokenDisplayName(t);
        return "'" + String(Character(integerLiteral: t)) + "'"
    }
}
