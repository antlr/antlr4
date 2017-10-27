/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// Provides an empty default implementation of _org.antlr.v4.runtime.ANTLRErrorListener_. The
/// default implementation of each method does nothing, but can be overridden as
/// necessary.
/// 
/// -  Sam Harwell
/// 

open class BaseErrorListener: ANTLRErrorListener {
    public init() {
    }

    open func syntaxError<T>(_ recognizer: Recognizer<T>,
                             _ offendingSymbol: AnyObject?,
                             _ line: Int,
                             _ charPositionInLine: Int,
                             _ msg: String,
                             _ e: AnyObject?
    ) {
    }


    open func reportAmbiguity(_ recognizer: Parser,
                                _ dfa: DFA,
                                _ startIndex: Int,
                                _ stopIndex: Int,
                                _ exact: Bool,
                                _ ambigAlts: BitSet,
                                _ configs: ATNConfigSet) {
    }


    open func reportAttemptingFullContext(_ recognizer: Parser,
                                            _ dfa: DFA,
                                            _ startIndex: Int,
                                            _ stopIndex: Int,
                                            _ conflictingAlts: BitSet?,
                                            _ configs: ATNConfigSet) {
    }


    open func reportContextSensitivity(_ recognizer: Parser,
                                         _ dfa: DFA,
                                         _ startIndex: Int,
                                         _ stopIndex: Int,
                                         _ prediction: Int,
                                         _ configs: ATNConfigSet) {
    }
}
