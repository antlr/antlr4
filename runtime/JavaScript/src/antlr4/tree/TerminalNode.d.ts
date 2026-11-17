import {ParserRuleContext} from "../context/index.js";
import {ParseTree} from "./ParseTree.js";
import {Token} from "../Token.js";
import { ParseTreeVisitor } from "./ParseTreeVisitor.js";

export declare class TerminalNode extends ParseTree {
    symbol: Token;
    parentCtx: ParserRuleContext;
    accept<T>(visitor: ParseTreeVisitor<T>): T;
}
