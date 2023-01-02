import Recognizer from "../Recognizer";
import RecognitionException from "./RecognitionException";

declare class ErrorListener<TSymbol> {
    syntaxError(recognizer: Recognizer<TSymbol>, offendingSymbol: TSymbol, line: number, column: number, msg: string, e: RecognitionException | undefined): void;
}

export default ErrorListener;
