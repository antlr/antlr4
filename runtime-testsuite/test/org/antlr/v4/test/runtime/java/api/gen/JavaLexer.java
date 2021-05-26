// Generated from /Users/julianbissekkou/Documents/development/tapped/antlr4/runtime-testsuite/test/org/antlr/v4/test/runtime/java/api/Java.g4 by ANTLR 4.9.1
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class JavaLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		ABSTRACT=1, ASSERT=2, BOOLEAN=3, BREAK=4, BYTE=5, CASE=6, CATCH=7, CHAR=8, 
		CLASS=9, CONST=10, CONTINUE=11, DEFAULT=12, DO=13, DOUBLE=14, ELSE=15, 
		ENUM=16, EXTENDS=17, FINAL=18, FINALLY=19, FLOAT=20, FOR=21, IF=22, GOTO=23, 
		IMPLEMENTS=24, IMPORT=25, INSTANCEOF=26, INT=27, INTERFACE=28, LONG=29, 
		NATIVE=30, NEW=31, PACKAGE=32, PRIVATE=33, PROTECTED=34, PUBLIC=35, RETURN=36, 
		SHORT=37, STATIC=38, STRICTFP=39, SUPER=40, SWITCH=41, SYNCHRONIZED=42, 
		THIS=43, THROW=44, THROWS=45, TRANSIENT=46, TRY=47, VOID=48, VOLATILE=49, 
		WHILE=50, IntegerLiteral=51, FloatingPointLiteral=52, BooleanLiteral=53, 
		CharacterLiteral=54, StringLiteral=55, NullLiteral=56, LPAREN=57, RPAREN=58, 
		LBRACE=59, RBRACE=60, LBRACK=61, RBRACK=62, SEMI=63, COMMA=64, DOT=65, 
		ASSIGN=66, GT=67, LT=68, BANG=69, TILDE=70, QUESTION=71, COLON=72, EQUAL=73, 
		LE=74, GE=75, NOTEQUAL=76, AND=77, OR=78, INC=79, DEC=80, ADD=81, SUB=82, 
		MUL=83, DIV=84, BITAND=85, BITOR=86, CARET=87, MOD=88, ADD_ASSIGN=89, 
		SUB_ASSIGN=90, MUL_ASSIGN=91, DIV_ASSIGN=92, AND_ASSIGN=93, OR_ASSIGN=94, 
		XOR_ASSIGN=95, MOD_ASSIGN=96, LSHIFT_ASSIGN=97, RSHIFT_ASSIGN=98, URSHIFT_ASSIGN=99, 
		Identifier=100, AT=101, ELLIPSIS=102, WS=103, COMMENT=104, LINE_COMMENT=105;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"ABSTRACT", "ASSERT", "BOOLEAN", "BREAK", "BYTE", "CASE", "CATCH", "CHAR", 
			"CLASS", "CONST", "CONTINUE", "DEFAULT", "DO", "DOUBLE", "ELSE", "ENUM", 
			"EXTENDS", "FINAL", "FINALLY", "FLOAT", "FOR", "IF", "GOTO", "IMPLEMENTS", 
			"IMPORT", "INSTANCEOF", "INT", "INTERFACE", "LONG", "NATIVE", "NEW", 
			"PACKAGE", "PRIVATE", "PROTECTED", "PUBLIC", "RETURN", "SHORT", "STATIC", 
			"STRICTFP", "SUPER", "SWITCH", "SYNCHRONIZED", "THIS", "THROW", "THROWS", 
			"TRANSIENT", "TRY", "VOID", "VOLATILE", "WHILE", "IntegerLiteral", "DecimalIntegerLiteral", 
			"HexIntegerLiteral", "OctalIntegerLiteral", "BinaryIntegerLiteral", "IntegerTypeSuffix", 
			"DecimalNumeral", "Digits", "Digit", "NonZeroDigit", "DigitOrUnderscore", 
			"Underscores", "HexNumeral", "HexDigits", "HexDigit", "HexDigitOrUnderscore", 
			"OctalNumeral", "OctalDigits", "OctalDigit", "OctalDigitOrUnderscore", 
			"BinaryNumeral", "BinaryDigits", "BinaryDigit", "BinaryDigitOrUnderscore", 
			"FloatingPointLiteral", "DecimalFloatingPointLiteral", "ExponentPart", 
			"ExponentIndicator", "SignedInteger", "Sign", "FloatTypeSuffix", "HexadecimalFloatingPointLiteral", 
			"HexSignificand", "BinaryExponent", "BinaryExponentIndicator", "BooleanLiteral", 
			"CharacterLiteral", "SingleCharacter", "StringLiteral", "StringCharacters", 
			"StringCharacter", "EscapeSequence", "OctalEscape", "UnicodeEscape", 
			"ZeroToThree", "NullLiteral", "LPAREN", "RPAREN", "LBRACE", "RBRACE", 
			"LBRACK", "RBRACK", "SEMI", "COMMA", "DOT", "ASSIGN", "GT", "LT", "BANG", 
			"TILDE", "QUESTION", "COLON", "EQUAL", "LE", "GE", "NOTEQUAL", "AND", 
			"OR", "INC", "DEC", "ADD", "SUB", "MUL", "DIV", "BITAND", "BITOR", "CARET", 
			"MOD", "ADD_ASSIGN", "SUB_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", 
			"OR_ASSIGN", "XOR_ASSIGN", "MOD_ASSIGN", "LSHIFT_ASSIGN", "RSHIFT_ASSIGN", 
			"URSHIFT_ASSIGN", "Identifier", "JavaLetter", "JavaLetterOrDigit", "AT", 
			"ELLIPSIS", "WS", "COMMENT", "LINE_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'abstract'", "'assert'", "'boolean'", "'break'", "'byte'", "'case'", 
			"'catch'", "'char'", "'class'", "'const'", "'continue'", "'default'", 
			"'do'", "'double'", "'else'", "'enum'", "'extends'", "'final'", "'finally'", 
			"'float'", "'for'", "'if'", "'goto'", "'implements'", "'import'", "'instanceof'", 
			"'int'", "'interface'", "'long'", "'native'", "'new'", "'package'", "'private'", 
			"'protected'", "'public'", "'return'", "'short'", "'static'", "'strictfp'", 
			"'super'", "'switch'", "'synchronized'", "'this'", "'throw'", "'throws'", 
			"'transient'", "'try'", "'void'", "'volatile'", "'while'", null, null, 
			null, null, null, "'null'", "'('", "')'", "'{'", "'}'", "'['", "']'", 
			"';'", "','", "'.'", "'='", "'>'", "'<'", "'!'", "'~'", "'?'", "':'", 
			"'=='", "'<='", "'>='", "'!='", "'&&'", "'||'", "'++'", "'--'", "'+'", 
			"'-'", "'*'", "'/'", "'&'", "'|'", "'^'", "'%'", "'+='", "'-='", "'*='", 
			"'/='", "'&='", "'|='", "'^='", "'%='", "'<<='", "'>>='", "'>>>='", null, 
			"'@'", "'...'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "ABSTRACT", "ASSERT", "BOOLEAN", "BREAK", "BYTE", "CASE", "CATCH", 
			"CHAR", "CLASS", "CONST", "CONTINUE", "DEFAULT", "DO", "DOUBLE", "ELSE", 
			"ENUM", "EXTENDS", "FINAL", "FINALLY", "FLOAT", "FOR", "IF", "GOTO", 
			"IMPLEMENTS", "IMPORT", "INSTANCEOF", "INT", "INTERFACE", "LONG", "NATIVE", 
			"NEW", "PACKAGE", "PRIVATE", "PROTECTED", "PUBLIC", "RETURN", "SHORT", 
			"STATIC", "STRICTFP", "SUPER", "SWITCH", "SYNCHRONIZED", "THIS", "THROW", 
			"THROWS", "TRANSIENT", "TRY", "VOID", "VOLATILE", "WHILE", "IntegerLiteral", 
			"FloatingPointLiteral", "BooleanLiteral", "CharacterLiteral", "StringLiteral", 
			"NullLiteral", "LPAREN", "RPAREN", "LBRACE", "RBRACE", "LBRACK", "RBRACK", 
			"SEMI", "COMMA", "DOT", "ASSIGN", "GT", "LT", "BANG", "TILDE", "QUESTION", 
			"COLON", "EQUAL", "LE", "GE", "NOTEQUAL", "AND", "OR", "INC", "DEC", 
			"ADD", "SUB", "MUL", "DIV", "BITAND", "BITOR", "CARET", "MOD", "ADD_ASSIGN", 
			"SUB_ASSIGN", "MUL_ASSIGN", "DIV_ASSIGN", "AND_ASSIGN", "OR_ASSIGN", 
			"XOR_ASSIGN", "MOD_ASSIGN", "LSHIFT_ASSIGN", "RSHIFT_ASSIGN", "URSHIFT_ASSIGN", 
			"Identifier", "AT", "ELLIPSIS", "WS", "COMMENT", "LINE_COMMENT"
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


	public JavaLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Java.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 140:
			return JavaLetter_sempred((RuleContext)_localctx, predIndex);
		case 141:
			return JavaLetterOrDigit_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean JavaLetter_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return Character.isJavaIdentifierStart(_input.LA(-1));
		case 1:
			return Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}
	private boolean JavaLetterOrDigit_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2:
			return Character.isJavaIdentifierPart(_input.LA(-1));
		case 3:
			return Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2k\u042e\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4H\tH\4I"+
		"\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\tS\4T\tT"+
		"\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^\4_\t_\4"+
		"`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j\tj\4k\t"+
		"k\4l\tl\4m\tm\4n\tn\4o\to\4p\tp\4q\tq\4r\tr\4s\ts\4t\tt\4u\tu\4v\tv\4"+
		"w\tw\4x\tx\4y\ty\4z\tz\4{\t{\4|\t|\4}\t}\4~\t~\4\177\t\177\4\u0080\t\u0080"+
		"\4\u0081\t\u0081\4\u0082\t\u0082\4\u0083\t\u0083\4\u0084\t\u0084\4\u0085"+
		"\t\u0085\4\u0086\t\u0086\4\u0087\t\u0087\4\u0088\t\u0088\4\u0089\t\u0089"+
		"\4\u008a\t\u008a\4\u008b\t\u008b\4\u008c\t\u008c\4\u008d\t\u008d\4\u008e"+
		"\t\u008e\4\u008f\t\u008f\4\u0090\t\u0090\4\u0091\t\u0091\4\u0092\t\u0092"+
		"\4\u0093\t\u0093\4\u0094\t\u0094\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3"+
		"\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3"+
		"\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3"+
		"\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3"+
		"\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\27\3\27\3"+
		"\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3"+
		"\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\33\3"+
		"\33\3\33\3\33\3\33\3\33\3\33\3\33\3\34\3\34\3\34\3\34\3\35\3\35\3\35\3"+
		"\35\3\35\3\35\3\35\3\35\3\35\3\35\3\36\3\36\3\36\3\36\3\36\3\37\3\37\3"+
		"\37\3\37\3\37\3\37\3\37\3 \3 \3 \3 \3!\3!\3!\3!\3!\3!\3!\3!\3\"\3\"\3"+
		"\"\3\"\3\"\3\"\3\"\3\"\3#\3#\3#\3#\3#\3#\3#\3#\3#\3#\3$\3$\3$\3$\3$\3"+
		"$\3$\3%\3%\3%\3%\3%\3%\3%\3&\3&\3&\3&\3&\3&\3\'\3\'\3\'\3\'\3\'\3\'\3"+
		"\'\3(\3(\3(\3(\3(\3(\3(\3(\3(\3)\3)\3)\3)\3)\3)\3*\3*\3*\3*\3*\3*\3*\3"+
		"+\3+\3+\3+\3+\3+\3+\3+\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,\3-\3-\3-\3-\3-\3"+
		"-\3.\3.\3.\3.\3.\3.\3.\3/\3/\3/\3/\3/\3/\3/\3/\3/\3/\3\60\3\60\3\60\3"+
		"\60\3\61\3\61\3\61\3\61\3\61\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3\62\3"+
		"\62\3\63\3\63\3\63\3\63\3\63\3\63\3\64\3\64\3\64\3\64\5\64\u0281\n\64"+
		"\3\65\3\65\5\65\u0285\n\65\3\66\3\66\5\66\u0289\n\66\3\67\3\67\5\67\u028d"+
		"\n\67\38\38\58\u0291\n8\39\39\3:\3:\3:\5:\u0298\n:\3:\3:\3:\5:\u029d\n"+
		":\5:\u029f\n:\3;\3;\7;\u02a3\n;\f;\16;\u02a6\13;\3;\5;\u02a9\n;\3<\3<"+
		"\5<\u02ad\n<\3=\3=\3>\3>\5>\u02b3\n>\3?\6?\u02b6\n?\r?\16?\u02b7\3@\3"+
		"@\3@\3@\3A\3A\7A\u02c0\nA\fA\16A\u02c3\13A\3A\5A\u02c6\nA\3B\3B\3C\3C"+
		"\5C\u02cc\nC\3D\3D\5D\u02d0\nD\3D\3D\3E\3E\7E\u02d6\nE\fE\16E\u02d9\13"+
		"E\3E\5E\u02dc\nE\3F\3F\3G\3G\5G\u02e2\nG\3H\3H\3H\3H\3I\3I\7I\u02ea\n"+
		"I\fI\16I\u02ed\13I\3I\5I\u02f0\nI\3J\3J\3K\3K\5K\u02f6\nK\3L\3L\5L\u02fa"+
		"\nL\3M\3M\3M\5M\u02ff\nM\3M\5M\u0302\nM\3M\5M\u0305\nM\3M\3M\3M\5M\u030a"+
		"\nM\3M\5M\u030d\nM\3M\3M\3M\5M\u0312\nM\3M\3M\3M\5M\u0317\nM\3N\3N\3N"+
		"\3O\3O\3P\5P\u031f\nP\3P\3P\3Q\3Q\3R\3R\3S\3S\3S\5S\u032a\nS\3T\3T\5T"+
		"\u032e\nT\3T\3T\3T\5T\u0333\nT\3T\3T\5T\u0337\nT\3U\3U\3U\3V\3V\3W\3W"+
		"\3W\3W\3W\3W\3W\3W\3W\5W\u0347\nW\3X\3X\3X\3X\3X\3X\3X\3X\5X\u0351\nX"+
		"\3Y\3Y\3Z\3Z\5Z\u0357\nZ\3Z\3Z\3[\6[\u035c\n[\r[\16[\u035d\3\\\3\\\5\\"+
		"\u0362\n\\\3]\3]\3]\3]\5]\u0368\n]\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\3^\5"+
		"^\u0375\n^\3_\3_\3_\3_\3_\3_\3_\3`\3`\3a\3a\3a\3a\3a\3b\3b\3c\3c\3d\3"+
		"d\3e\3e\3f\3f\3g\3g\3h\3h\3i\3i\3j\3j\3k\3k\3l\3l\3m\3m\3n\3n\3o\3o\3"+
		"p\3p\3q\3q\3r\3r\3r\3s\3s\3s\3t\3t\3t\3u\3u\3u\3v\3v\3v\3w\3w\3w\3x\3"+
		"x\3x\3y\3y\3y\3z\3z\3{\3{\3|\3|\3}\3}\3~\3~\3\177\3\177\3\u0080\3\u0080"+
		"\3\u0081\3\u0081\3\u0082\3\u0082\3\u0082\3\u0083\3\u0083\3\u0083\3\u0084"+
		"\3\u0084\3\u0084\3\u0085\3\u0085\3\u0085\3\u0086\3\u0086\3\u0086\3\u0087"+
		"\3\u0087\3\u0087\3\u0088\3\u0088\3\u0088\3\u0089\3\u0089\3\u0089\3\u008a"+
		"\3\u008a\3\u008a\3\u008a\3\u008b\3\u008b\3\u008b\3\u008b\3\u008c\3\u008c"+
		"\3\u008c\3\u008c\3\u008c\3\u008d\3\u008d\7\u008d\u03f4\n\u008d\f\u008d"+
		"\16\u008d\u03f7\13\u008d\3\u008e\3\u008e\3\u008e\3\u008e\3\u008e\3\u008e"+
		"\5\u008e\u03ff\n\u008e\3\u008f\3\u008f\3\u008f\3\u008f\3\u008f\3\u008f"+
		"\5\u008f\u0407\n\u008f\3\u0090\3\u0090\3\u0091\3\u0091\3\u0091\3\u0091"+
		"\3\u0092\6\u0092\u0410\n\u0092\r\u0092\16\u0092\u0411\3\u0092\3\u0092"+
		"\3\u0093\3\u0093\3\u0093\3\u0093\7\u0093\u041a\n\u0093\f\u0093\16\u0093"+
		"\u041d\13\u0093\3\u0093\3\u0093\3\u0093\3\u0093\3\u0093\3\u0094\3\u0094"+
		"\3\u0094\3\u0094\7\u0094\u0428\n\u0094\f\u0094\16\u0094\u042b\13\u0094"+
		"\3\u0094\3\u0094\3\u041b\2\u0095\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23"+
		"\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31"+
		"\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60"+
		"_\61a\62c\63e\64g\65i\2k\2m\2o\2q\2s\2u\2w\2y\2{\2}\2\177\2\u0081\2\u0083"+
		"\2\u0085\2\u0087\2\u0089\2\u008b\2\u008d\2\u008f\2\u0091\2\u0093\2\u0095"+
		"\2\u0097\66\u0099\2\u009b\2\u009d\2\u009f\2\u00a1\2\u00a3\2\u00a5\2\u00a7"+
		"\2\u00a9\2\u00ab\2\u00ad\67\u00af8\u00b1\2\u00b39\u00b5\2\u00b7\2\u00b9"+
		"\2\u00bb\2\u00bd\2\u00bf\2\u00c1:\u00c3;\u00c5<\u00c7=\u00c9>\u00cb?\u00cd"+
		"@\u00cfA\u00d1B\u00d3C\u00d5D\u00d7E\u00d9F\u00dbG\u00ddH\u00dfI\u00e1"+
		"J\u00e3K\u00e5L\u00e7M\u00e9N\u00ebO\u00edP\u00efQ\u00f1R\u00f3S\u00f5"+
		"T\u00f7U\u00f9V\u00fbW\u00fdX\u00ffY\u0101Z\u0103[\u0105\\\u0107]\u0109"+
		"^\u010b_\u010d`\u010fa\u0111b\u0113c\u0115d\u0117e\u0119f\u011b\2\u011d"+
		"\2\u011fg\u0121h\u0123i\u0125j\u0127k\3\2\30\4\2NNnn\3\2\63;\4\2ZZzz\5"+
		"\2\62;CHch\3\2\629\4\2DDdd\3\2\62\63\4\2GGgg\4\2--//\6\2FFHHffhh\4\2R"+
		"Rrr\4\2))^^\4\2$$^^\n\2$$))^^ddhhppttvv\3\2\62\65\6\2&&C\\aac|\4\2\2\u0081"+
		"\ud802\udc01\3\2\ud802\udc01\3\2\udc02\ue001\7\2&&\62;C\\aac|\5\2\13\f"+
		"\16\17\"\"\4\2\f\f\17\17\2\u043c\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2"+
		"\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2"+
		"\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2"+
		"\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2"+
		"\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2"+
		"\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2"+
		"\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O"+
		"\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2"+
		"\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2"+
		"\2\u0097\3\2\2\2\2\u00ad\3\2\2\2\2\u00af\3\2\2\2\2\u00b3\3\2\2\2\2\u00c1"+
		"\3\2\2\2\2\u00c3\3\2\2\2\2\u00c5\3\2\2\2\2\u00c7\3\2\2\2\2\u00c9\3\2\2"+
		"\2\2\u00cb\3\2\2\2\2\u00cd\3\2\2\2\2\u00cf\3\2\2\2\2\u00d1\3\2\2\2\2\u00d3"+
		"\3\2\2\2\2\u00d5\3\2\2\2\2\u00d7\3\2\2\2\2\u00d9\3\2\2\2\2\u00db\3\2\2"+
		"\2\2\u00dd\3\2\2\2\2\u00df\3\2\2\2\2\u00e1\3\2\2\2\2\u00e3\3\2\2\2\2\u00e5"+
		"\3\2\2\2\2\u00e7\3\2\2\2\2\u00e9\3\2\2\2\2\u00eb\3\2\2\2\2\u00ed\3\2\2"+
		"\2\2\u00ef\3\2\2\2\2\u00f1\3\2\2\2\2\u00f3\3\2\2\2\2\u00f5\3\2\2\2\2\u00f7"+
		"\3\2\2\2\2\u00f9\3\2\2\2\2\u00fb\3\2\2\2\2\u00fd\3\2\2\2\2\u00ff\3\2\2"+
		"\2\2\u0101\3\2\2\2\2\u0103\3\2\2\2\2\u0105\3\2\2\2\2\u0107\3\2\2\2\2\u0109"+
		"\3\2\2\2\2\u010b\3\2\2\2\2\u010d\3\2\2\2\2\u010f\3\2\2\2\2\u0111\3\2\2"+
		"\2\2\u0113\3\2\2\2\2\u0115\3\2\2\2\2\u0117\3\2\2\2\2\u0119\3\2\2\2\2\u011f"+
		"\3\2\2\2\2\u0121\3\2\2\2\2\u0123\3\2\2\2\2\u0125\3\2\2\2\2\u0127\3\2\2"+
		"\2\3\u0129\3\2\2\2\5\u0132\3\2\2\2\7\u0139\3\2\2\2\t\u0141\3\2\2\2\13"+
		"\u0147\3\2\2\2\r\u014c\3\2\2\2\17\u0151\3\2\2\2\21\u0157\3\2\2\2\23\u015c"+
		"\3\2\2\2\25\u0162\3\2\2\2\27\u0168\3\2\2\2\31\u0171\3\2\2\2\33\u0179\3"+
		"\2\2\2\35\u017c\3\2\2\2\37\u0183\3\2\2\2!\u0188\3\2\2\2#\u018d\3\2\2\2"+
		"%\u0195\3\2\2\2\'\u019b\3\2\2\2)\u01a3\3\2\2\2+\u01a9\3\2\2\2-\u01ad\3"+
		"\2\2\2/\u01b0\3\2\2\2\61\u01b5\3\2\2\2\63\u01c0\3\2\2\2\65\u01c7\3\2\2"+
		"\2\67\u01d2\3\2\2\29\u01d6\3\2\2\2;\u01e0\3\2\2\2=\u01e5\3\2\2\2?\u01ec"+
		"\3\2\2\2A\u01f0\3\2\2\2C\u01f8\3\2\2\2E\u0200\3\2\2\2G\u020a\3\2\2\2I"+
		"\u0211\3\2\2\2K\u0218\3\2\2\2M\u021e\3\2\2\2O\u0225\3\2\2\2Q\u022e\3\2"+
		"\2\2S\u0234\3\2\2\2U\u023b\3\2\2\2W\u0248\3\2\2\2Y\u024d\3\2\2\2[\u0253"+
		"\3\2\2\2]\u025a\3\2\2\2_\u0264\3\2\2\2a\u0268\3\2\2\2c\u026d\3\2\2\2e"+
		"\u0276\3\2\2\2g\u0280\3\2\2\2i\u0282\3\2\2\2k\u0286\3\2\2\2m\u028a\3\2"+
		"\2\2o\u028e\3\2\2\2q\u0292\3\2\2\2s\u029e\3\2\2\2u\u02a0\3\2\2\2w\u02ac"+
		"\3\2\2\2y\u02ae\3\2\2\2{\u02b2\3\2\2\2}\u02b5\3\2\2\2\177\u02b9\3\2\2"+
		"\2\u0081\u02bd\3\2\2\2\u0083\u02c7\3\2\2\2\u0085\u02cb\3\2\2\2\u0087\u02cd"+
		"\3\2\2\2\u0089\u02d3\3\2\2\2\u008b\u02dd\3\2\2\2\u008d\u02e1\3\2\2\2\u008f"+
		"\u02e3\3\2\2\2\u0091\u02e7\3\2\2\2\u0093\u02f1\3\2\2\2\u0095\u02f5\3\2"+
		"\2\2\u0097\u02f9\3\2\2\2\u0099\u0316\3\2\2\2\u009b\u0318\3\2\2\2\u009d"+
		"\u031b\3\2\2\2\u009f\u031e\3\2\2\2\u00a1\u0322\3\2\2\2\u00a3\u0324\3\2"+
		"\2\2\u00a5\u0326\3\2\2\2\u00a7\u0336\3\2\2\2\u00a9\u0338\3\2\2\2\u00ab"+
		"\u033b\3\2\2\2\u00ad\u0346\3\2\2\2\u00af\u0350\3\2\2\2\u00b1\u0352\3\2"+
		"\2\2\u00b3\u0354\3\2\2\2\u00b5\u035b\3\2\2\2\u00b7\u0361\3\2\2\2\u00b9"+
		"\u0367\3\2\2\2\u00bb\u0374\3\2\2\2\u00bd\u0376\3\2\2\2\u00bf\u037d\3\2"+
		"\2\2\u00c1\u037f\3\2\2\2\u00c3\u0384\3\2\2\2\u00c5\u0386\3\2\2\2\u00c7"+
		"\u0388\3\2\2\2\u00c9\u038a\3\2\2\2\u00cb\u038c\3\2\2\2\u00cd\u038e\3\2"+
		"\2\2\u00cf\u0390\3\2\2\2\u00d1\u0392\3\2\2\2\u00d3\u0394\3\2\2\2\u00d5"+
		"\u0396\3\2\2\2\u00d7\u0398\3\2\2\2\u00d9\u039a\3\2\2\2\u00db\u039c\3\2"+
		"\2\2\u00dd\u039e\3\2\2\2\u00df\u03a0\3\2\2\2\u00e1\u03a2\3\2\2\2\u00e3"+
		"\u03a4\3\2\2\2\u00e5\u03a7\3\2\2\2\u00e7\u03aa\3\2\2\2\u00e9\u03ad\3\2"+
		"\2\2\u00eb\u03b0\3\2\2\2\u00ed\u03b3\3\2\2\2\u00ef\u03b6\3\2\2\2\u00f1"+
		"\u03b9\3\2\2\2\u00f3\u03bc\3\2\2\2\u00f5\u03be\3\2\2\2\u00f7\u03c0\3\2"+
		"\2\2\u00f9\u03c2\3\2\2\2\u00fb\u03c4\3\2\2\2\u00fd\u03c6\3\2\2\2\u00ff"+
		"\u03c8\3\2\2\2\u0101\u03ca\3\2\2\2\u0103\u03cc\3\2\2\2\u0105\u03cf\3\2"+
		"\2\2\u0107\u03d2\3\2\2\2\u0109\u03d5\3\2\2\2\u010b\u03d8\3\2\2\2\u010d"+
		"\u03db\3\2\2\2\u010f\u03de\3\2\2\2\u0111\u03e1\3\2\2\2\u0113\u03e4\3\2"+
		"\2\2\u0115\u03e8\3\2\2\2\u0117\u03ec\3\2\2\2\u0119\u03f1\3\2\2\2\u011b"+
		"\u03fe\3\2\2\2\u011d\u0406\3\2\2\2\u011f\u0408\3\2\2\2\u0121\u040a\3\2"+
		"\2\2\u0123\u040f\3\2\2\2\u0125\u0415\3\2\2\2\u0127\u0423\3\2\2\2\u0129"+
		"\u012a\7c\2\2\u012a\u012b\7d\2\2\u012b\u012c\7u\2\2\u012c\u012d\7v\2\2"+
		"\u012d\u012e\7t\2\2\u012e\u012f\7c\2\2\u012f\u0130\7e\2\2\u0130\u0131"+
		"\7v\2\2\u0131\4\3\2\2\2\u0132\u0133\7c\2\2\u0133\u0134\7u\2\2\u0134\u0135"+
		"\7u\2\2\u0135\u0136\7g\2\2\u0136\u0137\7t\2\2\u0137\u0138\7v\2\2\u0138"+
		"\6\3\2\2\2\u0139\u013a\7d\2\2\u013a\u013b\7q\2\2\u013b\u013c\7q\2\2\u013c"+
		"\u013d\7n\2\2\u013d\u013e\7g\2\2\u013e\u013f\7c\2\2\u013f\u0140\7p\2\2"+
		"\u0140\b\3\2\2\2\u0141\u0142\7d\2\2\u0142\u0143\7t\2\2\u0143\u0144\7g"+
		"\2\2\u0144\u0145\7c\2\2\u0145\u0146\7m\2\2\u0146\n\3\2\2\2\u0147\u0148"+
		"\7d\2\2\u0148\u0149\7{\2\2\u0149\u014a\7v\2\2\u014a\u014b\7g\2\2\u014b"+
		"\f\3\2\2\2\u014c\u014d\7e\2\2\u014d\u014e\7c\2\2\u014e\u014f\7u\2\2\u014f"+
		"\u0150\7g\2\2\u0150\16\3\2\2\2\u0151\u0152\7e\2\2\u0152\u0153\7c\2\2\u0153"+
		"\u0154\7v\2\2\u0154\u0155\7e\2\2\u0155\u0156\7j\2\2\u0156\20\3\2\2\2\u0157"+
		"\u0158\7e\2\2\u0158\u0159\7j\2\2\u0159\u015a\7c\2\2\u015a\u015b\7t\2\2"+
		"\u015b\22\3\2\2\2\u015c\u015d\7e\2\2\u015d\u015e\7n\2\2\u015e\u015f\7"+
		"c\2\2\u015f\u0160\7u\2\2\u0160\u0161\7u\2\2\u0161\24\3\2\2\2\u0162\u0163"+
		"\7e\2\2\u0163\u0164\7q\2\2\u0164\u0165\7p\2\2\u0165\u0166\7u\2\2\u0166"+
		"\u0167\7v\2\2\u0167\26\3\2\2\2\u0168\u0169\7e\2\2\u0169\u016a\7q\2\2\u016a"+
		"\u016b\7p\2\2\u016b\u016c\7v\2\2\u016c\u016d\7k\2\2\u016d\u016e\7p\2\2"+
		"\u016e\u016f\7w\2\2\u016f\u0170\7g\2\2\u0170\30\3\2\2\2\u0171\u0172\7"+
		"f\2\2\u0172\u0173\7g\2\2\u0173\u0174\7h\2\2\u0174\u0175\7c\2\2\u0175\u0176"+
		"\7w\2\2\u0176\u0177\7n\2\2\u0177\u0178\7v\2\2\u0178\32\3\2\2\2\u0179\u017a"+
		"\7f\2\2\u017a\u017b\7q\2\2\u017b\34\3\2\2\2\u017c\u017d\7f\2\2\u017d\u017e"+
		"\7q\2\2\u017e\u017f\7w\2\2\u017f\u0180\7d\2\2\u0180\u0181\7n\2\2\u0181"+
		"\u0182\7g\2\2\u0182\36\3\2\2\2\u0183\u0184\7g\2\2\u0184\u0185\7n\2\2\u0185"+
		"\u0186\7u\2\2\u0186\u0187\7g\2\2\u0187 \3\2\2\2\u0188\u0189\7g\2\2\u0189"+
		"\u018a\7p\2\2\u018a\u018b\7w\2\2\u018b\u018c\7o\2\2\u018c\"\3\2\2\2\u018d"+
		"\u018e\7g\2\2\u018e\u018f\7z\2\2\u018f\u0190\7v\2\2\u0190\u0191\7g\2\2"+
		"\u0191\u0192\7p\2\2\u0192\u0193\7f\2\2\u0193\u0194\7u\2\2\u0194$\3\2\2"+
		"\2\u0195\u0196\7h\2\2\u0196\u0197\7k\2\2\u0197\u0198\7p\2\2\u0198\u0199"+
		"\7c\2\2\u0199\u019a\7n\2\2\u019a&\3\2\2\2\u019b\u019c\7h\2\2\u019c\u019d"+
		"\7k\2\2\u019d\u019e\7p\2\2\u019e\u019f\7c\2\2\u019f\u01a0\7n\2\2\u01a0"+
		"\u01a1\7n\2\2\u01a1\u01a2\7{\2\2\u01a2(\3\2\2\2\u01a3\u01a4\7h\2\2\u01a4"+
		"\u01a5\7n\2\2\u01a5\u01a6\7q\2\2\u01a6\u01a7\7c\2\2\u01a7\u01a8\7v\2\2"+
		"\u01a8*\3\2\2\2\u01a9\u01aa\7h\2\2\u01aa\u01ab\7q\2\2\u01ab\u01ac\7t\2"+
		"\2\u01ac,\3\2\2\2\u01ad\u01ae\7k\2\2\u01ae\u01af\7h\2\2\u01af.\3\2\2\2"+
		"\u01b0\u01b1\7i\2\2\u01b1\u01b2\7q\2\2\u01b2\u01b3\7v\2\2\u01b3\u01b4"+
		"\7q\2\2\u01b4\60\3\2\2\2\u01b5\u01b6\7k\2\2\u01b6\u01b7\7o\2\2\u01b7\u01b8"+
		"\7r\2\2\u01b8\u01b9\7n\2\2\u01b9\u01ba\7g\2\2\u01ba\u01bb\7o\2\2\u01bb"+
		"\u01bc\7g\2\2\u01bc\u01bd\7p\2\2\u01bd\u01be\7v\2\2\u01be\u01bf\7u\2\2"+
		"\u01bf\62\3\2\2\2\u01c0\u01c1\7k\2\2\u01c1\u01c2\7o\2\2\u01c2\u01c3\7"+
		"r\2\2\u01c3\u01c4\7q\2\2\u01c4\u01c5\7t\2\2\u01c5\u01c6\7v\2\2\u01c6\64"+
		"\3\2\2\2\u01c7\u01c8\7k\2\2\u01c8\u01c9\7p\2\2\u01c9\u01ca\7u\2\2\u01ca"+
		"\u01cb\7v\2\2\u01cb\u01cc\7c\2\2\u01cc\u01cd\7p\2\2\u01cd\u01ce\7e\2\2"+
		"\u01ce\u01cf\7g\2\2\u01cf\u01d0\7q\2\2\u01d0\u01d1\7h\2\2\u01d1\66\3\2"+
		"\2\2\u01d2\u01d3\7k\2\2\u01d3\u01d4\7p\2\2\u01d4\u01d5\7v\2\2\u01d58\3"+
		"\2\2\2\u01d6\u01d7\7k\2\2\u01d7\u01d8\7p\2\2\u01d8\u01d9\7v\2\2\u01d9"+
		"\u01da\7g\2\2\u01da\u01db\7t\2\2\u01db\u01dc\7h\2\2\u01dc\u01dd\7c\2\2"+
		"\u01dd\u01de\7e\2\2\u01de\u01df\7g\2\2\u01df:\3\2\2\2\u01e0\u01e1\7n\2"+
		"\2\u01e1\u01e2\7q\2\2\u01e2\u01e3\7p\2\2\u01e3\u01e4\7i\2\2\u01e4<\3\2"+
		"\2\2\u01e5\u01e6\7p\2\2\u01e6\u01e7\7c\2\2\u01e7\u01e8\7v\2\2\u01e8\u01e9"+
		"\7k\2\2\u01e9\u01ea\7x\2\2\u01ea\u01eb\7g\2\2\u01eb>\3\2\2\2\u01ec\u01ed"+
		"\7p\2\2\u01ed\u01ee\7g\2\2\u01ee\u01ef\7y\2\2\u01ef@\3\2\2\2\u01f0\u01f1"+
		"\7r\2\2\u01f1\u01f2\7c\2\2\u01f2\u01f3\7e\2\2\u01f3\u01f4\7m\2\2\u01f4"+
		"\u01f5\7c\2\2\u01f5\u01f6\7i\2\2\u01f6\u01f7\7g\2\2\u01f7B\3\2\2\2\u01f8"+
		"\u01f9\7r\2\2\u01f9\u01fa\7t\2\2\u01fa\u01fb\7k\2\2\u01fb\u01fc\7x\2\2"+
		"\u01fc\u01fd\7c\2\2\u01fd\u01fe\7v\2\2\u01fe\u01ff\7g\2\2\u01ffD\3\2\2"+
		"\2\u0200\u0201\7r\2\2\u0201\u0202\7t\2\2\u0202\u0203\7q\2\2\u0203\u0204"+
		"\7v\2\2\u0204\u0205\7g\2\2\u0205\u0206\7e\2\2\u0206\u0207\7v\2\2\u0207"+
		"\u0208\7g\2\2\u0208\u0209\7f\2\2\u0209F\3\2\2\2\u020a\u020b\7r\2\2\u020b"+
		"\u020c\7w\2\2\u020c\u020d\7d\2\2\u020d\u020e\7n\2\2\u020e\u020f\7k\2\2"+
		"\u020f\u0210\7e\2\2\u0210H\3\2\2\2\u0211\u0212\7t\2\2\u0212\u0213\7g\2"+
		"\2\u0213\u0214\7v\2\2\u0214\u0215\7w\2\2\u0215\u0216\7t\2\2\u0216\u0217"+
		"\7p\2\2\u0217J\3\2\2\2\u0218\u0219\7u\2\2\u0219\u021a\7j\2\2\u021a\u021b"+
		"\7q\2\2\u021b\u021c\7t\2\2\u021c\u021d\7v\2\2\u021dL\3\2\2\2\u021e\u021f"+
		"\7u\2\2\u021f\u0220\7v\2\2\u0220\u0221\7c\2\2\u0221\u0222\7v\2\2\u0222"+
		"\u0223\7k\2\2\u0223\u0224\7e\2\2\u0224N\3\2\2\2\u0225\u0226\7u\2\2\u0226"+
		"\u0227\7v\2\2\u0227\u0228\7t\2\2\u0228\u0229\7k\2\2\u0229\u022a\7e\2\2"+
		"\u022a\u022b\7v\2\2\u022b\u022c\7h\2\2\u022c\u022d\7r\2\2\u022dP\3\2\2"+
		"\2\u022e\u022f\7u\2\2\u022f\u0230\7w\2\2\u0230\u0231\7r\2\2\u0231\u0232"+
		"\7g\2\2\u0232\u0233\7t\2\2\u0233R\3\2\2\2\u0234\u0235\7u\2\2\u0235\u0236"+
		"\7y\2\2\u0236\u0237\7k\2\2\u0237\u0238\7v\2\2\u0238\u0239\7e\2\2\u0239"+
		"\u023a\7j\2\2\u023aT\3\2\2\2\u023b\u023c\7u\2\2\u023c\u023d\7{\2\2\u023d"+
		"\u023e\7p\2\2\u023e\u023f\7e\2\2\u023f\u0240\7j\2\2\u0240\u0241\7t\2\2"+
		"\u0241\u0242\7q\2\2\u0242\u0243\7p\2\2\u0243\u0244\7k\2\2\u0244\u0245"+
		"\7|\2\2\u0245\u0246\7g\2\2\u0246\u0247\7f\2\2\u0247V\3\2\2\2\u0248\u0249"+
		"\7v\2\2\u0249\u024a\7j\2\2\u024a\u024b\7k\2\2\u024b\u024c\7u\2\2\u024c"+
		"X\3\2\2\2\u024d\u024e\7v\2\2\u024e\u024f\7j\2\2\u024f\u0250\7t\2\2\u0250"+
		"\u0251\7q\2\2\u0251\u0252\7y\2\2\u0252Z\3\2\2\2\u0253\u0254\7v\2\2\u0254"+
		"\u0255\7j\2\2\u0255\u0256\7t\2\2\u0256\u0257\7q\2\2\u0257\u0258\7y\2\2"+
		"\u0258\u0259\7u\2\2\u0259\\\3\2\2\2\u025a\u025b\7v\2\2\u025b\u025c\7t"+
		"\2\2\u025c\u025d\7c\2\2\u025d\u025e\7p\2\2\u025e\u025f\7u\2\2\u025f\u0260"+
		"\7k\2\2\u0260\u0261\7g\2\2\u0261\u0262\7p\2\2\u0262\u0263\7v\2\2\u0263"+
		"^\3\2\2\2\u0264\u0265\7v\2\2\u0265\u0266\7t\2\2\u0266\u0267\7{\2\2\u0267"+
		"`\3\2\2\2\u0268\u0269\7x\2\2\u0269\u026a\7q\2\2\u026a\u026b\7k\2\2\u026b"+
		"\u026c\7f\2\2\u026cb\3\2\2\2\u026d\u026e\7x\2\2\u026e\u026f\7q\2\2\u026f"+
		"\u0270\7n\2\2\u0270\u0271\7c\2\2\u0271\u0272\7v\2\2\u0272\u0273\7k\2\2"+
		"\u0273\u0274\7n\2\2\u0274\u0275\7g\2\2\u0275d\3\2\2\2\u0276\u0277\7y\2"+
		"\2\u0277\u0278\7j\2\2\u0278\u0279\7k\2\2\u0279\u027a\7n\2\2\u027a\u027b"+
		"\7g\2\2\u027bf\3\2\2\2\u027c\u0281\5i\65\2\u027d\u0281\5k\66\2\u027e\u0281"+
		"\5m\67\2\u027f\u0281\5o8\2\u0280\u027c\3\2\2\2\u0280\u027d\3\2\2\2\u0280"+
		"\u027e\3\2\2\2\u0280\u027f\3\2\2\2\u0281h\3\2\2\2\u0282\u0284\5s:\2\u0283"+
		"\u0285\5q9\2\u0284\u0283\3\2\2\2\u0284\u0285\3\2\2\2\u0285j\3\2\2\2\u0286"+
		"\u0288\5\177@\2\u0287\u0289\5q9\2\u0288\u0287\3\2\2\2\u0288\u0289\3\2"+
		"\2\2\u0289l\3\2\2\2\u028a\u028c\5\u0087D\2\u028b\u028d\5q9\2\u028c\u028b"+
		"\3\2\2\2\u028c\u028d\3\2\2\2\u028dn\3\2\2\2\u028e\u0290\5\u008fH\2\u028f"+
		"\u0291\5q9\2\u0290\u028f\3\2\2\2\u0290\u0291\3\2\2\2\u0291p\3\2\2\2\u0292"+
		"\u0293\t\2\2\2\u0293r\3\2\2\2\u0294\u029f\7\62\2\2\u0295\u029c\5y=\2\u0296"+
		"\u0298\5u;\2\u0297\u0296\3\2\2\2\u0297\u0298\3\2\2\2\u0298\u029d\3\2\2"+
		"\2\u0299\u029a\5}?\2\u029a\u029b\5u;\2\u029b\u029d\3\2\2\2\u029c\u0297"+
		"\3\2\2\2\u029c\u0299\3\2\2\2\u029d\u029f\3\2\2\2\u029e\u0294\3\2\2\2\u029e"+
		"\u0295\3\2\2\2\u029ft\3\2\2\2\u02a0\u02a8\5w<\2\u02a1\u02a3\5{>\2\u02a2"+
		"\u02a1\3\2\2\2\u02a3\u02a6\3\2\2\2\u02a4\u02a2\3\2\2\2\u02a4\u02a5\3\2"+
		"\2\2\u02a5\u02a7\3\2\2\2\u02a6\u02a4\3\2\2\2\u02a7\u02a9\5w<\2\u02a8\u02a4"+
		"\3\2\2\2\u02a8\u02a9\3\2\2\2\u02a9v\3\2\2\2\u02aa\u02ad\7\62\2\2\u02ab"+
		"\u02ad\5y=\2\u02ac\u02aa\3\2\2\2\u02ac\u02ab\3\2\2\2\u02adx\3\2\2\2\u02ae"+
		"\u02af\t\3\2\2\u02afz\3\2\2\2\u02b0\u02b3\5w<\2\u02b1\u02b3\7a\2\2\u02b2"+
		"\u02b0\3\2\2\2\u02b2\u02b1\3\2\2\2\u02b3|\3\2\2\2\u02b4\u02b6\7a\2\2\u02b5"+
		"\u02b4\3\2\2\2\u02b6\u02b7\3\2\2\2\u02b7\u02b5\3\2\2\2\u02b7\u02b8\3\2"+
		"\2\2\u02b8~\3\2\2\2\u02b9\u02ba\7\62\2\2\u02ba\u02bb\t\4\2\2\u02bb\u02bc"+
		"\5\u0081A\2\u02bc\u0080\3\2\2\2\u02bd\u02c5\5\u0083B\2\u02be\u02c0\5\u0085"+
		"C\2\u02bf\u02be\3\2\2\2\u02c0\u02c3\3\2\2\2\u02c1\u02bf\3\2\2\2\u02c1"+
		"\u02c2\3\2\2\2\u02c2\u02c4\3\2\2\2\u02c3\u02c1\3\2\2\2\u02c4\u02c6\5\u0083"+
		"B\2\u02c5\u02c1\3\2\2\2\u02c5\u02c6\3\2\2\2\u02c6\u0082\3\2\2\2\u02c7"+
		"\u02c8\t\5\2\2\u02c8\u0084\3\2\2\2\u02c9\u02cc\5\u0083B\2\u02ca\u02cc"+
		"\7a\2\2\u02cb\u02c9\3\2\2\2\u02cb\u02ca\3\2\2\2\u02cc\u0086\3\2\2\2\u02cd"+
		"\u02cf\7\62\2\2\u02ce\u02d0\5}?\2\u02cf\u02ce\3\2\2\2\u02cf\u02d0\3\2"+
		"\2\2\u02d0\u02d1\3\2\2\2\u02d1\u02d2\5\u0089E\2\u02d2\u0088\3\2\2\2\u02d3"+
		"\u02db\5\u008bF\2\u02d4\u02d6\5\u008dG\2\u02d5\u02d4\3\2\2\2\u02d6\u02d9"+
		"\3\2\2\2\u02d7\u02d5\3\2\2\2\u02d7\u02d8\3\2\2\2\u02d8\u02da\3\2\2\2\u02d9"+
		"\u02d7\3\2\2\2\u02da\u02dc\5\u008bF\2\u02db\u02d7\3\2\2\2\u02db\u02dc"+
		"\3\2\2\2\u02dc\u008a\3\2\2\2\u02dd\u02de\t\6\2\2\u02de\u008c\3\2\2\2\u02df"+
		"\u02e2\5\u008bF\2\u02e0\u02e2\7a\2\2\u02e1\u02df\3\2\2\2\u02e1\u02e0\3"+
		"\2\2\2\u02e2\u008e\3\2\2\2\u02e3\u02e4\7\62\2\2\u02e4\u02e5\t\7\2\2\u02e5"+
		"\u02e6\5\u0091I\2\u02e6\u0090\3\2\2\2\u02e7\u02ef\5\u0093J\2\u02e8\u02ea"+
		"\5\u0095K\2\u02e9\u02e8\3\2\2\2\u02ea\u02ed\3\2\2\2\u02eb\u02e9\3\2\2"+
		"\2\u02eb\u02ec\3\2\2\2\u02ec\u02ee\3\2\2\2\u02ed\u02eb\3\2\2\2\u02ee\u02f0"+
		"\5\u0093J\2\u02ef\u02eb\3\2\2\2\u02ef\u02f0\3\2\2\2\u02f0\u0092\3\2\2"+
		"\2\u02f1\u02f2\t\b\2\2\u02f2\u0094\3\2\2\2\u02f3\u02f6\5\u0093J\2\u02f4"+
		"\u02f6\7a\2\2\u02f5\u02f3\3\2\2\2\u02f5\u02f4\3\2\2\2\u02f6\u0096\3\2"+
		"\2\2\u02f7\u02fa\5\u0099M\2\u02f8\u02fa\5\u00a5S\2\u02f9\u02f7\3\2\2\2"+
		"\u02f9\u02f8\3\2\2\2\u02fa\u0098\3\2\2\2\u02fb\u02fc\5u;\2\u02fc\u02fe"+
		"\7\60\2\2\u02fd\u02ff\5u;\2\u02fe\u02fd\3\2\2\2\u02fe\u02ff\3\2\2\2\u02ff"+
		"\u0301\3\2\2\2\u0300\u0302\5\u009bN\2\u0301\u0300\3\2\2\2\u0301\u0302"+
		"\3\2\2\2\u0302\u0304\3\2\2\2\u0303\u0305\5\u00a3R\2\u0304\u0303\3\2\2"+
		"\2\u0304\u0305\3\2\2\2\u0305\u0317\3\2\2\2\u0306\u0307\7\60\2\2\u0307"+
		"\u0309\5u;\2\u0308\u030a\5\u009bN\2\u0309\u0308\3\2\2\2\u0309\u030a\3"+
		"\2\2\2\u030a\u030c\3\2\2\2\u030b\u030d\5\u00a3R\2\u030c\u030b\3\2\2\2"+
		"\u030c\u030d\3\2\2\2\u030d\u0317\3\2\2\2\u030e\u030f\5u;\2\u030f\u0311"+
		"\5\u009bN\2\u0310\u0312\5\u00a3R\2\u0311\u0310\3\2\2\2\u0311\u0312\3\2"+
		"\2\2\u0312\u0317\3\2\2\2\u0313\u0314\5u;\2\u0314\u0315\5\u00a3R\2\u0315"+
		"\u0317\3\2\2\2\u0316\u02fb\3\2\2\2\u0316\u0306\3\2\2\2\u0316\u030e\3\2"+
		"\2\2\u0316\u0313\3\2\2\2\u0317\u009a\3\2\2\2\u0318\u0319\5\u009dO\2\u0319"+
		"\u031a\5\u009fP\2\u031a\u009c\3\2\2\2\u031b\u031c\t\t\2\2\u031c\u009e"+
		"\3\2\2\2\u031d\u031f\5\u00a1Q\2\u031e\u031d\3\2\2\2\u031e\u031f\3\2\2"+
		"\2\u031f\u0320\3\2\2\2\u0320\u0321\5u;\2\u0321\u00a0\3\2\2\2\u0322\u0323"+
		"\t\n\2\2\u0323\u00a2\3\2\2\2\u0324\u0325\t\13\2\2\u0325\u00a4\3\2\2\2"+
		"\u0326\u0327\5\u00a7T\2\u0327\u0329\5\u00a9U\2\u0328\u032a\5\u00a3R\2"+
		"\u0329\u0328\3\2\2\2\u0329\u032a\3\2\2\2\u032a\u00a6\3\2\2\2\u032b\u032d"+
		"\5\177@\2\u032c\u032e\7\60\2\2\u032d\u032c\3\2\2\2\u032d\u032e\3\2\2\2"+
		"\u032e\u0337\3\2\2\2\u032f\u0330\7\62\2\2\u0330\u0332\t\4\2\2\u0331\u0333"+
		"\5\u0081A\2\u0332\u0331\3\2\2\2\u0332\u0333\3\2\2\2\u0333\u0334\3\2\2"+
		"\2\u0334\u0335\7\60\2\2\u0335\u0337\5\u0081A\2\u0336\u032b\3\2\2\2\u0336"+
		"\u032f\3\2\2\2\u0337\u00a8\3\2\2\2\u0338\u0339\5\u00abV\2\u0339\u033a"+
		"\5\u009fP\2\u033a\u00aa\3\2\2\2\u033b\u033c\t\f\2\2\u033c\u00ac\3\2\2"+
		"\2\u033d\u033e\7v\2\2\u033e\u033f\7t\2\2\u033f\u0340\7w\2\2\u0340\u0347"+
		"\7g\2\2\u0341\u0342\7h\2\2\u0342\u0343\7c\2\2\u0343\u0344\7n\2\2\u0344"+
		"\u0345\7u\2\2\u0345\u0347\7g\2\2\u0346\u033d\3\2\2\2\u0346\u0341\3\2\2"+
		"\2\u0347\u00ae\3\2\2\2\u0348\u0349\7)\2\2\u0349\u034a\5\u00b1Y\2\u034a"+
		"\u034b\7)\2\2\u034b\u0351\3\2\2\2\u034c\u034d\7)\2\2\u034d\u034e\5\u00b9"+
		"]\2\u034e\u034f\7)\2\2\u034f\u0351\3\2\2\2\u0350\u0348\3\2\2\2\u0350\u034c"+
		"\3\2\2\2\u0351\u00b0\3\2\2\2\u0352\u0353\n\r\2\2\u0353\u00b2\3\2\2\2\u0354"+
		"\u0356\7$\2\2\u0355\u0357\5\u00b5[\2\u0356\u0355\3\2\2\2\u0356\u0357\3"+
		"\2\2\2\u0357\u0358\3\2\2\2\u0358\u0359\7$\2\2\u0359\u00b4\3\2\2\2\u035a"+
		"\u035c\5\u00b7\\\2\u035b\u035a\3\2\2\2\u035c\u035d\3\2\2\2\u035d\u035b"+
		"\3\2\2\2\u035d\u035e\3\2\2\2\u035e\u00b6\3\2\2\2\u035f\u0362\n\16\2\2"+
		"\u0360\u0362\5\u00b9]\2\u0361\u035f\3\2\2\2\u0361\u0360\3\2\2\2\u0362"+
		"\u00b8\3\2\2\2\u0363\u0364\7^\2\2\u0364\u0368\t\17\2\2\u0365\u0368\5\u00bb"+
		"^\2\u0366\u0368\5\u00bd_\2\u0367\u0363\3\2\2\2\u0367\u0365\3\2\2\2\u0367"+
		"\u0366\3\2\2\2\u0368\u00ba\3\2\2\2\u0369\u036a\7^\2\2\u036a\u0375\5\u008b"+
		"F\2\u036b\u036c\7^\2\2\u036c\u036d\5\u008bF\2\u036d\u036e\5\u008bF\2\u036e"+
		"\u0375\3\2\2\2\u036f\u0370\7^\2\2\u0370\u0371\5\u00bf`\2\u0371\u0372\5"+
		"\u008bF\2\u0372\u0373\5\u008bF\2\u0373\u0375\3\2\2\2\u0374\u0369\3\2\2"+
		"\2\u0374\u036b\3\2\2\2\u0374\u036f\3\2\2\2\u0375\u00bc\3\2\2\2\u0376\u0377"+
		"\7^\2\2\u0377\u0378\7w\2\2\u0378\u0379\5\u0083B\2\u0379\u037a\5\u0083"+
		"B\2\u037a\u037b\5\u0083B\2\u037b\u037c\5\u0083B\2\u037c\u00be\3\2\2\2"+
		"\u037d\u037e\t\20\2\2\u037e\u00c0\3\2\2\2\u037f\u0380\7p\2\2\u0380\u0381"+
		"\7w\2\2\u0381\u0382\7n\2\2\u0382\u0383\7n\2\2\u0383\u00c2\3\2\2\2\u0384"+
		"\u0385\7*\2\2\u0385\u00c4\3\2\2\2\u0386\u0387\7+\2\2\u0387\u00c6\3\2\2"+
		"\2\u0388\u0389\7}\2\2\u0389\u00c8\3\2\2\2\u038a\u038b\7\177\2\2\u038b"+
		"\u00ca\3\2\2\2\u038c\u038d\7]\2\2\u038d\u00cc\3\2\2\2\u038e\u038f\7_\2"+
		"\2\u038f\u00ce\3\2\2\2\u0390\u0391\7=\2\2\u0391\u00d0\3\2\2\2\u0392\u0393"+
		"\7.\2\2\u0393\u00d2\3\2\2\2\u0394\u0395\7\60\2\2\u0395\u00d4\3\2\2\2\u0396"+
		"\u0397\7?\2\2\u0397\u00d6\3\2\2\2\u0398\u0399\7@\2\2\u0399\u00d8\3\2\2"+
		"\2\u039a\u039b\7>\2\2\u039b\u00da\3\2\2\2\u039c\u039d\7#\2\2\u039d\u00dc"+
		"\3\2\2\2\u039e\u039f\7\u0080\2\2\u039f\u00de\3\2\2\2\u03a0\u03a1\7A\2"+
		"\2\u03a1\u00e0\3\2\2\2\u03a2\u03a3\7<\2\2\u03a3\u00e2\3\2\2\2\u03a4\u03a5"+
		"\7?\2\2\u03a5\u03a6\7?\2\2\u03a6\u00e4\3\2\2\2\u03a7\u03a8\7>\2\2\u03a8"+
		"\u03a9\7?\2\2\u03a9\u00e6\3\2\2\2\u03aa\u03ab\7@\2\2\u03ab\u03ac\7?\2"+
		"\2\u03ac\u00e8\3\2\2\2\u03ad\u03ae\7#\2\2\u03ae\u03af\7?\2\2\u03af\u00ea"+
		"\3\2\2\2\u03b0\u03b1\7(\2\2\u03b1\u03b2\7(\2\2\u03b2\u00ec\3\2\2\2\u03b3"+
		"\u03b4\7~\2\2\u03b4\u03b5\7~\2\2\u03b5\u00ee\3\2\2\2\u03b6\u03b7\7-\2"+
		"\2\u03b7\u03b8\7-\2\2\u03b8\u00f0\3\2\2\2\u03b9\u03ba\7/\2\2\u03ba\u03bb"+
		"\7/\2\2\u03bb\u00f2\3\2\2\2\u03bc\u03bd\7-\2\2\u03bd\u00f4\3\2\2\2\u03be"+
		"\u03bf\7/\2\2\u03bf\u00f6\3\2\2\2\u03c0\u03c1\7,\2\2\u03c1\u00f8\3\2\2"+
		"\2\u03c2\u03c3\7\61\2\2\u03c3\u00fa\3\2\2\2\u03c4\u03c5\7(\2\2\u03c5\u00fc"+
		"\3\2\2\2\u03c6\u03c7\7~\2\2\u03c7\u00fe\3\2\2\2\u03c8\u03c9\7`\2\2\u03c9"+
		"\u0100\3\2\2\2\u03ca\u03cb\7\'\2\2\u03cb\u0102\3\2\2\2\u03cc\u03cd\7-"+
		"\2\2\u03cd\u03ce\7?\2\2\u03ce\u0104\3\2\2\2\u03cf\u03d0\7/\2\2\u03d0\u03d1"+
		"\7?\2\2\u03d1\u0106\3\2\2\2\u03d2\u03d3\7,\2\2\u03d3\u03d4\7?\2\2\u03d4"+
		"\u0108\3\2\2\2\u03d5\u03d6\7\61\2\2\u03d6\u03d7\7?\2\2\u03d7\u010a\3\2"+
		"\2\2\u03d8\u03d9\7(\2\2\u03d9\u03da\7?\2\2\u03da\u010c\3\2\2\2\u03db\u03dc"+
		"\7~\2\2\u03dc\u03dd\7?\2\2\u03dd\u010e\3\2\2\2\u03de\u03df\7`\2\2\u03df"+
		"\u03e0\7?\2\2\u03e0\u0110\3\2\2\2\u03e1\u03e2\7\'\2\2\u03e2\u03e3\7?\2"+
		"\2\u03e3\u0112\3\2\2\2\u03e4\u03e5\7>\2\2\u03e5\u03e6\7>\2\2\u03e6\u03e7"+
		"\7?\2\2\u03e7\u0114\3\2\2\2\u03e8\u03e9\7@\2\2\u03e9\u03ea\7@\2\2\u03ea"+
		"\u03eb\7?\2\2\u03eb\u0116\3\2\2\2\u03ec\u03ed\7@\2\2\u03ed\u03ee\7@\2"+
		"\2\u03ee\u03ef\7@\2\2\u03ef\u03f0\7?\2\2\u03f0\u0118\3\2\2\2\u03f1\u03f5"+
		"\5\u011b\u008e\2\u03f2\u03f4\5\u011d\u008f\2\u03f3\u03f2\3\2\2\2\u03f4"+
		"\u03f7\3\2\2\2\u03f5\u03f3\3\2\2\2\u03f5\u03f6\3\2\2\2\u03f6\u011a\3\2"+
		"\2\2\u03f7\u03f5\3\2\2\2\u03f8\u03ff\t\21\2\2\u03f9\u03fa\n\22\2\2\u03fa"+
		"\u03ff\6\u008e\2\2\u03fb\u03fc\t\23\2\2\u03fc\u03fd\t\24\2\2\u03fd\u03ff"+
		"\6\u008e\3\2\u03fe\u03f8\3\2\2\2\u03fe\u03f9\3\2\2\2\u03fe\u03fb\3\2\2"+
		"\2\u03ff\u011c\3\2\2\2\u0400\u0407\t\25\2\2\u0401\u0402\n\22\2\2\u0402"+
		"\u0407\6\u008f\4\2\u0403\u0404\t\23\2\2\u0404\u0405\t\24\2\2\u0405\u0407"+
		"\6\u008f\5\2\u0406\u0400\3\2\2\2\u0406\u0401\3\2\2\2\u0406\u0403\3\2\2"+
		"\2\u0407\u011e\3\2\2\2\u0408\u0409\7B\2\2\u0409\u0120\3\2\2\2\u040a\u040b"+
		"\7\60\2\2\u040b\u040c\7\60\2\2\u040c\u040d\7\60\2\2\u040d\u0122\3\2\2"+
		"\2\u040e\u0410\t\26\2\2\u040f\u040e\3\2\2\2\u0410\u0411\3\2\2\2\u0411"+
		"\u040f\3\2\2\2\u0411\u0412\3\2\2\2\u0412\u0413\3\2\2\2\u0413\u0414\b\u0092"+
		"\2\2\u0414\u0124\3\2\2\2\u0415\u0416\7\61\2\2\u0416\u0417\7,\2\2\u0417"+
		"\u041b\3\2\2\2\u0418\u041a\13\2\2\2\u0419\u0418\3\2\2\2\u041a\u041d\3"+
		"\2\2\2\u041b\u041c\3\2\2\2\u041b\u0419\3\2\2\2\u041c\u041e\3\2\2\2\u041d"+
		"\u041b\3\2\2\2\u041e\u041f\7,\2\2\u041f\u0420\7\61\2\2\u0420\u0421\3\2"+
		"\2\2\u0421\u0422\b\u0093\2\2\u0422\u0126\3\2\2\2\u0423\u0424\7\61\2\2"+
		"\u0424\u0425\7\61\2\2\u0425\u0429\3\2\2\2\u0426\u0428\n\27\2\2\u0427\u0426"+
		"\3\2\2\2\u0428\u042b\3\2\2\2\u0429\u0427\3\2\2\2\u0429\u042a\3\2\2\2\u042a"+
		"\u042c\3\2\2\2\u042b\u0429\3\2\2\2\u042c\u042d\b\u0094\2\2\u042d\u0128"+
		"\3\2\2\2\64\2\u0280\u0284\u0288\u028c\u0290\u0297\u029c\u029e\u02a4\u02a8"+
		"\u02ac\u02b2\u02b7\u02c1\u02c5\u02cb\u02cf\u02d7\u02db\u02e1\u02eb\u02ef"+
		"\u02f5\u02f9\u02fe\u0301\u0304\u0309\u030c\u0311\u0316\u031e\u0329\u032d"+
		"\u0332\u0336\u0346\u0350\u0356\u035d\u0361\u0367\u0374\u03f5\u03fe\u0406"+
		"\u0411\u041b\u0429\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}