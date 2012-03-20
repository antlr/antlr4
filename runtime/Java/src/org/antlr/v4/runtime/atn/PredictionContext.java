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
	public static final EmptyPredictionContext EMPTY = new EmptyPredictionContext();
	public static final int EMPTY_FULL_INVOKING_STATE = Integer.MAX_VALUE;

	public static int globalNodeCount = 0;
	public final int id = globalNodeCount++;

	public final int cachedHashCode;

	protected PredictionContext(int cachedHashCode) {
		this.cachedHashCode = cachedHashCode;
	}

//	public PredictionContext() {
//		id = globalNodeCount++;
//	}

	public static PredictionContext fromRuleContext(RuleContext outerContext) {
		if ( outerContext==null ) outerContext = RuleContext.EMPTY;
		if ( outerContext==RuleContext.EMPTY ) {
			return PredictionContext.EMPTY;
		}

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

	public abstract int findInvokingState(int invokingState);

	/** This means just the EMPTY context */
	public boolean isEmpty() {
		return this == EMPTY;
	}

	protected static int calculateParentHashCode(PredictionContext[] parents) {
		int hashCode = 1;
		for (PredictionContext context : parents) {
			hashCode = hashCode * 31 ^ context.hashCode();
		}

		return hashCode;
	}

	protected static int calculateInvokingStatesHashCode(int[] invokingStates) {
		int hashCode = 1;
		for (int state : invokingStates) {
			hashCode = hashCode * 31 ^ state;
		}

		return hashCode;
	}

	protected static int calculateHashCode(PredictionContext[] parents, int[] invokingStates) {
		return calculateHashCode(calculateParentHashCode(parents),
								 calculateInvokingStatesHashCode(invokingStates));
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
				else if ( p.getInvokingState(index)!=EMPTY_FULL_INVOKING_STATE ) {
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
	public static PredictionContext merge(PredictionContext a, PredictionContext b, boolean rootIsWildcard) {
		if ( a instanceof SingletonPredictionContext && b instanceof SingletonPredictionContext) {
			return mergeSingletons((SingletonPredictionContext)a, (SingletonPredictionContext)b, rootIsWildcard);
		}
		if ( a instanceof SingletonPredictionContext) {
			a = new ArrayPredictionContext((SingletonPredictionContext)a);
		}
		if ( b instanceof SingletonPredictionContext) {
			b = new ArrayPredictionContext((SingletonPredictionContext)b);
		}
		return mergeArrays((ArrayPredictionContext)a, (ArrayPredictionContext)b, rootIsWildcard);
	}

	public static PredictionContext mergeSingletons(SingletonPredictionContext a, SingletonPredictionContext b,
											boolean rootIsWildcard)
	{
		if ( rootIsWildcard ) {
			if ( a == EMPTY ) return a;
			if ( b == EMPTY ) return b;
		}
		if ( a.invokingState==b.invokingState ) { // a == b
			PredictionContext parent = merge(a.parent, b.parent, rootIsWildcard);
			if ( parent == a.parent ) return a;
			if ( parent == b.parent ) return b;
			// new joined parent so create new singleton pointing to it
			return new SingletonPredictionContext(parent, a.invokingState);
		}
		else { // a != b payloads differ
			// parents differ, join them; nothing to reuse
			// sort payloads
			int[] payloads = {a.invokingState, b.invokingState};
			if ( a.invokingState > b.invokingState ) {
				payloads = new int[] {b.invokingState, a.invokingState};
			}
			if ( a.parent.equals(b.parent) ) {
				// parents are equal, pick left one as parent to reuse
				PredictionContext parent = a.parent;
				ArrayPredictionContext joined =
					new ArrayPredictionContext(new PredictionContext[]{parent, parent},
								  payloads);
				return joined;
			}
			// parents differ, just pack together into array; can't merge.
			ArrayPredictionContext joined =
				new ArrayPredictionContext(new PredictionContext[]{a.parent, b.parent},
							  payloads);
			return joined;
		}
	}

	public static PredictionContext mergeArrays(ArrayPredictionContext a, ArrayPredictionContext b,
										boolean rootIsWildcard)
	{
		// merge sorted payloads a + b => M
		int i = 0; // walks a
		int j = 0; // walks b
		int k = 0; // walks M target array
		int[] mergedPayloads = new int[a.invokingStates.length + b.invokingStates.length];
		PredictionContext[] mergedParents = new PredictionContext[a.invokingStates.length + b.invokingStates.length];
		ArrayPredictionContext M = new ArrayPredictionContext(mergedParents, mergedPayloads);
		while ( i<a.invokingStates.length && j<b.invokingStates.length ) {
			if ( a.invokingStates[i]==b.invokingStates[j] ) {
				// same payload; stack tops are equal
				int payload = a.invokingStates[i];
				SingletonPredictionContext a_ = new SingletonPredictionContext(a.parents[i], payload);
				SingletonPredictionContext b_ = new SingletonPredictionContext(b.parents[j], payload);
				// if same stack tops, must yield merged singleton
				SingletonPredictionContext r = (SingletonPredictionContext)mergeSingletons(a_, b_, rootIsWildcard);
				// if r is same as a_ or b_, we get to keep existing, else new
				if ( r==a_ ) {
					M.parents[k] = a.parents[i];
					M.invokingStates[k] = a.invokingStates[i];
				}
				else if ( r==b_ ) {
					M.parents[k] = b.parents[j];
					M.invokingStates[k] = b.invokingStates[j];
				}
				else {
					M.parents[k] = r.parent;
					M.invokingStates[k] = r.invokingState;
				}
				i++; // hop over left one as usual
				j++; // but also skip one in right side since we merge
			}
			else if ( a.invokingStates[i]<b.invokingStates[j] ) {
				M.parents[k] = a.parents[i];
				M.invokingStates[k] = a.invokingStates[i];
				i++;
			}
			else {
				M.parents[k] = a.parents[j];
				M.invokingStates[k] = b.invokingStates[j];
				j++;
			}
			k++;
		}
		// copy over any payloads remaining in either array
		if (i < a.invokingStates.length) {
			for (int p = i; p < a.invokingStates.length; p++) {
				M.parents[k] = a.parents[p];
				M.invokingStates[k] = a.invokingStates[p];
				k++;
			}
		}
		else {
			for (int p = j; p < b.invokingStates.length; p++) {
				M.parents[k] = b.parents[p];
				M.invokingStates[k] = b.invokingStates[p];
				k++;
			}
		}
		// trim merged
		if ( k < M.size() ) { // write index < last position; trim
			M = M.trim();
		}

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
				buf.append(" [label=\"").append(current.getInvokingState(0)).append("\"];\n");
				continue;
			}
			ArrayPredictionContext arr = (ArrayPredictionContext)current;
			buf.append("  s").append(arr.id);
			buf.append(" [label=\"");
			buf.append(Arrays.toString(arr.invokingStates));
			buf.append("\"];\n");
		}

		for (PredictionContext current : nodes) {
			if ( current==EMPTY ) continue;
			for (int i = 0; i < current.size(); i++) {
//				String s = String.valueOf(System.identityHashCode(current));
				String s = String.valueOf(current.id);
				buf.append("  s").append(s);
				buf.append("->");
				buf.append("s");
				buf.append(current.getParent(i).id);
				buf.append(";\n");
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
