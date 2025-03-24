import type { IntervalSet } from '../misc/IntervalSet';
import type { ATNState } from '../state/ATNState';

export declare class Transition {
	target: ATNState;
	isEpsilon: boolean;
	label: IntervalSet;
	
	static EPSILON: number;
	static RANGE: number;
	static RULE: number;
	static PREDICATE: number;
	static ATOM: number;
	static ACTION: number;
	static SET: number;
	static NOT_SET: number;
	static WILDCARD: number;
	static PRECEDENCE: number;
	static serializationNames: string[];
	static serializationTypes: Record<string, number>;
}
