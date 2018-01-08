/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime.tree.xpath;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.misc.Interval;

/** Mimic the old XPathLexer from .g4 file */
public class XPathLexer extends Lexer {
	public static final int
		TOKEN_REF=1, RULE_REF=2, ANYWHERE=3, ROOT=4, WILDCARD=5, BANG=6, ID=7,
		STRING=8;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "NameChar", "NameStartChar",
		"STRING"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, "'//'", "'/'", "'*'", "'!'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "TOKEN_REF", "RULE_REF", "ANYWHERE", "ROOT", "WILDCARD", "BANG",
		"ID", "STRING"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	public String getGrammarFileName() { return "XPathLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override
	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public ATN getATN() {
		return null;
	}

	protected int line = 1;
	protected int charPositionInLine = 0;

	public XPathLexer(CharStream input) {
		super(input);
	}

	@Override
	public Token nextToken() {
		_tokenStartCharIndex = _input.index();
		CommonToken t = null;
		while ( t==null ) {
			switch ( _input.LA(1) ) {
				case '/':
					consume();
					if ( _input.LA(1)=='/' ) {
						consume();
						t = new CommonToken(ANYWHERE, "//");
					}
					else {
						t = new CommonToken(ROOT, "/");
					}
					break;
				case '*':
					consume();
					t = new CommonToken(WILDCARD, "*");
					break;
				case '!':
					consume();
					t = new CommonToken(BANG, "!");
					break;
				case '\'':
					String s = matchString();
					t = new CommonToken(STRING, s);
					break;
				case CharStream.EOF :
					return new CommonToken(EOF, "<EOF>");
				default:
					if ( isNameStartChar(_input.LA(1)) ) {
						String id = matchID();
						if ( Character.isUpperCase(id.charAt(0)) ) t = new CommonToken(TOKEN_REF, id);
						else t = new CommonToken(RULE_REF, id);
					}
					else {
						throw new LexerNoViableAltException(this, _input, _tokenStartCharIndex, null);
					}
					break;
			}
		}
		t.setStartIndex(_tokenStartCharIndex);
		t.setCharPositionInLine(_tokenStartCharIndex);
		t.setLine(line);
		return t;
	}

	public void consume() {
		int curChar = _input.LA(1);
		if ( curChar=='\n' ) {
			line++;
			charPositionInLine=0;
		}
		else {
			charPositionInLine++;
		}
		_input.consume();
	}

	@Override
	public int getCharPositionInLine() {
		return charPositionInLine;
	}

	public String matchID() {
		int start = _input.index();
		consume(); // drop start char
		while ( isNameChar(_input.LA(1)) ) {
			consume();
		}
		return _input.getText(Interval.of(start,_input.index()-1));
	}

	public String matchString() {
		int start = _input.index();
		consume(); // drop first quote
		while ( _input.LA(1)!='\'' ) {
			consume();
		}
		consume(); // drop last quote
		return _input.getText(Interval.of(start,_input.index()-1));
	}

	public boolean isNameChar(int c) { return Character.isUnicodeIdentifierPart(c); }

	public boolean isNameStartChar(int c) { return Character.isUnicodeIdentifierStart(c); }
}
