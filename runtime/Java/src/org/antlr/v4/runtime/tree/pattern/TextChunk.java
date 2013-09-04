package org.antlr.v4.runtime.tree.pattern;

class TextChunk extends Chunk {
	public String text;
	public TextChunk(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return "'"+text+"'";
	}
}
