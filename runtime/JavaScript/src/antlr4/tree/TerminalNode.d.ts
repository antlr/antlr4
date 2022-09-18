import ParseTree from "./ParseTree";
import Token from "../Token";

export default class TerminalNode extends ParseTree {
    symbol: Token;
}
