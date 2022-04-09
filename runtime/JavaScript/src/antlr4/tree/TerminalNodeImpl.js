/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import Interval from "../misc/Interval.js";
import Token from '../Token.js';
import TerminalNode from "./TerminalNode.js";

export default class TerminalNodeImpl extends TerminalNode {
    constructor(symbol) {
        super();
        this.parentCtx = null;
        this.symbol = symbol;
    }

    getChild(i) {
        return null;
    }

    getSymbol() {
        return this.symbol;
    }

    getParent() {
        return this.parentCtx;
    }

    getPayload() {
        return this.symbol;
    }

    getSourceInterval() {
        if (this.symbol === null) {
            return Interval.INVALID_INTERVAL;
        }
        const tokenIndex = this.symbol.tokenIndex;
        return new Interval(tokenIndex, tokenIndex);
    }

    getChildCount() {
        return 0;
    }

    accept(visitor) {
        return visitor.visitTerminal(this);
    }

    getText() {
        return this.symbol.text;
    }

    toString() {
        if (this.symbol.type === Token.EOF) {
            return "<EOF>";
        } else {
            return this.symbol.text;
        }
    }
}

