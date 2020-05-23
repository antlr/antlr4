/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.LexerATNSimulator;

import std.conv;
import std.format;
import std.stdio;
import antlr.v4.runtime.atn.ATNSimulator;
import antlr.v4.runtime.IntStreamConstant;
import antlr.v4.runtime.Lexer;
import antlr.v4.runtime.UnsupportedOperationException;
import antlr.v4.runtime.dfa.DFA;
import antlr.v4.runtime.dfa.DFAState;
import antlr.v4.runtime.atn.ATN;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.ATNConfigSet;
import antlr.v4.runtime.atn.ATNConfig;
import antlr.v4.runtime.atn.LexerActionExecutor;
import antlr.v4.runtime.atn.LexerATNConfig;
import antlr.v4.runtime.atn.PredictionContext;
import antlr.v4.runtime.atn.RuleTransition;
import antlr.v4.runtime.atn.RuleStopState;
import antlr.v4.runtime.atn.PredictionContextCache;
import antlr.v4.runtime.atn.SimState;
import antlr.v4.runtime.atn.SingletonPredictionContext;
import antlr.v4.runtime.atn.PredicateTransition;
import antlr.v4.runtime.atn.Transition;
import antlr.v4.runtime.atn.TransitionStates;
import antlr.v4.runtime.atn.ActionTransition;
import antlr.v4.runtime.atn.OrderedATNConfigSet;
import antlr.v4.runtime.CharStream;
import antlr.v4.runtime.LexerNoViableAltException;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.misc;

/**
 * "dup" of ParserInterpreter
 */
class LexerATNSimulator : ATNSimulator
{

    public static immutable int MIN_DFA_EDGE = 0;

    /**
     * forces unicode to stay in ATN
     */
    public static immutable int MAX_DFA_EDGE = 127;

    protected Lexer recog;

    /**
     * @uml
     * The current token's starting index into the character stream.
     * Shared across DFA to ATN simulation in case the ATN fails and the
     * DFA did not have a previous accept state. In this case, we use the
     * ATN-generated exception object.
     */
    protected int startIndex = -1;

    /**
     * @uml
     * line number 1..n within the input
     */
    protected int line = 1;

    /**
     * @uml
     * The index of the character relative to the beginning of the line 0..n-1
     */
    protected int charPositionInLine = 0;

    public DFA[] decisionToDFA;

    protected int mode = Lexer.DEFAULT_MODE;

    protected SimState prevAccept;

    public static int match_calls = 0;

    public this(ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache)
    {
        this(null, atn, decisionToDFA,sharedContextCache);
    }

    public this(Lexer recog, ATN atn, DFA[] decisionToDFA, PredictionContextCache sharedContextCache)
    {
        super(atn, sharedContextCache);
        this.decisionToDFA = decisionToDFA;
        this.recog = recog;
    }

    public void copyState(LexerATNSimulator simulator)
    {
        this.charPositionInLine = simulator.charPositionInLine;
        this.line = simulator.line;
        this.mode = simulator.mode;
        this.startIndex = simulator.startIndex;
    }

    public int match(CharStream input, int mode)
    {
        match_calls++;
        this.mode = mode;
        int mark = input.mark;
        try {
            this.startIndex = to!int(input.index);
            this.prevAccept.reset;
            DFA dfa = decisionToDFA[mode];
            if (dfa.s0 is null) {
                return matchATN(input);
            }
            else {
                return execATN(input, dfa.s0);
            }
        }
        finally {
            input.release(mark);
        }
    }

    /**
     * @uml
     * @override
     */
    public override void reset()
    {
        prevAccept.reset();
        startIndex = -1;
        line = 1;
        charPositionInLine = 0;
        mode = Lexer.DEFAULT_MODE;
    }

    /**
     * @uml
     * @override
     */
    public override void clearDFA()
    {
        for (int d = 0; d < decisionToDFA.length; d++) {
            decisionToDFA[d] = new DFA(atn.getDecisionState(d), d);
        }
    }

    protected int matchATN(CharStream input)
    {
        ATNState startState = atn.modeToStartState[mode];
        debug(LexerATNSimulator)
            writefln!"matchATN mode %s start: %s"(mode, startState);
        int old_mode = mode;
        ATNConfigSet s0_closure = computeStartState(input, startState);
        bool suppressEdge = s0_closure.hasSemanticContext;
        s0_closure.hasSemanticContext = false;
        DFAState next = addDFAState(s0_closure);
        if (!suppressEdge) {
            decisionToDFA[mode].s0 = next;
        }
        int predict = execATN(input, next);

        debug(LexerATNSimulator)
            writefln("DFA after matchATN:\n%1$s\n", decisionToDFA[old_mode].toLexerString());
        return predict;
    }

    protected int execATN(CharStream input, DFAState ds0)
    {
        debug(LexerATNSimulator) {
            writefln("start state closure %s from %s", input.index, ds0.configs);
        }

        if (ds0.isAcceptState) {
            // allow zero-length tokens
            captureSimState(prevAccept, input, ds0);
        }
        auto t = input.LA(1);
        DFAState s = ds0; // s is current/from DFA state

        while (true) { // while more work
            debug(LexerATNSimulator) {
                writefln("execATN loop starting closure: %s\n", s.configs);
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
            DFAState target = getExistingTargetState(s, t);
            if (!target) {
                target = computeTargetState(input, s, t);
            }
            if (target is ERROR) {
                break;
            }
            // If this is a consumable input element, make sure to consume before
            // capturing the accept state so the input index, line, and char
            // position accurately reflect the state of the interpreter at the
            // end of the token.
            if (t != IntStreamConstant.EOF) {
                consume(input);
            }

            if (target.isAcceptState) {
                captureSimState(prevAccept, input, target);
                if (t == IntStreamConstant.EOF) {
                    break;
                }
            }
            t = input.LA(1);
            s = target; // flip; current DFA target becomes new src/from state
        }
        return failOrAccept(prevAccept, input, s.configs, t);
    }

    /**
     * @uml
     * Get an existing target state for an edge in the DFA. If the target state
     * for the edge has not yet been computed or is otherwise not available,
     * this method returns {@code null}.
     *
     *  @param s The current DFA state
     *  @param t The next input symbol
     *  @return The existing target DFA state for the given input symbol
     * {@code t}, or {@code null} if the target state for this edge is not
     * already cached
     */
    public DFAState getExistingTargetState(DFAState s, int t)
    {
        if (s.edges is null || t < MIN_DFA_EDGE || t > MAX_DFA_EDGE) {
            return null;
        }

        DFAState target = s.edges[t - MIN_DFA_EDGE];
        if (target !is null)
            debug(LexerATNSimulator) {
                writefln("reuse state %1$s"~
                         " edge to %2$s", s.stateNumber, target.stateNumber);
            }

        return target;
    }

    /**
     * @uml
     * Compute a target state for an edge in the DFA, and attempt to add the
     * computed state and corresponding edge to the DFA.
     *
     *  @param input The input stream
     *  @param s The current DFA state
     *  @param t The next input symbol
     *
     *  @return The computed target DFA state for the given input symbol
     * {@code t}. If {@code t} does not lead to a valid DFA state, this method
     * returns {@link #ERROR}.
     */
    protected DFAState computeTargetState(CharStream input, DFAState s, dchar t)
    {
        ATNConfigSet reach = new OrderedATNConfigSet();

        // if we don't find an existing DFA state
        // Fill reach starting from closure, following t transitions
        getReachableConfigSet(input, s.configs, reach, t);

        if (reach.isEmpty()) { // we got nowhere on t from s
            if (!reach.hasSemanticContext) {
                // we got nowhere on t, don't throw out this knowledge; it'd
                // cause a failover from DFA later.
                addDFAEdge(s, t, ERROR);
            }

            // stop when we can't match any more char
            return ERROR;
        }

        // Add an edge from s to target DFA found/created for reach
        return addDFAEdge(s, t, reach);
    }

    protected int failOrAccept(SimState prevAccept, CharStream input, ATNConfigSet reach,
                               dchar t)
    {
        if (prevAccept.dfaState) {
            LexerActionExecutor lexerActionExecutor = prevAccept.dfaState.lexerActionExecutor;
            accept(input, lexerActionExecutor, startIndex,
                   prevAccept.index, prevAccept.line, prevAccept.charPos);
            return prevAccept.dfaState.prediction;
        }
        else {
            // if no accept and EOF is first char, return EOF
            if (t == IntStreamConstant.EOF && input.index == startIndex) {
                return TokenConstantDefinition.EOF;
            }
            throw new LexerNoViableAltException(recog, input, startIndex, reach);
        }
    }

    /**
     * Given a starting configuration set, figure out all ATN configurations
     * we can reach upon input {@code t}. Parameter {@code reach} is a return
     * parameter.
     */
    protected void getReachableConfigSet(CharStream input, ATNConfigSet closureATNConfigSet, ATNConfigSet reach,
                                         dchar t)
    {
        // this is used to skip processing for configs which have a lower priority
        // than a config that already reached an accept state for the same rule
        int skipAlt = ATN.INVALID_ALT_NUMBER;
        foreach (ATNConfig c; closureATNConfigSet.configs) {
            bool currentAltReachedAcceptState = c.alt == skipAlt;
            if (currentAltReachedAcceptState && (cast(LexerATNConfig)c).hasPassedThroughNonGreedyDecision()) {
                continue;
            }

            debug(LexerATNSimulator) {
                writefln("testing %s at %s\n", getTokenName(t), c.toString(recog, true));
            }

            int n = c.state.getNumberOfTransitions();
            for (int ti=0; ti<n; ti++) {               // for each transition
                Transition trans = c.state.transition(ti);
                ATNState target = getReachableTarget(trans, t);
                if (target) {
                    LexerActionExecutor lexerActionExecutor = (cast(LexerATNConfig)c).getLexerActionExecutor();
                    if (lexerActionExecutor) {
                        lexerActionExecutor = lexerActionExecutor.fixOffsetBeforeMatch(input.index() - startIndex);
                    }

                    bool treatEofAsEpsilon = t == IntStreamConstant.EOF;
                    if (closure(input, new LexerATNConfig(cast(LexerATNConfig)c, target, lexerActionExecutor),
                                reach, currentAltReachedAcceptState, true, treatEofAsEpsilon)) {
                        // any remaining configs for this alt have a lower priority than
                        // the one that just reached an accept state.
                        skipAlt = c.alt;
                        break;
                    }
                }
            }
        }

    }

    protected void accept(CharStream input, LexerActionExecutor lexerActionExecutor, size_t startIndex,
                          size_t index, int line, int charPos)
    {
        debug(LexerATNSimulator) {
            writefln("ACTION %s\n", lexerActionExecutor);
        }

        // seek to after last char in token
        input.seek(index);
        this.line = line;
        this.charPositionInLine = charPos;

        if (lexerActionExecutor !is null && recog !is null) {
            lexerActionExecutor.execute(recog, input, startIndex);
        }
    }

    protected ATNState getReachableTarget(Transition trans, dchar t)
    {
        if (trans.matches(t, Lexer.MIN_CHAR_VALUE, Lexer.MAX_CHAR_VALUE)) {
            return trans.target;
        }
        return null;
    }

    protected ATNConfigSet computeStartState(CharStream input, ATNState p)
    {
        PredictionContext initialContext = cast(PredictionContext)PredictionContext.EMPTY;
        ATNConfigSet configs = new OrderedATNConfigSet();
        for (int i=0; i<p.getNumberOfTransitions(); i++) {
            ATNState target = p.transition(i).target;
            LexerATNConfig c = new LexerATNConfig(target, i+1, initialContext);
            closure(input, c, configs, false, false, false);
        }
        return configs;
    }

    /**
     * @uml
     * Since the alternatives within any lexer decision are ordered by
     * preference, this method stops pursuing the closure as soon as an accept
     * state is reached. After the first accept state is reached by depth-first
     * search from {@code config}, all other (potentially reachable) states for
     * this rule would have a lower priority.
     *
     *  @return {@code true} if an accept state is reached, otherwise
     * {@code false}.
     */
    protected bool closure(CharStream input, LexerATNConfig config, ATNConfigSet configs,
                           bool currentAltReachedAcceptState, bool speculative, bool treatEofAsEpsilon)
    {
        debug(LexerATNSimulator)
            writefln("closure(%s)", config);
        if (cast(RuleStopState)config.state) {
            debug(LexerATNSimulator)  {
                if (recog !is null) {
                    writefln("closure at %1$s rule stop %2$s\n", recog.getRuleNames()[config.state.ruleIndex], config);
                }
                else {
                    writefln("closure at rule stop %s\n", config);
                }
            }

            if (config.context is null || config.context.hasEmptyPath()) {
                if (config.context is null || config.context.isEmpty()) {
                    configs.add(config);
                    return true;
                }
                else {
                    configs.add(new LexerATNConfig(config,
                                                   config.state,
                                                   cast(PredictionContext)PredictionContext.EMPTY));
                    currentAltReachedAcceptState = true;
                }
            }
            if (config.context !is null && !config.context.isEmpty() ) {
                for (auto i = 0; i < config.context.size; i++) {
                    if (config.context.getReturnState(i) != PredictionContext.EMPTY_RETURN_STATE) {
                        PredictionContext newContext = config.context.getParent(i); // "pop" return state
                        ATNState returnState = atn.states[config.context.getReturnState(i)];
                        LexerATNConfig c = new LexerATNConfig(config, returnState, newContext);
                        currentAltReachedAcceptState = closure(input, c, configs, currentAltReachedAcceptState,
                                                               speculative, treatEofAsEpsilon);
                    }
                }
            }

            return currentAltReachedAcceptState;
        }

        // optimization
        if (!config.state.onlyHasEpsilonTransitions) {
            if (!currentAltReachedAcceptState || !config.hasPassedThroughNonGreedyDecision()) {
                configs.add(config);
            }
        }

        ATNState p = config.state;
        for (int i=0; i<p.getNumberOfTransitions(); i++) {
            Transition t = p.transition(i);
            LexerATNConfig c = getEpsilonTarget(input, config, t, configs, speculative, treatEofAsEpsilon);
            if (c !is null) {
                currentAltReachedAcceptState = closure(input, c, configs, currentAltReachedAcceptState,
                                                       speculative, treatEofAsEpsilon);
            }
        }
        return currentAltReachedAcceptState;
    }

    /**
     * side-effect: can alter configs.hasSemanticContext
     */
    protected LexerATNConfig getEpsilonTarget(CharStream input, LexerATNConfig config, Transition t,
                                              ref ATNConfigSet configs, bool speculative, bool treatEofAsEpsilon)
    {
        LexerATNConfig c = null;
        switch (t.getSerializationType) {
        case TransitionStates.RULE:
            RuleTransition ruleTransition = cast(RuleTransition)t;
            PredictionContext newContext =
                SingletonPredictionContext.create(config.context, ruleTransition.followState.stateNumber);
            c = new LexerATNConfig(config, t.target, newContext);
            break;
        case TransitionStates.PRECEDENCE:
            throw new UnsupportedOperationException("Precedence predicates are not supported in lexers.");
        case TransitionStates.PREDICATE:
            /*  Track traversing semantic predicates. If we traverse,
                we cannot add a DFA state for this "reach" computation
                because the DFA would not test the predicate again in the
                future. Rather than creating collections of semantic predicates
                like v3 and testing them on prediction, v4 will test them on the
                fly all the time using the ATN not the DFA. This is slower but
                semantically it's not used that often. One of the key elements to
                this predicate mechanism is not adding DFA states that see
                predicates immediately afterwards in the ATN. For example,

                a : ID {p1}? | ID {p2}? ;

                should create the start state for rule 'a' (to save start state
                competition), but should not create target of ID state. The
                collection of ATN states the following ID references includes
                states reached by traversing predicates. Since this is when we
                test them, we cannot cash the DFA state target of ID.
            */
            PredicateTransition pt = cast(PredicateTransition)t;
            debug(LexerATNSimulator) {
                writefln("EVAL rule %1$s:%2$s", pt.ruleIndex, pt.predIndex);
            }
            configs.hasSemanticContext = true;
            if (evaluatePredicate(input, pt.ruleIndex, pt.predIndex, speculative)) {
                c = new LexerATNConfig(config, t.target);
            }
            break;

        case TransitionStates.ACTION:
            if (config.context is null || config.context.hasEmptyPath()) {
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
                LexerActionExecutor lexerActionExecutor = LexerActionExecutor.append(config.getLexerActionExecutor(), atn.lexerActions[(cast(ActionTransition)t).actionIndex]);
                c = new LexerATNConfig(config, t.target, lexerActionExecutor);
                break;
            }
            else {
                // ignore actions in referenced rules
                c = new LexerATNConfig(config, t.target);
                break;
            }

        case TransitionStates.EPSILON:
            c = new LexerATNConfig(config, t.target);
            break;

        case TransitionStates.ATOM:
        case TransitionStates.RANGE:
        case TransitionStates.SET:
            if (treatEofAsEpsilon) {
                if (t.matches(IntStreamConstant.EOF, 0, 0xfffe)) {
                    c = new LexerATNConfig(config, t.target);
                    break;
                }
            }

            break;
        default: {}
        }

        return c;
    }

    /**
     * Evaluate a predicate specified in the lexer.
     *
     * If {@code speculative} is {@code true}, this method was called before
     * {@link #consume} for the matched character. This method should call
     * {@link #consume} before evaluating the predicate to ensure position
     * sensitive values, including {@link Lexer#getText}, {@link Lexer#getLine},
     * and {@link Lexer#getCharPositionInLine}, properly reflect the current
     * lexer state. This method should restore {@code input} and the simulator
     * to the original state before returning (i.e. undo the actions made by the
     * call to {@link #consume}).
     *
     *  @param input The input stream.
     *  @param ruleIndex The rule containing the predicate.
     *  @param predIndex The index of the predicate within the rule.
     *  @param speculative {@code true} if the current index in {@code input} is
     *  one character before the predicate's location.
     *
     *  @return {@code true} if the specified predicate evaluates to
     * {@code true}.
     */
    protected bool evaluatePredicate(CharStream input, int ruleIndex, int predIndex, bool speculative)
    {
        // assume true if no recognizer was provided
        if (recog is null) {
            return true;
        }

        if (!speculative) {
            return recog.sempred(null, ruleIndex, predIndex);
        }

        int savedCharPositionInLine = charPositionInLine;
        int savedLine = line;
        auto index = input.index();
        int marker = input.mark();
        try {
            consume(input);
            return recog.sempred(null, ruleIndex, predIndex);
        }
        finally {
            charPositionInLine = savedCharPositionInLine;
            line = savedLine;
            input.seek(index);
            input.release(marker);
        }
    }

    public void captureSimState(ref SimState settings, CharStream input, DFAState dfaState)
    {
        settings.index = to!int(input.index);
        settings.line = line;
        settings.charPos = charPositionInLine;
        settings.dfaState = dfaState;
    }

    protected DFAState addDFAEdge(DFAState from, int t, ATNConfigSet q)
    {
        /* leading to this call, ATNConfigSet.hasSemanticContext is used as a
         * marker indicating dynamic predicate evaluation makes this edge
         * dependent on the specific input sequence, so the static edge in the
         * DFA should be omitted. The target DFAState is still created since
         * execATN has the ability to resynchronize with the DFA state cache
         * following the predicate evaluation step.
         *
         * TJP notes: next time through the DFA, we see a pred again and eval.
         * If that gets us to a previously created (but dangling) DFA
         * state, we can continue in pure DFA mode from there.
         */
        bool suppressEdge = q.hasSemanticContext;
        q.hasSemanticContext = false;

        DFAState to = addDFAState(q);

        if (suppressEdge) {
            return to;
        }

        addDFAEdge(from, t, to);
        return to;
    }

    protected void addDFAEdge(DFAState p, int t, DFAState q)
    {
        if (t < MIN_DFA_EDGE || t > MAX_DFA_EDGE) {
            // Only track edges within the DFA bounds
            return;
        }

        debug(LexerATNSimulator) {
            writefln("EDGE %1$s -> %2$s upon %3$s", p, q, cast(dchar)t);
        }

        synchronized (p) {
            if (p.edges is null) {
                //  make room for tokens 1..n and -1 masquerading as index 0
                p.edges = new DFAState[MAX_DFA_EDGE-MIN_DFA_EDGE+1];
            }
            p.edges[t - MIN_DFA_EDGE] = q; // connect
        }
    }

    /**
     * Add a new DFA state if there isn't one with this set of
     * configurations already. This method also detects the first
     * configuration containing an ATN rule stop state. Later, when
     * traversing the DFA, we will know which rule to accept.
     */
    protected DFAState addDFAState(ATNConfigSet configs)
    {
        /* the lexer evaluates predicates on-the-fly; by this point configs
         * should not contain any configurations with unevaluated predicates.
         */
        assert(!configs.hasSemanticContext);
        DFAState proposed = new DFAState(configs);
        ATNConfig firstConfigWithRuleStopState;
        foreach (ATNConfig c; configs.configs) {
            if (cast(RuleStopState)c.state) {
                firstConfigWithRuleStopState = c;
                break;
            }
        }
        if (firstConfigWithRuleStopState) {
            proposed.isAcceptState = true;
            proposed.lexerActionExecutor = (cast(LexerATNConfig)firstConfigWithRuleStopState).getLexerActionExecutor();
            proposed.prediction = atn.ruleToTokenType[firstConfigWithRuleStopState.state.ruleIndex];
        }
        DFA dfa = decisionToDFA[mode];
        //DFAState existing = dfa.states[proposed];
        if (proposed in dfa.states)
            return dfa.states[proposed];
        DFAState newState = proposed;
        newState.stateNumber = to!int(dfa.states.length);
        configs.readonly(true);
        newState.configs = configs;
        dfa.states[newState] =  newState;
        return newState;
    }

    public DFA getDFA(int mode)
    {
        return decisionToDFA[mode];
    }

    public string getText(CharStream input)
    {
        // index is first lookahead char, don't include.
        return input.getText(Interval.of(startIndex, to!int(input.index) - 1));
    }

    public int getLine()
    {
        return line;
    }

    public void setLine(int line)
    {
        this.line = line;
    }

    public int getCharPositionInLine()
    {
        return charPositionInLine;
    }

    public void setCharPositionInLine(int charPositionInLine)
    {
        this.charPositionInLine = charPositionInLine;
    }

    public void consume(CharStream input)
    {
        int curChar = input.LA(1);
        if (curChar == '\n') {
            line++;
            charPositionInLine=0;
        } else {
            charPositionInLine++;
        }
        input.consume();
    }

    public string getTokenName(int t)
    {
        if (t == -1) return "EOF";
        //if ( atn.g!=null ) return atn.g.getTokenDisplayName(t);
        return format("'%s'", cast(dchar)t);
    }

}
