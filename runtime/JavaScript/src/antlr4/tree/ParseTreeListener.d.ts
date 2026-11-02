import { ParserRuleContext } from "../context/index.js";
import { ErrorNode } from "./ErrorNode.js";
import { TerminalNode } from "./TerminalNode.js";

export declare interface ParseTreeListener {
    visitTerminal(node: TerminalNode): void;
    visitErrorNode(node: ErrorNode): void;
    enterEveryRule(ctx: ParserRuleContext): void;
    exitEveryRule(ctx: ParserRuleContext): void;
}
