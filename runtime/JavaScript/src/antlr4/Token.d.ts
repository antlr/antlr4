import CharStream from "./CharStream";
import TokenSource from './TokenSource';

declare class Token {

    tokenIndex: number;
    line: number;
    column: number;
    text: string;
    type: number;
    start : number;
    stop: number;

    getTokenSource(): TokenSource;
    getInputStream(): CharStream;

    static readonly INVALID_TYPE = 0;
    static readonly EPSILON = -2;
    static readonly MIN_USER_TOKEN_TYPE = 1;
    static readonly EOF = -1;
    static readonly DEFAULT_CHANNEL = 0;
    static readonly HIDDEN_CHANNEL = 1;
}

export default Token;
