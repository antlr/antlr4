import {default as LexerActionType } from "../atn/LexerActionType.js";
import LexerAction from "./LexerAction.js";

/**
 * Implements the {@code type} lexer action by calling {@link Lexer//setType}
 * with the assigned type
 */

export default class LexerTypeAction extends LexerAction {
    constructor(type) {
        super(LexerActionType.TYPE);
        this.type = type;
    }

    execute(lexer) {
        lexer.type = this.type;
    }

    updateHashCode(hash) {
        hash.update(this.actionType, this.type);
    }

    equals(other) {
        if(this === other) {
            return true;
        } else if (! (other instanceof LexerTypeAction)) {
            return false;
        } else {
            return this.type === other.type;
        }
    }

    toString() {
        return "type(" + this.type + ")";
    }
}

