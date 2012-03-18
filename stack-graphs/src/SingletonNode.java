public class SingletonNode extends GraphNode {
	public final GraphNode parent;
	public final String payload;

	public SingletonNode(GraphNode parent, String payload) {
		this.parent = parent;
		this.payload = payload;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		else if ( !(o instanceof SingletonNode) ) {
			return false;
		}

		if ( this.hashCode() != o.hashCode() ) {
			return false; // can't be same if hash is different
		}

		SingletonNode s = (SingletonNode)o;
		return payload.equals(s.payload) && parent.equals(s.parent);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return payload+":"+id;
	}
}
