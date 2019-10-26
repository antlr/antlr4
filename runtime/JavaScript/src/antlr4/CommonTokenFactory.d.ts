/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { CommonToken, NullableSourcePair } from "./Token"
import { TokenFactory } from "./TokenFactory"

export declare class CommonTokenFactory implements TokenFactory {
    static readonly DEFAULT: CommonTokenFactory

    copyText: boolean

    constructor(copyText?: boolean)

    // If source[1] and text are non-null, then start and stop may be required.
    // If source[1] and text are null, then start and stop are optional.
    create(source: NullableSourcePair, type: number, text: string | null, channel: number, start: number | undefined, stop: number | undefined, line: number, column: number): CommonToken
    createThin(type: number, text: string | null): CommonToken
}
