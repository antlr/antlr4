package org.antlr.v4.test.runtime;

public class GrammarFile {
	public final String grammarName;
	public final GrammarType type;
	public final String content;

	public boolean containsLexer() {
		return type == GrammarType.Lexer || type == GrammarType.Combined;
	}

	public boolean containsParser() {
		return type == GrammarType.Parser || type == GrammarType.Combined;
	}

	public GrammarFile(String grammarName, GrammarType type, String content) {
		this.grammarName = grammarName;
		this.content = content;
		this.type = type;
	}
}
