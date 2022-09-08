import Recognizer from './Recognizer';
import CharStream from "./CharStream";
import LexerATNSimulator from "./atn/LexerATNSimulator";
import Token from './Token'

export default class Lexer extends Recognizer<number> {

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
