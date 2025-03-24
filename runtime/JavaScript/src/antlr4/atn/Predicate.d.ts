import type { ParserRuleContext } from '../context/ParserRuleContext';
import type { HashCode } from '../misc/HashCode';
import type { Recognizer } from '../Recognizer';
import type { Token } from '../Token';
import type { SemanticContext } from './SemanticContext';

export declare class Predicate extends SemanticContext {
	ruleIndex: number;
	predIndex: number;
	isCtxDependent: boolean;
	
	constructor(ruleIndex: number, predIndex: number, isCtxDependent: boolean);
	
	evaluate(parser: Recognizer<Token>, outerContext: ParserRuleContext): boolean;
	updateHashCode(hash: HashCode): void;
	equals(obj: any): boolean;
	toString(): string;
}
