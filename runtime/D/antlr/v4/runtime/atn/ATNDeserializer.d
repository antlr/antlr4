/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.ATNDeserializer;

import antlr.v4.runtime.IllegalArgumentException;
import antlr.v4.runtime.IllegalStateException;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.UnsupportedOperationException;
import antlr.v4.runtime.atn.ATN;
import antlr.v4.runtime.atn.ATNDeserializationOptions;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.ATNType;
import antlr.v4.runtime.atn.ActionTransition;
import antlr.v4.runtime.atn.AtomTransition;
import antlr.v4.runtime.atn.BasicBlockStartState;
import antlr.v4.runtime.atn.BasicState;
import antlr.v4.runtime.atn.BlockEndState;
import antlr.v4.runtime.atn.BlockStartState;
import antlr.v4.runtime.atn.DecisionState;
import antlr.v4.runtime.atn.EpsilonTransition;
import antlr.v4.runtime.atn.LexerAction;
import antlr.v4.runtime.atn.LexerActionType;
import antlr.v4.runtime.atn.LexerChannelAction;
import antlr.v4.runtime.atn.LexerCustomAction;
import antlr.v4.runtime.atn.LexerModeAction;
import antlr.v4.runtime.atn.LexerMoreAction;
import antlr.v4.runtime.atn.LexerPopModeAction;
import antlr.v4.runtime.atn.LexerPushModeAction;
import antlr.v4.runtime.atn.LexerSkipAction;
import antlr.v4.runtime.atn.LexerTypeAction;
import antlr.v4.runtime.atn.LoopEndState;
import antlr.v4.runtime.atn.NotSetTransition;
import antlr.v4.runtime.atn.PlusBlockStartState;
import antlr.v4.runtime.atn.PlusLoopbackState;
import antlr.v4.runtime.atn.PrecedencePredicateTransition;
import antlr.v4.runtime.atn.PredicateTransition;
import antlr.v4.runtime.atn.RangeTransition;
import antlr.v4.runtime.atn.RuleStartState;
import antlr.v4.runtime.atn.RuleStopState;
import antlr.v4.runtime.atn.RuleTransition;
import antlr.v4.runtime.atn.SetTransition;
import antlr.v4.runtime.atn.StarBlockStartState;
import antlr.v4.runtime.atn.StarLoopEntryState;
import antlr.v4.runtime.atn.StarLoopbackState;
import antlr.v4.runtime.atn.StateNames;
import antlr.v4.runtime.atn.TokensStartState;
import antlr.v4.runtime.atn.Transition;
import antlr.v4.runtime.atn.TransitionStates;
import antlr.v4.runtime.atn.WildcardTransition;
import antlr.v4.runtime.misc;
import std.algorithm: canFind;
import std.algorithm: map;
import std.algorithm: reverse;
import std.array;
import std.conv;
import std.format;
import std.stdio;
import std.uuid;
import std.utf;

/**
 * TODO add class description
 */
class ATNDeserializer
{

    public static immutable int SERIALIZED_VERSION = 3;

    /**
     * This is the earliest supported serialized UUID.
     */
    private static UUID BASE_SERIALIZED_UUID;

    /**
     * This UUID indicates an extension of {@link BASE_SERIALIZED_UUID} for the
     * addition of precedence predicates.
     */
    private static UUID ADDED_PRECEDENCE_TRANSITIONS;

    /**
     * This UUID indicates an extension of {@link #ADDED_PRECEDENCE_TRANSITIONS}
     * for the addition of lexer actions encoded as a sequence of
     * {@link LexerAction} instances.
     */
    private static UUID ADDED_LEXER_ACTIONS;

    private static UUID ADDED_UNICODE_SMP;

    /**
     * This list contains all of the currently supported UUIDs, ordered by when
     * the feature first appeared in this branch.
     */
    private static UUID[] SUPPORTED_UUIDS;

    /**
     * This is the current serialized UUID.
     */
    public static UUID SERIALIZED_UUID;

    private ATNDeserializationOptions deserializationOptions;

    private int[] data;

    private int p;

    public UUID uuid;

    public static this()
    {
        /* WARNING: DO NOT MERGE THESE LINES. If UUIDs differ during a merge,
         * resolve the conflict by generating a new ID!
         */
        BASE_SERIALIZED_UUID = UUID("33761B2D-78BB-4A43-8B0B-4F5BEE8AACF3");
        ADDED_PRECEDENCE_TRANSITIONS = UUID("1DA0C57D-6C06-438A-9B27-10BCB3CE0F61");
        ADDED_LEXER_ACTIONS = UUID("AADB8D7E-AEEF-4415-AD2B-8204D6CF042E");
        ADDED_UNICODE_SMP = UUID("59627784-3BE5-417A-B9EB-8131A7286089");
        SUPPORTED_UUIDS ~= BASE_SERIALIZED_UUID;
        SUPPORTED_UUIDS ~= ADDED_PRECEDENCE_TRANSITIONS;
        SUPPORTED_UUIDS ~= ADDED_LEXER_ACTIONS;
        SUPPORTED_UUIDS ~= ADDED_UNICODE_SMP;

        SERIALIZED_UUID = ADDED_LEXER_ACTIONS;
    }

    public this()
    {
        this(ATNDeserializationOptions.defaultOptions);
    }

    public this(ATNDeserializationOptions deserializationOptions)
    {
        if (deserializationOptions is null) {
            deserializationOptions = new ATNDeserializationOptions();
        }
        this.deserializationOptions = deserializationOptions;
    }

    /**
     * Determines if a particular serialized representation of an ATN supports
     * a particular feature, identified by the {@link UUID} used for serializing
     * the ATN at the time the feature was first introduced.
     *
     *  @param feature The {@link UUID} marking the first time the feature was
     *  supported in the serialized ATN.
     *  @param actualUuid The {@link UUID} of the actual serialized ATN which is
     *  currently being deserialized.
     *  @return {@code true} if the {@code actualUuid} value represents a
     *  serialized ATN at or after the feature identified by {@code feature} was
     *  introduced; otherwise, {@code false}.
     */
    protected bool isFeatureSupported(UUID feature, UUID actualUuid)
    {
        bool not_found = true;
        foreach (uu; SUPPORTED_UUIDS) {
            if (not_found && uu == feature) {
                if (uu == actualUuid)
                    return true;
                else
                    not_found = false;
            }
            else {
                if (uu == actualUuid) {
                    return true;
                }
            }
        }
        return false;
    }

    public ATN deserialize(wstring input_data)
    {
        reset(input_data);
        checkVersion;
        checkUUID;
        ATN atn = readATN;
        readStates(atn);
        readRules(atn);
        readModes(atn);
        IntervalSet[] sets;
        readSets(sets, atn, &readInt);
        if (isFeatureSupported(ADDED_UNICODE_SMP, uuid))
        {
            readSets(sets, atn, &readInt32);
        }
        readEdges(atn, sets);
        readDecisions(atn);
        readLexerActions(atn);
        markPrecedenceDecisions(atn);
        if (deserializationOptions.verifyATN) {
            verifyATN(atn);
        }
        if (deserializationOptions.generateRuleBypassTransitions && atn.grammarType == ATNType.PARSER) {
            generateRuleBypassTransitions(atn);
        }
        if (deserializationOptions.optimize) {
            optimizeATN(atn);
        }
        identifyTailCalls(atn);
        return atn;
    }

    protected void verifyATN(ATN atn)
    {
        // verify assumptions
        foreach (ATNState state; atn.states) {
            if (state is null) {
                continue;
            }

            checkCondition(state.onlyHasEpsilonTransitions || state.getNumberOfTransitions <= 1);

            if (cast(PlusBlockStartState)state) {
                checkCondition((cast(PlusBlockStartState)state).loopBackState !is null);
            }

            if (cast(StarLoopEntryState)state) {
                StarLoopEntryState starLoopEntryState = cast(StarLoopEntryState)state;
                checkCondition(starLoopEntryState.loopBackState !is null);
                checkCondition(starLoopEntryState.getNumberOfTransitions() == 2);

                if (cast(StarBlockStartState)(starLoopEntryState.transition(0).target)) {
                    checkCondition(cast(LoopEndState)(starLoopEntryState.transition(1).target) !is null);
                    checkCondition(!starLoopEntryState.nonGreedy);
                }
                else if (cast(LoopEndState)(starLoopEntryState.transition(0).target)) {
                    checkCondition(cast(StarBlockStartState)(starLoopEntryState.transition(1).target) !is null);
                    checkCondition(starLoopEntryState.nonGreedy);
                }
                else {
                    throw new IllegalStateException();
                }
            }

            if (cast(StarLoopbackState)state) {
                checkCondition(state.getNumberOfTransitions() == 1);
                checkCondition(cast(StarLoopEntryState)(state.transition(0).target) !is null);
            }

            if (cast(LoopEndState)state) {
                checkCondition((cast(LoopEndState)state).loopBackState !is null);
            }

            if (cast(RuleStartState)state) {
                checkCondition((cast(RuleStartState)state).stopState !is null);
            }

            if (cast(BlockStartState)state) {
                checkCondition((cast(BlockStartState)state).endState !is null);
            }

            if (cast(BlockEndState)state) {
                checkCondition((cast(BlockEndState)state).startState !is null);
            }

            if (cast(DecisionState)state) {
                DecisionState decisionState = cast(DecisionState)state;
                checkCondition(decisionState.getNumberOfTransitions() <= 1 || decisionState.decision >= 0);
            }
            else {
                checkCondition(state.getNumberOfTransitions() <= 1 || state.classinfo == RuleStopState.classinfo);
            }
        }

    }

    private void readSets(ref IntervalSet[] sets, ATN aTN, int delegate() readUnicode)
    {
        // SETS
        int nsets = readInt;
        for (int i=0; i<nsets; i++) {
            IntervalSet set = new IntervalSet();
            sets ~= set;
            int nintervals = readInt;
            bool containsEof = readInt != 0;
            if (containsEof) {
                set.add(-1);
            }
            for (int j=0; j<nintervals; j++) {
                set.add(readUnicode(), readUnicode());
            }
        }
    }

    protected void markPrecedenceDecisions(ATN atn)
    {
        foreach (ATNState state; atn.states) {
            if (!cast(StarLoopEntryState)state) {
                continue;
            }

            /* We analyze the ATN to determine if this ATN decision state is the
             * decision for the closure block that determines whether a
             * precedence rule should continue or complete.
             */
            if (atn.ruleToStartState[state.ruleIndex].isLeftRecursiveRule) {
                ATNState maybeLoopEndState = state.transition(state.getNumberOfTransitions() - 1).target;
                if (cast(LoopEndState)maybeLoopEndState) {
                    if (maybeLoopEndState.epsilonOnlyTransitions &&
                        maybeLoopEndState.transitions[0].target.classinfo ==
                        RuleStopState.classinfo) {
                        (cast(StarLoopEntryState)state).isPrecedenceDecision = true;
                    }
                }
            }
        }

    }

    protected void checkCondition(bool condition)
    {
        checkCondition(condition, null);
    }

    protected void checkCondition(bool condition, string message)
    {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    protected Transition edgeFactory(ATN atn, int type, int src, int trg, int arg1, int arg2,
                                     int arg3, IntervalSet[] sets)
    {
    ATNState target = atn.states[trg];
        with(TransitionStates) {
            switch (type) {
            case EPSILON : return new EpsilonTransition(target);
            case RANGE :
                if (arg3 != 0) {
                    return new RangeTransition(target, TokenConstantDefinition.EOF, arg2);
                }
                else {
                    return new RangeTransition(target, arg1, arg2);
                }
            case RULE :
                RuleTransition rt = new RuleTransition(cast(RuleStartState)atn.states[arg1], arg2, arg3, target);
                return rt;
            case PREDICATE :
                PredicateTransition pt = new PredicateTransition(target, arg1, arg2, arg3 != 0);
                return pt;
            case PRECEDENCE:
                return new PrecedencePredicateTransition(target, arg1);
            case ATOM :
                if (arg3 != 0) {
                    return new AtomTransition(target, TokenConstantDefinition.EOF);
                }
                else {
                    return new AtomTransition(target, arg1);
                }
            case ACTION :
                ActionTransition a = new ActionTransition(target, arg1, arg2, arg3 != 0);
                return a;
            case SET : return new SetTransition(target, sets[arg1]);
            case NOT_SET : return new NotSetTransition(target, sets[arg1]);
            case WILDCARD : return new WildcardTransition(target);
            default: throw new IllegalArgumentException("The specified transition type is not valid.");
            }
        }
    }

    protected ATNState stateFactory(int type, int ruleIndex)
    {
        ATNState s;
        with(StateNames) {
            switch (type) {
            case INVALID: return null;
            case BASIC : s = new BasicState(); break;
            case RULE_START : s = new RuleStartState(); break;
            case BLOCK_START : s = new BasicBlockStartState(); break;
            case PLUS_BLOCK_START : s = new PlusBlockStartState(); break;
            case STAR_BLOCK_START : s = new StarBlockStartState(); break;
            case TOKEN_START : s = new TokensStartState(); break;
            case RULE_STOP : s = new RuleStopState(); break;
            case BLOCK_END : s = new BlockEndState(); break;
            case STAR_LOOP_BACK : s = new StarLoopbackState(); break;
            case STAR_LOOP_ENTRY : s = new StarLoopEntryState(); break;
            case PLUS_LOOP_BACK : s = new PlusLoopbackState(); break;
            case LOOP_END : s = new LoopEndState(); break;
            default :
                string message = format("The specified state type %d is not valid.", type);
                throw new IllegalArgumentException(message);
            }
        }
        s.ruleIndex = ruleIndex;
        return s;
    }

    protected LexerAction lexerActionFactory(LexerActionType type, int data1, int data2)
    {
        switch (type) {
        case LexerActionType.CHANNEL:
            return new LexerChannelAction(data1);

        case LexerActionType.CUSTOM:
            return new LexerCustomAction(data1, data2);

        case LexerActionType.MODE:
            return new LexerModeAction(data1);

        case LexerActionType.MORE:
            return cast(LexerAction)LexerMoreAction.instance;

        case LexerActionType.POP_MODE:
            return cast(LexerAction)LexerPopModeAction.instance;

        case LexerActionType.PUSH_MODE:
            return new LexerPushModeAction(data1);

        case LexerActionType.SKIP:
            return cast(LexerAction)LexerSkipAction.instance;

        case LexerActionType.TYPE:
            return new LexerTypeAction(data1);

        default:
            string message = format("The specified lexer action type %d is not valid.", type);
            throw new IllegalArgumentException(message);
        }
    }

    private void reset(const wstring atn)
    {
        wchar el;
        data.length = 0;

        for(int i; i < atn.length; i++) {
            el =  atn[i];
            if (el == '[') {
                if (i+7 >= atn.length) {
                    data ~=to!int('[') - 2;
                    continue;
                }
                if (atn[i+7] != ']') {
                    data ~=to!int('[') - 2;
                    continue;
                }
                if (hasSixOct(atn, i))
                {
                    data ~= readSixOct(atn, i) -2;
                    i += 7;
                    continue;
                }
            }
            if (el > 1)
                data ~= (el - 2) & 0xffff;
            else
                data ~= (el - 3) & 0xffff;
        }
        // don't adjust the first value since that's the version number
        data[0] = atn[0];
        p = 0;
    }

    private void checkVersion()
    {
        auto version_atn = readInt;
        if (version_atn != SERIALIZED_VERSION) {
            string reason = format("Could not deserialize ATN with version %d (expected %d).", version_atn, SERIALIZED_VERSION);
            assert(false, reason);
        }
    }

    private void checkUUID()
    {
        uuid = readUUID;
        if (!SUPPORTED_UUIDS.canFind(uuid)) {
            string reason = format("Could not deserialize ATN with UUID %s (expected %s or a legacy UUID).", uuid, SERIALIZED_UUID);
            assert(false, reason);
        }
    }

    private ATN readATN()
    {
        ATNType grammarType = cast(ATNType)readInt;
        int maxTokenType = readInt;
        return new ATN(grammarType, maxTokenType);
    }

    private void readStates(ATN atn)
    {
        // STATES
        int[LoopEndState][] loopBackStateNumbers;
        int[BlockStartState][] endStateNumbers;
        int nstates = readInt;
        debug(deserializer)
            writefln("%s: Number of states %s", this.p-1 , nstates);
        for (int i=0; i<nstates; i++) {
            int stype = readInt;
            // ignore bad type of states
            if (stype == StateNames.INVALID) {
                atn.addState(null);
                continue;
            }

            int ruleIndex = readInt;
            if (ruleIndex == wchar.max) {
                ruleIndex = -1;
            }
            debug(deserializer)
                writefln("%s: readState %s, ruleIndex %s, index %s",
                         this.p-1,
                         stype,
                         ruleIndex,
                         i);
            ATNState s = stateFactory(stype, ruleIndex);
            if (stype == StateNames.LOOP_END) {
                // special case
                int loopBackStateNumber = readInt;
                int[LoopEndState] _a;
                _a[cast(LoopEndState)s] = loopBackStateNumber;
                loopBackStateNumbers ~= _a;
            }
            else
                if (cast(BlockStartState)s) {
                    int endStateNumber = readInt;
                    int[BlockStartState] _a;
                    _a[cast(BlockStartState)s] = endStateNumber;
                    endStateNumbers ~= _a;
                }
            atn.addState(s);
        }

        // delay the assignment of loop back and end states until we know all the state instances have been initialized
        foreach (ref pair; loopBackStateNumbers) {
            pair.keys[0].loopBackState = atn.states[pair[pair.keys[0]]];
        }

        foreach (ref pair; endStateNumbers) {
            pair.keys[0].endState = cast(BlockEndState)atn.states[pair[pair.keys[0]]];
        }

        int numNonGreedyStates = readInt;
        for (int i = 0; i < numNonGreedyStates; i++) {
            int stateNumber = readInt;
            (cast(DecisionState)atn.states[stateNumber]).nonGreedy = true;
        }

        int numPrecedenceStates = readInt;
        for (int i = 0; i < numPrecedenceStates; i++) {
            int stateNumber = readInt;
            (cast(RuleStartState)atn.states[stateNumber]).isLeftRecursiveRule = true;
        }

    }

    private void readRules(ATN atn)
    {
        // RULES
        int nrules = readInt;

        if ( atn.grammarType == ATNType.LEXER ) {
            atn.ruleToTokenType = new int[nrules];
        }

        atn.ruleToStartState = new RuleStartState[nrules];
        for (int i=0; i<nrules; i++) {
            int s = readInt;
            RuleStartState startState = cast(RuleStartState)atn.states[s];
            atn.ruleToStartState[i] = startState;
            if ( atn.grammarType == ATNType.LEXER ) {
                int tokenType = readInt;
                if (tokenType == 0xFFFF) {
                    tokenType = TokenConstantDefinition.EOF;
                }

                atn.ruleToTokenType[i] = tokenType;
            }
        }

        atn.ruleToStopState = new RuleStopState[nrules];
        foreach (ATNState state; atn.states) {
            if (!cast(RuleStopState)state) {
                continue;
            }
            RuleStopState stopState = cast(RuleStopState)state;
            atn.ruleToStopState[state.ruleIndex] = stopState;
            atn.ruleToStartState[state.ruleIndex].stopState = stopState;
        }

    }

    public void readModes(ATN atn)
    {
        // MODES
        int nmodes = readInt;
        for (int i=0; i<nmodes; i++) {
            int s = readInt;
            atn.modeToStartState ~= cast(TokensStartState)atn.states[s];
        }
    }

    private void readEdges(ATN atn, IntervalSet[] sets)
    {
        // EDGES

        int nedges = readInt;
        for (int i=0; i<nedges; i++) {
            int src = readInt;
            int trg = readInt;
            int ttype = readInt;
            int arg1 = readInt;
            int arg2 = readInt;
            int arg3 = readInt;
            Transition trans = edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
            debug(deserializer) {
                writefln!"EDGES %1$s %2$s->%3$s %4$s %5$s %6$s %7$s"
                    (
                         trans.classinfo, src,
                         trg,
                         Transition.serializationNames[ttype],
                         arg1, arg2, arg3
                    );
            }
            ATNState srcState = atn.states[src];
            srcState.addTransition(trans);
        }

        // edges for rule stop states can be derived, so they aren't serialized
        foreach (ATNState state; atn.states) {
            for (int i = 0; i < state.getNumberOfTransitions(); i++) {
                Transition t = state.transition(i);
                if (!cast(RuleTransition)t) {
                    continue;
                }

                RuleTransition ruleTransition = cast(RuleTransition)t;
                int outermostPrecedenceReturn = -1;
                if (atn.ruleToStartState[ruleTransition.target.ruleIndex].isLeftRecursiveRule) {
                    if (ruleTransition.precedence == 0) {
                        outermostPrecedenceReturn = ruleTransition.target.ruleIndex;
                    }
                }
                EpsilonTransition returnTransition = new EpsilonTransition(ruleTransition.followState, outermostPrecedenceReturn);
                atn.ruleToStopState[ruleTransition.target.ruleIndex].addTransition(returnTransition);
            }
        }

        foreach (ATNState state; atn.states) {
            if (cast(BlockStartState)state) {
                // we need to know the end state to set its start state
                if ((cast(BlockStartState)state).endState is null) {
                    throw new IllegalStateException();
                }

                // block end states can only be associated to a single block start state
                if ((cast(BlockStartState)state).endState.startState !is null) {
                    throw new IllegalStateException();
                }

                (cast(BlockStartState)state).endState.startState = cast(BlockStartState)state;
            }

            if (cast(PlusLoopbackState)state) {
                PlusLoopbackState loopbackState = cast(PlusLoopbackState)state;
                for (int i = 0; i < loopbackState.getNumberOfTransitions(); i++) {
                    ATNState target = loopbackState.transition(i).target;
                    if (cast(PlusBlockStartState)target) {
                        (cast(PlusBlockStartState)target).loopBackState = loopbackState;
                    }
                }
            }
            else if (cast(StarLoopbackState)state) {
                StarLoopbackState loopbackState = cast(StarLoopbackState)state;
                for (int i = 0; i < loopbackState.getNumberOfTransitions(); i++) {
                    ATNState target = loopbackState.transition(i).target;
                    if (cast(StarLoopEntryState)target) {
                        (cast(StarLoopEntryState)target).loopBackState = loopbackState;
                    }
                }
            }
        }

    }

    private void readDecisions(ATN atn)
    {
        // DECISIONS

        int ndecisions = readInt;
        for (int i=1; i<=ndecisions; i++) {
            int s = readInt;
            DecisionState decState = cast(DecisionState)atn.states[s];
            atn.decisionToState ~= decState;
            decState.decision = i-1;
        }
    }

    private void readLexerActions(ATN atn)
    {
        // LEXER ACTIONS

        if (atn.grammarType == ATNType.LEXER) {
            atn.lexerActions = new LexerAction[readInt];
            for (int i = 0; i < atn.lexerActions.length; i++) {
                LexerActionType actionType = cast(LexerActionType)readInt;
                int data1 = readInt;
                if (data1 == 0xFFFF) {
                    data1 = -1;
                }

                int data2 = readInt;
                if (data2 == 0xFFFF) {
                    data2 = -1;
                }
                LexerAction lexerAction = lexerActionFactory(actionType, data1, data2);
                atn.lexerActions[i] = lexerAction;
            }
        }
    }

    private void optimizeATN(ATN atn)
    {
        while (true)
            {
                int optimizationCount = 0;
                optimizationCount += inlineSetRules(atn);
                optimizationCount += combineChainedEpsilons(atn);
                bool preserveOrder = atn.grammarType == ATNType.LEXER;
                optimizationCount += optimizeSets(atn, preserveOrder);
                if (optimizationCount == 0)
                    {
                        break;
                    }
            }
        if (deserializationOptions.verifyATN)
            {
                // reverify after modification
                verifyATN(atn);
            }
    }

    public bool hasSixOct(wstring data, size_t p)
    {
        for (auto i = p + 1; i < p + 7; i++) {
            auto c = to!int(data[i] - 0x30);
            if (c > 7 || c < 0)
                return false;
        }
        return true;
    }

    public int readSixOct(wstring data, size_t p)
    {
        int res = 0;
        for (auto i = p + 1; i < p + 7; i++) {
            res = res<<3 | to!int(data[i] - 0x30);
        }
        return res;
    }

    private UUID readUUID()
    {
        ubyte[16] data;
        long b = readLong;
        for(int i = 0; i < 8; i++) {
            data[i] = b & 0xff;
            b = b >>> 8;
        }
        long a = readLong;
        for (int i = 8; i < 16; i++) {
            data[i] = a & 0xff;
            a = a >>> 8;
        }
        data[].reverse();
        auto uuid = UUID(data);
        return uuid;
    }

    private long readLong()
    {
        long lowOrder = to!long(readInt32) & 0x00000000FFFFFFFFL;
        return lowOrder | (to!long(readInt32) << 32);
    }

    private int readInt32()
    {
        return data[p++] | data[p++] << 16;
    }

    private int readInt()
    {
        return data[p++];
    }

    private static void identifyTailCalls(ATN atn)
    {
        foreach (ATNState state; atn.states)
            {
                foreach (Transition transition; state.transitions)
                    {
                        if (!(cast(RuleTransition)transition))
                            {
                                continue;
                            }
                        RuleTransition ruleTransition = cast(RuleTransition)transition;
                        ruleTransition.tailCall = testTailCall(atn, ruleTransition, false);
                        ruleTransition.optimizedTailCall = testTailCall(atn, ruleTransition, true);
                    }
                if (!state.isOptimized)
                    {
                        continue;
                    }
                foreach (Transition transition; state.optimizedTransitions)
                    {
                        if (!(cast(RuleTransition)transition))
                            {
                                continue;
                            }
                        RuleTransition ruleTransition = cast(RuleTransition)transition;
                        ruleTransition.tailCall = testTailCall(atn, ruleTransition, false);
                        ruleTransition.optimizedTailCall = testTailCall(atn, ruleTransition, true);
                    }
            }
    }

    private static bool testTailCall(ATN atn, RuleTransition transition, bool optimizedPath)
    {
        if (optimizedPath && transition.optimizedTailCall)
            {
                return true;
            }
        BitSet *reachable = new BitSet(atn.states.length);
        ATNState[] worklist;
        worklist ~= transition.followState;
        while (worklist.length > 0)
            {
                ATNState state = worklist[$-1];
                worklist.length -= 1;
                if (reachable.get(state.stateNumber))
                    {
                        continue;
                    }
                if (cast(RuleStopState)state)
                    {
                        continue;
                    }
                if (!state.onlyHasEpsilonTransitions)
                    {
                        return false;
                    }
                Transition[] transitions = optimizedPath ? state.optimizedTransitions : state.transitions;
                foreach (Transition t; transitions)
                    {
                        if (t.getSerializationType != TransitionStates.EPSILON)
                            {
                                return false;
                            }
                        worklist ~= t.target;
                    }
            }
        return true;
    }

    public void generateRuleBypassTransitions(ATN atn)
    {
        atn.ruleToTokenType = new int[atn.ruleToStartState.length];
        for (int i = 0; i < atn.ruleToStartState.length; i++) {
            atn.ruleToTokenType[i] = atn.maxTokenType + i + 1;
        }

        for (int i = 0; i < atn.ruleToStartState.length; i++) {
            BasicBlockStartState bypassStart = new BasicBlockStartState();
            bypassStart.ruleIndex = i;
            atn.addState(bypassStart);

            BlockEndState bypassStop = new BlockEndState();
            bypassStop.ruleIndex = i;
            atn.addState(bypassStop);

            bypassStart.endState = bypassStop;
            atn.defineDecisionState(bypassStart);

            bypassStop.startState = bypassStart;

            ATNState endState;
            Transition excludeTransition = null;
            if (atn.ruleToStartState[i].isLeftRecursiveRule) {
                // wrap from the beginning of the rule to the StarLoopEntryState
                endState = null;
                foreach (ATNState state; atn.states) {
                    if (state.ruleIndex != i) {
                        continue;
                    }

                    if (state.classinfo != StarLoopEntryState.classinfo) {
                        continue;
                    }

                    ATNState maybeLoopEndState = state.transition(state.getNumberOfTransitions() - 1).target;
                    if (maybeLoopEndState.classinfo != LoopEndState.classinfo) {
                        continue;
                    }

                    if (maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target.classinfo == RuleStopState.classinfo) {
                        endState = state;
                        break;
                    }
                }

                if (endState is null) {
                    throw new UnsupportedOperationException("Couldn't identify final state of the precedence rule prefix section.");
                }

                excludeTransition = (cast(StarLoopEntryState)endState).loopBackState.transition(0);
            }
            else {
                endState = atn.ruleToStopState[i];
            }

            // all non-excluded transitions that currently target end state need to target blockEnd instead
            foreach (ATNState state; atn.states) {
                foreach (Transition transition; state.transitions) {
                    if (transition == excludeTransition) {
                        continue;
                    }

                    if (transition.target == endState) {
                        transition.target = bypassStop;
                    }
                }
            }

            // all transitions leaving the rule start state need to leave blockStart instead
            while (atn.ruleToStartState[i].getNumberOfTransitions() > 0) {
                Transition transition = atn.ruleToStartState[i].removeTransition(atn.ruleToStartState[i].getNumberOfTransitions() - 1);
                bypassStart.addTransition(transition);
            }

            // link the new states
            atn.ruleToStartState[i].addTransition(new EpsilonTransition(bypassStart));
            bypassStop.addTransition(new EpsilonTransition(endState));

            ATNState matchState = new BasicState();
            atn.addState(matchState);
            matchState.addTransition(new AtomTransition(bypassStop, atn.ruleToTokenType[i]));
            bypassStart.addTransition(new EpsilonTransition(matchState));
        }

        if (deserializationOptions.verifyATN()) {
            // reverify after modification
            verifyATN(atn);
        }
    }

    private static int inlineSetRules(ATN atn)
    {
        int inlinedCalls = 0;
        Transition[] ruleToInlineTransition;
        for (int i = 0; i < atn.ruleToStartState.length; i++)
            {
                RuleStartState startState = atn.ruleToStartState[i];
                ATNState middleState = startState;
                while (middleState.onlyHasEpsilonTransitions && middleState.numberOfOptimizedTransitions == 1 && middleState.getOptimizedTransition(0).getSerializationType == TransitionStates.EPSILON)
                    {
                        middleState = middleState.getOptimizedTransition(0).target;
                    }
                if (middleState.numberOfOptimizedTransitions != 1)
                    {
                        continue;
                    }
                Transition matchTransition = middleState.getOptimizedTransition(0);
                ATNState matchTarget = matchTransition.target;
                if (matchTransition.isEpsilon || !matchTarget.onlyHasEpsilonTransitions || matchTarget.numberOfOptimizedTransitions != 1 || !(cast(RuleStopState)matchTarget.getOptimizedTransition(0).target))
                    {
                        continue;
                    }
                with(TransitionStates) {
                    switch (matchTransition.getSerializationType)
                        {
                        case ATOM:
                        case RANGE:
                        case SET:
                            {
                                ruleToInlineTransition[i] = matchTransition;
                                break;
                            }

                        case NOT_SET:
                        case WILDCARD:
                            {
                                // not implemented yet
                                continue;
                            }

                        default:
                            {
                                continue;
                            }
                        }
                }
            }
        for (int stateNumber = 0; stateNumber < atn.states.length; stateNumber++)
            {
                ATNState state = atn.states[stateNumber];
                if (state.ruleIndex < 0)
                    {
                        continue;
                    }
                Transition[] optimizedTransitions = null;
                for (int i = 0; i < state.numberOfOptimizedTransitions; i++) {
                    Transition transition = state.getOptimizedTransition(i);
                    if (!(cast(RuleTransition)transition))
                        {
                            if (optimizedTransitions != null)
                                {
                                    optimizedTransitions ~= transition;
                                }
                            continue;
                        }
                    RuleTransition ruleTransition = cast(RuleTransition)transition;
                    Transition effective = ruleToInlineTransition[ruleTransition.target.ruleIndex];
                    if (effective is null)
                        {
                            if (optimizedTransitions != null)
                                {
                                    optimizedTransitions ~= transition;
                                }
                            continue;
                        }
                    if (optimizedTransitions == null)
                        {
                            optimizedTransitions = [];
                            for (int j = 0; j < i; j++)
                                {
                                    optimizedTransitions ~= state.getOptimizedTransition(i);
                                }
                        }
                    inlinedCalls++;
                    ATNState target = ruleTransition.followState;
                    ATNState intermediateState = new BasicState();
                    intermediateState.setRuleIndex(target.ruleIndex);
                    atn.addState(intermediateState);
                    optimizedTransitions ~= new EpsilonTransition(intermediateState);
                    with(TransitionStates) {
                        switch (effective.getSerializationType)
                            {
                            case ATOM:
                                {
                                    intermediateState.addTransition(new AtomTransition(target, (cast(AtomTransition)effective)._label));
                                    break;
                                }

                            case RANGE:
                                {
                                    intermediateState.addTransition(new RangeTransition(target, (cast(RangeTransition)effective).from, (cast(RangeTransition)effective).to));
                                    break;
                                }

                            case SET:
                                {
                                    intermediateState.addTransition(new SetTransition(target, effective.label));
                                    break;
                                }

                            default:
                                {
                                    assert(false, "NotSupportedException");
                                }
                            }
                    }
                }
                if (optimizedTransitions != null)
                    {
                        if (state.isOptimized)
                            {
                                while (state.numberOfOptimizedTransitions > 0)
                                    {
                                        state.removeOptimizedTransition(state.numberOfOptimizedTransitions - 1);
                                    }
                            }
                        foreach (Transition transition; optimizedTransitions)
                            {
                                state.addOptimizedTransition(transition);
                            }
                    }
            }
        return inlinedCalls;
    }

    private static int combineChainedEpsilons(ATN atn)
    {
        int removedEdges = 0;
        foreach (ATNState state; atn.states)
            {
                if (!state.onlyHasEpsilonTransitions || cast(RuleStopState)state)
                    {
                        continue;
                    }
                Transition[] optimizedTransitions = null;
                for (int i = 0; i < state.numberOfOptimizedTransitions; i++)
                    {
                        Transition transition = state.getOptimizedTransition(i);
                        ATNState intermediate = transition.target;
                        if (transition.getSerializationType != TransitionStates.EPSILON ||
                            (cast(EpsilonTransition)transition).outermostPrecedenceReturn != -1 ||
                            intermediate.getStateType != StateNames.BASIC ||
                            !intermediate.onlyHasEpsilonTransitions)
                            {
                                if (optimizedTransitions != null)
                                    {
                                        optimizedTransitions ~= transition;
                                    }
                                goto nextTransition_continue;
                            }
                        for (int j = 0; j < intermediate.numberOfOptimizedTransitions; j++)
                            {
                                if (intermediate.getOptimizedTransition(j).getSerializationType != TransitionStates.EPSILON ||

                                    (cast(EpsilonTransition)intermediate.getOptimizedTransition(j)).outermostPrecedenceReturn != -1)
                                    {
                                        if (optimizedTransitions != null)
                                            {
                                                optimizedTransitions ~= transition;
                                            }
                                        goto nextTransition_continue;
                                    }
                            }
                        removedEdges++;
                        if (optimizedTransitions == null)
                            {
                                optimizedTransitions = [];
                                for (int j = 0; j < i; j++)
                                    {
                                        optimizedTransitions ~= state.getOptimizedTransition(j);
                                    }
                            }
                        for (int j = 0; j < intermediate.numberOfOptimizedTransitions; j++)
                            {
                                ATNState target = intermediate.getOptimizedTransition(j).target;
                                optimizedTransitions ~= new EpsilonTransition(target);
                            }
                    nextTransition_continue: ;
                    }

                if (optimizedTransitions != null)
                    {
                        if (state.isOptimized)
                            {
                                while (state.numberOfOptimizedTransitions > 0)
                                    {
                                        state.removeOptimizedTransition(state.numberOfOptimizedTransitions - 1);
                                    }
                            }
                        foreach (Transition transition; optimizedTransitions)
                            {
                                state.addOptimizedTransition(transition);
                            }
                    }
            }

        return removedEdges;
    }

    private static int optimizeSets(ATN atn, bool preserveOrder)
    {
        if (preserveOrder)
            {
                // this optimization currently doesn't preserve edge order.
                return 0;
            }
        int removedPaths = 0;
        DecisionState[] decisions = atn.decisionToState;
        foreach (DecisionState decision; decisions)
            {
                IntervalSet setTransitions = new IntervalSet();
                for (int i = 0; i < decision.optimizedTransitions.length; i++)
                    {
                        Transition epsTransition = decision.optimizedTransitions[i];
                        if (!(cast(EpsilonTransition)epsTransition))
                            {
                                continue;
                            }
                        if (epsTransition.target.optimizedTransitions.length != 1)
                            {
                                continue;
                            }
                        Transition transition = epsTransition.target.optimizedTransitions[0];
                        if (!(cast(BlockEndState)transition.target))
                            {
                                continue;
                            }
                        if (cast(NotSetTransition)transition)
                            {
                                // TODO: not yet implemented
                                continue;
                            }
                        if (cast(AtomTransition)transition ||
                            cast(RangeTransition)transition ||
                            cast(SetTransition)transition)
                            {
                                setTransitions.add(i);
                            }
                    }
                if (setTransitions.size <= 1)
                    {
                        continue;
                    }
                Transition[] optimizedTransitions;
                for (int i = 0; i < decision.optimizedTransitions.length; i++)
                    {
                        if (!setTransitions.contains(i))
                            {
                                optimizedTransitions ~= decision.optimizedTransitions[i];
                            }
                    }
                ATNState blockEndState = decision.optimizedTransitions[setTransitions.getMinElement].target.optimizedTransitions[0].target;
                IntervalSet matchSet = new IntervalSet;
                for (int i = 0; i < setTransitions.intervals.length; i++)
                    {
                        Interval interval = setTransitions.intervals[i];
                        for (int j = interval.a; j <= interval.b; j++)
                            {
                                Transition matchTransition = decision.optimizedTransitions[j].target.optimizedTransitions[0];
                                if (cast(NotSetTransition)matchTransition)
                                    {
                                        assert(false, "Not yet implemented.");
                                    }
                                else
                                    {
                                        matchSet.addAll(matchTransition.label);
                                    }
                            }
                    }
                Transition newTransition;
                if (matchSet.intervals.length == 1)
                    {
                        if (matchSet.size == 1)
                            {
                                newTransition = new AtomTransition(blockEndState, matchSet.getMinElement);
                            }
                        else
                            {
                                Interval matchInterval = matchSet.intervals[0];
                                newTransition = new RangeTransition(blockEndState, matchInterval.a, matchInterval.b);
                            }
                    }
                else
                    {
                        newTransition = new SetTransition(blockEndState, matchSet);
                    }
                ATNState optimizedState = new BasicState();
                optimizedState.setRuleIndex(decision.ruleIndex);
                atn.addState(optimizedState);
                optimizedState.addTransition(newTransition);
                optimizedTransitions ~= new EpsilonTransition(optimizedState);
                removedPaths += decision.numberOfOptimizedTransitions - optimizedTransitions.length;
                if (decision.isOptimized)
                    {
                        while (decision.numberOfOptimizedTransitions > 0)
                            {
                                decision.removeOptimizedTransition(decision.numberOfOptimizedTransitions - 1);
                            }
                    }
                foreach (Transition transition; optimizedTransitions)
                    {
                        decision.addOptimizedTransition(transition);
                    }
            }
        return removedPaths;
    }

}

version(unittest) {
    import dshould : be, equal, not, should;
    import std.typecons : tuple;
    import unit_threaded;

    @Tags("des11")
    @("testEncodingATNDeserialize")
    unittest {
        auto des = new ATNDeserializer;
        des.should.not.be(null);
    }
}
