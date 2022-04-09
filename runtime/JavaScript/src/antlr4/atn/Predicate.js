/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import SemanticContext from "./SemanticContext.js";

export default class Predicate extends SemanticContext {

    constructor(ruleIndex, predIndex, isCtxDependent) {
        super();
        this.ruleIndex = ruleIndex === undefined ? -1 : ruleIndex;
        this.predIndex = predIndex === undefined ? -1 : predIndex;
        this.isCtxDependent = isCtxDependent === undefined ? false : isCtxDependent; // e.g., $i ref in pred
    }

    evaluate(parser, outerContext) {
        const localctx = this.isCtxDependent ? outerContext : null;
        return parser.sempred(localctx, this.ruleIndex, this.predIndex);
    }

    updateHashCode(hash) {
        hash.update(this.ruleIndex, this.predIndex, this.isCtxDependent);
    }

    equals(other) {
        if (this === other) {
            return true;
        } else if (!(other instanceof Predicate)) {
            return false;
        } else {
            return this.ruleIndex === other.ruleIndex &&
                this.predIndex === other.predIndex &&
                this.isCtxDependent === other.isCtxDependent;
        }
    }

    toString() {
        return "{" + this.ruleIndex + ":" + this.predIndex + "}?";
    }
}

/**
 * The default {@link SemanticContext}, which is semantically equivalent to
 * a predicate of the form {@code {true}?}
 */
SemanticContext.NONE = new Predicate();
