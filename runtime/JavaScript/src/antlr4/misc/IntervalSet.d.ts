export default class IntervalSet {

    isNil: boolean;
    size: number;
    minElement: number;
    maxElement: number;

    contains(i: number): boolean;
    toString(literalNames?: (string | null)[], symbolicNames?: string[], elemsAreChar?: boolean): string;
}
