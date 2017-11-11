/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// 
/// This implementation of _org.antlr.v4.runtime.ANTLRErrorListener_ dispatches all calls to a
/// collection of delegate listeners. This reduces the effort required to support multiple
/// listeners.
/// 
/// - Author: Sam Harwell
/// 

public class ProxyErrorListener: ANTLRErrorListener {
    private final var delegates: [ANTLRErrorListener]

    public init(_ delegates: [ANTLRErrorListener]) {
        self.delegates = delegates
    }

    public func syntaxError<T>(_ recognizer: Recognizer<T>,
                               _ offendingSymbol: AnyObject?,
                               _ line: Int,
                               _ charPositionInLine: Int,
                               _ msg: String,
                               _ e: AnyObject?)
    {
        for listener in delegates {
            listener.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
        }
    }


    public func reportAmbiguity(_ recognizer: Parser,
                                _ dfa: DFA,
                                _ startIndex: Int,
                                _ stopIndex: Int,
                                _ exact: Bool,
                                _ ambigAlts: BitSet,
                                _ configs: ATNConfigSet) {
        for listener in delegates {
            listener.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs)
        }
    }


    public func reportAttemptingFullContext(_ recognizer: Parser,
                                            _ dfa: DFA,
                                            _ startIndex: Int,
                                            _ stopIndex: Int,
                                            _ conflictingAlts: BitSet?,
                                            _ configs: ATNConfigSet) {
        for listener in delegates {
            listener.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs)
        }
    }


    public func reportContextSensitivity(_ recognizer: Parser,
                                         _ dfa: DFA,
                                         _ startIndex: Int,
                                         _ stopIndex: Int,
                                         _ prediction: Int,
                                         _ configs: ATNConfigSet) {
        for listener in delegates {
            listener.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs)
        }
    }
}
