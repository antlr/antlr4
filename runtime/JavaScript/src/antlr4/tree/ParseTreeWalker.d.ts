import ParseTreeListener from "./ParseTreeListener";
import ParseTree from "./ParseTree";

declare class ParseTreeWalker {
    static DEFAULT: ParseTreeWalker;

    walk<T extends ParseTreeListener>(listener: T, t: ParseTree): void;
}

export default ParseTreeWalker;
