/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  Copyright (c) 2015 Janyou
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


public class LexerInterpreter: Lexer {
    internal final var grammarFileName: String
    internal final var atn: ATN

    ////@Deprecated
    internal final var tokenNames: [String?]?
    internal final var ruleNames: [String]
    internal final var modeNames: [String]


    private final var vocabulary: Vocabulary?

    internal final var _decisionToDFA: [DFA]
    internal final var _sharedContextCache: PredictionContextCache =
    PredictionContextCache()
//   public override init() {
//    super.init()}

//    public  convenience   init(_ input : CharStream) {
//        self.init()
//        self._input = input;
//        self._tokenFactorySourcePair = (self, input);
//    }
    //@Deprecated
    public convenience init(_ grammarFileName: String, _ tokenNames: Array<String?>?, _ ruleNames: Array<String>, _ modeNames: Array<String>, _ atn: ATN, _ input: CharStream) throws {
        try self.init(grammarFileName, Vocabulary.fromTokenNames(tokenNames), ruleNames, modeNames, atn, input)
    }

    public init(_ grammarFileName: String, _ vocabulary: Vocabulary, _ ruleNames: Array<String>, _ modeNames: Array<String>, _ atn: ATN, _ input: CharStream) throws {

        self.grammarFileName = grammarFileName
        self.atn = atn
        self.tokenNames = [String?]()
        //new String[atn.maxTokenType];
        let length = tokenNames!.count
        for i in 0..<length {
            tokenNames![i] = vocabulary.getDisplayName(i)
        }

        self.ruleNames = ruleNames
        self.modeNames = modeNames
        self.vocabulary = vocabulary

        self._decisionToDFA = [DFA]() //new DFA[atn.getNumberOfDecisions()];
        let _decisionToDFALength = _decisionToDFA.count
        for i in 0..<_decisionToDFALength {
            _decisionToDFA[i] = DFA(atn.getDecisionState(i)!, i)
        }
        super.init()
        self._input = input
        self._tokenFactorySourcePair = (self, input)
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
    ////@Deprecated
    public func getTokenNames() -> [String?]? {
        return tokenNames
    }

    override
    public func getRuleNames() -> [String] {
        return ruleNames
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
