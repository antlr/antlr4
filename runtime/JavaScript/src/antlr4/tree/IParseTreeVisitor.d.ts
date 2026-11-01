import {RuleNode} from "./RuleNode.js";
import {ErrorNode} from "./ErrorNode.js";
import {TerminalNode} from "./TerminalNode.js";
import {ParseTree} from "./ParseTree.js";

/**
 * This interface defines the basic notion of a parse tree visitor. Generated
 * visitors implement this interface and the {@code XVisitor} interface for
 * grammar {@code X}.
 *
 * @param Result The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
export declare interface IParseTreeVisitor<Result> {
	/**
	 * Visit a parse tree, and return a user-defined result of the operation.
	 *
	 * @param tree The {@link ParseTree} to visit.
	 * @return The result of visiting the parse tree.
	 */
	visit(tree: ParseTree): Result;

	/**
	 * Visit the children of a node, and return a user-defined result of the
	 * operation.
	 *
	 * @param node The {@link RuleNode} whose children should be visited.
	 * @return The result of visiting the children of the node.
	 */
	visitChildren(node: RuleNode): Result;

	/**
	 * Visit a terminal node, and return a user-defined result of the operation.
	 *
	 * @param node The {@link TerminalNode} to visit.
	 * @return The result of visiting the node.
	 */
	visitTerminal(node: TerminalNode): Result;

	/**
	 * Visit an error node, and return a user-defined result of the operation.
	 *
	 * @param node The {@link ErrorNode} to visit.
	 * @return The result of visiting the node.
	 */
	visitErrorNode(node: ErrorNode): Result;
}
