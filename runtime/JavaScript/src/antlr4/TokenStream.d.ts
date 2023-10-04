import {Interval} from "./misc";
import {Token} from "./Token";

export declare class TokenStream {

    index: number;
    size: number;

    LA(i: number): number;
    LT(k: number): Token;
    getText(interval?: Interval): string;
    // channelIndex can be retrieved using: lexer.channelNames().findIndex(channelName)
    getHiddenTokensToLeft(tokenIndex: number, channelIndex?: number): Token[];
    getHiddenTokensToRight(tokenIndex: number, channelIndex?: number): Token[];
    get(idx: number): Token;
}
