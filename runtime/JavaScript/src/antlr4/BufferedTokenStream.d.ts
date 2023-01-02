import TokenStream from './TokenStream';
import Lexer from "./Lexer";

declare class BufferedTokenStream extends TokenStream {

    tokenSource: Lexer;

}

export default BufferedTokenStream;
