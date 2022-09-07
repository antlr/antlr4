import ParseTree from "./ParseTree";
import Token from "../Token";

export default class TerminalNode implements ParseTree {
    symbol: Token;
}
