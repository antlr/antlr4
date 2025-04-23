import {ParseTreeListener} from "./ParseTreeListener.js";
import {ParseTree} from "./ParseTree.js";

export declare class ParseTreeWalker {
    static DEFAULT: ParseTreeWalker;

    walk<T extends ParseTreeListener>(listener: T, t: ParseTree): void;
}
