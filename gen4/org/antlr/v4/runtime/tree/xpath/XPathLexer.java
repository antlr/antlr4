// Generated from /Users/parrt/antlr/code/antlr4/runtime/Java/src/org/antlr/v4/runtime/tree/xpath/XPathLexer.g4 by ANTLR 4.5.1
package org.antlr.v4.runtime.tree.xpath;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class XPathLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
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
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public XPathLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "XPathLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 4:
			ID_action((RuleContext)_localctx, actionIndex);
			break;
		}
	}
	private void ID_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:

							String text = getText();
							if ( Character.isUpperCase(text.charAt(0)) ) setType(TOKEN_REF);
							else setType(RULE_REF);
							
			break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\n\64\b\1\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3"+
		"\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\7\6\37\n\6\f\6\16\6\"\13\6\3\6\3\6\3\7"+
		"\3\7\5\7(\n\7\3\b\3\b\3\t\3\t\7\t.\n\t\f\t\16\t\61\13\t\3\t\3\t\3/\2\n"+
		"\3\5\5\6\7\7\t\b\13\t\r\2\17\2\21\n\3\2\4\7\2\62;aa\u00b9\u00b9\u0302"+
		"\u0371\u2041\u2042\17\2C\\c|\u00c2\u00d8\u00da\u00f8\u00fa\u0301\u0372"+
		"\u037f\u0381\u2001\u200e\u200f\u2072\u2191\u2c02\u2ff1\u3003\ud801\uf902"+
		"\ufdd1\ufdf2\uffff\64\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2"+
		"\2\13\3\2\2\2\2\21\3\2\2\2\3\23\3\2\2\2\5\26\3\2\2\2\7\30\3\2\2\2\t\32"+
		"\3\2\2\2\13\34\3\2\2\2\r\'\3\2\2\2\17)\3\2\2\2\21+\3\2\2\2\23\24\7\61"+
		"\2\2\24\25\7\61\2\2\25\4\3\2\2\2\26\27\7\61\2\2\27\6\3\2\2\2\30\31\7,"+
		"\2\2\31\b\3\2\2\2\32\33\7#\2\2\33\n\3\2\2\2\34 \5\17\b\2\35\37\5\r\7\2"+
		"\36\35\3\2\2\2\37\"\3\2\2\2 \36\3\2\2\2 !\3\2\2\2!#\3\2\2\2\" \3\2\2\2"+
		"#$\b\6\2\2$\f\3\2\2\2%(\5\17\b\2&(\t\2\2\2\'%\3\2\2\2\'&\3\2\2\2(\16\3"+
		"\2\2\2)*\t\3\2\2*\20\3\2\2\2+/\7)\2\2,.\13\2\2\2-,\3\2\2\2.\61\3\2\2\2"+
		"/\60\3\2\2\2/-\3\2\2\2\60\62\3\2\2\2\61/\3\2\2\2\62\63\7)\2\2\63\22\3"+
		"\2\2\2\6\2 \'/\3\3\6\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}