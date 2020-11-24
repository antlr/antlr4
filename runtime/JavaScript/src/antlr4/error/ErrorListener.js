/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

/**
 * Provides an empty default implementation of {@link ANTLRErrorListener}. The
 * default implementation of each method does nothing, but can be overridden as
 * necessary.
 */
class ErrorListener {
    syntaxError(recognizer, offendingSymbol, line, column, msg, e) {
    }

    reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs) {
    }

    reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs) {
    }

    reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs) {
    }
}

/**
 * {@inheritDoc}
 *
 * <p>
 * This implementation prints messages to {@link System//err} containing the
 * values of {@code line}, {@code charPositionInLine}, and {@code msg} using
 * the following format.</p>
 *
 * <pre>
 * line <em>line</em>:<em>charPositionInLine</em> <em>msg</em>
 * </pre>
 *
 */
class ConsoleErrorListener extends ErrorListener {
    constructor() {
        super();
    }

    syntaxError(recognizer, offendingSymbol, line, column, msg, e) {
        console.error("line " + line + ":" + column + " " + msg);
    }
}


/**
 * Provides a default instance of {@link ConsoleErrorListener}.
 */
ConsoleErrorListener.INSTANCE = new ConsoleErrorListener();

class ProxyErrorListener extends ErrorListener {
    constructor(delegates) {
        super();
        if (delegates===null) {
            throw "delegates";
        }
        this.delegates = delegates;
        return this;
    }

    syntaxError(recognizer, offendingSymbol, line, column, msg, e) {
        this.delegates.map(d => d.syntaxError(recognizer, offendingSymbol, line, column, msg, e));
    }

    reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs) {
        this.delegates.map(d => d.reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs));
    }

    reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs) {
        this.delegates.map(d => d.reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs));
    }

    reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs) {
        this.delegates.map(d => d.reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs));
    }
}

module.exports = {ErrorListener, ConsoleErrorListener, ProxyErrorListener}

