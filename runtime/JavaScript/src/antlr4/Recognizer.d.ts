import {ErrorListener} from "./error/index.js";

export declare class Recognizer<TSymbol> {

    state: number;

    removeErrorListeners(): void;
    addErrorListener(listener: ErrorListener<TSymbol>): void;
    getErrorListener(): ErrorListener<TSymbol>;
    getLiteralNames(): string[];
    getSymbolicNames(): string[];
}
