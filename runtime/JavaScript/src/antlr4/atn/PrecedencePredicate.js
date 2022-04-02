import SemanticContext from "./SemanticContext.js";

export default class PrecedencePredicate extends SemanticContext {

    constructor(precedence) {
        super();
        this.precedence = precedence === undefined ? 0 : precedence;
    }

    evaluate(parser, outerContext) {
        return parser.precpred(outerContext, this.precedence);
    }

    evalPrecedence(parser, outerContext) {
        if (parser.precpred(outerContext, this.precedence)) {
            return SemanticContext.NONE;
        } else {
            return null;
        }
    }

    compareTo(other) {
        return this.precedence - other.precedence;
    }

    updateHashCode(hash) {
        hash.update(this.precedence);
    }

    equals(other) {
        if (this === other) {
            return true;
        } else if (!(other instanceof PrecedencePredicate)) {
            return false;
        } else {
            return this.precedence === other.precedence;
        }
    }

    toString() {
        return "{" + this.precedence + ">=prec}?";
    }

}

// HORRIBLE workaround circular import, avoiding dynamic import
SemanticContext.PrecedencePredicate = PrecedencePredicate;
