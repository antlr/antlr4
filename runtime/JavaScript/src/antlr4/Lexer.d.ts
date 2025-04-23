import {Recognizer} from "./Recognizer.js";
import {LexerATNSimulator} from "./atn/index.js";
import {CharStream} from "./CharStream.js";
import {Token} from "./Token.js";

export declare class Lexer extends Recognizer<number> {

    static DEFAULT_MODE: number;

    _input: CharStream;
    _interp: LexerATNSimulator;
    text: string;
    line: number;
    column: number;
    _tokenStartCharIndex: number;
    _tokenStartLine: number;
    _tokenStartColumn: number;
    _type: number;

    constructor(input: CharStream);
    reset(): void;
    nextToken(): Token;
    skip(): void;
    more(): void;
    setMode(m: number): void;
    getMode(): number;
    getModeStack(): number[];
    pushMode(m: number): void;
    popMode(): number;
    emitToken(token: Token): void;
    emit(): Token;
    emitEOF(): Token;
    getAllTokens(): Token[];
}
