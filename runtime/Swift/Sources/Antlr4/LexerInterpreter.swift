/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


public class LexerInterpreter: Lexer {
    internal final var grammarFileName: String
    internal final var atn: ATN

    internal final var ruleNames: [String]
    internal final var channelNames: [String]
    internal final var modeNames: [String]

    private final var vocabulary: Vocabulary?

    internal final var _decisionToDFA: [DFA]
    internal final var _sharedContextCache = PredictionContextCache()

//   public override init() {
//    super.init()}

//    public  convenience   init(_ input : CharStream) {
//        self.init()
//        self._input = input;
//        self._tokenFactorySourcePair = (self, input);
//    }
    //@Deprecated
    public convenience init(_ grammarFileName: String, _ tokenNames: Array<String?>?, _ ruleNames: Array<String>, _ channelNames: Array<String>, _ modeNames: Array<String>, _ atn: ATN, _ input: CharStream) throws {
        try self.init(grammarFileName, Vocabulary.fromTokenNames(tokenNames), ruleNames, channelNames, modeNames, atn, input)
    }

    public init(_ grammarFileName: String, _ vocabulary: Vocabulary, _ ruleNames: Array<String>, _ channelNames: Array<String>, _ modeNames: Array<String>, _ atn: ATN, _ input: CharStream) throws {

        self.grammarFileName = grammarFileName
        self.atn = atn
        self.ruleNames = ruleNames
        self.channelNames = channelNames
        self.modeNames = modeNames
        self.vocabulary = vocabulary

        self._decisionToDFA = [DFA]() //new DFA[atn.getNumberOfDecisions()];
        let _decisionToDFALength = _decisionToDFA.count
        for i in 0..<_decisionToDFALength {
            _decisionToDFA[i] = DFA(atn.getDecisionState(i)!, i)
        }
        super.init(input)
        self._interp = LexerATNSimulator(self, atn, _decisionToDFA, _sharedContextCache)

        if atn.grammarType != ATNType.lexer {
            throw ANTLRError.illegalArgument(msg: "The ATN must be a lexer ATN.")

        }
    }

    override
    public func getATN() -> ATN {
        return atn
    }

    override
    public func getGrammarFileName() -> String {
        return grammarFileName
    }

    override
    public func getRuleNames() -> [String] {
        return ruleNames
    }

    override
    public func getChannelNames() -> [String] {
        return channelNames
    }

    override
    public func getModeNames() -> [String] {
        return modeNames
    }

    override
    public func getVocabulary() -> Vocabulary {
        if vocabulary != nil {
            return vocabulary!
        }

        return super.getVocabulary()
    }
}
