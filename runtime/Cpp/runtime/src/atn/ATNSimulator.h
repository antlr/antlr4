﻿/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "atn/ATN.h"
#include "atn/PredictionContext.h"
#include "atn/PredictionContextCache.h"
#include "misc/IntervalSet.h"
#include "support/CPPUtils.h"

namespace antlr4 {
namespace atn {

  class ANTLR4CPP_PUBLIC ATNSimulator {
  public:
    /// Must distinguish between missing edge and edge we know leads nowhere.
    static const Ref<dfa::DFAState> ERROR;
    const ATN &atn;

    ATNSimulator(const ATN &atn, PredictionContextCache &sharedContextCache);

    virtual ~ATNSimulator() = default;

    virtual void reset() = 0;

    /**
     * Clear the DFA cache used by the current instance. Since the DFA cache may
     * be shared by multiple ATN simulators, this method may affect the
     * performance (but not accuracy) of other parsers which are being used
     * concurrently.
     *
     * @throws UnsupportedOperationException if the current instance does not
     * support clearing the DFA.
     *
     * @since 4.3
     */
    virtual void clearDFA();

    PredictionContextCache& getSharedContextCache() const;
    Ref<const PredictionContext> getCachedContext(const Ref<const PredictionContext> &context);

  protected:
    /// <summary>
    /// The context cache maps all PredictionContext objects that are equals()
    ///  to a single cached copy. This cache is shared across all contexts
    ///  in all ATNConfigs in all DFA states.  We rebuild each ATNConfigSet
    ///  to use only cached nodes/graphs in addDFAState(). We don't want to
    ///  fill this during closure() since there are lots of contexts that
    ///  pop up but are not used ever again. It also greatly slows down closure().
    ///  <p/>
    ///  This cache makes a huge difference in memory and a little bit in speed.
    ///  For the Java grammar on java.*, it dropped the memory requirements
    ///  at the end from 25M to 16M. We don't store any of the full context
    ///  graphs in the DFA because they are limited to local context only,
    ///  but apparently there's a lot of repetition there as well. We optimize
    ///  the config contexts before storing the config set in the DFA states
    ///  by literally rebuilding them with cached subgraphs only.
    ///  <p/>
    ///  I tried a cache for use during closure operations, that was
    ///  whacked after each adaptivePredict(). It cost a little bit
    ///  more time I think and doesn't save on the overall footprint
    ///  so it's not worth the complexity.
    /// </summary>
    PredictionContextCache &_sharedContextCache;
  };

} // namespace atn
} // namespace antlr4
