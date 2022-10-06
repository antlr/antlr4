import { Interval } from "./misc";
import Token from "./Token";
import TokenSource from "./TokenSource";

export default class TokenStream {

    index: number;
    size: number;

    LA(i: number): number;
    LT(k: number): Token;
    getText(interval?: Interval): string;
    getHiddenTokensToLeft(tokenIndex: number, channelName?: string): Token[];
    getHiddenTokensToRight(tokenIndex: number, channelName?: string): Token[];
    get(idx: number): Token;
}
