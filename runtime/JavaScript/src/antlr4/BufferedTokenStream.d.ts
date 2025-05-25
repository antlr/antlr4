import { TokenStream } from "./TokenStream.js";
import { Lexer } from "./Lexer.js";

export declare class BufferedTokenStream extends TokenStream {

    tokenSource: Lexer;

}
