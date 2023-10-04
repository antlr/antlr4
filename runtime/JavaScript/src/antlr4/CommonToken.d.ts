import { Token } from "./Token";
import {InputStream} from "./InputStream";
import {TokenSource} from "./TokenSource";

export declare class CommonToken extends Token {
    constructor(source: [ TokenSource, InputStream ], type: number, channel: number, start: number, stop: number);
    clone(): CommonToken;
    cloneWithType(type: number): CommonToken;
    toString(): string;
}
