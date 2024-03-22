import {Lexer} from "./Lexer";
import {BufferedTokenStream} from "./BufferedTokenStream";
import {Token} from "./Token";

export declare class CommonTokenStream extends BufferedTokenStream {
    // properties
    tokens: Token[];
    // methods
    constructor(lexer: Lexer);
    constructor(lexer: Lexer, channel: number);
    fill(): void;
}
