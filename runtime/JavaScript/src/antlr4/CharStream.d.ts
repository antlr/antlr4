/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

export declare class CharStream {
    index: number; // defined as property
    size: number;// defined as property

    constructor(data: string);
    constructor(data: string, decodeToUnicodeCodePoints: boolean);
    reset(): void;
    consume(): void;
    LA(offset: number): number;
    LT(offset: number): number;
    mark(): number;
    release(marker: number): void;
    seek(index: number): void;
    getText(start: number, stop: number): string;
    toString(): string;
}

export default CharStream;
