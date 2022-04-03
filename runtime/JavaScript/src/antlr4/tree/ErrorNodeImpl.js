/**
 * Represents a token that was consumed during resynchronization
 * rather than during a valid match operation. For example,
 * we will create this kind of a node during single token insertion
 * and deletion as well as during "consume until error recovery set"
 * upon no viable alternative exceptions.
 */
import TerminalNodeImpl from "./TerminalNodeImpl.js";

export default class ErrorNodeImpl extends TerminalNodeImpl {
    constructor(token) {
        super(token);
    }

    isErrorNode() {
        return true;
    }

    accept(visitor) {
        return visitor.visitErrorNode(this);
    }
}
