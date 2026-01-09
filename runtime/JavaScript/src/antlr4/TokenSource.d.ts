import {Token} from "./Token";

export declare class TokenSource {
    _factory: any;
    nextToken(): Token;
    getSourceName(): string;
}
