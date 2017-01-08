/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import Foundation

open class Recognizer<ATNInterpreter:ATNSimulator> {
    //public  static let EOF: Int = -1
    //TODO: WeakKeyDictionary NSMapTable Dictionary MapTable<Vocabulary,HashMap<String, Int>>
    private let tokenTypeMapCache = HashMap<Vocabulary,Dictionary<String, Int>>()

    private let ruleIndexMapCache = HashMap<ArrayWrapper<String>,Dictionary<String, Int>>()


    private var _listeners: Array<ANTLRErrorListener> = [ConsoleErrorListener.INSTANCE]


    public var _interp: ATNInterpreter!

    private var _stateNumber: Int = -1

    /** Used to print out token names like ID during debugging and
     *  error reporting.  The generated parsers implement a method
     *  that overrides this to point to their String[] tokenNames.
     *
     * @deprecated Use {@link #getVocabulary()} instead.
     */
    ////@Deprecated
    open func getTokenNames() -> [String?]? {
        RuntimeException(#function + " must be overridden")
        return []
    }

    open func getRuleNames() -> [String] {
        RuntimeException(#function + " must be overridden")
        return []
    }


    /**
     * Get the vocabulary used by the recognizer.
     *
     * @return A {@link org.antlr.v4.runtime.Vocabulary} instance providing information about the
     * vocabulary used by the grammar.
     */

    open func getVocabulary() -> Vocabulary {
        return Vocabulary.fromTokenNames(getTokenNames())
    }

    /**
     * Get a map from token names to token types.
     *
     * <p>Used for XPath and tree pattern compilation.</p>
     */
    public func getTokenTypeMap() -> Dictionary<String, Int> {
        let vocabulary: Vocabulary = getVocabulary()
        var result: Dictionary<String, Int>? = self.tokenTypeMapCache[vocabulary]
        synced(tokenTypeMapCache) {
            [unowned self] in
            if result == nil {
                result = Dictionary<String, Int>()
                let length = self.getATN().maxTokenType
                for i in 0...length {
                    let literalName: String? = vocabulary.getLiteralName(i)
                    if literalName != nil {
                        result![literalName!] = i
                    }

                    let symbolicName: String? = vocabulary.getSymbolicName(i)
                    if symbolicName != nil {
                        result![symbolicName!] = i
                    }
                }

                result!["EOF"] = CommonToken.EOF

                //TODO Result Collections.unmodifiableMap

                self.tokenTypeMapCache[vocabulary] = result!
            }


        }
        return result!

    }

    /**
     * Get a map from rule names to rule indexes.
     *
     * <p>Used for XPath and tree pattern compilation.</p>
     */
    public func getRuleIndexMap() -> Dictionary<String, Int> {
        let ruleNames: [String] = getRuleNames()

        let result: Dictionary<String, Int>? = self.ruleIndexMapCache[ArrayWrapper<String>(ruleNames)]
        synced(ruleIndexMapCache) {
            [unowned self] in
            if result == nil {
                self.ruleIndexMapCache[ArrayWrapper<String>(ruleNames)] = Utils.toMap(ruleNames)
            }

        }
        return result!

    }

    public func getTokenType(_ tokenName: String) -> Int {
        let ttype: Int? = getTokenTypeMap()[tokenName]
        if ttype != nil {
            return ttype!
        }
        return CommonToken.INVALID_TYPE
    }

    /**
     * If this recognizer was generated, it will have a serialized ATN
     * representation of the grammar.
     *
     * <p>For interpreters, we don't know their serialized ATN despite having
     * created the interpreter from it.</p>
     */
    open func getSerializedATN() -> String {
        RuntimeException("there is no serialized ATN")
        fatalError()
        ///throw  ANTLRError.UnsupportedOperation /* throw UnsupportedOperationException("there is no /serialized ATN"); */
    }

    /** For debugging and other purposes, might want the grammar name.
     *  Have ANTLR generate an implementation for this method.
     */
    open func getGrammarFileName() -> String {
        RuntimeException(#function + " must be overridden")
        return ""
    }

    /**
     * Get the {@link org.antlr.v4.runtime.atn.ATN} used by the recognizer for prediction.
     *
     * @return The {@link org.antlr.v4.runtime.atn.ATN} used by the recognizer for prediction.
     */
    open func getATN() -> ATN {
        RuntimeException(#function + " must be overridden")
        fatalError()
    }

    /**
     * Get the ATN interpreter used by the recognizer for prediction.
     *
     * @return The ATN interpreter used by the recognizer for prediction.
     */
    open func getInterpreter() -> ATNInterpreter {
        return _interp
    }

    /** If profiling during the parse/lex, this will return DecisionInfo records
     *  for each decision in recognizer in a ParseInfo object.
     *
     * @since 4.3
     */
    open func getParseInfo() -> ParseInfo? {
        return nil
    }

    /**
     * Set the ATN interpreter used by the recognizer for prediction.
     *
     * @param interpreter The ATN interpreter used by the recognizer for
     * prediction.
     */
    open func setInterpreter(_ interpreter: ATNInterpreter) {
        _interp = interpreter
    }

    /** What is the error header, normally line/character position information? */
    //public func getErrorHeader(e : RecognitionException

    open func getErrorHeader(_ e: AnyObject) -> String {
        let line: Int = (e as! RecognitionException).getOffendingToken().getLine()
        let charPositionInLine: Int = (e as! RecognitionException).getOffendingToken().getCharPositionInLine()
        return "line " + String(line) + ":" + String(charPositionInLine)
    }

    /** How should a token be displayed in an error message? The default
     *  is to display just the text, but during development you might
     *  want to have a lot of information spit out.  Override in that case
     *  to use t.toString() (which, for CommonToken, dumps everything about
     *  the token). This is better than forcing you to override a method in
     *  your token objects because you don't have to go modify your lexer
     *  so that it creates a new Java type.
     *
     * @deprecated This method is not called by the ANTLR 4 Runtime. Specific
     * implementations of {@link org.antlr.v4.runtime.ANTLRErrorStrategy} may provide a similar
     * feature when necessary. For example, see
     * {@link org.antlr.v4.runtime.DefaultErrorStrategy#getTokenErrorDisplay}.
     */
    ////@Deprecated
    open func getTokenErrorDisplay(_ t: Token?) -> String {
        guard let t = t else {
            return "<no token>"
        }
        var s: String

        if let text = t.getText() {
            s = text
        } else {
            if t.getType() == CommonToken.EOF {
                s = "<EOF>"
            } else {
                s = "<\(t.getType())>"
            }
        }
        s = s.replaceAll("\n", replacement: "\\n")
        s = s.replaceAll("\r", replacement: "\\r")
        s = s.replaceAll("\t", replacement: "\\t")
        return "\(s)"
    }

    /**
     * @exception NullPointerException if {@code listener} is {@code null}.
     */
    open func addErrorListener(_ listener: ANTLRErrorListener) {

        _listeners.append(listener)
    }

    open func removeErrorListener(_ listener: ANTLRErrorListener) {
        _listeners = _listeners.filter() {
            $0 !== listener
        }

        // _listeners.removeObject(listener);
    }

    open func removeErrorListeners() {
        _listeners.removeAll()
    }


    open func getErrorListeners() -> Array<ANTLRErrorListener> {
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

    open func precpred(_ localctx: RuleContext?, _ precedence: Int) throws -> Bool {
        return true
    }

    open func action(_ _localctx: RuleContext?, _ ruleIndex: Int, _ actionIndex: Int) throws {
    }

    public final func getState() -> Int {
        return _stateNumber
    }

    /** Indicate that the recognizer has changed internal state that is
     *  consistent with the ATN state passed in.  This way we always know
     *  where we are in the ATN as the parser goes along. The rule
     *  context objects form a stack that lets us see the stack of
     *  invoking rules. Combine this and we have complete ATN
     *  configuration information.
     */
    public final func setState(_ atnState: Int) {
//		System.err.println("setState "+atnState);
        _stateNumber = atnState
//		if ( traceATNStates ) _ctx.trace(atnState);
    }

    open func getInputStream() -> IntStream? {
        RuntimeException(#function + "Must be overridden")
        fatalError()
    }


    open func setInputStream(_ input: IntStream) throws {
        RuntimeException(#function + "Must be overridden")

    }


    open func getTokenFactory() -> TokenFactory {
        RuntimeException(#function + "Must be overridden")
        fatalError()
    }


    open func setTokenFactory(_ input: TokenFactory) {
        RuntimeException(#function + "Must be overridden")

    }

}
