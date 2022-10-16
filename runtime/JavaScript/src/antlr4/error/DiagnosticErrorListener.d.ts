import {ErrorListener} from "./ErrorListener";
import {Recognizer} from "../Recognizer";
import {RecognitionException} from "./RecognitionException";

export declare class DiagnosticErrorListener<TSymbol> implements ErrorListener<TSymbol> {

    syntaxError(recognizer: Recognizer<TSymbol>, offendingSymbol: TSymbol, line: number, column: number, msg: string, e: RecognitionException | undefined): void;

}
