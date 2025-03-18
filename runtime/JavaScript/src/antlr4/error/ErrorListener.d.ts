import { ATNConfigSet } from "../atn";
import { BitSet } from '../misc/BitSet';
import { DFA } from "../dfa";
import { Recognizer } from "../Recognizer";
import { RecognitionException } from "./RecognitionException";

export declare class ErrorListener<TSymbol> {
    reportAmbiguity(recognizer: Recognizer<TSymbol>, dfa: DFA, startIndex: number, stopIndex: number, exact: boolean, ambigAlts: BitSet, configs: ATNConfigSet): void;
    
    reportAttemptingFullContext(recognizer: Recognizer<TSymbol>, dfa: DFA, startIndex: number, stopIndex: number, conflictingAlts: BitSet, configs: ATNConfigSet): void;
    
    reportContextSensitivity(recognizer: Recognizer<TSymbol>, dfa: DFA, startIndex: number, stopIndex: number, prediction: number, configs: ATNConfigSet): void;
    
    syntaxError(recognizer: Recognizer<TSymbol>, offendingSymbol: TSymbol, line: number, column: number, msg: string, e: RecognitionException | undefined): void;
}
