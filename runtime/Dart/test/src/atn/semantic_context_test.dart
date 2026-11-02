import 'package:antlr4/antlr4.dart';
import 'package:test/test.dart';

class ConstantHashContext extends SemanticContext {
  @override
  bool eval(Recognizer parser, RuleContext? parserCallStack) => true;

  @override
  int get hashCode => 0;
}

void main() {
  const a = Predicate(0, 0, false);
  const b = Predicate(0, 2, false);
  const c = Predicate(0, 3, false);
  final prec2 = PrecedencePredicate(2);
  final prec5 = PrecedencePredicate(5);

  test('AND ignores operand order', () {
    expect(SemanticContext.and(a, b), SemanticContext.and(b, a));
    expect(
        SemanticContext.and(a, b).hashCode, SemanticContext.and(b, a).hashCode);

    expect(SemanticContext.and(b, a).toString(), '{0:0}?&&{0:2}?');
  });

  test('OR ignores operand order', () {
    expect(SemanticContext.or(a, b), SemanticContext.or(b, a));
    expect(
        SemanticContext.or(a, b).hashCode, SemanticContext.or(b, a).hashCode);
  });

  test('AND keeps lowest precedence, OR keeps highest', () {
    expect(SemanticContext.and(SemanticContext.and(prec5, a), prec2).toString(),
        '{0:0}?&&{2>=prec}?');

    expect(SemanticContext.or(SemanticContext.or(prec2, a), prec5).toString(),
        '{0:0}?||{5>=prec}?');
  });

  test('OR orders ANDs operand by operand', () {
    expect(
        SemanticContext.or(SemanticContext.and(a, c), SemanticContext.and(a, b))
            .toString(),
        '{0:0}?&&{0:2}?||{0:0}?&&{0:3}?');
  });

  test('AND keeps unequal operands with equal hash codes', () {
    final and =
        SemanticContext.and(ConstantHashContext(), ConstantHashContext());
    expect((and as AND).opnds.length, 2);
  });
}
