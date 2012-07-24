package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class PredictionContext implements Iterable<SingletonPredictionContext> {
	/** Represents $ in local ctx prediction, which means wildcard. *+x = *. */
	public static final EmptyPredictionContext EMPTY = new EmptyPredictionContext();

	/** Represents $ in an array in full ctx mode, when $ doesn't mean wildcard:
	 *  $ + x = [$,x]. Here, $ = EMPTY_FULL_CTX_INVOKING_STATE.
	 */
	public static final int EMPTY_FULL_CTX_INVOKING_STATE = Integer.MAX_VALUE;

	public static int globalNodeCount = 0;
	public final int id = globalNodeCount++;

	public final int cachedHashCode;

	protected PredictionContext(int cachedHashCode) {
		this.cachedHashCode = cachedHashCode;
	}

	/** Convert a RuleContext tree to a PredictionContext graph.
	 *  Return EMPTY if outerContext is empty or null.
	 */
	public static PredictionContext fromRuleContext(RuleContext outerContext) {
		if ( outerContext==null ) outerContext = RuleContext.EMPTY;

		// if we are in RuleContext of start rule, s, then PredictionContext
		// is EMPTY. Nobody called us. (if we are empty, return empty)
		if ( outerContext.parent==null || outerContext==RuleContext.EMPTY ) {
			return PredictionContext.EMPTY;
		}

		// If we have a parent, convert it to a PredictionContext graph
		PredictionContext parent = EMPTY;
		if ( outerContext.parent != null ) {
			parent = PredictionContext.fromRuleContext(outerContext.parent);
		}

		return new SingletonPredictionContext(parent, outerContext.invokingState);
	}

	@Override
	public abstract Iterator<SingletonPredictionContext> iterator();

	public abstract int size();

	public abstract PredictionContext getParent(int index);

	public abstract int getInvokingState(int index);

//	public abstract int findInvokingState(int invokingState);

	/** This means only the EMPTY context is in set */
	public boolean isEmpty() {
		return this == EMPTY;
	}

	public abstract PredictionContext popAll(int invokingState, boolean fullCtx);

	@Override
	public int hashCode() {
		return cachedHashCode;
	}

	protected static int calculateHashCode(int parentHashCode, int invokingStateHashCode) {
		return 5 * 5 * 7 + 5 * parentHashCode + invokingStateHashCode;
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
	public static PredictionContext merge(PredictionContext a, PredictionContext b,
										  boolean rootIsWildcard)
	{
		if ( (a==null&&b==null) || a.equals(b) ) return a; // share same graph if both same

		if ( a instanceof SingletonPredictionContext && b instanceof SingletonPredictionContext) {
			return mergeSingletons((SingletonPredictionContext)a,
								   (SingletonPredictionContext)b,
								   rootIsWildcard);
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
						   rootIsWildcard);
	}

	// http://www.antlr.org/wiki/download/attachments/32014352/singleton-merge.png
	public static PredictionContext mergeSingletons(SingletonPredictionContext a,
													SingletonPredictionContext b,
													boolean rootIsWildcard)
	{
		PredictionContext rootMerge = mergeRoot(a, b, rootIsWildcard);
		if ( rootMerge!=null ) return rootMerge;

		if ( a.invokingState==b.invokingState ) { // a == b
			PredictionContext parent = merge(a.parent, b.parent, rootIsWildcard);
			// if parent is same as existing a or b parent or reduced to a parent, return it
			if ( parent == a.parent ) return a; // ax + bx = ax, if a=b
			if ( parent == b.parent ) return b; // ax + bx = bx, if a=b
			// else: ax + ay = a'[x,y]
			// merge parents x and y, giving array node with x,y then remainders
			// of those graphs.  dup a, a' points at merged array
			// new joined parent so create new singleton pointing to it, a'
			return new SingletonPredictionContext(parent, a.invokingState);
		}
		else { // a != b payloads differ
			// see if we can collapse parents due to $+x parents if local ctx
			PredictionContext singleParent = null;
			if ( rootIsWildcard ) {
				if ( a.parent == EMPTY ) singleParent = EMPTY;  // $ + b = $
				if ( b.parent == EMPTY ) singleParent = EMPTY;  // a + $ = $
			}
			if ( a.parent.equals(b.parent) ) { // ax + bx = [a,b]x
				singleParent = a.parent;
			}
			if ( singleParent!=null ) {	// parents are same
				// sort payloads and use same parent
				int[] payloads = {a.invokingState, b.invokingState};
				if ( a.invokingState > b.invokingState ) {
					payloads[0] = b.invokingState;
					payloads[1] = a.invokingState;
				}
				PredictionContext[] parents = {singleParent, singleParent};
				return new ArrayPredictionContext(parents, payloads);
			}
			// parents differ and can't merge them. Just pack together
			// into array; can't merge.
			// ax + by = [ax,by]
			int[] payloads = {a.invokingState, b.invokingState};
			PredictionContext[] parents = {a.parent, b.parent};
			if ( a.invokingState > b.invokingState ) { // sort by payload
				payloads[0] = b.invokingState;
				payloads[1] = a.invokingState;
				parents = new PredictionContext[] {b.parent, a.parent};
			}
			return new ArrayPredictionContext(parents, payloads);
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
				int[] payloads = {EMPTY_FULL_CTX_INVOKING_STATE, b.invokingState};
				PredictionContext[] parents = {null, b.parent};
				ArrayPredictionContext joined =
					new ArrayPredictionContext(parents, payloads);
				return joined;
			}
			if ( b == EMPTY ) { // x + $ = [$,x] ($ is always first if present)
				int[] payloads = {EMPTY_FULL_CTX_INVOKING_STATE, a.invokingState};
				PredictionContext[] parents = {null, a.parent};
				ArrayPredictionContext joined =
					new ArrayPredictionContext(parents, payloads);
				return joined;
			}
		}
		return null;
	}

	// http://www.antlr.org/wiki/download/attachments/32014352/array-merge.png
	public static PredictionContext mergeArrays(ArrayPredictionContext a,
												ArrayPredictionContext b,
												boolean rootIsWildcard)
	{
		// merge sorted payloads a + b => M
		int i = 0; // walks a
		int j = 0; // walks b
		int k = 0; // walks target M array

		int[] mergedInvokingStates =
			new int[a.invokingStates.length + b.invokingStates.length];
		PredictionContext[] mergedParents =
			new PredictionContext[a.invokingStates.length + b.invokingStates.length];
		// walk and merge to yield mergedParents, mergedInvokingStates
		while ( i<a.invokingStates.length && j<b.invokingStates.length ) {
			PredictionContext a_parent = a.parents[i];
			PredictionContext b_parent = b.parents[j];
			if ( a.invokingStates[i]==b.invokingStates[j] ) {
				// same payload (stack tops are equal), must yield merged singleton
				int payload = a.invokingStates[i];
				// $+$ = $
				boolean both$ = payload == EMPTY_FULL_CTX_INVOKING_STATE &&
								a_parent == null && b_parent == null;
				boolean ax_ax = (a_parent!=null && b_parent!=null) &&
								a_parent.equals(b_parent); // ax+ax -> ax
				if ( both$ || ax_ax ) {
					mergedParents[k] = a_parent; // choose left
					mergedInvokingStates[k] = payload;
				}
				else { // ax+ay -> a'[x,y]
					PredictionContext mergedParent = merge(a_parent, b_parent, rootIsWildcard);
					mergedParents[k] = mergedParent;
					mergedInvokingStates[k] = payload;
				}
				i++; // hop over left one as usual
				j++; // but also skip one in right side since we merge
			}
			else if ( a.invokingStates[i]<b.invokingStates[j] ) { // copy a[i] to M
				mergedParents[k] = a_parent;
				mergedInvokingStates[k] = a.invokingStates[i];
				i++;
			}
			else { // b > a, copy b[j] to M
				mergedParents[k] = b_parent;
				mergedInvokingStates[k] = b.invokingStates[j];
				j++;
			}
			k++;
		}

		// copy over any payloads remaining in either array
		if (i < a.invokingStates.length) {
			for (int p = i; p < a.invokingStates.length; p++) {
				mergedParents[k] = a.parents[p];
				mergedInvokingStates[k] = a.invokingStates[p];
				k++;
			}
		}
		else {
			for (int p = j; p < b.invokingStates.length; p++) {
				mergedParents[k] = b.parents[p];
				mergedInvokingStates[k] = b.invokingStates[p];
				k++;
			}
		}

		// trim merged if we combined a few that had same stack tops
		if ( k < mergedParents.length ) { // write index < last position; trim
			int lastSlot = mergedParents.length - 1;
			int p = lastSlot; // walk backwards from last index until we find non-null parent
			while ( p>=0 && mergedParents[p]==null ) { p--; }
			// p is now last non-null index
			assert p>0; // could only happen to be <0 if two arrays with $
			if ( p < lastSlot ) {
				int n = p+1; // how many slots we really used in merge
				if ( n == 1 ) { // for just one merged element, return singleton top
					return new SingletonPredictionContext(mergedParents[0],
														  mergedInvokingStates[0]);
				}
				mergedParents = Arrays.copyOf(mergedParents, n);
				mergedInvokingStates = Arrays.copyOf(mergedInvokingStates, n);
			}
		}

		ArrayPredictionContext M =
			new ArrayPredictionContext(mergedParents, mergedInvokingStates);

		// if we created same array as a or b, return that instead
		// TODO: track whether this is possible above during merge sort for speed
		if ( M.equals(a) ) return a;
		if ( M.equals(b) ) return b;

		combineCommonParents(mergedParents);

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

	public static String toDotString(PredictionContext context) {
		if ( context==null ) return "";
		StringBuilder buf = new StringBuilder();
		buf.append("digraph G {\n");
		buf.append("rankdir=LR;\n");

		List<PredictionContext> nodes = getAllContextNodes(context);

		for (PredictionContext current : nodes) {
			if ( current instanceof SingletonPredictionContext ) {
				String s = String.valueOf(current.id);
				buf.append("  s").append(s);
				String invokingState = String.valueOf(current.getInvokingState(0));
				if ( current instanceof EmptyPredictionContext ) invokingState = "$";
				buf.append(" [label=\"").append(invokingState).append("\"];\n");
				continue;
			}
			ArrayPredictionContext arr = (ArrayPredictionContext)current;
			buf.append("  s").append(arr.id);
			buf.append(" [shape=box, label=\"");
			buf.append("[");
			boolean first = true;
			for (int inv : arr.invokingStates) {
				if ( !first ) buf.append(", ");
				if ( inv == EMPTY_FULL_CTX_INVOKING_STATE ) buf.append("$");
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

		existing = contextCache.get(context);
		if (existing != null) {
			visited.put(context, existing);
			return existing;
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
			contextCache.add(context);
			visited.put(context, context);
			return context;
		}

		PredictionContext updated;
		if (parents.length == 0) {
			updated = EMPTY;
		}
		else if (parents.length == 1) {
			updated = new SingletonPredictionContext(parents[0], context.getInvokingState(0));
		}
		else {
			ArrayPredictionContext arrayPredictionContext = (ArrayPredictionContext)context;
			updated = new ArrayPredictionContext(parents, arrayPredictionContext.invokingStates);
		}

		contextCache.add(updated);
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
//			if ( !p.isEmpty() ) buf.append(p.invokingState);
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
				else if ( p.getInvokingState(index)!= EMPTY_FULL_CTX_INVOKING_STATE) {
					if ( !p.isEmpty() ) {
						if (localBuffer.length() > 1) {
							// first char is '[', if more than that this isn't the first rule
							localBuffer.append(' ');
						}

						localBuffer.append(p.getInvokingState(index));
					}
				}
				stateNumber = p.getInvokingState(index);
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
