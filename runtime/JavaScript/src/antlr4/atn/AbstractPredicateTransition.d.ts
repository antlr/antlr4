import { ATNState } from '../state/ATNState';
import { Transition } from '../transition/Transition';

export declare class AbstractPredicateTransition extends Transition {
	constructor(target: ATNState);
}