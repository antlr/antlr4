package org.antlr.v4.runtime.tree.pattern;

import java.util.Iterator;

public class MatchIterator implements Iterator<ParseTreeMatch> {
	@Override
	public boolean hasNext() {
		return false;
	}

	@Override
	public ParseTreeMatch next() {
		return null;
	}

	@Override
	public void remove() {
	}
}
