import ATNSimulator from "./ATNSimulator";
import Recognizer from "../Recognizer";
import ATN from "./ATN";
import DFA from "../dfa/DFA";
import PredictionContextCache from "./PredictionContextCache";

export default class ParserATNSimulator extends ATNSimulator {

    constructor(recog: Recognizer, atn: ATN, decisionToDFA: DFA[], sharedContextCache: PredictionContextCache);

}
