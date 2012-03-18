import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public abstract class PredictionContext {
	public static final EmptyPredictionContext EMPTY = new EmptyPredictionContext();

	public static int globalNodeCount = 0;
	public final int id;

	public PredictionContext() {
		id = globalNodeCount++;
	}

	public abstract int size();

	public abstract PredictionContext getParent(int index);

	public abstract String getPayload(int index);

	public abstract int findPayload(String payload);

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
		if ( a.payload.equals(b.payload) ) { // a == b
			PredictionContext parent = merge(a.parent, b.parent, rootIsWildcard);
			if ( parent == a.parent ) return a;
			if ( parent == b.parent ) return b;
			// new joined parent so create new singleton pointing to it
			return new SingletonPredictionContext(parent, a.payload);
		}
		else { // a != b payloads differ
			// parents differ, join them; nothing to reuse
			// sort payloads
			String[] payloads = {a.payload, b.payload};
			if ( a.payload.compareTo(b.payload) > 0 ) {
				payloads = new String[] {b.payload, a.payload};
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
		String[] mergedPayloads = new String[a.payloads.length + b.payloads.length];
		PredictionContext[] mergedParents = new PredictionContext[a.payloads.length + b.payloads.length];
		ArrayPredictionContext M = new ArrayPredictionContext(mergedParents, mergedPayloads);
		while ( i<a.payloads.length && j<b.payloads.length ) {
			if ( a.payloads[i].equals(b.payloads[j]) ) {
				// same payload; stack tops are equal
				String payload = a.payloads[i];
				SingletonPredictionContext a_ = new SingletonPredictionContext(a.parents[i], payload);
				SingletonPredictionContext b_ = new SingletonPredictionContext(b.parents[j], payload);
				// if same stack tops, must yield merged singleton
				SingletonPredictionContext r = (SingletonPredictionContext)mergeSingletons(a_, b_, rootIsWildcard);
				// if r is same as a_ or b_, we get to keep existing, else new
				if ( r==a_ ) {
					M.parents[k] = a.parents[i];
					M.payloads[k] = a.payloads[i];
				}
				else if ( r==b_ ) {
					M.parents[k] = b.parents[j];
					M.payloads[k] = b.payloads[j];
				}
				else {
					M.parents[k] = r.parent;
					M.payloads[k] = r.payload;
				}
				i++; // hop over left one as usual
				j++; // but also skip one in right side since we merge
			}
			else if ( a.payloads[i].compareTo(b.payloads[j]) < 0 ) {
				M.parents[k] = a.parents[i];
				M.payloads[k] = a.payloads[i];
				i++;
			}
			else {
				M.parents[k] = a.parents[j];
				M.payloads[k] = b.payloads[j];
				j++;
			}
			k++;
		}
		// copy over any payloads remaining in either array
		if (i < a.payloads.length) {
			for (int p = i; p < a.payloads.length; p++) {
				M.parents[k] = a.parents[p];
				M.payloads[k] = a.payloads[p];
				k++;
			}
		}
		else {
			for (int p = j; p < b.payloads.length; p++) {
				M.parents[k] = b.parents[p];
				M.payloads[k] = b.payloads[p];
				k++;
			}
		}
		// trim merged
		if ( k < M.size() ) { // write index < last position; trim
			M = M.trim();
		}
		// if we created same array as a or b, return that instead
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
				buf.append(" [label=\"").append(current.getPayload(0)).append("\"];\n");
				continue;
			}
			ArrayPredictionContext arr = (ArrayPredictionContext)current;
			buf.append("  s").append(arr.id);
			buf.append(" [label=\"");
			buf.append(Arrays.toString(arr.payloads));
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
