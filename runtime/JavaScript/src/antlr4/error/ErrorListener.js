/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

/**
 * Provides an empty default implementation of {@link ANTLRErrorListener}. The
 * default implementation of each method does nothing, but can be overridden as
 * necessary.
 */
export default class ErrorListener {
    syntaxError(recognizer, offendingSymbol, line, column, msg, e) {
    }

    reportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs) {
    }

    reportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, configs) {
    }

    reportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, configs) {
    }
}
