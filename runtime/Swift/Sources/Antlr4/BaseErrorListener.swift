/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// Provides an empty default implementation of {@link org.antlr.v4.runtime.ANTLRErrorListener}. The
/// default implementation of each method does nothing, but can be overridden as
/// necessary.
/// 
/// -  Sam Harwell

public class BaseErrorListener: ANTLRErrorListener {

    public func syntaxError<T:ATNSimulator>(_ recognizer: Recognizer<T>,
                                            _ offendingSymbol: AnyObject?,
                                            _ line: Int,
                                            _ charPositionInLine: Int,
                                            _ msg: String,
                                            _ e: AnyObject?//RecognitionException
    ) {
    }


    public func reportAmbiguity(_ recognizer: Parser,
                                _ dfa: DFA,
                                _ startIndex: Int,
                                _ stopIndex: Int,
                                _ exact: Bool,
                                _ ambigAlts: BitSet,
                                _ configs: ATNConfigSet) throws {
    }


    public func reportAttemptingFullContext(_ recognizer: Parser,
                                            _ dfa: DFA,
                                            _ startIndex: Int,
                                            _ stopIndex: Int,
                                            _ conflictingAlts: BitSet?,
                                            _ configs: ATNConfigSet) throws {
    }


    public func reportContextSensitivity(_ recognizer: Parser,
                                         _ dfa: DFA,
                                         _ startIndex: Int,
                                         _ stopIndex: Int,
                                         _ prediction: Int,
                                         _ configs: ATNConfigSet) throws {
    }
}
