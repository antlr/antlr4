import RuleNode from "./RuleNode";
import ErrorNode from "./ErrorNode";
import TerminalNode from "./TerminalNode";
import ParseTree from "./ParseTree";

declare class ParseTreeVisitor<Result> {

    visit(tree: ParseTree): Result;

    visitChildren(node: RuleNode): Result;

    visitTerminal(node: TerminalNode): Result;

    visitErrorNode(node: ErrorNode): Result;

}

export default ParseTreeVisitor;
