import {RuleNode} from "./RuleNode.js";
import {ErrorNode} from "./ErrorNode.js";
import {TerminalNode} from "./TerminalNode.js";
import {ParseTree} from "./ParseTree.js";

export declare class ParseTreeVisitor<Result> {

    visit(tree: ParseTree): Result;

    visitChildren(node: RuleNode): Result;

    visitTerminal(node: TerminalNode): Result;

    visitErrorNode(node: ErrorNode): Result;

}
