import { Interval } from "./misc";
import Token from "./Token";

export default interface TokenStream {
    LA(i: number): number;
    LT(k: number): Token;
    getText(): string;
    getText(interval: Interval): string;
}
