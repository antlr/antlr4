export default class Token {

    static EOF: number;

    tokenIndex: number;
    line: number;
    column: number;
    text: string;
    type: number;

    clone(): Token;
    cloneWithType(type: number): Token;
}
