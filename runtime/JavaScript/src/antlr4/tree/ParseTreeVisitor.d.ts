import ParseTree from "./ParseTree";
import TerminalNode from "./TerminalNode";
import RuleNode from "./RuleNode";
import ErrorNode from "./ErrorNode";

export default class ParseTreeVisitor<Result> {

    visit(tree: ParseTree): Result;

    visitChildren(node: RuleNode): Result;

    visitTerminal(node: TerminalNode): Result;

    visitErrorNode(node: ErrorNode): Result;

}
