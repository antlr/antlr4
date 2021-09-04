/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import '../../dfa/dfa.dart';
import '../../prediction_context.dart';
import 'atn.dart';
import 'atn_config_set.dart';

abstract class ATNSimulator {
  /// Must distinguish between missing edge and edge we know leads nowhere */

  static final DFAState ERROR = DFAState(
    stateNumber: 0x7FFFFFFF,
    configs: ATNConfigSet(),
  );

  final ATN atn;

  /// The context cache maps all PredictionContext objects that are equals()
  ///  to a single cached copy. This cache is shared across all contexts
  ///  in all ATNConfigs in all DFA states.  We rebuild each ATNConfigSet
  ///  to use only cached nodes/graphs in addDFAState(). We don't want to
  ///  fill this during closure() since there are lots of contexts that
  ///  pop up but are not used ever again. It also greatly slows down closure().
  ///
  ///  <p>This cache makes a huge difference in memory and a little bit in speed.
  ///  For the Java grammar on java.*, it dropped the memory requirements
  ///  at the end from 25M to 16M. We don't store any of the full context
  ///  graphs in the DFA because they are limited to local context only,
  ///  but apparently there's a lot of repetition there as well. We optimize
  ///  the config contexts before storing the config set in the DFA states
  ///  by literally rebuilding them with cached subgraphs only.</p>
  ///
  ///  <p>I tried a cache for use during closure operations, that was
  ///  whacked after each adaptivePredict(). It cost a little bit
  ///  more time I think and doesn't save on the overall footprint
  ///  so it's not worth the complexity.</p>
  final PredictionContextCache? sharedContextCache;

  ATNSimulator(this.atn, this.sharedContextCache);

  void reset();

  /// Clear the DFA cache used by the current instance. Since the DFA cache may
  /// be shared by multiple ATN simulators, this method may affect the
  /// performance (but not accuracy) of other parsers which are being used
  /// concurrently.
  ///
  /// @throws UnsupportedOperationException if the current instance does not
  /// support clearing the DFA.
  ///
  /// @since 4.3
  void clearDFA() {
    throw UnsupportedError(
        'This ATN simulator does not support clearing the DFA.');
  }

  PredictionContext getCachedContext(PredictionContext context) {
    if (sharedContextCache == null) return context;

    final visited = <PredictionContext, PredictionContext>{};
    return PredictionContext.getCachedContext(
        context, sharedContextCache!, visited);
  }
}

/// Used to cache [PredictionContext] objects. Its used for the shared
///  context cash associated with contexts in DFA states. This cache
///  can be used for both lexers and parsers.
class PredictionContextCache {
  final cache = <PredictionContext, PredictionContext>{};

  /// Add a context to the cache and return it. If the context already exists,
  ///  return that one instead and do not add a new context to the cache.
  ///  Protect shared cache from unsafe thread access.
  PredictionContext add(PredictionContext ctx) {
    if (ctx == PredictionContext.EMPTY) return PredictionContext.EMPTY;
    final existing = cache[ctx];
    if (existing != null) {
//			System.out.println(name+" reuses "+existing);
      return existing;
    }
    cache[ctx] = ctx;
    return ctx;
  }

  PredictionContext? operator [](PredictionContext ctx) {
    return cache[ctx];
  }

  int get length {
    return cache.length;
  }
}
