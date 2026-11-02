import {IParseTreeListener} from "./IParseTreeListener.js";
import {ParseTree} from "./ParseTree.js";

export declare class ParseTreeWalker {
    static DEFAULT: ParseTreeWalker;

    walk<T extends IParseTreeListener>(listener: T, t: ParseTree): void;
}
