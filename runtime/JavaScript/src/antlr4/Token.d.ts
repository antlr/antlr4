import {CharStream} from "./CharStream";
import {TokenSource} from "./TokenSource";
import {InputStream} from "./InputStream";

export declare class Token {

    static EOF: number;
    
    static DEFAULT_CHANNEL: number;
    static HIDDEN_CHANNEL: number;

    source: [ TokenSource, InputStream ];
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
    getInputStream(): CharStream;
}
