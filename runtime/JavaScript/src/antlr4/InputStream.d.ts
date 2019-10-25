/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Token } from "./Token"

export declare class InputStream {
    name: string
    strdata: string
    decodeToUnicodeCodePoints: boolean

    protected _index: number
    protected _size: number

    constructor(data: string, decodeToUnicodeCodePoints?: boolean | null)

    get index(): number

    get size(): number

    reset(): void
    consume(): void
    LA(offset: number): number
    LT(offset: number): number
    mark(): number
    release(marker: number): void
    seek(_index: number): void
    getText(start: number, stop: number): string
    toString(): string
}
