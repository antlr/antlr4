import type { ATNState } from '../state/ATNState';
import type { Transition } from './Transition';

export declare class AtomTransition extends Transition {
	serializationType: number;
	constructor(target: ATNState, label: number)
	
	matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean;
	toString(): string;
}