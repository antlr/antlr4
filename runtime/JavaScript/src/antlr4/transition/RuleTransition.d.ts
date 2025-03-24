import type { ATNState } from '../state/ATNState';
import type { Transition } from './Transition';

export declare class RuleTransition extends Transition {
	serializationType: number;
	ruleIndex: number;
	precedence: number;
	followState: ATNState;

	constructor(ruleStart: ATNState, ruleIndex: number, precedence: number, followState: ATNState);

	matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean;
}