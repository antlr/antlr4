import Recognizer from "../Recognizer";
import ATNSimulator from "./ATNSimulator";
import ATN from "./ATN";
import PredictionContextCache from "./PredictionContextCache";
import DFA from "../dfa/DFA";
import CharStream from "../CharStream";

declare class LexerATNSimulator implements ATNSimulator {

    decisionToDFA: DFA[];

    constructor(recog: Recognizer<number>, atn: ATN, decisionToDFA: DFA[], sharedContextCache: PredictionContextCache);
    consume(input: CharStream) : void;

}

export default LexerATNSimulator;
