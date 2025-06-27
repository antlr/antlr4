import {ParserRuleContext} from "../context/index.js";
import {ParseTree} from "./ParseTree.js";
import {Token} from "../Token.js";

export declare class TerminalNode extends ParseTree {
    symbol: Token;
    parentCtx: ParserRuleContext;
}
