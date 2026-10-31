import unittest
from antlr4.atn.SemanticContext import SemanticContext, Predicate, PrecedencePredicate, andContext, orContext

# A and B share a slot of an 8-slot set, so operands kept in a set's iteration
# order fail the IgnoresOperandOrder tests.
A = Predicate(0, 0, False)
B = Predicate(0, 8, False)
C = Predicate(0, 9, False)
PREC_2 = PrecedencePredicate(2)
PREC_5 = PrecedencePredicate(5)


class ConstantHashContext(SemanticContext):
    def __hash__(self):
        return 0


class TestSemanticContext(unittest.TestCase):
    def testAndIgnoresOperandOrder(self):
        self.assertEqual(andContext(A, B), andContext(B, A))
        self.assertEqual(hash(andContext(A, B)), hash(andContext(B, A)))

        self.assertEqual("{0:0}?&&{0:8}?", str(andContext(B, A)))

    def testOrIgnoresOperandOrder(self):
        self.assertEqual(orContext(A, B), orContext(B, A))
        self.assertEqual(hash(orContext(A, B)), hash(orContext(B, A)))

    def testAndKeepsLowestPrecedenceOrKeepsHighest(self):
        self.assertEqual("{0:0}?&&{2>=prec}?", str(andContext(andContext(PREC_5, A), PREC_2)))

        self.assertEqual("{0:0}?||{5>=prec}?", str(orContext(orContext(PREC_2, A), PREC_5)))

    def testOrOrdersAndsOperandByOperand(self):
        self.assertEqual("{0:0}?&&{0:8}?||{0:0}?&&{0:9}?", str(orContext(andContext(A, C), andContext(A, B))))

    def testAndKeepsUnequalOperandsWithEqualHashCodes(self):
        self.assertEqual(2, len(andContext(ConstantHashContext(), ConstantHashContext()).opnds))
