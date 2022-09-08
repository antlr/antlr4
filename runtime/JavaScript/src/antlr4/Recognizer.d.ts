import ErrorListener from "./error/ErrorListener";

export default class Recognizer<TSymbol> {
    state: number;

    addErrorListener(listener: ErrorListener<TSymbol>): void;
}
