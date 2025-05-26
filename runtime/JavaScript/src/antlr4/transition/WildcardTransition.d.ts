import type { ATNState } from '../state/ATNState';
import type { Transition } from './Transition';

export declare class WildcardTransition extends Transition {
	serializationType: number;
	constructor(target: ATNState);
	
	matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean;
	toString(): string;
}
