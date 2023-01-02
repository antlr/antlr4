import ParserRuleContext from "../context/ParserRuleContext";
import ParseTree from "./ParseTree";
import Token from "../Token";

declare class TerminalNode extends ParseTree {
    symbol: Token;
    parentCtx: ParserRuleContext;
}

export default TerminalNode;
