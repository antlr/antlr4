import TokenStream from './TokenStream';
import Token from "./Token";

export default class BufferedTokenStream implements TokenStream {
    LA(i: number): number;
    LT(k: number): Token;
    getText(start: any, stop: any): string;
}
