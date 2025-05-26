import type { ParserRuleContext } from '../context/ParserRuleContext';
import type { Recognizer } from '../Recognizer';
import type { Token } from '../Token';

export declare class SemanticContext {
	hashCode(): number;
	evaluate(parser: Recognizer<Token>, outerContext: ParserRuleContext): boolean;
	evalPrecedence(parser: Recognizer<Token>, outerContext: ParserRuleContext): SemanticContext;

	static andContext(a: SemanticContext, b: SemanticContext): SemanticContext;
	static orContext(a: SemanticContext, b: SemanticContext): SemanticContext;
}
