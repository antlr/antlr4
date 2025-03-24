import type { ATNState } from '../state/ATNState';
import { Transition } from './Transition';

export declare class EpsilonTransition extends Transition {
	serializationType: number;
	outermostPrecedenceReturn: number;
	constructor(target: ATNState, outermostPrecendenceReturn: number);
	
	matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean;
	toString(): string;
}