import {ParserRuleContext} from "../context";
import {ErrorNode} from "./ErrorNode";
import {TerminalNode} from "./TerminalNode";

export declare abstract class ParseTreeListener {
    visitTerminal(node: TerminalNode) : void;
    visitErrorNode(node: ErrorNode) : void;
    enterEveryRule(ctx: ParserRuleContext) : void;
    exitEveryRule(ctx: ParserRuleContext) : void;
}
