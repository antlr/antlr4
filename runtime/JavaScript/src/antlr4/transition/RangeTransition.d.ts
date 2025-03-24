import type { ATNState } from '../state/ATNState';
import type { Transition } from './Transition';

export declare class RangeTransition extends Transition {
	serializationType: number;
	start: number;
	stop: number;
	constructor(target: ATNState, start: number, stop: number);
	
	matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean;
	toString(): string;
}