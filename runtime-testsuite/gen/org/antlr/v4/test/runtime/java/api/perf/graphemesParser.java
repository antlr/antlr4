// Generated from /Users/julianbissekkou/Documents/development/tapped/antlr4/runtime-testsuite/test/org/antlr/v4/test/runtime/java/api/perf/graphemes.g4 by ANTLR 4.9.1
package org.antlr.v4.test.runtime.java.api.perf;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class graphemesParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Extend=1, ZWJ=2, SpacingMark=3, EmojiCoreSequence=4, EmojiZWJSequence=5, 
		Prepend=6, NonControl=7, CRLF=8, HangulSyllable=9;
	public static final int
		RULE_emoji_sequence = 0, RULE_grapheme_cluster = 1, RULE_graphemes = 2;
	private static String[] makeRuleNames() {
		return new String[] {
			"emoji_sequence", "grapheme_cluster", "graphemes"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, "'\u200D'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Extend", "ZWJ", "SpacingMark", "EmojiCoreSequence", "EmojiZWJSequence", 
			"Prepend", "NonControl", "CRLF", "HangulSyllable"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
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

	@Override
	public String getGrammarFileName() { return "graphemes.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public graphemesParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class Emoji_sequenceContext extends ParserRuleContext {
		public TerminalNode EmojiZWJSequence() { return getToken(graphemesParser.EmojiZWJSequence, 0); }
		public TerminalNode EmojiCoreSequence() { return getToken(graphemesParser.EmojiCoreSequence, 0); }
		public List<TerminalNode> Extend() { return getTokens(graphemesParser.Extend); }
		public TerminalNode Extend(int i) {
			return getToken(graphemesParser.Extend, i);
		}
		public List<TerminalNode> ZWJ() { return getTokens(graphemesParser.ZWJ); }
		public TerminalNode ZWJ(int i) {
			return getToken(graphemesParser.ZWJ, i);
		}
		public List<TerminalNode> SpacingMark() { return getTokens(graphemesParser.SpacingMark); }
		public TerminalNode SpacingMark(int i) {
			return getToken(graphemesParser.SpacingMark, i);
		}
		public Emoji_sequenceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_emoji_sequence; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof graphemesListener ) ((graphemesListener)listener).enterEmoji_sequence(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof graphemesListener ) ((graphemesListener)listener).exitEmoji_sequence(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof graphemesVisitor ) return ((graphemesVisitor<? extends T>)visitor).visitEmoji_sequence(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Emoji_sequenceContext emoji_sequence() throws RecognitionException {
		Emoji_sequenceContext _localctx = new Emoji_sequenceContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_emoji_sequence);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(6);
			_la = _input.LA(1);
			if ( !(_la==EmojiCoreSequence || _la==EmojiZWJSequence) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(10);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(7);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Extend) | (1L << ZWJ) | (1L << SpacingMark))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					} 
				}
				setState(12);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Grapheme_clusterContext extends ParserRuleContext {
		public TerminalNode CRLF() { return getToken(graphemesParser.CRLF, 0); }
		public Emoji_sequenceContext emoji_sequence() {
			return getRuleContext(Emoji_sequenceContext.class,0);
		}
		public TerminalNode HangulSyllable() { return getToken(graphemesParser.HangulSyllable, 0); }
		public TerminalNode NonControl() { return getToken(graphemesParser.NonControl, 0); }
		public List<TerminalNode> Prepend() { return getTokens(graphemesParser.Prepend); }
		public TerminalNode Prepend(int i) {
			return getToken(graphemesParser.Prepend, i);
		}
		public List<TerminalNode> Extend() { return getTokens(graphemesParser.Extend); }
		public TerminalNode Extend(int i) {
			return getToken(graphemesParser.Extend, i);
		}
		public List<TerminalNode> ZWJ() { return getTokens(graphemesParser.ZWJ); }
		public TerminalNode ZWJ(int i) {
			return getToken(graphemesParser.ZWJ, i);
		}
		public List<TerminalNode> SpacingMark() { return getTokens(graphemesParser.SpacingMark); }
		public TerminalNode SpacingMark(int i) {
			return getToken(graphemesParser.SpacingMark, i);
		}
		public Grapheme_clusterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_grapheme_cluster; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof graphemesListener ) ((graphemesListener)listener).enterGrapheme_cluster(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof graphemesListener ) ((graphemesListener)listener).exitGrapheme_cluster(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof graphemesVisitor ) return ((graphemesVisitor<? extends T>)visitor).visitGrapheme_cluster(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Grapheme_clusterContext grapheme_cluster() throws RecognitionException {
		Grapheme_clusterContext _localctx = new Grapheme_clusterContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_grapheme_cluster);
		int _la;
		try {
			setState(31);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CRLF:
				enterOuterAlt(_localctx, 1);
				{
				setState(13);
				match(CRLF);
				}
				break;
			case EmojiCoreSequence:
			case EmojiZWJSequence:
			case Prepend:
			case NonControl:
			case HangulSyllable:
				enterOuterAlt(_localctx, 2);
				{
				setState(17);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==Prepend) {
					{
					{
					setState(14);
					match(Prepend);
					}
					}
					setState(19);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(23);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case EmojiCoreSequence:
				case EmojiZWJSequence:
					{
					setState(20);
					emoji_sequence();
					}
					break;
				case HangulSyllable:
					{
					setState(21);
					match(HangulSyllable);
					}
					break;
				case NonControl:
					{
					setState(22);
					match(NonControl);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(28);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Extend) | (1L << ZWJ) | (1L << SpacingMark))) != 0)) {
					{
					{
					setState(25);
					_la = _input.LA(1);
					if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << Extend) | (1L << ZWJ) | (1L << SpacingMark))) != 0)) ) {
					_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					}
					}
					setState(30);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class GraphemesContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(graphemesParser.EOF, 0); }
		public List<Grapheme_clusterContext> grapheme_cluster() {
			return getRuleContexts(Grapheme_clusterContext.class);
		}
		public Grapheme_clusterContext grapheme_cluster(int i) {
			return getRuleContext(Grapheme_clusterContext.class,i);
		}
		public GraphemesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_graphemes; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof graphemesListener ) ((graphemesListener)listener).enterGraphemes(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof graphemesListener ) ((graphemesListener)listener).exitGraphemes(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof graphemesVisitor ) return ((graphemesVisitor<? extends T>)visitor).visitGraphemes(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GraphemesContext graphemes() throws RecognitionException {
		GraphemesContext _localctx = new GraphemesContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_graphemes);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(36);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EmojiCoreSequence) | (1L << EmojiZWJSequence) | (1L << Prepend) | (1L << NonControl) | (1L << CRLF) | (1L << HangulSyllable))) != 0)) {
				{
				{
				setState(33);
				grapheme_cluster();
				}
				}
				setState(38);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(39);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\13,\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\7\2\13\n\2\f\2\16\2\16\13\2\3\3\3\3\7\3\22\n\3\f\3"+
		"\16\3\25\13\3\3\3\3\3\3\3\5\3\32\n\3\3\3\7\3\35\n\3\f\3\16\3 \13\3\5\3"+
		"\"\n\3\3\4\7\4%\n\4\f\4\16\4(\13\4\3\4\3\4\3\4\2\2\5\2\4\6\2\4\3\2\6\7"+
		"\3\2\3\5\2/\2\b\3\2\2\2\4!\3\2\2\2\6&\3\2\2\2\b\f\t\2\2\2\t\13\t\3\2\2"+
		"\n\t\3\2\2\2\13\16\3\2\2\2\f\n\3\2\2\2\f\r\3\2\2\2\r\3\3\2\2\2\16\f\3"+
		"\2\2\2\17\"\7\n\2\2\20\22\7\b\2\2\21\20\3\2\2\2\22\25\3\2\2\2\23\21\3"+
		"\2\2\2\23\24\3\2\2\2\24\31\3\2\2\2\25\23\3\2\2\2\26\32\5\2\2\2\27\32\7"+
		"\13\2\2\30\32\7\t\2\2\31\26\3\2\2\2\31\27\3\2\2\2\31\30\3\2\2\2\32\36"+
		"\3\2\2\2\33\35\t\3\2\2\34\33\3\2\2\2\35 \3\2\2\2\36\34\3\2\2\2\36\37\3"+
		"\2\2\2\37\"\3\2\2\2 \36\3\2\2\2!\17\3\2\2\2!\23\3\2\2\2\"\5\3\2\2\2#%"+
		"\5\4\3\2$#\3\2\2\2%(\3\2\2\2&$\3\2\2\2&\'\3\2\2\2\')\3\2\2\2(&\3\2\2\2"+
		")*\7\2\2\3*\7\3\2\2\2\b\f\23\31\36!&";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}