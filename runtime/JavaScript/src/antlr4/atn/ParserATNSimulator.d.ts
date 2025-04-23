import {ATNSimulator} from "./ATNSimulator.js";
import {ParserRuleContext} from "../context/index.js";
import {TokenStream} from "../TokenStream.js";
import {Recognizer} from "../Recognizer.js";
import {ATN} from "./ATN.js";
import {PredictionContextCache} from "./PredictionContextCache.js";
import {DFA} from "../dfa/index.js";
import {PredictionMode} from "./PredictionMode.js";
import {Token} from "../Token.js";

export declare class ParserATNSimulator extends ATNSimulator {

    predictionMode: PredictionMode;
    decisionToDFA: DFA[];
    atn: ATN;
    debug?: boolean;
    trace_atn_sim?: boolean;

    constructor(recog: Recognizer<Token>, atn: ATN, decisionToDFA: DFA[], sharedContextCache: PredictionContextCache);
    adaptivePredict(input: TokenStream, decision: number, outerContext: ParserRuleContext) : number;
}
