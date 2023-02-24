import {ATNSimulator} from "./ATNSimulator";
import {ParserRuleContext} from "../context";
import {TokenStream} from "../TokenStream";
import {Recognizer} from "../Recognizer";
import {ATN} from "./ATN";
import {PredictionContextCache} from "./PredictionContextCache";
import {DFA} from "../dfa";
import {PredictionMode} from "./PredictionMode";
import {Token} from "../Token";

export declare class ParserATNSimulator extends ATNSimulator {

    predictionMode: PredictionMode;
    decisionToDFA: DFA[];
    atn: ATN;
    debug?: boolean;
    trace_atn_sim?: boolean;

    constructor(recog: Recognizer<Token>, atn: ATN, decisionToDFA: DFA[], sharedContextCache: PredictionContextCache);
    adaptivePredict(input: TokenStream, decision: number, outerContext: ParserRuleContext) : number;
}
