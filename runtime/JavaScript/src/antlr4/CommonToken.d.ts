import { Token } from "./Token";

export declare class CommonToken extends Token {
    constructor(source: number, type: number, channel: number, start: number, stop: number);
    clone(): CommonToken;
    cloneWithType(type: number): CommonToken;
    toString(): string;
}
