import Recognizer from './Recognizer';
import CharStream from "./CharStream";
import LexerATNSimulator from "./atn/LexerATNSimulator";

export default class Lexer implements Recognizer {

    _interp: LexerATNSimulator;

    constructor(input: CharStream);
}
