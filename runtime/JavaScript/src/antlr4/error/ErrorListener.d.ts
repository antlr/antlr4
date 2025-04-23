import { ATNConfigSet } from "../atn/index.js";
import { BitSet } from "../misc/BitSet.js";
import { DFA } from "../dfa/index.js";
import { Recognizer } from "../Recognizer.js";
import { RecognitionException } from "./RecognitionException.js";

export declare class ErrorListener<TSymbol> {
    reportAmbiguity(recognizer: Recognizer<TSymbol>, dfa: DFA, startIndex: number, stopIndex: number, exact: boolean, ambigAlts: BitSet, configs: ATNConfigSet): void;
    
    reportAttemptingFullContext(recognizer: Recognizer<TSymbol>, dfa: DFA, startIndex: number, stopIndex: number, conflictingAlts: BitSet, configs: ATNConfigSet): void;
    
    reportContextSensitivity(recognizer: Recognizer<TSymbol>, dfa: DFA, startIndex: number, stopIndex: number, prediction: number, configs: ATNConfigSet): void;
    
    syntaxError(recognizer: Recognizer<TSymbol>, offendingSymbol: TSymbol, line: number, column: number, msg: string, e: RecognitionException | undefined): void;
}
