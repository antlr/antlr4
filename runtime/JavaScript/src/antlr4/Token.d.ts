import { TokenSource } from "./TokenSource.js";
import { CharStream } from "./CharStream.js";

export declare class Token {

    static INVALID_TYPE: number;
    static EOF: number;
    static DEFAULT_CHANNEL: number;
    static HIDDEN_CHANNEL: number;

    tokenIndex: number;
    line: number;
    column: number;
    channel: number;
    text: string;
    type: number;
    start : number;
    stop: number;

    clone(): Token;
    cloneWithType(type: number): Token;
    getTokenSource(): TokenSource;
    getInputStream(): CharStream;
}
