import {ParseTreeListener} from "./ParseTreeListener";
import {ParseTree} from "./ParseTree";

export declare class ParseTreeWalker {
    static DEFAULT: ParseTreeWalker;

    walk<T extends ParseTreeListener>(listener: T, t: ParseTree): void;
}
