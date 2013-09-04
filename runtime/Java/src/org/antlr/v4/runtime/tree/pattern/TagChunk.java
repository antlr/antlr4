package org.antlr.v4.runtime.tree.pattern;

class TagChunk extends Chunk { // <e:expr> or <ID>
	public String tag;
	public String label;

	public TagChunk(String tag) {
		this.tag = tag;
	}

	public TagChunk(String label, String tag) {
		this.label = label;
		this.tag = tag;
	}
	@Override
	public String toString() {
		if ( label!=null ) return label+":"+tag;
		return tag;
	}
}
