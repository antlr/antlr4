/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import Foundation


public protocol RecognizerProtocol {
    func getATN() -> ATN
    func getGrammarFileName() -> String
    func getParseInfo() -> ParseInfo?
    func getRuleNames() -> [String]
    func getSerializedATN() -> String
    func getState() -> Int
    func getTokenType(_ tokenName: String) -> Int
    func getVocabulary() -> Vocabulary
}


open class Recognizer<ATNInterpreter: ATNSimulator>: RecognizerProtocol {
    private var _listeners: [ANTLRErrorListener] = [ConsoleErrorListener.INSTANCE]

    public var _interp: ATNInterpreter!

    private var _stateNumber = -1

    open func getRuleNames() -> [String] {
        fatalError(#function + " must be overridden")
    }

    ///
    /// Get the vocabulary used by the recognizer.
    /// 
    /// - Returns: A _org.antlr.v4.runtime.Vocabulary_ instance providing information about the
    /// vocabulary used by the grammar.
    /// 
    open func getVocabulary() -> Vocabulary {
        fatalError(#function + " must be overridden")
    }

    /// 
    /// Get a map from token names to token types.
    /// 
    /// Used for XPath and tree pattern compilation.
    /// 
    public func getTokenTypeMap() -> [String: Int] {
        return tokenTypeMap
    }

    public lazy var tokenTypeMap: [String: Int] = {
        let vocabulary = getVocabulary()

        var result = [String: Int]()
        let length = getATN().maxTokenType
        for i in 0...length {
            if let literalName = vocabulary.getLiteralName(i) {
                result[literalName] = i
            }

            if let symbolicName = vocabulary.getSymbolicName(i) {
                result[symbolicName] = i
            }
        }

        result["EOF"] = CommonToken.EOF

        return result
    }()


    /// 
    /// Get a map from rule names to rule indexes.
    /// 
    /// Used for XPath and tree pattern compilation.
    /// 
    public func getRuleIndexMap() -> [String : Int] {
        return ruleIndexMap
    }

    public lazy var ruleIndexMap: [String: Int] = {
        let ruleNames = getRuleNames()
        return Utils.toMap(ruleNames)
    }()


    public func getTokenType(_ tokenName: String) -> Int {
        return getTokenTypeMap()[tokenName] ?? CommonToken.INVALID_TYPE
    }

    /// 
    /// If this recognizer was generated, it will have a serialized ATN
    /// representation of the grammar.
    /// 
    /// For interpreters, we don't know their serialized ATN despite having
    /// created the interpreter from it.
    /// 
    open func getSerializedATN() -> String {
        fatalError("there is no serialized ATN")
    }

    /// For debugging and other purposes, might want the grammar name.
    /// Have ANTLR generate an implementation for this method.
    /// 
    open func getGrammarFileName() -> String {
        fatalError(#function + " must be overridden")
    }

    /// 
    /// Get the _org.antlr.v4.runtime.atn.ATN_ used by the recognizer for prediction.
    /// 
    /// - Returns: The _org.antlr.v4.runtime.atn.ATN_ used by the recognizer for prediction.
    /// 
    open func getATN() -> ATN {
        fatalError(#function + " must be overridden")
    }

    /// 
    /// Get the ATN interpreter used by the recognizer for prediction.
    /// 
    /// - Returns: The ATN interpreter used by the recognizer for prediction.
    /// 
    open func getInterpreter() -> ATNInterpreter {
        return _interp
    }

    /// If profiling during the parse/lex, this will return DecisionInfo records
    /// for each decision in recognizer in a ParseInfo object.
    /// 
    /// - Since: 4.3
    /// 
    open func getParseInfo() -> ParseInfo? {
        return nil
    }

    /// 
    /// Set the ATN interpreter used by the recognizer for prediction.
    /// 
    /// - Parameter interpreter: The ATN interpreter used by the recognizer for
    /// prediction.
    /// 
    open func setInterpreter(_ interpreter: ATNInterpreter) {
        _interp = interpreter
    }

    /// 
    /// What is the error header, normally line/character position information?
    /// 
    open func getErrorHeader(_ e: RecognitionException) -> String {
        let offending = e.getOffendingToken()
        let line = offending.getLine()
        let charPositionInLine = offending.getCharPositionInLine()
        return "line \(line):\(charPositionInLine)"
    }

    open func addErrorListener(_ listener: ANTLRErrorListener) {
        _listeners.append(listener)
    }

    open func removeErrorListener(_ listener: ANTLRErrorListener) {
        _listeners = _listeners.filter() {
            $0 !== listener
        }
    }

    open func removeErrorListeners() {
        _listeners.removeAll()
    }

    open func getErrorListeners() -> [ANTLRErrorListener] {
        return _listeners
    }

    open func getErrorListenerDispatch() -> ANTLRErrorListener {
        return ProxyErrorListener(getErrorListeners())
    }

    // subclass needs to override these if there are sempreds or actions
    // that the ATN interp needs to execute
    open func sempred(_ _localctx: RuleContext?, _ ruleIndex: Int, _ actionIndex: Int) throws -> Bool {
        return true
    }

    open func precpred(_ localctx: RuleContext?, _ precedence: Int) -> Bool {
        return true
    }

    open func action(_ _localctx: RuleContext?, _ ruleIndex: Int, _ actionIndex: Int) throws {
    }

    public final func getState() -> Int {
        return _stateNumber
    }

    /// Indicate that the recognizer has changed internal state that is
    /// consistent with the ATN state passed in.  This way we always know
    /// where we are in the ATN as the parser goes along. The rule
    /// context objects form a stack that lets us see the stack of
    /// invoking rules. Combine this and we have complete ATN
    /// configuration information.
    /// 
    public final func setState(_ atnState: Int) {
//		System.err.println("setState "+atnState);
        _stateNumber = atnState
//		if ( traceATNStates ) _ctx.trace(atnState);
    }

    open func getInputStream() -> IntStream? {
        fatalError(#function + " must be overridden")
    }

    open func setInputStream(_ input: IntStream) throws {
        fatalError(#function + " must be overridden")
    }

    open func getTokenFactory() -> TokenFactory {
        fatalError(#function + " must be overridden")
    }

    open func setTokenFactory(_ input: TokenFactory) {
        fatalError(#function + " must be overridden")
    }
}
