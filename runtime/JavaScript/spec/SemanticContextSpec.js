import SemanticContext from "../src/antlr4/atn/SemanticContext.js";
import Predicate from "../src/antlr4/atn/Predicate.js";
import PrecedencePredicate from "../src/antlr4/atn/PrecedencePredicate.js";

// A and B share a bucket of a 16-bucket HashSet, so operands kept in a HashSet's iteration order fail the "ignores operand order" specs.
const A = new Predicate(0, 0, false);
const B = new Predicate(1, 1, false);
const C = new Predicate(1, 2, false);
const PREC_2 = new PrecedencePredicate(2);
const PREC_5 = new PrecedencePredicate(5);

class ConstantHashContext extends SemanticContext {
    updateHashCode(hash) {
        hash.update(0);
    }

    equals(other) {
        return this === other;
    }
}

describe('test SemanticContext', () => {

    it("and ignores operand order", () => {
        const ab = SemanticContext.andContext(A, B);
        const ba = SemanticContext.andContext(B, A);
        expect(ab.equals(ba)).toBeTrue();
        expect(ab.hashCode()).toEqual(ba.hashCode());
        expect(ba.toString()).toEqual("{0:0}?&&{1:1}?");
    })

    it("or ignores operand order", () => {
        const ab = SemanticContext.orContext(A, B);
        const ba = SemanticContext.orContext(B, A);
        expect(ab.equals(ba)).toBeTrue();
        expect(ab.hashCode()).toEqual(ba.hashCode());
    })

    it("and keeps lowest precedence, or keeps highest", () => {
        expect(SemanticContext.andContext(SemanticContext.andContext(PREC_5, A), PREC_2).toString())
            .toEqual("{0:0}?&&{2>=prec}?");
        expect(SemanticContext.orContext(SemanticContext.orContext(PREC_2, A), PREC_5).toString())
            .toEqual("{0:0}?||{5>=prec}?");
    })

    it("or orders ands operand by operand", () => {
        expect(SemanticContext.orContext(SemanticContext.andContext(A, C), SemanticContext.andContext(A, B)).toString())
            .toEqual("{0:0}?&&{1:1}?||{0:0}?&&{1:2}?");
    })

    it("and keeps unequal operands with equal hash codes", () => {
        expect(SemanticContext.andContext(new ConstantHashContext(), new ConstantHashContext()).opnds.length).toEqual(2);
    })
})
