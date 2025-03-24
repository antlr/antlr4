import type { IntervalSet } from '../misc/IntervalSet';
import type { ATNState } from '../state/ATNState';
import { SetTransition } from './SetTransition';

export declare class NotSetTransition extends SetTransition {
	serializationType: number;
	constructor(target: ATNState, set: IntervalSet);

	matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean;
	toString(): string;
}
