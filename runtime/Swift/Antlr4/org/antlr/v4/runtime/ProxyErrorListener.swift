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


/**
 * This implementation of {@link org.antlr.v4.runtime.ANTLRErrorListener} dispatches all calls to a
 * collection of delegate listeners. This reduces the effort required to support multiple
 * listeners.
 *
 * @author Sam Harwell
 */

public class ProxyErrorListener: ANTLRErrorListener {
    private final var delegates: Array<ANTLRErrorListener>

    public init(_ delegates: Array<ANTLRErrorListener>) {

        self.delegates = delegates
    }

    //_ e : RecognitionException
    public func syntaxError<T:ATNSimulator>(_ recognizer: Recognizer<T>,
                                            _ offendingSymbol: AnyObject?,
                                            _ line: Int,
                                            _ charPositionInLine: Int,
                                            _ msg: String,
                                            _ e: AnyObject?)
     {
        for listener: ANTLRErrorListener in delegates {
            listener.syntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e)
        }
    }


    public func reportAmbiguity(_ recognizer: Parser,
                                _ dfa: DFA,
                                _ startIndex: Int,
                                _ stopIndex: Int,
                                _ exact: Bool,
                                _ ambigAlts: BitSet,
                                _ configs: ATNConfigSet) throws {
        for listener: ANTLRErrorListener in delegates {
            try listener.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs)
        }
    }


    public func reportAttemptingFullContext(_ recognizer: Parser,
                                            _ dfa: DFA,
                                            _ startIndex: Int,
                                            _ stopIndex: Int,
                                            _ conflictingAlts: BitSet?,
                                            _ configs: ATNConfigSet) throws {
        for listener: ANTLRErrorListener in delegates {
            try listener.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs)
        }
    }


    public func reportContextSensitivity(_ recognizer: Parser,
                                         _ dfa: DFA,
                                         _ startIndex: Int,
                                         _ stopIndex: Int,
                                         _ prediction: Int,
                                         _ configs: ATNConfigSet) throws {
        for listener: ANTLRErrorListener in delegates {
            try listener.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs)
        }
    }
}
