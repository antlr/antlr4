// Generated from XPathLexer.g4 by ANTLR 4.8
module antlr.v4.runtime.tree.xpath.XPathLexer;

import antlr.v4.runtime.Lexer;
import antlr.v4.runtime.CharStream;
import antlr.v4.runtime.InterfaceRuleContext;
import antlr.v4.runtime.RuleContext;
import antlr.v4.runtime.RuntimeMetaData;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenStream;
import antlr.v4.runtime.Vocabulary;
import antlr.v4.runtime.VocabularyImpl;
import antlr.v4.runtime.atn.ATN : ATN;
alias ATNType = ATN;
import antlr.v4.runtime.atn.PredictionContextCache;
import antlr.v4.runtime.atn.ATNDeserializer;
import antlr.v4.runtime.atn.LexerATNSimulator;
import antlr.v4.runtime.dfa.DFA;
import antlr.v4.runtime.LexerNoViableAltException;

public class XPathLexer : Lexer {
    alias recover = Lexer.recover;
    static this() { RuntimeMetaData.checkVersion("4.8", RuntimeMetaData.VERSION); }

    protected static DFA[] _decisionToDFA;
    protected PredictionContextCache _sharedContextCache =
        new PredictionContextCache();
    public static immutable int
        TOKEN_REF=1,RULE_REF=2,ANYWHERE=3,ROOT=4,WILDCARD=5,BANG=6,ID=7,
        STRING=8;
    public static string[] channelNames = [
        "DEFAULT_TOKEN_CHANNEL", "HIDDEN"
    ];
    public static string[] modeNames = [
        "DEFAULT_MODE"
    ];

    public static string[] ruleNames = [
        "ANYWHERE","ROOT","WILDCARD","BANG","ID","NameChar","NameStartChar",
        "STRING"
    ];

    private static const string[] _LITERAL_NAMES = [
        null,null,null,"'//'","'/'","'*'","'!'"
    ];
    private static const string[] _SYMBOLIC_NAMES = [
        null,"TOKEN_REF","RULE_REF","ANYWHERE","ROOT","WILDCARD","BANG",
        "ID","STRING"
    ];
    public static Vocabulary VOCABULARY;

    /**
     * @deprecated Use {@link #VOCABULARY} instead.
     */
    public static string[_SYMBOLIC_NAMES.length] tokenNames;

    static this() {
        VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
                if (tokenNames[i] is null) {
                    tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] is null)
            {
                tokenNames[i] = "<INVALID>";
            }
        }
    }

    override public string[] getTokenNames() {
        return tokenNames;
    }

    override public Vocabulary getVocabulary() {
        return VOCABULARY;
    }


    public this(CharStream input) {
        super(input);
        _interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
    }

    override
    public string getGrammarFileName() { return "XPathLexer.g4"; }

    override
    public string[] getRuleNames() { return ruleNames; }

    override
    public wstring getSerializedATN() { return _serializedATN; }

        override
    public string[] getChannelNames() { return channelNames; }

    override
    public string[] getModeNames() { return modeNames; }

    override
    public ATNType getATN() { return _ATN; }

    override
    public void action(InterfaceRuleContext _localctx, int ruleIndex, int actionIndex) {
        switch (ruleIndex) {
        case 4:
            ID_action(cast(InterfaceRuleContext)_localctx, actionIndex);
            break;
            default: {}
        }
    }
    private void ID_action(InterfaceRuleContext _localctx, int actionIndex) {
        switch (actionIndex) {
        case 0:

                           import std.ascii : isUpper;
                           import std.conv : to;

                           string text = to!string(getText);
            	       if (isUpper(text[0])) setType(TOKEN_REF);
            	       else setType(RULE_REF);
            	       
            break;
            default: {}
        }
    }

    public static immutable wstring _serializedATN =
        "\x03\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\x02\n4\b\x01"~
    	"\x04\x02\t\x02\x04\x03\t\x03\x04\x04\t\x04\x04\x05\t\x05\x04\x06\t"~
    	"\x06\x04\x07\t\x07\x04\b\t\b\x04\t\t\t\x03\x02\x03\x02\x03\x02\x03"~
    	"\x03\x03\x03\x03\x04\x03\x04\x03\x05\x03\x05\x03\x06\x03\x06\x07\x06"~
    	"\x1f\n\x06\f\x06\x0e\x06\"\x0b\x06\x03\x06\x03\x06\x03\x07\x03\x07"~
    	"\x05\x07(\n\x07\x03\b\x03\b\x03\t\x03\t\x07\t.\n\t\f\t\x0e\t1\x0b\t"~
    	"\x03\t\x03\t\x03/\x02\n\x03\x05\x05\x06\x07\x07\t\b\x0b\t\r\x02\x0f"~
    	"\x02\x11\n\x03\x02\x04\x07\x022;aa\u00b9\u00b9\u0302\u0371\u2041\u2042"~
    	"\x0f\x02C\\c|\u00c2\u00d8\u00da\u00f8\u00fa\u0301\u0372\u037f\u0381"~
    	"\u2001\u200e\u200f\u2072\u2191\u2c02\u2ff1\u3003[154001]\uf902\ufdd1"~
    	"\ufdf2\uffff\x024\x02\x03\x03\x02\x02\x02\x02\x05\x03\x02\x02\x02\x02"~
    	"\x07\x03\x02\x02\x02\x02\t\x03\x02\x02\x02\x02\x0b\x03\x02\x02\x02"~
    	"\x02\x11\x03\x02\x02\x02\x03\x13\x03\x02\x02\x02\x05\x16\x03\x02\x02"~
    	"\x02\x07\x18\x03\x02\x02\x02\t\x1a\x03\x02\x02\x02\x0b\x1c\x03\x02"~
    	"\x02\x02\r\'\x03\x02\x02\x02\x0f)\x03\x02\x02\x02\x11+\x03\x02\x02"~
    	"\x02\x13\x14\x071\x02\x02\x14\x15\x071\x02\x02\x15\x04\x03\x02\x02"~
    	"\x02\x16\x17\x071\x02\x02\x17\x06\x03\x02\x02\x02\x18\x19\x07,\x02"~
    	"\x02\x19\b\x03\x02\x02\x02\x1a\x1b\x07#\x02\x02\x1b\n\x03\x02\x02\x02"~
    	"\x1c \x05\x0f\b\x02\x1d\x1f\x05\r\x07\x02\x1e\x1d\x03\x02\x02\x02\x1f"~
    	"\"\x03\x02\x02\x02 \x1e\x03\x02\x02\x02 !\x03\x02\x02\x02!#\x03\x02"~
    	"\x02\x02\" \x03\x02\x02\x02#$\b\x06\x02\x02$\f\x03\x02\x02\x02%(\x05"~
    	"\x0f\b\x02&(\t\x02\x02\x02\'%\x03\x02\x02\x02\'&\x03\x02\x02\x02(\x0e"~
    	"\x03\x02\x02\x02)*\t\x03\x02\x02*\x10\x03\x02\x02\x02+/\x07)\x02\x02"~
    	",.\x0b\x02\x02\x02-,\x03\x02\x02\x02.1\x03\x02\x02\x02/0\x03\x02\x02"~
    	"\x02/-\x03\x02\x02\x0202\x03\x02\x02\x021/\x03\x02\x02\x0223\x07)\x02"~
    	"\x023\x12\x03\x02\x02\x02\x06\x02 \'/\x03\x03\x06\x02";
    public static ATNType _ATN;

    static this() {
        auto atnDeserializer = new ATNDeserializer;
        _ATN = atnDeserializer.deserialize(_serializedATN);
        _decisionToDFA.length = 0;
        for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
            _decisionToDFA ~= new DFA(_ATN.getDecisionState(i), i);
        }
    }
}