import { AbstractPredicateTransition } from '../atn/AbstractPredicateTransition';
import type { PrecedencePredicate } from '../atn/PrecedencePredicate';
import type { ATNState } from '../state/ATNState';

export declare class PrecedencePredicateTransition extends AbstractPredicateTransition {
	serializationType: number;
	precedence: number;
	constructor(target: ATNState, precedence: number);

	matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean;
	getPredicate(): PrecedencePredicate;
	toString(): string;
}
