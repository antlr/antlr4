/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Token } from "./Token"

export class Interval {
    public start: number
    public stop: number

    constructor(start: number, stop: number)

    get length(): number

    contains(item: Interval): boolean
    toString(): string
}

export class IntervalSet {
    public intervals: Array<Interval> | null
    public readOnly: boolean

    constructor()

    get length(): number

    first(): typeof Token.INVALID_TYPE | number
    addOne(v: number): void
    addRange(l: number, h: number): void
    addInterval(v: number): void
    addSet(other: IntervalSet): IntervalSet
    reduce(k: number): void
    complement(start: number, stop: number): IntervalSet
    contains(item: Interval): boolean
    removeRange(v: number): void
    removeOne(v: number): void
    toString(
        literalNames?: Array<string | null>,
        symbolicNames?: Array<string | null>,
        elemsAreChar?: boolean
    ): string
    toCharString(): string
    toIndexString(): string
    toTokenString(): string
    elementName(
        literalNames: Array<string | null>,
        symbolicNames: Array<string | null>,
        a: number
    ): string | null
}
