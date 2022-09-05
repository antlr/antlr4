import ATNSimulator from "./ATNSimulator";
import Recognizer from "../Recognizer";
import ATN from "./ATN";
import DFA from "../dfa/DFA";
import PredictionContextCache from "./PredictionContextCache";
import ParserRuleContext from "../context/ParserRuleContext";
import TokenStream from "../TokenStream";

export default class ParserATNSimulator extends ATNSimulator {

    constructor(recog: Recognizer, atn: ATN, decisionToDFA: DFA[], sharedContextCache: PredictionContextCache);
    adaptivePredict(input: TokenStream, decision: number, outerContext: ParserRuleContext) : number;
}
