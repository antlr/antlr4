import { Token } from "./Token.js";
import {InputStream} from "./InputStream.js";
import {TokenSource} from "./TokenSource.js";

export declare class CommonToken extends Token {
    constructor(source: [ TokenSource, InputStream ], type: number, channel: number, start: number, stop: number);
    clone(): CommonToken;
    cloneWithType(type: number): CommonToken;
    toString(): string;
}
