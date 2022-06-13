package org.antlr.v4.test.tool;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Interval;

public class MockIntTokenStream implements TokenStream {

	public IntegerList types;
	int p=0;

	public MockIntTokenStream(IntegerList types) { this.types = types; }

	@Override
	public void consume() { p++; }

	@Override
	public int LA(int i) { return LT(i).getType(); }

	@Override
	public int mark() {
		return index();
	}

	@Override
	public int index() { return p; }

	@Override
	public void release(int marker) {
		seek(marker);
	}

	@Override
	public void seek(int index) {
		p = index;
	}

	@Override
	public int size() {
		return types.size();
	}

	@Override
	public String getSourceName() {
		return UNKNOWN_SOURCE_NAME;
	}

	@Override
	public Token LT(int i) {
		CommonToken t;
		int rawIndex = p + i - 1;
		if ( rawIndex>=types.size() ) t = new CommonToken(Token.EOF);
		else t = new CommonToken(types.get(rawIndex));
		t.setTokenIndex(rawIndex);
		return t;
	}

	@Override
	public Token get(int i) {
		return new org.antlr.v4.runtime.CommonToken(types.get(i));
	}

	@Override
	public TokenSource getTokenSource() {
		return null;
	}


	@Override
	public String getText() {
		throw new UnsupportedOperationException("can't give strings");
	}


	@Override
	public String getText(Interval interval) {
		throw new UnsupportedOperationException("can't give strings");
	}


	@Override
	public String getText(RuleContext ctx) {
		throw new UnsupportedOperationException("can't give strings");
	}


	@Override
	public String getText(Token start, Token stop) {
		throw new UnsupportedOperationException("can't give strings");
	}
}
