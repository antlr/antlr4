import ATNSimulator from "./ATNSimulator";
import Recognizer from "../Recognizer";
import ATN from "./ATN";
import DFA from "../dfa/DFA";
import PredictionContextCache from "./PredictionContextCache";
import ParserRuleContext from "../context/ParserRuleContext";
import TokenStream from "../TokenStream";
import Token from "../Token";
import PredictionMode from "./PredictionMode";

export default class ParserATNSimulator extends ATNSimulator {

    predictionMode: PredictionMode;
    decisionToDFA: DFA[];

    constructor(recog: Recognizer<Token>, atn: ATN, decisionToDFA: DFA[], sharedContextCache: PredictionContextCache);
    adaptivePredict(input: TokenStream, decision: number, outerContext: ParserRuleContext) : number;
}
