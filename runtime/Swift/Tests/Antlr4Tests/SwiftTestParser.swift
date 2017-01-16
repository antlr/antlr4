// Generated from SwiftTest.g4 by ANTLR 4.6
import Antlr4

open class SwiftTestParser: Parser {

	internal static var _decisionToDFA: [DFA] = {
          var decisionToDFA = [DFA]()
          let length = SwiftTestParser._ATN.getNumberOfDecisions()
          for i in 0..<length {
            decisionToDFA.append(DFA(SwiftTestParser._ATN.getDecisionState(i)!, i))
           }
           return decisionToDFA
     }()
	internal static let _sharedContextCache: PredictionContextCache = PredictionContextCache()
	public enum Tokens: Int {
		case EOF = -1, T__0 = 1
	}
	public static let RULE_s = 0
	public static let ruleNames: [String] = [
		"s"
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

	override
	open func getGrammarFileName() -> String { return "SwiftTest.g4" }

	override
	open func getRuleNames() -> [String] { return SwiftTestParser.ruleNames }

	override
	open func getSerializedATN() -> String { return SwiftTestParser._serializedATN }

	override
	open func getATN() -> ATN { return SwiftTestParser._ATN }

	open override func getVocabulary() -> Vocabulary {
	    return SwiftTestParser.VOCABULARY
	}

	public override init(_ input:TokenStream)throws {
	    RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION)
		try super.init(input)
		_interp = ParserATNSimulator(self,SwiftTestParser._ATN,SwiftTestParser._decisionToDFA, SwiftTestParser._sharedContextCache)
	}
	open class SContext:ParserRuleContext {
		open func EOF() -> TerminalNode? { return getToken(SwiftTestParser.Tokens.EOF.rawValue, 0) }
		open override func getRuleIndex() -> Int { return SwiftTestParser.RULE_s }
		override
		open func enterRule(_ listener: ParseTreeListener) {
			if listener is SwiftTestListener {
			 	(listener as! SwiftTestListener).enterS(self)
			}
		}
		override
		open func exitRule(_ listener: ParseTreeListener) {
			if listener is SwiftTestListener {
			 	(listener as! SwiftTestListener).exitS(self)
			}
		}
	}
	@discardableResult
	open func s() throws -> SContext {
		var _localctx: SContext = SContext(_ctx, getState())
		try enterRule(_localctx, 0, SwiftTestParser.RULE_s)
		defer {
	    		try! exitRule()
	    }
		do {
		 	try enterOuterAlt(_localctx, 1)
		 	setState(2)
		 	try match(SwiftTestParser.Tokens.T__0.rawValue)
		 	setState(3)
		 	try match(SwiftTestParser.Tokens.EOF.rawValue)

		}
		catch ANTLRException.recognition(let re) {
			_localctx.exception = re
			_errHandler.reportError(self, re)
			try _errHandler.recover(self, re)
		}

		return _localctx
	}

   public static let _serializedATN : String = SwiftTestParserATN().jsonString
   public static let _ATN: ATN = ATNDeserializer().deserializeFromJson(_serializedATN)
}