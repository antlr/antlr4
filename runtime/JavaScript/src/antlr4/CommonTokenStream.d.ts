import { Lexer } from "./Lexer";
import { BufferedTokenStream } from "./BufferedTokenStream";
import { CommonToken } from "./CommonToken";

export declare class CommonTokenStream extends BufferedTokenStream {
    // properties
    tokens: CommonToken[];
    // methods
    constructor(lexer: Lexer);
    constructor(lexer: Lexer, channel: number);
    fill(): void;
}
