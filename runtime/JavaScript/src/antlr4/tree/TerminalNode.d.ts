import {ParserRuleContext} from "../context";
import {ParseTree} from "./ParseTree";
import {Token} from "../Token";

export declare class TerminalNode extends ParseTree {
    symbol: Token;
    parentCtx: ParserRuleContext;
}
