import Recognizer from "./Recognizer";
import LexerATNSimulator from "./atn/LexerATNSimulator";
import CharStream from "./CharStream";
import Token from "./Token";

declare class Lexer extends Recognizer<number> {

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
    nextToken() : Token;
    emit() : Token;
}

export default Lexer;
