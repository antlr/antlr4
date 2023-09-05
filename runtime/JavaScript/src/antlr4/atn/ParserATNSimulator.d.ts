/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import Parser from "../Parser.js";
import TokenStream from "../TokenStream.js";
import ParserRuleContext from "../context/ParserRuleContext.js";
import DFA from "../dfa/DFA.js";
import ATN from "./ATN.js";
import ATNSimulator from "./ATNSimulator.js";
import PredictionContextCache from "./PredictionContextCache.js";
import PredictionMode from "./PredictionMode.js";

export declare class ParserATNSimulator extends ATNSimulator {
    public predictionMode: PredictionMode;
    public decisionToDFA: DFA[];
    public atn: ATN;
    public debug?: boolean;
    public trace_atn_sim?: boolean;

    public constructor(recog: Parser, atn: ATN, decisionToDFA: DFA[], sharedContextCache: PredictionContextCache);
    public adaptivePredict(input: TokenStream, decision: number, outerContext: ParserRuleContext): number;
}

export default ParserATNSimulator;
