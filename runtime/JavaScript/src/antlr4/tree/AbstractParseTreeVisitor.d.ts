import { ErrorNode } from "./ErrorNode";
import { ParseTree } from "./ParseTree";
import { IParseTreeVisitor } from "./IParseTreeVisitor";
import { RuleNode } from "./RuleNode";
import { TerminalNode } from "./TerminalNode";

/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
export declare abstract class AbstractParseTreeVisitor<VisitType> implements IParseTreeVisitor<VisitType> {
    visit(tree: ParseTree) : VisitType;

    visitChildren(node: RuleNode) : VisitType;

    visitTerminal(node: TerminalNode) : VisitType;

    visitErrorNode(node: ErrorNode) : VisitType;

		protected defaultResult(): VisitType;
		protected aggregateResult(aggregate: VisitType, nextResult: VisitType): VisitType;
		protected shouldVisitNextChild(node: RuleNode, currentResult: VisitType) : boolean;
}
