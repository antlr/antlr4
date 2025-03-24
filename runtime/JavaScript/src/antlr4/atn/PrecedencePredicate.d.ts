import type { ParserRuleContext } from '../context/ParserRuleContext.js';
import type { HashCode } from '../misc/HashCode';
import type { Recognizer } from '../Recognizer.js';
import type { Token } from '../Token.js';
import type { SemanticContext } from './SemanticContext';

export declare class PrecedencePredicate extends SemanticContext {
	constructor(precedence: number);

	compareTo(other: SemanticContext): number;
	updateHashCode(hash: HashCode): void;
	equals(obj: any): boolean;
	toString(): string;
}