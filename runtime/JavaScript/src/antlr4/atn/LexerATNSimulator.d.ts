import ATNSimulator from "./ATNSimulator";
import Recognizer from "../Recognizer";
import ATN from "./ATN";
import DFA from "../dfa/DFA";
import PredictionContextCache from "./PredictionContextCache";
import CharStream from "../CharStream";

export default class LexerATNSimulator implements ATNSimulator {

    decisionToDFA: DFA[];

    constructor(recog: Recognizer<number>, atn: ATN, decisionToDFA: DFA[], sharedContextCache: PredictionContextCache);
    consume(input: CharStream) : void;

}
