/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import InputStream from "./InputStream.js";
import Token from "./Token.js";
import TokenSource from "./TokenSource.js";

export declare class CommonToken extends Token {
    public readonly EMPTY_SOURCE: [TokenSource | null, InputStream | null];

    public readonly source: [TokenSource | null, InputStream | null];

    public constructor(source: [TokenSource | null, InputStream | null], type: number, channel: number, start: number, stop: number);

    public clone(): CommonToken;
    public cloneWithType(type: number): CommonToken;
    public toString(): string;
}

export default CommonToken;
