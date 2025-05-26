import type { HashCode } from '../misc/HashCode';
import type { SemanticContext } from './SemanticContext';

export declare class PrecedencePredicate extends SemanticContext {
	constructor(precedence: number);

	compareTo(other: SemanticContext): number;
	updateHashCode(hash: HashCode): void;
	equals(obj: any): boolean;
	toString(): string;
}
