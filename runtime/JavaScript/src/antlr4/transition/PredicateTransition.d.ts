import type { AbstractPredicateTransition } from '../atn/AbstractPredicateTransition';
import type { Predicate } from '../atn/Predicate';
import type { ATNState } from '../state/ATNState';

export declare class PredicateTransition extends AbstractPredicateTransition {
	serializationType: number;
	ruleIndex: number;
	predIndex: number
	isCtxDependent: boolean;
	constructor(target: ATNState, ruleIndex: number, predIndex: number, isCtxDependent: boolean);
	
	matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean;
	getPredicate(): Predicate
	toString(): string;
}
