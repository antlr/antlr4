import { TokenStream } from './TokenStream';
import { Lexer } from "./Lexer";

export declare class BufferedTokenStream extends TokenStream {

    tokenSource: Lexer;

}
