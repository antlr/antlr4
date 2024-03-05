import {ErrorListener} from "./error";

export declare class Recognizer<TSymbol> {

    state: number;

    removeErrorListeners(): void;
    addErrorListener(listener: ErrorListener<TSymbol>): void;
    getErrorListenerDispatch(): ErrorListener<TSymbol>;
    getLiteralNames(): string[] | [];
    getSymbolicNames(): string[] | [];
}
