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
  /**
   * Represents {@code $} in local context prediction, which means wildcard.
   * {@code *+x = *}.
   */
  static final EmptyPredictionContext EMPTY = new EmptyPredictionContext();

  /**
   * Represents {@code $} in an array in full context mode, when {@code $}
   * doesn't mean wildcard: {@code $ + x = [$,x]}. Here,
   * {@code $} = {@link #EMPTY_RETURN_STATE}.
   */
  static final int EMPTY_RETURN_STATE = 0x7FFFFFFF;

  static final int INITIAL_HASH = 1;

  static int globalNodeCount = 0;
  int id = globalNodeCount++;

  /**
   * Stores the computed hash code of this [PredictionContext]. The hash
   * code is computed in parts to match the following reference algorithm.
   *
   * <pre>
   *   int referenceHashCode() {
   *      int hash = {@link MurmurHash#initialize MurmurHash.initialize}({@link #INITIAL_HASH});
   *
   *      for (int i = 0; i &lt; {@link #size()}; i++) {
   *          hash = {@link MurmurHash#update MurmurHash.update}(hash, {@link #getParent getParent}(i));
   *      }
   *
   *      for (int i = 0; i &lt; {@link #size()}; i++) {
   *          hash = {@link MurmurHash#update MurmurHash.update}(hash, {@link #getReturnState getReturnState}(i));
   *      }
   *
   *      hash = {@link MurmurHash#finish MurmurHash.finish}(hash, 2 * {@link #size()});
   *      return hash;
   *  }
   * </pre>
   */
  final int cachedHashCode;

  PredictionContext(this.cachedHashCode);

  /** Convert a [RuleContext] tree to a [PredictionContext] graph.
   *  Return {@link #EMPTY} if [outerContext] is empty or null.
   */
  static PredictionContext fromRuleContext(ATN atn, RuleContext outerContext) {
    if (outerContext == null) outerContext = RuleContext.EMPTY;

    // if we are in RuleContext of start rule, s, then PredictionContext
    // is EMPTY. Nobody called us. (if we are empty, return empty)
    if (outerContext.parent == null || outerContext == RuleContext.EMPTY) {
      return PredictionContext.EMPTY;
    }

    // If we have a parent, convert it to a PredictionContext graph
    PredictionContext parent = EMPTY;
    parent = PredictionContext.fromRuleContext(atn, outerContext.parent);

    ATNState state = atn.states[outerContext.invokingState];
    RuleTransition transition = state.transition(0);
    return SingletonPredictionContext.create(
        parent, transition.followState.stateNumber);
  }

  int get length;

  PredictionContext getParent(int index);

  int getReturnState(int index);

  /** This means only the {@link #EMPTY} (wildcard? not sure) context is in set. */
  bool get isEmpty {
    return this == EMPTY;
  }

  bool hasEmptyPath() {
    // since EMPTY_RETURN_STATE can only appear in the last position, we check last one
    return getReturnState(length - 1) == EMPTY_RETURN_STATE;
  }

  int get hashCode {
    return cachedHashCode;
  }

  bool operator ==(Object obj);

  static int calculateEmptyHashCode() {
    int hash = MurmurHash.initialize(INITIAL_HASH);
    hash = MurmurHash.finish(hash, 0);
    return hash;
  }

  static int calculateHashCode(
      List<PredictionContext> parents, List<int> returnStates) {
    int hash = MurmurHash.initialize(INITIAL_HASH);

    for (PredictionContext parent in parents) {
      hash = MurmurHash.update(hash, parent);
    }

    for (int returnState in returnStates) {
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
      Map<Pair<PredictionContext, PredictionContext>, PredictionContext>
          mergeCache) {
    assert(a != null && b != null); // must be empty context, never null

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
      a = new ArrayPredictionContext.of(a);
    }
    if (b is SingletonPredictionContext) {
      b = new ArrayPredictionContext.of(b);
    }
    return mergeArrays(a, b, rootIsWildcard, mergeCache);
  }

  /**
   * Merge two [SingletonPredictionContext] instances.
   *
   * <p>Stack tops equal, parents merge is same; return left graph.<br>
   * <embed src="images/SingletonMerge_SameRootSamePar.svg" type="image/svg+xml"/></p>
   *
   * <p>Same stack top, parents differ; merge parents giving array node, then
   * remainders of those graphs. A new root node is created to point to the
   * merged parents.<br>
   * <embed src="images/SingletonMerge_SameRootDiffPar.svg" type="image/svg+xml"/></p>
   *
   * <p>Different stack tops pointing to same parent. Make array node for the
   * root where both element in the root point to the same (original)
   * parent.<br>
   * <embed src="images/SingletonMerge_DiffRootSamePar.svg" type="image/svg+xml"/></p>
   *
   * <p>Different stack tops pointing to different parents. Make array node for
   * the root where each element points to the corresponding original
   * parent.<br>
   * <embed src="images/SingletonMerge_DiffRootDiffPar.svg" type="image/svg+xml"/></p>
   *
   * @param a the first [SingletonPredictionContext]
   * @param b the second [SingletonPredictionContext]
   * @param rootIsWildcard [true] if this is a local-context merge,
   * otherwise false to indicate a full-context merge
   * @param mergeCache
   */
  static PredictionContext mergeSingletons(
      SingletonPredictionContext a,
      SingletonPredictionContext b,
      bool rootIsWildcard,
      Map<Pair<PredictionContext, PredictionContext>, PredictionContext>
          mergeCache) {
    if (mergeCache != null) {
      PredictionContext previous = mergeCache[Pair(a, b)];
      if (previous != null) return previous;
      previous = mergeCache[Pair(b, a)];
      if (previous != null) return previous;
    }

    PredictionContext rootMerge = mergeRoot(a, b, rootIsWildcard);
    if (rootMerge != null) {
      if (mergeCache != null) mergeCache[Pair(a, b)] = rootMerge;
      return rootMerge;
    }

    if (a.returnState == b.returnState) {
      // a == b
      PredictionContext parent =
          merge(a.parent, b.parent, rootIsWildcard, mergeCache);
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
      PredictionContext singleParent = null;
      if (a == b || (a.parent != null && a.parent == b.parent)) {
        // ax + bx = [a,b]x
        singleParent = a.parent;
      }
      if (singleParent != null) {
        // parents are same
        // sort payloads and use same parent
        List<int> payloads = [a.returnState, b.returnState];
        if (a.returnState > b.returnState) {
          payloads[0] = b.returnState;
          payloads[1] = a.returnState;
        }
        List<PredictionContext> parents = [singleParent, singleParent];
        PredictionContext a_ = new ArrayPredictionContext(parents, payloads);
        if (mergeCache != null) mergeCache[Pair(a, b)] = a_;
        return a_;
      }
      // parents differ and can't merge them. Just pack together
      // into array; can't merge.
      // ax + by = [ax,by]
      List<int> payloads = [a.returnState, b.returnState];
      List<PredictionContext> parents = [a.parent, b.parent];
      if (a.returnState > b.returnState) {
        // sort by payload
        payloads[0] = b.returnState;
        payloads[1] = a.returnState;
        parents = [b.parent, a.parent];
      }
      PredictionContext a_ = new ArrayPredictionContext(parents, payloads);
      if (mergeCache != null) mergeCache[Pair(a, b)] = a_;
      return a_;
    }
  }

  /**
   * Handle case where at least one of [a] or [b] is
   * {@link #EMPTY}. In the following diagrams, the symbol {@code $} is used
   * to represent {@link #EMPTY}.
   *
   * <h2>Local-Context Merges</h2>
   *
   * <p>These local-context merge operations are used when [rootIsWildcard]
   * is true.</p>
   *
   * <p>{@link #EMPTY} is superset of any graph; return {@link #EMPTY}.<br>
   * <embed src="images/LocalMerge_EmptyRoot.svg" type="image/svg+xml"/></p>
   *
   * <p>{@link #EMPTY} and anything is {@code #EMPTY}, so merged parent is
   * {@code #EMPTY}; return left graph.<br>
   * <embed src="images/LocalMerge_EmptyParent.svg" type="image/svg+xml"/></p>
   *
   * <p>Special case of last merge if local context.<br>
   * <embed src="images/LocalMerge_DiffRoots.svg" type="image/svg+xml"/></p>
   *
   * <h2>Full-Context Merges</h2>
   *
   * <p>These full-context merge operations are used when [rootIsWildcard]
   * is false.</p>
   *
   * <p><embed src="images/FullMerge_EmptyRoots.svg" type="image/svg+xml"/></p>
   *
   * <p>Must keep all contexts; {@link #EMPTY} in array is a special value (and
   * null parent).<br>
   * <embed src="images/FullMerge_EmptyRoot.svg" type="image/svg+xml"/></p>
   *
   * <p><embed src="images/FullMerge_SameRoot.svg" type="image/svg+xml"/></p>
   *
   * @param a the first [SingletonPredictionContext]
   * @param b the second [SingletonPredictionContext]
   * @param rootIsWildcard [true] if this is a local-context merge,
   * otherwise false to indicate a full-context merge
   */
  static PredictionContext mergeRoot(SingletonPredictionContext a,
      SingletonPredictionContext b, bool rootIsWildcard) {
    if (rootIsWildcard) {
      if (a == EMPTY) return EMPTY; // * + b = *
      if (b == EMPTY) return EMPTY; // a + * = *
    } else {
      if (a == EMPTY && b == EMPTY) return EMPTY; // $ + $ = $
      if (a == EMPTY) {
        // $ + x = [x,$]
        List<int> payloads = [b.returnState, EMPTY_RETURN_STATE];
        List<PredictionContext> parents = [b.parent, null];
        PredictionContext joined =
            new ArrayPredictionContext(parents, payloads);
        return joined;
      }
      if (b == EMPTY) {
        // x + $ = [x,$] ($ is always last if present)
        List<int> payloads = [a.returnState, EMPTY_RETURN_STATE];
        final parents = [a.parent, null];
        PredictionContext joined =
            new ArrayPredictionContext(parents, payloads);
        return joined;
      }
    }
    return null;
  }

  /**
   * Merge two [ArrayPredictionContext] instances.
   *
   * <p>Different tops, different parents.<br>
   * <embed src="images/ArrayMerge_DiffTopDiffPar.svg" type="image/svg+xml"/></p>
   *
   * <p>Shared top, same parents.<br>
   * <embed src="images/ArrayMerge_ShareTopSamePar.svg" type="image/svg+xml"/></p>
   *
   * <p>Shared top, different parents.<br>
   * <embed src="images/ArrayMerge_ShareTopDiffPar.svg" type="image/svg+xml"/></p>
   *
   * <p>Shared top, all shared parents.<br>
   * <embed src="images/ArrayMerge_ShareTopSharePar.svg" type="image/svg+xml"/></p>
   *
   * <p>Equal tops, merge parents and reduce top to
   * [SingletonPredictionContext].<br>
   * <embed src="images/ArrayMerge_EqualTop.svg" type="image/svg+xml"/></p>
   */
  static PredictionContext mergeArrays(
      ArrayPredictionContext a,
      ArrayPredictionContext b,
      bool rootIsWildcard,
      Map<Pair<PredictionContext, PredictionContext>, PredictionContext>
          mergeCache) {
    if (mergeCache != null) {
      PredictionContext previous = mergeCache[Pair(a, b)];
      if (previous != null) return previous;
      previous = mergeCache[Pair(b, a)];
      if (previous != null) return previous;
    }

    // merge sorted payloads a + b => M
    int i = 0; // walks a
    int j = 0; // walks b
    int k = 0; // walks target M array

    List<int> mergedReturnStates = List(
        a.returnStates.length + b.returnStates.length); // TODO Will it grow?
    var mergedParents = List<PredictionContext>(
        a.returnStates.length + b.returnStates.length); // TODO Will it grow?
    // walk and merge to yield mergedParents, mergedReturnStates
    while (i < a.returnStates.length && j < b.returnStates.length) {
      PredictionContext a_parent = a.parents[i];
      PredictionContext b_parent = b.parents[j];
      if (a.returnStates[i] == b.returnStates[j]) {
        // same payload (stack tops are equal), must yield merged singleton
        int payload = a.returnStates[i];
        // $+$ = $
        bool both$ = payload == EMPTY_RETURN_STATE &&
            a_parent == null &&
            b_parent == null;
        bool ax_ax = (a_parent != null && b_parent != null) &&
            a_parent == b_parent; // ax+ax -> ax
        if (both$ || ax_ax) {
          mergedParents[k] = a_parent; // choose left
          mergedReturnStates[k] = payload;
        } else {
          // ax+ay -> a'[x,y]
          PredictionContext mergedParent =
              merge(a_parent, b_parent, rootIsWildcard, mergeCache);
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
      for (int p = i; p < a.returnStates.length; p++) {
        mergedParents[k] = a.parents[p];
        mergedReturnStates[k] = a.returnStates[p];
        k++;
      }
    } else {
      for (int p = j; p < b.returnStates.length; p++) {
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
            mergedParents[0], mergedReturnStates[0]);
        if (mergeCache != null) mergeCache[Pair(a, b)] = a_;
        return a_;
      }
      mergedParents = List(k)..setRange(0, k, mergedParents);
      mergedReturnStates = List(k)..setRange(0, k, mergedReturnStates);
    }

    PredictionContext M =
        new ArrayPredictionContext(mergedParents, mergedReturnStates);

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

  /**
   * Make pass over all <em>M</em> [parents]; merge any {@code equals()}
   * ones.
   */
  static void combineCommonParents(List<PredictionContext> parents) {
    Map<PredictionContext, PredictionContext> uniqueParents =
        new Map<PredictionContext, PredictionContext>();

    for (int p = 0; p < parents.length; p++) {
      PredictionContext parent = parents[p];
      if (!uniqueParents.containsKey(parent)) {
        // don't replace
        uniqueParents[parent] = parent;
      }
    }

    for (int p = 0; p < parents.length; p++) {
      parents[p] = uniqueParents[parents[p]];
    }
  }

  static String toDOTString(PredictionContext context) {
    if (context == null) return "";
    StringBuffer buf = new StringBuffer();
    buf.write("digraph G {\n");
    buf.write("rankdir=LR;\n");

    List<PredictionContext> nodes = getAllContextNodes(context);
    nodes.sort((PredictionContext o1, PredictionContext o2) {
      return o1.id - o2.id;
    });

    for (PredictionContext current in nodes) {
      if (current is SingletonPredictionContext) {
        String s = current.id.toString();
        buf.write("  s");
        buf.write(s);
        String returnState = current.getReturnState(0).toString();
        if (current is EmptyPredictionContext) returnState = r"$";
        buf.write(" [label=\"");
        buf.write(returnState);
        buf.write("\"];\n");
        continue;
      }
      ArrayPredictionContext arr = current;
      buf.write("  s");
      buf.write(arr.id);
      buf.write(" [shape=box, label=\"");
      buf.write("[");
      bool first = true;
      for (int inv in arr.returnStates) {
        if (!first) buf.write(", ");
        if (inv == EMPTY_RETURN_STATE)
          buf.write(r"$");
        else
          buf.write(inv);
        first = false;
      }
      buf.write("]");
      buf.write("\"];\n");
    }

    for (PredictionContext current in nodes) {
      if (current == EMPTY) continue;
      for (int i = 0; i < current.length; i++) {
        if (current.getParent(i) == null) continue;
        String s = current.id.toString();
        buf.write("  s");
        buf.write(s);
        buf.write("->");
        buf.write("s");
        buf.write(current.getParent(i).id);
        if (current.length > 1)
          buf.write(" [label=\"parent[$i]\"];\n");
        else
          buf.write(";\n");
      }
    }

    buf.write("}\n");
    return buf.toString();
  }

  // From Sam
  static PredictionContext getCachedContext(
      PredictionContext context,
      PredictionContextCache contextCache,
      Map<PredictionContext, PredictionContext> visited) {
    if (context.isEmpty) {
      return context;
    }

    PredictionContext existing = visited[context];
    if (existing != null) {
      return existing;
    }

    existing = contextCache[context];
    if (existing != null) {
      visited[context] = existing;
      return existing;
    }

    bool changed = false;
    var parents = List<PredictionContext>(context.length);
    for (int i = 0; i < parents.length; i++) {
      PredictionContext parent =
          getCachedContext(context.getParent(i), contextCache, visited);
      if (changed || parent != context.getParent(i)) {
        if (!changed) {
          parents = List<PredictionContext>(context.length);
          for (int j = 0; j < context.length; j++) {
            parents[j] = context.getParent(j);
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
    if (parents.length == 0) {
      updated = EMPTY;
    } else if (parents.length == 1) {
      updated = SingletonPredictionContext.create(
          parents[0], context.getReturnState(0));
    } else {
      ArrayPredictionContext arrayPredictionContext = context;
      updated = new ArrayPredictionContext(
          parents, arrayPredictionContext.returnStates);
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
    List<PredictionContext> nodes = List<PredictionContext>();
    Map<PredictionContext, PredictionContext> visited =
        Map<PredictionContext, PredictionContext>();
    getAllContextNodes_(context, nodes, visited);
    return nodes;
  }

  static void getAllContextNodes_(
      PredictionContext context,
      List<PredictionContext> nodes,
      Map<PredictionContext, PredictionContext> visited) {
    if (context == null || visited.containsKey(context)) return;
    visited[context] = context;
    nodes.add(context);
    for (int i = 0; i < context.length; i++) {
      getAllContextNodes_(context.getParent(i), nodes, visited);
    }
  }

  // FROM SAM
  List<String> toStrings(
      Recognizer recognizer, PredictionContext stop, int currentState) {
    List<String> result = [];

    outer:
    for (int perm = 0;; perm++) {
      int offset = 0;
      bool last = true;
      PredictionContext p = this;
      int stateNumber = currentState;
      StringBuffer localBuffer = new StringBuffer();
      localBuffer.write("[");
      while (!p.isEmpty && p != stop) {
        int index = 0;
        if (p.length > 0) {
          int bits = 1;
          while ((1 << bits) < p.length) {
            bits++;
          }

          int mask = (1 << bits) - 1;
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

          ATN atn = recognizer.getATN();
          ATNState s = atn.states[stateNumber];
          String ruleName = recognizer.ruleNames[s.ruleIndex];
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
        p = p.getParent(index);
      }
      localBuffer.write("]");
      result.add(localBuffer.toString());

      if (last) {
        break;
      }
    }

    return result;
  }
}

class SingletonPredictionContext extends PredictionContext {
  final PredictionContext parent;
  final int returnState;

  SingletonPredictionContext(PredictionContext this.parent, this.returnState)
      : super(parent != null
            ? PredictionContext.calculateHashCode([parent], [returnState])
            : PredictionContext.calculateEmptyHashCode()) {
    assert(this.returnState != ATNState.INVALID_STATE_NUMBER);
  }

  static SingletonPredictionContext create(
      PredictionContext parent, int returnState) {
    if (returnState == PredictionContext.EMPTY_RETURN_STATE && parent == null) {
      // someone can pass in the bits of an array ctx that mean $
      return PredictionContext.EMPTY;
    }
    return new SingletonPredictionContext(parent, returnState);
  }

  int get length {
    return 1;
  }

  PredictionContext getParent(int index) {
    assert(index == 0);
    return parent;
  }

  int getReturnState(int index) {
    assert(index == 0);
    return returnState;
  }

  bool operator ==(Object o) {
    if (identical(this, o)) {
      return true;
    } else if (o is SingletonPredictionContext) {
      if (this.hashCode != o.hashCode) {
        return false; // can't be same if hash is different
      }

      SingletonPredictionContext s = o;
      return returnState == s.returnState &&
          (parent != null && parent == s.parent);
    }
    return false;
  }

  String toString() {
    String up = parent != null ? parent.toString() : "";
    if (up.length == 0) {
      if (returnState == PredictionContext.EMPTY_RETURN_STATE) {
        return r"$";
      }
      return returnState.toString();
    }
    return "$returnState $up";
  }
}

class EmptyPredictionContext extends SingletonPredictionContext {
  EmptyPredictionContext() : super(null, PredictionContext.EMPTY_RETURN_STATE);

  bool get isEmpty {
    return true;
  }

  int get length {
    return 1;
  }

  PredictionContext getParent(int index) {
    return null;
  }

  int getReturnState(int index) {
    return returnState;
  }

  String toString() {
    return r"$";
  }
}

class ArrayPredictionContext extends PredictionContext {
  /** Parent can be null only if full ctx mode and we make an array
   *  from {@link #EMPTY} and non-empty. We merge {@link #EMPTY} by using null parent and
   *  returnState == {@link #EMPTY_RETURN_STATE}.
   */
  List<PredictionContext> parents;

  /** Sorted for merge, no duplicates; if present,
   *  {@link #EMPTY_RETURN_STATE} is always last.
   */
  List<int> returnStates;

  ArrayPredictionContext.of(SingletonPredictionContext a)
      : this([a.parent], [a.returnState]);

  ArrayPredictionContext(
      List<PredictionContext> parents, List<int> returnStates)
      : super(PredictionContext.calculateHashCode(parents, returnStates)) {
    assert(parents != null && parents.length > 0);
    assert(returnStates != null && returnStates.length > 0);
//		System.err.println("CREATE ARRAY: "+Arrays.toString(parents)+", "+Arrays.toString(returnStates));
    this.parents = parents;
    this.returnStates = returnStates;
  }

  bool get isEmpty {
    // since EMPTY_RETURN_STATE can only appear in the last position, we
    // don't need to verify that size==1
    return returnStates[0] == PredictionContext.EMPTY_RETURN_STATE;
  }

  int get length {
    return returnStates.length;
  }

  PredictionContext getParent(int index) {
    return parents[index];
  }

  int getReturnState(int index) {
    return returnStates[index];
  }

//	 int findReturnState(int returnState) {
//		return Arrays.binarySearch(returnStates, returnState);
//	}

  bool operator ==(Object o) {
    if (identical(this, o)) {
      return true;
    } else if (o is ArrayPredictionContext) {
      if (this.hashCode != o.hashCode) {
        return false; // can't be same if hash is different
      }

      ArrayPredictionContext a = o;
      return ListEquality().equals(returnStates, a.returnStates) &&
          ListEquality().equals(parents, a.parents);
    }
    return false;
  }

  String toString() {
    if (isEmpty) return "[]";
    StringBuffer buf = new StringBuffer();
    buf.write("[");
    for (int i = 0; i < returnStates.length; i++) {
      if (i > 0) buf.write(", ");
      if (returnStates[i] == PredictionContext.EMPTY_RETURN_STATE) {
        buf.write(r"$");
        continue;
      }
      buf.write(returnStates[i]);
      if (parents[i] != null) {
        buf.write(' ');
        buf.write(parents[i].toString());
      } else {
        buf.write("null");
      }
    }
    buf.write("]");
    return buf.toString();
  }
}
