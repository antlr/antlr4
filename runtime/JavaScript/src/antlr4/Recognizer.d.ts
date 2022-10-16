import {ErrorListener} from "./error";

export declare class Recognizer<TSymbol> {

    state: number;

    removeErrorListeners(): void;
    addErrorListener(listener: ErrorListener<TSymbol>): void;
}
