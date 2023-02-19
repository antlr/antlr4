/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

/**
 * The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
 *  3 kinds of errors: prediction errors, failed predicate errors, and
 *  mismatched input errors. In each case, the parser knows where it is
 *  in the input, where it is in the ATN, the rule invocation stack,
 *  and what kind of problem occurred.
 */

export default class RecognitionException extends Error {

    constructor(params) {
        super(params.message);
        if (Error.captureStackTrace)
            Error.captureStackTrace(this, RecognitionException);
        this.message = params.message;
        this.recognizer = params.recognizer;
        this.input = params.input;
        this.ctx = params.ctx;
        /**
         * The current {@link Token} when an error occurred. Since not all streams
         * support accessing symbols by index, we have to track the {@link Token}
         * instance itself
        */
        this.offendingToken = null;
        /**
         * Get the ATN state number the parser was in at the time the error
         * occurred. For {@link NoViableAltException} and
         * {@link LexerNoViableAltException} exceptions, this is the
         * {@link DecisionState} number. For others, it is the state whose outgoing
         * edge we couldn't match.
         */
        this.offendingState = -1;
        if (this.recognizer!==null) {
            this.offendingState = this.recognizer.state;
        }
    }

    /**
     * Gets the set of input symbols which could potentially follow the
     * previously matched symbol at the time this exception was thrown.
     *
     * <p>If the set of expected tokens is not known and could not be computed,
     * this method returns {@code null}.</p>
     *
     * @return The set of token types that could potentially follow the current
     * state in the ATN, or {@code null} if the information is not available.
     */
    getExpectedTokens() {
        if (this.recognizer!==null) {
            return this.recognizer.atn.getExpectedTokens(this.offendingState, this.ctx);
        } else {
            return null;
        }
    }

    // <p>If the state number is not known, this method returns -1.</p>
    toString() {
        return this.message;
    }
}



