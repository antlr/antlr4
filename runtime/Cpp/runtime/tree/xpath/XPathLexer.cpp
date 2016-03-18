// Generated from java-escape by ANTLR 4.x
#include "Lexer.h"
#include "CharStream.h"
#include "Token.h"
#include "TokenStream.h"
#include "PredictionContextCache.h"
#include "DFA.h"
#include "ATN.h"

class XPathLexer : public org::antlr::v4::runtime::Lexer {
	static org::antlr::v4::runtime::dfa::DFA _decisionToDFA[];
	 org::antlr::v4::runtime::atn::PredictionContextCache *_sharedContextCache =
		new org::antlr::v4::runtime::atn::PredictionContextCache();
	 int
		TOKEN_REF=1, RULE_REF=2, ANYWHERE=3, ROOT=4, WILDCARD=5, BANG=6, ID=7, 
		STRING=8;

	static std::vector<std::wstring> ruleNames = {
		"ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "NameChar", "NameStartChar", 
		"STRING"
	};


	XPathLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
		_modeNames.insert(_modeNames.end(),L"DEFAULT_MODE");
	    _tokenNames.insert(_tokenNames.end(),L"<INVALID>",L"TOKEN_REF",L"RULE_REF",
                                                       L"'//'",L"'/'",L"'*'",
                                                       L"'!'",L"ID",L"STRING");
	}

	const std::wstring& getGrammarFileName() { return "XPathLexer.g4"; }

	const std::vector<std::wstring>& getTokenNames() { return tokenNames; }

	const std::vector<std::wstring>& getRuleNames() { return ruleNames; }

	const std::wstring& getSerializedATN() { return _serializedATN; }

	const std::vector<std::wstring>& getModeNames() { return modeNames; }

	@Override
	const ATN& getATN() { return _ATN; }

	@Override
	void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 4: ID_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private:
	void ID_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: 
						String text = getText();
						if ( Character.isUpperCase(text.charAt(0)) ) setType(TOKEN_REF);
						else setType(RULE_REF);
						 break;
		}
	}
	public:

	static std::wstring _serializedATN =
		"\3\u0f63\ub3d0\u10be\u9b29\u438c\u6c08\uc57f\u1da2\2\n\64\b\1\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\3\2\3\2\3\2\3"+
		"\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\7\6\37\n\6\f\6\16\6\"\13\6\3\6\3\6\3\7"+
		"\3\7\5\7(\n\7\3\b\3\b\3\t\3\t\7\t.\n\t\f\t\16\t\61\13\t\3\t\3\t\3/\2\n"+
		"\3\5\1\5\6\1\7\7\1\t\b\1\13\t\2\r\2\1\17\2\1\21\n\1\3\2\4\7\2\62;aa\u00b9"+
		"\u00b9\u0302\u0371\u2041\u2042\17\2C\\c|\u00c2\u00d8\u00da\u00f8\u00fa"+
		"\u0301\u0372\u037f\u0381\u2001\u200e\u200f\u2072\u2191\u2c02\u2ff1\u3003"+
		"\ud801\uf902\ufdd1\ufdf2\uffff\64\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2"+
		"\2\t\3\2\2\2\2\13\3\2\2\2\2\21\3\2\2\2\3\23\3\2\2\2\5\26\3\2\2\2\7\30"+
		"\3\2\2\2\t\32\3\2\2\2\13\34\3\2\2\2\r\'\3\2\2\2\17)\3\2\2\2\21+\3\2\2"+
		"\2\23\24\7\61\2\2\24\25\7\61\2\2\25\4\3\2\2\2\26\27\7\61\2\2\27\6\3\2"+
		"\2\2\30\31\7,\2\2\31\b\3\2\2\2\32\33\7#\2\2\33\n\3\2\2\2\34 \5\17\b\2"+
		"\35\37\5\r\7\2\36\35\3\2\2\2\37\"\3\2\2\2 \36\3\2\2\2 !\3\2\2\2!#\3\2"+
		"\2\2\" \3\2\2\2#$\b\6\2\2$\f\3\2\2\2%(\5\17\b\2&(\t\2\2\2\'%\3\2\2\2\'"+
		"&\3\2\2\2(\16\3\2\2\2)*\t\3\2\2*\20\3\2\2\2+/\7)\2\2,.\13\2\2\2-,\3\2"+
		"\2\2.\61\3\2\2\2/\60\3\2\2\2/-\3\2\2\2\60\62\3\2\2\2\61/\3\2\2\2\62\63"+
		"\7)\2\2\63\22\3\2\2\2\6\2 \'/";
	static ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
};
