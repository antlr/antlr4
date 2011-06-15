package org.antlr.v4.runtime.dfa;

public class LexerDFASerializer extends DFASerializer {
	public LexerDFASerializer(DFA dfa) {
		super(dfa, null);
	}

	@Override
	protected String getEdgeLabel(int i) {
		return "'"+(char)i+"'";
	}
}
