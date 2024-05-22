import {Interval} from "./Interval";
import { Token } from "../Token";

export default class IntervalSet {
    private intervals: Interval[] | null;
    private readOnly: boolean;

    constructor();

    first(v: number): number;

    addOne(v: number): void;

    addRange(l: number, h: number): void;

    addInterval(toAdd: Interval): void;

    addSet(other: IntervalSet): IntervalSet;

    private reduce(pos: number): void;

    complement(start: number, stop: number): IntervalSet;

    contains(item: number): boolean;

    removeRange(toRemove: Interval): void;

    removeOne(value: number): void;

    toString(literalNames?: string[], symbolicNames?: string[], elemsAreChar?: boolean): string;

    private toCharString(): string;

    private toIndexString(): string;

    private toTokenString(literalNames: string[], symbolicNames: string[]): string;

    private elementName(literalNames: string[], symbolicNames: string[], token: number): string;

    get length(): number;
}
