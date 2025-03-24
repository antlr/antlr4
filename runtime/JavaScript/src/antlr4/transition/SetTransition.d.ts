import type { IntervalSet } from '../misc/IntervalSet';
import type { ATNState } from '../state/ATNState';
import type { Transition } from './Transition';
export declare class SetTransition extends Transition {
	serializationType: number

	constructor(target: ATNState, set: IntervalSet);

	matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean;
	toString(): string;
}
