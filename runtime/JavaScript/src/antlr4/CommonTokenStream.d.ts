import {Lexer} from "./Lexer";
import {BufferedTokenStream} from "./BufferedTokenStream";

export declare class CommonTokenStream extends BufferedTokenStream {
    // properties
    tokens: string[];
    // methods
    constructor(lexer: Lexer);
    constructor(lexer: Lexer, channel: number);
    fill(): void;
}
