import ErrorListener from "./error/ErrorListener";

declare class Recognizer<TSymbol> {

    state: number;

    removeErrorListeners(): void;
    addErrorListener(listener: ErrorListener<TSymbol>): void;
}

export default Recognizer;
