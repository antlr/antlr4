package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.DoubleKeyMap;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class PredictionContext implements Iterable<SingletonPredictionContext>,
													   Comparable<PredictionContext> // to sort node lists by id
{
	/** Represents $ in local ctx prediction, which means wildcard. *+x = *. */
	public static final EmptyPredictionContext EMPTY = new EmptyPredictionContext();

	/** Represents $ in an array in full ctx mode, when $ doesn't mean wildcard:
	 *  $ + x = [$,x]. Here, $ = EMPTY_RETURN_STATE.
	 */
	public static final int EMPTY_RETURN_STATE = Integer.MAX_VALUE;

	public static int globalNodeCount = 0;
	public final int id = globalNodeCount++;

	public final int cachedHashCode;

	protected PredictionContext(int cachedHashCode) {
		this.cachedHashCode = cachedHashCode;
	}

	/** Convert a RuleContext tree to a PredictionContext graph.
	 *  Return EMPTY if outerContext is empty or null.
	 */
	public static PredictionContext fromRuleContext(@NotNull ATN atn, RuleContext outerContext) {
		if ( outerContext==null ) outerContext = RuleContext.EMPTY;

		// if we are in RuleContext of start rule, s, then PredictionContext
		// is EMPTY. Nobody called us. (if we are empty, return empty)
		if ( outerContext.parent==null || outerContext==RuleContext.EMPTY ) {
			return PredictionContext.EMPTY;
		}

		// If we have a parent, convert it to a PredictionContext graph
		PredictionContext parent = EMPTY;
		if ( outerContext.parent != null ) {
			parent = PredictionContext.fromRuleContext(atn, outerContext.parent);
		}

		ATNState state = atn.states.get(outerContext.invokingState);
		RuleTransition transition = (RuleTransition)state.transition(0);
		return SingletonPredictionContext.create(parent, transition.followState.stateNumber);
	}

	@Override
	public abstract Iterator<SingletonPredictionContext> iterator();

	public abstract int size();

	public abstract PredictionContext getParent(int index);

	public abstract int getReturnState(int index);

	/** This means only the EMPTY context is in set */
	public boolean isEmpty() {
		return this == EMPTY;
	}

	public boolean hasEmptyPath() {
		return getReturnState(size() - 1) == EMPTY_RETURN_STATE;
	}

	@Override
	public int compareTo(PredictionContext o) { // used for toDotString to print nodes in order
		return id - o.id;
	}

	@Override
	public int hashCode() {
		return cachedHashCode;
	}

	protected static int calculateHashCode(int parentHashCode, int returnStateHashCode) {
		return 5 * 5 * 7 + 5 * parentHashCode + returnStateHashCode;
	}

	/** Two contexts conflict() if they are equals() or one is a stack suffix
	 *  of the other.  For example, contexts [21 12 $] and [21 9 $] do not
	 *  conflict, but [21 $] and [21 12 $] do conflict.  Note that I should
	 *  probably not show the $ in this case.  There is a dummy node for each
	 *  stack that just means empty; $ is a marker that's all.
	 *
	 *  This is used in relation to checking conflicts associated with a
	 *  single NFA state's configurations within a single DFA state.
	 *  If there are configurations s and t within a DFA state such that
	 *  s.state=t.state && s.alt != t.alt && s.ctx conflicts t.ctx then
	 *  the DFA state predicts more than a single alt--it's nondeterministic.
	 *  Two contexts conflict if they are the same or if one is a suffix
	 *  of the other.
	 *
	 *  When comparing contexts, if one context has a stack and the other
	 *  does not then they should be considered the same context.  The only
	 *  way for an NFA state p to have an empty context and a nonempty context
	 *  is the case when closure falls off end of rule without a call stack
	 *  and re-enters the rule with a context.  This resolves the issue I
	 *  discussed with Sriram Srinivasan Feb 28, 2005 about not terminating
	 *  fast enough upon nondeterminism.
	 *
	 *  UPDATE FOR GRAPH STACK; no suffix
	 */
//	public boolean conflictsWith(PredictionContext other) {
//		return this.equals(other);
//	}

	// dispatch
	public static PredictionContext merge(
		PredictionContext a, PredictionContext b,
		boolean rootIsWildcard,
		DoubleKeyMap<PredictionContext,PredictionContext,PredictionContext> mergeCache)
	{
		// share same graph if both same
		if ( (a==null&&b==null) || a==b || (a!=null&&a.equals(b)) ) return a;

		if ( a instanceof SingletonPredictionContext && b instanceof SingletonPredictionContext) {
			return mergeSingletons((SingletonPredictionContext)a,
								   (SingletonPredictionContext)b,
								   rootIsWildcard, mergeCache);
		}

		// At least one of a or b is array
		// If one is $ and rootIsWildcard, return $ as * wildcard
		if ( rootIsWildcard ) {
			if ( a instanceof EmptyPredictionContext ) return a;
			if ( b instanceof EmptyPredictionContext ) return b;
		}

		// convert singleton so both are arrays to normalize
		if ( a instanceof SingletonPredictionContext ) {
			a = new ArrayPredictionContext((SingletonPredictionContext)a);
		}
		if ( b instanceof SingletonPredictionContext) {
			b = new ArrayPredictionContext((SingletonPredictionContext)b);
		}
		return mergeArrays((ArrayPredictionContext) a, (ArrayPredictionContext) b,
						   rootIsWildcard, mergeCache);
	}

	// http://www.antlr.org/wiki/download/attachments/32014352/singleton-merge.png
	public static PredictionContext mergeSingletons(
		SingletonPredictionContext a,
		SingletonPredictionContext b,
		boolean rootIsWildcard,
		DoubleKeyMap<PredictionContext,PredictionContext,PredictionContext> mergeCache)
	{
		if ( mergeCache!=null ) {
			PredictionContext previous = mergeCache.get(a,b);
			if ( previous!=null ) return previous;
			previous = mergeCache.get(b,a);
			if ( previous!=null ) return previous;
		}

		PredictionContext rootMerge = mergeRoot(a, b, rootIsWildcard);
		if ( rootMerge!=null ) {
			if ( mergeCache!=null ) mergeCache.put(a, b, rootMerge);
			return rootMerge;
		}

		if ( a.returnState==b.returnState ) { // a == b
			PredictionContext parent = merge(a.parent, b.parent, rootIsWildcard, mergeCache);
			// if parent is same as existing a or b parent or reduced to a parent, return it
			if ( parent == a.parent ) return a; // ax + bx = ax, if a=b
			if ( parent == b.parent ) return b; // ax + bx = bx, if a=b
			// else: ax + ay = a'[x,y]
			// merge parents x and y, giving array node with x,y then remainders
			// of those graphs.  dup a, a' points at merged array
			// new joined parent so create new singleton pointing to it, a'
			PredictionContext a_ = SingletonPredictionContext.create(parent, a.returnState);
			if ( mergeCache!=null ) mergeCache.put(a, b, a_);
			return a_;
		}
		else { // a != b payloads differ
			// see if we can collapse parents due to $+x parents if local ctx
			PredictionContext singleParent = null;
			if ( a==b || (a.parent!=null && a.parent.equals(b.parent)) ) { // ax + bx = [a,b]x
				singleParent = a.parent;
			}
			if ( singleParent!=null ) {	// parents are same
				// sort payloads and use same parent
				int[] payloads = {a.returnState, b.returnState};
				if ( a.returnState > b.returnState ) {
					payloads[0] = b.returnState;
					payloads[1] = a.returnState;
				}
				PredictionContext[] parents = {singleParent, singleParent};
				PredictionContext a_ = new ArrayPredictionContext(parents, payloads);
				if ( mergeCache!=null ) mergeCache.put(a, b, a_);
				return a_;
			}
			// parents differ and can't merge them. Just pack together
			// into array; can't merge.
			// ax + by = [ax,by]
			int[] payloads = {a.returnState, b.returnState};
			PredictionContext[] parents = {a.parent, b.parent};
			if ( a.returnState > b.returnState ) { // sort by payload
				payloads[0] = b.returnState;
				payloads[1] = a.returnState;
				parents = new PredictionContext[] {b.parent, a.parent};
			}
			PredictionContext a_ = new ArrayPredictionContext(parents, payloads);
			if ( mergeCache!=null ) mergeCache.put(a, b, a_);
			return a_;
		}
	}

	// http://www.antlr.org/wiki/download/attachments/32014352/local-ctx-root-merge.png
	// http://www.antlr.org/wiki/download/attachments/32014352/full-ctx-root-merge.png
	/** Handle case where at least one of a or b is $ (EMPTY) */
	public static PredictionContext mergeRoot(SingletonPredictionContext a,
											  SingletonPredictionContext b,
											  boolean rootIsWildcard)
	{
		if ( rootIsWildcard ) {
			if ( a == EMPTY ) return EMPTY;  // * + b = *
			if ( b == EMPTY ) return EMPTY;  // a + * = *
		}
		else {
			if ( a == EMPTY && b == EMPTY ) return EMPTY; // $ + $ = $
			if ( a == EMPTY ) { // $ + x = [$,x]
				int[] payloads = {b.returnState, EMPTY_RETURN_STATE};
				PredictionContext[] parents = {b.parent, null};
				PredictionContext joined =
					new ArrayPredictionContext(parents, payloads);
				return joined;
			}
			if ( b == EMPTY ) { // x + $ = [$,x] ($ is always first if present)
				int[] payloads = {a.returnState, EMPTY_RETURN_STATE};
				PredictionContext[] parents = {a.parent, null};
				PredictionContext joined =
					new ArrayPredictionContext(parents, payloads);
				return joined;
			}
		}
		return null;
	}

	// http://www.antlr.org/wiki/download/attachments/32014352/array-merge.png
	public static PredictionContext mergeArrays(
		ArrayPredictionContext a,
		ArrayPredictionContext b,
		boolean rootIsWildcard,
		DoubleKeyMap<PredictionContext,PredictionContext,PredictionContext> mergeCache)
	{
		if ( mergeCache!=null ) {
			PredictionContext previous = mergeCache.get(a,b);
			if ( previous!=null ) return previous;
			previous = mergeCache.get(b,a);
			if ( previous!=null ) return previous;
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
				boolean both$ = payload == EMPTY_RETURN_STATE &&
								a_parent == null && b_parent == null;
				boolean ax_ax = (a_parent!=null && b_parent!=null) &&
								a_parent.equals(b_parent); // ax+ax -> ax
				if ( both$ || ax_ax ) {
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
				if ( mergeCache!=null ) mergeCache.put(a,b,a_);
				return a_;
			}
			mergedParents = Arrays.copyOf(mergedParents, k);
			mergedReturnStates = Arrays.copyOf(mergedReturnStates, k);
		}

		PredictionContext M =
			new ArrayPredictionContext(mergedParents, mergedReturnStates);

		// if we created same array as a or b, return that instead
		// TODO: track whether this is possible above during merge sort for speed
		if ( M.equals(a) ) {
			if ( mergeCache!=null ) mergeCache.put(a,b,a);
			return a;
		}
		if ( M.equals(b) ) {
			if ( mergeCache!=null ) mergeCache.put(a,b,b);
			return b;
		}

		combineCommonParents(mergedParents);

		if ( mergeCache!=null ) mergeCache.put(a,b,M);
		return M;
	}

	/** make pass over all M parents; merge any equals() ones */
	protected static void combineCommonParents(PredictionContext[] parents) {
		Map<PredictionContext, PredictionContext> uniqueParents =
			new HashMap<PredictionContext, PredictionContext>();

		for (int p = 0; p < parents.length; p++) {
			PredictionContext parent = parents[p];
			if ( !uniqueParents.containsKey(parent) ) { // don't replace
				uniqueParents.put(parent, parent);
			}
		}

		for (int p = 0; p < parents.length; p++) {
			parents[p] = uniqueParents.get(parents[p]);
		}
	}

	public static String toDOTString(PredictionContext context) {
		if ( context==null ) return "";
		StringBuilder buf = new StringBuilder();
		buf.append("digraph G {\n");
		buf.append("rankdir=LR;\n");

		List<PredictionContext> nodes = getAllContextNodes(context);
		Collections.sort(nodes);

		for (PredictionContext current : nodes) {
			if ( current instanceof SingletonPredictionContext ) {
				String s = String.valueOf(current.id);
				buf.append("  s").append(s);
				String returnState = String.valueOf(current.getReturnState(0));
				if ( current instanceof EmptyPredictionContext ) returnState = "$";
				buf.append(" [label=\"").append(returnState).append("\"];\n");
				continue;
			}
			ArrayPredictionContext arr = (ArrayPredictionContext)current;
			buf.append("  s").append(arr.id);
			buf.append(" [shape=box, label=\"");
			buf.append("[");
			boolean first = true;
			for (int inv : arr.returnStates) {
				if ( !first ) buf.append(", ");
				if ( inv == EMPTY_RETURN_STATE ) buf.append("$");
				else buf.append(inv);
				first = false;
			}
			buf.append("]");
			buf.append("\"];\n");
		}

		for (PredictionContext current : nodes) {
			if ( current==EMPTY ) continue;
			for (int i = 0; i < current.size(); i++) {
				if ( current.getParent(i)==null ) continue;
				String s = String.valueOf(current.id);
				buf.append("  s").append(s);
				buf.append("->");
				buf.append("s");
				buf.append(current.getParent(i).id);
				if ( current.size()>1 ) buf.append(" [label=\"parent["+i+"]\"];\n");
				else buf.append(";\n");
			}
		}

		buf.append("}\n");
		return buf.toString();
	}

	// From Sam
	public static PredictionContext getCachedContext(
		@NotNull PredictionContext context,
		@NotNull PredictionContextCache contextCache,
		@NotNull IdentityHashMap<PredictionContext, PredictionContext> visited)
	{
		if (context.isEmpty()) {
			return context;
		}

		PredictionContext existing = visited.get(context);
		if (existing != null) {
			return existing;
		}

		synchronized (contextCache) {
			existing = contextCache.get(context);
			if (existing != null) {
				visited.put(context, existing);
				return existing;
			}
		}

		boolean changed = false;
		PredictionContext[] parents = new PredictionContext[context.size()];
		for (int i = 0; i < parents.length; i++) {
			PredictionContext parent = getCachedContext(context.getParent(i), contextCache, visited);
			if (changed || parent != context.getParent(i)) {
				if (!changed) {
					parents = new PredictionContext[context.size()];
					for (int j = 0; j < context.size(); j++) {
						parents[j] = context.getParent(j);
					}

					changed = true;
				}

				parents[i] = parent;
			}
		}

		if (!changed) {
			synchronized (contextCache) {
				contextCache.add(context);
			}
			visited.put(context, context);
			return context;
		}

		PredictionContext updated;
		if (parents.length == 0) {
			updated = EMPTY;
		}
		else if (parents.length == 1) {
			updated = SingletonPredictionContext.create(parents[0], context.getReturnState(0));
		}
		else {
			ArrayPredictionContext arrayPredictionContext = (ArrayPredictionContext)context;
			updated = new ArrayPredictionContext(parents, arrayPredictionContext.returnStates);
		}

		synchronized (contextCache) {
			contextCache.add(updated);
		}
		visited.put(updated, updated);
		visited.put(context, updated);

		return updated;
	}

//	// extra structures, but cut/paste/morphed works, so leave it.
//	// seems to do a breadth-first walk
//	public static List<PredictionContext> getAllNodes(PredictionContext context) {
//		Map<PredictionContext, PredictionContext> visited =
//			new IdentityHashMap<PredictionContext, PredictionContext>();
//		Deque<PredictionContext> workList = new ArrayDeque<PredictionContext>();
//		workList.add(context);
//		visited.put(context, context);
//		List<PredictionContext> nodes = new ArrayList<PredictionContext>();
//		while (!workList.isEmpty()) {
//			PredictionContext current = workList.pop();
//			nodes.add(current);
//			for (int i = 0; i < current.size(); i++) {
//				PredictionContext parent = current.getParent(i);
//				if ( parent!=null && visited.put(parent, parent) == null) {
//					workList.push(parent);
//				}
//			}
//		}
//		return nodes;
//	}

	// ter's recursive version of Sam's getAllNodes()
	public static List<PredictionContext> getAllContextNodes(PredictionContext context) {
		List<PredictionContext> nodes = new ArrayList<PredictionContext>();
		Map<PredictionContext, PredictionContext> visited =
			new IdentityHashMap<PredictionContext, PredictionContext>();
		getAllContextNodes_(context, nodes, visited);
		return nodes;
	}

	public static void getAllContextNodes_(PredictionContext context,
										   List<PredictionContext> nodes,
										   Map<PredictionContext, PredictionContext> visited)
	{
		if ( context==null || visited.containsKey(context) ) return;
		visited.put(context, context);
		nodes.add(context);
		for (int i = 0; i < context.size(); i++) {
			getAllContextNodes_(context.getParent(i), nodes, visited);
		}
	}

	public String toString(@Nullable Recognizer<?,?> recog) {
		return toString();
//		return toString(recog, ParserRuleContext.EMPTY);
	}

	// recog null unless ParserRuleContext, in which case we use subclass toString(...)
	public String toString(@Nullable Recognizer<?,?> recog, RuleContext stop) {
		StringBuilder buf = new StringBuilder();
		PredictionContext p = this;
		buf.append("[");
//		while ( p != null && p != stop ) {
//			if ( !p.isEmpty() ) buf.append(p.returnState);
//			if ( p.parent != null && !p.parent.isEmpty() ) buf.append(" ");
//			p = p.parent;
//		}
		buf.append("]");
		return buf.toString();
	}

	public String[] toStrings(Recognizer<?, ?> recognizer, int currentState) {
		return toStrings(recognizer, EMPTY, currentState);
	}

	// FROM SAM
	public String[] toStrings(Recognizer<?, ?> recognizer, PredictionContext stop, int currentState) {
		List<String> result = new ArrayList<String>();

		outer:
		for (int perm = 0; ; perm++) {
			int offset = 0;
			boolean last = true;
			PredictionContext p = this;
			int stateNumber = currentState;
			StringBuilder localBuffer = new StringBuilder();
			localBuffer.append("[");
			while ( !p.isEmpty() && p != stop ) {
				int index = 0;
				if (p.size() > 0) {
					int bits = 1;
					while ((1 << bits) < p.size()) {
						bits++;
					}

					int mask = (1 << bits) - 1;
					index = (perm >> offset) & mask;
					last &= index >= p.size() - 1;
					if (index >= p.size()) {
						continue outer;
					}
					offset += bits;
				}

				if ( recognizer!=null ) {
					if (localBuffer.length() > 1) {
						// first char is '[', if more than that this isn't the first rule
						localBuffer.append(' ');
					}

					ATN atn = recognizer.getATN();
					ATNState s = atn.states.get(stateNumber);
					String ruleName = recognizer.getRuleNames()[s.ruleIndex];
					localBuffer.append(ruleName);
				}
				else if ( p.getReturnState(index)!= EMPTY_RETURN_STATE) {
					if ( !p.isEmpty() ) {
						if (localBuffer.length() > 1) {
							// first char is '[', if more than that this isn't the first rule
							localBuffer.append(' ');
						}

						localBuffer.append(p.getReturnState(index));
					}
				}
				stateNumber = p.getReturnState(index);
				p = p.getParent(index);
			}
			localBuffer.append("]");
			result.add(localBuffer.toString());

			if (last) {
				break;
			}
		}

		return result.toArray(new String[result.size()]);
	}
}
