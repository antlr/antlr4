import type { ATNState } from '../state/ATNState';
import { Transition } from './Transition';

export declare class ActionTransition extends Transition {
	serializationType: number;
	ruleIndex: number;
	actionIndex: number;
	isCtxDependent: boolean;
	constructor(target: ATNState, ruleIndex: number, actionIndex: number, isCtxDependent: boolean)

	matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean;
	toString(): string;
}
