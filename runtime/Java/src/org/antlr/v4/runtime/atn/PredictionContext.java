package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
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

	@Override
	public String toString() {
		return toString(null);
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

	// dispatch
	public static PredictionContext merge(PredictionContext a, PredictionContext b,
										  boolean rootIsWildcard)
	{
		if ( a.equals(b) ) return a; // share same graph if both same

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
		return mergeArrays((ArrayPredictionContext)a, (ArrayPredictionContext)b,
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
			// if parent is same as existing a or b parent, return it
			if ( parent == a.parent ) return a; // ax + ax = ax
			if ( parent == b.parent ) return b; // not sure can happen since merge(a,a) returns left a
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
				ArrayPredictionContext joined =
					new ArrayPredictionContext(parents, payloads);
				return joined;
			}
			// parents differ and can't merge them. Just pack together
			// into array; can't merge. sort, though, by payload
			// ax + by = [ax,by]
			int[] payloads = {a.invokingState, b.invokingState};
			PredictionContext[] parents = {a.parent, b.parent};
			if ( a.invokingState > b.invokingState ) {
				payloads = new int[] {b.invokingState, a.invokingState};
				parents = new PredictionContext[] {b.parent, a.parent};
			}
			ArrayPredictionContext joined =
				new ArrayPredictionContext(parents, payloads);
			return joined;
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
		int k = 0; // walks M target array

		int[] mergedInvokingStates =
			new int[a.invokingStates.length + b.invokingStates.length];
		PredictionContext[] mergedParents =
			new PredictionContext[a.invokingStates.length + b.invokingStates.length];
		while ( i<a.invokingStates.length && j<b.invokingStates.length ) {
			if ( a.invokingStates[i]==b.invokingStates[j] ) {
				// same payload; stack tops are equal
				int payload = a.invokingStates[i];
				SingletonPredictionContext a_ = new SingletonPredictionContext(a.parents[i], payload);
				SingletonPredictionContext b_ = new SingletonPredictionContext(b.parents[j], payload);
				// if same stack tops, must yield merged singleton
				SingletonPredictionContext r =
					(SingletonPredictionContext)mergeSingletons(a_, b_, rootIsWildcard);
				// if r is same as a_ or b_, we get to keep existing, else new
				if ( r==a_ ) {
					mergedParents[k] = a.parents[i];
					mergedInvokingStates[k] = a.invokingStates[i];
				}
				else if ( r==b_ ) {
					mergedParents[k] = b.parents[j];
					mergedInvokingStates[k] = b.invokingStates[j];
				}
				else {
					mergedParents[k] = r.parent;
					mergedInvokingStates[k] = r.invokingState;
				}
				i++; // hop over left one as usual
				j++; // but also skip one in right side since we merge
			}
			else if ( a.invokingStates[i]<b.invokingStates[j] ) {
				mergedParents[k] = a.parents[i];
				mergedInvokingStates[k] = a.invokingStates[i];
				i++;
			}
			else { // b > a
				mergedParents[k] = b.parents[j];
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

		// trim merged
		if ( k < mergedParents.length ) { // write index < last position; trim
			int p = mergedParents.length-1;
			while ( p>=0 && mergedParents[p]==null ) { p--; }
			// p is now last non-null index
			if ( p < mergedParents.length-1 ) {
				int n = p+1;
				mergedParents = Arrays.copyOf(mergedParents, n);
				mergedInvokingStates = Arrays.copyOf(mergedInvokingStates, n);
			}
		}

		ArrayPredictionContext M =
			new ArrayPredictionContext(mergedParents, mergedInvokingStates);

		// TODO: if we created same array as a or b, return that instead

		// TODO: make pass over all M parents; merge any equal() ones

		return M;
	}

	public static String toDotString(PredictionContext context) {
		if ( context==null ) return "";
		StringBuilder buf = new StringBuilder();
		buf.append("digraph G {\n");
		buf.append("rankdir=LR;\n");

		List<PredictionContext> nodes = getAllNodes(context);

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
			buf.append(Arrays.toString(arr.invokingStates));
			buf.append("\"];\n");
		}

		for (PredictionContext current : nodes) {
			if ( current==EMPTY ) continue;
			for (int i = 0; i < current.size(); i++) {
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

	// extra structures, but cut/paste/morphed works, so leave it.
	// seems to do a breadth-first walk
	public static List<PredictionContext> getAllNodes(PredictionContext context) {
		Map<PredictionContext, PredictionContext> visited =
			new IdentityHashMap<PredictionContext, PredictionContext>();
		Deque<PredictionContext> workList = new ArrayDeque<PredictionContext>();
		workList.add(context);
		visited.put(context, context);
		List<PredictionContext> nodes = new ArrayList<PredictionContext>();
		while (!workList.isEmpty()) {
			PredictionContext current = workList.pop();
			nodes.add(current);
			for (int i = 0; i < current.size(); i++) {
				PredictionContext parent = current.getParent(i);
				if ( parent!=null && visited.put(parent, parent) == null) {
					workList.push(parent);
				}
			}
		}
		return nodes;
	}
}
