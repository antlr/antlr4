import Token from "./Token";

export default interface TokenStream {
    LA(i: number): number;
    LT(k: number): Token;
    tryLT(k: number): Token | undefined;
    getTextFromRange(start: any, stop: any): string;
}
