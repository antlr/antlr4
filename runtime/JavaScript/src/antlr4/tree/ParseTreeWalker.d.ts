import ParseTree from "./ParseTree";
import ParseTreeListener from "./ParseTreeListener";

export default class ParseTreeWalker {
    static DEFAULT: ParseTreeWalker;

    walk<T extends ParseTreeListener>(listener: T, t: ParseTree): void;
}
