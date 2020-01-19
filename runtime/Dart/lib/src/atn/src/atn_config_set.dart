/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'dart:collection';
import 'dart:math';

import 'package:collection/collection.dart';

import '../../misc/pair.dart';
import '../../prediction_context.dart';
import '../../util/bit_set.dart';
import '../../util/utils.dart';
import 'atn.dart';
import 'atn_config.dart';
import 'atn_state.dart';
import 'semantic_context.dart';

class ATNConfigSet extends Iterable<ATNConfig> {
  /**
   * Indicates that the set of configurations is read-only. Do not
   *  allow any code to manipulate the set; DFA states will point at
   *  the sets and they must not change. This does not protect the other
   *  fields; in particular, conflictingAlts is set after
   *  we've made this readonly.
   */
  bool _readOnly = false;

  bool get readOnly => _readOnly;

  set readOnly(bool readOnly) {
    this._readOnly = readOnly;
    if (readOnly) {
      this.configLookup = null; // can't mod, no need for lookup cache
    }
  }

  /// The reason that we need this is because we don't want the hash map to use
  /// the standard hash code and equals. We need all configurations with the same
  /// {@code (s,i,_,semctx)} to be equal. Unfortunately, this key effectively doubles
  /// the number of objects associated with ATNConfigs. The other solution is to
  /// use a hash table that lets us specify the equals/hashcode operation.
  ///
  /// All configs but hashed by (s, i, _, pi) not including context. Wiped out
  /// when we go readonly as this set becomes a DFA state.
  Set<ATNConfig> configLookup = new HashSet<ATNConfig>(equals: (a, b) {
    if (a == null || b == null) return false;
    return a.state.stateNumber == b.state.stateNumber &&
        a.alt == b.alt &&
        a.semanticContext == b.semanticContext;
  }, hashCode: (ATNConfig o) {
    int hashCode = 7;
    hashCode = 31 * hashCode + o.state.stateNumber;
    hashCode = 31 * hashCode + o.alt;
    hashCode = 31 * hashCode + o.semanticContext.hashCode;
    return hashCode;
  });

  /** Track the elements as they are added to the set; supports get(i) */
  final List<ATNConfig> configs = [];

  // TODO: these fields make me pretty uncomfortable but nice to pack up info together, saves recomputation
  // TODO: can we track conflicts as they are added to save scanning configs later?
  int uniqueAlt = 0;

  /**
   * Currently this is only used when we detect SLL conflict; this does
   *  not necessarily represent the ambiguous alternatives. In fact,
   *  I should also point out that this seems to include predicated alternatives
   *  that have predicates that evaluate to false. Computed in computeTargetState().
   */
  BitSet conflictingAlts;

  // Used in parser and lexer. In lexer, it indicates we hit a pred
  // while computing a closure operation.  Don't make a DFA state from this.
  bool hasSemanticContext = false;
  bool dipsIntoOuterContext = false;

  /** Indicates that this configuration set is part of a full context
   *  LL prediction. It will be used to determine how to merge $. With SLL
   *  it's a wildcard whereas it is not for LL context merge.
   */
  bool fullCtx;

  int cachedHashCode = -1;

  ATNConfigSet([this.fullCtx = true]);

  ATNConfigSet.dup(ATNConfigSet old) {
    this.fullCtx = old.fullCtx;
    addAll(old);
    this.uniqueAlt = old.uniqueAlt;
    this.conflictingAlts = old.conflictingAlts;
    this.hasSemanticContext = old.hasSemanticContext;
    this.dipsIntoOuterContext = old.dipsIntoOuterContext;
  }

  /**
   * Adding a new config means merging contexts with existing configs for
   * {@code (s, i, pi, _)}, where [s] is the
   * {@link ATNConfig#state}, [i] is the {@link ATNConfig#alt}, and
   * [pi] is the {@link ATNConfig#semanticContext}. We use
   * {@code (s,i,pi)} as key.
   *
   * <p>This method updates {@link #dipsIntoOuterContext} and
   * {@link #hasSemanticContext} when necessary.</p>
   */
  bool add(ATNConfig config,
      [Map<Pair<PredictionContext, PredictionContext>, PredictionContext>
          mergeCache = null]) {
    if (readOnly) throw new StateError("This set is readonly");
    if (config.semanticContext != SemanticContext.NONE) {
      hasSemanticContext = true;
    }
    if (config.outerContextDepth > 0) {
      dipsIntoOuterContext = true;
    }
    final existing = configLookup.lookup(config) ?? config;
    if (identical(existing, config)) {
      // we added this new one
      cachedHashCode = -1;
      configLookup.add(config);
      configs.add(config); // track order here
      return true;
    }
    // a previous (s,i,pi,_), merge with it and save result
    bool rootIsWildcard = !fullCtx;
    PredictionContext merged = PredictionContext.merge(
        existing.context, config.context, rootIsWildcard, mergeCache);
    // no need to check for existing.context, config.context in cache
    // since only way to create new graphs is "call rule" and here. We
    // cache at both places.
    existing.reachesIntoOuterContext =
        max(existing.reachesIntoOuterContext, config.reachesIntoOuterContext);

    // make sure to preserve the precedence filter suppression during the merge
    if (config.isPrecedenceFilterSuppressed()) {
      existing.setPrecedenceFilterSuppressed(true);
    }

    existing.context = merged; // replace context; no need to alt mapping
    return true;
  }

  /** Return a List holding list of configs */
  List<ATNConfig> get elements {
    return configs;
  }

  Set<ATNState> get states {
    var states = new Set<ATNState>();
    for (var i = 0; i < this.configs.length; i++) {
      states.add(this.configs[i].state);
    }
    return states;
  }

  /**
   * Gets the complete set of represented alternatives for the configuration
   * set.
   *
   * @return the set of represented alternatives in this configuration set
   *
   * @since 4.3
   */
  BitSet get alts {
    BitSet alts = new BitSet();
    for (ATNConfig config in configs) {
      alts.set(config.alt);
    }
    return alts;
  }

  List<SemanticContext> get predicates {
    List<SemanticContext> preds = [];
    for (ATNConfig c in configs) {
      if (c.semanticContext != SemanticContext.NONE) {
        preds.add(c.semanticContext);
      }
    }
    return preds;
  }

  ATNConfig get(int i) {
    return configs[i];
  }

  optimizeConfigs(interpreter) {
    if (this.readOnly) throw StateError("This set is readonly");

    if (this.configLookup.isEmpty) return;

    for (ATNConfig config in configs) {
//			int before = PredictionContext.getAllContextNodes(config.context).length;
      config.context = interpreter.getCachedContext(config.context);
//			int after = PredictionContext.getAllContextNodes(config.context).length;
//			System.out.println("configs "+before+"->"+after);
    }
  }

  addAll(coll) {
    for (ATNConfig c in coll) add(c);
    return false;
  }

  bool operator ==(other) {
    return identical(this, other) ||
        (other is ATNConfigSet &&
            other != null &&
            ListEquality().equals(this.configs, other.configs) &&
            this.fullCtx == other.fullCtx &&
            this.uniqueAlt == other.uniqueAlt &&
            this.conflictingAlts == other.conflictingAlts &&
            this.hasSemanticContext == other.hasSemanticContext &&
            this.dipsIntoOuterContext == other.dipsIntoOuterContext);
  }

  int get hashCode {
    if (readOnly) {
      if (cachedHashCode == -1) {
        cachedHashCode = ListEquality().hash(configs);
      }

      return cachedHashCode;
    }

    return ListEquality().hash(configs);
  }

  int get length {
    return configs.length;
  }

  bool get isEmpty => configs.isEmpty;

  updateHashCode(hash) {
    if (this.readOnly) {
      if (this.cachedHashCode == -1) {
        this.cachedHashCode = this.hashCode;
      }
      hash.update(this.cachedHashCode);
    } else {
      hash.update(this.hashCode);
    }
  }

  bool contains(Object o) {
    if (configLookup == null) {
      throw new UnsupportedError(
          "This method is not implemented for readonly sets.");
    }

    return configLookup.contains(o);
  }

  Iterator<ATNConfig> get iterator => configs.iterator;

  clear() {
    if (readOnly) throw StateError("This set is readonly");
    configs.clear();
    cachedHashCode = -1;
    configLookup.clear();
  }

  String toString() {
    final buf = new StringBuffer();
    buf.write(arrayToString(elements));
    if (hasSemanticContext)
      buf.write(",hasSemanticContext=$hasSemanticContext");
    if (uniqueAlt != ATN.INVALID_ALT_NUMBER) buf.write(",uniqueAlt=$uniqueAlt");
    if (conflictingAlts != null) buf.write(",conflictingAlts=$conflictingAlts");
    if (dipsIntoOuterContext) buf.write(",dipsIntoOuterContext");
    return buf.toString();
  }
}

class OrderedATNConfigSet extends ATNConfigSet {
  final configLookup = Set();
}
