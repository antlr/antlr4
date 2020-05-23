/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.LexerATNConfig;

import antlr.v4.runtime.atn.ATNConfig;
import antlr.v4.runtime.atn.LexerActionExecutor;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.DecisionState;
import antlr.v4.runtime.atn.PredictionContext;
import antlr.v4.runtime.atn.SemanticContext;
import antlr.v4.runtime.misc.MurmurHash;
import antlr.v4.runtime.misc.ObjectEqualityComparator;

/**
 * TODO add class description
 */
class LexerATNConfig : ATNConfig
{

    /**
     * This is the backing field for {@link #getLexerActionExecutor}.
     */
    public LexerActionExecutor lexerActionExecutor;

    public bool passedThroughNonGreedyDecision;

    public this(ATNState state, int alt, PredictionContext context)
    {
        if (!SemanticContext.NONE) {
            auto sp = new SemanticContext;
            SemanticContext.NONE = sp.new SemanticContext.Predicate;
        }
        super(state, alt, context, SemanticContext.NONE);
        this.passedThroughNonGreedyDecision = false;
        this.lexerActionExecutor = null;
    }

    public this(ATNState state, int alt, PredictionContext context, LexerActionExecutor lexerActionExecutor)
    {
        if (!SemanticContext.NONE) {
            auto sp = new SemanticContext;
            SemanticContext.NONE = sp.new SemanticContext.Predicate;
        }
        super(state, alt, context, SemanticContext.NONE);
        this.lexerActionExecutor = lexerActionExecutor;
        this.passedThroughNonGreedyDecision = false;
    }

    public this(LexerATNConfig c, ATNState state)
    {
        super(c, state, c.context, c.semanticContext);
        this.lexerActionExecutor = c.lexerActionExecutor;
        this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
    }

    public this(LexerATNConfig c, ATNState state, LexerActionExecutor lexerActionExecutor)
    {
        super(c, state, c.context, c.semanticContext);
        this.lexerActionExecutor = lexerActionExecutor;
        this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
    }

    public this(LexerATNConfig c, ATNState state, PredictionContext context)
    {
        super(c, state, context, c.semanticContext);
        this.lexerActionExecutor = c.lexerActionExecutor;
        this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
    }

    /**
     * Gets the {@link LexerActionExecutor} capable of executing the embedded
     *  action(s) for the current configuration.
     */
    public LexerActionExecutor getLexerActionExecutor()
    {
        return lexerActionExecutor;
    }

    public bool hasPassedThroughNonGreedyDecision()
    {
        return passedThroughNonGreedyDecision;
    }

    /**
     * @uml
     * @override
     * @safe
     * @nothrow
     */
    public override size_t toHash() @safe nothrow
    {
        size_t hashCode = MurmurHash.initialize(7);
        hashCode = MurmurHash.update(hashCode, state.stateNumber);
        hashCode = MurmurHash.update(hashCode, alt);
        hashCode = MurmurHash.update(hashCode, context);
        hashCode = MurmurHash.update(hashCode, semanticContext);
        hashCode = MurmurHash.update(hashCode, passedThroughNonGreedyDecision ? 1 : 0);
        hashCode = MurmurHash.update(hashCode, lexerActionExecutor);
        hashCode = MurmurHash.finish(hashCode, 6);
        return hashCode;
    }

    public bool equals(ATNConfig other)
    {
        if (this is other) {
            return true;
        }
        else if (other.classinfo != LexerATNConfig.classinfo) {
            return false;
        }

        LexerATNConfig lexerOther = cast(LexerATNConfig)other;
        if (passedThroughNonGreedyDecision != lexerOther.passedThroughNonGreedyDecision) {
            return false;
        }
        if (!ObjectEqualityComparator.opEquals(lexerActionExecutor, lexerOther.lexerActionExecutor)) {
            return false;
        }

        return super.opEquals(other);
    }

    public static bool checkNonGreedyDecision(LexerATNConfig source, ATNState target)
    {
        return source.passedThroughNonGreedyDecision
            || cast(DecisionState)target && (cast(DecisionState)target).nonGreedy;
    }

}
