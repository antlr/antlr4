import {Recognizer} from "../Recognizer.js";
import {ATNSimulator} from "./ATNSimulator.js";
import {ATN} from "./ATN.js";
import {PredictionContextCache} from "./PredictionContextCache.js";
import {DFA} from "../dfa/index.js";
import {CharStream} from "../CharStream.js";

export declare class LexerATNSimulator implements ATNSimulator {

    decisionToDFA: DFA[];

    constructor(recog: Recognizer<number>, atn: ATN, decisionToDFA: DFA[], sharedContextCache: PredictionContextCache);
    consume(input: CharStream) : void;

}
