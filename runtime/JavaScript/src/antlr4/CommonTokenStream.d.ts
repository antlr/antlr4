import {Lexer} from "./Lexer.js";
import {BufferedTokenStream} from "./BufferedTokenStream.js";
import {Token} from "./Token.js";

export declare class CommonTokenStream extends BufferedTokenStream {
    // properties
    tokens: Token[];
    // methods
    constructor(lexer: Lexer);
    constructor(lexer: Lexer, channel: number);
    fill(): void;
}
