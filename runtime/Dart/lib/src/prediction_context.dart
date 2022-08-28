/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'package:collection/collection.dart';

import 'atn/atn.dart';
import 'misc/pair.dart';
import 'recognizer.dart';
import 'rule_context.dart';
import 'util/murmur_hash.dart';

abstract class PredictionContext {
  /// Represents {@code $} in an array in full context mode, when {@code $}
  /// doesn't mean wildcard: {@code $ + x = [$,x]}. Here,
  /// {@code $} = {@link #EMPTY_RETURN_STATE}.
  static final int EMPTY_RETURN_STATE = 0x7FFFFFFF;

  static final int INITIAL_HASH = 1;

  static int globalNodeCount = 0;
  int id = globalNodeCount++;

  /// Stores the computed hash code of this [PredictionContext]. The hash
  /// code is computed in parts to match the following reference algorithm.
  ///
  /// <pre>
  ///   int referenceHashCode() {
  ///      int hash = {@link MurmurHash#initialize MurmurHash.initialize}({@link #INITIAL_HASH});
  ///
  ///      for (int i = 0; i &lt; {@link #size()}; i++) {
  ///          hash = {@link MurmurHash#update MurmurHash.update}(hash, {@link #getParent getParent}(i));
  ///      }
  ///
  ///      for (int i = 0; i &lt; {@link #size()}; i++) {
  ///          hash = {@link MurmurHash#update MurmurHash.update}(hash, {@link #getReturnState getReturnState}(i));
  ///      }
  ///
  ///      hash = {@link MurmurHash#finish MurmurHash.finish}(hash, 2 * {@link #size()});
  ///      return hash;
  ///  }
  /// </pre>
  final int cachedHashCode;

  PredictionContext(this.cachedHashCode);

  /// Convert a [RuleContext] tree to a [PredictionContext] graph.
  ///  Return {@link #EMPTY} if [outerContext] is empty or null.
  static PredictionContext fromRuleContext(ATN atn, RuleContext? outerContext) {
    outerContext ??= RuleContext.EMPTY;

    // if we are in RuleContext of start rule, s, then PredictionContext
    // is EMPTY. Nobody called us. (if we are empty, return empty)
    if (outerContext.parent == null || outerContext == RuleContext.EMPTY) {
      return EmptyPredictionContext.Instance;
    }

    // If we have a parent, convert it to a PredictionContext graph
    PredictionContext parent = EmptyPredictionContext.Instance;
    parent = PredictionContext.fromRuleContext(atn, outerContext.parent);

    final state = atn.states[outerContext.invokingState]!;
    final transition = state.transition(0) as RuleTransition;
    return SingletonPredictionContext.create(
      parent,
      transition.followState.stateNumber,
    );
  }

  int get length;

  PredictionContext? getParent(int index);

  int getReturnState(int index);

  /// This means only the {@link #EMPTY} (wildcard? not sure) context is in set. */
  bool get isEmpty {
    return this == EmptyPredictionContext.Instance;
  }

  bool hasEmptyPath() {
    // since EMPTY_RETURN_STATE can only appear in the last position, we check last one
    return getReturnState(length - 1) == EMPTY_RETURN_STATE;
  }

  @override
  int get hashCode {
    return cachedHashCode;
  }

  @override
  bool operator ==(Object obj);

  static int calculateEmptyHashCode() {
    var hash = MurmurHash.initialize(INITIAL_HASH);
    hash = MurmurHash.finish(hash, 0);
    return hash;
  }

  static int calculateHashCode(
      List<PredictionContext?> parents, List<int> returnStates) {
    var hash = MurmurHash.initialize(INITIAL_HASH);

    for (var parent in parents) {
      hash = MurmurHash.update(hash, parent);
    }

    for (var returnState in returnStates) {
      hash = MurmurHash.update(hash, returnState);
    }

    hash = MurmurHash.finish(hash, 2 * parents.length);
    return hash;
  }

  // dispatch
  static PredictionContext merge(
    PredictionContext a,
    PredictionContext b,
    bool rootIsWildcard,
    Map<Pair<PredictionContext, PredictionContext>, PredictionContext>?
        mergeCache,
  ) {
    // share same graph if both same
    if (a == b || a == b) return a;

    if (a is SingletonPredictionContext && b is SingletonPredictionContext) {
      return mergeSingletons(a, b, rootIsWildcard, mergeCache);
    }

    // At least one of a or b is array
    // If one is $ and rootIsWildcard, return $ as * wildcard
    if (rootIsWildcard) {
      if (a is EmptyPredictionContext) return a;
      if (b is EmptyPredictionContext) return b;
    }

    // convert singleton so both are arrays to normalize
    if (a is SingletonPredictionContext) {
      a = ArrayPredictionContext.of(a);
    }
    if (b is SingletonPredictionContext) {
      b = ArrayPredictionContext.of(b);
    }
    return mergeArrays(
      a as ArrayPredictionContext,
      b as ArrayPredictionContext,
      rootIsWildcard,
      mergeCache,
    );
  }

  /// Merge two [SingletonPredictionContext] instances.
  ///
  /// <p>Stack tops equal, parents merge is same; return left graph.<br>
  /// <embed src="images/SingletonMerge_SameRootSamePar.svg" type="image/svg+xml"/></p>
  ///
  /// <p>Same stack top, parents differ; merge parents giving array node, then
  /// remainders of those graphs. A new root node is created to point to the
  /// merged parents.<br>
  /// <embed src="images/SingletonMerge_SameRootDiffPar.svg" type="image/svg+xml"/></p>
  ///
  /// <p>Different stack tops pointing to same parent. Make array node for the
  /// root where both element in the root point to the same (original)
  /// parent.<br>
  /// <embed src="images/SingletonMerge_DiffRootSamePar.svg" type="image/svg+xml"/></p>
  ///
  /// <p>Different stack tops pointing to different parents. Make array node for
  /// the root where each element points to the corresponding original
  /// parent.<br>
  /// <embed src="images/SingletonMerge_DiffRootDiffPar.svg" type="image/svg+xml"/></p>
  ///
  /// @param a the first [SingletonPredictionContext]
  /// @param b the second [SingletonPredictionContext]
  /// @param rootIsWildcard [true] if this is a local-context merge,
  /// otherwise false to indicate a full-context merge
  /// @param mergeCache
  static PredictionContext mergeSingletons(
    SingletonPredictionContext a,
    SingletonPredictionContext b,
    bool rootIsWildcard,
    Map<Pair<PredictionContext, PredictionContext>, PredictionContext>?
        mergeCache,
  ) {
    if (mergeCache != null) {
      var previous = mergeCache[Pair(a, b)];
      if (previous != null) return previous;
      previous = mergeCache[Pair(b, a)];
      if (previous != null) return previous;
    }

    final rootMerge = mergeRoot(a, b, rootIsWildcard);
    if (rootMerge != null) {
      if (mergeCache != null) mergeCache[Pair(a, b)] = rootMerge;
      return rootMerge;
    }

    if (a.returnState == b.returnState) {
      assert(a.parent != null &&
          b.parent != null); // must be empty context, never null

      // a == b
      final parent = merge(a.parent!, b.parent!, rootIsWildcard, mergeCache);
      // if parent is same as existing a or b parent or reduced to a parent, return it
      if (parent == a.parent) return a; // ax + bx = ax, if a=b
      if (parent == b.parent) return b; // ax + bx = bx, if a=b
      // else: ax + ay = a'[x,y]
      // merge parents x and y, giving array node with x,y then remainders
      // of those graphs.  dup a, a' points at merged array
      // new joined parent so create new singleton pointing to it, a'
      PredictionContext a_ =
          SingletonPredictionContext.create(parent, a.returnState);
      if (mergeCache != null) mergeCache[Pair(a, b)] = a_;
      return a_;
    } else {
      // a != b payloads differ
      // see if we can collapse parents due to $+x parents if local ctx
      PredictionContext? singleParent;
      if (a == b || (a.parent != null && a.parent == b.parent)) {
        // ax + bx = [a,b]x
        singleParent = a.parent;
      }
      if (singleParent != null) {
        // parents are same
        // sort payloads and use same parent
        final payloads = <int>[a.returnState, b.returnState];
        if (a.returnState > b.returnState) {
          payloads[0] = b.returnState;
          payloads[1] = a.returnState;
        }
        final parents = <PredictionContext>[singleParent, singleParent];
        PredictionContext a_ = ArrayPredictionContext(parents, payloads);
        if (mergeCache != null) mergeCache[Pair(a, b)] = a_;
        return a_;
      }
      // parents differ and can't merge them. Just pack together
      // into array; can't merge.
      // ax + by = [ax,by]
      final payloads = <int>[a.returnState, b.returnState];
      var parents = <PredictionContext?>[a.parent, b.parent];
      if (a.returnState > b.returnState) {
        // sort by payload
        payloads[0] = b.returnState;
        payloads[1] = a.returnState;
        parents = [b.parent, a.parent];
      }
      PredictionContext a_ = ArrayPredictionContext(parents, payloads);
      if (mergeCache != null) mergeCache[Pair(a, b)] = a_;
      return a_;
    }
  }

  /// Handle case where at least one of [a] or [b] is
  /// {@link #EMPTY}. In the following diagrams, the symbol {@code $} is used
  /// to represent {@link #EMPTY}.
  ///
  /// <h2>Local-Context Merges</h2>
  ///
  /// <p>These local-context merge operations are used when [rootIsWildcard]
  /// is true.</p>
  ///
  /// <p>{@link #EMPTY} is superset of any graph; return {@link #EMPTY}.<br>
  /// <embed src="images/LocalMerge_EmptyRoot.svg" type="image/svg+xml"/></p>
  ///
  /// <p>{@link #EMPTY} and anything is {@code #EMPTY}, so merged parent is
  /// {@code #EMPTY}; return left graph.<br>
  /// <embed src="images/LocalMerge_EmptyParent.svg" type="image/svg+xml"/></p>
  ///
  /// <p>Special case of last merge if local context.<br>
  /// <embed src="images/LocalMerge_DiffRoots.svg" type="image/svg+xml"/></p>
  ///
  /// <h2>Full-Context Merges</h2>
  ///
  /// <p>These full-context merge operations are used when [rootIsWildcard]
  /// is false.</p>
  ///
  /// <p><embed src="images/FullMerge_EmptyRoots.svg" type="image/svg+xml"/></p>
  ///
  /// <p>Must keep all contexts; {@link #EMPTY} in array is a special value (and
  /// null parent).<br>
  /// <embed src="images/FullMerge_EmptyRoot.svg" type="image/svg+xml"/></p>
  ///
  /// <p><embed src="images/FullMerge_SameRoot.svg" type="image/svg+xml"/></p>
  ///
  /// @param a the first [SingletonPredictionContext]
  /// @param b the second [SingletonPredictionContext]
  /// @param rootIsWildcard [true] if this is a local-context merge,
  /// otherwise false to indicate a full-context merge
  static PredictionContext? mergeRoot(
    SingletonPredictionContext a,
    SingletonPredictionContext b,
    bool rootIsWildcard,
  ) {
    if (rootIsWildcard) {
      if (a == EmptyPredictionContext.Instance) return EmptyPredictionContext.Instance; // * + b = *
      if (b == EmptyPredictionContext.Instance) return EmptyPredictionContext.Instance; // a + * = *
    } else {
      if (a == EmptyPredictionContext.Instance && b == EmptyPredictionContext.Instance) return EmptyPredictionContext.Instance; // $ + $ = $
      if (a == EmptyPredictionContext.Instance) {
        // $ + x = [x,$]
        final payloads = <int>[b.returnState, EMPTY_RETURN_STATE];
        final parents = <PredictionContext?>[b.parent, null];
        PredictionContext joined = ArrayPredictionContext(parents, payloads);
        return joined;
      }
      if (b == EmptyPredictionContext.Instance) {
        // x + $ = [x,$] ($ is always last if present)
        final payloads = <int>[a.returnState, EMPTY_RETURN_STATE];
        final parents = [a.parent, null];
        PredictionContext joined = ArrayPredictionContext(parents, payloads);
        return joined;
      }
    }
    return null;
  }

  /// Merge two [ArrayPredictionContext] instances.
  ///
  /// <p>Different tops, different parents.<br>
  /// <embed src="images/ArrayMerge_DiffTopDiffPar.svg" type="image/svg+xml"/></p>
  ///
  /// <p>Shared top, same parents.<br>
  /// <embed src="images/ArrayMerge_ShareTopSamePar.svg" type="image/svg+xml"/></p>
  ///
  /// <p>Shared top, different parents.<br>
  /// <embed src="images/ArrayMerge_ShareTopDiffPar.svg" type="image/svg+xml"/></p>
  ///
  /// <p>Shared top, all shared parents.<br>
  /// <embed src="images/ArrayMerge_ShareTopSharePar.svg" type="image/svg+xml"/></p>
  ///
  /// <p>Equal tops, merge parents and reduce top to
  /// [SingletonPredictionContext].<br>
  /// <embed src="images/ArrayMerge_EqualTop.svg" type="image/svg+xml"/></p>
  static PredictionContext mergeArrays(
    ArrayPredictionContext a,
    ArrayPredictionContext b,
    bool rootIsWildcard,
    Map<Pair<PredictionContext, PredictionContext>, PredictionContext>?
        mergeCache,
  ) {
    if (mergeCache != null) {
      var previous = mergeCache[Pair(a, b)];
      if (previous != null) return previous;
      previous = mergeCache[Pair(b, a)];
      if (previous != null) return previous;
    }

    // merge sorted payloads a + b => M
    var i = 0; // walks a
    var j = 0; // walks b
    var k = 0; // walks target M array

    var mergedReturnStates = List<int>.filled(
      a.returnStates.length + b.returnStates.length,
      0,
    ); // TODO Will it grow?
    var mergedParents = List<PredictionContext?>.filled(
      a.returnStates.length + b.returnStates.length,
      null,
    ); // TODO Will it grow?
    // walk and merge to yield mergedParents, mergedReturnStates
    while (i < a.returnStates.length && j < b.returnStates.length) {
      final a_parent = a.parents[i];
      final b_parent = b.parents[j];
      if (a.returnStates[i] == b.returnStates[j]) {
        // same payload (stack tops are equal), must yield merged singleton
        final payload = a.returnStates[i];
        // $+$ = $
        final both$ = payload == EMPTY_RETURN_STATE &&
            a_parent == null &&
            b_parent == null;
        final ax_ax = (a_parent != null && b_parent != null) &&
            a_parent == b_parent; // ax+ax -> ax
        if (both$ || ax_ax) {
          mergedParents[k] = a_parent; // choose left
          mergedReturnStates[k] = payload;
        } else {
          // ax+ay -> a'[x,y]
          final mergedParent =
              merge(a_parent!, b_parent!, rootIsWildcard, mergeCache);
          mergedParents[k] = mergedParent;
          mergedReturnStates[k] = payload;
        }
        i++; // hop over left one as usual
        j++; // but also skip one in right side since we merge
      } else if (a.returnStates[i] < b.returnStates[j]) {
        // copy a[i] to M
        mergedParents[k] = a_parent;
        mergedReturnStates[k] = a.returnStates[i];
        i++;
      } else {
        // b > a, copy b[j] to M
        mergedParents[k] = b_parent;
        mergedReturnStates[k] = b.returnStates[j];
        j++;
      }
      k++;
    }

    // copy over any payloads remaining in either array
    if (i < a.returnStates.length) {
      for (var p = i; p < a.returnStates.length; p++) {
        mergedParents[k] = a.parents[p];
        mergedReturnStates[k] = a.returnStates[p];
        k++;
      }
    } else {
      for (var p = j; p < b.returnStates.length; p++) {
        mergedParents[k] = b.parents[p];
        mergedReturnStates[k] = b.returnStates[p];
        k++;
      }
    }

    // trim merged if we combined a few that had same stack tops
    if (k < mergedParents.length) {
      // write index < last position; trim
      if (k == 1) {
        // for just one merged element, return singleton top
        PredictionContext a_ = SingletonPredictionContext.create(
          mergedParents[0]!,
          mergedReturnStates[0],
        );
        if (mergeCache != null) mergeCache[Pair(a, b)] = a_;
        return a_;
      }

      mergedParents = List.generate(k, (n) => mergedParents[n]);
      mergedReturnStates = List.generate(k, (n) => mergedReturnStates[n]);
    }

    PredictionContext M = ArrayPredictionContext(
      mergedParents,
      mergedReturnStates,
    );

    // if we created same array as a or b, return that instead
    // TODO: track whether this is possible above during merge sort for speed
    if (M == a) {
      if (mergeCache != null) mergeCache[Pair(a, b)] = a;
      return a;
    }
    if (M == b) {
      if (mergeCache != null) mergeCache[Pair(a, b)] = b;
      return b;
    }

    combineCommonParents(mergedParents);

    if (mergeCache != null) mergeCache[Pair(a, b)] = M;
    return M;
  }

  /// Make pass over all <em>M</em> [parents]; merge any {@code equals()}
  /// ones.
  static void combineCommonParents(List<PredictionContext?> parents) {
    final uniqueParents = <PredictionContext, PredictionContext>{};

    for (var p = 0; p < parents.length; p++) {
      final parent = parents[p];
      if (parent != null && !uniqueParents.containsKey(parent)) {
        // don't replace
        uniqueParents[parent] = parent;
      }
    }

    for (var p = 0; p < parents.length; p++) {
      parents[p] = uniqueParents[parents[p]]!;
    }
  }

  static String toDOTString(PredictionContext? context) {
    if (context == null) return '';
    final buf = StringBuffer();
    buf.write('digraph G {\n');
    buf.write('rankdir=LR;\n');

    final nodes = getAllContextNodes(context);
    nodes.sort((PredictionContext o1, PredictionContext o2) {
      return o1.id - o2.id;
    });

    for (var current in nodes) {
      if (current is SingletonPredictionContext) {
        final s = current.id.toString();
        buf.write('  s');
        buf.write(s);
        var returnState = current.getReturnState(0).toString();
        if (current is EmptyPredictionContext) returnState = r'$';
        buf.write(' [label=\"');
        buf.write(returnState);
        buf.write('\"];\n');
        continue;
      }
      final arr = current as ArrayPredictionContext;
      buf.write('  s');
      buf.write(arr.id);
      buf.write(' [shape=box, label=\"');
      buf.write('[');
      var first = true;
      for (var inv in arr.returnStates) {
        if (!first) buf.write(', ');
        if (inv == EMPTY_RETURN_STATE) {
          buf.write(r'$');
        } else {
          buf.write(inv);
        }
        first = false;
      }
      buf.write(']');
      buf.write('\"];\n');
    }

    for (var current in nodes) {
      if (current == EmptyPredictionContext.Instance) continue;
      for (var i = 0; i < current.length; i++) {
        if (current.getParent(i) == null) continue;
        final s = current.id.toString();
        buf.write('  s');
        buf.write(s);
        buf.write('->');
        buf.write('s');
        buf.write(current.getParent(i)?.id);
        if (current.length > 1) {
          buf.write(' [label=\"parent[$i]\"];\n');
        } else {
          buf.write(';\n');
        }
      }
    }

    buf.write('}\n');
    return buf.toString();
  }

  // From Sam
  static PredictionContext getCachedContext(
    PredictionContext context,
    PredictionContextCache contextCache,
    Map<PredictionContext, PredictionContext> visited,
  ) {
    if (context.isEmpty) {
      return context;
    }

    var existing = visited[context];
    if (existing != null) {
      return existing;
    }

    existing = contextCache[context];
    if (existing != null) {
      visited[context] = existing;
      return existing;
    }

    var changed = false;
    var parents = <PredictionContext>[];
    for (var i = 0; i < parents.length; i++) {
      final parent = getCachedContext(
        context.getParent(i)!,
        contextCache,
        visited,
      );
      if (changed || parent != context.getParent(i)) {
        if (!changed) {
          parents = <PredictionContext>[];
          for (var j = 0; j < context.length; j++) {
            parents.add(context.getParent(j)!);
          }

          changed = true;
        }

        parents[i] = parent;
      }
    }

    if (!changed) {
      contextCache.add(context);
      visited[context] = context;
      return context;
    }

    PredictionContext updated;
    if (parents.isEmpty) {
      updated = EmptyPredictionContext.Instance;
    } else if (parents.length == 1) {
      updated = SingletonPredictionContext.create(
          parents[0], context.getReturnState(0));
    } else {
      final arrayPredictionContext = context as ArrayPredictionContext;
      updated = ArrayPredictionContext(
        parents,
        arrayPredictionContext.returnStates,
      );
    }

    contextCache.add(updated);
    visited[updated] = updated;
    visited[context] = updated;

    return updated;
  }

//	// extra structures, but cut/paste/morphed works, so leave it.
//	// seems to do a breadth-first walk
//	 static List<PredictionContext> getAllNodes(PredictionContext context) {
//		Map<PredictionContext, PredictionContext> visited =
//			new IdentityHashMap<PredictionContext, PredictionContext>();
//		Deque<PredictionContext> workList = new ArrayDeque<PredictionContext>();
//		workList.add(context);
//		visited.put(context, context);
//		List<PredictionContext> nodes = new ArrayList<PredictionContext>();
//		while (!workList.isEmpty) {
//			PredictionContext current = workList.pop();
//			nodes.add(current);
//			for (int i = 0; i < current.length; i++) {
//				PredictionContext parent = current.getParent(i);
//				if ( parent!=null && visited.put(parent, parent) == null) {
//					workList.push(parent);
//				}
//			}
//		}
//		return nodes;
//	}

  // ter's recursive version of Sam's getAllNodes()
  static List<PredictionContext> getAllContextNodes(PredictionContext context) {
    final nodes = <PredictionContext>[];
    final visited = <PredictionContext, PredictionContext>{};
    getAllContextNodes_(context, nodes, visited);
    return nodes;
  }

  static void getAllContextNodes_(
    PredictionContext? context,
    List<PredictionContext> nodes,
    Map<PredictionContext, PredictionContext> visited,
  ) {
    if (context == null || visited.containsKey(context)) return;
    visited[context] = context;
    nodes.add(context);
    for (var i = 0; i < context.length; i++) {
      getAllContextNodes_(context.getParent(i), nodes, visited);
    }
  }

  // FROM SAM
  List<String> toStrings(
    Recognizer? recognizer,
    PredictionContext stop,
    int currentState,
  ) {
    final result = <String>[];

    outer:
    for (var perm = 0;; perm++) {
      var offset = 0;
      var last = true;
      var p = this;
      var stateNumber = currentState;
      final localBuffer = StringBuffer();
      localBuffer.write('[');
      while (!p.isEmpty && p != stop) {
        var index = 0;
        if (p.length > 0) {
          var bits = 1;
          while ((1 << bits) < p.length) {
            bits++;
          }

          final mask = (1 << bits) - 1;
          index = (perm >> offset) & mask;
          last &= index >= p.length - 1;
          if (index >= p.length) {
            continue outer;
          }
          offset += bits;
        }

        if (recognizer != null) {
          if (localBuffer.length > 1) {
            // first char is '[', if more than that this isn't the first rule
            localBuffer.write(' ');
          }

          final atn = recognizer.getATN();
          final s = atn.states[stateNumber]!;
          final ruleName = recognizer.ruleNames[s.ruleIndex];
          localBuffer.write(ruleName);
        } else if (p.getReturnState(index) != EMPTY_RETURN_STATE) {
          if (!p.isEmpty) {
            if (localBuffer.length > 1) {
              // first char is '[', if more than that this isn't the first rule
              localBuffer.write(' ');
            }

            localBuffer.write(p.getReturnState(index));
          }
        }
        stateNumber = p.getReturnState(index);
        p = p.getParent(index) ?? EmptyPredictionContext.Instance;
      }
      localBuffer.write(']');
      result.add(localBuffer.toString());

      if (last) {
        break;
      }
    }

    return result;
  }
}

class SingletonPredictionContext extends PredictionContext {
  final PredictionContext? parent;
  final int returnState;

  SingletonPredictionContext(this.parent, this.returnState)
      : super(parent != null
            ? PredictionContext.calculateHashCode([parent], [returnState])
            : PredictionContext.calculateEmptyHashCode()) {
    assert(returnState != ATNState.INVALID_STATE_NUMBER);
  }

  static SingletonPredictionContext create(
    PredictionContext? parent,
    int returnState,
  ) {
    if (returnState == PredictionContext.EMPTY_RETURN_STATE && parent == null) {
      // someone can pass in the bits of an array ctx that mean $
      return EmptyPredictionContext.Instance;
    }
    return SingletonPredictionContext(parent, returnState);
  }

  @override
  int get length {
    return 1;
  }

  @override
  PredictionContext? getParent(int index) {
    assert(index == 0);
    return parent;
  }

  @override
  int getReturnState(int index) {
    assert(index == 0);
    return returnState;
  }

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) {
      return true;
    } else if (o is SingletonPredictionContext) {
      if (hashCode != o.hashCode) {
        return false; // can't be same if hash is different
      }

      final s = o;
      return returnState == s.returnState &&
          (parent != null && parent == s.parent);
    }
    return false;
  }

  @override
  String toString() {
    final up = parent != null ? parent.toString() : '';
    if (up.isEmpty) {
      if (returnState == PredictionContext.EMPTY_RETURN_STATE) {
        return r'$';
      }
      return returnState.toString();
    }
    return '$returnState $up';
  }
}

class EmptyPredictionContext extends SingletonPredictionContext {
  /// Represents {@code $} in local context prediction, which means wildcard.
  /// {@code *+x = *}.
  static final EmptyPredictionContext Instance = EmptyPredictionContext();

  EmptyPredictionContext() : super(null, PredictionContext.EMPTY_RETURN_STATE);

  @override
  bool get isEmpty {
    return true;
  }

  @override
  int get length {
    return 1;
  }

  @override
  PredictionContext? getParent(int index) {
    return null;
  }

  @override
  int getReturnState(int index) {
    return returnState;
  }

  @override
  String toString() {
    return r'$';
  }
}

class ArrayPredictionContext extends PredictionContext {
  /// Parent can be null only if full ctx mode and we make an array
  ///  from {@link #EMPTY} and non-empty. We merge {@link #EMPTY} by using null parent and
  ///  returnState == {@link #EMPTY_RETURN_STATE}.
  List<PredictionContext?> parents;

  /// Sorted for merge, no duplicates; if present,
  ///  {@link #EMPTY_RETURN_STATE} is always last.
  List<int> returnStates;

  ArrayPredictionContext.of(SingletonPredictionContext a)
      : this([a.parent], [a.returnState]);

  ArrayPredictionContext(
    // Todo: this generic should be null this wont change
    this.parents,
    this.returnStates,
  )   : assert(parents.isNotEmpty),
        assert(returnStates.isNotEmpty),
        super(PredictionContext.calculateHashCode(parents, returnStates));

  @override
  bool get isEmpty {
    // since EMPTY_RETURN_STATE can only appear in the last position, we
    // don't need to verify that size==1
    return returnStates[0] == PredictionContext.EMPTY_RETURN_STATE;
  }

  @override
  int get length {
    return returnStates.length;
  }

  @override
  PredictionContext? getParent(int index) {
    return parents[index];
  }

  @override
  int getReturnState(int index) {
    return returnStates[index];
  }

//	 int findReturnState(int returnState) {
//		return Arrays.binarySearch(returnStates, returnState);
//	}

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) {
      return true;
    } else if (o is ArrayPredictionContext) {
      if (hashCode != o.hashCode) {
        return false; // can't be same if hash is different
      }

      final a = o;
      return ListEquality().equals(returnStates, a.returnStates) &&
          ListEquality().equals(parents, a.parents);
    }
    return false;
  }

  @override
  String toString() {
    if (isEmpty) return '[]';
    final buf = StringBuffer();
    buf.write('[');
    for (var i = 0; i < returnStates.length; i++) {
      if (i > 0) buf.write(', ');
      if (returnStates[i] == PredictionContext.EMPTY_RETURN_STATE) {
        buf.write(r'$');
        continue;
      }
      buf.write(returnStates[i]);
      if (parents[i] != null) {
        buf.write(' ');
        buf.write(parents[i].toString());
      } else {
        buf.write('null');
      }
    }
    buf.write(']');
    return buf.toString();
  }
}
