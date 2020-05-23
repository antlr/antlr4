/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.ATNSimulator;

import antlr.v4.runtime.UnsupportedOperationException;
import antlr.v4.runtime.atn.ATN;
import antlr.v4.runtime.atn.ATNDeserializer;
import antlr.v4.runtime.atn.ATNConfigSet;
import antlr.v4.runtime.atn.InterfaceATNSimulator;
import antlr.v4.runtime.atn.PredictionContext;
import antlr.v4.runtime.atn.PredictionContextCache;
import antlr.v4.runtime.dfa.DFAState;
import std.uuid;

/**
 * ATN simulator base class
 */
abstract class ATNSimulator : InterfaceATNSimulator
{

    public static int SERIALIZED_VERSION;

    /**
     * This is the current serialized UUID.
     * deprecated Use {@link ATNDeserializer#checkCondition(boolean)} instead.
     */
    public static UUID SERIALIZED_UUID;

    /**
     * Must distinguish between missing edge and edge we know leads nowhere
     */
    public static DFAState ERROR;

    public ATN atn;

    /**
     * The context cache maps all PredictionContext objects that are equals()
     * to a single cached copy. This cache is shared across all contexts
     * in all ATNConfigs in all DFA states.  We rebuild each ATNConfigSet
     * to use only cached nodes/graphs in addDFAState(). We don't want to
     * fill this during closure() since there are lots of contexts that
     * pop up but are not used ever again. It also greatly slows down closure().
     *
     * <p>This cache makes a huge difference in memory and a little bit in speed.
     * For the Java grammar on java.*, it dropped the memory requirements
     * at the end from 25M to 16M. We don't store any of the full context
     * graphs in the DFA because they are limited to local context only,
     * but apparently there's a lot of repetition there as well. We optimize
     * the config contexts before storing the config set in the DFA states
     * by literally rebuilding them with cached subgraphs only.</p>
     *
     * <p>I tried a cache for use during closure operations, that was
     * whacked after each adaptivePredict(). It cost a little bit
     * more time I think and doesn't save on the overall footprint
     * so it's not worth the complexity.</p>
     */
    public PredictionContextCache sharedContextCache;

    public static this()
    {
        SERIALIZED_VERSION = ATNDeserializer.SERIALIZED_VERSION;
        SERIALIZED_UUID = ATNDeserializer.SERIALIZED_UUID;
        ERROR = new DFAState(new ATNConfigSet());
        ERROR.stateNumber = int.max;
    }

    public this(ATN atn, PredictionContextCache sharedContextCache)
    {
        this.atn = atn;
        this.sharedContextCache = sharedContextCache;
    }

    abstract public void reset();

    /**
     * Clear the DFA cache used by the current instance. Since the DFA cache may
     * be shared by multiple ATN simulators, this method may affect the
     * performance (but not accuracy) of other parsers which are being used
     * concurrently.
     *
     *  @throws UnsupportedOperationException if the current instance does not
     * support clearing the DFA.
     */
    public void clearDFA()
    {
        throw new UnsupportedOperationException("This ATN simulator does not support clearing the DFA.");
    }

    public PredictionContextCache getSharedContextCache()
    {
        return sharedContextCache;
    }

    public PredictionContext getCachedContext(PredictionContext context)
    {
        if (sharedContextCache is null)
            return context;
        PredictionContext[PredictionContext] visited;
        return PredictionContext.getCachedContext(context,
                                                  sharedContextCache,
                                                  visited);
    }

}
