/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.PredictionContext;

import antlr.v4.runtime.InterfaceRecognizer;
import antlr.v4.runtime.RuleContext;
import antlr.v4.runtime.atn.ATN;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.ArrayPredictionContext;
import antlr.v4.runtime.atn.EmptyPredictionContext;
import antlr.v4.runtime.atn.PredictionContextCache;
import antlr.v4.runtime.atn.RuleTransition;
import antlr.v4.runtime.atn.SingletonPredictionContext;
import antlr.v4.runtime.misc;
import core.atomic;
import std.algorithm.sorting;
import std.array;
import std.conv;
import std.typecons;

/**
 * TODO add class description
 */
abstract class PredictionContext
{

    /**
     * Represents {@code $} in local context prediction, which means wildcard.
     * {@code *+x = *}.
     */
    public static const EmptyPredictionContext EMPTY = new EmptyPredictionContext;

    /**
     * Represents {@code $} in an array in full context mode, when {@code $}
     * doesn't mean wildcard: {@code $ + x = [$,x]}. Here,
     * {@code $} = {@link #EMPTY_RETURN_STATE}.
     */
    public static immutable int EMPTY_RETURN_STATE = int.max;

    private static immutable int INITIAL_HASH = 1;

    /**
     * @uml
     * @shared
     */
    public static shared int globalNodeCount = 0;

    public int id;

    /**
     * Stores the computed hash code of this {@link PredictionContext}. The hash
     * code is computed in parts to match the following reference algorithm.
     *
     * <pre>
     * private int referenceHashCode() {
     *    int hash = {@link MurmurHash#initialize MurmurHash.initialize}({@link #INITIAL_HASH});
     *
     *    for (int i = 0; i &lt; {@link #size()}; i++) {
     *       hash = {@link MurmurHash#update MurmurHash.update}(hash, {@link #getParent getParent}(i));
     *     }
     *
     *    for (int i = 0; i &lt; {@link #size()}; i++) {
     *        hash = {@link MurmurHash#update MurmurHash.update}(hash, {@link #getReturnState getReturnState}(i));
     *     }
     *
     *    hash = {@link MurmurHash#finish MurmurHash.finish}(hash, 2 * {@link #size()});
     *    return hash;
     *   }
     * </pre>
     */
    private size_t cachedHashCode;

    public this()
    {
        id = globalNodeCount;
        atomicOp!"+="(globalNodeCount, 1);
    }

    public this(size_t cachedHashCode)
    {
        this.cachedHashCode = cachedHashCode;
    }

    public static PredictionContext fromRuleContext(ATN atn, RuleContext outerContext)
    {
        if (outerContext is null)
            outerContext = cast(RuleContext)RuleContext.EMPTY;
        // if we are in RuleContext of start rule, s, then PredictionContext
        // is EMPTY. Nobody called us. (if we are empty, return empty)
        if (outerContext.parent is null ||
             outerContext == cast(RuleContext)RuleContext.EMPTY)
            {
                return cast(PredictionContext)PredictionContext.EMPTY;
            }

        // If we have a parent, convert it to a PredictionContext graph
        PredictionContext parent = cast(PredictionContext)EMPTY;
        parent = PredictionContext.fromRuleContext(atn, outerContext.parent);

        ATNState state = atn.states[outerContext.invokingState];
        RuleTransition transition = cast(RuleTransition)state.transition(0);
        return SingletonPredictionContext.create(parent, transition.followState.stateNumber);
    }

    abstract public size_t size();

    abstract public PredictionContext getParent(int index);

    abstract public int getReturnState(int index);

    /**
     * @uml
     * This means only the {@link #EMPTY} context is in set.
     */
    public bool isEmpty()
    {
        return this == EMPTY;
    }

    public bool hasEmptyPath()
    {
        // since EMPTY_RETURN_STATE can only appear in the last position, we check last one
        return getReturnState(to!int(size() - 1)) == EMPTY_RETURN_STATE;
    }

    /**
     * @uml
     * @safe
     * @nothrow
     * @override
     */
    public override size_t toHash() @safe nothrow
    {
        return cachedHashCode;
    }

    /**
     * @uml
     * @override
     */
    abstract public override bool opEquals(Object obj);

    public static size_t calculateEmptyHashCode()
    {
        size_t hash = MurmurHash.initialize(INITIAL_HASH);
        hash = MurmurHash.finish(hash, 0);
        return hash;
    }

    public size_t calculateHashCode(PredictionContext parent, int returnState)
    {
        size_t hash = MurmurHash.initialize(INITIAL_HASH);
        hash = MurmurHash.update!PredictionContext(hash, parent);
        hash = MurmurHash.update(hash, returnState);
        hash = MurmurHash.finish(hash, 2);
        return hash;
    }

    public static size_t calculateHashCode(PredictionContext[] parents, int[] returnStates)
    {
        size_t hash = MurmurHash.initialize(INITIAL_HASH);

        foreach (parent; parents) {
            hash = MurmurHash.update!PredictionContext(hash, parent);
        }

        foreach (returnState; returnStates) {
            hash = MurmurHash.update(hash, returnState);
        }

        hash = MurmurHash.finish(hash, (2 * parents.length));
        return hash;
    }

    public static PredictionContext merge(PredictionContext a, PredictionContext b, bool rootIsWildcard,
                                          ref DoubleKeyMap!(PredictionContext, PredictionContext, PredictionContext) mergeCache)
    in
    {
        assert(a !is null && b !is null); // must be empty context, never null
    }
    do
    {
        // share same graph if both same
        if (a is b || a.opEquals(b))
            return a;
        if (cast(SingletonPredictionContext)a &&
            cast(SingletonPredictionContext)b) {
            return mergeSingletons(cast(SingletonPredictionContext)a,
                                   cast(SingletonPredictionContext)b,
                                   rootIsWildcard, mergeCache);
        }
        // At least one of a or b is array
        // If one is $ and rootIsWildcard, return $ as * wildcard
        if (rootIsWildcard) {
            if (cast(EmptyPredictionContext)a) return a;
            if (cast(EmptyPredictionContext)b) return b;
        }

        // convert singleton so both are arrays to normalize
        if (cast(SingletonPredictionContext)a) {
            a = new ArrayPredictionContext(cast(SingletonPredictionContext)a);
        }
        if (cast(SingletonPredictionContext)b) {
            b = new ArrayPredictionContext(cast(SingletonPredictionContext)b);
        }
        return mergeArrays(cast(ArrayPredictionContext)a, cast(ArrayPredictionContext)b,
                           rootIsWildcard, mergeCache);
    }

    /**
     * @uml
     * Merge two {@link SingletonPredictionContext} instances.
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
     *  @param a the first {@link SingletonPredictionContext}
     *  @param b the second {@link SingletonPredictionContext}
     *  @param rootIsWildcard {@code true} if this is a local-context merge,
     *  otherwise false to indicate a full-context merge
     *  @param mergeCache
     */
    public static PredictionContext mergeSingletons(SingletonPredictionContext a, SingletonPredictionContext b,
                                                    bool rootIsWildcard, ref DoubleKeyMap!(PredictionContext, PredictionContext, PredictionContext) mergeCache)
    {
        if (mergeCache !is null ) {
            Nullable!PredictionContext previous = mergeCache.get(a, b);
            if (!previous.isNull) return previous.get;
            previous = mergeCache.get(b, a);
            if (!previous.isNull) return previous.get;
        }

        PredictionContext rootMerge = mergeRoot(a, b, rootIsWildcard);
        if (rootMerge !is null) {
            if (mergeCache !is null) mergeCache.put(a, b, rootMerge);
            return rootMerge;
        }

        if (a.returnState == b.returnState) { // a == b
            PredictionContext parent = merge(a.parent, b.parent, rootIsWildcard, mergeCache);
            // if parent is same as existing a or b parent or reduced to a parent, return it
            if ( parent == a.parent )
                return a; // ax + bx = ax, if a=b
            if ( parent == b.parent )
                return b; // ax + bx = bx, if a=b
            // else: ax + ay = a'[x,y]
            // merge parents x and y, giving array node with x,y then remainders
            // of those graphs.  dup a, a' points at merged array
            // new joined parent so create new singleton pointing to it, a'
            PredictionContext a_ = SingletonPredictionContext.create(parent, a.returnState);
            if (mergeCache !is null)
                mergeCache.put(a, b, a_);
            return a_;
        }
        else { // a != b payloads differ
            // see if we can collapse parents due to $+x parents if local ctx
            PredictionContext singleParent = null;
            if (a == b || (a.parent !is null && a.parent.opEquals(b.parent))) { // ax + bx = [a,b]x
                singleParent = a.parent;
            }
            if (singleParent !is null) {  // parents are same
                // sort payloads and use same parent
                int[] payloads = [a.returnState, b.returnState];
                if ( a.returnState > b.returnState ) {
                    payloads[0] = b.returnState;
                    payloads[1] = a.returnState;
                }
                PredictionContext[] parents = [singleParent, singleParent];
                PredictionContext a_ = new ArrayPredictionContext(parents, payloads);
                if (mergeCache !is null) mergeCache.put(a, b, a_);
                return a_;
            }
            // parents differ and can't merge them. Just pack together
            // into array; can't merge.
            // ax + by = [ax,by]
            int[] payloads = [a.returnState, b.returnState];
            PredictionContext[] parents = [a.parent, b.parent];
            if ( a.returnState > b.returnState ) { // sort by payload
                payloads[0] = b.returnState;
                payloads[1] = a.returnState;
                parents.length = 0;
                parents ~= b.parent;
                parents ~= a.parent;
            }
            PredictionContext a_ = new ArrayPredictionContext(parents, payloads);
            if (mergeCache !is null ) mergeCache.put(a, b, a_);
            return a_;
        }
    }

    /**
     * Handle case where at least one of {@code a} or {@code b} is
     * {@link #EMPTY}. In the following diagrams, the symbol {@code $} is used
     * to represent {@link #EMPTY}.
     *
     * <h2>Local-Context Merges</h2>
     *
     * <p>These local-context merge operations are used when {@code rootIsWildcard}
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
     * <p>These full-context merge operations are used when {@code rootIsWildcard}
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
     *  @param a the first {@link SingletonPredictionContext}
     *  @param b the second {@link SingletonPredictionContext}
     *  @param rootIsWildcard {@code true} if this is a local-context merge,
     *  otherwise false to indicate a full-context merge
     */
    public static PredictionContext mergeRoot(SingletonPredictionContext a, SingletonPredictionContext b,
                                              bool rootIsWildcard)
    {
        if (rootIsWildcard) {
            if ( a == EMPTY ) return cast(PredictionContext)EMPTY;  // * + b = *
            if ( b == EMPTY ) return cast(PredictionContext)EMPTY;  // a + * = *
        }
        else {
            if ( a == EMPTY && b == EMPTY ) return cast(PredictionContext)EMPTY; // $ + $ = $
            if ( a == EMPTY ) { // $ + x = [$,x]
                int[] payloads = [b.returnState, EMPTY_RETURN_STATE];
                PredictionContext[] parents = [b.parent, null];
                PredictionContext joined =
                    new ArrayPredictionContext(parents, payloads);
                return joined;
            }
            if ( b == EMPTY ) { // x + $ = [$,x] ($ is always first if present)
                int[] payloads = [a.returnState, EMPTY_RETURN_STATE];
                PredictionContext[] parents = [a.parent, null];
                PredictionContext joined =
                    new ArrayPredictionContext(parents, payloads);
                return joined;
            }
        }
        return null;
    }

    /**
     * Merge two {@link ArrayPredictionContext} instances.
     *          *
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
     * {@link SingletonPredictionContext}.<br>
     * <embed src="images/ArrayMerge_EqualTop.svg" type="image/svg+xml"/></p>
     */
    public static PredictionContext mergeArrays(ArrayPredictionContext a, ArrayPredictionContext b,
                                                bool rootIsWildcard, ref DoubleKeyMap!(PredictionContext, PredictionContext, PredictionContext) mergeCache)
    {
        if (mergeCache) {
            Nullable!PredictionContext previous = mergeCache.get(a, b);
            if (!previous.isNull)
                return previous.get;
            previous = mergeCache.get(b, a);
            if (!previous.isNull)
                return previous.get;
        }

        // merge sorted payloads a + b => M
        int i = 0; // walks a
        int j = 0; // walks b
        int k = 0; // walks target M array

        int[] mergedReturnStates =
            new int[a.returnStates.length + b.returnStates.length];
        PredictionContext[] mergedParents =
            new PredictionContext[a.returnStates.length + b.returnStates.length];
        // walk and merge to yield mergedParents, mergedReturnStates

        while ( i<a.returnStates.length && j<b.returnStates.length ) {
            PredictionContext a_parent = a.parents[i];
            PredictionContext b_parent = b.parents[j];
            if ( a.returnStates[i]==b.returnStates[j] ) {
                // same payload (stack tops are equal), must yield merged singleton
                int payload = a.returnStates[i];
                // $+$ = $
                bool both = payload == EMPTY_RETURN_STATE &&
                    a_parent is null && b_parent is null;
                bool ax_ax = (a_parent !is null && b_parent !is null) &&
                    a_parent.opEquals(b_parent); // ax+ax -> ax
                if (both || ax_ax ) {
                    mergedParents[k] = a_parent; // choose left
                    mergedReturnStates[k] = payload;
                }
                else { // ax+ay -> a'[x,y]
                    PredictionContext mergedParent =
                        merge(a_parent, b_parent, rootIsWildcard, mergeCache);
                    mergedParents[k] = mergedParent;
                    mergedReturnStates[k] = payload;
                }
                i++; // hop over left one as usual
                j++; // but also skip one in right side since we merge
            }
            else if ( a.returnStates[i]<b.returnStates[j] ) { // copy a[i] to M
                mergedParents[k] = a_parent;
                mergedReturnStates[k] = a.returnStates[i];
                i++;
            }
            else { // b > a, copy b[j] to M
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
        }
        else {
            for (int p = j; p < b.returnStates.length; p++) {
                mergedParents[k] = b.parents[p];
                mergedReturnStates[k] = b.returnStates[p];
                k++;
            }
        }

        // trim merged if we combined a few that had same stack tops
        if ( k < mergedParents.length ) { // write index < last position; trim
            if ( k == 1 ) { // for just one merged element, return singleton top
                PredictionContext a_ =
                    SingletonPredictionContext.create(mergedParents[0],
                                                      mergedReturnStates[0]);
                if ( mergeCache !is null ) mergeCache.put(a,b,a_);
                return a_;
            }
            mergedParents = mergedParents[0..k];
            mergedReturnStates = mergedReturnStates[0..k];
        }

        PredictionContext M =
            new ArrayPredictionContext(mergedParents, mergedReturnStates);

        // if we created same array as a or b, return that instead
        // TODO: track whether this is possible above during merge sort for speed
        if ( M.opEquals(a) ) {
            if ( mergeCache !is null ) mergeCache.put(a,b,a);
            return a;
        }
        if ( M.opEquals(b) ) {
            if ( mergeCache !is null ) mergeCache.put(a,b,b);
            return b;
        }

        combineCommonParents(mergedParents);

        if ( mergeCache !is null ) mergeCache.put(a,b,M);
        return M;
    }

    /**
     * Make pass over all <em>M</em> {@code parents}; merge any {@code equals()}
     * ones.
     */
    public static void combineCommonParents(ref PredictionContext[] parents)
    {
        PredictionContext[PredictionContext] uniqueParents;
        for (int p = 0; p < parents.length; p++) {
            PredictionContext parent = parents[p];
            if (!(parent in uniqueParents)) { // don't replace
                uniqueParents[parent] = parent;
            }
        }
        for (int p = 0; p < parents.length; p++) {
            parents[p] = uniqueParents[parents[p]];
        }
    }

    public static string toDOTString(PredictionContext context)
    {
        if (context is null) return "";
        auto buf = appender!string;
        buf.put("digraph G {\n");
        buf.put("rankdir=LR;\n");

        PredictionContext[] nodes = getAllContextNodes(context);
        nodes.sort();
        foreach (PredictionContext current; nodes) {
            if (current.classinfo == SingletonPredictionContext.classinfo) {
                string s = to!string(current.id);
                buf.put("  s" ~ s);
                string returnState = to!string(current.getReturnState(0));
                if (current.classinfo == EmptyPredictionContext.classinfo)
                    returnState = "$";
                buf.put(" [label=\"" ~ returnState ~ "\"];\n");
                continue;
            }
            ArrayPredictionContext arr = cast(ArrayPredictionContext)current;
            buf.put("  s" ~ to!string(arr.id));
            buf.put(" [shape=box, label=\"");
            buf.put("[");
            bool first = true;
            foreach (int inv; arr.returnStates) {
                if (!first) buf.put(", ");
                if (inv == EMPTY_RETURN_STATE) buf.put("$");
                else buf.put(to!string(inv));
                first = false;
            }
            buf.put("]");
            buf.put("\"];\n");
        }

        foreach (PredictionContext current; nodes) {
            if (current == EMPTY) continue;
            for (int i = 0; i < current.size(); i++) {
                if (current.getParent(i) is null) continue;
                string s = to!string(current.id);
                buf.put("  s" ~ s);
                buf.put("->");
                buf.put("s");
                buf.put(to!string(current.getParent(i).id));
                if ( current.size()>1 ) buf.put(" [label=\"parent[" ~ to!string(i) ~ "]\"];\n");
                else buf.put(";\n");
            }
        }

        buf.put("}\n");
        return buf.data;
    }

    /**
     * ref visited ?
     */
    public static PredictionContext getCachedContext(PredictionContext context, PredictionContextCache contextCache,
                                                    PredictionContext[PredictionContext] visited)
    {
        if (context.isEmpty) {
            return context;
        }

        if (context in visited) {
            return visited[context];
        }

        if (contextCache.hasKey(context)) {
            auto existing = contextCache.get(context);
            visited[context] = existing;
            return existing;
        }

        bool changed = false;
        PredictionContext[] parents = new PredictionContext[context.size];
        for (int i = 0; i < parents.length; i++) {
            PredictionContext parent = getCachedContext(context.getParent(i), contextCache, visited);
            if (changed || parent != context.getParent(i)) {
                if (!changed) {
                    parents = new PredictionContext[context.size];
                    for (int j = 0; j < context.size; j++) {
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
            updated = cast(PredictionContext)EMPTY;
        }
        else if (parents.length == 1) {
            updated = SingletonPredictionContext.create(parents[0], context.getReturnState(0));
        }
        else {
            ArrayPredictionContext arrayPredictionContext = cast(ArrayPredictionContext)context;
            updated = new ArrayPredictionContext(parents, arrayPredictionContext.returnStates);
        }
        contextCache.add(updated);
        visited[updated] = updated;
        visited[context] = updated;

        return updated;
    }

    /**
     * recursive version of Sam's getAllNodes()
     */
    public static PredictionContext[] getAllContextNodes(PredictionContext context)
    {
        PredictionContext[] nodes;
        PredictionContext[PredictionContext] visited;
        getAllContextNodes_(context, nodes, visited);
        return nodes;
    }

    public static void getAllContextNodes_(PredictionContext context, PredictionContext[] nodes,
                                           PredictionContext[PredictionContext] visited)
    {
        if (context is null || (context in visited)) return;
        visited[context] = context;
        nodes ~= context;
        for (int i = 0; i < context.size; i++) {
            getAllContextNodes_(context.getParent(i), nodes, visited);
        }
    }

    public string[] toStrings(InterfaceRecognizer recognizer, int currentState)
    {
        return toStrings(recognizer, cast(PredictionContext)EMPTY, currentState);
    }

    public string[] toStrings(InterfaceRecognizer recognizer, PredictionContext stop,
                              int currentState)
    {
        string[] result;
    outer:
        for (int perm = 0; ; perm++) {
            int offset = 0;
            bool last = true;
            PredictionContext p = this;
            int stateNumber = currentState;
            auto localBuffer = appender!string;
            localBuffer.put("[");
            while (!p.isEmpty() && p != stop) {
                int index = 0;
                if (p.size > 0) {
                    int bits = 1;
                    while ((1 << bits) < p.size) {
                        bits++;
                    }
                    int mask = (1 << bits) - 1;
                    index = (perm >> offset) & mask;
                    last &= index >= p.size - 1;
                    if (index >= p.size) {
                        continue outer;
                    }
                    offset += bits;
                }
                if (recognizer !is null) {
                    if (localBuffer.data.length > 1) {
                        // first char is '[', if more than that this isn't the first rule
                        localBuffer.put(' ');
                    }
                    ATN atn = recognizer.getATN();
                    ATNState s = atn.states[stateNumber];
                    string ruleName = recognizer.getRuleNames()[s.ruleIndex];
                    localBuffer.put(ruleName);
                }
                else if ( p.getReturnState(index)!= EMPTY_RETURN_STATE) {
                    if ( !p.isEmpty ) {
                        if (localBuffer.data.length > 1) {
                            // first char is '[', if more than that this isn't the first rule
                            localBuffer.put(' ');
                        }

                        localBuffer.put(to!string(p.getReturnState(index)));
                    }
                }
                stateNumber = p.getReturnState(index);
                p = p.getParent(index);
            }
            localBuffer.put("]");
            result ~= localBuffer.data;

            if (last) {
                break;
            }
        }
        return result;
    }

}

version(unittest) {
    import dshould : be, equal, not, should;
    import unit_threaded;

    class Test {

        @Tags("ArrayPredictionContext")
        @("empty")
        unittest {
            auto spcA = new EmptyPredictionContext;
            spcA.should.not.be(null);
            auto spcB = new SingletonPredictionContext(new EmptyPredictionContext, 0);
            spcA.should.not.equal(spcB);
            spcA.hasEmptyPath.should.equal(true);
            spcA.isEmpty.should.equal(true);
            spcB.hasEmptyPath.should.equal(false);
            spcB.isEmpty.should.equal(false);
        }

        @Tags("ArrayPredictionContext")
        @("mergeArrayContext")
        unittest {
            DoubleKeyMap!(PredictionContext, PredictionContext, PredictionContext) mergeCache;
            auto spcA = new EmptyPredictionContext;
            auto apcA = new ArrayPredictionContext(spcA);
            apcA.should.not.be(null);
            auto spcB = new EmptyPredictionContext;
            auto apcB = new ArrayPredictionContext(spcB);
            apcB.should.not.be(null);
            auto spcC = new EmptyPredictionContext;

            PredictionContext[] predA = [apcA, apcB, spcC, apcA]; // not unique
            predA.length.should.equal(4);
            PredictionContext.combineCommonParents(predA);
            predA.length.should.equal(4);
        }

        @Tags("ArrayPredictionContext")
        @("mergeEmptyContext")
        unittest {
            auto spcC = new EmptyPredictionContext;
            spcC.should.not.be(null);
            auto spcD = new EmptyPredictionContext;

            PredictionContext[] predB = [spcC, spcD];
            PredictionContext.combineCommonParents(predB);
        }
    }
}
