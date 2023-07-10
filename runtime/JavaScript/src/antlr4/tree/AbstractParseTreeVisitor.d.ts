import { ErrorNode } from "./ErrorNode";
import { ParseTree } from "./ParseTree";
import { ParseTreeVisitor } from "./ParseTreeVisitor";
import { RuleNode } from "./RuleNode";
import { TerminalNode } from "./TerminalNode";

/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
export declare abstract class AbstractParseTreeVisitor<VisitType, DefaultType = null> implements ParseTreeVisitor<VisitType | DefaultType> {
    visit(tree: ParseTree) : VisitType | DefaultType;

    visitChildren(node: RuleNode) : VisitType | DefaultType;

    visitTerminal(node: TerminalNode) : VisitType | DefaultType;

    visitErrorNode(node: ErrorNode) : VisitType | DefaultType;

		protected defaultResult(): DefaultType;
		protected aggregateResult(aggregate: VisitType | DefaultType, nextResult: VisitType | DefaultType): VisitType | DefaultType;
		protected shouldVisitNextChild(node: RuleNode, currentResult: VisitType | DefaultType) : boolean;
}
