/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

export declare class Interval {
    start: number
    stop: number

    constructor(start: number, stop: number)

    get length(): number

    contains(item: Interval): boolean
    toString(): string
}

export declare class IntervalSet {
    intervals: Array<Interval> | null
    readOnly: boolean

    constructor()

    get length(): number

    first(): number
    addOne(v: number): void
    addRange(l: number, h: number): void
    addInterval(v: number): void
    addSet(other: IntervalSet): IntervalSet
    reduce(k: number): void
    complement(start: number, stop: number): IntervalSet
    contains(item: Interval): boolean
    removeRange(v: number): void
    removeOne(v: number): void
    toString(literalNames?: Array<string | null> | null, symbolicNames?: Array<string | null> | null, elemsAreChar?: boolean | null): string
    toCharString(): string
    toIndexString(): string
    toTokenString(): string
    elementName(literalNames: Array<string | null>, symbolicNames: Array<string | null>, a: number): string | null
}
