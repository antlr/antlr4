public abstract class GraphNode {
	public static final SingletonNode EMPTY = new SingletonNode(null,"$");

	public static int globalNodeCount = 0;
	public final int id;

	public GraphNode() {
		id = globalNodeCount++;
	}

	public static GraphNode merge(GraphNode a, GraphNode b) { // dispatch
		if ( a instanceof SingletonNode && b instanceof SingletonNode ) {
			return mergeSingletons((SingletonNode) a, (SingletonNode) b);
		}
		if ( a instanceof SingletonNode ) {
			a = new ArrayNode((SingletonNode)a);
		}
		if ( b instanceof SingletonNode ) {
			b = new ArrayNode((SingletonNode)b);
		}
		return mergeArrays((ArrayNode) a, (ArrayNode) b);
	}

	public static GraphNode mergeSingletons(SingletonNode a, SingletonNode b) {
		if ( a == EMPTY ) return a;
		if ( b == EMPTY ) return b;
		if ( a.payload.equals(b.payload) ) {
			GraphNode parent = merge(a.parent, b.parent);
			if ( parent == a.parent ) return a;
			if ( parent == b.parent ) return b;
			// new joined parent so create new singleton pointing to it
			return new SingletonNode(parent, a.payload);
		}
		else { // a != b payloads differ
			// parents differ, join them; nothing to reuse
			if ( !a.parent.equals(b.parent) ) {
				ArrayNode joined =
				new ArrayNode(new GraphNode[]{a.parent,  b.parent},
							  new String[]   {a.payload, b.payload});
				return joined;
			}
			// parents are equal, pick left one as parent to reuse
			GraphNode parent = a.parent;
			ArrayNode joined =
			new ArrayNode(new GraphNode[]{parent,    parent},
						  new String[]   {a.payload, b.payload});
			return joined;
		}
	}

	public static GraphNode mergeArrays(ArrayNode a, ArrayNode b) {
		return null;
	}

	public static String toDOT(GraphNode a) {
		return null;
	}
}
