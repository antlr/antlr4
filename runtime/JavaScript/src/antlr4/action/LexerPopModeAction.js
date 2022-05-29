/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import {default as LexerActionType } from "../atn/LexerActionType.js";
import LexerAction from "./LexerAction.js";

/**
 * Implements the {@code popMode} lexer action by calling {@link Lexer//popMode}.
 *
 * <p>The {@code popMode} command does not have any parameters, so this action is
 * implemented as a singleton instance exposed by {@link //INSTANCE}.</p>
 */
export default class LexerPopModeAction extends LexerAction {
    constructor() {
        super(LexerActionType.POP_MODE);
    }

    /**
     * <p>This action is implemented by calling {@link Lexer//popMode}.</p>
     */
    execute(lexer) {
        lexer.popMode();
    }

    toString() {
        return "popMode";
    }
}

LexerPopModeAction.INSTANCE = new LexerPopModeAction();
