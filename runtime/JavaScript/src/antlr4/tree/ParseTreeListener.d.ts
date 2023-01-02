import ParserRuleContext from "../context/ParserRuleContext";
import ErrorNode from "./ErrorNode";
import TerminalNode from "./TerminalNode";

declare abstract class ParseTreeListener {
    visitTerminal(node: TerminalNode) : void;
    visitErrorNode(node: ErrorNode) : void;
    enterEveryRule(ctx: ParserRuleContext) : void;
    exitEveryRule(ctx: ParserRuleContext) : void;
}

export default ParseTreeListener;
