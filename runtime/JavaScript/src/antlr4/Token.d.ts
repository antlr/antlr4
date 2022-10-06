import CharStream from "./CharStream";

export default class Token {

    static EOF: number;

    tokenIndex: number;
    line: number;
    column: number;
    text: string;
    type: number;
    start : number;
    stop: number;

    clone(): Token;
    cloneWithType(type: number): Token;
    getInputStream(): CharStream;
}
