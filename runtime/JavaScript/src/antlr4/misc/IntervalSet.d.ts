import Interval from "./Interval";

declare class IntervalSet {

    isNil: boolean;
    size: number;
    minElement: number;
    maxElement: number;
    intervals: Interval[];

    contains(i: number): boolean;
    toString(literalNames?: (string | null)[], symbolicNames?: string[], elemsAreChar?: boolean): string;
}

export default IntervalSet;
