/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import ErrorListener from "./ErrorListener.js";

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
export default class ConsoleErrorListener extends ErrorListener {
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
