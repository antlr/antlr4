import {Recognizer} from '../Recognizer.js';
import {RecognitionException} from './RecognitionException.js';

export declare class ErrorListener<TSymbol> {
    syntaxError(recognizer: Recognizer<TSymbol>, offendingSymbol: TSymbol, line: number, column: number, msg: string, e: RecognitionException | undefined): void;
}
