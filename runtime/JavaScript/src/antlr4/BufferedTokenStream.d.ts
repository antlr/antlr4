import TokenStream from './TokenStream';
import Lexer from "./Lexer";

export default class BufferedTokenStream extends TokenStream {

    tokenSource: Lexer;

}
