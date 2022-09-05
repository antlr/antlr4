import TokenStream from './TokenStream';
import Token from "./Token";
import Interval from './misc/Interval';

export default class BufferedTokenStream implements TokenStream {
    LA(i: number): number;
    LT(k: number): Token;
    getText(interval: Interval): string;
}
