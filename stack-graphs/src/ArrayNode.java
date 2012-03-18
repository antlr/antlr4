import java.util.Arrays;

public class ArrayNode extends GraphNode {
	public final GraphNode[] parents;
	public final String[] payloads;

	public ArrayNode(SingletonNode a) {
		this.parents = new GraphNode[] {a.parent};
		this.payloads = new String[] {a.payload};
	}

	public ArrayNode(GraphNode[] parents, String[] payloads) {
		this.parents = parents;
		this.payloads = payloads;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		else if ( !(o instanceof ArrayNode) ) {
			return false;
		}

		if ( this.hashCode() != o.hashCode() ) {
			return false; // can't be same if hash is different
		}

		ArrayNode a = (ArrayNode)o;
		if ( payloads.length != a.payloads.length ) {
			return false;
		}

		for (int i=0; i<payloads.length; i++) {
			if ( !payloads[i].equals(a.payloads[i]) ) return false;
			if ( !parents[i].equals(a.parents[i]) ) return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return Arrays.toString(payloads)+":"+id;
	}
}
