import ParseTree from "./ParseTree";
import Token from "../Token";
import {ParserRuleContext} from "../context";

export default class TerminalNode extends ParseTree {
    symbol: Token;
    parentCtx: ParserRuleContext;
}
