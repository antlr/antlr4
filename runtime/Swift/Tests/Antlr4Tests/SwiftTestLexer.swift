// Generated from SwiftTest.g4 by ANTLR 4.6
import Antlr4

open class SwiftTestLexer: Lexer {
	internal static var _decisionToDFA: [DFA] = {
          var decisionToDFA = [DFA]()
          let length = SwiftTestLexer._ATN.getNumberOfDecisions()
          for i in 0..<length {
          	    decisionToDFA.append(DFA(SwiftTestLexer._ATN.getDecisionState(i)!, i))
          }
           return decisionToDFA
     }()

	internal static let _sharedContextCache:PredictionContextCache = PredictionContextCache()
	public static let T__0=1
	public static let modeNames: [String] = [
		"DEFAULT_MODE"
	]

	public static let ruleNames: [String] = [
		"T__0"
	]

	private static let _LITERAL_NAMES: [String?] = [
		nil, "'A'"
	]
	private static let _SYMBOLIC_NAMES: [String?] = [
	]
	public static let VOCABULARY: Vocabulary = Vocabulary(_LITERAL_NAMES, _SYMBOLIC_NAMES)

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	//@Deprecated
	public let tokenNames: [String?]? = {
	    let length = _SYMBOLIC_NAMES.count
	    var tokenNames = [String?](repeating: nil, count: length)
		for i in 0..<length {
			var name = VOCABULARY.getLiteralName(i)
			if name == nil {
				name = VOCABULARY.getSymbolicName(i)
			}
			if name == nil {
				name = "<INVALID>"
			}
			tokenNames[i] = name
		}
		return tokenNames
	}()

	override
	open func getTokenNames() -> [String?]? {
		return tokenNames
	}

    open override func getVocabulary() -> Vocabulary {
        return SwiftTestLexer.VOCABULARY
    }

	public override init(_ input: CharStream) {
	    RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION)
		super.init(input)
		_interp = LexerATNSimulator(self, SwiftTestLexer._ATN, SwiftTestLexer._decisionToDFA, SwiftTestLexer._sharedContextCache)
	}

	override
	open func getGrammarFileName() -> String { return "SwiftTest.g4" }

    override
	open func getRuleNames() -> [String] { return SwiftTestLexer.ruleNames }

	override
	open func getSerializedATN() -> String { return SwiftTestLexer._serializedATN }

	override
	open func getModeNames() -> [String] { return SwiftTestLexer.modeNames }

	override
	open func getATN() -> ATN { return SwiftTestLexer._ATN }

    public static let _serializedATN: String = SwiftTestLexerATN().jsonString
	public static let _ATN: ATN = ATNDeserializer().deserializeFromJson(_serializedATN)
}
